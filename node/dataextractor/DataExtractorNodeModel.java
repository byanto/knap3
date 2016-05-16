package org.knime.base.node.audio3.node.dataextractor;

import java.io.File;
import java.io.IOException;

import org.knime.base.node.audio3.data.component.AudioColumnSelection;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.container.ColumnRearranger;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelStringArray;
import org.knime.core.util.UniqueNameGenerator;

/**
 * This is the model implementation of DataExtractor.
 *
 *
 * @author Budi Yanto, KNIME.com
 */
public class DataExtractorNodeModel extends NodeModel {

    private final AudioColumnSelection m_audioColumnSelection = new AudioColumnSelection();
    private final SettingsModelStringArray m_dataToExtract = createDataToExtractModel();

    static SettingsModelStringArray createDataToExtractModel(){
        return new SettingsModelStringArray("DataToExtract", null);
    }


    /**
     * Constructor for the node model.
     */
    protected DataExtractorNodeModel() {
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
        final BufferedDataTable inTable = inData[0];
        final BufferedDataTable resultTable;
        final DataExtractor[] extractors = DataExtractor.getExctractor(
            m_dataToExtract.getStringArrayValue());
        if (extractors == null || extractors.length == 0) {
            setWarningMessage(
                    "No data is selected. Node returns unaltered table");
            resultTable = inTable;
        } else {
            final int colIdx = m_audioColumnSelection.getSelectedColumnIndex();
            final DataTableSpec inSpec = inTable.getDataTableSpec();
            final ColumnRearranger rearranger = new ColumnRearranger(inSpec);
            rearranger.append(new DataExtractorCellFactory(colIdx,
                createColumnSpecs(inSpec), extractors));
            resultTable = exec.createColumnRearrangeTable(inTable, rearranger, exec);
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

        m_audioColumnSelection.configure(inSpecs[0]);

        if (m_dataToExtract.getStringArrayValue() == null ||
                m_dataToExtract.getStringArrayValue().length < 1) {
            setWarningMessage("No data is selected");
        }

        final DataTableSpec inSpec = inSpecs[0];
        final DataTableSpec resultSpec;
        final DataColumnSpec[] colSpecs = createColumnSpecs(inSpec);
        if(colSpecs == null || colSpecs.length == 0){
            resultSpec = inSpec;
        }else{
            resultSpec = new DataTableSpec(inSpec, new DataTableSpec(colSpecs));
        }

        return new DataTableSpec[]{resultSpec};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
         m_audioColumnSelection.saveSettingsTo(settings);
         m_dataToExtract.saveSettingsTo(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        m_audioColumnSelection.loadSettingsFrom(settings);
        m_dataToExtract.loadSettingsFrom(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        m_audioColumnSelection.validateSettings(settings);
        m_dataToExtract.validateSettings(settings);
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

    private DataColumnSpec[] createColumnSpecs(final DataTableSpec inSpec){
        final DataExtractor[] extractors = DataExtractor.getExctractor(
            m_dataToExtract.getStringArrayValue());
        if(extractors == null || extractors.length < 1){
            return new DataColumnSpec[0];
        }

        final DataColumnSpec[] colSpecs = new DataColumnSpec[extractors.length];
        final UniqueNameGenerator generator = new UniqueNameGenerator(inSpec);
        for(int i = 0; i < colSpecs.length; i++){
            colSpecs[i] = generator.newColumn(extractors[i].getName(),
                extractors[i].getType());
        }
        return colSpecs;
    }

}

