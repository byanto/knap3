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
 *   May 15, 2016 (budiyanto): created
 */
package org.knime.base.node.audio3.node.recognizer.bing;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.UUID;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.lang.StringUtils;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.knime.base.node.audio3.data.Audio;
import org.knime.base.node.audio3.data.recognizer.RecognitionResult;
import org.knime.base.node.audio3.data.recognizer.Recognizer;
import org.knime.core.node.NodeLogger;

/**
 *
 * @author Budi Yanto, KNIME.com
 */
public class BingSpeechRecognizer implements Recognizer {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(BingSpeechRecognizer.class);

    /** Default language of the audio file to recognize */
    static final String DEFAULT_LANGUAGE = "en-US";

    /** Default scenario used for the recognition */
    static final String DEFAULT_SCENARIO = "ulm";

    /** Default maxnbest */
    static final int DEFAULT_MAXNBEST = 1;

    /** Default profanity markup */
    static final int DEFAULT_PROFANITY_MARKUP = 1;

    // Data needed for authentication to get the access token from Microsoft
    private static final String ACCESS_URI = "https://oxford-speech.cloudapp.net/token/issueToken";

    private static final String GRANT_TYPE = "client_credentials";

    private static final String CLIENT_ID = "microsoft-cognitive-service-speechapi";

    private static final String SCOPE = "https://speech.platform.bing.com";

    // Data needed for recognition
    private static final String HOST = "https://speech.platform.bing.com/recognize/query";

    private static final String VERSION = "3.0";

    private static final String APP_ID = "D4D52672-91D7-4C74-8AD8-42B1D98141A5";

    private static final String FORMAT = "json";

    private static final String DEVICE_OS = "Linux";

    private final String INSTANCE_ID = UUID.randomUUID().toString();

    private String m_subscriptionKey;

    private String m_language;

    private String m_scenario;

    private int m_maxNBest;

    private int m_profanityMarkup;

    private AccessTokenInfo m_accessToken = null;

    /**
    *
    */
    public BingSpeechRecognizer() {
        this(null, DEFAULT_LANGUAGE, DEFAULT_SCENARIO, DEFAULT_MAXNBEST, DEFAULT_PROFANITY_MARKUP);
    }

    /**
     *
     * @param subscriptionKey
     */
    public BingSpeechRecognizer(final String subscriptionKey) {
        this(subscriptionKey, DEFAULT_LANGUAGE, DEFAULT_SCENARIO, DEFAULT_MAXNBEST, DEFAULT_PROFANITY_MARKUP);
    }

    /**
     *
     * @param subscriptionKey
     * @param language
     */
    public BingSpeechRecognizer(final String subscriptionKey, final String language) {
        this(subscriptionKey, language, DEFAULT_SCENARIO, DEFAULT_MAXNBEST, DEFAULT_PROFANITY_MARKUP);
    }

    /**
     *
     * @param subscriptionKey
     * @param language
     * @param maxNBest
     */
    public BingSpeechRecognizer(final String subscriptionKey, final String language, final int maxNBest) {
        this(subscriptionKey, language, DEFAULT_SCENARIO, maxNBest, DEFAULT_PROFANITY_MARKUP);
    }

    /**
     *
     * @param subscriptionKey
     * @param language
     * @param maxNBest
     * @param profanityMarkup
     */
    public BingSpeechRecognizer(final String subscriptionKey, final String language, final int maxNBest,
        final int profanityMarkup) {
        this(subscriptionKey, language, DEFAULT_SCENARIO, maxNBest, profanityMarkup);
    }

    /**
     *
     * @param subscriptionKey
     * @param language
     * @param scenario
     * @param maxNBest
     * @param profanityMarkup
     */
    public BingSpeechRecognizer(final String subscriptionKey, final String language, final String scenario,
        final int maxNBest, final int profanityMarkup) {
        m_subscriptionKey = subscriptionKey;
        m_language = language;
        m_scenario = scenario;
        m_maxNBest = maxNBest;
        m_profanityMarkup = profanityMarkup;
    }

    /**
     * @return the subscriptionKey
     */
    public String getSubscriptionKey() {
        return m_subscriptionKey;
    }

    /**
     * @param subscriptionKey the subscriptionKey to set
     */
    public void setSubscriptionKey(final String subscriptionKey) {
        m_subscriptionKey = subscriptionKey;
    }

    /**
     * @return <code>true</code> if the subscription key isn't empty, otherwise <code>false</code>
     */
    public boolean hasSubscriptionKey() {
        return !StringUtils.isBlank(m_subscriptionKey);
    }

    /**
     * @return the language
     */
    public String getLanguage() {
        return m_language;
    }

    /**
     * @param language the language to set
     */
    public void setLanguage(final String language) {
        m_language = language;
    }

    /**
     * @return the scenario
     */
    public String getScenario(){
        return m_scenario;
    }

