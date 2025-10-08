package com.healthcare.skindetector.models;

public class Diagnosis {
    private int diagnosisId;
    private int patientId;
    private String diseasePrediction;
    private double confidenceScore;
    private String imagePath;

    public Diagnosis() {}

    public int getDiagnosisId() {
        return diagnosisId;
    }

    public void setDiagnosisId(int diagnosisId) {
        this.diagnosisId = diagnosisId;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public String getDiseasePrediction() {
        return diseasePrediction;
    }

    public void setDiseasePrediction(String diseasePrediction) {
        this.diseasePrediction = diseasePrediction;
    }

    public double getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(double confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
