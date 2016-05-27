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
 *   May 27, 2016 (budiyanto): created
 */
package org.knime.base.node.audio3.node.mpeg7featrureextractor;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

import de.crysandt.audio.mpeg7audio.Config;
import de.crysandt.audio.mpeg7audio.ConfigDefault;

/**
 *
 * @author Budi Yanto, KNIME.com
 */
class MPEG7FeatureExtractorSettings {
    static final String CFG_RESIZER = "Resizer";

    static final String CFG_AUDIO_WAVEFORM = "AudioWaveform";
    static final String CFG_AUDIO_POWER = "AudioPower";

    static final String CFG_AUDIO_SPECTRUM_ENVELOPE = "AudioSpectrumEnvelope";
    static final String CFG_AUDIO_SPECTRUM_CENTROID = "AudioSpectrumCentroidSpread";
    static final String CFG_AUDIO_SPECTRUM_SPREAD = "AudioSpectrumCentroidSpread";
    static final String CFG_AUDIO_SPECTRUM_FLATNESS = "AudioSpectrumFlatness";

    static final String CFG_AUDIO_HARMONICITY = "AudioHarmonicity";
    static final String CFG_AUDIO_FUNDAMENTAL_FREQUENCY = "AudioFundamentalFrequency";

    static final String CFG_SIGNAL_ENVELOPE = "SignalEnvelope";
    static final String CFG_LOG_ATTACK_TIME = "LogAttackTime";
    static final String CFG_TEMPORAL_CENTROID = "TemporalCentroid";

    static final String CFG_HARMONIC_PEAKS = "HarmonicPeaks";
    static final String CFG_HARMONIC_SPECTRAL_CENTROID = "HarmonicSpectralCentroid";
    static final String CFG_HARMONIC_SPECTRAL_VARIATION = "HarmonicSpectralVariation";
    static final String CFG_HARMONIC_SPECTRAL_DEVIATION = "HarmonicSpectralDeviation";
    static final String CFG_HARMONIC_SPECTRAL_SPREAD = "HarmonicSpectralSpread";
    static final String CFG_SPECTRAL_CENTROID = "SpectralCentroid";

    static final String CFG_AUDIO_SPECTRUM_BASIS = "AudioSpectrumBasisProjection";
    static final String CFG_AUDIO_SPECTRUM_PROJECTION = "AudioSpectrumBasisProjection";

    static final String CFG_HOP_SIZE = "HopSize";
    static final String CFG_ENABLE = "enable";
    static final String CFG_WINDOW_LENGTH = "windowlength";
    static final String CFG_WINDOW_SLIDE = "windowslide";
    static final String CFG_THRESHOLD = "threshold";
    static final String CFG_LOG_SCALE = "logScale";
    static final String CFG_LOW_LIMIT = "lolimit";
    static final String CFG_HIGH_LIMIT = "hilimit";
    static final String CFG_NON_HARMONICITY = "nonHarmonicity";
    static final String CFG_NR_OF_FRAMES = "";
    static final String CFG_NR_OF_FUNCTIONS = "";
    static final String CFG_LOW_EDGE = "loEdge";
    static final String CFG_HIGH_EDGE = "hiEdge";
    static final String CFG_RESOLUTION = "resolution";
    static final String CFG_DB_SCALE = "dbScale";
    static final String CFG_NORMALIZE = "normalize";

    private final Config m_config = new ConfigDefault();

