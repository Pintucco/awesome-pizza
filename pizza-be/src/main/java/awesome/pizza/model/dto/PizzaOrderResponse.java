package awesome.pizza.model.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Builder
@Data
public class PizzaOrderResponse implements Serializable {

    private PizzaOrderDto pizzaOrder;
    private AwesomePizzaResponseStatus responseStatus;

}
