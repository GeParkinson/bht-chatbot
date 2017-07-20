package de.bht.beuthbot.conf;

import javax.ejb.Remote;

/**
 * @author: georg.glossmann@adesso.de
 * Date: 05.07.17
 */
@Remote
public interface Application {
    String getConfiguration(Configuration property);
}
