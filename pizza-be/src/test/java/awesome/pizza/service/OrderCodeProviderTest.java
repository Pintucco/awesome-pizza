package awesome.pizza.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OrderCodeProviderTest {

    @Test
    @DisplayName("generateOrderCode returns valid code ")
    void testGenerateOrderCode() {
        String code = OrderCodeProvider.generateOrderCode();
        assertNotNull(code);
        assertTrue(code.matches(OrderCodeProvider.ORDER_CODE_REGEX));
    }

    @Test
    @DisplayName("Valid order codes")
    void testOrderCodeIsValid1() {
        assertTrue(OrderCodeProvider.orderCodeIsValid("AWSPZ-ABCDEFG1"));
        assertTrue(OrderCodeProvider.orderCodeIsValid("AWSPZ-ABt5EFG1"));
        assertTrue(OrderCodeProvider.orderCodeIsValid("awspz-12345678"));
        assertTrue(OrderCodeProvider.orderCodeIsValid("  AWSPZ-1A2B3C4D  "));
    }

    @Test
    @DisplayName("Invalid order codes")
    void testOrderCodeIsValid_Invalid() {
        assertFalse(OrderCodeProvider.orderCodeIsValid(null));
        assertFalse(OrderCodeProvider.orderCodeIsValid(""));
        assertFalse(OrderCodeProvider.orderCodeIsValid("         "));
        assertFalse(OrderCodeProvider.orderCodeIsValid("AWSPZ-a"));
        assertFalse(OrderCodeProvider.orderCodeIsValid("dino-abcd1234"));
        assertFalse(OrderCodeProvider.orderCodeIsValid("AWSP12345678"));
        assertFalse(OrderCodeProvider.orderCodeIsValid("AWSPZ-1234567$"));
        assertFalse(OrderCodeProvider.orderCodeIsValid("AWSP-12345678"));
    }
}