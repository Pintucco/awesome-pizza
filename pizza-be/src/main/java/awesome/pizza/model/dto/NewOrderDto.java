package awesome.pizza.model.dto;

import awesome.pizza.model.entities.DoughType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
public class NewOrderDto implements Serializable {

    @NotNull(message = "Pizza Recipe must be provided")
    private Long pizzaRecipeId;
    @NotNull
    private DoughType doughType = DoughType.STANDARD;
    @NotNull
    private Set<Long> additionalIngredients = new HashSet<>();


}
