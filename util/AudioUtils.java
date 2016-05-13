/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME GmbH, Konstanz, Germany
 *  Website: http://www.knime.org; Email: contact@knime.org
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ---------------------------------------------------------------------
 *
 * History
 *   May 6, 2016 (budiyanto): created
 */
package org.knime.base.node.audio3.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.knime.base.node.audio3.data.Audio;
import org.knime.base.node.audio3.data.AudioSamples;
import org.knime.core.node.NodeLogger;

import jAudioFeatureExtractor.jAudioTools.AudioMethods;
import jAudioFeatureExtractor.jAudioTools.DSPMethods;

/**
 *
 * @author Budi Yanto, KNIME.com
 */
public class AudioUtils {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(AudioUtils.class);

    /**
     * Prevent to directly create a new instance.
     * Only use the static methods
     */
    private AudioUtils() {}

    /**
     * Normalizes bytes from bits. The maximal bit depth supported is 16 bits.
     *
     * Some formats allow for bit depths in non-multiples of 8.
     * they will, however, typically pad so the samples are stored
     * that way. AIFF is one of these formats.
     *
     * so the expression:
     *
     *  bitsPerSample + 7 >> 3
     *
     * computes a division of 8 rounding up (for positive numbers).
     *
     * this is basically equivalent to:
     *
     * (int)Math.ceil(bitsPerSample / 8.0)
     *
     * @param bitsPerSample bits to normalize
     * @return the normalized bytes
     */
    public static int normalizeBytesFromBits(final int bitsPerSample){
        return bitsPerSample + 7 >> 3;
    }

    /**
     * Normalize bit depths to multiples of 8.
     * @param bitsPerSample bit depths to normalize
     * @return the normalized bit depths
     */
    public static int normalizeBitDepthFromBits(final int bitsPerSample){
        return normalizeBytesFromBits(bitsPerSample) * 8;
    }

