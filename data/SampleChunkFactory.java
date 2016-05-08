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

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.knime.base.node.audio3.util.AudioUtils;

/**
 *
 * @author Budi Yanto, KNIME.com
 */
public class SampleChunkFactory {

    private AudioInputStream m_stream;
    private final Audio m_audio;
    private final boolean m_inSamples;
    private final int m_chunkSize;

    /**
     * @param audio
     * @param keepOriginalFormat
     * @param inSamples
     * @param chunkSize
     */
    public SampleChunkFactory(final Audio audio,
            final boolean inSamples, final int chunkSize){
        m_audio = audio;
        m_inSamples = inSamples;
        m_chunkSize = chunkSize;
    }

    private void openStream() throws UnsupportedAudioFileException, IOException{
        if(m_stream == null){
            m_stream = AudioSystem.getAudioInputStream(m_audio.getFile());
//            if(m_keepOriginalFormat){
//
//            }else{
//                AudioInputStream inStream = AudioSystem.getAudioInputStream(m_audio.getFile());
//                m_stream = AudioUtils.convertUnsupportedFormat(inStream);
//            }
        }
    }

    private void closeStream() throws IOException{
        if(m_stream != null){
            m_stream.close();
            m_stream = null;
        }
    }

    /**
     * @return the next sample chunk
     * @throws UnsupportedAudioFileException
     * @throws IOException
     */
    public SampleChunk nextSampleChunk() throws UnsupportedAudioFileException, IOException{
        openStream();
        final AudioFormat audioFormat = m_stream.getFormat();
        final int normalizedBytes = AudioUtils.normalizeBytesFromBits(
            audioFormat.getSampleSizeInBits());
        byte[] buf = new byte[m_chunkSize * audioFormat.getChannels() * normalizedBytes];
        SampleChunk chunk = null;
        final int length = m_stream.read(buf);
        if(length != -1){ // Data is available to read
            if(buf.length == length){
                chunk = new SampleChunk(buf, audioFormat);
            }else{
                final byte[] copy = new byte[length];
                System.arraycopy(buf, 0, copy, 0, length);
                chunk = new SampleChunk(copy, audioFormat);
            }
        }

        if(chunk == null){
            closeStream();
        }
        return chunk;
    }

}
