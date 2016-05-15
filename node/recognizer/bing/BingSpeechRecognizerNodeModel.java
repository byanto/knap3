package org.knime.base.node.audio3.node.recognizer.bing;

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
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * This is the model implementation of BingSpeechRecognizer.
 *
 *
 * @author Budi Yanto, KNIME.com
 */
public class BingSpeechRecognizerNodeModel extends RecognizerNodeModel {

    private final SettingsModelString m_subscriptionKeyModel = createSubscriptionKeySettingsModel();
    private final SettingsModelString m_audioLanguageModel = createAudioLanguageSettingsModel();
    private final SettingsModelString m_scenarioModel = createScenarioSettingsModel();
    private final SettingsModelIntegerBounded m_maxNBestModel = createMaxNBestSettingsModel();
    private final SettingsModelIntegerBounded m_profinityMarkupModel = createProfanityMarkupSettingsModel();

    static SettingsModelString createSubscriptionKeySettingsModel(){
        return new SettingsModelString("SubscriptionKey", null);
    }

    static SettingsModelString createAudioLanguageSettingsModel(){
        return new SettingsModelString("AudioLanguage", BingSpeechRecognizer.DEFAULT_LANGUAGE);
    }

    static SettingsModelString createScenarioSettingsModel(){
        return new SettingsModelString("Scenario", BingSpeechRecognizer.DEFAULT_SCENARIO);
    }

    static SettingsModelIntegerBounded createMaxNBestSettingsModel(){
        return new SettingsModelIntegerBounded("MaxNBest",
            BingSpeechRecognizer.DEFAULT_MAXNBEST, 1, 5);
    }

    static SettingsModelIntegerBounded createProfanityMarkupSettingsModel(){
        return new SettingsModelIntegerBounded("ProfanityMarkup",
            BingSpeechRecognizer.DEFAULT_PROFANITY_MARKUP, 0, 1);
    }

    /**
     * Constructor for the node model.
     */
    protected BingSpeechRecognizerNodeModel() {
        super(new BingSpeechRecognizer());
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

        final BingSpeechRecognizer recognizer = (BingSpeechRecognizer) getRecognizer();
        recognizer.setSubscriptionKey(m_subscriptionKeyModel.getStringValue());
        recognizer.setLanguage(m_audioLanguageModel.getStringValue());
        recognizer.setScenario(m_scenarioModel.getStringValue());
        recognizer.setMaxNBest(m_maxNBestModel.getIntValue());
        recognizer.setProfanityMarkup(m_profinityMarkupModel.getIntValue());

        final BufferedDataTable dataTable = inData[0];
        final ColumnRearranger rearranger = createColumnRearranger(
            dataTable.getDataTableSpec());

        return new BufferedDataTable[]{
            exec.createColumnRearrangeTable(dataTable, rearranger, exec)};

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

        final DataTableSpec inSpec = inSpecs[0];
        super.configure(inSpec);

        if(StringUtils.isBlank(m_subscriptionKeyModel.getStringValue())){
            throw new InvalidSettingsException("Subscription key cannot be empty.");
        }

        return new DataTableSpec[]{createColumnRearranger(inSpec).createSpec()};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
         super.saveSettingsTo(settings);
         m_subscriptionKeyModel.saveSettingsTo(settings);
         m_audioLanguageModel.saveSettingsTo(settings);
         m_scenarioModel.saveSettingsTo(settings);
         m_maxNBestModel.saveSettingsTo(settings);
         m_profinityMarkupModel.saveSettingsTo(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        super.loadValidatedSettingsFrom(settings);
        m_subscriptionKeyModel.loadSettingsFrom(settings);
        m_audioLanguageModel.loadSettingsFrom(settings);
        m_scenarioModel.loadSettingsFrom(settings);
        m_maxNBestModel.loadSettingsFrom(settings);
        m_profinityMarkupModel.loadSettingsFrom(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        super.validateSettings(settings);
        m_subscriptionKeyModel.validateSettings(settings);
        m_audioLanguageModel.validateSettings(settings);
        m_scenarioModel.validateSettings(settings);
        m_maxNBestModel.validateSettings(settings);
        m_profinityMarkupModel.validateSettings(settings);
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

