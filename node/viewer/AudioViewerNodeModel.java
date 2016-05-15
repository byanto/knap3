package org.knime.base.node.audio3.node.viewer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.UnsupportedAudioFileException;

import org.knime.base.node.audio3.data.Audio;
import org.knime.base.node.audio3.data.AudioBuilder;
import org.knime.base.node.audio3.data.io.BufferedDataInputStream;
import org.knime.base.node.audio3.data.io.BufferedDataOutputStream;
import org.knime.base.node.audio3.util.DataStructureUtils;
import org.knime.base.node.audio3.util.KNAPConstants;
import org.knime.core.data.DataTableSpec;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * This is the model implementation of AudioViewer.
 *
 *
 * @author Budi Yanto, KNIME.com
 */
public class AudioViewerNodeModel extends NodeModel {

    private static final String CFG_AUDIO_COLUMN = "AudioColumn";
//    private static final String INTERNAL_MODEL = "AudioViewerModel";
    private static final String SETTINGS_FILE = "AudioViewerNodeModelSettings.dat";

    private SettingsModelString m_audioColumn = createAudioColumnSettingsModel();
    private List<Audio> m_audioList;

    /**
     * Constructor for the node model.
     */
    protected AudioViewerNodeModel() {
        super(1, 0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
            final ExecutionContext exec) throws Exception {

        final int audioColIdx = inData[0].getDataTableSpec().findColumnIndex(
            m_audioColumn.getStringValue());
        m_audioList = DataStructureUtils.createAudioList(inData[0], audioColIdx, exec);

        return new BufferedDataTable[]{};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void reset() {
        if(m_audioList != null){
            m_audioList.clear();
            m_audioList = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DataTableSpec[] configure(final DataTableSpec[] inSpecs)
            throws InvalidSettingsException {

        // Check whether the selected audio column really exists in the DataTableSpec
        final String audioColumnName = m_audioColumn.getStringValue();
        final int audioColumnIdx = inSpecs[0].findColumnIndex(audioColumnName);
        if(audioColumnIdx < 0){
            throw new InvalidSettingsException("Cannot find the audio column \""
                    + audioColumnName + "\" in the input data table spec.");
        }

        return new DataTableSpec[]{};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
         m_audioColumn.saveSettingsTo(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        m_audioColumn.loadSettingsFrom(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        m_audioColumn.validateSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
        final File file = new File(internDir, SETTINGS_FILE);
        final BufferedDataInputStream input = new BufferedDataInputStream(
            new FileInputStream(file));
        System.out.println(file.getAbsolutePath());
        final int size = input.readInt();
        m_audioList = new ArrayList<Audio>(size);
        for(int i = 0; i < size; i++){
            exec.checkCanceled();
            try{
                m_audioList.add(AudioBuilder.loadInternals(input));
            } catch(UnsupportedAudioFileException ex) {
                IOException ioe = new IOException("Could not load internals!");
                ioe.initCause(ex);
                input.close();
                throw ioe;
            }
        }

        input.close();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {

        final File file = new File(internDir, SETTINGS_FILE);
        final BufferedDataOutputStream output = new BufferedDataOutputStream(
            new FileOutputStream(file));
        final int size = m_audioList.size();
        output.writeInt(size);
        for(final Audio audio : m_audioList){
            exec.checkCanceled();
            AudioBuilder.saveInternals(audio, output);
        }

        output.close();
    }

    static SettingsModelString createAudioColumnSettingsModel(){
        return new SettingsModelString(CFG_AUDIO_COLUMN,
            KNAPConstants.AUDIO_COL_NAME);
    }

    /**
     * @return the list of audio to display
     */
    List<Audio> getAudioList(){
        return m_audioList;
    }

}

