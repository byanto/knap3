package org.knime.base.node.audio3.node.recognizer.bing;

/**
 *
 * @author Budi Yanto, KNIME.com
 */
class AccessTokenInfo {

    private long m_startTime = System.currentTimeMillis();
	private String m_accessToken;
	private String m_tokenType;
	private String m_expiresIn;
	private String m_scope;

	AccessTokenInfo(){}

	/**
	 * @return the accessToken
	 */
	String getAccess_token() {
		return m_accessToken;
	}

	/**
	 * @param accessToken the accessToken to set
	 */
	void setAccess_token(final String accessToken) {
		m_accessToken = accessToken;
	}

	/**
	 * @return the tokenType
	 */
	String getToken_type() {
		return m_tokenType;
	}

	/**
	 * @param tokenType the tokenType to set
	 */
	void setToken_type(final String tokenType) {
		m_tokenType = tokenType;
	}

	/**
	 * @return the expiresIn
	 */
	String getExpires_in() {
		return m_expiresIn;
	}

	/**
	 * @param expiresIn the expiresIn to set
	 */
	void setExpires_in(final String expiresIn) {
		m_expiresIn = expiresIn;
	}

	/**
	 * @return the scope
	 */
	String getScope() {
		return m_scope;
	}

	/**
	 * @param scope the scope to set
	 */
	void setScope(final String scope) {
		m_scope = scope;
	}

	/**
	 * @return <code>true</code> if the token is expired, otherwise <code>false</code>
	 */
	boolean isExpired(){
	    if((m_startTime + (Long.valueOf(m_expiresIn) * 1000)) > System.currentTimeMillis()){
	        return false;
	    }
	    return true;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("AccessTokenInfo{\n");
		builder.append("accessToken=").append(m_accessToken).append("\n");
		builder.append("tokenType=").append(m_tokenType).append("\n");
		builder.append("expiresIn=").append(m_expiresIn).append("\n");
		builder.append("scope=").append(m_scope).append("\n");
		builder.append("}");
		return builder.toString();
	}

}
