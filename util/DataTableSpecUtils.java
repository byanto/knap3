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
 *   Apr 13, 2016 (budiyanto): created
 */
package org.knime.base.node.audio3.util;

import org.knime.base.node.audio3.data.cell.AudioValue;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;

/**
 *
 * @author Budi Yanto, KNIME.com
 */
public final class DataTableSpecUtils {

    /**
     * @param spec the <code>DataTableSpec</code> to get the information
     * @return the first audio column in the given <code>DataTableSpec</code>
     */
    public static String getFirstAudioColumn(final DataTableSpec spec){
        String result = null;
        for(final DataColumnSpec colSpec : spec){
            if(colSpec.getType().isCompatible(AudioValue.class)){
                result = colSpec.getName();
                break;
            }
        }
        return result;
    }

    /**
     * Verifies that the given column exists in the <code>DataTableSpec</code>
     * @param spec the <code>DataTableSpec</code> to analyze
     * @param column the column name to analyze
     * @return the index of the column in the <code>DataTableSpec</code> if exists,
     * otherwise -1
     * @throws InvalidSettingsException if the column doesn't exist in the <code>DataTableSpec</code>
     */
    public static int verifyColumnInDataTableSpec(final DataTableSpec spec,
            final String column) throws InvalidSettingsException {
        final int colIndex = spec.findColumnIndex(column);
        if(colIndex < 0){
            throw new InvalidSettingsException(
                "Column \"" + column + "\" doesn't exist in DataTableSpec");
        }
        return colIndex;
    }

}
