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

import org.knime.base.node.audio3.data.AudioSamples;

/**
 *
 * @author Budi Yanto, KNIME.com
 */
public abstract class FeatureExtractor {

    private final FeatureType m_type;
    private final Map<String, Double> m_parameters = new LinkedHashMap<String, Double>();

    /**
     *
     * @param type
     */
    protected FeatureExtractor(final FeatureType type) {
        this(type, new double[0]);
    }

    /**
     *
     * @param type
     * @param parameterValues
     */
    protected FeatureExtractor(final FeatureType type, final double[] parameterValues){
        if(type == null){
            throw new IllegalArgumentException("Feature type cannot be null");
        }

        final String[] parameters = type.getParameters();

        if(parameterValues.length != parameters.length){
            throw new IllegalArgumentException("Parameters and their default "
                + "values must have the same length.");
        }

        m_type = type;
        for(int i = 0; i < parameters.length; i++){
            m_parameters.put(parameters[i], parameterValues[i]);
        }
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
    public Double getParameterValue(final String parameter){
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
    public void setParameterValue(final String parameter, final double value){
        if(m_parameters != null &&  m_parameters.containsKey(parameter)){
            m_parameters.put(parameter, value);
        }
    }

    /**
     * Extract the feature of the given sample chunk
     * @param samples the samples of the audio whose feature should be extracted
     * @param additionalFeatureValues the values of the dependencies if needed for the extraction
     * @return the extracted feature of the given sample chunk
     * @throws Exception
     */
    public abstract double[] extractFeature(final AudioSamples samples,
        final double[][] additionalFeatureValues) throws Exception;

    /**
     * @param windowSize the windows size of the sample chunk
     * @return the dimension of the extracted feature values
     */
    public abstract int getDimension(final int windowSize);

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
            case SPECTRAL_CENTROID:
                return new SpectralCentroid();
            case SPECTRAL_ROLLOFF_POINT:
                return new SpectralRolloffPoint();
            case COMPACTNESS:
                return new Compactness();
            case ROOT_MEAN_SQUARE:
                return new RootMeanSquare();
            case ZERO_CROSSINGS:
                return new ZeroCrossings();
            case LPC:
                return new LPC();
            case PEAK_DETECTION:
                return new PeakDetection();
            case CONSTANTQ:
                return new ConstantQ();
            case CHROMA:
                return new Chroma();
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

    public static Aggregator getAggregator(final String aggregator){
        if(aggregator.equals(Aggregator.MEAN.getName())){
            return Aggregator.MEAN;
        }else if(aggregator.equals(Aggregator.STD_DEVIATION.getName())){
            return Aggregator.STD_DEVIATION;
        }else{
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((m_parameters == null) ? 0 : m_parameters.hashCode());
        result = prime * result + ((m_type == null) ? 0 : m_type.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        FeatureExtractor other = (FeatureExtractor)obj;
        if (m_parameters == null) {
            if (other.m_parameters != null) {
                return false;
            }
        } else if (!m_parameters.equals(other.m_parameters)) {
            return false;
        }
        if (m_type != other.m_type) {
            return false;
        }
        return true;
    }



    public enum Aggregator{
        MEAN("Mean"),

        STD_DEVIATION("Standard Deviation");

        private final String m_name;
        private Aggregator(final String name){
            m_name = name;
        }

        public String getName(){
            return m_name;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return getName();
        }

    }
}
