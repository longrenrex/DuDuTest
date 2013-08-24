package com.baidu.dudu.network.http;


import java.io.IOException;

import org.apache.commons.httpclient.auth.AuthenticationException;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthenticationHelper {

    private static final Logger m_logger = LoggerFactory.getLogger(AuthenticationHelper.class);
    public static final String CONTEXT_ATTR_AUTHENTICATION_RESULT = "context_attr_auth_result";
    private Authenticator m_authenticator;
    private HttpRequestInterceptor m_reqInterceptor;
    private HttpResponseInterceptor m_respInterceptor;

    public AuthenticationHelper(Authenticator authenticator) {
        m_authenticator = authenticator;
        m_reqInterceptor = new HttpRequestInterceptor() {

            public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
                Object result = null;
                try {
                    result = m_authenticator.authenticate(request.getFirstHeader("Authorization"));
                    if (result != null)
                        context.setAttribute(CONTEXT_ATTR_AUTHENTICATION_RESULT, result);
                }
                catch (AuthenticationException e) {
                    m_logger.warn("fail to authenticate the HTTP request from host server: " + e);
                    context.setAttribute(CONTEXT_ATTR_AUTHENTICATION_RESULT, e);
                }
            }
        };
        m_respInterceptor = new HttpResponseInterceptor() {

            public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
                Object authResult = context.removeAttribute(CONTEXT_ATTR_AUTHENTICATION_RESULT);
                if (authResult instanceof AuthenticationException) {
                    response.addHeader(m_authenticator.getChallengeHeader());
                    response.setStatusCode(401);
                }
            }
        };
    }

    public void addAuthenticationInterceptors(BasicHttpProcessor httpproc) {
        httpproc.addRequestInterceptor(m_reqInterceptor);
        httpproc.addInterceptor(m_respInterceptor);
    }

    public static Object getAuthenticationResult(HttpContext context) throws AuthenticationException {
        Object obj = context.getAttribute(CONTEXT_ATTR_AUTHENTICATION_RESULT);
        if (obj instanceof AuthenticationException)
            throw (AuthenticationException) obj;
        return obj;
    }
}
