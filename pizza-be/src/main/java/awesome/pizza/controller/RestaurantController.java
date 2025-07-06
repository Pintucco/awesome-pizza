package awesome.pizza.controller;

import awesome.pizza.model.dto.*;
import awesome.pizza.service.restaurant.PizzaRecipeService;
import awesome.pizza.service.restaurant.RestaurantService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("restaurant")
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final PizzaRecipeService pizzaRecipeService;

    @GetMapping(value = "/next", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public PizzaOrderResponse nextOrder() {
        return restaurantService.getNextOrder().map(
                order -> PizzaOrderResponse.builder()
                        .pizzaOrder(order)
                        .responseStatus(AwesomePizzaResponseStatus.OK)
                        .build()
        ).orElse(PizzaOrderResponse.builder()
                .responseStatus(AwesomePizzaResponseStatus.EMPTY_QUEUE)
                .build());
    }

    @GetMapping(value = "/orders-in-progress", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public PizzaOrdersResponse getOrdersInProgress() {
        List<PizzaOrderDto> ordersInProgress = restaurantService.getOrdersInProgress();
        return PizzaOrdersResponse.builder()
                .pizzaOrders(ordersInProgress)
                .responseStatus(AwesomePizzaResponseStatus.OK)
                .build();
    }

    @PostMapping(value = "/accept/{orderId}", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public PizzaOrderResponse acceptOrder(@NotNull @PathVariable Long orderId) {
        return restaurantService.acceptOrder(orderId);
    }

    @PostMapping(value = "/refuse/{orderId}", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public PizzaOrderResponse refuseOrder(@NotNull @PathVariable Long orderId) {
        return restaurantService.refuseOrder(orderId);
    }

    @PostMapping(value = "/conclude/{orderId}", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public PizzaOrderResponse concludeOrder(@NotNull @PathVariable Long orderId) {
        return restaurantService.concludeOrder(orderId);
    }

    @PostMapping(value = "/new-pizza-recipe", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public PizzaRecipeDto getOrderStatus(@NotNull @Valid @RequestBody PizzaRecipeDto pizzaRecipeDto) {
        return pizzaRecipeService.newPizzaRecipe(pizzaRecipeDto);
    }

}
