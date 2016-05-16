package org.knime.base.node.audio3.node.dataextractor;

import org.knime.base.node.audio3.data.component.AudioColumnSelection;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentStringListSelection;

/**
 * <code>NodeDialog</code> for the "DataExtractor" Node.
 *
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from
 * {@link org.knime.core.node.NodeDialogPane}.
 *
 * @author Budi Yanto, KNIME.com
 */
public class DataExtractorNodeDialog extends DefaultNodeSettingsPane {

    /**
     * New pane for configuring the DataExtractor node.
     */
    protected DataExtractorNodeDialog() {
        addDialogComponent(AudioColumnSelection.createDialogComponent());
        final DialogComponentStringListSelection comp =
                new DialogComponentStringListSelection(
                    DataExtractorNodeModel.createDataToExtractModel(),
                    "Data to extract: ",
                    DataExtractor.getExtractorNames());
        comp.setSizeComponents(100, 100);
        comp.setVisibleRowCount(5);
        addDialogComponent(comp);
    }
}

