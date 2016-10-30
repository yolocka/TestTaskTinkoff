
package com.fourbeams.testtasktinkoff.NewsListPOJO;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class News {

    @SerializedName("resultCode")
    @Expose
    private String resultCode;
    @SerializedName("payload")
    @Expose
    private List<Payload> payload = new ArrayList<Payload>();

    /**
     * 
     * @return
     *     The resultCode
     */
    public String getResultCode() {
        return resultCode;
    }

    /**
     * 
     * @param resultCode
     *     The resultCode
     */
    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    /**
     * 
     * @return
     *     The payload
     */
    public List<Payload> getPayload() {
        return payload;
    }

    /**
     * 
     * @param payload
     *     The payload
     */
    public void setPayload(List<Payload> payload) {
        this.payload = payload;
    }

}
