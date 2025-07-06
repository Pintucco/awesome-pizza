package awesome.pizza.model.dto;

import awesome.pizza.model.entities.PizzaRecipe;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;


@Data
@RequiredArgsConstructor
@Builder
public class PizzaRecipeDto implements Serializable {

    private final Long id;

    @NotNull
    @NotBlank
    private final String name;

    @NotNull
    @NotBlank
    private final String description;

    @NotNull
    private final Double defaultPrice;

    public PizzaRecipeDto(PizzaRecipe pizzaRecipe) {
        id = pizzaRecipe.getId();
        name = pizzaRecipe.getName();
        description = pizzaRecipe.getDescription();
        defaultPrice = pizzaRecipe.getDefaultPrice();
    }

}
