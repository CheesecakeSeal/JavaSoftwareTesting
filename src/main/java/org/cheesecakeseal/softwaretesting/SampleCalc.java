package org.cheesecakeseal.softwaretesting;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

public class SampleCalc {
    public static void main(String[] args) {
        runCalculator(System.in, System.out);
    }

    public static void runCalculator(InputStream input, PrintStream output) {
        Scanner sc = new Scanner(input);
        double num1 = 0;
        double num2 = 0;
        double result = 0;
        char operator = '0';
        boolean success = false;

        output.println("---Calculator---");

        // Get the first number as a valid double
        while (!success) {
            output.print("Enter first number: ");
            String inputStr = sc.nextLine();
            try {
                num1 = Double.parseDouble(inputStr);
                success = true;
            } catch (NumberFormatException e) {
                output.println("Invalid input. Please enter a valid number.");
            }
        }

        // Get the operator as a valid char
        success = false;
        while (!success) {
            output.print("Enter an operator (+, -, *, /): ");
            operator = sc.nextLine().charAt(0);
            if (operator == '+' || operator == '-' || operator == '*' || operator == '/') {
                success = true;
            } else {
                output.println("Invalid operator. Valid operators are +, -, *, /.");
            }
        }

        // Get the second number as a valid double
        success = false;
        while (!success) {
            output.print("Enter second number: ");
            String inputStr = sc.nextLine();
            try {
                num2 = Double.parseDouble(inputStr);
                success = true;
            } catch (NumberFormatException e) {
                output.println("Invalid input. Please enter a valid number.");
            }
        }

        // Perform the calculation
        switch (operator) {
            case '+':
                result = num1 + num2;
                break;
            case '-':
                result = num1 - num2;
                break;
            case '*':
                result = num1 * num2;
                break;
            case '/':
                if (num2 != 0) {
                    result = num1 / num2;
                } else {
                    output.println("Error: Division by zero is not allowed.");
                    sc.close();
                    return;
                }
                break;
            default:
                output.println("Invalid action.");
                sc.close();
                return;
        }

        // Display result
        output.println("The result is: " + result);

        sc.close();
    }
}


