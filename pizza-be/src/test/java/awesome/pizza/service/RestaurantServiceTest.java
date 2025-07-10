package awesome.pizza.service;

import awesome.pizza.config.AwesomePizzaException;
import awesome.pizza.config.ErrorEnum;
import awesome.pizza.model.dto.PizzaOrderDto;
import awesome.pizza.model.dto.PizzaOrderResponse;
import awesome.pizza.model.entities.AwesomePizzaUser;
import awesome.pizza.model.entities.OrderStatus;
import awesome.pizza.model.entities.PizzaOrder;
import awesome.pizza.repository.IPizzaOrderRepository;
import awesome.pizza.service.restaurant.RestaurantService;
import awesome.pizza.service.security.AuthenticationFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

class RestaurantServiceTest {

    @Mock
    private IPizzaOrderRepository orderRepository;

    @Mock
    private AuthenticationFacade authenticationFacade;

    @InjectMocks
    private RestaurantService restaurantService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        AwesomePizzaUser mockLoggedUser = new AwesomePizzaUser();
        mockLoggedUser.setId(1L);
        mockLoggedUser.setUsername("mario");
        mockLoggedUser.setPassword("pizza");
        when(authenticationFacade.getLoggedUser()).thenReturn(mockLoggedUser);
    }

    @Test
    @DisplayName("get next order returns older submitted order")
    void getNextOrder() {
        PizzaOrder pizzaOrder1 = new PizzaOrder();
        pizzaOrder1.setId(2L);
        pizzaOrder1.setOrderStatus(OrderStatus.SUBMITTED);

        PizzaOrder pizzaOrder2 = new PizzaOrder();
        pizzaOrder2.setId(1L);
        pizzaOrder2.setOrderStatus(OrderStatus.SUBMITTED);

        when(orderRepository.findByOrderStatus(OrderStatus.SUBMITTED)).thenReturn(List.of(pizzaOrder1, pizzaOrder2));

        Optional<PizzaOrderDto> result = restaurantService.getNextOrder();

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    @DisplayName("Get orders in progress")
    void getOrdersInProgress() {
        PizzaOrder pizzaOrder1 = new PizzaOrder();
        pizzaOrder1.setId(5L);
        pizzaOrder1.setOrderStatus(OrderStatus.PIZZA_IN_PROGRESS);

        PizzaOrder pizzaOrder2 = new PizzaOrder();
        pizzaOrder2.setId(3L);
        pizzaOrder2.setOrderStatus(OrderStatus.PIZZA_IN_PROGRESS);

        when(orderRepository.findByOrderStatus(OrderStatus.PIZZA_IN_PROGRESS)).thenReturn(List.of(pizzaOrder1, pizzaOrder2));

        List<PizzaOrderDto> result = restaurantService.getOrdersInProgress();

        assertEquals(2, result.size());
        assertEquals(3L, result.get(0).getId());
        assertEquals(5L, result.get(1).getId());
    }

    @Test
    @DisplayName("Successfully accept order")
    void acceptOrder1() {
        PizzaOrder order = new PizzaOrder();
        order.setId(11L);
        order.setOrderStatus(OrderStatus.SUBMITTED);

        when(orderRepository.findById(11L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PizzaOrderResponse response = restaurantService.acceptOrder(11L);

        assertEquals(OrderStatus.PIZZA_IN_PROGRESS, response.getPizzaOrder().getOrderStatus());
        assertEquals("OK", response.getResponseStatus().name());
    }

    @Test
    @DisplayName("Failing accepting in progress order")
    void acceptOrder2() {
        PizzaOrder order = new PizzaOrder();
        order.setId(11L);
        order.setOrderStatus(OrderStatus.PIZZA_IN_PROGRESS);

        when(orderRepository.findById(11L)).thenReturn(Optional.of(order));

        AwesomePizzaException exception = assertThrows(AwesomePizzaException.class,
                () -> restaurantService.acceptOrder(11L));

        assertEquals(ErrorEnum.ALREADY_PROCESSED_ORDER.name(), exception.getCode());
    }


    @Test
    @DisplayName("Successfully refuse order")
    void refuseOrder1() {
        PizzaOrder order = new PizzaOrder();
        order.setId(11L);
        order.setOrderStatus(OrderStatus.SUBMITTED);

        when(orderRepository.findById(11L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PizzaOrderResponse response = restaurantService.refuseOrder(11L);

        assertEquals(OrderStatus.REFUSED, response.getPizzaOrder().getOrderStatus());
        assertEquals("OK", response.getResponseStatus().name());
    }


    @Test
    @DisplayName("Failing refusing concluded order")
    void refuseOrder2() {
        PizzaOrder order = new PizzaOrder();
        order.setId(11L);
        order.setOrderStatus(OrderStatus.CONCLUDED);

        when(orderRepository.findById(11L)).thenReturn(Optional.of(order));

        AwesomePizzaException exception = assertThrows(AwesomePizzaException.class,
                () -> restaurantService.acceptOrder(11L));

        assertEquals(ErrorEnum.ALREADY_PROCESSED_ORDER.name(), exception.getCode());
    }


    @Test
    @DisplayName("Successfully conclude order")
    void concludeOrder1() {
        PizzaOrder order = new PizzaOrder();
        order.setId(11L);
        order.setOrderStatus(OrderStatus.PIZZA_IN_PROGRESS);

        when(orderRepository.findById(11L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PizzaOrderResponse response = restaurantService.concludeOrder(11L);

        assertEquals(OrderStatus.CONCLUDED, response.getPizzaOrder().getOrderStatus());
        assertEquals("OK", response.getResponseStatus().name());
    }

    @Test
    @DisplayName("failing concluding not worked order")
    void concludeOrder2() {
        PizzaOrder order = new PizzaOrder();
        order.setId(11L);
        order.setOrderStatus(OrderStatus.SUBMITTED);

        when(orderRepository.findById(11L)).thenReturn(Optional.of(order));

        AwesomePizzaException exception = assertThrows(AwesomePizzaException.class,
                () -> restaurantService.concludeOrder(11L));

        assertEquals(ErrorEnum.INVALID_ORDER_STATUS_FOR_CONCLUSION.name(), exception.getCode());
    }

    @Test
    @DisplayName("failing concluding not existing order")
    void concludeOrder3() {

        when(orderRepository.findById(11L)).thenReturn(Optional.empty());

        AwesomePizzaException exception = assertThrows(AwesomePizzaException.class,
                () -> restaurantService.concludeOrder(11L));

        assertEquals(ErrorEnum.ORDER_NOT_FOUND.name(), exception.getCode());
    }
}


