package org.knime.base.node.audio3.node.recognizer.cmusphinx;

import javax.swing.JFileChooser;

import org.knime.base.node.audio3.node.recognizer.RecognizerNodeDialog;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentFileChooser;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * <code>NodeDialog</code> for the "CMUSphinxRecognizer" Node.
 *
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from
 * {@link org.knime.core.node.NodeDialogPane}.
 *
 * @author Budi Yanto, KNIME.com
 */
public class CMUSphinxRecognizerNodeDialog extends RecognizerNodeDialog {

    private static final String PREFIX_HISTORY_ID = CMUSphinxRecognizerNodeDialog.class.getSimpleName();
    private static final String ACOUSTIC_MODEL_HISTORY_ID = PREFIX_HISTORY_ID + "_AcousticModelPath";
    private static final String DICTIONARY_HISTORY_ID = PREFIX_HISTORY_ID + "_DictionaryPath";
    private static final String LANGUAGE_MODEL_HISTORY_ID = PREFIX_HISTORY_ID + "_LanguageModelPath";

    private final SettingsModelString m_acousticModelPathSettingsModel =
            CMUSphinxRecognizerNodeModel.createAcousticModelPathSettingsModel();
    private final SettingsModelString m_dictionaryPathSettingsModel =
            CMUSphinxRecognizerNodeModel.createDictionaryPathSettingsModel();
    private final SettingsModelString m_languageModelPathSettingsModel =
            CMUSphinxRecognizerNodeModel.createLanguageModelPathSettingsModel();

    /**
     * New pane for configuring the CMUSphinxRecognizer node.
     */
    protected CMUSphinxRecognizerNodeDialog() {
        super();

        createNewGroup("Set Recognizer Configuration");
        final DialogComponentFileChooser acousticComp = new DialogComponentFileChooser(
            m_acousticModelPathSettingsModel, ACOUSTIC_MODEL_HISTORY_ID,
            JFileChooser.OPEN_DIALOG, true);
        acousticComp.setBorderTitle("Selected Acoustic Model Directory");

        final DialogComponentFileChooser dictionaryComp = new DialogComponentFileChooser(
            m_dictionaryPathSettingsModel, DICTIONARY_HISTORY_ID,
            JFileChooser.OPEN_DIALOG, "dict");
        dictionaryComp.setBorderTitle("Selected Dictionary File");

        final DialogComponentFileChooser languageComp = new DialogComponentFileChooser(
            m_languageModelPathSettingsModel, LANGUAGE_MODEL_HISTORY_ID,
            JFileChooser.OPEN_DIALOG, "lm");
        languageComp.setBorderTitle("Selected Language Model File");

        addDialogComponent(acousticComp);
        addDialogComponent(dictionaryComp);
        addDialogComponent(languageComp);

        closeCurrentGroup();
    }
}

