package org.knime.base.node.audio3.node.mpeg7featrureextractor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.knime.base.node.audio3.data.component.AudioColumnSelection;
import org.knime.base.node.audio3.data.feature.mpeg7.MPEG7Constants;
import org.knime.base.node.audio3.data.feature.mpeg7.MPEG7FeatureType;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentButtonGroup;
import org.knime.core.node.defaultnodesettings.DialogComponentColumnNameSelection;
import org.knime.core.node.defaultnodesettings.DialogComponentStringSelection;

/**
 * <code>NodeDialog</code> for the "MPEG7FeatureExtractor" Node.
 *
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from
 * {@link org.knime.core.node.NodeDialogPane}.
 *
 * @author Budi Yanto, KNIME.com
 */
public class MPEG7FeatureExtractorNodeDialog extends DefaultNodeSettingsPane {

    private JTable m_featuresTable;

    private JEditorPane m_descriptionPane;

    private JPanel m_parameterPanel;

    private JLabel m_parameterLabel;

    private final ParameterUtils m_utils = new ParameterUtils();

//    private final FeatureExtractorSettings m_settings = new FeatureExtractorSettings();

    /**
     * New pane for configuring the MPEG7FeatureExtractor node.
     */
    protected MPEG7FeatureExtractorNodeDialog() {
        createPreProcessingTab();
        addTab("Features", createFeaturesTab());
    }

    private void createPreProcessingTab(){
        setDefaultTabTitle("Pre-Processing");
        createNewGroup("Audio Column");
        final DialogComponentColumnNameSelection columnNameSelectionComp =
                AudioColumnSelection.createDialogComponent();
        addDialogComponent(columnNameSelectionComp);
        closeCurrentGroup();
        createNewGroup("Global Options");

        final DialogComponentStringSelection hopSizeComp = new DialogComponentStringSelection(
            MPEG7FeatureExtractorNodeModel.createHopSizeModel(),
            "Hop size [ms]: ", MPEG7Constants.HOP_SIZE);
        hopSizeComp.setSizeComponents(200, hopSizeComp.getComponentPanel()
            .getComponent(1).getPreferredSize().height);
        addDialogComponent(hopSizeComp);

        addDialogComponent(new DialogComponentButtonGroup(
            MPEG7FeatureExtractorNodeModel.createAggregatorSettingsModel(),
            false, "Aggregator Method",
            new String[]{MPEG7FeatureExtractorNodeModel.MEAN,
                MPEG7FeatureExtractorNodeModel.STD_DEVIATION}));

        closeCurrentGroup();
    }

    private JPanel createFeaturesTab(){
        final JPanel panel = new JPanel(new BorderLayout());
        /* Content Panel to hold the features selection and description */
        final JPanel contentPanel = new JPanel(new GridLayout(1, 1));

        /* Create left panel for the selection of the available features */
        final JPanel leftPanel = new JPanel(new GridLayout(1, 1));
        m_featuresTable = new JTable(new FeaturesTableModel());
        initFeaturesTable();

        // Create list of available features
        final JScrollPane featuresScrollPane = new JScrollPane(m_featuresTable);
        featuresScrollPane.setMinimumSize(new Dimension(200, 155));
        featuresScrollPane.setBorder(BorderFactory.createTitledBorder("Select Features"));
        leftPanel.add(featuresScrollPane);

        /* Create right panel for the description and parameters of the selected feature */
        final JPanel rightPanel = new JPanel(new GridLayout(1, 1));
        final Box rightBox = Box.createVerticalBox();
        rightPanel.add(rightBox);
        m_descriptionPane = new JEditorPane("text/html", "No feature is selected.");
        m_descriptionPane.setEditable(false);

        // Create panel for features description
        final JScrollPane descriptionScrollPane = new JScrollPane(m_descriptionPane);
        descriptionScrollPane.setPreferredSize(new Dimension(450, 250));
        descriptionScrollPane.setBorder(BorderFactory.createTitledBorder("Feature Description"));
        rightBox.add(descriptionScrollPane);

        // Create panel for editing parameters
        m_parameterPanel = new JPanel();
        m_parameterPanel.setBorder(BorderFactory.createTitledBorder("Feature Parameters"));
        m_parameterPanel.setPreferredSize(new Dimension(450, 250));
        m_parameterLabel = new JLabel("No feature is selected");
        m_parameterPanel.add(m_parameterLabel, 0);
        rightBox.add(m_parameterPanel);

        /* Add to DialogPane */
        final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            leftPanel, rightPanel);
        contentPanel.add(splitPane);

