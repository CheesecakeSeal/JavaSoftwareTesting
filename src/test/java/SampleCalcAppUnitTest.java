import org.cheesecakeseal.softwaretesting.SampleCalcApp;

import io.javalin.Javalin;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

class SampleCalcAppUnitTest {

    private static Javalin app;

    @BeforeAll
    static void setUp() {
        app = Javalin.create(config -> {
            config.plugins.enableCors(cors -> cors.add(it -> it.anyHost()));
        }).start(0); // Bind to an available port
        RestAssured.port = app.port(); // Get the dynamically assigned port for RestAssured
        app.get("/", ctx -> ctx.result("Welcome to SampleCalcApp! Use /calculate with POST to perform operations."));
        app.post("/calculate", SampleCalcApp::handleCalculation);
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
                .body("{ \"num1\": \"abc\", \"num2\": 5, \"operator\": \"+\" }")
                .when()
                .post("/calculate")
                .then()
                .statusCode(500)
                .body("error", equalTo("An unexpected error occurred: For input string: \"abc\""));
    }

}

