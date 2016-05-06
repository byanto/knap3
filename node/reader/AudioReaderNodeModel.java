package org.knime.base.node.audio3.node.reader;

import java.io.File;
import java.io.IOException;

import org.knime.base.node.audio3.data.AudioBuilder;
import org.knime.base.node.audio3.data.cell.AudioCell;
import org.knime.base.node.audio3.data.cell.AudioCellFactory;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelStringArray;

/**
 * This is the model implementation of AudioReader.
 *
 *
 * @author Budi Yanto, KNIME.com
 */
public class AudioReaderNodeModel extends NodeModel {

    private final SettingsModelStringArray m_files = createFileListModel();

    /**
     * Constructor for the node model.
     */
    protected AudioReaderNodeModel() {
        super(0, 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
            final ExecutionContext exec) throws Exception {

        final BufferedDataContainer bdc = exec.createDataContainer(createOutSpec());
        final AudioCellFactory cellFactory = new AudioCellFactory();
        int rowId = 0;
        for(final String file : m_files.getStringArrayValue()){
            final DataCell cell = cellFactory.createCell(
                AudioBuilder.createAudio(file));
            final DataRow row = new DefaultRow(("row" + (rowId++)), cell);
            bdc.addRowToTable(row);
        }
        bdc.close();
        return new BufferedDataTable[]{bdc.getTable()};
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

        // Check if some files are selected
        if(m_files == null || m_files.getStringArrayValue() == null ||
                m_files.getStringArrayValue().length == 0) {
            throw new InvalidSettingsException("No file is selected");
        }

        return new DataTableSpec[]{createOutSpec()};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
         m_files.saveSettingsTo(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        m_files.loadSettingsFrom(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        m_files.validateSettings(settings);
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

    /**
     * @return Model for the settings holding the file list.
     */
    public static SettingsModelStringArray createFileListModel() {
        return new SettingsModelStringArray("fileList", new String[] {});
    }

    private DataTableSpec createOutSpec(){
        final DataColumnSpecCreator creator = new DataColumnSpecCreator("Audio", AudioCell.TYPE);
        final DataColumnSpec[] cspecs = new DataColumnSpec[]{creator.createSpec()};
        return new DataTableSpec(cspecs);
    }

}

