package awesome.pizza.model.dto;

import awesome.pizza.model.entities.PizzaOrder;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Builder
@Data
public class OrderStatusDto implements Serializable {

    private PizzaOrder pizzaOrder;
    private OrderResponseStatus orderResponseStatus;
}
