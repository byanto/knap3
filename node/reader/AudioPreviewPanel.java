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
 *   Apr 3, 2016 (budiyanto): created
 */
package org.knime.base.node.audio3.node.reader;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import org.knime.base.node.audio3.data.AudioBuilder;
import org.knime.base.node.audio3.util.AudioErrorUtils;
import org.knime.base.node.audio3.util.AudioEventListener;
import org.knime.base.node.audio3.util.AudioPlayer;
import org.knime.base.node.audio3.util.AudioUtils;
import org.knime.core.node.NodeLogger;

/**
 *
 * @author Budi Yanto, KNIME.com
 */
public class AudioPreviewPanel extends JPanel{

    private static final NodeLogger LOGGER = NodeLogger.getLogger(AudioPreviewPanel.class);

    private static final long serialVersionUID = 1L;

    private static final Color LIGHT_BLUE = new Color(128, 192, 255);
    private static final Color DARK_BLUE = new Color(0, 0, 127);

    private final JPanel m_contentPane = new JPanel(new BorderLayout());
    private final JLabel m_fileLabel = new JLabel("No file loaded");
    private final DisplayPanel m_displayPanel = new DisplayPanel();
    private final JToolBar m_playbackTools = new JToolBar();
    private final JButton m_buttonPlay = new JButton("Play");
    private final JButton m_buttonPause = new JButton("Pause");
    private final JButton m_buttonStop = new JButton("Stop");

    private File m_selectedFile = null;

    private AudioPlayer m_player = null;

