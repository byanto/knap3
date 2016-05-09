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

import java.io.IOException;
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

    private AudioInputStream m_stream;
    private final Audio m_audio;
//    private final boolean m_inSamples;
    private final int m_chunkSize;
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
            final boolean keepOriginalFormat, final int chunkSize, final float chunkOverlap){
        m_audio = audio;
        m_chunkType = chunkType;
        m_keepOriginalFormat = keepOriginalFormat;
        m_chunkSize = chunkSize;
        m_chunkOverlapOffset = (int) (chunkOverlap * chunkSize);
    }

    private void openStream() throws UnsupportedAudioFileException, IOException{
        if(m_stream == null){
            if(m_chunkType == ChunkType.BYTE && m_keepOriginalFormat){
                m_stream = AudioSystem.getAudioInputStream(m_audio.getFile());
            } else {
                AudioInputStream inStream = AudioSystem.getAudioInputStream(
                    m_audio.getFile());
                m_stream = AudioUtils.convertUnsupportedFormat(inStream);
            }
            m_samples = AudioUtils.getBytesFromAudioInputStream(m_stream);
            m_pointer = 0;
        }
    }

    private void closeStream() throws IOException{
        if(m_stream != null){
            m_stream.close();
            m_stream = null;
            m_samples = null;
            m_pointer = 0;
        }
    }

    public SampleChunk nextSampleChunk() throws UnsupportedAudioFileException, IOException{
        openStream();
        SampleChunk chunk = null;
        if(m_chunkType == ChunkType.BYTE){
            chunk = nextByteSampleChunk();
        }else if(m_chunkType == ChunkType.MONO_CHANNEL){
            chunk = nextMonoChannelSampleChunk();
        }else if(m_chunkType == ChunkType.MULTI_CHANNELS){
            chunk = nextMultiChannelsSampleChunk();
        }

        if(chunk == null){
            closeStream();
        }

        return chunk;
    }

    private SampleChunk nextByteSampleChunk() {
        if(m_pointer >= m_samples.length){
            return null;
        }

        final AudioFormat audioFormat = m_stream.getFormat();
        final int normalizedBytes = AudioUtils.normalizeBytesFromBits(
            audioFormat.getSampleSizeInBits());
        int size = m_chunkSize * audioFormat.getChannels() * normalizedBytes;
        final int rest = m_samples.length - m_pointer;
        if(size > rest){
            size = rest;
        }
        final byte[] buf = new byte[size];
        System.arraycopy(m_samples, m_pointer, buf, 0, size);
        m_pointer += size;

        return new ByteSampleChunk(audioFormat, buf);
    }

    private SampleChunk nextMultiChannelsSampleChunk() {
        if(m_pointer >= m_samples.length){
            return null;
        }

        final AudioFormat audioFormat = m_stream.getFormat();
        final int bitPerSample = audioFormat.getSampleSizeInBits();
        final int bytesPerSample = bitPerSample / 8;
        final int nrOfChannels = audioFormat.getChannels();

        final int totalBytesOverlapOffset = m_chunkOverlapOffset
                * bytesPerSample * nrOfChannels;
        m_pointer -= totalBytesOverlapOffset;
        if(m_pointer < 0){
            m_pointer = 0;
        }

        int totalBytestoRead = m_chunkSize * bytesPerSample * nrOfChannels;
        final byte[] buf = new byte[totalBytestoRead];
        final int rest = m_samples.length - m_pointer;
        if(totalBytestoRead > rest){
            totalBytestoRead = rest;
        }

        System.arraycopy(m_samples, m_pointer, buf, 0, totalBytestoRead);
        m_pointer += totalBytestoRead;
        final double maxSampleValue = AudioMethods.findMaximumSampleValue(bitPerSample) + 2.0;
        final double[][] samples = new double[nrOfChannels][m_chunkSize];
        ByteBuffer byteBuf = ByteBuffer.wrap(buf);
        if (bitPerSample == 8) {
            for (int sample = 0; sample < m_chunkSize; sample++) {
                for (int channel = 0; channel < nrOfChannels; channel++) {
                    samples[channel][sample] = byteBuf.get() / maxSampleValue;
                }
            }
        } else if (bitPerSample == 16) {
            ShortBuffer shortBuf = byteBuf.asShortBuffer();
            for (int sample = 0; sample < m_chunkSize; sample++) {
                for (int channel = 0; channel < nrOfChannels; channel++) {
                    samples[channel][sample] = shortBuf.get() / maxSampleValue;
                }
            }
        }

        return new MultiChannelSampleChunk(audioFormat, samples);
    }

    private SampleChunk nextMonoChannelSampleChunk() throws IOException{
        MultiChannelSampleChunk multiChannelChunk =
                (MultiChannelSampleChunk) nextMultiChannelsSampleChunk();
        if(multiChannelChunk == null){
            return null;
        }

        final double[][] samples = multiChannelChunk.getSamples();
        final double[] mixedDownSamples = DSPMethods
                .getSamplesMixedDownIntoOneChannel(samples);

        return new MonoChannelSampleChunk(m_stream.getFormat(), mixedDownSamples);
    }

//    private SampleChunk nextByteSampleChunk() throws IOException {
//        final AudioFormat audioFormat = m_stream.getFormat();
//        final int normalizedBytes = AudioUtils.normalizeBytesFromBits(
//            audioFormat.getSampleSizeInBits());
//        final byte[] buf = new byte[m_chunkSize * audioFormat.getChannels() * normalizedBytes];
//        SampleChunk chunk = null;
//        final int totalRead = m_stream.read(buf);
//        if(totalRead > -1){
//            if(buf.length == totalRead){
//                chunk = new ByteSampleChunk(audioFormat, buf);
//            }else{
//                final byte[] copy = new byte[totalRead];
//                System.arraycopy(buf, 0, copy, 0, totalRead);
//                chunk = new ByteSampleChunk(audioFormat, copy);
//            }
//        }
//        return chunk;
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
