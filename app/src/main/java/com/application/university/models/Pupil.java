package com.application.university.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ashish on 30/12/17.
 */

public class Pupil {

    @SerializedName("email")
    private String email;
    @SerializedName("full_name")
    private String fullName;
    @SerializedName("points")
    private Integer points;
    @SerializedName("bio")
    private Object bio;
    @SerializedName("user_type")
    private String userType;
    @SerializedName("dp")
    private String dp;
    @SerializedName("auth_token")
    private String authToken;
    @SerializedName("fb_user")
    private Boolean fbUser;
    @SerializedName("skills")
    private List<Object> skills = null;
    @SerializedName("categories")
    private List<Object> categories = null;
    @SerializedName("is_host")
    private Boolean isHost;
    @SerializedName("password")
    private String password;

    public Pupil() {

    }

    public Pupil(User user) {
        this.email = user.getEmail();
        this.fullName = user.getFullName();
        this.points = user.getPoints();
        this.bio = user.getBio();
        this.userType = user.getUserType();
        this.dp = user.getDp();
        this.authToken = user.getAuthToken();
        this.fbUser = user.getFbUser();
        this.isHost = user.isHost();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Object getBio() {
        return bio;
    }

    public void setBio(Object bio) {
        this.bio = bio;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getDp() {
        return dp;
    }

    public void setDp(String dp) {
        this.dp = dp;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public Boolean getFbUser() {
        return fbUser;
    }

    public void setFbUser(Boolean fbUser) {
        this.fbUser = fbUser;
    }

    public List<Object> getSkills() {
        return skills;
    }

    public void setSkills(List<Object> skills) {
        this.skills = skills;
    }

    public List<Object> getCategories() {
        return categories;
    }

    public void setCategories(List<Object> categories) {
        this.categories = categories;
    }

    public Boolean getIsHost() {
        return isHost;
    }

    public void setIsHost(Boolean isHost) {
        this.isHost = isHost;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}