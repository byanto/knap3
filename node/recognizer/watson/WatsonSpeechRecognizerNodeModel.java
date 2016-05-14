package org.knime.base.node.audio3.node.recognizer.watson;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.knime.base.node.audio.data.node.AudioColumnSelection;
import org.knime.base.node.audio3.data.cell.RecognizerCellFactory;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.container.ColumnRearranger;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.util.UniqueNameGenerator;

/**
 * This is the model implementation of WatsonSpeechRecognizer.
 *
 *
 * @author Budi Yanto, KNIME.com
 */
public class WatsonSpeechRecognizerNodeModel extends NodeModel {

    private AudioColumnSelection m_audioColumnSelection = new AudioColumnSelection();
    private SettingsModelString m_userNameSettingsModel = createUserNameSettingsModel();
    private SettingsModelString m_passwordSettingsModel = createPasswordSettingsModel();
    private SettingsModelBoolean m_appendTranscriptionModel = createAppendTranscriptionModel();
    private final WatsonSpeechRecognizer m_recognizer = new WatsonSpeechRecognizer();

    static SettingsModelString createUserNameSettingsModel(){
        return new SettingsModelString("UserName", null);
    }

    static SettingsModelString createPasswordSettingsModel(){
        return new SettingsModelString("Password", null);
    }

    static SettingsModelBoolean createAppendTranscriptionModel(){
        return new SettingsModelBoolean("AppendTranscription", true);
    }

    /**
     * Constructor for the node model.
     */
    protected WatsonSpeechRecognizerNodeModel() {
        super(1, 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
            final ExecutionContext exec) throws Exception {

        if (inData == null || inData.length < 1) {
            throw new IllegalArgumentException("Invalid input data");
        }

        m_recognizer.setUserName(m_userNameSettingsModel.getStringValue());
        m_recognizer.setPassword(m_passwordSettingsModel.getStringValue());

        final BufferedDataTable dataTable = inData[0];
        final ColumnRearranger rearranger = createColumnRearranger(
            dataTable.getDataTableSpec());

        return new BufferedDataTable[]{exec
            .createColumnRearrangeTable(dataTable, rearranger, exec)};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void reset() {
        // TODO: generated method stub
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DataTableSpec[] configure(final DataTableSpec[] inSpecs)
            throws InvalidSettingsException {

        if (inSpecs == null || inSpecs.length < 1) {
            throw new IllegalArgumentException("Invalid input data table spec");
        }

        final DataTableSpec inSpec = inSpecs[0];
        m_audioColumnSelection.configure(inSpec);

        if(StringUtils.isBlank(m_userNameSettingsModel.getStringValue())){
            throw new InvalidSettingsException("User name cannot be empty.");
        }

        if(StringUtils.isBlank(m_passwordSettingsModel.getStringValue())){
            throw new InvalidSettingsException("Password cannot be empty.");
        }

        return new DataTableSpec[]{createColumnRearranger(inSpec).createSpec()};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
        m_audioColumnSelection.saveSettingsTo(settings);
        m_userNameSettingsModel.saveSettingsTo(settings);
        m_passwordSettingsModel.saveSettingsTo(settings);
        m_appendTranscriptionModel.saveSettingsTo(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        m_audioColumnSelection.laodSettingsFrom(settings);
        m_userNameSettingsModel.loadSettingsFrom(settings);
        m_passwordSettingsModel.loadSettingsFrom(settings);
        m_appendTranscriptionModel.loadSettingsFrom(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        m_audioColumnSelection.validateSettings(settings);
        m_userNameSettingsModel.validateSettings(settings);
        m_passwordSettingsModel.validateSettings(settings);
        m_appendTranscriptionModel.validateSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
        // TODO: generated method stub
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
        // TODO: generated method stub
    }

    private ColumnRearranger createColumnRearranger(final DataTableSpec inSpec){
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