    /**
     * The {@link AudioInputStream} must have Big-Endian signed linear
     * PCM Encoding and bit depth of 8 or 16 bits. If not, it will be converted
     * automatically to the supported {@link AudioFormat}
     * @param inStream the <code>AudioInputStream</code> to convert
     * @return a converted <code>AudioInputStream</code> if it is necessary,
     * otherwise returns the original <code>AudioInputStream</code>
     */
    public static AudioInputStream convertUnsupportedFormat(final AudioInputStream inStream){
        AudioInputStream newStream = inStream;
        final AudioFormat inFormat = inStream.getFormat();
        int bitsPerSample = inFormat.getSampleSizeInBits();
        if(bitsPerSample > 16){
            throw new IllegalArgumentException(
                "The maximal bit depth supported is 16 bits.");
        }
        boolean isSupportedBitsSize = true;
        if(bitsPerSample != 8 && bitsPerSample != 16){
            isSupportedBitsSize = false;
        }

        if(inFormat.getEncoding() != AudioFormat.Encoding.PCM_SIGNED
                || !inFormat.isBigEndian() || !isSupportedBitsSize){
            bitsPerSample = normalizeBitDepthFromBits(bitsPerSample);
            final AudioFormat newFormat = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                inFormat.getSampleRate(),
                bitsPerSample,
                inFormat.getChannels(),
                inFormat.getChannels() * (bitsPerSample / 8),
                inFormat.getSampleRate(),
                true);
            newStream = AudioSystem.getAudioInputStream(newFormat, inStream);
        }
        return newStream;
    }

    /**
    *
    * @param bytes
    * @param transfer
    * @param samples
    * @param bvalid
    * @param fmt
    * @return samples
    */
   public static float[] unpack(final byte[] bytes, final long[] transfer,
           final float[] samples, final int bvalid, final AudioFormat fmt) {
       if(fmt.getEncoding() != AudioFormat.Encoding.PCM_SIGNED
               && fmt.getEncoding() != AudioFormat.Encoding.PCM_UNSIGNED) {
           return samples;
       }

       final int bitsPerSample = fmt.getSampleSizeInBits();
       final int normalBytes = normalizeBytesFromBits(bitsPerSample);

       /*
        * not the most DRY way to do this but it's a bit more efficient.
        * otherwise there would either have to be 4 separate methods for
        * each combination of endianness/signedness or do it all in one
        * loop and check the format for each sample.
        *
        * a helper array (transfer) allows the logic to be split up
        * but without being too repetetive.
        *
        * here there are two loops converting bytes to raw long samples.
        * integral primitives in Java get sign extended when they are
        * promoted to a larger type so the & 0xffL mask keeps them intact.
        *
        */

       if(fmt.isBigEndian()) {
           for(int i = 0, k = 0, b; i < bvalid; i += normalBytes, k++) {
               transfer[k] = 0L;

               int least = i + normalBytes - 1;
               for(b = 0; b < normalBytes; b++) {
                   transfer[k] |= (bytes[least - b] & 0xffL) << (8 * b);
               }
           }
       } else {
           for(int i = 0, k = 0, b; i < bvalid; i += normalBytes, k++) {
               transfer[k] = 0L;

               for(b = 0; b < normalBytes; b++) {
                   transfer[k] |= (bytes[i + b] & 0xffL) << (8 * b);
               }
           }
       }

       final long fullScale = (long)Math.pow(2.0, bitsPerSample - 1);

       /*
        * the OR is not quite enough to convert,
        * the signage needs to be corrected.
        *
        */

       if(fmt.getEncoding() == AudioFormat.Encoding.PCM_SIGNED) {

           /*
            * if the samples were signed, they must be
            * extended to the 64-bit long.
            *
            * the arithmetic right shift in Java  will fill
            * the left bits with 1's if the MSB is set.
            *
            * so sign extend by first shifting left so that
            * if the sample is supposed to be negative,
            * it will shift the sign bit in to the 64-bit MSB
            * then shift back and fill with 1's.
            *
            * as an example, imagining these were 4-bit samples originally
            * and the destination is 8-bit, if we have a hypothetical
            * sample -5 that ought to be negative, the left shift looks
            * like this:
            *
            *     00001011
            *  <<  (8 - 4)
            *  ===========
            *     10110000
            *
            * (except the destination is 64-bit and the original
            * bit depth from the file could be anything.)
            *
            * and the right shift now fills with 1's:
            *
            *     10110000
            *  >>  (8 - 4)
            *  ===========
            *     11111011
            *
            */

           final long signShift = 64L - bitsPerSample;

           for(int i = 0; i < transfer.length; i++) {
               transfer[i] = (
                       (transfer[i] << signShift) >> signShift
                       );
           }
       } else {
           /*
            * unsigned samples are easier since they
            * will be read correctly in to the long.
            *
            * so just sign them:
            * subtract 2^(bits - 1) so the center is 0.
            *
            */

           for(int i = 0; i < transfer.length; i++) {
               transfer[i] -= fullScale;
           }
       }
       /* finally normalize to range of -1.0f to 1.0f */

       for(int i = 0; i < transfer.length; i++) {
           samples[i] = (float)transfer[i] / (float)fullScale;
       }

       return samples;
   }

   /**
    * most basic window function
    * multiply the window against a sine curve, tapers ends
    *
    * nested loops here show a paradigm for processing multi-channel formats
    * the interleaved samples can be processed "in place"
    * inner loop processes individual channels using an offset
    *
    * @param samples
    * @param svalid
    * @param fmt
    * @return samples
    */
   public static float[] window(final float[] samples, final int svalid,
           final AudioFormat fmt) {

       int channels = fmt.getChannels();
       int slen = svalid / channels;

       for(int ch = 0, k, i; ch < channels; ch++) {
           for(i = ch, k = 0; i < svalid; i += channels) {
               samples[i] *= Math.sin(Math.PI * k++ / (slen - 1));
           }
       }

       return samples;
   }

   public static AudioSamples getAudioSamples(final Audio audio) throws UnsupportedAudioFileException, IOException{
       final AudioInputStream originalStream = AudioSystem.getAudioInputStream(
           audio.getFile());
       AudioInputStream convertedStream = AudioMethods.getConvertedAudioStream(originalStream);
       final AudioFormat audioFormat = convertedStream.getFormat();
       double[][] channelSamples = null;
       try{
           channelSamples = AudioMethods.extractSampleValues(convertedStream);
       } catch(Exception ex){
           LOGGER.error(ex.getMessage());
       }

       originalStream.close();
       convertedStream.close();

       if(channelSamples == null){
           return null;
       }
       return new AudioSamples(channelSamples, audioFormat);
   }

   /**
    * @param audio
    * @return the 2D array containing the audio samples. The first indices
    * indicate the channel and the second indices indicate the samples in
    * the corresponding channel. In stereo, indices 0 corresponds to left and
    * indices 1 correspond to right. The value of the samples is between -1 and +1.
    * @throws UnsupportedAudioFileException
    * @throws IOException
    */
   public static double[][] getSamples(final Audio audio) throws UnsupportedAudioFileException, IOException{
       final AudioInputStream originalStream = AudioSystem.getAudioInputStream(
           audio.getFile());
       AudioInputStream convertedStream = AudioMethods.getConvertedAudioStream(originalStream);

       double[][] channelSamples = null;
       try{
           channelSamples = AudioMethods.extractSampleValues(convertedStream);
       } catch(Exception ex){
           LOGGER.error(ex.getMessage());
       }

       originalStream.close();

       if(convertedStream != null){
           convertedStream.close();
       }

       return channelSamples;
   }

   /**
    * @param audio
    * @return a double array containing the audio samples that have been
    * mixed down into one channel
    * @throws UnsupportedAudioFileException
    * @throws IOException
    */
   public static double[] getSamplesMixedDownIntoOneChannel(
           final Audio audio) throws UnsupportedAudioFileException, IOException {
       final double[][] samples = getSamples(audio);
       return DSPMethods.getSamplesMixedDownIntoOneChannel(samples);
   }

   /**
    * Converts samples to time in seconds
    * @param totalSamples
    * @param sampleRate
    * @return the time in seconds converted from samples
    */
   public static float convertSamplesToTime(final int totalSamples, final float sampleRate){
       if(totalSamples < 0){
           return 0;
       }
       return totalSamples / sampleRate;
   }



   /**
    *
    * @param samples
    * @param windowSize
    * @return
    */
   public static List<double[]> cutSamplesIntoWindows(final double[] samples,
           final int windowSize, final int windowOverlapInPercent) {

       final int windowOverlapOffset = (int)((windowOverlapInPercent / 100f)
               * windowSize);
       final List<double[]> result = new ArrayList<double[]>();

       int position =  0;
       int restSamples = samples.length;
       int toCopy = windowSize;
       while(position < samples.length){
           double[] window = new double[windowSize];
           if(toCopy > restSamples){
               toCopy = restSamples;
           }

           System.arraycopy(samples, position, window, 0, toCopy);
           position = position + windowSize - windowOverlapOffset;
           restSamples = samples.length - position;
           result.add(window);
       }

       return result;
    }



   /**
    * @param audioInputStream
    * @return bytes representation of the audio input stream
    * @throws IOException
    */
   public static byte[] getBytesFromAudioInputStream(
           final AudioInputStream audioInputStream) throws IOException{
        // Calculate the buffer size to use
        float buffer_duration_in_seconds = 0.25F;
        int buffer_size = AudioMethods.getNumberBytesNeeded(
            buffer_duration_in_seconds, audioInputStream.getFormat());
        byte rw_buffer[] = new byte[buffer_size + 2];

        // Read the bytes into the rw_buffer and then into the ByteArrayOutputStream
        ByteArrayOutputStream byte_array_output_stream = new ByteArrayOutputStream();
        int position = audioInputStream.read(rw_buffer, 0, rw_buffer.length);
        while (position > 0) {
            byte_array_output_stream.write(rw_buffer, 0, position);
            position = audioInputStream.read(rw_buffer, 0, rw_buffer.length);
        }
        byte[] results = byte_array_output_stream.toByteArray();

        byte_array_output_stream.close();

        // Return the results
        return results;
   }
}
