package org.knime.base.node.audio3.node.featureextractor;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.knime.base.node.audio3.data.component.AudioColumnSelection;
import org.knime.base.node.audio3.data.feature.FeatureType;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.container.ColumnRearranger;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * This is the model implementation of FeatureExtractor.
 *
 *
 * @author Budi Yanto, KNIME.com
 */
public class FeatureExtractorNodeModel extends NodeModel {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(FeatureExtractorNodeModel.class);

    static final String MEAN = "Mean";
    static final String STD_DEVIATION = "Standard Deviation";

    private final AudioColumnSelection m_audioColumnSelectionSettingsModel = new AudioColumnSelection();
    private final SettingsModelIntegerBounded m_windowSizeSettingsModel = createWindowSizeSettingsModel();
    private final SettingsModelIntegerBounded m_windowOverlapSettingsModel = createWindowOverlapSettingsModel();
    private final SettingsModelString m_aggregatorSettingsModel = createAggregatorSettingsModel();

    private final FeatureExtractorSettings m_settings = new FeatureExtractorSettings();

    /**
     * Constructor for the node model.
     */
    protected FeatureExtractorNodeModel() {
        super(1, 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
            final ExecutionContext exec) throws Exception {
        LOGGER.debug("--------------------------");
        LOGGER.debug("Audio Column: " + m_audioColumnSelectionSettingsModel.getSelectedColumn());
        LOGGER.debug("Windows size: " + m_windowSizeSettingsModel.getIntValue());
        LOGGER.debug("Window overlap: " + m_windowOverlapSettingsModel.getIntValue());
        LOGGER.debug("Aggregator: " + m_aggregatorSettingsModel.getStringValue());
        LOGGER.debug("--------------------------");
        LOGGER.debug("All Features");
        for(FeatureType type : m_settings.getAudioFeatureTypes()){
            LOGGER.debug("Type: " + type.getName());
            LOGGER.debug("Is Selected: " + m_settings.isSelected(type));
            for(String parameter : type.getParameters()){
                LOGGER.debug("Parameter: " + parameter);
                LOGGER.debug("Parameter Value: " + m_settings.getParameterValue(type, parameter));
            }
        }
        LOGGER.debug("--------------------------");
        LOGGER.debug("Selected Features");
        for(FeatureType type : m_settings.getSelectedFeatures()){
            LOGGER.debug("Type: " + type.getName());
            LOGGER.debug("Is Selected: " + m_settings.isSelected(type));
            for(String parameter : type.getParameters()){
                LOGGER.debug("Parameter: " + parameter);
                LOGGER.debug("Parameter Value: " + m_settings.getParameterValue(type, parameter));
            }
        }

        if (inData == null || inData.length < 1) {
            throw new IllegalArgumentException("Invalid input data");
        }

        final BufferedDataTable dataTable = inData[0];

        Set<FeatureType> selFeatures = m_settings.getSelectedFeatures();
        final BufferedDataTable resultTable;
        if(selFeatures == null || selFeatures.isEmpty()){
            setWarningMessage("No feature is selected. Node returns the original unaltered table.");
            resultTable = dataTable;
        }else{
            final ColumnRearranger rearranger = createColumnRearranger(
                dataTable.getDataTableSpec());
            resultTable = exec.createColumnRearrangeTable(dataTable, rearranger, exec);
        }

        return new BufferedDataTable[]{resultTable};
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
            throw new InvalidSettingsException("Invalid input spec");
        }
        final Set<FeatureType> selectedTypes = m_settings.getSelectedFeatures();
        if(selectedTypes == null || selectedTypes.isEmpty()){
            setWarningMessage("No feature is selected.");
        }

        final DataTableSpec inSpec = inSpecs[0];
        m_audioColumnSelectionSettingsModel.configure(inSpec);

        return new DataTableSpec[]{createColumnRearranger(inSpec).createSpec()};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
        m_audioColumnSelectionSettingsModel.saveSettingsTo(settings);
        m_windowSizeSettingsModel.saveSettingsTo(settings);
        m_windowOverlapSettingsModel.saveSettingsTo(settings);
        m_aggregatorSettingsModel.saveSettingsTo(settings);
        m_settings.saveSettingsTo(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        m_audioColumnSelectionSettingsModel.laodSettingsFrom(settings);
        m_windowSizeSettingsModel.loadSettingsFrom(settings);
        m_windowOverlapSettingsModel.loadSettingsFrom(settings);
        m_aggregatorSettingsModel.loadSettingsFrom(settings);
        m_settings.loadSettingsFrom(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        m_audioColumnSelectionSettingsModel.validateSettings(settings);
        m_windowSizeSettingsModel.validateSettings(settings);
        m_windowOverlapSettingsModel.validateSettings(settings);
        m_aggregatorSettingsModel.validateSettings(settings);
        m_settings.validateSettings(settings);
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

    static SettingsModelIntegerBounded createWindowSizeSettingsModel(){
        return new SettingsModelIntegerBounded("windowSize", 512, 4, Integer.MAX_VALUE);
    }

    static SettingsModelIntegerBounded createWindowOverlapSettingsModel(){
        return new SettingsModelIntegerBounded("windowOverlap", 0, 0, 99);
    }

    static SettingsModelString createAggregatorSettingsModel(){
        return new SettingsModelString("aggregator", MEAN);
    }

    private ColumnRearranger createColumnRearranger(final DataTableSpec inSpec){
        final ColumnRearranger rearranger = new ColumnRearranger(inSpec);

        return rearranger;
    }

}

