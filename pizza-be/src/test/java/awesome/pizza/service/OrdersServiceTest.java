package awesome.pizza.service;

import awesome.pizza.config.AwesomePizzaException;
import awesome.pizza.config.ErrorEnum;
import awesome.pizza.model.dto.AwesomePizzaResponseStatus;
import awesome.pizza.model.dto.NewPizzaOrderDto;
import awesome.pizza.model.dto.NewPizzaOrderItemDto;
import awesome.pizza.model.dto.PizzaOrderResponse;
import awesome.pizza.model.entities.DoughType;
import awesome.pizza.model.entities.OrderStatus;
import awesome.pizza.model.entities.PizzaOrder;
import awesome.pizza.repository.IPizzaOrderRepository;
import awesome.pizza.repository.IPizzaRecipeRepository;
import awesome.pizza.service.orders.OrdersService;
import awesome.pizza.utils.TestSamples;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrdersServiceTest {

    @Mock
    private IPizzaOrderRepository pizzaOrderRepository;

    @Mock
    private IPizzaRecipeRepository pizzaRecipeRepository;

    @InjectMocks
    private OrdersService ordersService;

    @Test
    @DisplayName("Make new order successfully")
    void createOrder1() {

        NewPizzaOrderItemDto newPizzaOrderItemDto1 = new NewPizzaOrderItemDto();
        newPizzaOrderItemDto1.setDoughType(DoughType.GLUTEN_FREE);
        newPizzaOrderItemDto1.setPizzaRecipeId(TestSamples.pizzaRecipeMargheritaSample().getId());

        NewPizzaOrderItemDto newPizzaOrderItemDto2 = new NewPizzaOrderItemDto();
        newPizzaOrderItemDto2.setDoughType(DoughType.STANDARD);
        newPizzaOrderItemDto2.setPizzaRecipeId(TestSamples.pizzaRecipeMargheritaSample().getId());

        NewPizzaOrderDto newPizzaOrderDto = new NewPizzaOrderDto();
        newPizzaOrderDto.getPizzaOrderItems().add(newPizzaOrderItemDto1);
        newPizzaOrderDto.getPizzaOrderItems().add(newPizzaOrderItemDto2);

        PizzaOrder savedOrder = new PizzaOrder();
        savedOrder.setCode("CODICE_ORDINE");
        savedOrder.setOrderStatus(OrderStatus.SUBMITTED);
        savedOrder.setPrice(7.0);

        when(pizzaRecipeRepository.findById(TestSamples.pizzaRecipeMargheritaSample().getId())).thenReturn(Optional.of(TestSamples.pizzaRecipeMargheritaSample()));
        when(pizzaOrderRepository.save(any(PizzaOrder.class))).thenReturn(savedOrder);

        PizzaOrderResponse result = ordersService.makeNewOrder(newPizzaOrderDto);

        assertEquals(AwesomePizzaResponseStatus.OK, result.getResponseStatus());
        assertEquals(OrderStatus.SUBMITTED, result.getPizzaOrder().getOrderStatus());
        assertEquals(7.0, result.getPizzaOrder().getPrice());
    }

    @Test
    @DisplayName("Make new order with dismissed pizza throws exception")
    void createOrder2() {
        Long pizzaRecipeId=1L;
        NewPizzaOrderItemDto newPizzaOrderItemDto = new NewPizzaOrderItemDto();
        newPizzaOrderItemDto.setPizzaRecipeId(pizzaRecipeId);
        NewPizzaOrderDto newPizzaOrderDto = new NewPizzaOrderDto();
        newPizzaOrderDto.getPizzaOrderItems().add(newPizzaOrderItemDto);

        when(pizzaRecipeRepository.findById(pizzaRecipeId)).thenReturn(Optional.empty());

        AwesomePizzaException exception = assertThrows(AwesomePizzaException.class,
                () -> ordersService.makeNewOrder(newPizzaOrderDto));

        assertEquals(ErrorEnum.PIZZA_RECIPE_NOT_FOUND.name(), exception.getCode());
    }

    @Test
    @DisplayName("Monitor order status: submitted")
    void monitorOrder1() {
        String orderCode="AWSPZ-12345678";

        PizzaOrder savedOrder = new PizzaOrder();
        savedOrder.setCode(orderCode);
        savedOrder.setOrderStatus(OrderStatus.SUBMITTED);
        savedOrder.setPrice(7.0);

        when(pizzaOrderRepository.findByCode(any(String.class))).thenReturn(Optional.of(savedOrder));

        PizzaOrderResponse result = ordersService.getOrderStatus(orderCode);

        assertEquals(AwesomePizzaResponseStatus.OK, result.getResponseStatus());
        assertEquals(OrderStatus.SUBMITTED, result.getPizzaOrder().getOrderStatus());
        assertEquals(7.0, result.getPizzaOrder().getPrice());
    }

    @Test
    @DisplayName("Monitor order status: invalid code->query is not performed")
    void monitorOrder2() {
        String orderCode="abc123'%20OR%20'1'%3D'1";

        verify(pizzaOrderRepository, never()).findByCode(any());

        PizzaOrderResponse result = ordersService.getOrderStatus(orderCode);

        assertEquals(AwesomePizzaResponseStatus.ORDER_NOT_FOUND, result.getResponseStatus());
        assertNull( result.getPizzaOrder());
    }

    @Test
    @DisplayName("Monitor order status: wrong code")
    void monitorOrder3() {
        String orderCode="AWSPZ-12345678";

        when(pizzaOrderRepository.findByCode(any(String.class))).thenReturn(Optional.empty());

        PizzaOrderResponse result = ordersService.getOrderStatus(orderCode);

        verify(pizzaOrderRepository).findByCode(any());

        assertEquals(AwesomePizzaResponseStatus.ORDER_NOT_FOUND, result.getResponseStatus());
        assertNull( result.getPizzaOrder());
    }

}