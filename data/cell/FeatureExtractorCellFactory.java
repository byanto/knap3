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
 *   Mar 24, 2016 (budiyanto): created
 */
package org.knime.base.node.audio3.data.cell;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.knime.base.node.audio3.data.Audio;
import org.knime.base.node.audio3.data.AudioSamples;
import org.knime.base.node.audio3.data.feature.FeatureExtractor;
import org.knime.base.node.audio3.data.feature.FeatureType;
import org.knime.base.node.audio3.util.AudioUtils;
import org.knime.base.node.audio3.util.MathUtils;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataRow;
import org.knime.core.data.container.AbstractCellFactory;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.node.NodeLogger;

/**
 * The {@link AbstractCellFactory} implementation of the AudioFeatureExtractor node
 * that creates a cell for each selected document property.
 *
 * @author Budi Yanto, KNIME.com
 */
public class FeatureExtractorCellFactory extends AbstractCellFactory{

    private static final NodeLogger LOGGER = NodeLogger.getLogger(
        FeatureExtractorCellFactory.class);

    private final int m_audioColIdx;
    private final FeatureExtractor[] m_extractors;
    private final int m_windowSizeInSamples;
    private final int m_windowsOverlapInPercent;
    private final FeatureExtractor.Aggregator m_aggregator;

//    private final AudioFeatureCellExtractor[] m_extractors;
    /**
     *
     * @param audioColIdx
     * @param colSpecs
     * @param extractors
     * @param windowSizeInSamples
     * @param windowsOverlapInPercent
     * @param aggregatorMethod
     */
    public FeatureExtractorCellFactory(final int audioColIdx,
            final DataColumnSpec[] colSpecs, final FeatureExtractor[] extractors,
            final int windowSizeInSamples, final int windowsOverlapInPercent,
            final String aggregatorMethod){
        super(colSpecs);
        if (audioColIdx < 0) {
            throw new IllegalArgumentException("Invalid audio column");
        }

        if(StringUtils.isBlank(aggregatorMethod)){
            throw new IllegalArgumentException("Aggregator method cannot be empty");
        }
        m_audioColIdx = audioColIdx;
        m_extractors = extractors;
        m_windowSizeInSamples = windowSizeInSamples;
        m_windowsOverlapInPercent = windowsOverlapInPercent;
        m_aggregator = FeatureExtractor.getAggregator(aggregatorMethod);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataCell[] getCells(final DataRow row) {
        final DataCell[] cells = new DataCell[getColumnSpecs().length];
        final DataCell cell = row.getCell(m_audioColIdx);
        if(!cell.getType().isCompatible(AudioValue.class)){
            throw new IllegalStateException("Invalid column type");
        }

        final Audio audio = ((AudioCell) cell).getAudio();

        try {
            final AudioSamples audioSamples = AudioUtils.getAudioSamples(audio);
            final double[] samples = audioSamples.getSamplesMixedDownIntoOneChannel();
            final int windowOverlapOffset = (int)((m_windowsOverlapInPercent / 100f)
                    * m_windowSizeInSamples);

            final List<Integer> chunkIndices = getChunkStartIndices(samples,
                m_windowSizeInSamples, windowOverlapOffset);

            final Map<FeatureType, FeatureExtractor> orderedExtractors =
                    reorderExtractors(m_extractors);

            final Map<FeatureType, List<double[]>> featuresMap =
                    new HashMap<FeatureType, List<double[]>>();
            int toRead = m_windowSizeInSamples;
            for(int i = 0; i < chunkIndices.size(); i++){
                if(i == chunkIndices.size() - 1){
                    /* Last index, only read the rest of the samples */
                    toRead = samples.length - chunkIndices.get(i);
                }
                final double[] buf = new double[m_windowSizeInSamples];
                System.arraycopy(samples, chunkIndices.get(i), buf, 0, toRead);
                final AudioSamples chunk = new AudioSamples(buf, audioSamples.getAudioFormat());
                for(FeatureExtractor extractor : orderedExtractors.values()){
                    final FeatureType type = extractor.getType();
                    final FeatureType[] dependencies = type.getDependencies();
                    double[][] additionalValues = new double[dependencies.length][];
                    for(int j = 0; j < dependencies.length; j++){
                        additionalValues[j] = featuresMap.get(dependencies[j]).get(i);
                    }
                    if(!type.hasDependencies()){
                        additionalValues = null;
                    }
                    final double[] features = extractor.extractFeature(chunk, additionalValues);
                    List<double[]> list = featuresMap.get(type);
                    if(list == null){
                        list = new ArrayList<double[]>();
                        list.add(features);
                        featuresMap.put(type, list);
                    }else{
                        list.add(features);
                    }
                }
            }

            /* Aggregate all of the features */
            final Map<FeatureType, double[]> extractionResults =
                    new HashMap<FeatureType, double[]>();
            for(Entry<FeatureType, List<double[]>> entry : featuresMap.entrySet()){
                final String text = "Aggregate feature of '" + entry.getKey().getName() + "' with ";
                double[] aggregation = new double[0];
                final double[][] values = entry.getValue().toArray(
                    new double[entry.getValue().size()][]);
                switch (m_aggregator) {
                    case MEAN:
                        LOGGER.debug(text + "'" +
                                FeatureExtractor.Aggregator.MEAN.getName() + "'");
                        aggregation = MathUtils.mean(values);
                        break;
                    case STD_DEVIATION:
                        LOGGER.debug(text + "'" +
                                FeatureExtractor.Aggregator.STD_DEVIATION.getName()
                                + "'");
                        aggregation = MathUtils.standardDeviation(values);
                        break;
                    default:
                        break;
                }
                extractionResults.put(entry.getKey(), aggregation);
            }

            /* Put extracted features into DoubleCell */
            int cellIdx = 0;
            for(FeatureExtractor extractor : m_extractors){
                final double[] features = extractionResults.get(extractor.getType());
                LOGGER.debug("Put features of '" + extractor.getType().getName()
                    + "' into cells with dimension of " + features.length);
                for(final double d : features){
                    cells[cellIdx++] = new DoubleCell(d);
                }
            }
        } catch (Exception ex) {
            LOGGER.error(ex);
        }

        return cells;
    }

//    private List<AudioSamples> cutSamplesIntoChunks(final AudioSamples audioSamples,
//            final int chunkSize, final int chunkOverlap){
//        final List<AudioSamples> result = new ArrayList<AudioSamples>();
//        final double[] samples = audioSamples.getSamplesMixedDownIntoOneChannel();
//        int position =  0;
//        int restSamples = samples.length;
//        int toCopy = chunkSize;
//        while(position < samples.length){
//            double[] window = new double[chunkSize];
//            if(toCopy > restSamples){
//                toCopy = restSamples;
//            }
//
//            System.arraycopy(samples, position, window, 0, toCopy);
//            position = position + chunkSize - chunkOverlap;
//            restSamples = samples.length - position;
//            result.add(new AudioSamples(window, audioSamples.getAudioFormat()));
//        }
//
//        return result;
//    }

    private List<Integer> getChunkStartIndices(final double[] samples,
            final int chunkSize, final int chunkOverlapOffset){
        final List<Integer> result = new ArrayList<Integer>();
        int position = 0;
        while (position < samples.length) {
            position -= chunkOverlapOffset;
            if(position < 0){
                position = 0;
            }
            result.add(position);
            position += chunkSize;
        }
        return result;
    }

    private Map<FeatureType, FeatureExtractor> reorderExtractors(final FeatureExtractor[] extractors){
        List<FeatureExtractor> extractorList = new ArrayList<FeatureExtractor>(
                Arrays.asList(extractors));
        Map<FeatureType, FeatureExtractor> result =
                new LinkedHashMap<FeatureType, FeatureExtractor>();

        Iterator<FeatureExtractor> it = extractorList.iterator();
        while(it.hasNext()){
            FeatureExtractor extractor = it.next();
            if(!extractor.getType().hasDependencies()){
                result.put(extractor.getType(), extractor);
                it.remove();
            }
        }

        /* Iterate over the list to handle extractor with dependencies, if any */
        for(FeatureExtractor ext : extractorList){
            for(FeatureType type : ext.getType().getDependencies()){
                if(!result.containsKey(type)){
                    result.put(type, FeatureExtractor.getFeatureExtractor(type));
                }
            }
            result.put(ext.getType(), ext);
        }
        return result;
    }


//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    public DataCell[] getCells(final DataRow row) {
//
//        final DataCell cell = row.getCell(m_audioColIdx);
//        if(!cell.getType().isCompatible(AudioValue.class)){
//            throw new IllegalStateException("Invalid column type");
//        }
//
//        final int windowOverlapOffset = (int)((m_windowsOverlapInPercent / 100f) * m_windowSizeInSamples);
//
//        final Audio audio = ((AudioCell) cell).getAudio();
//        final DataCell[] cells = new DataCell[getColumnSpecs().length];
//        int cellIdx = 0;
//        SampleChunkFactory chunkFactory = null;
//        try{
//            chunkFactory = new SampleChunkFactory(
//                audio, m_windowSizeInSamples, m_windowsOverlapInPercent, true);
//
//            for(final FeatureExtractor extractor : m_extractors){
//                final List<double[]> featuresList = new ArrayList<double[]>();
//                chunkFactory.reset();
//                SampleChunk chunk = null;
//                while((chunk = chunkFactory.nextSampleChunk()) != null){
//                    final double[] features = extractor.extractFeature(
//                        (MonoChannelSampleChunk) chunk, null);
//                    featuresList.add(features);
//                }
//
//                double[] aggregation = new double[0];
//                final double[][] values = featuresList.toArray(
//                    new double[featuresList.size()][]);
//                switch (m_aggregator) {
//                    case MEAN:
//                        aggregation = MathUtils.mean(values);
//                        break;
//                    case STD_DEVIATION:
//                        aggregation = MathUtils.standardDeviation(values);
//                        break;
//                    default:
//                        break;
//                }
//                for(int i = 0; i < aggregation.length; i++){
//                    cells[cellIdx++] = new DoubleCell(aggregation[i]);
//                }
//            }
//
//        } catch(Exception ex){
//            LOGGER.error(ex);
//        } finally{
//            try{
//                if(chunkFactory != null){
//                    chunkFactory.closeStream();
//                }
//            } catch(IOException ex){
//                LOGGER.error(ex);
//            }
//        }
//
//        return cells;
//    }

}