        panel.add(splitPane, BorderLayout.CENTER);

        return panel;
    }

    private void initFeaturesTable() {
        m_featuresTable.setFillsViewportHeight(true);
        m_featuresTable.setPreferredScrollableViewportSize(
            m_featuresTable.getPreferredSize());
        m_featuresTable.setShowGrid(false);
        m_featuresTable.setTableHeader(null);
        m_featuresTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        m_featuresTable.getColumnModel().getColumn(0).setMinWidth(16);
        m_featuresTable.getColumnModel().getColumn(0).setMaxWidth(16);
        m_featuresTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(final ListSelectionEvent evt) {
                if(!evt.getValueIsAdjusting()){
                    FeaturesTableModel model = (FeaturesTableModel)m_featuresTable.getModel();
                    int row = m_featuresTable.getSelectedRow();
                    MPEG7FeatureType type = model.getFeatureType(row);
                    updateDescriptionPane(type);
                    updateParametersPanel(type);
                }
            }
        });
    }

    private void updateDescriptionPane(final MPEG7FeatureType type) {
        StringBuilder builder = new StringBuilder();
        builder.append("<h3>" + type.getName() + "</h3>");
        builder.append(type.getDescription());
//        builder.append("<h4>Dependencies</h4>");
//        if (type.hasDependencies()) {
//            builder.append("<ul>");
//            for (FeatureType dep : type.getDependencies()) {
//                builder.append("<li>" + dep.getName() + "</li>");
//            }
//        } else {
//            builder.append(type.getName() + " has no dependencies.");
//        }
//        builder.append("</ul>");
        m_descriptionPane.setText(builder.toString());
    }

    private void updateParametersPanel(final MPEG7FeatureType type) {
        if((!type.hasParameters()) || (type.getParameters().length < 1)){
            if(!(m_parameterPanel.getComponent(0) instanceof JLabel)){
                m_parameterPanel.remove(0);
                m_parameterPanel.add(m_parameterLabel, 0);
                m_parameterPanel.validate();
                m_parameterPanel.repaint();
            }
            m_parameterLabel.setText("This feature doesn't have any parameter to set.");

        } else {
            m_parameterPanel.remove(0);
            m_parameterPanel.add(m_utils.getComponent(type));
            m_parameterPanel.validate();
            m_parameterPanel.repaint();
        }
    }

    private class FeaturesTableModel extends DefaultTableModel {

        private static final long serialVersionUID = 1L;

        private static final int SELECTED_IDX = 0;

        private static final int TYPE_IDX = 1;

        private final Class<?>[] m_colClasses = new Class<?>[]{Boolean.class, String.class};

        private FeaturesTableModel() {
            super(0, 2);
            for(MPEG7FeatureType type : MPEG7FeatureType.values()){
                addRow(new Object[]{false, type});
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Class<?> getColumnClass(final int columnIndex) {
            return m_colClasses[columnIndex];
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isCellEditable(final int row, final int column) {
            if (column == SELECTED_IDX) {
                return true;
            }
            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getColumnName(final int column) {
            return "Undefined";
        }

        private MPEG7FeatureType getFeatureType(final int row) {
            return (MPEG7FeatureType)getValueAt(row, TYPE_IDX);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setValueAt(final Object aValue, final int row, final int column) {
            super.setValueAt(aValue, row, column);
            if (column == SELECTED_IDX) {
//                FeatureType type = getFeatureType(row);
//                m_settings.setSelected(type, ((Boolean)aValue).booleanValue());
            }
        }
    }
}