    /**
     * @param scenario the scenario to set
     */
    public void setScenario(final String scenario) {
        m_scenario = scenario;
    }

    /**
     * @return the maxNBest
     */
    public int getMaxNBest() {
        return m_maxNBest;
    }

    /**
     * @param maxNBest the maxNBest to set
     */
    public void setMaxNBest(final int maxNBest) {
        m_maxNBest = maxNBest;
    }

    /**
     * @return the profanityMarkup
     */
    public int getProfanityMarkup() {
        return m_profanityMarkup;
    }

    /**
     * @param profanityMarkup the profanityMarkup to set
     */
    public void setProfanityMarkup(final int profanityMarkup) {
        m_profanityMarkup = profanityMarkup;
    }

    private boolean authenticate(){
        if(m_accessToken == null || m_accessToken.isExpired()){
            final Form form = new Form();
            form.param("grant_type", GRANT_TYPE);
            form.param("client_id", CLIENT_ID);
            form.param("client_secret", m_subscriptionKey);
            form.param("scope", SCOPE);

            final Client client = ClientBuilder.newBuilder()
                    .register(JacksonFeature.class)
                    .build();
//            client.register(new LoggingFilter());
            LOGGER.info("Retrieve access token from Microsoft.");
            LOGGER.info("Access URI: " + ACCESS_URI);
            final Response response = client.target(ACCESS_URI)
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
            if(response.getStatusInfo().getFamily() == Family.SUCCESSFUL){
                LOGGER.info("Successfully retrieved access token");
                m_accessToken = response.readEntity(AccessTokenInfo.class);
                return true;
            }else{
                LOGGER.error("Cannot retrieved access token - Status " + response.getStatus());
                LOGGER.error("Info: " + response.getStatusInfo());
                m_accessToken = null;
            }
        }

        if(!m_accessToken.isExpired()){
            return true;
        }

        return false;
    }


    /**
     * @return all supported languages of the recognizer in UTF-8 format
     */
    public static String[] getSupportedLanguages() {
        return new String[]{
            "de-DE", "es-ES", "en-GB", "en-US", "fr-FR", "it-IT", "zh-CN",
            "zh-TW", "ja-JP", "en-IN", "pt-BR", "ko-KR", "fr-CA", "en-AU",
            "zh-HK", "ar-EG", "fi-FI", "pt-PT", "en-NZ", "pl-PL", "en-CA",
            "ru-RU", "da-DK", "nl-NL", "ca-ES", "nb-NO", "es-MX", "sv-SE"
        };
    }

    /**
     * @return all supported scenarios of the recognizer
     */
    public static String[] getSupportedScenarios() {
        return new String[]{
            "ulm", "websearch"};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "Microsoft Bing Speech";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RecognitionResult recognize(final Audio audio) {
        if(m_subscriptionKey == null || m_subscriptionKey.isEmpty()){
            throw new NullPointerException("Subscription key must be set.");
        }

        RecognitionResult result = null;

        if(authenticate()){
            // Start recognition
            final String authToken = "Bearer " + m_accessToken.getAccess_token();
            final String requestId = UUID.randomUUID().toString();

            final Client client = ClientBuilder.newClient();
//            client.register(new LoggingFilter());

            final String endPoint = UriBuilder.fromPath(HOST)
                    .queryParam("scenarios", m_scenario)
                    .queryParam("appid", APP_ID)
                    .queryParam("locale", m_language)
                    .queryParam("device.os", DEVICE_OS)
                    .queryParam("version", VERSION)
                    .queryParam("format", FORMAT)
                    .queryParam("instanceid", INSTANCE_ID)
                    .queryParam("requestid", requestId)
                    .queryParam("maxnbest", m_maxNBest)
                    .queryParam("result.profanitymarkup", m_profanityMarkup)
                    .toString();

            LOGGER.info("Start recognition using Microsoft Congitive Service - Speech API");
            LOGGER.info("End-Point: " + endPoint);

            final Invocation.Builder builder = client.target(endPoint).request();
            builder.header(HttpHeaders.AUTHORIZATION, authToken);

            InputStream stream = null;
            try{
                stream = new FileInputStream(audio.getFile());
            } catch(FileNotFoundException ex){
                LOGGER.error(ex);
            }

            Response response = builder.post(Entity.entity(stream, "audio/wav; samplerate=16000"));

            if(response.getStatusInfo().getFamily() == Family.SUCCESSFUL){
                LOGGER.info("Successfully recognized audio file.");
                final BingRecognitionResponse responseResult = response.readEntity(
                    BingRecognitionResponse.class);
                Result res = responseResult.getResults().get(0);
                result = new RecognitionResult(getName(), res.getLexical(), res.getConfidence());
            }else{
                LOGGER.error("Cannot recognized audio file - Status " + response.getStatus());
                LOGGER.error("Info: " + response.getStatusInfo());
            }
        }

        return result;
    }

}
