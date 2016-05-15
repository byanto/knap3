package org.knime.base.node.audio3.node.recognizer.watson;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.knime.base.node.audio3.node.recognizer.RecognizerNodeModel;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.container.ColumnRearranger;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * This is the model implementation of WatsonSpeechRecognizer.
 *
 *
 * @author Budi Yanto, KNIME.com
 */
public class WatsonSpeechRecognizerNodeModel extends RecognizerNodeModel {

    private final SettingsModelString m_userNameSettingsModel = createUserNameSettingsModel();
    private final SettingsModelString m_passwordSettingsModel = createPasswordSettingsModel();

    static SettingsModelString createUserNameSettingsModel(){
        return new SettingsModelString("UserName", null);
    }

    static SettingsModelString createPasswordSettingsModel(){
        return new SettingsModelString("Password", null);
    }

    /**
     * Constructor for the node model.
     */
    protected WatsonSpeechRecognizerNodeModel() {
        super(new WatsonSpeechRecognizer());
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

        final WatsonSpeechRecognizer recognizer = (WatsonSpeechRecognizer) getRecognizer();
        recognizer.setUserName(m_userNameSettingsModel.getStringValue());
        recognizer.setPassword(m_passwordSettingsModel.getStringValue());

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

//        if (inSpecs == null || inSpecs.length < 1) {
//            throw new IllegalArgumentException("Invalid input data table spec");
//        }

        final DataTableSpec inSpec = inSpecs[0];
        super.configure(inSpec);

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
        super.saveSettingsTo(settings);
        m_userNameSettingsModel.saveSettingsTo(settings);
        m_passwordSettingsModel.saveSettingsTo(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        super.loadValidatedSettingsFrom(settings);
        m_userNameSettingsModel.loadSettingsFrom(settings);
        m_passwordSettingsModel.loadSettingsFrom(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        super.validateSettings(settings);
        m_userNameSettingsModel.validateSettings(settings);
        m_passwordSettingsModel.validateSettings(settings);
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
}

