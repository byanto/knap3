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
package org.knime.base.node.audio3.data;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;

import org.knime.base.node.audio3.data.io.BufferedDataInputStream;
import org.knime.base.node.audio3.data.io.BufferedDataOutputStream;
import org.knime.core.data.DataCellDataInput;
import org.knime.core.data.DataCellDataOutput;

/**
 *
 * @author Budi Yanto, KNIME.com
 */
public class AudioBuilder {

    /**
     *
     * @param filePath
     * @return a new audio instance
     * @throws UnsupportedAudioFileException
     * @throws IOException
     */
    public static Audio createAudio(final String filePath)
            throws UnsupportedAudioFileException, IOException{
        return createAudio(new File(filePath));
    }

    /**
     *
     * @param file
     * @return a new audio instance
     * @throws UnsupportedAudioFileException
     * @throws IOException
     */
    public static Audio createAudio(final File file)
            throws UnsupportedAudioFileException, IOException{
        return new Audio(file);
    }

    /**
    *
    * @param audio
    * @return a new audio instance
    */
   public static Audio createAudio(final Audio audio){
       return new Audio(audio.getFile(), audio.getRecognitionResults());
   }

    /**
     *
     * @param audio
     * @param output
     * @throws IOException
     */
    public static void serialize(final Audio audio,
            final DataCellDataOutput output) throws IOException{
        Audio.serialize(audio, output);
    }

    /**
     *
     * @param input
     * @return an <code>Audio</code> instance
     * @throws IOException
     * @throws UnsupportedAudioFileException
     */
    public static Audio deserialize(final DataCellDataInput input)
            throws IOException, UnsupportedAudioFileException{
        return Audio.deserialize(input);
    }

    /**
     *
     * @param audio
     * @param output
     * @throws IOException
     */
    public static void saveInternals(final Audio audio,
            final BufferedDataOutputStream output) throws IOException{
        Audio.saveInternals(audio, output);
    }

    /**
     *
     * @param input
     * @return an <code>Audio</code> instance
     * @throws IOException
     * @throws UnsupportedAudioFileException
     */
    public static Audio loadInternals(final BufferedDataInputStream input)
            throws IOException, UnsupportedAudioFileException {
        return Audio.loadInternals(input);
    }

}
