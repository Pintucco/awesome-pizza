package awesome.pizza.service.restaurant;

import awesome.pizza.model.dto.PizzaRecipeDto;
import awesome.pizza.model.entities.PizzaRecipe;
import awesome.pizza.repository.IPizzaRecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PizzaRecipeService {

    private  final IPizzaRecipeRepository pizzaRecipeRepository;

    public List<PizzaRecipeDto> getAllActiveRecipes(){
        return  pizzaRecipeRepository.findByActiveTrue().stream()
                .map(PizzaRecipeDto::new)
                .sorted(Comparator.comparing(PizzaRecipeDto::getName))
                .toList();
    }


    public PizzaRecipeDto newPizzaRecipe(PizzaRecipeDto pizzaRecipeDto) {
        PizzaRecipe pizzaRecipe = new PizzaRecipe();
        pizzaRecipe.setName(pizzaRecipeDto.getName());
        pizzaRecipe.setDescription(pizzaRecipeDto.getDescription());
        pizzaRecipe.setDefaultPrice(pizzaRecipeDto.getDefaultPrice());
        PizzaRecipe savedPizza = pizzaRecipeRepository.save(pizzaRecipe);
        return new PizzaRecipeDto(savedPizza);
    }


}
