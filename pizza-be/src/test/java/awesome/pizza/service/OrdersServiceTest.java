package awesome.pizza.service;

import awesome.pizza.config.AwesomePizzaException;
import awesome.pizza.config.ErrorEnum;
import awesome.pizza.model.dto.AwesomePizzaResponseStatus;
import awesome.pizza.model.dto.NewOrderDto;
import awesome.pizza.model.dto.PizzaOrderResponse;
import awesome.pizza.model.dto.PizzaRecipeDto;
import awesome.pizza.model.entities.DoughType;
import awesome.pizza.model.entities.OrderStatus;
import awesome.pizza.model.entities.PizzaOrder;
import awesome.pizza.model.entities.PizzaRecipe;
import awesome.pizza.repository.IPizzaOrderRepository;
import awesome.pizza.repository.IPizzaRecipeRepository;
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
        Long pizzaRecipeId=1L;
        NewOrderDto newOrderDto = new NewOrderDto();
        newOrderDto.setDoughType(DoughType.GLUTEN_FREE);
        newOrderDto.setPizzaRecipeId(pizzaRecipeId);

        PizzaRecipe recipe = new PizzaRecipe();
        recipe.setId(pizzaRecipeId);
        recipe.setDefaultPrice(7.0);

        PizzaOrder savedOrder = new PizzaOrder();
        savedOrder.setCode("CODICE_ORDINE");
        savedOrder.setPizzaRecipe(recipe);
        savedOrder.setOrderStatus(OrderStatus.SUBMITTED);
        savedOrder.setDoughType(DoughType.GLUTEN_FREE);
        savedOrder.setPrice(7.0);

        when(pizzaRecipeRepository.findById(pizzaRecipeId)).thenReturn(Optional.of(recipe));
        when(pizzaOrderRepository.save(any(PizzaOrder.class))).thenReturn(savedOrder);

        PizzaOrderResponse result = ordersService.makeNewOrder(newOrderDto);

        assertEquals(AwesomePizzaResponseStatus.OK, result.getResponseStatus());
        assertEquals(DoughType.GLUTEN_FREE, result.getPizzaOrder().getDoughType());
        assertEquals(OrderStatus.SUBMITTED, result.getPizzaOrder().getOrderStatus());
        assertEquals(7.0, result.getPizzaOrder().getPrice());
        assertEquals(new PizzaRecipeDto(recipe), result.getPizzaOrder().getPizzaRecipe());
    }

    @Test
    @DisplayName("Make new order with dismissed pizza throws exception")
    void createOrder2() {
        Long pizzaRecipeId=1L;
        NewOrderDto newOrderDto = new NewOrderDto();
        newOrderDto.setPizzaRecipeId(pizzaRecipeId);

        when(pizzaRecipeRepository.findById(pizzaRecipeId)).thenReturn(Optional.empty());

        AwesomePizzaException exception = assertThrows(AwesomePizzaException.class,
                () -> ordersService.makeNewOrder(newOrderDto));

        assertEquals(ErrorEnum.PIZZA_RECIPE_NOT_FOUND.name(), exception.getCode());
    }

    @Test
    @DisplayName("Monitor order status: submitted")
    void monitorOrder1() {
        String orderCode="AWSPZ-12345678";

        PizzaRecipe recipe = new PizzaRecipe();
        recipe.setId(1L);
        recipe.setDefaultPrice(7.0);

        PizzaOrder savedOrder = new PizzaOrder();
        savedOrder.setCode(orderCode);
        savedOrder.setPizzaRecipe(recipe);
        savedOrder.setOrderStatus(OrderStatus.SUBMITTED);
        savedOrder.setDoughType(DoughType.GLUTEN_FREE);
        savedOrder.setPrice(7.0);

        when(pizzaOrderRepository.findByCode(any(String.class))).thenReturn(Optional.of(savedOrder));

        PizzaOrderResponse result = ordersService.getOrderStatus(orderCode);

        assertEquals(AwesomePizzaResponseStatus.OK, result.getResponseStatus());
        assertEquals(DoughType.GLUTEN_FREE, result.getPizzaOrder().getDoughType());
        assertEquals(OrderStatus.SUBMITTED, result.getPizzaOrder().getOrderStatus());
        assertEquals(7.0, result.getPizzaOrder().getPrice());
        assertEquals(new PizzaRecipeDto(recipe), result.getPizzaOrder().getPizzaRecipe());
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