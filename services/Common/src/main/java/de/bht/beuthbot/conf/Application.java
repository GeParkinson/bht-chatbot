package de.bht.beuthbot.conf;


import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author: georg.glossmann@adesso.de
 * Date: 03.07.17
 */
@Stateful
@ApplicationScoped
public class Application {

    /**
     * Path to application configuration file on target wildfly server
     */
    private static final String CONFIGURATION_PATH = "/opt/jboss/wildfly/standalone/conf/";

    /**
     * Configuration file name
     */
    private static final String CONFIGURATION_FILE = "beuthbot.properties";

    private Properties properties;

    @PostConstruct
    public void init() {
        getProperties();
    }

    private Properties getProperties(){
        if (properties == null) {
            this.properties = new Properties();
            InputStream inputStream = null;
            try {
                inputStream = Application.class.getResourceAsStream(CONFIGURATION_PATH + CONFIGURATION_FILE);
                properties.load(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return properties;
    }

    /**
     * Gets the value of configured property
     * @param property enum of defined property in configuration file
     * @return String of configured value
     */
    public String getConfiguration(final Configuration property) {
        return getProperties().getProperty(property.name());
    }
}
