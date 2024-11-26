import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.StringContains.containsString;

class SampleCalcAppIntegrationTest {

    //Setup method to configure the base URI and port for RestAssured.
    @BeforeAll
    static void setup() {
        RestAssured.baseURI = "http://localhost"; // API server base URL
        RestAssured.port = 7000; // Default server port for the application
    }

    // Addition Test. This is required to see if the app works as expected
    @Test
    void testCalculatorAddition() {
        given()
                .contentType(ContentType.JSON)
                .body("{ \"num1\": 5, \"num2\": 3, \"operator\": \"+\" }") // Request payload
                .when()
                .post("/calculate") // API endpoint
                .then()
                .statusCode(200) // Expect HTTP 200 OK
                .body("result", equalTo(8.0f)); // Verify the response result is 8.0
    }

    // Divsion by Zero check
    @Test
    void testCalculatorDivisionByZero() {
        given()
                .contentType(ContentType.JSON)
                .body("{ \"num1\": 10, \"num2\": 0, \"operator\": \"/\" }") // Request with division by zero
                .when()
                .post("/calculate")
                .then()
                .statusCode(400) // Expect HTTP 400 Bad Request
                .body("error", equalTo("Division by zero is not allowed.")); // Verify the error message
    }

    // Unexpected Error Check
    @Test
    void testUnexpectedError() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"num1\": \"abc\", \"num2\": 5, \"operator\": \"+\"}") // Invalid input for num1
                .when()
                .post("/calculate")
                .then()
                .statusCode(500) // Expect HTTP 500 Internal Server Error
                .body("error", containsString("Cannot deserialize value of type `double` from String \"abc\"")); // Error details
    }
}


