
package de.bht.beuthbot.nlp.apiai.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * @author: Oliver
 * Date: 19.06.17
 */
public class Status  implements Serializable {

    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("errorType")
    @Expose
    private String errorType;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

}
