package awesome.pizza.config;

public enum ErrorEnum {

    INTERNAL_ERROR("Internal error"),
    DATA_INTEGRITY_VIOLATION("Data integrity violation"),
    INVALID_REQUEST("Invalid request"),
    ORDER_NOT_FOUND("Order not found"),
    PIZZA_RECIPE_NOT_FOUND("Pizza recipe not found"),
    ALREADY_PROCESSED_ORDER("Already processed order");

    private String message;


    ErrorEnum(String message) {
        this.setMessage(message);
    }

    public String getMessage() {
        return message;
    }

    private void setMessage(String message) {
        this.message = message;
    }
}
