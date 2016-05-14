package org.knime.base.node.audio3.node.recognizer.watson;

import org.knime.base.node.audio3.data.component.AudioColumnSelection;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentBoolean;
import org.knime.core.node.defaultnodesettings.DialogComponentPasswordField;
import org.knime.core.node.defaultnodesettings.DialogComponentString;

/**
 * <code>NodeDialog</code> for the "WatsonSpeechRecognizer" Node.
 *
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from
 * {@link org.knime.core.node.NodeDialogPane}.
 *
 * @author Budi Yanto, KNIME.com
 */
public class WatsonSpeechRecognizerNodeDialog extends DefaultNodeSettingsPane {

    /**
     * New pane for configuring the WatsonSpeechRecognizer node.
     */
    protected WatsonSpeechRecognizerNodeDialog() {
        createNewGroup("Options");
        addDialogComponent(AudioColumnSelection.createDialogComponent());
        addDialogComponent(new DialogComponentBoolean(
            WatsonSpeechRecognizerNodeModel.createAppendTranscriptionModel(),
            "Append transcription"));
        closeCurrentGroup();

        // Create authentication group
        createNewGroup("Authentication");
        addDialogComponent(new DialogComponentString(
            WatsonSpeechRecognizerNodeModel.createUserNameSettingsModel(),
            "User Name: ", false, 40));
        addDialogComponent(new DialogComponentPasswordField(
            WatsonSpeechRecognizerNodeModel.createPasswordSettingsModel(),
            "Password: ", 40));
        closeCurrentGroup();
    }
}

