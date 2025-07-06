package awesome.pizza.controller;

import awesome.pizza.model.dto.PizzaRecipes;
import awesome.pizza.service.restaurant.PizzaRecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("lookup")
public class LookupController {

    private  final PizzaRecipeService pizzaRecipeService;

    @GetMapping(value = "/all-available-pizzas", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public PizzaRecipes getAllAvailablePizzas() {
        return new PizzaRecipes(pizzaRecipeService.getAllActiveRecipes());
    }



}
