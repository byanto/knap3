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
package org.knime.base.node.audio3.data.component;

import org.knime.base.node.audio3.data.cell.AudioValue;
import org.knime.base.node.audio3.util.DataTableSpecUtils;
import org.knime.base.node.audio3.util.KNAPConstants;
import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.DialogComponentColumnNameSelection;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 *
 * @author Budi Yanto, KNIME.com
 */
public class AudioColumnSelection {

    private SettingsModelString m_audioColumnModel = createAudioColumnSettingsModel();
    private int m_audioColumnIndex = -1;

    /**
     * @return the <code>DialogComponentColumnNameSelection</code> used to select
     * audio column
     */
    public static DialogComponentColumnNameSelection createDialogComponent(){
        return createDialogComponent(KNAPConstants.AUDIO_COL_LABEL, 0);
    }

    /**
     * @param specIndex index of (input) port listing available columns
     * @return the <code>DialogComponentColumnNameSelection</code> used to select
     * audio column
     */
    public static DialogComponentColumnNameSelection createDialogComponent(
            final int specIndex){
        return createDialogComponent(KNAPConstants.AUDIO_COL_LABEL, specIndex);
    }

    /**
     * @param label label for the dialog component
     * @param specIndex index of (input) port listing available columns
     * @return the <code>DialogComponentColumnNameSelection</code> used to select
     * audio column
     */
    @SuppressWarnings("unchecked")
    public static DialogComponentColumnNameSelection createDialogComponent(
            final String label, final int specIndex){
        return new DialogComponentColumnNameSelection(
                    createAudioColumnSettingsModel(),
                    label, specIndex, AudioValue.class);
    }

    private static SettingsModelString createAudioColumnSettingsModel(){
        return new SettingsModelString("AudioColumn", null);
    }

    /**
     * @param inSpec the input <code>DataTableSpec</code>
     * @throws InvalidSettingsException if failed.
     */
    public void configure(final DataTableSpec inSpec) throws InvalidSettingsException{
        final String audioColName = m_audioColumnModel.getStringValue();
        if(audioColName == null){
            // At the beginning, no audio column is selected
            // So, select the first audio column in the DataTableSpec
            final String column = DataTableSpecUtils.getFirstAudioColumn(inSpec);
            m_audioColumnModel.setStringValue(column);
            m_audioColumnIndex = inSpec.findColumnIndex(column);
        }else{
            // Check whether the selected audio column really exists in the DataTableSpec
            m_audioColumnIndex = DataTableSpecUtils.verifyColumnInDataTableSpec(inSpec, audioColName);
        }
    }

    /**
     * Validates the settings in the passed <code>NodeSettings</code> object.
     * @param settings The settings to validate.
     * @throws InvalidSettingsException If the validation of the settings failed.
     */
    public void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        m_audioColumnModel.validateSettings(settings);
    }

    /**
     * Sets new settings from the passed object in the model.
     * @param settings The settings to read.
     * @throws InvalidSettingsException If a property is not available.
     */
    public void laodSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        m_audioColumnModel.loadSettingsFrom(settings);
    }

    /**
     * Adds to the given <code>NodeSettings</code> the model specific
     * settings.
     * @param settings The object to write settings into.
     */
    public void saveSettingsTo(final NodeSettingsWO settings) {
        m_audioColumnModel.saveSettingsTo(settings);
    }

    /**
     * @return the selected audio column
     */
    public String getSelectedColumn(){
        return m_audioColumnModel.getStringValue();
    }

    /**
     * @return the index of the selected audio column in <code>DataTableSpec</code>
     */
    public int getSelectedColumnIndex(){
        return m_audioColumnIndex;
    }

}
