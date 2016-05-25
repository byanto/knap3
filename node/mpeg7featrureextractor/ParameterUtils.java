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
 *   May 23, 2016 (budiyanto): created
 */
package org.knime.base.node.audio3.node.mpeg7featrureextractor;

import java.awt.Component;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import org.knime.base.node.audio3.data.feature.mpeg7.MPEG7Constants;
import org.knime.base.node.audio3.data.feature.mpeg7.MPEG7FeatureType;

/**
 *
 * @author Budi Yanto, KNIME.com
 */
class ParameterUtils {

    /* Audio Power */
    private final JCheckBox m_apLogScale = new JCheckBox("Log Scale");

    /* Audio Spectrum Envelope */
    private final DefaultComboBoxModel<Float> m_aseLowEdge = new DefaultComboBoxModel<Float>(MPEG7Constants.LOW_EDGE);
    private final DefaultComboBoxModel<Float> m_aseHighEdge = new DefaultComboBoxModel<Float>(MPEG7Constants.HIGH_EDGE);
    private final DefaultComboBoxModel<Float> m_aseResolution = new DefaultComboBoxModel<Float>(MPEG7Constants.RESOLUTION);
    private final DefaultComboBoxModel<Boolean> m_aseDbScale = new DefaultComboBoxModel<Boolean>(MPEG7Constants.DB_SCALE);
    private final DefaultComboBoxModel<String> m_aseNormalize = new DefaultComboBoxModel<String>(MPEG7Constants.NORMALIZE);

    /* Audio Spectrum Flatness */
    private final DefaultComboBoxModel<Float>m_asfLowEdge = new DefaultComboBoxModel<Float>(MPEG7Constants.LOW_EDGE);
    private final DefaultComboBoxModel<Float>m_asfHighEdge = new DefaultComboBoxModel<Float>(MPEG7Constants.HIGH_EDGE);

    /* Audio Fundamental Frequency */
    private final SpinnerModel m_affLowLimit = new SpinnerNumberModel(50, 1, 16000, 1);
    private final SpinnerModel m_affHighLimit = new SpinnerNumberModel(12000, 1, 16000, 1);

    /* Signal Envelope -> used by Temporal Centroid & Log Attack Time */
    private final SpinnerModel m_seWindowLength = new SpinnerNumberModel(10, 1, 100, 1);
    private final SpinnerModel m_seWindowSlide = new SpinnerNumberModel(5, 1, 100, 1);

    /* Log Attack Time -> Signal Envelope + Threshold */
    private final SpinnerModel m_latThreshold = new SpinnerNumberModel(0.02, 0.01, 10, 0.01);

    /* Harmonic Peaks -> used by Harmonic Spectral [Centroid | Deviation | Spread | Variation] */
    private final SpinnerModel m_hpNonHarmonicity = new SpinnerNumberModel(0.15, 0.01, 10, 0.01);
    private final SpinnerModel m_hpThreshold = new SpinnerNumberModel(0.0, 0.0, 10, 0.1);

    /* Audio Spectrum Basis & Projection */
    private final SpinnerModel m_asbpNrOfFrames = new SpinnerNumberModel(0, 0, 100, 1);
    private final SpinnerModel m_asbpNrOfFunctions = new SpinnerNumberModel(8, 0, 100, 1);


    private Map<MPEG7FeatureType, Component> m_components =
            new HashMap<MPEG7FeatureType, Component>();

