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
package org.knime.base.node.audio3.data.cell;

import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;

import org.knime.base.node.audio3.data.Audio;
import org.knime.base.node.audio3.data.AudioBuilder;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataCellDataInput;
import org.knime.core.data.DataCellDataOutput;
import org.knime.core.data.DataCellSerializer;
import org.knime.core.data.DataType;
import org.knime.core.data.StringValue;

/**
 *
 * @author Budi Yanto, KNIME.com
 */
public class AudioCell extends DataCell implements AudioValue, StringValue{

    /**
     * Serializer for <code>AudioCell</code>s.
     * @noreference This class is not intended to be referenced by clients.
     */
    public static final class AudioSerializer implements DataCellSerializer<AudioCell> {

        /**
         * {@inheritDoc}
         */
        @Override
        public void serialize(final AudioCell cell, final DataCellDataOutput output) throws IOException {
            output.writeUTF(cell.getAudio().getFile().getAbsolutePath());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public AudioCell deserialize(final DataCellDataInput input) throws IOException {
            final String filePath = input.readUTF();
            final Audio audio;
            try{
                audio = AudioBuilder.createAudio(filePath);
            } catch(UnsupportedAudioFileException ex){
                throw new IOException(ex);
            }
            return new AudioCell(audio);
        }

    }

    /**
     * Automatically generated Version UID
     */
    private static final long serialVersionUID = 3098973491962802951L;

    /**
     * Convenience access member for
     * <code>DataType.getType(AudioCell.class)</code>.
     *
     * @see DataType#getType(Class)
     */
    public static final DataType TYPE = DataType.getType(AudioCell.class);

    private final Audio m_audio;

    /**
     * Prevent to directly create a new audio cell instance.
     * A new audio cell should only be created using {@link AudioCellFactory}.
     */
    AudioCell(final Audio audio){
        if(audio == null){
            throw new IllegalArgumentException("Audio cannot be null.");
        }
        m_audio = audio;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getStringValue() {
        StringBuilder builder = new StringBuilder();
        builder.append("Audio[\npath=");
        builder.append(m_audio.getFile().getAbsolutePath());
        builder.append("\n]");
        return builder.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Audio getAudio() {
        return m_audio;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getStringValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean equalsDataCell(final DataCell dc) {
        if(dc == null){
            return false;
        }

        AudioCell cell = (AudioCell) dc;
        if(!cell.getAudio().equals(m_audio)){
            return false;
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return AudioValue.hashCode(this);
    }

}
