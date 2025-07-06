package awesome.pizza.service;

import awesome.pizza.config.AwesomePizzaException;
import awesome.pizza.config.ErrorEnum;
import awesome.pizza.model.dto.AwesomePizzaResponseStatus;
import awesome.pizza.model.dto.NewOrderDto;
import awesome.pizza.model.dto.PizzaOrderDto;
import awesome.pizza.model.dto.PizzaOrderResponse;
import awesome.pizza.model.entities.OrderStatus;
import awesome.pizza.model.entities.PizzaOrder;
import awesome.pizza.model.entities.PizzaRecipe;
import awesome.pizza.repository.IPizzaOrderRepository;
import awesome.pizza.repository.IPizzaRecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrdersService {

    private final IPizzaOrderRepository orderRepository;
    private final IPizzaRecipeRepository pizzaRecipeRepository;

    public PizzaOrderResponse getOrderStatus(String orderCode) {
        if(!OrderCodeProvider.orderCodeIsValid(orderCode)){
            return PizzaOrderResponse.builder()
                    .responseStatus(AwesomePizzaResponseStatus.ORDER_NOT_FOUND)
                    .build();
        }
        Optional<PizzaOrder> orderOpt = orderRepository.findByCode(orderCode.trim().toUpperCase());
        return orderOpt.map(pizzaOrder -> PizzaOrderResponse.builder()
                        .pizzaOrder(new PizzaOrderDto(pizzaOrder))
                        .responseStatus(AwesomePizzaResponseStatus.OK)
                        .build())
                .orElse(PizzaOrderResponse.builder()
                        .responseStatus(AwesomePizzaResponseStatus.ORDER_NOT_FOUND)
                        .build());
    }

    public PizzaOrderResponse makeNewOrder(NewOrderDto newOrderDto) {
        PizzaRecipe pizzaRecipe = pizzaRecipeRepository.findById(newOrderDto.getPizzaRecipeId())
                .orElseThrow(() -> new AwesomePizzaException(ErrorEnum.PIZZA_RECIPE_NOT_FOUND));
        PizzaOrder pizzaOrder = new PizzaOrder();
        String orderCode= OrderCodeProvider.generateOrderCode();
        pizzaOrder.setCode(orderCode);
        pizzaOrder.setOrderStatus(OrderStatus.SUBMITTED);
        pizzaOrder.setPizzaRecipe(pizzaRecipe);
        pizzaOrder.setDoughType(newOrderDto.getDoughType());
        pizzaOrder.setPrice(computePizzaPrice(pizzaRecipe));
        PizzaOrder savedPizzaOrder = orderRepository.save(pizzaOrder);
        return PizzaOrderResponse.builder()
                .pizzaOrder(new PizzaOrderDto(savedPizzaOrder))
                .responseStatus(AwesomePizzaResponseStatus.OK)
                .build();
    }



    public double computePizzaPrice(PizzaRecipe pizzaRecipe){
        return pizzaRecipe.getDefaultPrice();
    }
}
