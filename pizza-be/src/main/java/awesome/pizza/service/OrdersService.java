package awesome.pizza.service;

import awesome.pizza.config.AwesomePizzaException;
import awesome.pizza.config.ErrorEnum;
import awesome.pizza.model.dto.NewOrderDto;
import awesome.pizza.model.dto.OrderResponseStatus;
import awesome.pizza.model.dto.OrderStatusDto;
import awesome.pizza.model.entities.OrderStatus;
import awesome.pizza.model.entities.PizzaOrder;
import awesome.pizza.model.entities.PizzaRecipe;
import awesome.pizza.repository.IOrderRepository;
import awesome.pizza.repository.IPizzaRecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrdersService {

    private final IOrderRepository orderRepository;
    private final IPizzaRecipeRepository pizzaRecipeRepository;

    public OrderStatusDto getOrderStatus(String orderCode) {
        Optional<PizzaOrder> orderOpt = orderRepository.findByCode(orderCode.trim().toUpperCase());
        return orderOpt.map(pizzaOrder -> OrderStatusDto.builder()
                        .pizzaOrder(pizzaOrder)
                        .orderResponseStatus(OrderResponseStatus.OK)
                        .build())
                .orElse(OrderStatusDto.builder()
                        .orderResponseStatus(OrderResponseStatus.ORDER_NOT_FOUND)
                        .build());
    }

    public OrderStatusDto makeNewOrder(NewOrderDto newOrderDto) {
        PizzaRecipe pizzaRecipe = pizzaRecipeRepository.findById(newOrderDto.getPizzaRecipeId())
                .orElseThrow(() -> new AwesomePizzaException(ErrorEnum.PIZZA_RECIPE_NOT_FOUND));
        PizzaOrder pizzaOrder = new PizzaOrder();
        String orderCode= generateOrderCode();
        pizzaOrder.setCode(orderCode);
        pizzaOrder.setOrderStatus(OrderStatus.SUBMITTED);
        pizzaOrder.setPizzaRecipe(pizzaRecipe);
        pizzaOrder.setDoughType(newOrderDto.getDoughType());
        pizzaOrder.setPrice(computePizzaPrice(pizzaRecipe));
        PizzaOrder savedPizzaOrder = orderRepository.save(pizzaOrder);
        return OrderStatusDto.builder().pizzaOrder(savedPizzaOrder).orderResponseStatus(OrderResponseStatus.OK).build();
    }

    public static String generateOrderCode(){
        return UUID.randomUUID().toString().trim().toUpperCase();
    }

    public double computePizzaPrice(PizzaRecipe pizzaRecipe){
        return pizzaRecipe.getDefaultPrice();
    }
}
