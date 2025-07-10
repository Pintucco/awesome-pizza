package awesome.pizza.model.dto;

import awesome.pizza.model.entities.OrderStatus;
import awesome.pizza.model.entities.PizzaOrder;
import awesome.pizza.service.orders.OrderCodeProvider;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@Builder
public class PizzaOrderDto implements Serializable {

    @NotNull
    private Long id;

    @Pattern(regexp = OrderCodeProvider.ORDER_CODE_REGEX, message = "Invalid code format")
    @NotNull
    private String code;

    @NotNull
    @Builder.Default
    private OrderStatus orderStatus = OrderStatus.SUBMITTED;

    @NotNull
    private Double price;

    @NotNull
    @Builder.Default
    private List<PizzaOrderItemDto> pizzaOrderItems = new ArrayList<>();


    private Instant submittedAt;

    private Instant acceptedRefusedAt;

    private Instant concludedAt;

    private String workedBy;


    public PizzaOrderDto(PizzaOrder pizzaOrder) {
        id = pizzaOrder.getId();
        code = pizzaOrder.getCode();
        orderStatus = pizzaOrder.getOrderStatus();
        price = pizzaOrder.getPrice();
        pizzaOrderItems = pizzaOrder.getPizzaOrderItems().stream().map(PizzaOrderItemDto::new)
                .collect(Collectors.toList());
        submittedAt = pizzaOrder.getSubmittedAt();
        acceptedRefusedAt = pizzaOrder.getAcceptedRefusedAt();
        workedBy = pizzaOrder.getWorkedBy() != null ? pizzaOrder.getWorkedBy().getUsername() : null;
    }
}
