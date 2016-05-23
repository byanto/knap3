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
package org.knime.base.node.audio3.data.feature.mpeg7;

/**
 *
 * @author Budi Yanto, KNIME.com
 */
public enum MPEG7FeatureType {
    /** Audio Waveform */
    AUDIO_WAVEFORM(
        "Audio Waveform",
        "AudioWaveform",
        "Description AW",
        new String[0]),

    /** Audio Power */
    AUDIO_POWER(
        "Audio Power",
        "AudioPower",
        "Description AP",
        new String[0]),

    /** Audio Spectrum Envelope */
    AUDIO_SPECTRUM_ENVELOPE(
        "Audio Spectrum Envelope",
        "AudioSpectrumEnvelope",
        "Description ASE",
        new String[]{"loEdge", "hiEdge", "resolution", "dbScale", "normalize"}),

    /** Audio Spectrum Centroid */
    AUDIO_SPECTRUM_CENTROID(
        "Audio Spectrum Centroid",
        "AudioSpectrumCentroid",
        "Description ASC",
        new String[0]),

    /** Audio Spectrum Spread */
    AUDIO_SPECTRUM_SPREAD(
        "Audio Spectrum Spread",
        "AudioSpectrumSpread",
        "Description ASS",
        new String[0]),

    /** Audio Spectrum Flatness */
    AUDIO_SPECTRUM_FLATNESS(
        "Audio Spectrum Flatness",
        "AudioSpectrumFlatness",
        "Description ASF",
        new String[]{"loEdge", "hiEdge"}),

    /** Audio Harmonicity */
    AUDIO_HARMONICITY(
        "Audio Harmonicity",
        "AudioHarmonicity",
        "Description AH",
        new String[0]),

    /** Audio Fundamental Frequency */
    AUDIO_FUNDAMENTAL_FREQUENCY(
        "Audio Fundamental Frequency",
        "AudioFundamentalFrequency",
        "Description AFF",
        new String[]{"lolimit", "hilimit"}),

    /** Log Attack Time */
    LOG_ATTACK_TIME(
        "Log Attack Time",
        "LogAttackTime",
        "Description LAT",
        new String[]{"windowlength", "windowslide", "threshold"}),

    /** Temporal Centroid */
    TEMPORAL_CENTROID(
        "Temporal Centroid",
        "TemporalCentroid",
        "Description TC",
        new String[]{"windowlength", "windowslide"}),

    /** Harmonic Spectral Centroid */
    HARMONIC_SPECTRAL_CENTROID(
        "Harmonic Spectral Centroid",
        "HarmonicSpectralCentroid",
        "Description HSC",
        new String[]{"nonHarmonicity", "threshold"}),

    /** Harmonic Spectral Deviation */
    HARMONIC_SPECTRAL_DEVIATION(
        "Harmonic Spectral Deviation",
        "HarmonicSpectralVariation",
        "Description HSD",
        new String[]{"nonHarmonicity", "threshold"}),

    /** Harmonic Spectral Spread */
    HARMONIC_SPECTRAL_SPREAD(
        "Harmonic Spectral Spread",
        "HarmonicSpectralSpread",
        "Description HSS",
        new String[]{"nonHarmonicity", "threshold"}),

    /** Harmonic Spectral Variation */
    HARMONIC_SPECTRAL_VARIATION(
        "Harmonic Spectral Variation",
        "HarmonicSpectralVariation",
        "Description HSV",
        new String[]{"nonHarmonicity", "threshold"}),

    /** Spectral Centroid */
    SPECTRAL_CENTROID(
        "Spectral Centroid",
        "SpectralCentroid",
        "Description SC",
        new String[0]),

    /** Audio Spectrum Basis */
    AUDIO_SPECTRUM_BASIS(
        "Audio Spectrum Basis",
        "AudioSpectrumBasisProjection",
        "Description ASB",
        new String[]{"frames", "numic"}),

    /** Audio Spectrum Projection */
    AUDIO_SPECTRUM_PROJECTION(
        "Audio Spectrum Projection",
        "AudioSpectrumBasisProjection",
        "Description ASP",
        new String[]{"frames", "numic"});

    private final String m_name;
    private final String m_configName;
    private final String m_description;
    private final String[] m_parameters;

    private MPEG7FeatureType(final String name, final String configName,
        final String description, final String[] parameters){
        m_name = name;
        m_configName = configName;
        m_description = description;
        m_parameters = parameters;
    }

    /**
     * @return the name
     */
    public String getName() {
        return m_name;
    }

    /**
     * @return the configName
     */
    public String getConfigName() {
        return m_configName;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return m_description;
    }

    /**
     * @return <code>true</code> if this feature type has parameters, otherwise <code>false</code>
     */
    public boolean hasParameters() {
        return (m_parameters != null && m_parameters.length > 0);
    }

    /**
     * @return the parameters
     */
    public String[] getParameters() {
        return m_parameters;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getName();
    }

}
