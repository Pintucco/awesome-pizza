package awesome.pizza.controller;

import awesome.pizza.model.dto.NewOrderDto;
import awesome.pizza.model.dto.PizzaOrderResponse;
import awesome.pizza.service.OrdersService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("orders")
public class OrdersController {

    private final OrdersService ordersService;

    @GetMapping(value = "/monitor/{code}", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public PizzaOrderResponse getOrderStatus(@NotNull @PathVariable String code) {
        return ordersService.getOrderStatus(code);
    }

    @PostMapping(value = "/new", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public PizzaOrderResponse makeNewOrder(@NotNull @Valid @RequestBody NewOrderDto newOrderDto) {
        return ordersService.makeNewOrder(newOrderDto);
    }
}
