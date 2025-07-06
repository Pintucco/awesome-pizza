package awesome.pizza;

import awesome.pizza.model.entities.PizzaRecipe;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TestSamples {


    public PizzaRecipe pizzaRecipeMargheritaSample() {
        PizzaRecipe pizzaRecipe = new PizzaRecipe();
        pizzaRecipe.setId(2L);
        pizzaRecipe.setName("Margherita");
        pizzaRecipe.setDescription("Pomodoro, mozzarella, origano");
        pizzaRecipe.setDefaultPrice(5.0);
        return pizzaRecipe;
    }

    public PizzaRecipe pizzaRecipeMarinaraSample() {
        PizzaRecipe pizzaRecipe = new PizzaRecipe();
        pizzaRecipe.setId(2L);
        pizzaRecipe.setName("Marinara");
        pizzaRecipe.setDescription("Pomodoro, origano");
        pizzaRecipe.setDefaultPrice(4.0);
        return pizzaRecipe;
    }
}