    void loadSettingsFrom(final NodeSettingsRO settings) {
        try{
            final NodeSettingsRO resizer = settings.getNodeSettings(CFG_RESIZER);
            m_config.setValue(CFG_RESIZER, CFG_HOP_SIZE, resizer.getInt(CFG_HOP_SIZE));

            final NodeSettingsRO signalEnvelope = settings.getNodeSettings(CFG_SIGNAL_ENVELOPE);
            m_config.setValue(CFG_SIGNAL_ENVELOPE, CFG_WINDOW_LENGTH, signalEnvelope.getInt(CFG_WINDOW_LENGTH));
            m_config.setValue(CFG_SIGNAL_ENVELOPE, CFG_WINDOW_SLIDE, signalEnvelope.getInt(CFG_WINDOW_SLIDE));

            final NodeSettingsRO logAttackTime = settings.getNodeSettings(CFG_LOG_ATTACK_TIME);
            m_config.setValue(CFG_LOG_ATTACK_TIME, CFG_ENABLE, logAttackTime.getBoolean(CFG_ENABLE));
            m_config.setValue(CFG_LOG_ATTACK_TIME, CFG_THRESHOLD, logAttackTime.getFloat(CFG_THRESHOLD));

            final NodeSettingsRO temporalCentroid = settings.getNodeSettings(CFG_TEMPORAL_CENTROID);
            m_config.setValue(CFG_TEMPORAL_CENTROID, CFG_TEMPORAL_CENTROID, temporalCentroid.getBoolean(CFG_ENABLE));

            final NodeSettingsRO audioWaveform = settings.getNodeSettings(CFG_AUDIO_WAVEFORM);
            m_config.setValue(CFG_AUDIO_WAVEFORM, CFG_ENABLE, audioWaveform.getBoolean(CFG_ENABLE));

            final NodeSettingsRO audioPower = settings.getNodeSettings(CFG_AUDIO_POWER);
            m_config.setValue(CFG_AUDIO_POWER, CFG_ENABLE, audioPower.getBoolean(CFG_ENABLE));
            m_config.setValue(CFG_AUDIO_POWER, CFG_LOG_SCALE, audioPower.getBoolean(CFG_LOG_SCALE));

            final NodeSettingsRO audioFundamentalFrequency = settings.getNodeSettings(CFG_AUDIO_FUNDAMENTAL_FREQUENCY);
            m_config.setValue(CFG_AUDIO_FUNDAMENTAL_FREQUENCY, CFG_ENABLE, audioFundamentalFrequency.getBoolean(CFG_ENABLE));
            m_config.setValue(CFG_AUDIO_FUNDAMENTAL_FREQUENCY, CFG_LOW_LIMIT, audioFundamentalFrequency.getFloat(CFG_LOW_LIMIT));
            m_config.setValue(CFG_AUDIO_FUNDAMENTAL_FREQUENCY, CFG_HIGH_LIMIT, audioFundamentalFrequency.getFloat(CFG_HIGH_LIMIT));

            final NodeSettingsRO audioHarmonicity = settings.getNodeSettings(CFG_AUDIO_HARMONICITY);
            m_config.setValue(CFG_AUDIO_HARMONICITY, CFG_ENABLE, audioHarmonicity.getBoolean(CFG_ENABLE));

            final NodeSettingsRO harmonicPeaks = settings.getNodeSettings(CFG_HARMONIC_PEAKS);
            m_config.setValue(CFG_HARMONIC_PEAKS, CFG_NON_HARMONICITY, harmonicPeaks.getFloat(CFG_NON_HARMONICITY));
            m_config.setValue(CFG_HARMONIC_PEAKS, CFG_THRESHOLD, harmonicPeaks.getFloat(CFG_THRESHOLD));

            final NodeSettingsRO harmonicSpectralCentroid = settings.getNodeSettings(CFG_HARMONIC_SPECTRAL_CENTROID);
            m_config.setValue(CFG_HARMONIC_SPECTRAL_CENTROID, CFG_ENABLE, harmonicSpectralCentroid.getBoolean(CFG_ENABLE));

            final NodeSettingsRO harmonicSpectralDeviation = settings.getNodeSettings(CFG_HARMONIC_SPECTRAL_DEVIATION);
            m_config.setValue(CFG_HARMONIC_SPECTRAL_DEVIATION, CFG_ENABLE, harmonicSpectralDeviation.getBoolean(CFG_ENABLE));

            final NodeSettingsRO harmonicSpectralSpread = settings.getNodeSettings(CFG_HARMONIC_SPECTRAL_SPREAD);
            m_config.setValue(CFG_HARMONIC_SPECTRAL_SPREAD, CFG_ENABLE, harmonicSpectralSpread.getBoolean(CFG_ENABLE));

            final NodeSettingsRO harmonicSpectralVariation = settings.getNodeSettings(CFG_HARMONIC_SPECTRAL_VARIATION);
            m_config.setValue(CFG_HARMONIC_SPECTRAL_VARIATION, CFG_ENABLE, harmonicSpectralVariation.getBoolean(CFG_ENABLE));

            final NodeSettingsRO spectralCentroid = settings.getNodeSettings(CFG_SPECTRAL_CENTROID);
            m_config.setValue(CFG_SPECTRAL_CENTROID, CFG_ENABLE, spectralCentroid.getBoolean(CFG_ENABLE));

            final NodeSettingsRO audioSpectrumEnvelope = settings.getNodeSettings(CFG_AUDIO_SPECTRUM_ENVELOPE);
            m_config.setValue(CFG_AUDIO_SPECTRUM_ENVELOPE, CFG_ENABLE, audioSpectrumEnvelope.getBoolean(CFG_ENABLE));
            m_config.setValue(CFG_AUDIO_SPECTRUM_ENVELOPE, CFG_RESOLUTION, audioSpectrumEnvelope.getFloat(CFG_RESOLUTION));
            m_config.setValue(CFG_AUDIO_SPECTRUM_ENVELOPE, CFG_LOW_EDGE, audioSpectrumEnvelope.getFloat(CFG_LOW_EDGE));
            m_config.setValue(CFG_AUDIO_SPECTRUM_ENVELOPE, CFG_HIGH_EDGE, audioSpectrumEnvelope.getFloat(CFG_HIGH_EDGE));
            m_config.setValue(CFG_AUDIO_SPECTRUM_ENVELOPE, CFG_DB_SCALE, audioSpectrumEnvelope.getBoolean(CFG_DB_SCALE));
            m_config.setValue(CFG_AUDIO_SPECTRUM_ENVELOPE, CFG_NORMALIZE, audioSpectrumEnvelope.getString(CFG_NORMALIZE));

            final NodeSettingsRO audioSpectrumFlatness = settings.getNodeSettings(CFG_AUDIO_SPECTRUM_FLATNESS);
            m_config.setValue(CFG_AUDIO_SPECTRUM_FLATNESS, CFG_LOW_EDGE, audioSpectrumFlatness.getFloat(CFG_LOW_EDGE));
            m_config.setValue(CFG_AUDIO_SPECTRUM_FLATNESS, CFG_HIGH_EDGE, audioSpectrumFlatness.getFloat(CFG_HIGH_EDGE));
        } catch (InvalidSettingsException ex){
            // Do nothing, just use the default value of ConfigDefault
        }
    }

