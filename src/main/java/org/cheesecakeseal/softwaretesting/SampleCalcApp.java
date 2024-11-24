package org.cheesecakeseal.softwaretesting;

import io.javalin.Javalin;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;

public class SampleCalcApp {
    public static void main(String[] args) {
        // Create and start the Javalin server
        Javalin app = Javalin.create(config -> {
            // Register CORS plugin to allow requests from all origins
            config.plugins.enableCors(cors -> cors.add(it -> it.anyHost()));
        }).start(7000);

        // Define routes
        app.get("/", ctx -> ctx.result("Welcome to SampleCalcApp! Use /calculate with POST to perform operations."));

        app.post("/calculate", SampleCalcApp::handleCalculation);

        // Global exception handler for invalid JSON or malformed requests
        app.exception(BadRequestResponse.class, (e, ctx) -> {
            ctx.status(400).json(new ErrorResponse("Invalid request: " + e.getMessage()));
        });

        // Global exception handler for unexpected exceptions
        app.exception(Exception.class, (e, ctx) -> {
            ctx.status(500).json(new ErrorResponse("An unexpected error occurred: " + e.getMessage()));
        });
    }

    /**
     * Handles calculation requests by processing input, validating, and calculating results.
     *
     * @param ctx Context of the HTTP request
     */
    public static void handleCalculation(Context ctx) {
        try {
            // Parse input from the request body as JSON
            CalculationRequest request = ctx.bodyAsClass(CalculationRequest.class);

            // Perform validation and computation
            double result = calculate(request);

            // Respond with the result
            ctx.json(new CalculationResponse(result));
        } catch (IllegalArgumentException e) {
            // Handle validation errors (e.g., division by zero or invalid operator)
            ctx.status(400).json(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Validates the input and performs the calculation.
     *
     * @param request The parsed calculation request
     * @return The result of the calculation
     * @throws IllegalArgumentException for invalid input or operations
     */
    private static double calculate(CalculationRequest request) {
        validateOperator(request.getOperator());
        return performCalculation(request.getNum1(), request.getNum2(), request.getOperator());
    }

    /**
     * Validates the operator provided in the request.
     *
     * @param operator The operator to validate
     */
    private static void validateOperator(char operator) {
        if ("+-*/".indexOf(operator) == -1) {
            throw new IllegalArgumentException("Invalid operator. Valid operators are +, -, *, /.");
        }
    }

    /**
     * Performs the arithmetic operation based on the operator.
     *
     * @param num1     The first number
     * @param num2     The second number
     * @param operator The operator to apply
     * @return The result of the operation
     */
    private static double performCalculation(double num1, double num2, char operator) {
        switch (operator) {
            case '+': return num1 + num2;
            case '-': return num1 - num2;
            case '*': return num1 * num2;
            case '/':
                if (num2 == 0) throw new IllegalArgumentException("Division by zero is not allowed.");
                return num1 / num2;
            default:
                throw new IllegalArgumentException("Invalid operator. Valid operators are +, -, *, /.");
        }
    }

    // Request class for deserializing JSON inputs
    public static class CalculationRequest {
        private double num1;
        private double num2;
        private char operator;

        // Getters and Setters (needed for deserialization)
        public double getNum1() { return num1; }
        public void setNum1(double num1) { this.num1 = num1; }
        public double getNum2() { return num2; }
        public void setNum2(double num2) { this.num2 = num2; }
        public char getOperator() { return operator; }
        public void setOperator(char operator) { this.operator = operator; }
    }

    // Response class for sending results as JSON
    public static class CalculationResponse {
        private double result;

        public CalculationResponse(double result) {
            this.result = result;
        }

        public double getResult() { return result; }
    }

    // Error response class for sending error messages
    public static class ErrorResponse {
        private String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() { return error; }
    }
}






