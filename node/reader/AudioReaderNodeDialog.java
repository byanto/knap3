package org.knime.base.node.audio3.node.reader;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;

/**
 * <code>NodeDialog</code> for the "AudioReader" Node.
 *
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from
 * {@link org.knime.core.node.NodeDialogPane}.
 *
 * @author Budi Yanto, KNIME.com
 */
public class AudioReaderNodeDialog extends DefaultNodeSettingsPane {

    private static final String CFG_DIR_HISTORY = "audioReaderDirHistory";

    private static final String[] EXTENSIONS = new String[]{
        "aiff", "aifc", "wav", "au", "snd"
    };

    private static final FileFilter FILE_FILTER =
            new FileNameExtensionFilter("Audio Files", EXTENSIONS);

    private final DialogComponentMultiFileChooser m_fileChooser;

    /**
     * New pane for configuring the AudioReader node.
     */
    protected AudioReaderNodeDialog() {
        m_fileChooser = new DialogComponentMultiFileChooser(
            AudioReaderNodeModel.createFileListModel(), FILE_FILTER, CFG_DIR_HISTORY);
        addDialogComponent(m_fileChooser);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onClose() {
        super.onClose();
        m_fileChooser.onClose();
    }

}

