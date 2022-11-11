package com.example.project.model;

public class Step {

    private int stepNumber;
    private String stepDescription;

    public Step(int stepNumber, String stepDescription) {
        this.stepNumber = stepNumber;
        this.stepDescription = stepDescription;
    }

    public int getStepNumber() {
        return stepNumber;
    }

    public String getStepDescription() {
        return stepDescription;
    }

    public void setStepNumber(int stepNumber) {
        this.stepNumber = stepNumber;
    }

    public void setStepDescription(String stepDescription) {
        this.stepDescription = stepDescription;
    }

    @Override
    public String toString() {
        return "Step{" +
                "stepNumber=" + stepNumber +
                ", stepDescription='" + stepDescription + '\'' +
                '}';
    }

}
