import org.cheesecakeseal.softwaretesting.SampleCalc;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SampleCalcUnitTest {

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outputStream));
    }


    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    private void provideInput(String data) {
        System.setIn(new ByteArrayInputStream(data.getBytes()));
    }

    // Addition Test
    @Test
    public void testAddition() {
        provideInput("5\n+\n3\n");
        SampleCalc.main(new String[]{});
        assertTrue(outputStream.toString().contains("The result is: 8.0"));
    }

    // Subtraction Test
    @Test
    public void testSubtraction() {
        provideInput("10\n-\n4\n");
        SampleCalc.main(new String[]{});
        assertTrue(outputStream.toString().contains("The result is: 6.0"));
    }

    // Multiplication Test
    @Test
    public void testMultiplication() {
        provideInput("6\n*\n7\n");
        SampleCalc.main(new String[]{});
        assertTrue(outputStream.toString().contains("The result is: 42.0"));
    }

    // Division Test
    @Test
    public void testDivision() {
        provideInput("20\n/\n5\n");
        SampleCalc.main(new String[]{});
        assertTrue(outputStream.toString().contains("The result is: 4.0"));
    }

    // Invalid Operator Check
    @Test
    public void testInvalidOperator() {
        provideInput("10\n%\n+\n3\n");
        SampleCalc.main(new String[]{});
        assertTrue(outputStream.toString().contains("Invalid operator. Valid operators are +, -, *, /."));
    }

    // Division by Zero Check
    @Test
    public void testDivisionByZero() {
        provideInput("10\n/\n0\n");
        SampleCalc.main(new String[]{});
        assertTrue(outputStream.toString().contains("Error: Division by zero is not allowed."));
    }

    // Invalid First Number Check
    @Test
    public void testInvalidFirstNumber() {
        provideInput("abc\n10\n+\n5\n");
        SampleCalc.main(new String[]{});
        assertTrue(outputStream.toString().contains("Invalid input. Please enter a valid number."));
    }

    // Invalid Second Number Check
    @Test
    public void testInvalidSecondNumber() {
        provideInput("10\n+\nabc\n5\n");
        SampleCalc.main(new String[]{});
        assertTrue(outputStream.toString().contains("Invalid input. Please enter a valid number."));
    }
}

