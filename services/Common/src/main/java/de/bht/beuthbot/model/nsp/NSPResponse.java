package de.bht.beuthbot.model.nsp;

import java.io.Serializable;

/**
 * @Author: Christopher Kümmel on 6/14/2017.
 */
public interface NSPResponse extends Serializable {

    /** generated text from file */
    String getText();

    /** probability of recognition */
    String getRecognitionStatus();
}
