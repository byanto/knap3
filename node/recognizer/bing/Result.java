package org.knime.base.node.audio3.node.recognizer.bing;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 *
 * @author Budi Yanto, KNIME.com
 */
class Result {

	private String m_scenario;
	private String m_name;
	private String m_lexical;
	private float m_confidence;

	Result(){}

	/**
	 * @return the scenario
	 */
	String getScenario() {
		return m_scenario;
	}

	/**
	 * @param scenario the scenario to set
	 */
	void setScenario(final String scenario) {
		m_scenario = scenario;
	}

	/**
	 * @return the name
	 */
	String getName() {
		return m_name;
	}

	/**
	 * @param name the name to set
	 */
	void setName(final String name) {
		m_name = name;
	}

	/**
	 * @return the lexical
	 */
	String getLexical() {
		return m_lexical;
	}

	/**
	 * @param lexical the lexical to set
	 */
	void setLexical(final String lexical) {
		m_lexical = lexical;
	}

	/**
	 * @return the confidence
	 */
	float getConfidence() {
		return m_confidence;
	}

	/**
	 * @param confidence the confidence to set
	 */
	void setConfidence(final float confidence) {
		m_confidence = confidence;
	}

	@JsonIgnore
	public String getProperties(){
		// Ignore properties
	    return null;
	}

	@JsonIgnore
	void setProperties(){
	    // Ignore properties
	}

}
