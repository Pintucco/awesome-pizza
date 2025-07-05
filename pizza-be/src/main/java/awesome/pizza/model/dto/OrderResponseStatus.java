package awesome.pizza.model.dto;

import java.io.Serializable;

public enum OrderResponseStatus implements Serializable {

    ORDER_NOT_FOUND,
    EMPTY_QUEUE,
    OK
}
