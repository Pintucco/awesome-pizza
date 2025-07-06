package awesome.pizza.service.restaurant;

import awesome.pizza.config.AwesomePizzaException;
import awesome.pizza.config.ErrorEnum;
import awesome.pizza.model.dto.AwesomePizzaResponseStatus;
import awesome.pizza.model.dto.PizzaOrderDto;
import awesome.pizza.model.dto.PizzaOrderResponse;
import awesome.pizza.model.entities.OrderStatus;
import awesome.pizza.model.entities.PizzaOrder;
import awesome.pizza.repository.IPizzaOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final IPizzaOrderRepository orderRepository;

    public Optional<PizzaOrderDto> getNextOrder() {
        return orderRepository.findByOrderStatus(OrderStatus.SUBMITTED).stream()
                .min(Comparator.comparing(PizzaOrder::getId))
                .map(PizzaOrderDto::new);
    }

    public List<PizzaOrderDto> getOrdersInProgress() {
        return orderRepository.findByOrderStatus(OrderStatus.PIZZA_IN_PROGRESS).stream()
                .sorted(Comparator.comparing(PizzaOrder::getId))
                .map(PizzaOrderDto::new)
                .toList();
    }

    public PizzaOrderResponse acceptOrder(Long orderId) {
        PizzaOrder pizzaOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new AwesomePizzaException(ErrorEnum.ORDER_NOT_FOUND));
        if (!pizzaOrder.getOrderStatus().equals(OrderStatus.SUBMITTED)) {
            throw new AwesomePizzaException(ErrorEnum.ALREADY_PROCESSED_ORDER);
        }
        pizzaOrder.setOrderStatus(OrderStatus.PIZZA_IN_PROGRESS);
        PizzaOrder updatedPizzaOrder = orderRepository.save(pizzaOrder);
        return PizzaOrderResponse.builder()
                .pizzaOrder(new PizzaOrderDto(updatedPizzaOrder))
                .responseStatus(AwesomePizzaResponseStatus.OK)
                .build();
    }

    public PizzaOrderResponse refuseOrder(Long orderId) {
        PizzaOrder pizzaOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new AwesomePizzaException(ErrorEnum.ORDER_NOT_FOUND));
        if (!pizzaOrder.getOrderStatus().equals(OrderStatus.SUBMITTED)) {
            throw new AwesomePizzaException(ErrorEnum.ALREADY_PROCESSED_ORDER);
        }
        pizzaOrder.setOrderStatus(OrderStatus.REFUSED);
        PizzaOrder updatedPizzaOrder = orderRepository.save(pizzaOrder);
        return PizzaOrderResponse.builder()
                .pizzaOrder(new PizzaOrderDto(updatedPizzaOrder))
                .responseStatus(AwesomePizzaResponseStatus.OK)
                .build();
    }

    public PizzaOrderResponse concludeOrder(Long orderId) {
        PizzaOrder pizzaOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new AwesomePizzaException(ErrorEnum.ORDER_NOT_FOUND));
        if (!pizzaOrder.getOrderStatus().equals(OrderStatus.PIZZA_IN_PROGRESS)) {
            throw new AwesomePizzaException(ErrorEnum.INVALID_ORDER_STATUS_FOR_CONCLUSION);
        }
        pizzaOrder.setOrderStatus(OrderStatus.CONCLUDED);
        PizzaOrder updatedPizzaOrder = orderRepository.save(pizzaOrder);
        return PizzaOrderResponse.builder()
                .pizzaOrder(new PizzaOrderDto(updatedPizzaOrder))
                .responseStatus(AwesomePizzaResponseStatus.OK)
                .build();
    }

}
