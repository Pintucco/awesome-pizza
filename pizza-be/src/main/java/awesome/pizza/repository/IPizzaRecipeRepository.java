package awesome.pizza.repository;

import awesome.pizza.model.entities.PizzaRecipe;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface IPizzaRecipeRepository  extends CrudRepository<PizzaRecipe, Long> {

    List<PizzaRecipe> findByActiveTrue();
}