package com.example.d_fir_login.Model;

public class Case {

    private String CaseId;
    private String CaseCategory;
    private String OfficerName;
    private String TimeStamp;

    public Case(){

    }

    public Case(String caseId, String caseCategory, String officerName, String timeStamp) {
        CaseId = caseId;
        CaseCategory = caseCategory;
        OfficerName = officerName;
        TimeStamp = timeStamp;
    }

    public String getCaseId() {
        return CaseId;
    }

    public void setCaseId(String caseId) {
        CaseId = caseId;
    }

    public String getCaseCategory() {
        return CaseCategory;
    }

    public void setCaseCategory(String caseCategory) {
        CaseCategory = caseCategory;
    }

    public String getOfficerName() {
        return OfficerName;
    }

    public void setOfficerName(String officerName) {
        OfficerName = officerName;
    }

    public String getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        TimeStamp = timeStamp;
    }
}
