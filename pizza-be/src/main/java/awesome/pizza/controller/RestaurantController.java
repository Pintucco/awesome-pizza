package awesome.pizza.controller;

import awesome.pizza.model.dto.OrderResponseStatus;
import awesome.pizza.model.dto.OrderStatusDto;
import awesome.pizza.service.RestaurantService;
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
    public OrderStatusDto nextOrder() {
        return restaurantService.getNextOrder().map(
                order -> OrderStatusDto.builder()
                        .pizzaOrder(order)
                        .orderResponseStatus(OrderResponseStatus.OK)
                        .build()
        ).orElse(OrderStatusDto.builder()
                .orderResponseStatus(OrderResponseStatus.EMPTY_QUEUE)
                .build());
    }

    @PostMapping(value = "/accept/{orderId}", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public OrderStatusDto acceptOrder(@NotNull @PathVariable Long orderId) {
        return restaurantService.acceptOrder(orderId);
    }

    @PostMapping(value = "/refuse/{orderId}", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public OrderStatusDto refuseOrder(@NotNull @PathVariable Long orderId) {
        return restaurantService.refuseOrder(orderId);
    }

}
