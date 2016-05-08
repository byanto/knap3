package org.knime.base.node.audio3.node.viewer;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import org.knime.base.node.audio3.data.Audio;
import org.knime.core.node.KNIMEConstants;
import org.knime.core.node.NodeView;

/**
 * <code>NodeView</code> for the "AudioViewer" Node.
 *
 *
 * @author Budi Yanto, KNIME.com
 */
public class AudioViewerNodeView extends NodeView<AudioViewerNodeModel> {

    /**
     * Creates a new view.
     *
     * @param nodeModel The model (class: {@link AudioViewerNodeModel})
     */
    protected AudioViewerNodeView(final AudioViewerNodeModel nodeModel) {
        super(nodeModel);
        setComponent(createMainPanel(nodeModel.getAudioList()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void modelChanged() {
        // TODO: generated method stub
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onClose() {
        // TODO: generated method stub
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onOpen() {
        // TODO: generated method stub
    }

    private JPanel createMainPanel(final List<Audio> audioList){
        final JPanel panel = new JPanel();
        final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"No", "Audio Path"}, 0){

            /**
             * Automatically generated serial version UID
             */
            private static final long serialVersionUID = 7003040761215511305L;

            /**
             * {@inheritDoc}
             */
            @Override
            public boolean isCellEditable(final int row, final int column) {
                return false;
            }
        };

        int idx = 1;
        for(final Audio audio : audioList){
            tableModel.addRow(new Object[]{idx++, audio.getFile().getAbsolutePath()});
        }

        final JTable table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setOpaque(false);
        table.setToolTipText("Double click to open audio cell view");
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(750);
        table.addMouseListener(new MouseAdapter() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void mouseClicked(final MouseEvent e) {
                if(e.getClickCount() == 2){
                    final int selectedIdx = table.getSelectedRow();
                    viewAudio(audioList.get(selectedIdx));
                }
                super.mouseClicked(e);
            }
        });

        final JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(800, 600));
        panel.add(scrollPane);
        return panel;
    }

    private void viewAudio(final Audio audio){
        JFrame detailsFrame = new JFrame(audio.getName());
        if (KNIMEConstants.KNIME16X16 != null) {
            detailsFrame.setIconImage(KNIMEConstants.KNIME16X16.getImage());
        }

        final AudioCellView view = new AudioCellView(audio);
        detailsFrame.setContentPane(view);
        detailsFrame.pack();
        detailsFrame.setVisible(true);
    }

}

