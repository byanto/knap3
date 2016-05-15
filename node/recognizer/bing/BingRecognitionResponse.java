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
 *   Apr 12, 2016 (budiyanto): created
 */
package org.knime.base.node.audio3.node.recognizer.bing;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 *
 * @author Budi Yanto, KNIME.com
 */
class BingRecognitionResponse {

    private String m_version;
    private List<Result> m_results;

    BingRecognitionResponse() {}

    /**
     * @return the version
     */
    String getVersion() {
        return m_version;
    }

    /**
     * @param version the version to set
     */
    void setVersion(final String version) {
        m_version = version;
    }

    /**
     * @return the header
     */
    @JsonIgnore
    Object getHeader() {
        // Ignore header
        return null;
    }

    /**
     * @param header the header to set
     */
    @JsonIgnore
    void setHeader(final String header) {
        // Ignore header
    }

    /**
     * @return the results
     */
    List<Result> getResults() {
        return m_results;
    }

    /**
     * @param results the results to set
     */
    void setResults(final List<Result> results) {
        m_results = results;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("[version]: ").append(m_version).append("\n");
        builder.append("[results]\n");
        for(Result res : m_results){
            builder.append("scenario: ").append(res.getScenario()).append("\n");
            builder.append("name: ").append(res.getName()).append("\n");
            builder.append("lexical: ").append(res.getLexical()).append("\n");
            builder.append("confidence: ").append(res.getConfidence()).append("\n\n");
        }

        return builder.toString();
    }

}
