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
 *   May 14, 2016 (budiyanto): created
 */
package org.knime.base.node.audio3.node.recognizer.watson;

import org.apache.commons.lang.StringUtils;
import org.knime.base.node.audio3.data.Audio;
import org.knime.base.node.audio3.data.recognizer.RecognitionResult;
import org.knime.base.node.audio3.data.recognizer.Recognizer;

import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechAlternative;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;

/**
 *
 * @author Budi Yanto, KNIME.com
 */
public class WatsonSpeechRecognizer implements Recognizer{

    private String m_userName = "";
    private String m_password = "";

    /**
     * @return the userName
     */
    public String getUserName() {
        return m_userName;
    }

    /**
     * @param userName the userName to set
     */
    public void setUserName(final String userName) {
        m_userName = userName;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return m_password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(final String password) {
        m_password = password;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RecognitionResult recognize(final Audio audio) {
        if(StringUtils.isBlank(m_userName) || StringUtils.isBlank(m_password)){
            throw new IllegalArgumentException("Username and password cannot be empty.");
        }
        final SpeechToText service = new SpeechToText();
        service.setUsernameAndPassword(m_userName, m_password);
        final SpeechResults results = service.recognize(audio.getFile());

        final SpeechAlternative alternative = results.getResults().get(0)
                .getAlternatives().get(0);
        final String transcript = alternative.getTranscript();
        final double confidence = alternative.getConfidence();
        return new RecognitionResult(getName(), transcript, confidence);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "IBM Watson Speech To Text";
    }

}
