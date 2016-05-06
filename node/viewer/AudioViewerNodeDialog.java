package org.knime.base.node.audio3.node.viewer;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;

/**
 * <code>NodeDialog</code> for the "AudioViewer" Node.
 * 
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author Budi Yanto, KNIME.com
 */
public class AudioViewerNodeDialog extends DefaultNodeSettingsPane {

    /**
     * New pane for configuring the AudioViewer node.
     */
    protected AudioViewerNodeDialog() {

    }
}

