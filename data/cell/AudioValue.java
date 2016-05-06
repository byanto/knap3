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
 *   May 6, 2016 (budiyanto): created
 */
package org.knime.base.node.audio3.data.cell;

import java.util.Objects;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.knime.base.node.audio3.data.Audio;
import org.knime.core.data.DataValue;
import org.knime.core.data.DataValueComparator;
import org.knime.core.data.ExtensibleUtilityFactory;

/**
 *
 * @author Budi Yanto, KNIME.com
 */
public interface AudioValue extends DataValue {

    /**
     * @return the underlying <code>Audio</code> instance
     */
    Audio getAudio();

    /**
     * Returns whether the two audio values have the same content.
     *
     * @param val1 the first audio value
     * @param val2 the second audio value
     * @return <code>true</code> if both values are equal, <code>false</code> otherwise
     */
    static boolean equalContent(final AudioValue val1, final AudioValue val2){
        return Objects.equals(val1.getAudio(), val2.getAudio());
    }

    /**
     * Returns a hash code for the given audio value.
     *
     * @param val an audio value
     * @return the hash code
     */
    static int hashCode(final AudioValue val){
        return Objects.hashCode(val.getAudio());
    }

    class AudioUtilityFactory extends ExtensibleUtilityFactory{

        /** Singleton icon to be used to display this cell type. */
        private static final Icon ICON;

        /** Load icon, use <code>null</code> if not available. */
        static {
            ImageIcon icon;
            try {
                ClassLoader loader = AudioValue.class.getClassLoader();
                icon = new ImageIcon(loader.getResource("icon/AudioValue.png"));
            } catch (Exception e) {
                icon = null;
            }
            ICON = icon;
        }

        private static AudioValueComparator AUDIO_COMPARATOR = new AudioValueComparator();

        /**
         * Only subclasses are allowed to instantiate this class
         */
        protected AudioUtilityFactory(){
            super(AudioValue.class);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Icon getIcon() {
            return ICON;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected DataValueComparator getComparator() {
            return AUDIO_COMPARATOR;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getName() {
            return "Audio File";
        }

    }
}
