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
 *   May 8, 2016 (budiyanto): created
 */
package org.knime.base.node.audio3.data;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.knime.base.node.audio3.data.SampleChunk.ChunkType;
import org.knime.base.node.audio3.util.AudioUtils;
import org.knime.core.node.NodeLogger;

import jAudioFeatureExtractor.jAudioTools.AudioMethods;
import jAudioFeatureExtractor.jAudioTools.DSPMethods;

/**
 *
 * @author Budi Yanto, KNIME.com
 */
public class SampleChunkFactory {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(SampleChunkFactory.class);

    private InputStream m_inStream;

    private AudioInputStream m_audioInStream;

    private final Audio m_audio;

    //    private final boolean m_inSamples;
    private final float m_chunkSize;

    private int m_bytesToRead = 0;

    private final SampleChunk.ChunkType m_chunkType;

    private final int m_chunkOverlapOffset;

    private final boolean m_keepOriginalFormat;

    private byte[] m_samples;

    private int m_pointer;

    //    /**
    //     * @param audio
    //     * @param keepOriginalFormat
    //     * @param inSamples
    //     * @param chunkSize
    //     */
    //    public SampleChunkFactory(final Audio audio,
    //            final boolean inSamples, final int chunkSize){
    //        m_audio = audio;
    ////        m_inSamples = inSamples;
    //        m_chunkSize = chunkSize;
    //        m_chunkType = null;
    //        m_chunkOverlap = 0;
    //        m_keepOriginalFormat = false;
    //    }

    /**
     *
     * @param audio
     * @param chunkType
     * @param keepOriginalFormat
     * @param chunkSize
     * @param chunkOverlap
     */
    public SampleChunkFactory(final Audio audio, final SampleChunk.ChunkType chunkType,
        final boolean keepOriginalFormat, final float chunkSize, final float chunkOverlap) {
        m_audio = audio;
        m_chunkType = chunkType;
        m_chunkSize = chunkSize;
        m_keepOriginalFormat = keepOriginalFormat;
        m_chunkOverlapOffset = (int)(chunkOverlap * chunkSize);
        if(m_chunkOverlapOffset >= chunkSize){
            throw new IllegalArgumentException(
                "Chunk overlap offset cannot be greater than chunk size");
        }
    }

    private int calculateBytesToRead(final float chunkSize, final ChunkType chunkType) {
        final AudioFormat audioFormat = m_audioInStream.getFormat();
        int bytesToRead = 0;
        if (chunkType == ChunkType.BYTE) {
            bytesToRead = (int)(chunkSize * audioFormat.getFrameSize() * audioFormat.getFrameRate());
        } else if (chunkType == ChunkType.MONO_CHANNEL || chunkType == ChunkType.MULTI_CHANNELS) {
            final int normalizedBytes = AudioUtils.normalizeBytesFromBits(audioFormat.getSampleSizeInBits());
            bytesToRead = ((int)chunkSize) * audioFormat.getChannels() * normalizedBytes;
        }
        return bytesToRead;
    }

    private void openStream() throws UnsupportedAudioFileException, IOException {
        if (m_audioInStream == null && m_inStream == null) {
            m_inStream = new BufferedInputStream(new FileInputStream(m_audio.getFile()));
            if (m_chunkType == ChunkType.BYTE && m_keepOriginalFormat) {
                m_audioInStream = AudioSystem.getAudioInputStream(m_inStream);
            } else {
                final AudioInputStream inStream = AudioSystem.getAudioInputStream(m_inStream);
                m_audioInStream = AudioUtils.convertUnsupportedFormat(inStream);
                m_samples = AudioUtils.getBytesFromAudioInputStream(m_audioInStream);

                // TEST
//                LOGGER.debug("Create dummy samples");
//                LOGGER.debug("--------------------");
//                m_samples = new byte[10000];
//                new Random().nextBytes(m_samples);
//                LOGGER.debug("m_samples length: " + m_samples.length);
//                for (int i = 0; i < 20; i++) {
//                    LOGGER.debug("m_samples[" + i + "] = " + m_samples[i]);
//                }

            }
            m_pointer = 0;
            m_bytesToRead = calculateBytesToRead(m_chunkSize, m_chunkType);
        }
    }

    private void closeStream() throws IOException {
        if (m_audioInStream != null) {
            m_audioInStream.close();
            m_audioInStream = null;
        }
        if (m_inStream != null) {
            m_inStream.close();
            m_inStream = null;
        }
        m_pointer = 0;
        m_samples = null;
    }

    public SampleChunk nextSampleChunk() throws UnsupportedAudioFileException, IOException {
        openStream();
        SampleChunk chunk = null;
        if (m_chunkType == ChunkType.BYTE) {
            chunk = nextByteSampleChunk();
        } else if (m_chunkType == ChunkType.MONO_CHANNEL) {
            chunk = nextMonoChannelSampleChunk();
        } else if (m_chunkType == ChunkType.MULTI_CHANNELS) {
            chunk = nextMultiChannelsSampleChunk();
        }

        if (chunk == null) {
            closeStream();
        }

        return chunk;
    }

