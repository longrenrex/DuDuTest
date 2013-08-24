package com.baidu.dudu.network.http;

import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthenticationException;
import org.apache.commons.httpclient.auth.BasicScheme;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

/**
 * A simple http authenticator, only support basic scheme
 *
 */
@SuppressWarnings("deprecation")
public class Authenticator {
    private String m_realm;
    private String m_username;
    private String m_password;
    private final String m_basicHeader;

    protected Authenticator(String realm) {
        m_realm = realm;
        m_basicHeader = null;
    }
    
    public Authenticator(String username, String password, String realm) {
        m_realm = realm;
        m_username = username;
        m_password = password;
        if(m_username != null)
            m_basicHeader = BasicScheme.authenticate(new UsernamePasswordCredentials(m_username, m_password));
        else
            m_basicHeader = null;
    }
    
    public boolean isAuthEnabled() {
        if(m_username == null || "".equals(m_username.trim()))
            return false;
        return true;
    }
    
    /**
     * authenticate HTTP clients, throw exception if authentication fails
     * 
     * @param authHeader
     * @throws AuthenticationException
     */
    public Object authenticate(Header authHeader) throws AuthenticationException {
        //if username is not set, always succeed the authentication
        if(!isAuthEnabled())
            return null;
        return auth(authHeader);
    }
    
    protected Object auth(Header authHeader) throws AuthenticationException {
        if(authHeader == null || !"Authorization".equals(authHeader.getName()))
            throw new AuthenticationException("need authentication");
        
        if(m_basicHeader.equals(authHeader.getValue())) {
            return new Object();
        } else {
            throw new AuthenticationException("invalid auth scheme/username/password");
        }
    }
    
    public Header getChallengeHeader() {
        Header challenge = new BasicHeader("WWW-Authenticate", "Basic realm=\""+m_realm+"\"");
        return challenge;
    }

}