    /**
     *
     */
    public AudioPreviewPanel(){
        m_playbackTools.setLayout(new FlowLayout());
        m_playbackTools.setFloatable(false);
        m_playbackTools.add(m_buttonPlay);
        m_playbackTools.add(m_buttonPause);
        m_playbackTools.add(m_buttonStop);

        m_buttonPlay.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                onPlay();
            }
        });
        m_buttonPause.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                onPause();
            }
        });
        m_buttonStop.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                onStop();
            }
        });

        m_fileLabel.setOpaque(true);
        m_fileLabel.setBackground(Color.BLACK);
        m_fileLabel.setForeground(Color.WHITE);
        m_fileLabel.setHorizontalAlignment(SwingConstants.CENTER);

        m_playbackTools.setBackground(Color.GRAY);
        m_playbackTools.setMargin(new Insets(0, 24, 0, 0));

        m_contentPane.add(m_fileLabel, BorderLayout.NORTH);
        m_contentPane.add(m_displayPanel, BorderLayout.CENTER);
        m_contentPane.add(m_playbackTools, BorderLayout.SOUTH);

        add(m_contentPane);
    }

    void onSelect(final File file){
        m_selectedFile = file;
        m_fileLabel.setText(file.getName());
    }

    void onPlay() {
        try{
            if(m_player != null && m_player.isPaused()){
                m_player.play();
                return;
            }
            if(m_player != null && m_player.isPlaying()){
                m_player.stop();
                m_player = null;
            }
            m_player = new AudioPlayer(AudioBuilder.createAudio(m_selectedFile));
            m_player.addAudioEventListener(new AudioWaveDrawer());
            m_player.start();
        } catch(UnsupportedAudioFileException | IOException ex){
            AudioErrorUtils.showError(this, ex.getMessage(),
                "Cannot open file: " + m_selectedFile.getName());
        }

    }

    void onPause() {
        if(m_player != null){
            m_player.pause();
        }
    }

    void onStop() {
        if(m_player != null){
            m_player.stop();
        }
    }

    private class AudioWaveDrawer implements AudioEventListener {

        /**
         * {@inheritDoc}
         */
        @Override
        public void beforePlay(final byte[] samples, final AudioFormat audioFormat) {
            final int nrOfChannels = audioFormat.getChannels();
            final int normalizedBytes = AudioUtils.normalizeBytesFromBits(
                audioFormat.getSampleSizeInBits());
            final int samplesLength = samples.length;
            float[] newSamples = new float[samplesLength / normalizedBytes];
            long[] transfer = new long[samplesLength / normalizedBytes];

            newSamples = AudioUtils.unpack(samples, transfer, newSamples,
                samplesLength, audioFormat);
            newSamples = AudioUtils.window(newSamples, samplesLength / normalizedBytes, audioFormat);

            m_displayPanel.makePath(nrOfChannels, newSamples, samplesLength / normalizedBytes);
            m_displayPanel.repaint();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void afterPlay(final byte[] samples, final AudioFormat audioFormat) {

        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void playEnded() {
            m_displayPanel.reset();
            m_displayPanel.repaint();
        }

    }

    /**
    * The code is mainly based on
    * https://github.com/Radiodef/WaveformDemo/blob/master/waveformdemo/WaveformDemo.java
    *
    * Original author: David Staver, 2013
    *
    * This work is licensed under the Creative Commons
    * Attribution-ShareAlike 3.0 Unported License.
    * To view a copy of this license, visit
    * http://creativecommons.org/licenses/by-sa/3.0/
    *
    * */
    private class DisplayPanel extends JPanel {

        private static final long serialVersionUID = 1L;

        private final BufferedImage image;

        private final Path2D.Float[] paths = {new Path2D.Float(), new Path2D.Float(), new Path2D.Float()};

        private final Object pathLock = new Object();

        private DisplayPanel() {
            Dimension pref = getPreferredSize();

            image = (GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
                .getDefaultConfiguration().createCompatibleImage(pref.width, pref.height, Transparency.OPAQUE));
            setOpaque(false);
        }

        private void reset() {
            Graphics2D g2d = image.createGraphics();
            g2d.setBackground(Color.BLACK);
            g2d.clearRect(0, 0, image.getWidth(), image.getHeight());
            g2d.dispose();
        }

        public void makePath(final int nChannels, final float[] samples, final int svalid) {
            /* shuffle */

            Path2D.Float current = paths[2];
            paths[2] = paths[1];
            paths[1] = paths[0];

            /* lots of ratios */

            float avg = 0f;
            float hd2 = getHeight() / 2f;

            /*
             * have to do a special op for the
             * 0th samples because moveTo.
             *
             */

            int i = 0;
            while (i < nChannels && i < svalid) {
                avg += samples[i++];
            }

            avg /= nChannels;

            current.reset();
            current.moveTo(0, hd2 - avg * hd2);

            int fvalid = svalid / nChannels;
            for (int ch, frame = 0; i < svalid; frame++) {
                avg = 0f;

                /* average the channels for each frame. */

                for (ch = 0; ch < nChannels; ch++) {
                    avg += samples[i++];
                }

                avg /= nChannels;

                current.lineTo((float)frame / fvalid * image.getWidth(), hd2 - avg * hd2);
            }

            paths[0] = current;

            Graphics2D g2d = image.createGraphics();

            synchronized (pathLock) {
                g2d.setBackground(Color.BLACK);
                g2d.clearRect(0, 0, image.getWidth(), image.getHeight());

                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

                g2d.setPaint(DARK_BLUE);
                g2d.draw(paths[2]);

                g2d.setPaint(LIGHT_BLUE);
                g2d.draw(paths[1]);

                g2d.setPaint(Color.WHITE);
                g2d.draw(paths[0]);
            }

            g2d.dispose();
        }

        @Override
        protected void paintComponent(final Graphics g) {
            super.paintComponent(g);

            synchronized (pathLock) {
                g.drawImage(image, 0, 0, null);
            }
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(1024 / 2, 128);
        }

        @Override
        public Dimension getMinimumSize() {
            return getPreferredSize();
        }

        @Override
        public Dimension getMaximumSize() {
            return getPreferredSize();
        }
    }

}
