package awesome.pizza.controller;

import awesome.pizza.model.dto.PizzaRecipeDto;
import awesome.pizza.model.dto.PizzaRecipes;
import awesome.pizza.service.restaurant.PizzaRecipeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("recipes")
public class PizzaRecipeController {

    private  final PizzaRecipeService pizzaRecipeService;

    @GetMapping(value = "/all", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public PizzaRecipes getAllRecipes() {
        return new PizzaRecipes(pizzaRecipeService.getAllActiveRecipes());
    }

    @PostMapping(value = "/new", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public PizzaRecipeDto getOrderStatus(@NotNull @Valid @RequestBody PizzaRecipeDto pizzaRecipeDto) {
        return pizzaRecipeService.newPizzaRecipe(pizzaRecipeDto);
    }

}
