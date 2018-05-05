package com.doubeye.experiments.dataAnalyze.eEnrollment;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.Date;

public class StudentInfo {
    private int studentId;
    private int age;
    private int cityId = 0;
    private int facultyId = 0;
    private int eduLevel;
    private String addTime;
    private String firstEnrollmentDate;
    private int feedbackCount;
    private JSONObject feedbackDetails = new JSONObject();
    private JSONArray feedbackContents = new JSONArray();
    private int enrollLevel;

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public int getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(int facultyId) {
        this.facultyId = facultyId;
    }

    public int getEduLevel() {
        return eduLevel;
    }

    public void setEduLevel(int eduLevel) {
        this.eduLevel = eduLevel;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public String getFirstEnrollmentDate() {
        return firstEnrollmentDate;
    }

    public void setFirstEnrollmentDate(String firstEnrollmentDate) {
        this.firstEnrollmentDate = firstEnrollmentDate;
    }

    public int getFeedbackCount() {
        return feedbackCount;
    }

    public void setFeedbackCount(int feedbackCount) {
        this.feedbackCount = feedbackCount;
    }

    public JSONObject getFeedbackDetails() {
        return feedbackDetails;
    }

    public void setFeedbackDetails(JSONObject feedbackDetails) {
        this.feedbackDetails = feedbackDetails;
    }

    public JSONArray getFeedbackContents() {
        return feedbackContents;
    }

    public void setFeedbackContents(JSONArray feedbackContents) {
        this.feedbackContents = feedbackContents;
    }

    public int getEnrollLevel() {
        return enrollLevel;
    }

    public void setEnrollLevel(int enrollLevel) {
        this.enrollLevel = enrollLevel;
    }
}
