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
 *   May 9, 2016 (budiyanto): created
 */
package org.knime.base.node.audio3.data.feature;

/**
 *
 * @author Budi Yanto, KNIME.com
 */
public enum FeatureType {

    /**
    *
    */
    POWER_SPECTRUM(
        "Power Spectrum",
        "A measure of the power of different frequency components.",
        new FeatureType[0],
        new String[0]),

    /**
     *
     */
    MAGNITUDE_SPECTRUM(
        "Magnitude Spectrum",
        "A measure of the strength of different frequency components.",
        new FeatureType[0],
        new String[0]),

    /**
     *
     */
    MFCC(
        "MFCC",
        "MFCC calculations based upon Orange Cow code.",
        new FeatureType[]{MAGNITUDE_SPECTRUM},
        new String[]{"Number of coefficients"});

    private final String m_name;

    private final String m_description;

    private final FeatureType[] m_dependencies;

    private final String[] m_parameters;

    private FeatureType(final String name, final String description, final FeatureType[] dependencies,
        final String[] parameters) {

        m_name = name;
        m_description = description;
        m_dependencies = dependencies;
        m_parameters = parameters;
    }

    /**
     * @return the name
     */
    public String getName() {
        return m_name;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return m_description;
    }

    /**
     *
     * @return true if the feature type has dependencies, otherwise false
     */
    public boolean hasDependencies() {
        return (m_dependencies != null && m_dependencies.length > 0);
    }

    /**
     * @return the dependencies
     */
    public FeatureType[] getDependencies() {
        return m_dependencies;
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

    /**
     * Returns the name of all audio feature types
     *
     * @return the name of all audio feature types
     */
    public static String[] getFeatureTypeNames() {
        final FeatureType[] types = FeatureType.values();
        final String[] names = new String[types.length];
        for (int i = 0; i < types.length; i++) {
            names[i] = types[i].getName();
        }
        return names;
    }

    /**
     * Returns the audio feature types based on the given name in the same order
     *
     * @param names the name of the audio feature types to get
     * @return the audio feature types based on the given name in the same order
     */
    public static FeatureType[] getFeatureTypes(final String... names) {
        if (names == null) {
            return null;
        }

        final FeatureType[] types = new FeatureType[names.length];
        for (int i = 0; i < names.length; i++) {
            for (final FeatureType type : FeatureType.values()) {
                if (type.getName().equals(names[i])) {
                    types[i] = type;
                    break;
                }
            }
            if (types[i] == null) {
                throw new IllegalArgumentException("Invalid name of audio feature type: " + names[i]);
            }
        }
        return types;
    }

    /**
     * @param name the name of the feature type to retrieve
     * @return the feature type based on the given name
     */
    public static FeatureType getFeatureType(final String name) {
        final FeatureType[] types = getFeatureTypes(name);
        if (types != null && types.length == 1) {
            return types[0];
        }
        return null;
    }

}
