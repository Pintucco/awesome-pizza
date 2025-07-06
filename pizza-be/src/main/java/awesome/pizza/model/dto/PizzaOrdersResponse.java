package awesome.pizza.model.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Builder
@Data
public class PizzaOrdersResponse implements Serializable {

    private List<PizzaOrderDto> pizzaOrders = new ArrayList<>();
    private AwesomePizzaResponseStatus responseStatus;

}