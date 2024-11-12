package com.example.farrerpark;

import java.util.Date;

public class PatientDet {
    private String patientName;
    private int age;
    private String gender;
    private String mrn;
    private String admissionId;
    private String studyNumber;
    private Date studyDate;
    private String studyDateStr;
    private String dateOfBirth;
    private int noOfPg;

    // Getters and setters for each field
    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getMrn() { return mrn; }
    public void setMrn(String mrn) { this.mrn = mrn; }

    public String getAdmissionId() { return admissionId; }
    public void setAdmissionId(String admissionId) { this.admissionId = admissionId; }

    public String getStudyNumber() { return studyNumber; }
    public void setStudyNumber(String studyNumber) { this.studyNumber = studyNumber; }

    public Date getStudyDate() { return studyDate; }
    public void setStudyDate(Date studyDate) { this.studyDate = studyDate; }

    public String getStudyDateStr() { return studyDateStr; }
    public void setStudyDateStr(String studyDateStr) { this.studyDateStr = studyDateStr; }

    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public int getNoOfPg() { return noOfPg; }
    public void setNoOfPg(int noOfPg) { this.noOfPg = noOfPg; }

    @Override
    public String toString() {
        return "Parsed Patient Details:\n" +
                "Patient Name: " + (patientName != null ? patientName : "N/A") + "\n" +
                "Age: " + (age > 0 ? age : "N/A") + "\n" +
                "Gender: " + (gender != null ? gender : "N/A") + "\n" +
                "MRN: " + (mrn != null ? mrn : "N/A") + "\n" +
                "Admission ID: " + (admissionId != null ? admissionId : "N/A") + "\n" +
                "Study Number: " + (studyNumber != null ? studyNumber : "N/A") + "\n" +
                "Study Date: " + (studyDateStr != null ? studyDateStr : "N/A") + "\n" +
                "DOB: " + (dateOfBirth != null ? dateOfBirth : "N/A") + "\n" +
                "No Of Pages: " + (noOfPg > 0 ? noOfPg : "N/A");
    }
}

