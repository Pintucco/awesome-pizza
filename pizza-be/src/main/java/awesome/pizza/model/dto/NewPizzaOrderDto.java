package awesome.pizza.model.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class NewPizzaOrderDto implements Serializable {

    @NotEmpty
    private List<NewPizzaOrderItemDto> pizzaOrderItems = new ArrayList<>();
}
