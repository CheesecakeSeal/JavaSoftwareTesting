import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.StringContains.containsString;

class SampleCalcAppIntegrationTest {

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 7000; // Ensure the server is running on this port
    }

    @Test
    void testCalculatorAddition() {
        given()
                .contentType(ContentType.JSON)
                .body("{ \"num1\": 5, \"num2\": 3, \"operator\": \"+\" }")
                .when()
                .post("/calculate")
                .then()
                .statusCode(200)
                .body("result", equalTo(8.0f));
    }

    @Test
    void testCalculatorDivisionByZero() {
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
    void testUnexpectedError() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"num1\": \"abc\", \"num2\": 5, \"operator\": \"+\"}")
                .when()
                .post("/calculate")
                .then()
                .statusCode(500) // Assuming 500 for invalid input
                .body("error", containsString("Cannot deserialize value of type `double` from String \"abc\""));
    }

}

