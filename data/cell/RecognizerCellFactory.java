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
 *   May 14, 2016 (budiyanto): created
 */
package org.knime.base.node.audio3.data.cell;

import org.knime.base.node.audio3.data.Audio;
import org.knime.base.node.audio3.data.AudioBuilder;
import org.knime.base.node.audio3.data.recognizer.RecognitionResult;
import org.knime.base.node.audio3.data.recognizer.Recognizer;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataType;
import org.knime.core.data.container.AbstractCellFactory;
import org.knime.core.data.def.StringCell;

/**
 *
 * @author Budi Yanto, KNIME.com
 */
public class RecognizerCellFactory extends AbstractCellFactory{

    private final int m_audioColIdx;
    private final Recognizer m_recognizer;

    /**
     *
     * @param audioColIdx
     * @param recognizer
     * @param colSpecs
     */
    public RecognizerCellFactory(final int audioColIdx,
            final Recognizer recognizer, final DataColumnSpec[] colSpecs){
        super(colSpecs);
        m_audioColIdx = audioColIdx;
        m_recognizer = recognizer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataCell[] getCells(final DataRow row) {
        final DataCell[] cells = new DataCell[getColumnSpecs().length];
        final DataCell audioCell = row.getCell(m_audioColIdx);
        if(!audioCell.getType().isCompatible(AudioValue.class)){
            throw new IllegalStateException("Invalid column type");
        }

        if(audioCell.isMissing()){
            for(int i = 0; i < cells.length; i++){
                cells[i] = DataType.getMissingCell();
            }
        } else {
            /* Create a new audio cell containing the recognition result
             * to replace the old one */
            final Audio newAudio = AudioBuilder.createAudio(
                ((AudioCell) audioCell).getAudio());
//            final RecognitionResult result = m_recognizer.recognize(newAudio);

            final RecognitionResult result = new RecognitionResult(
                "Dummy Recognizer " + row.getKey(),
                "Dummy Transcript " + row.getKey());
            newAudio.addRecognitionResult(result);
            cells[0] = new AudioCell(newAudio);

            /* Append the transcription if it is necessary */
            if(cells.length == 2){
                cells[1] = new StringCell(result.getTranscript());
            }
        }
        return cells;
    }

}
