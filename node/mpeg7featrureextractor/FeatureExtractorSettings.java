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
 *   Mar 22, 2016 (budiyanto): created
 */
package org.knime.base.node.audio3.node.mpeg7featrureextractor;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.knime.base.node.audio3.data.feature.FeatureExtractor;
import org.knime.base.node.audio3.data.feature.FeatureType;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

/**
 *
 * @author Budi Yanto, KNIME.com
 */
class FeatureExtractorSettings {

    private final Map<FeatureType, FeatureSetting> m_featuresMap;
    private static final String CFG_FEATURES = "features";

    FeatureExtractorSettings() {
        m_featuresMap = new LinkedHashMap<FeatureType, FeatureExtractorSettings.FeatureSetting>();
        for(FeatureType type : FeatureType.values()){
            m_featuresMap.put(type, new FeatureSetting(type));
        }
    }

    void saveSettingsTo(final NodeSettingsWO settings){
        NodeSettingsWO root = settings.addNodeSettings(CFG_FEATURES);
        for(Entry<FeatureType, FeatureSetting> entry : m_featuresMap.entrySet()){
            NodeSettingsWO cfg = root.addNodeSettings(entry.getKey().getName());
            entry.getValue().saveSettingsTo(cfg);
        }
    }

    void loadSettingsFrom(final NodeSettingsRO settings){
        try{
            final NodeSettingsRO root = settings.getNodeSettings(CFG_FEATURES);
            for(String key : root.keySet()){
                final NodeSettingsRO cfg = root.getNodeSettings(key);
                final FeatureType type = FeatureType.getFeatureType(key);
                if(type != null){
                    m_featuresMap.get(type).loadSettingsFrom(cfg);
                }
            }
        } catch (InvalidSettingsException ex){
            // Do nothing if no setting is available
        }
    }

    void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException{
        NodeSettingsRO root = settings.getNodeSettings(CFG_FEATURES);
        for(String key : root.keySet()){
            final NodeSettingsRO cfg = root.getNodeSettings(key);
            final FeatureType type = FeatureType.getFeatureType(key);
            if(type != null){
                m_featuresMap.get(type).validateSettings(cfg);
            }
        }
    }

    void setSelected(final FeatureType type, final boolean isSelected){
        if(m_featuresMap.containsKey(type)){
            m_featuresMap.get(type).setSelected(isSelected);
        }
    }

    Boolean isSelected(final FeatureType type){
        if(m_featuresMap.containsKey(type)){
            return m_featuresMap.get(type).isSelected();
        }
        return null;
    }

    Set<FeatureType> getAudioFeatureTypes(){
        return m_featuresMap.keySet();
    }

    void setParameterValue(final FeatureType type, final String parameter,
            final double value){
        if(m_featuresMap.containsKey(type)){
            m_featuresMap.get(type).setParameterValue(parameter, value);
        }
    }

    Double getParameterValue(final FeatureType type, final String parameter){
        if(m_featuresMap.containsKey(type)){
            return m_featuresMap.get(type).getParameterValue(parameter);
        }
        return null;
    }

    Set<FeatureType> getSelectedFeatures(){
        final Set<FeatureType> features = new LinkedHashSet<FeatureType>();
        for(Entry<FeatureType, FeatureSetting> entry : m_featuresMap.entrySet()){
            final FeatureType feature = entry.getKey();
            if(entry.getValue().isSelected()){
                features.add(feature);
            }
        }
        return features;
    }

    void updateExtractorParameters(final FeatureExtractor... extractors){
        for(FeatureExtractor extractor : extractors){
            final FeatureType type = extractor.getType();
            if(type.hasParameters()){
                for(final String parameter : type.getParameters()){
                    extractor.setParameterValue(parameter,
                        getParameterValue(type, parameter));
                }
            }
        }
    }

    private class FeatureSetting{
        private static final String CFG_IS_SELECTED = "isSelected";
        private static final String CFG_PARAMETERS = "parameters";

        private static final boolean DEFAULT_IS_SELECTED = false;

        private boolean m_isSelected;
        private final Map<String, Double> m_parameters;

        private FeatureSetting(final FeatureType type){
            m_isSelected = DEFAULT_IS_SELECTED;
            if(type.hasParameters()){
                final FeatureExtractor extractor = FeatureExtractor.getFeatureExtractor(type);
                m_parameters = new LinkedHashMap<String, Double>();
                for(final String parameter : type.getParameters()){
                    m_parameters.put(parameter, extractor.getParameterValue(parameter));
                }
            }else{
                m_parameters = null;
            }
        }

        private void saveSettingsTo(final NodeSettingsWO settings){
            settings.addBoolean(CFG_IS_SELECTED, isSelected());
            if(m_parameters != null && m_parameters.size() > 0){
                final NodeSettingsWO cfg = settings.addNodeSettings(CFG_PARAMETERS);
                for(final Entry<String, Double> entry : m_parameters.entrySet()){
                    cfg.addDouble(entry.getKey(), entry.getValue());
                }
            }
        }

        private void loadSettingsFrom(final NodeSettingsRO settings){
            setSelected(settings.getBoolean(CFG_IS_SELECTED, DEFAULT_IS_SELECTED));
            try{
                final NodeSettingsRO cfg = settings.getNodeSettings(CFG_PARAMETERS);
                m_parameters.clear();
                for(final String key : cfg.keySet()){
                    m_parameters.put(key, cfg.getDouble(key));
                }
            } catch(InvalidSettingsException ex){
                // No parameters
            }
        }

        private void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException{
            settings.getBoolean(CFG_IS_SELECTED);
            if(m_parameters != null){
                final NodeSettingsRO cfg = settings.getNodeSettings(CFG_PARAMETERS);
                for(final String key : cfg.keySet()){
                    cfg.getDouble(key);
                }
            }
        }

        private void setSelected(final boolean isSelected){
            m_isSelected = isSelected;
        }

        private boolean isSelected(){
            return m_isSelected;
        }

        private void setParameterValue(final String parameter, final double value){
            if(m_parameters != null && m_parameters.size() > 0){
                m_parameters.put(parameter, value);
            }
        }

        /**
         * Returns the value of the given parameter
         * @param parameter the parameter whose value should be returned
         * @return the value of the given parameter or null if the parameter doesn't exist
         */
        private Double getParameterValue(final String parameter){
            if(m_parameters != null && m_parameters.size() > 0){
                return m_parameters.get(parameter);
            }
            return null;
        }
    }

}
