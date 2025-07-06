package awesome.pizza.service;

import awesome.pizza.config.AwesomePizzaException;
import awesome.pizza.config.ErrorEnum;
import awesome.pizza.model.dto.*;
import awesome.pizza.model.entities.OrderStatus;
import awesome.pizza.model.entities.PizzaOrder;
import awesome.pizza.model.entities.PizzaOrderItem;
import awesome.pizza.model.entities.PizzaRecipe;
import awesome.pizza.repository.IPizzaOrderRepository;
import awesome.pizza.repository.IPizzaRecipeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrdersService {

    private final IPizzaOrderRepository orderRepository;
    private final IPizzaRecipeRepository pizzaRecipeRepository;

    public PizzaOrderResponse getOrderStatus(String orderCode) {
        if (!OrderCodeProvider.orderCodeIsValid(orderCode)) {
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

    @Transactional
    public PizzaOrderResponse makeNewOrder(NewPizzaOrderDto newPizzaOrderDto) {
        String orderCode = OrderCodeProvider.generateOrderCode();
        PizzaOrder pizzaOrder = new PizzaOrder();
        pizzaOrder.setCode(orderCode);
        pizzaOrder.setOrderStatus(OrderStatus.SUBMITTED);
        for (NewPizzaOrderItemDto newPizzaOrderItemDto : newPizzaOrderDto.getPizzaOrderItems()) {
            PizzaRecipe pizzaRecipe = pizzaRecipeRepository.findById(newPizzaOrderItemDto.getPizzaRecipeId())
                    .orElseThrow(() -> new AwesomePizzaException(ErrorEnum.PIZZA_RECIPE_NOT_FOUND));
            PizzaOrderItem pizzaOrderItem = new PizzaOrderItem();
            pizzaOrderItem.setPizzaRecipe(pizzaRecipe);
            pizzaOrderItem.setDoughType(newPizzaOrderItemDto.getDoughType());
            pizzaOrderItem.setPrice(pizzaRecipe.getDefaultPrice());
            pizzaOrderItem.setPizzaOrder(pizzaOrder);
            pizzaOrder.getPizzaOrderItems().add(pizzaOrderItem);
        }
        pizzaOrder.setPrice(pizzaOrder.getPizzaOrderItems().stream().mapToDouble(PizzaOrderItem::getPrice).sum());
        PizzaOrder savedPizzaOrder = orderRepository.save(pizzaOrder);
        return PizzaOrderResponse.builder()
                .pizzaOrder(new PizzaOrderDto(savedPizzaOrder))
                .responseStatus(AwesomePizzaResponseStatus.OK)
                .build();
    }


    public double computePizzaPrice(PizzaRecipe pizzaRecipe) {
        return pizzaRecipe.getDefaultPrice();
    }
}
