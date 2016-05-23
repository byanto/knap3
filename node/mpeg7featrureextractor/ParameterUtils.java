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

    private Map<MPEG7FeatureType, Component> m_components =
            new HashMap<MPEG7FeatureType, Component>();

    ParameterUtils(){
        /* Audio Spectrum Envelope */
        final Box aseBox = Box.createVerticalBox();
        aseBox.add(createComboBoxWithLabel("Low Edge", MPEG7Constants.LOW_EDGE), 0);
        aseBox.add(createComboBoxWithLabel("High Edge", MPEG7Constants.HIGH_EDGE), 1);
        aseBox.add(createComboBoxWithLabel("Resolution", MPEG7Constants.RESOLUTION), 2);
        aseBox.add(createComboBoxWithLabel("dbScale", MPEG7Constants.DB_SCALE), 3);
        aseBox.add(createComboBoxWithLabel("Normalize", MPEG7Constants.NORMALIZE), 4);
        m_components.put(MPEG7FeatureType.AUDIO_SPECTRUM_ENVELOPE, aseBox);

        /* Audio Spectrum Flatness */
        final Box asfBox = Box.createVerticalBox();
        asfBox.add(createComboBoxWithLabel("Low Edge", MPEG7Constants.LOW_EDGE), 0);
        asfBox.add(createComboBoxWithLabel("High Edge", MPEG7Constants.HIGH_EDGE), 1);
        m_components.put(MPEG7FeatureType.AUDIO_SPECTRUM_FLATNESS, asfBox);

        /* Audio Fundamental Frequency */
        final Box affBox = Box.createVerticalBox();
        affBox.add(createIntSpinnerWithLabel("Low Limit", 50, 1, 16000, 1));
        affBox.add(createIntSpinnerWithLabel("High Limit", 12000, 1, 16000, 1));
        m_components.put(MPEG7FeatureType.AUDIO_FUNDAMENTAL_FREQUENCY, affBox);

        /* Signal Envelope -> used by Temporal Centroid & Log Attack Time */
        final Box signalEnvelopeBox = Box.createVerticalBox();
        signalEnvelopeBox.add(createIntSpinnerWithLabel("Window Length", 10, 1, 100, 1));
        signalEnvelopeBox.add(createIntSpinnerWithLabel("Window Slide", 5, 1, 100, 1));
        m_components.put(MPEG7FeatureType.TEMPORAL_CENTROID, signalEnvelopeBox);

        final JPanel latPanel = createDoubleSpinnerWithLabel("Threshold", 0.02, 0.01, 10, 0.01);
        m_components.put(MPEG7FeatureType.LOG_ATTACK_TIME, latPanel);

        /* Harmonic Peaks -> used by Harmonic Spectral [Centroid | Deviation | Spread | Variation] */
        final Box harmonicPeaksBox = Box.createVerticalBox();
        harmonicPeaksBox.add(createDoubleSpinnerWithLabel("Non-Harmonicity", 0.15, 0.01, 10, 0.01));
        harmonicPeaksBox.add(createDoubleSpinnerWithLabel("Threshold", 0.0, 0.0, 10, 0.1));
        m_components.put(MPEG7FeatureType.HARMONIC_SPECTRAL_CENTROID, harmonicPeaksBox);
        m_components.put(MPEG7FeatureType.HARMONIC_SPECTRAL_DEVIATION, harmonicPeaksBox);
        m_components.put(MPEG7FeatureType.HARMONIC_SPECTRAL_SPREAD, harmonicPeaksBox);
        m_components.put(MPEG7FeatureType.HARMONIC_SPECTRAL_VARIATION, harmonicPeaksBox);

        /* Audio Spectrum Basis & Projection */
        final Box asbpBox = Box.createVerticalBox();
        asbpBox.add(createIntSpinnerWithLabel("Number of Frames", 0, 0, 100, 1));
        asbpBox.add(createIntSpinnerWithLabel("Number of Functions", 8, 0, 100, 1));
        m_components.put(MPEG7FeatureType.AUDIO_SPECTRUM_BASIS, asbpBox);
    }

    private static JPanel createComboBoxWithLabel(final String text, final Object[] list){
        final JComboBox<Object> cmbBox = new JComboBox<Object>(list);
        return createComponent(text, cmbBox);
    }

    private static JPanel createIntSpinnerWithLabel(final String text,
            final int initialValue, final int min, final int max, final int step){
        final SpinnerModel model = new SpinnerNumberModel(initialValue, min, max, step);
        return createComponent(text, new JSpinner(model));
    }

    private static JPanel createDoubleSpinnerWithLabel(final String text,
            final double initialValue, final double min, final double max,
            final double step){
        final SpinnerModel model = new SpinnerNumberModel(initialValue, min, max, step);
        return createComponent(text, new JSpinner(model));
    }

    private static JPanel createComponent(final String text, final Component comp){
        final JPanel panel = new JPanel();
        final JLabel label = new JLabel(text + ":");
        label.setPreferredSize(new Dimension(150, label.getPreferredSize().height));
        comp.setPreferredSize(new Dimension(150, comp.getPreferredSize().height));
        panel.add(label);
        panel.add(comp);
        return panel;
    }

    Component getComponent(final MPEG7FeatureType type){
        switch (type) {
            case LOG_ATTACK_TIME:
                final Box latBox = Box.createVerticalBox();
                latBox.add(m_components.get(MPEG7FeatureType.TEMPORAL_CENTROID));
                latBox.add(m_components.get(MPEG7FeatureType.LOG_ATTACK_TIME));
                return latBox;
            case AUDIO_SPECTRUM_BASIS:
                return createAudioSpectrumBasisProjectionBox();
            case AUDIO_SPECTRUM_PROJECTION:
                return createAudioSpectrumBasisProjectionBox();
            default:
                break;
        }
        return m_components.get(type);
    }

    private Box createAudioSpectrumBasisProjectionBox(){
        final Box box = Box.createVerticalBox();
        box.add(m_components.get(MPEG7FeatureType.AUDIO_SPECTRUM_ENVELOPE));
        box.add(m_components.get(MPEG7FeatureType.AUDIO_SPECTRUM_BASIS));
        return box;
    }

}
