/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME GmbH, Konstanz, Germany
 *  Website: http://www.knime.org; Email: contact@knime.org
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ---------------------------------------------------------------------
 *
 * History
 *   May 6, 2016 (budiyanto): created
 */
package org.knime.base.node.audio3.node.viewer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.knime.base.node.audio3.data.Audio;
import org.knime.base.node.audio3.util.AudioUtils;
import org.knime.core.node.NodeLogger;

/**
 *
 * @author Budi Yanto, KNIME.com
 */
class AudioCellView extends JPanel{

    /**
     * Automatically generated Serial Version UID
     */
    private static final long serialVersionUID = -4234704116274105268L;

    private static final NodeLogger LOGGER = NodeLogger.getLogger(AudioCellView.class);

    private Audio m_audio;

    AudioCellView(final Audio audio){
        m_audio = audio;
        setLayout(new BorderLayout());
        add(createViewerPanel(), BorderLayout.CENTER);
    }

    private JSplitPane createViewerPanel(){
        final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setPreferredSize(new Dimension(1000, 500));
        final JScrollPane waveScrollPane = new JScrollPane(createAudioWavePanel());
        waveScrollPane.setMinimumSize(new Dimension(700, 600));

        final JScrollPane infoScrollPane = new JScrollPane(createAudioInfoPanel());
        infoScrollPane.setMinimumSize(new Dimension(200, 300));

        splitPane.setLeftComponent(waveScrollPane);
        splitPane.setRightComponent(infoScrollPane);
        splitPane.setDividerLocation(0.8);
        return splitPane;
    }

    private JPanel createAudioWavePanel(){
        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Audio Wave"));

        try {
            final double[][] samples = AudioUtils.getSamples(m_audio);
            for(int channel = 0; channel < samples.length; channel++){
                final XYSeriesCollection dataset = new XYSeriesCollection();
                final XYSeries series = new XYSeries("Audio Wave");
                for(int i = 0; i < samples[channel].length; i++){
                    series.add(i, samples[channel][i]);
                }
                dataset.addSeries(series);
                JFreeChart chart = ChartFactory.createXYLineChart(
                    "Channel " + (channel + 1), "Sample", "Value", dataset);
                chart.removeLegend();
                final JPanel chartPanel = new ChartPanel(chart);
                panel.add(chartPanel);
            }
        } catch (UnsupportedAudioFileException | IOException ex) {
            panel.add(new JLabel("Error generating audio wave panel for: "
                    + m_audio.getName()));
            LOGGER.error(ex.getMessage());
        }

        return panel;
    }

    private JPanel createAudioInfoPanel(){
        final JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setPreferredSize(new Dimension(200, 300));
        panel.setBorder(BorderFactory.createTitledBorder("Audio Information"));

        final DefaultTableModel model = new DefaultTableModel(0, 2){

            private static final long serialVersionUID = 1L;

            /**
             * {@inheritDoc}
             */
            @Override
            public boolean isCellEditable(final int row, final int column) {
                return false;
            }
        };

        model.addRow(new Object[]{"Name", m_audio.getName()});
        model.addRow(new Object[]{"Path", m_audio.getFile().getAbsolutePath()});
        model.addRow(new Object[]{"Length in Bytes", m_audio.getAudioFileFormat().getByteLength()});
        model.addRow(new Object[]{"Length in Frames", m_audio.getAudioFileFormat().getFrameLength()});
        model.addRow(new Object[]{"Type", m_audio.getAudioFileFormat().getType()});

        final AudioFormat format = m_audio.getAudioFileFormat().getFormat();
        model.addRow(new Object[]{"Encoding", format.getEncoding()});
        model.addRow(new Object[]{"Sample Rate in Hz", format.getSampleRate()});
        model.addRow(new Object[]{"Sample Size in Bits", format.getSampleSizeInBits()});
        model.addRow(new Object[]{"Channels", format.getChannels()});
        model.addRow(new Object[]{"Frame Size", format.getFrameSize()});
        model.addRow(new Object[]{"Frame Rate", format.getFrameRate()});
        model.addRow(new Object[]{"Big Endian", format.isBigEndian()});

        final JTable table = new JTable(model);
        table.setFillsViewportHeight(true);
        table.setPreferredScrollableViewportSize(table.getPreferredSize());
        table.setTableHeader(null);
        table.getColumnModel().getColumn(0).setMinWidth(120);
        table.getColumnModel().getColumn(0).setMaxWidth(120);

        panel.add(new JScrollPane(table));

        return panel;
    }

}