    //    private SampleChunk nextByteSampleChunk() {
    //        if(m_pointer >= m_samples.length){
    //            return null;
    //        }
    //
    ////        final AudioFormat audioFormat = m_stream.getFormat();
    ////        final int normalizedBytes = AudioUtils.normalizeBytesFromBits(
    ////            audioFormat.getSampleSizeInBits());
    ////        int size = m_chunkSize * audioFormat.getChannels() * normalizedBytes;
    //        int bytesToRead = calculateBytesToRead(m_chunkSize, m_chunkType);
    //        final int rest = m_samples.length - m_pointer;
    //        if(bytesToRead > rest){
    //            bytesToRead = rest;
    //        }
    //        final byte[] buf = new byte[bytesToRead];
    //        System.arraycopy(m_samples, m_pointer, buf, 0, bytesToRead);
    //        m_pointer += bytesToRead;
    //
    //        return new ByteSampleChunk(m_audioInStream.getFormat(), buf);
    //    }

    private SampleChunk nextMultiChannelsSampleChunk() {
        final double[][] samples = extractSamplesPerChunk();
        if(samples == null){
            return null;
        }else{
            return new MultiChannelSampleChunk(m_audioInStream.getFormat(), samples);
        }
    }

    private SampleChunk nextMonoChannelSampleChunk(){
        final double[][] multiChannelSamples = extractSamplesPerChunk();
        if(multiChannelSamples == null){
            return null;
        }else{
            final double[] monoChannelSamples = DSPMethods
                    .getSamplesMixedDownIntoOneChannel(multiChannelSamples);
            return new MonoChannelSampleChunk(m_audioInStream.getFormat(),
                monoChannelSamples);
        }
    }

    private double[][] extractSamplesPerChunk(){
        LOGGER.debug("nextSampleChunk()");
        LOGGER.debug("-----------------");

        if (m_pointer >= m_samples.length) {
            LOGGER.debug("m_pointer: " + m_pointer);
            LOGGER.debug("m_samples.length: " + m_samples.length);
            LOGGER.debug("m_pointer is bigger or equals m_samples.length");
            return null;
        }

        final AudioFormat audioFormat = m_audioInStream.getFormat();
        final int bitsPerSample = audioFormat.getSampleSizeInBits();
        final int bytesPerSample = bitsPerSample / 8;
        final int nrOfChannels = audioFormat.getChannels();

        final int totalBytesOverlapOffset = m_chunkOverlapOffset * bytesPerSample * nrOfChannels;

        LOGGER.debug("totalBytesOverlapOffset: " + totalBytesOverlapOffset);
        LOGGER.debug("bitsPerSample: " + bitsPerSample);
        LOGGER.debug("bytesPerSample: " + bytesPerSample);
        LOGGER.debug("nrOfChannels: " + nrOfChannels);
        LOGGER.debug("AudioFormat: " + audioFormat.toString());

        m_pointer -= totalBytesOverlapOffset;
        if (m_pointer < 0) {
            m_pointer = 0;
        }
        LOGGER.debug("m_pointer: " + m_pointer);

        final byte[] buf = new byte[m_bytesToRead];
        final int rest = m_samples.length - m_pointer;
        if (m_bytesToRead > rest) {
            m_bytesToRead = rest;
        }

        LOGGER.debug("buf.length : " + buf.length);
        LOGGER.debug("rest: " + rest);
        LOGGER.debug("m_bytesToRead: " + m_bytesToRead);

        System.arraycopy(m_samples, m_pointer, buf, 0, m_bytesToRead);
        m_pointer += m_bytesToRead;
        final double maxSampleValue = AudioMethods.findMaximumSampleValue(bitsPerSample) + 2.0;
        LOGGER.debug("maxSampleValue: " + maxSampleValue);
        final double[][] samples = new double[nrOfChannels][(int)m_chunkSize];
        ByteBuffer byteBuf = ByteBuffer.wrap(buf);
        if (bitsPerSample == 8) {
            for (int sample = 0; sample < m_chunkSize; sample++) {
                for (int channel = 0; channel < nrOfChannels; channel++) {
                    samples[channel][sample] = byteBuf.get() / maxSampleValue;
                }
            }
        } else if (bitsPerSample == 16) {
            ShortBuffer shortBuf = byteBuf.asShortBuffer();
            for (int sample = 0; sample < m_chunkSize; sample++) {
                for (int channel = 0; channel < nrOfChannels; channel++) {
                    samples[channel][sample] = shortBuf.get() / maxSampleValue;
                }
            }
        }

        return samples;

    }

