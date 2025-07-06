package awesome.pizza.model.dto;

import awesome.pizza.model.entities.DoughType;
import awesome.pizza.model.entities.PizzaOrderItem;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@Data
@RequiredArgsConstructor
@Builder
public class PizzaOrderItemDto implements Serializable {

    @NotNull
    private final PizzaRecipeDto pizzaRecipe;
    @NotNull
    private final DoughType doughType;
    @NotNull
    private final Double price;

    public PizzaOrderItemDto(PizzaOrderItem pizzaOrderItem) {
        pizzaRecipe = new PizzaRecipeDto(pizzaOrderItem.getPizzaRecipe());
        doughType = pizzaOrderItem.getDoughType();
        price = pizzaOrderItem.getPrice();
    }


}
