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
 *   Mar 28, 2016 (budiyanto): created
 */
package org.knime.base.node.audio3.data.feature;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author Budi Yanto, KNIME.com
 */
public abstract class FeatureExtractor {

    private final FeatureType m_type;
    private final Map<String, Integer> m_parameters;

    /**
     *
     * @param type
     */
    protected FeatureExtractor(final FeatureType type) {
        this(type, null);
    }

    /**
     *
     * @param type
     * @param parameterValues
     */
    protected FeatureExtractor(final FeatureType type, final int[] parameterValues){
        if(type == null){
            throw new IllegalArgumentException("Feature type cannot be null");
        }

        final String[] parameters = type.getParameters();
        if(parameterValues == null && parameters != null){
            throw new IllegalArgumentException("Parameters must have default values");
        }

        if(parameterValues != null && parameters != null &&
                (parameterValues.length != parameters.length)){
            throw new IllegalArgumentException("Parameters and their default "
                + "values must have the same length.");
        }

        if(parameters == null || parameterValues == null){
            m_parameters = null;
        }else{
            m_parameters = new LinkedHashMap<String, Integer>();
            for(int i = 0; i < parameters.length; i++){
                m_parameters.put(parameters[i], parameterValues[i]);
            }
        }

        m_type = type;

    }

    /**
     *
     * @return the feature type of this extractor
     */
    public FeatureType getType(){
        return m_type;
    }

    /**
     * Returns the value of the given parameter
     * @param parameter the parameter whose value should be returned
     * @return the value of the given parameter, returns null if the parameter doesn't exist
     */
    public Integer getParameterValue(final String parameter){
        if(m_parameters != null){
            return m_parameters.get(parameter);
        }
        return null;
    }

    /**
     * Sets the value of the given parameter, do nothing if the given parameter doesn't exist
     * @param parameter the parameter whose value should be set
     * @param value the value to set
     */
    public void setParameterValue(final String parameter, final int value){
        if(m_parameters != null &&  m_parameters.containsKey(parameter)){
            m_parameters.put(parameter, value);
        }
    }

    /**
     * Extract the feature of the given samples
     * @param samples the samples of the audio whose feature should be extracted
     * @param sampleRate the sample rate of the audio
     * @param additionalFeatureValues the values of the dependencies if needed for the extraction
     * @return the feature of the given samples
     * @throws Exception
     */
    public abstract double[] extractFeature(final double[] samples, final double sampleRate,
        final double[][] additionalFeatureValues) throws Exception;

    /**
     *
     * @param type
     * @return the feature extractor for the given feature type
     */
    public static FeatureExtractor getFeatureExtractor(final FeatureType type){
        switch (type) {
            case POWER_SPECTRUM:
                return new PowerSpectrum();
            case MAGNITUDE_SPECTRUM:
                return new MagnitudeSpectrum();
            case MFCC:
                return new MFCC();
            default:
                throw new IllegalArgumentException("There isn't extractor defined "
                    + "for the given feature type: " + type);
        }
    }

    /**
     *
     * @param types
     * @return the feature extractors
     */
    public static FeatureExtractor[] getFeatureExtractors(final FeatureType... types){
        final FeatureExtractor[] extractors = new FeatureExtractor[types.length];
        for(int i = 0; i < extractors.length; i++){
            extractors[i] = getFeatureExtractor(types[i]);
        }
        return extractors;
    }
}