    //    private SampleChunk nextMonoChannelSampleChunk() throws IOException{
    //        MultiChannelSampleChunk multiChannelChunk =
    //                (MultiChannelSampleChunk) nextMultiChannelsSampleChunk();
    //        if(multiChannelChunk == null){
    //            return null;
    //        }
    //
    //        final double[][] samples = multiChannelChunk.getSamples();
    //        final double[] mixedDownSamples = DSPMethods
    //                .getSamplesMixedDownIntoOneChannel(samples);
    //
    //        return new MonoChannelSampleChunk(m_stream.getFormat(), mixedDownSamples);
    //    }

    private SampleChunk nextByteSampleChunk() throws IOException {
        final byte[] buf = new byte[m_bytesToRead + 2];
        SampleChunk chunk = null;
        final int totalRead = m_audioInStream.read(buf);
        if (totalRead > 0) {
            if (buf.length == totalRead) {
                chunk = new ByteSampleChunk(m_audioInStream.getFormat(), buf);
            } else {
                final byte[] copy = new byte[totalRead];
                System.arraycopy(buf, 0, copy, 0, totalRead);
                chunk = new ByteSampleChunk(m_audioInStream.getFormat(), copy);
            }
        }
        return chunk;
    }

    //    private SampleChunk nextMultiChannelsSampleChunk() throws IOException {
    //        final AudioFormat audioFormat = m_audioInStream.getFormat();
    //        final int bitsPerSample = audioFormat.getSampleSizeInBits();
    //        final int bytesPerSample = bitsPerSample / 8;
    //        final int nrOfChannels = audioFormat.getChannels();
    //
    //        final byte[] buf = new byte[m_bytesToRead];
    //        final int totalRead = m_audioInStream.read(buf);
    //        if(totalRead > 0){
    //            final double[][] samples = new double[nrOfChannels][(int)m_chunkSize];
    //            final double maxSampleValue = AudioMethods.findMaximumSampleValue(
    //                bitsPerSample) + 2.0;
    //            final ByteBuffer byteBuf = ByteBuffer.wrap(buf);
    //            if (bitsPerSample == 8) {
    //                for (int sample = 0; sample < m_chunkSize; sample++) {
    //                    for (int channel = 0; channel < nrOfChannels; channel++) {
    //                        try{
    //                            samples[channel][sample] = byteBuf.get() / maxSampleValue;
    //                        } catch(BufferUnderflowException ex){
    //                            samples[channel][sample] = 0;
    //                        }
    //                    }
    //                }
    //            } else if (bitsPerSample == 16) {
    //                ShortBuffer shortBuf = byteBuf.asShortBuffer();
    //                for (int sample = 0; sample < m_chunkSize; sample++) {
    //                    for (int channel = 0; channel < nrOfChannels; channel++) {
    //                        try{
    //                            samples[channel][sample] = shortBuf.get() / maxSampleValue;
    //                        } catch(BufferUnderflowException ex){
    //                            samples[channel][sample] = 0;
    //                        }
    //                    }
    //                }
    //            }
    //        }
    //
    //
    //        m_pointer = totalRead;
    //
    //        final int bytesOverlapOffset = m_chunkOverlapOffset * bytesPerSample * nrOfChannels;
    //
    //        return null;
    //    }
    //
    //    private SampleChunk nextMonoChannelSampleChunk() throws IOException {
    //        return null;
    //    }

    //    private SampleChunk nextMultiChannelsSampleChunk() throws IOException {
    //        ByteSampleChunk byteChunk = (ByteSampleChunk) nextByteSampleChunk();
    //        if(byteChunk == null){
    //            return null;
    //        }
    //        final byte[] byteSamples = byteChunk.getSamples();
    //        final AudioFormat audioFormat = m_stream.getFormat();
    //        final int bitPerSample = audioFormat.getSampleSizeInBits();
    //        final int bytesPerSample = bitPerSample / 8;
    //        final int nrOfChannels = audioFormat.getChannels();
    //        final int nrOfSamples = byteSamples.length / bytesPerSample / nrOfChannels;
    //
    //        final double[][] samples = new double[nrOfChannels][nrOfSamples];
    //        final double maxSampleValue = AudioMethods.findMaximumSampleValue(
    //            bitPerSample) + 2.0;
    //        ByteBuffer byteBuf = ByteBuffer.wrap(byteSamples);
    //        if(bitPerSample == 8){
    //            for(int sample = 0; sample < nrOfSamples; sample++){
    //                for(int channel = 0; channel < nrOfChannels; channel++){
    //                    samples[channel][sample] = byteBuf.get() / maxSampleValue;
    //                }
    //            }
    //        }else if(bitPerSample == 16){
    //            ShortBuffer shortBuf = byteBuf.asShortBuffer();
    //            for(int sample = 0; sample < nrOfSamples; sample++){
    //                for(int channel = 0; channel < nrOfChannels; channel++){
    //                    samples[channel][sample] = shortBuf.get() / maxSampleValue;
    //                }
    //            }
    //        }
    //
    //        return new MultiChannelSampleChunk(audioFormat, samples);
    //    }

}
