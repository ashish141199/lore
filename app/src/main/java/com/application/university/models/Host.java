package com.application.university.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ashish on 30/12/17.
 */

public class Host {

    @SerializedName("pupil")
    private Pupil pupil;
    @SerializedName("specializes")
    private List<Object> specializes = null;
    @SerializedName("h_categories")
    private List<Object> hCategories = null;
    @SerializedName("approved_by")
    private Integer approvedBy;

    public Pupil getPupil() {
        return pupil;
    }

    public void setPupil(Pupil pupil) {
        this.pupil = pupil;
    }

    public List<Object> getSpecializes() {
        return specializes;
    }

    public void setSpecializes(List<Object> specializes) {
        this.specializes = specializes;
    }

    public List<Object> getHCategories() {
        return hCategories;
    }

    public void setHCategories(List<Object> hCategories) {
        this.hCategories = hCategories;
    }

    public Integer getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(Integer approvedBy) {
        this.approvedBy = approvedBy;
    }

}