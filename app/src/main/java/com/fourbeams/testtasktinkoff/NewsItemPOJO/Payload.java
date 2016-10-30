
package com.fourbeams.testtasktinkoff.NewsItemPOJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Payload {

    @SerializedName("title")
    @Expose
    private Title title;
    @SerializedName("creationDate")
    @Expose
    private CreationDate creationDate;
    @SerializedName("lastModificationDate")
    @Expose
    private LastModificationDate lastModificationDate;
    @SerializedName("content")
    @Expose
    private String content;
    @SerializedName("bankInfoTypeId")
    @Expose
    private Long bankInfoTypeId;
    @SerializedName("typeId")
    @Expose
    private String typeId;

    /**
     * 
     * @return
     *     The title
     */
    public Title getTitle() {
        return title;
    }

    /**
     * 
     * @param title
     *     The title
     */
    public void setTitle(Title title) {
        this.title = title;
    }

    /**
     * 
     * @return
     *     The creationDate
     */
    public CreationDate getCreationDate() {
        return creationDate;
    }

    /**
     * 
     * @param creationDate
     *     The creationDate
     */
    public void setCreationDate(CreationDate creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * 
     * @return
     *     The lastModificationDate
     */
    public LastModificationDate getLastModificationDate() {
        return lastModificationDate;
    }

    /**
     * 
     * @param lastModificationDate
     *     The lastModificationDate
     */
    public void setLastModificationDate(LastModificationDate lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
    }

    /**
     * 
     * @return
     *     The content
     */
    public String getContent() {
        return content;
    }

    /**
     * 
     * @param content
     *     The content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * 
     * @return
     *     The bankInfoTypeId
     */
    public Long getBankInfoTypeId() {
        return bankInfoTypeId;
    }

    /**
     * 
     * @param bankInfoTypeId
     *     The bankInfoTypeId
     */
    public void setBankInfoTypeId(Long bankInfoTypeId) {
        this.bankInfoTypeId = bankInfoTypeId;
    }

    /**
     * 
     * @return
     *     The typeId
     */
    public String getTypeId() {
        return typeId;
    }

    /**
     * 
     * @param typeId
     *     The typeId
     */
    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

}
