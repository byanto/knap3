package org.knime.base.node.audio3.node.recognizer.bing;

import org.knime.base.node.audio3.node.recognizer.RecognizerNodeDialog;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentNumber;
import org.knime.core.node.defaultnodesettings.DialogComponentPasswordField;
import org.knime.core.node.defaultnodesettings.DialogComponentStringSelection;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;

/**
 * <code>NodeDialog</code> for the "BingSpeechRecognizer" Node.
 *
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from
 * {@link org.knime.core.node.NodeDialogPane}.
 *
 * @author Budi Yanto, KNIME.com
 */
public class BingSpeechRecognizerNodeDialog extends RecognizerNodeDialog {

    private SettingsModelIntegerBounded m_maxNBestModel =
            BingSpeechRecognizerNodeModel.createMaxNBestSettingsModel();
    private SettingsModelIntegerBounded m_profanityMarkupModel =
            BingSpeechRecognizerNodeModel.createProfanityMarkupSettingsModel();

    /**
     * New pane for configuring the BingSpeechRecognizer node.
     */
    protected BingSpeechRecognizerNodeDialog() {
        super();
        // Create authentication group
        createNewGroup("Authentication");
        addDialogComponent(new DialogComponentPasswordField(
            BingSpeechRecognizerNodeModel.createSubscriptionKeySettingsModel(),
            "Subscription Key: ", 40));
        closeCurrentGroup();

        // Create recognition group
        createNewGroup("Recognition Parameters");
        addDialogComponent(new DialogComponentStringSelection(
            BingSpeechRecognizerNodeModel.createAudioLanguageSettingsModel(),
            "Audio Language: ",
            BingSpeechRecognizer.getSupportedLanguages()));
        addDialogComponent(new DialogComponentStringSelection(
            BingSpeechRecognizerNodeModel.createScenarioSettingsModel(),
            "Scenario: ",
            BingSpeechRecognizer.getSupportedScenarios()));
        addDialogComponent(new DialogComponentNumber(
            m_maxNBestModel,
            "MaxNBest: ", 1));
        addDialogComponent(new DialogComponentNumber(
            m_profanityMarkupModel,
            "Profanity Markup: ", 1));
        closeCurrentGroup();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveAdditionalSettingsTo(final NodeSettingsWO settings)
            throws InvalidSettingsException {
        if(m_maxNBestModel.getIntValue() < 1 || m_maxNBestModel.getIntValue() > 5){
            m_maxNBestModel.setIntValue(BingSpeechRecognizer.DEFAULT_MAXNBEST);
            m_maxNBestModel.saveSettingsTo(settings);
        }

        if(m_profanityMarkupModel.getIntValue() < 0 ||
                m_profanityMarkupModel.getIntValue() > 1){
            m_profanityMarkupModel.setIntValue(
                BingSpeechRecognizer.DEFAULT_PROFANITY_MARKUP);
            m_profanityMarkupModel.saveSettingsTo(settings);
        }

        super.saveAdditionalSettingsTo(settings);
    }
}

