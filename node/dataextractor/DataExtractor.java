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
 *   May 16, 2016 (budiyanto): created
 */
package org.knime.base.node.audio3.node.dataextractor;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.knime.base.node.audio3.data.Audio;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataType;
import org.knime.core.data.def.StringCell;

/**
 *
 * @author Budi Yanto, KNIME.com
 */
public enum DataExtractor {

    /** Returns the name of an audio */
    NAME("Name", new Extractor() {
        @Override
        public DataCell getValue(final Audio audio) {
            final String name = FilenameUtils.getBaseName(
                audio.getFile().getName());
            if(StringUtils.isBlank(name)){
                return DataType.getMissingCell();
            }
            return new StringCell(name);
        }

        @Override
        public DataType getType() {
            return StringCell.TYPE;
        }
    }),

    /** Returns the path of an audio */
    PATH("Path", new Extractor() {
        @Override
        public DataCell getValue(final Audio audio) {
            final String path = audio.getFile().getAbsolutePath();
            if(StringUtils.isBlank(path)){
                return DataType.getMissingCell();
            }
            return new StringCell(path);
        }

        @Override
        public DataType getType() {
            return StringCell.TYPE;
        }
    });

//    /** Returns the type of an audio */
//    TYPE("Type", new Extractor() {
//        @Override
//        public DataCell getValue(final Audio audio) {
//            final String type = audio.getAudioFileFormat().getType().toString();
//            if(StringUtils.isBlank(type)){
//                return DataType.getMissingCell();
//            }
//            return new StringCell(type);
//        }
//
//        @Override
//        public DataType getType() {
//            return StringCell.TYPE;
//        }
//    });

//    /** Returns the recognition results of an audio */
//    RECOGNITION("Recognition", new Extractor() {
//        @Override
//        public DataCell[] getValue(final Audio audio) {
//            final Map<String, RecognitionResult> map = audio.getRecognitionResults();
//            final DataCell[] cells = new DataCell[map.size() * 3];
//            int idx = 0;
//            for(Entry<String, RecognitionResult> entry : map.entrySet()){
//                cells[idx++] = new StringCell(entry.getKey());
//                cells[idx++] = new StringCell(entry.getValue().getTranscript());
//                cells[idx++] = new DoubleCell(entry.getValue().getConfidence());
//            }
//            return cells;
//        }
//    });

    private interface Extractor {

        /**
         * @param audio the {@link Audio} to extract the data from
         * @return the extracted data as array of {@link DataCell}
         */
        public DataCell getValue(final Audio audio);

        /**
         * @return the {@link DataType}
         */
        public DataType getType();

    }

    private final String m_name;
    private final Extractor m_extractor;

    private DataExtractor(final String name, final Extractor extractor){
        if(StringUtils.isBlank(name)){
            throw new IllegalArgumentException("Name cannot be empty.");
        }
        if(extractor == null){
            throw new IllegalArgumentException("Extractor cannot be null.");
        }
        m_name = name;
        m_extractor = extractor;
    }

    /**
     * @return the name of the extractor
     */
    public String getName() {
        return m_name;
    }

    /**
     * @param audio the {@link Audio} to extract the data from
     * @return the extracted data as array of {@link DataCell}
     */
    public DataCell getValue(final Audio audio){
        return m_extractor.getValue(audio);
    }

    /**
     * @return the {@link DataType}
     */
    public DataType getType(){
        return m_extractor.getType();
    }

    /**
     * @return the name of all extractors
     */
    public static String[] getExtractorNames() {
        final DataExtractor[] values = values();
        final String[] names = new String[values.length];
        for (int i = 0, length = values.length; i < length; i++) {
            names[i] = values[i].getName();
        }
        return names;
    }

    /**
     * @param names the name of the extractors to get
     * @return the extractors with the given name in the same order
     */
    public static DataExtractor[] getExctractor(final String...names) {
        if (names == null) {
            return null;
        }
        final DataExtractor[] extractors = new DataExtractor[names.length];
        for (int i = 0, length = names.length; i < length; i++) {
            final String name = names[i];
            for (final DataExtractor extractor : values()) {
                if (extractor.getName().equals(name)) {
                    extractors[i] = extractor;
                    break;
                }
            }
            if (extractors[i] == null) {
                throw new IllegalArgumentException(
                        "Invalid extractor name: " + name);
            }
        }
        return extractors;
    }
}