    ParameterUtils() {
        /* Audio Power */
        m_components.put(MPEG7FeatureType.AUDIO_POWER, m_apLogScale);

        /* Audio Spectrum Envelope */
        final Box aseBox = Box.createVerticalBox();
        aseBox.add(createComponent("Low Edge", new JComboBox<Float>(m_aseLowEdge)));
        m_aseLowEdge.setSelectedItem(MPEG7Constants.LOW_EDGE[1]);
        aseBox.add(createComponent("High Edge", new JComboBox<Float>(m_aseHighEdge)));
        m_aseHighEdge.setSelectedItem(MPEG7Constants.HIGH_EDGE[3]);
        aseBox.add(createComponent("Resolution", new JComboBox<Float>(m_aseResolution)));
        m_aseResolution.setSelectedItem(MPEG7Constants.RESOLUTION[2]);
        aseBox.add(createComponent("dbScale", new JComboBox<Boolean>(m_aseDbScale)));
        m_aseDbScale.setSelectedItem(MPEG7Constants.DB_SCALE[0]);
        aseBox.add(createComponent("Normalize", new JComboBox<String>(m_aseNormalize)));
        m_aseNormalize.setSelectedItem(MPEG7Constants.NORMALIZE[0]);
        m_components.put(MPEG7FeatureType.AUDIO_SPECTRUM_ENVELOPE, aseBox);

        /* Audio Spectrum Flatness */
        final Box asfBox = Box.createVerticalBox();
        asfBox.add(createComponent("Low Edge", new JComboBox<Float>(m_asfLowEdge)));
        m_asfLowEdge.setSelectedItem(MPEG7Constants.LOW_EDGE[3]);
        asfBox.add(createComponent("High Edge", new JComboBox<Float>(m_asfHighEdge)));
        m_asfHighEdge.setSelectedItem(MPEG7Constants.HIGH_EDGE[3]);
        m_components.put(MPEG7FeatureType.AUDIO_SPECTRUM_FLATNESS, asfBox);

        /* Audio Fundamental Frequency */
        final Box affBox = Box.createVerticalBox();
        affBox.add(createComponent("Low Limit", new JSpinner(m_affLowLimit)));
        affBox.add(createComponent("High Limit", new JSpinner(m_affHighLimit)));
        m_components.put(MPEG7FeatureType.AUDIO_FUNDAMENTAL_FREQUENCY, affBox);

        /* Temporal Centroid */
        final Box tcBox = Box.createVerticalBox();
        tcBox.add(createComponent("Window Length", new JSpinner(m_seWindowLength)));
        tcBox.add(createComponent("Window Slide", new JSpinner(m_seWindowSlide)));
        m_components.put(MPEG7FeatureType.TEMPORAL_CENTROID, tcBox);

        /* Log Attack Time */
        final Box latBox = Box.createVerticalBox();
        latBox.add(createComponent("Window Length", new JSpinner(m_seWindowLength)));
        latBox.add(createComponent("Window Slide", new JSpinner(m_seWindowSlide)));
        latBox.add(createComponent("Threshold", new JSpinner(m_latThreshold)));
        m_components.put(MPEG7FeatureType.LOG_ATTACK_TIME, latBox);

        /* Harmonic Peaks -> used by Harmonic Spectral [Centroid | Deviation | Spread | Variation] */
        final Box harmonicPeaksBox = Box.createVerticalBox();
        harmonicPeaksBox.add(createComponent("Non-Harmonicity", new JSpinner(m_hpNonHarmonicity)));
        harmonicPeaksBox.add(createComponent("Threshold", new JSpinner(m_hpThreshold)));
        m_components.put(MPEG7FeatureType.HARMONIC_SPECTRAL_CENTROID, harmonicPeaksBox);
        m_components.put(MPEG7FeatureType.HARMONIC_SPECTRAL_DEVIATION, harmonicPeaksBox);
        m_components.put(MPEG7FeatureType.HARMONIC_SPECTRAL_SPREAD, harmonicPeaksBox);
        m_components.put(MPEG7FeatureType.HARMONIC_SPECTRAL_VARIATION, harmonicPeaksBox);

        /* Audio Spectrum Basis & Projection */
        final Box asbpBox = Box.createVerticalBox();
        asbpBox.add(createComponent("Low Edge", new JComboBox<Float>(m_aseLowEdge)));
        asbpBox.add(createComponent("High Edge", new JComboBox<Float>(m_aseHighEdge)));
        asbpBox.add(createComponent("Resolution", new JComboBox<Float>(m_aseResolution)));
        asbpBox.add(createComponent("dbScale", new JComboBox<Boolean>(m_aseDbScale)));
        asbpBox.add(createComponent("Normalize", new JComboBox<String>(m_aseNormalize)));
        asbpBox.add(createComponent("Number of Frames", new JSpinner(m_asbpNrOfFrames)));
        asbpBox.add(createComponent("Number of Functions", new JSpinner(m_asbpNrOfFunctions)));
        m_components.put(MPEG7FeatureType.AUDIO_SPECTRUM_BASIS, asbpBox);
        m_components.put(MPEG7FeatureType.AUDIO_SPECTRUM_PROJECTION, asbpBox);
    }

    private static JPanel createComponent(final String text, final Component comp){
        final JPanel panel = new JPanel();
        final JLabel label = new JLabel(text + ":");
        label.setPreferredSize(new Dimension(160, label.getPreferredSize().height));
        comp.setPreferredSize(new Dimension(150, comp.getPreferredSize().height));
        panel.add(label);
        panel.add(comp);
        return panel;
    }

    Component getComponent(final MPEG7FeatureType type){
       return m_components.get(type);
    }

}
