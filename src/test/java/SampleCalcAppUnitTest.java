import org.cheesecakeseal.softwaretesting.SampleCalcApp;

import io.javalin.Javalin;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.StringContains.containsString;

class SampleCalcAppUnitTest {

    private static Javalin app;

    @BeforeAll
    static void setUp() {
        // Start Javalin app
        app = Javalin.create(config -> {
            config.plugins.enableCors(cors -> cors.add(it -> it.anyHost()));
        }).start(0); // Bind to an available port

        // Set RestAssured port to match the app's port
        RestAssured.port = app.port();

        // Register custom routes
        app.get("/", ctx -> ctx.result("Welcome to SampleCalcApp! Use /calculate with POST to perform operations."));
        app.post("/calculate", SampleCalcApp::handleCalculation);

        // Register a custom parser for text/plain responses
        RestAssured.registerParser("text/plain", Parser.TEXT);
    }

    @AfterAll
    static void tearDown() {
        if (app != null) {
            app.stop();
        }
    }

    @Test
    void testAddition() {
        given()
                .contentType(ContentType.JSON)
                .body("{ \"num1\": 10, \"num2\": 5, \"operator\": \"+\" }")
                .when()
                .post("/calculate")
                .then()
                .statusCode(200)
                .body("result", equalTo(15.0f));
    }

    @Test
    void testSubtraction() {
        given()
                .contentType(ContentType.JSON)
                .body("{ \"num1\": 10, \"num2\": 5, \"operator\": \"-\" }")
                .when()
                .post("/calculate")
                .then()
                .statusCode(200)
                .body("result", equalTo(5.0f));
    }

    @Test
    void testMultiplication() {
        given()
                .contentType(ContentType.JSON)
                .body("{ \"num1\": 10, \"num2\": 5, \"operator\": \"*\" }")
                .when()
                .post("/calculate")
                .then()
                .statusCode(200)
                .body("result", equalTo(50.0f));
    }

    @Test
    void testDivision() {
        given()
                .contentType(ContentType.JSON)
                .body("{ \"num1\": 10, \"num2\": 5, \"operator\": \"/\" }")
                .when()
                .post("/calculate")
                .then()
                .statusCode(200)
                .body("result", equalTo(2.0f));
    }

    @Test
    void testDivisionByZero() {
        given()
                .contentType(ContentType.JSON)
                .body("{ \"num1\": 10, \"num2\": 0, \"operator\": \"/\" }")
                .when()
                .post("/calculate")
                .then()
                .statusCode(400)
                .body("error", equalTo("Division by zero is not allowed."));
    }

    @Test
    void testInvalidOperator() {
        given()
                .contentType(ContentType.JSON)
                .body("{ \"num1\": 10, \"num2\": 5, \"operator\": \"%\" }")
                .when()
                .post("/calculate")
                .then()
                .statusCode(400)
                .body("error", equalTo("Invalid operator. Valid operators are +, -, *, /."));
    }

    @Test
    void testUnexpectedError() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"num1\": \"abc\", \"num2\": 5, \"operator\": \"+\"}")
                .when()
                .post("/calculate")
                .then()
                .statusCode(500) // Validate the status code
                .contentType(ContentType.TEXT) // Ensure the response is 'text/plain'
                .body(containsString("Server Error")); // Validate the error message in the body
    }

}


