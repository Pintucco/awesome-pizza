package awesome.pizza.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PizzaRecipes implements Serializable {

    private List<PizzaRecipeDto> pizzaRecipesList = new ArrayList<>();
}
