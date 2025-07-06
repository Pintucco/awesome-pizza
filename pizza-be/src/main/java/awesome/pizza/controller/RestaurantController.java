package awesome.pizza.controller;

import awesome.pizza.model.dto.AwesomePizzaResponseStatus;
import awesome.pizza.model.dto.PizzaOrderDto;
import awesome.pizza.model.dto.PizzaOrderResponse;
import awesome.pizza.service.restaurant.RestaurantService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("restaurant")
public class RestaurantController {

    private final RestaurantService restaurantService;

    @GetMapping(value = "/next", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public PizzaOrderResponse nextOrder() {
        return restaurantService.getNextOrder().map(
                order -> PizzaOrderResponse.builder()
                        .pizzaOrder(new PizzaOrderDto(order))
                        .responseStatus(AwesomePizzaResponseStatus.OK)
                        .build()
        ).orElse(PizzaOrderResponse.builder()
                .responseStatus(AwesomePizzaResponseStatus.EMPTY_QUEUE)
                .build());
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

}
