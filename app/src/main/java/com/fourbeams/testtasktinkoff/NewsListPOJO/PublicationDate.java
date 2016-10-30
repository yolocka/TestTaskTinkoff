
package com.fourbeams.testtasktinkoff.NewsListPOJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PublicationDate {

    @SerializedName("milliseconds")
    @Expose
    private Long milliseconds;

    /**
     * 
     * @return
     *     The milliseconds
     */
    public Long getMilliseconds() {
        return milliseconds;
    }

    /**
     * 
     * @param milliseconds
     *     The milliseconds
     */
    public void setMilliseconds(Long milliseconds) {
        this.milliseconds = milliseconds;
    }

}
