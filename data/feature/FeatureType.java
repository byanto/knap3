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

import java.util.ArrayList;
import java.util.List;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataType;
import org.knime.core.data.collection.CollectionCellFactory;
import org.knime.core.data.collection.ListCell;
import org.knime.core.data.def.DoubleCell;

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
        new String[0],
        new CellExtractor() {
            @Override
            public DataCell[] getCells(final double[] featureValues) {
                return getDoubleArrayCells(featureValues);
            }

            @Override
            public DataType getDataType() {
                return DoubleCell.TYPE;
            }
        }),

    /**
     *
     */
    MAGNITUDE_SPECTRUM(
        "Magnitude Spectrum",
        "A measure of the strength of different frequency components.",
        new FeatureType[0],
        new String[0],
        new CellExtractor() {
            @Override
            public DataCell[] getCells(final double[] featureValues) {
                return getDoubleArrayCells(featureValues);
            }

            @Override
            public DataType getDataType() {
                return DoubleCell.TYPE;
            }
        }),

    /**
     *
     */
    MFCC(
        "MFCC",
        "MFCC calculations based upon Orange Cow code.",
        new FeatureType[]{MAGNITUDE_SPECTRUM},
        new String[]{"Number of coefficients"},
        new CellExtractor() {
            @Override
            public DataCell[] getCells(final double[] featureValues) {
                return getDoubleArrayCells(featureValues);
            }

            @Override
            public DataType getDataType() {
                return DoubleCell.TYPE;
            }
        }),

    /**
     *
     */
    SPECTRAL_CENTROID(
        "Spectral Centroid",
        "The center of mass of the power spectrum.",
        new FeatureType[]{POWER_SPECTRUM},
        new String[0],
        new CellExtractor() {
            @Override
            public DataCell[] getCells(final double[] featureValues) {
                return getDoubleArrayCells(featureValues);
            }

            @Override
            public DataType getDataType() {
                return DoubleCell.TYPE;
            }
        }),

    /**
     *
     */
    SPECTRAL_ROLLOFF_POINT(
        "Spectral Rolloff Point",
        "The fraction of bins in the power spectrum at which 85% of the power "
        + "is at lower frequencies. This is a measure of the right-skewedness "
        + "of the power spectrum.",
        new FeatureType[]{POWER_SPECTRUM},
        new String[]{"Cutoff point (0-1)"},
        new CellExtractor() {
            @Override
            public DataCell[] getCells(final double[] featureValues) {
                return getDoubleArrayCells(featureValues);
            }

            @Override
            public DataType getDataType() {
                return DoubleCell.TYPE;
            }
        }),

    /**
     *
     */
    COMPACTNESS(
        "Compactness",
        "A measure of the noisiness of a signal. Found by comparing the "
        + "components of a window's magnitude spectrum with the magnitude "
        + "spectrum of its neighbouring windows.",
        new FeatureType[]{MAGNITUDE_SPECTRUM},
        new String[0],
        new CellExtractor() {
            @Override
            public DataCell[] getCells(final double[] featureValues) {
                return getDoubleArrayCells(featureValues);
            }

            @Override
            public DataType getDataType() {
                return DoubleCell.TYPE;
            }
        }),

    /**
     *
     */
    ROOT_MEAN_SQUARE(
        "Root Mean Square",
        "A measure of the power of a signal.",
        new FeatureType[0],
        new String[0],
        new CellExtractor() {
            @Override
            public DataCell[] getCells(final double[] featureValues) {
                return getDoubleArrayCells(featureValues);
            }

            @Override
            public DataType getDataType() {
                return DoubleCell.TYPE;
            }
        }),

    /**
     *
     */
    ZERO_CROSSINGS(
        "Zero Crosssings",
        "The number of times the waveform changed sign. An indication of "
        + "frequency as well as noisiness.",
        new FeatureType[0],
        new String[0],
        new CellExtractor() {
            @Override
            public DataCell[] getCells(final double[] featureValues) {
                return getDoubleArrayCells(featureValues);
            }

            @Override
            public DataType getDataType() {
                return DoubleCell.TYPE;
            }
        }),

    /**
     *
     */
    LPC(
        "LPC",
        "Linear Prediction Coeffecients calculated using autocorrelation and "
        + "Levinson-Durbin recursion.",
        new FeatureType[0],
        new String[]{"lambda for frequency warping",
            "number of coeffecients to calculate"},
        new CellExtractor() {
            @Override
            public DataCell[] getCells(final double[] featureValues) {
                return getDoubleArrayCells(featureValues);
            }

            @Override
            public DataType getDataType() {
                return DoubleCell.TYPE;
            }
        }),

    /**
     *
     */
    PEAK_DETECTION(
        "Peak Detection",
        "All peaks that are within an order of magnitude of the highest point.",
        new FeatureType[]{MAGNITUDE_SPECTRUM},
        new String[]{"Threshold for peak detection"},
        new CellExtractor() {
            @Override
            public DataCell[] getCells(final double[] featureValues) {
                return getListCells(featureValues);
            }

            @Override
            public DataType getDataType() {
                return ListCell.getCollectionType(DoubleCell.TYPE);
            }
        }),

    /**
     *
     */
    CONSTANTQ(
        "ConstantQ",
        "Signal to frequency transform using exponential-spaced frequency bins.",
        new FeatureType[0],
        new String[]{"Percent of a semitone per bin"},
        new CellExtractor() {
            @Override
            public DataCell[] getCells(final double[] featureValues) {
                return getListCells(featureValues);
            }

            @Override
            public DataType getDataType() {
                return ListCell.getCollectionType(DoubleCell.TYPE);
            }
        }),

    /**
     *
     */
    CHROMA(
        "Chroma",
        "Basic chroma feature derived from ConstantQ function output.",
        new FeatureType[]{CONSTANTQ},
        new String[0],
        new CellExtractor() {
            @Override
            public DataCell[] getCells(final double[] featureValues) {
                return getDoubleArrayCells(featureValues);
            }

            @Override
            public DataType getDataType() {
                return DoubleCell.TYPE;
            }
        });

