package awesome.pizza.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorEnum {

    INTERNAL_ERROR("Internal error"),
    DATA_INTEGRITY_VIOLATION("Data integrity violation"),
    INVALID_REQUEST("Invalid request"),
    ORDER_NOT_FOUND("Order not found"),
    PIZZA_RECIPE_NOT_FOUND("Pizza recipe not found"),
    ALREADY_PROCESSED_ORDER("Already processed order");

    private final String message;

}
