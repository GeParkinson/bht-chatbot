package de.bht.beuthbot.conf;


import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author: georg.glossmann@adesso.de
 * Date: 03.07.17
 */
@Stateless
public class ApplicationBean implements Application {

    /**
     * Path to application configuration file on target wildfly server
     */
    private static final String CONFIGURATION_PATH = "/opt/jboss/wildfly/standalone/conf/";

    /**
     * Configuration file name
     */
    private static final String CONFIGURATION_FILE = "beuthbot.properties";

    /**
     * slf4j Logger
     */
    private final Logger logger = LoggerFactory.getLogger(ApplicationBean.class);

    /**
     * Properties defined in conf file
     */
    private Properties properties;

    /**
     * Loading defined properties from conf file
     * @return
     */
    private Properties loadProperties() {
        if (properties == null) {
            this.properties = new Properties();
            File confFile = new File(CONFIGURATION_PATH + CONFIGURATION_FILE);
            try (InputStream inputStream = FileUtils.openInputStream(confFile)) {
                properties.load(inputStream);
            } catch (IOException e) {
                logger.error("Error while reading file {}", confFile.getAbsolutePath(), e);
            }
        }
        return properties;
    }

    /**
     * Gets the value of configured property
     *
     * @param property enum of defined property in configuration file
     * @return String of configured value
     */
    public String getConfiguration(final Configuration property) {
        return loadProperties().getProperty(property.name());
    }
}
