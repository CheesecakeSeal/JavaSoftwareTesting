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

    public static void handleCalculation(Context ctx) {
        try {
            CalculationRequest request = ctx.bodyAsClass(CalculationRequest.class);
            double result = calculate(request);
            ctx.json(new CalculationResponse(result));
        } catch (IllegalArgumentException e) {
            ctx.status(400).json(new ErrorResponse(e.getMessage()));
        }
    }

    private static double calculate(CalculationRequest request) {
        validateOperator(request.getOperator());
        return performCalculation(request.getNum1(), request.getNum2(), request.getOperator());
    }

    private static void validateOperator(char operator) {
        if ("+-*/".indexOf(operator) == -1) {
            throw new IllegalArgumentException("Invalid operator. Valid operators are +, -, *, /.");
        }
    }

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

    public static class CalculationRequest {
        private double num1;
        private double num2;
        private char operator;

        public double getNum1() { return num1; }
        public void setNum1(double num1) { this.num1 = num1; }
        public double getNum2() { return num2; }
        public void setNum2(double num2) { this.num2 = num2; }
        public char getOperator() { return operator; }
        public void setOperator(char operator) { this.operator = operator; }
    }

    public static class CalculationResponse {
        private final double result;

        public CalculationResponse(double result) {
            this.result = result;
        }

        public double getResult() { return result; }
    }

    public static class ErrorResponse {
        private final String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() { return error; }
    }
}







