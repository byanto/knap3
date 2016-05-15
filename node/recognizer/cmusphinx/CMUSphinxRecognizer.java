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
package org.knime.base.node.audio3.node.recognizer.cmusphinx;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.knime.base.node.audio3.data.Audio;
import org.knime.base.node.audio3.data.recognizer.RecognitionResult;
import org.knime.base.node.audio3.data.recognizer.Recognizer;
import org.knime.core.node.NodeLogger;

import edu.cmu.sphinx.api.AbstractSpeechRecognizer;
import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;

/**
 *
 * @author Budi Yanto, KNIME.com
 */
public class CMUSphinxRecognizer implements Recognizer {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(CMUSphinxRecognizer.class);

    private AbstractSpeechRecognizer m_recognizer;
    private final Configuration m_config;

    /**
     *
     */
    public CMUSphinxRecognizer() {

        m_config = new Configuration();

        final String packageName = CMUSphinxRecognizer.class.getPackage().getName().replace(".", File.separator);
        final String acousticModelPath = "resource:/" + packageName + File.separator + "models" + File.separator + "en-us-5.2";
        final String languageModelPath = "resource:/" + packageName + File.separator + "models" + File.separator + "en-us.lm";
        final String dictionaryPath = "resource:/" + packageName + File.separator + "models" + File.separator + "cmudict-en-us.dict";

        m_config.setAcousticModelPath(acousticModelPath);
        m_config.setLanguageModelPath(languageModelPath);
        m_config.setDictionaryPath(dictionaryPath);

        try{
            m_recognizer = new StreamSpeechRecognizer(m_config);
        } catch(IOException ex){
            LOGGER.error(ex.getMessage());
        }
    }

    /**
    *
    * @param acousticModelPath
    */
   public void setAcoustisModelPath(final String acousticModelPath){
       m_config.setAcousticModelPath(acousticModelPath);
   }

   /**
    * @return the acoustic model path
    */
   public String getAcousticModelPath(){
       return m_config.getAcousticModelPath();
   }

   /**
    *
    * @param languageModelPath
    */
   public void setLanguageModelPath(final String languageModelPath){
       m_config.setLanguageModelPath(languageModelPath);
   }

   /**
    * @return the language model path
    */
   public String getLanguageModelPath(){
       return m_config.getLanguageModelPath();
   }

   /**
   *
   * @param dictionaryPath
   */
  public void setDictionaryPath(final String dictionaryPath){
      m_config.setDictionaryPath(dictionaryPath);
  }

  /**
   * @return the dictionary path
   */
  public String getDictionaryPath(){
      return m_config.getDictionaryPath();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
      return "CMU Sphinx-4 Recognizer";
  }

    /**
     * {@inheritDoc}
     */
    @Override
    public RecognitionResult recognize(final Audio audio) {
        InputStream inStream = null;
        try{
            inStream = new BufferedInputStream(
                new FileInputStream(audio.getFile()));
        } catch(FileNotFoundException ex){
            LOGGER.error(ex);
        }

        final StreamSpeechRecognizer recognizer = (StreamSpeechRecognizer) m_recognizer;
        recognizer.startRecognition(inStream);
        SpeechResult result;
        final StringBuilder builder = new StringBuilder();
        while((result = recognizer.getResult()) != null){
            builder.append(result.getHypothesis());
        }
        recognizer.stopRecognition();
        return new RecognitionResult(getName(), builder.toString());
    }

}
