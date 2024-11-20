package org.cheesecakeseal.softwaretesting;

import io.javalin.Javalin;
import io.javalin.http.Context;

public class SampleCalcApp {
    public static void main(String[] args) {
        // Create and start the Javalin server
        Javalin app = Javalin.create(config -> {
            // Register CORS plugin to allow requests from all origins
            config.plugins.enableCors(cors -> cors.add(it -> it.anyHost()));
        }).start(7000);

        // Define routes
        app.get("/", ctx -> ctx.result("Calculator App!"));

        app.post("/calculate", SampleCalcApp::handleCalculation);
    }

    private static void handleCalculation(Context ctx) {
        try {
            // Parse input from the request body
            SampleCalcAppCalculationRequest request = ctx.bodyAsClass(SampleCalcAppCalculationRequest.class);

            // Perform calculation
            double result = performCalculation(request.getNum1(), request.getNum2(), request.getOperator());

            // Respond with the result
            ctx.json(new SampleCalcAppCalculationResponse(result));
        } catch (IllegalArgumentException e) {
            ctx.status(400).result(e.getMessage());
        }
    }

    private static double performCalculation(double num1, double num2, char operator) {
        return switch (operator) {
            case '+' -> num1 + num2;
            case '-' -> num1 - num2;
            case '*' -> num1 * num2;
            case '/' -> {
                if (num2 == 0) {
                    throw new IllegalArgumentException("Division by zero is not allowed.");
                }
                yield num1 / num2;
            }
            default -> throw new IllegalArgumentException("Invalid operator: " + operator);
        };
    }
}