//    DUMMY1(
//        "Dummy1",
//        "Basic Dummy1",
//        new FeatureType[]{CONSTANTQ, MFCC, CHROMA},
//        new String[0]),
//
//    DUMMY2(
//        "Dummy2",
//        "Basic Dummy2.",
//        new FeatureType[]{DUMMY1, MFCC, POWER_SPECTRUM},
//        new String[0]),
//
//    DUMMY3(
//        "Dummy3",
//        "Basic Dummy3.",
//        new FeatureType[]{DUMMY2, POWER_SPECTRUM, CHROMA},
//        new String[0]);

    private final String m_name;

    private final String m_description;

    private final FeatureType[] m_dependencies;

    private final String[] m_parameters;

    private final CellExtractor m_cellExtractor;

    private FeatureType(final String name, final String description, final FeatureType[] dependencies,
        final String[] parameters, final CellExtractor cellExtractor) {

        m_name = name;
        m_description = description;
        m_dependencies = dependencies;
        m_parameters = parameters;
        if (cellExtractor == null) {
            throw new NullPointerException("Extractor must not be null");
        }
        m_cellExtractor = cellExtractor;
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
     * @param featureValues
     * @return data cells
     */
    public DataCell[] getDataCells(final double[] featureValues){
        return m_cellExtractor.getCells(featureValues);
    }

    /**
     * @return {@link DataType}
     */
    public DataType getDataType(){
        return m_cellExtractor.getDataType();
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

    private interface CellExtractor{
        /**
         * @param featureValues the feature values to extract the data from
         * @return the extracted data as {@link DataCell}
         */
        public DataCell[] getCells(final double[] featureValues);

        /**
         * @return the {@link DataType}
         */
        public DataType getDataType();
    }

    private static DataCell[] getDoubleArrayCells(final double[] featureValues){
        if(featureValues == null || featureValues.length == 0){
            return new DataCell[]{DataType.getMissingCell()};
        }
        final DataCell[] cells = new DataCell[featureValues.length];
        for(int i = 0; i < featureValues.length; i++){
            cells[i] = new DoubleCell(featureValues[i]);
        }
        return cells;
    }

    private static DataCell[] getListCells(final double[] featureValues){
        if(featureValues == null || featureValues.length == 0){
            return new DataCell[]{DataType.getMissingCell()};
        }
        final List<DoubleCell> cells = new ArrayList<DoubleCell>();
        for(double val : featureValues){
            cells.add(new DoubleCell(val));
        }
        return new DataCell[]{CollectionCellFactory.createListCell(cells)};
    }

}
