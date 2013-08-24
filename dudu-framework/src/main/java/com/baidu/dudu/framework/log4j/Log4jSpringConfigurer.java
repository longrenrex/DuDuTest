package com.baidu.dudu.framework.log4j;


import java.io.FileNotFoundException;

import org.springframework.util.Log4jConfigurer;
import org.springframework.util.ResourceUtils;
import org.springframework.util.SystemPropertyUtils;

/**
 * @author rzhao
 */
public class Log4jSpringConfigurer {

    private String log4jConfigLocation = "classpath:log4j.properties";

    private long log4jRefreshInterval;

    public void init() {

        try {
            // Return a URL (e.g. "classpath:" or "file:") as-is
            if (!ResourceUtils.isUrl(log4jConfigLocation)) {
                // Resolve system property placeholders
                log4jConfigLocation = SystemPropertyUtils.resolvePlaceholders(log4jConfigLocation);
            }

            Log4jConfigurer.initLogging(log4jConfigLocation, log4jRefreshInterval);
        }
        catch (FileNotFoundException ex) {
            throw new IllegalArgumentException("Invalid 'log4jConfigLocation' parameter: " + ex.getMessage());
        }
    }

    public void destroy() {
        Log4jConfigurer.shutdownLogging();
    }

    public void setLog4jConfigLocation(String log4jConfigLocation) {
        this.log4jConfigLocation = log4jConfigLocation;
    }

    public void setLog4jRefreshInterval(long log4jRefreshInterval) {
        this.log4jRefreshInterval = log4jRefreshInterval * 1000;
    }

}
