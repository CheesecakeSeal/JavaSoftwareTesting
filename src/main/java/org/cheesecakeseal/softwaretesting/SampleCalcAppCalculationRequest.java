package org.cheesecakeseal.softwaretesting;
// THIS IS A HELPER CLASS FOR SampleCalcApp
// Request object
public class SampleCalcAppCalculationRequest {
    private double num1;
    private double num2;
    private char operator;

    public double getNum1() {
        return num1;
    }

    public void setNum1(double num1) {
        this.num1 = num1;
    }

    public double getNum2() {
        return num2;
    }

    public void setNum2(double num2) {
        this.num2 = num2;
    }

    public char getOperator() {
        return operator;
    }

    public void setOperator(char operator) {
        this.operator = operator;
    }
}

