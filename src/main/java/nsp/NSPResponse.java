package nsp;

import java.io.Serializable;

/**
 * @Author: Christopher Kümmel on 6/14/2017.
 */
public interface NSPResponse extends Serializable {

    public String getText();

    public String getRecognitionStatus();
}
