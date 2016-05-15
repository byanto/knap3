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
 *   May 15, 2016 (budiyanto): created
 */
package org.knime.base.node.audio3.node.recognizer;

import java.io.File;
import java.io.IOException;

import org.knime.base.node.audio3.data.cell.RecognizerCellFactory;
import org.knime.base.node.audio3.data.component.AudioColumnSelection;
import org.knime.base.node.audio3.data.recognizer.Recognizer;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.container.ColumnRearranger;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.util.UniqueNameGenerator;

/**
 *
 * @author Budi Yanto, KNIME.com
 */
public class RecognizerNodeModel extends NodeModel{

    private final AudioColumnSelection m_audioColumnSelection = new AudioColumnSelection();
    private final SettingsModelBoolean m_appendTranscriptionModel = createAppendTranscriptionModel();
    private final Recognizer m_recognizer;

    static SettingsModelBoolean createAppendTranscriptionModel(){
        return new SettingsModelBoolean("AppendTranscription", true);
    }

    /**
     * Constructor for the node model.
     * @param recognizer
     */
    protected RecognizerNodeModel(final Recognizer recognizer) {
        super(1, 1);
        m_recognizer = recognizer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadInternals(final File nodeInternDir, final ExecutionMonitor exec)
        throws IOException, CanceledExecutionException {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveInternals(final File nodeInternDir, final ExecutionMonitor exec)
        throws IOException, CanceledExecutionException {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
        m_audioColumnSelection.saveSettingsTo(settings);
        m_appendTranscriptionModel.saveSettingsTo(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_audioColumnSelection.validateSettings(settings);
        m_appendTranscriptionModel.validateSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_audioColumnSelection.loadSettingsFrom(settings);
        m_appendTranscriptionModel.loadSettingsFrom(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void reset() {
        // TODO Auto-generated method stub

    }

    /**
     * @return the recognizer
     */
    protected Recognizer getRecognizer(){
        return m_recognizer;
    }

    /**
     *
     * @param inSpec
     * @throws InvalidSettingsException
     */
    protected void configure(final DataTableSpec inSpec)
            throws InvalidSettingsException{
        if (inSpec == null) {
            throw new IllegalArgumentException("Invalid input data table spec");
        }

        m_audioColumnSelection.configure(inSpec);
    }

    /**
     *
     * @param inSpec
     * @return a <code>ColumnRearranger</code>
     */
    protected ColumnRearranger createColumnRearranger(final DataTableSpec inSpec){
        final ColumnRearranger rearranger = new ColumnRearranger(inSpec);
        final int colIdx = m_audioColumnSelection.getSelectedColumnIndex();

        final DataColumnSpec[] newSpecs;
        final DataColumnSpec audioSpec = inSpec.getColumnSpec(colIdx);
        if(m_appendTranscriptionModel.getBooleanValue()){
            final DataColumnSpec transcriptionSpec = new UniqueNameGenerator(inSpec)
                    .newColumn("Transcription", StringCell.TYPE);
            newSpecs = new DataColumnSpec[]{audioSpec, transcriptionSpec};
        } else {
            newSpecs = new DataColumnSpec[]{audioSpec};
        }

        rearranger.remove(colIdx);
        rearranger.append(new RecognizerCellFactory(colIdx,
            m_recognizer, newSpecs));
        rearranger.move(audioSpec.getName(), 0);

        return rearranger;
    }

}