    void saveSettingsTo(final NodeSettingsWO settings) {
        final NodeSettingsWO resizer = settings.addNodeSettings(CFG_RESIZER);
        resizer.addInt(CFG_HOP_SIZE, m_config.getInt(CFG_RESIZER, CFG_HOP_SIZE));

        final NodeSettingsWO signalEnvelope = settings.addNodeSettings(CFG_SIGNAL_ENVELOPE);
        signalEnvelope.addInt(CFG_WINDOW_LENGTH, m_config.getInt(CFG_SIGNAL_ENVELOPE, CFG_WINDOW_LENGTH));
        signalEnvelope.addInt(CFG_WINDOW_SLIDE, m_config.getInt(CFG_SIGNAL_ENVELOPE, CFG_WINDOW_SLIDE));

        final NodeSettingsWO logAttackTime = settings.addNodeSettings(CFG_LOG_ATTACK_TIME);
        logAttackTime.addBoolean(CFG_ENABLE, m_config.getBoolean(CFG_LOG_ATTACK_TIME, CFG_ENABLE));
        logAttackTime.addFloat(CFG_THRESHOLD, m_config.getFloat(CFG_LOG_ATTACK_TIME, CFG_THRESHOLD));

        final NodeSettingsWO temporalCentroid = settings.addNodeSettings(CFG_TEMPORAL_CENTROID);
        temporalCentroid.addBoolean(CFG_ENABLE, m_config.getBoolean(CFG_TEMPORAL_CENTROID, CFG_ENABLE));

        final NodeSettingsWO audioWaveform = settings.addNodeSettings(CFG_AUDIO_WAVEFORM);
        audioWaveform.addBoolean(CFG_ENABLE, m_config.getBoolean(CFG_AUDIO_WAVEFORM, CFG_ENABLE));

        final NodeSettingsWO audioPower = settings.addNodeSettings(CFG_AUDIO_POWER);
        audioPower.addBoolean(CFG_ENABLE, m_config.getBoolean(CFG_AUDIO_POWER, CFG_ENABLE));
        audioPower.addBoolean(CFG_LOG_SCALE, m_config.getBoolean(CFG_AUDIO_POWER, CFG_LOG_SCALE));

        final NodeSettingsWO audioFundamentalFrequency = settings.addNodeSettings(CFG_AUDIO_FUNDAMENTAL_FREQUENCY);
        audioFundamentalFrequency.addBoolean(CFG_ENABLE, m_config.getBoolean(CFG_AUDIO_FUNDAMENTAL_FREQUENCY, CFG_ENABLE));
        audioFundamentalFrequency.addFloat(CFG_LOW_LIMIT, m_config.getFloat(CFG_AUDIO_FUNDAMENTAL_FREQUENCY, CFG_LOW_LIMIT));
        audioFundamentalFrequency.addFloat(CFG_HIGH_LIMIT, m_config.getFloat(CFG_AUDIO_FUNDAMENTAL_FREQUENCY, CFG_HIGH_LIMIT));

        final NodeSettingsWO audioHarmonicity = settings.addNodeSettings(CFG_AUDIO_HARMONICITY);
        audioHarmonicity.addBoolean(CFG_ENABLE, m_config.getBoolean(CFG_AUDIO_HARMONICITY, CFG_ENABLE));

        final NodeSettingsWO harmonicPeaks = settings.addNodeSettings(CFG_HARMONIC_PEAKS);
        harmonicPeaks.addFloat(CFG_NON_HARMONICITY, m_config.getFloat(CFG_HARMONIC_PEAKS, CFG_NON_HARMONICITY));
        harmonicPeaks.addFloat(CFG_THRESHOLD, m_config.getFloat(CFG_HARMONIC_PEAKS, CFG_THRESHOLD));

        final NodeSettingsWO harmonicSpectralCentroid = settings.addNodeSettings(CFG_HARMONIC_SPECTRAL_CENTROID);
        harmonicSpectralCentroid.addBoolean(CFG_ENABLE, m_config.getBoolean(CFG_HARMONIC_SPECTRAL_CENTROID, CFG_ENABLE));

        final NodeSettingsWO harmonicSpectralDeviation = settings.addNodeSettings(CFG_HARMONIC_SPECTRAL_DEVIATION);
        harmonicSpectralDeviation.addBoolean(CFG_ENABLE, m_config.getBoolean(CFG_HARMONIC_SPECTRAL_DEVIATION, CFG_ENABLE));

        final NodeSettingsWO harmonicSpectralSpread = settings.addNodeSettings(CFG_HARMONIC_SPECTRAL_SPREAD);
        harmonicSpectralSpread.addBoolean(CFG_ENABLE, m_config.getBoolean(CFG_HARMONIC_SPECTRAL_SPREAD, CFG_ENABLE));

        final NodeSettingsWO harmonicSpectralVariation = settings.addNodeSettings(CFG_HARMONIC_SPECTRAL_VARIATION);
        harmonicSpectralVariation.addBoolean(CFG_ENABLE, m_config.getBoolean(CFG_HARMONIC_SPECTRAL_VARIATION, CFG_ENABLE));

        final NodeSettingsWO spectralCentroid = settings.addNodeSettings(CFG_SPECTRAL_CENTROID);
        spectralCentroid.addBoolean(CFG_ENABLE, m_config.getBoolean(CFG_SPECTRAL_CENTROID, CFG_ENABLE));

        final NodeSettingsWO audioSpectrumEnvelope = settings.addNodeSettings(CFG_AUDIO_SPECTRUM_ENVELOPE);
        audioSpectrumEnvelope.addBoolean(CFG_ENABLE, m_config.getBoolean(CFG_AUDIO_SPECTRUM_ENVELOPE, CFG_ENABLE));
        audioSpectrumEnvelope.addFloat(CFG_RESOLUTION, m_config.getFloat(CFG_AUDIO_SPECTRUM_ENVELOPE, CFG_RESOLUTION));
        audioSpectrumEnvelope.addFloat(CFG_LOW_EDGE, m_config.getFloat(CFG_AUDIO_SPECTRUM_ENVELOPE, CFG_LOW_EDGE));
        audioSpectrumEnvelope.addFloat(CFG_HIGH_EDGE, m_config.getFloat(CFG_AUDIO_SPECTRUM_ENVELOPE, CFG_HIGH_EDGE));
        audioSpectrumEnvelope.addBoolean(CFG_DB_SCALE, m_config.getBoolean(CFG_AUDIO_SPECTRUM_ENVELOPE, CFG_DB_SCALE));
        audioSpectrumEnvelope.addString(CFG_NORMALIZE, m_config.getString(CFG_AUDIO_SPECTRUM_ENVELOPE, CFG_NORMALIZE));

        final NodeSettingsWO audioSpectrumFlatness = settings.addNodeSettings(CFG_AUDIO_SPECTRUM_FLATNESS);
        audioSpectrumFlatness.addFloat(CFG_LOW_EDGE, m_config.getFloat(CFG_AUDIO_SPECTRUM_FLATNESS, CFG_LOW_EDGE));
        audioSpectrumFlatness.addFloat(CFG_HIGH_EDGE, m_config.getFloat(CFG_AUDIO_SPECTRUM_FLATNESS, CFG_HIGH_EDGE));
    }


}
