package awesome.pizza.service;

import awesome.pizza.config.AwesomePizzaException;
import awesome.pizza.config.ErrorEnum;
import awesome.pizza.model.dto.OrderResponseStatus;
import awesome.pizza.model.dto.OrderStatusDto;
import awesome.pizza.model.entities.OrderStatus;
import awesome.pizza.model.entities.PizzaOrder;
import awesome.pizza.repository.IOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final IOrderRepository orderRepository;

    public Optional<PizzaOrder> getNextOrder() {
        return orderRepository.findByOrderStatus(OrderStatus.SUBMITTED).stream()
                .min(Comparator.comparing(PizzaOrder::getId));
    }

    public OrderStatusDto acceptOrder(Long orderId) {
        PizzaOrder pizzaOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new AwesomePizzaException(ErrorEnum.ORDER_NOT_FOUND));
        if (!pizzaOrder.getOrderStatus().equals(OrderStatus.SUBMITTED)) {
            throw new AwesomePizzaException(ErrorEnum.ALREADY_PROCESSED_ORDER);
        }
        pizzaOrder.setOrderStatus(OrderStatus.PIZZA_IN_PROGRESS);
        PizzaOrder updatedPizzaOrder = orderRepository.save(pizzaOrder);
        return OrderStatusDto.builder()
                .pizzaOrder(updatedPizzaOrder)
                .orderResponseStatus(OrderResponseStatus.OK)
                .build();
    }

    public OrderStatusDto refuseOrder(Long orderId) {
        PizzaOrder pizzaOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new AwesomePizzaException(ErrorEnum.ORDER_NOT_FOUND));
        if (!pizzaOrder.getOrderStatus().equals(OrderStatus.SUBMITTED)) {
            throw new AwesomePizzaException(ErrorEnum.ALREADY_PROCESSED_ORDER);
        }
        pizzaOrder.setOrderStatus(OrderStatus.REFUSED);
        PizzaOrder updatedPizzaOrder = orderRepository.save(pizzaOrder);
        return OrderStatusDto.builder()
                .pizzaOrder(updatedPizzaOrder)
                .orderResponseStatus(OrderResponseStatus.OK)
                .build();
    }
}
