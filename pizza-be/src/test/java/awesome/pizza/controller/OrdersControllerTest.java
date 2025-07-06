package awesome.pizza.controller;

import awesome.pizza.TestSamples;
import awesome.pizza.model.dto.*;
import awesome.pizza.model.entities.DoughType;
import awesome.pizza.model.entities.OrderStatus;
import awesome.pizza.service.orders.OrdersService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrdersController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrdersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrdersService ordersService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /orders/monitor Retrieve submitted order")
    void testGetOrderStatus1() throws Exception {
        PizzaOrderItemDto pizzaOrderItemDto= PizzaOrderItemDto.builder()
                .pizzaRecipe(new PizzaRecipeDto(TestSamples.pizzaRecipeMargheritaSample()))
                .price(TestSamples.pizzaRecipeMargheritaSample().getDefaultPrice())
                .doughType(DoughType.STANDARD)
                .build();

        String orderCode = "AWSPZ-12345678";
        PizzaOrderDto pizzaOrderDto = PizzaOrderDto.builder()
                .code(orderCode)
                .price(11.0)
                .pizzaOrderItems(List.of(pizzaOrderItemDto))
                .build();
        PizzaOrderResponse pizzaOrderResponse = PizzaOrderResponse.builder()
                .responseStatus(AwesomePizzaResponseStatus.OK)
                .pizzaOrder(pizzaOrderDto)
                .build();
        when(ordersService.getOrderStatus(orderCode)).thenReturn(pizzaOrderResponse);

        mockMvc.perform(get("/orders/monitor/{code}", orderCode)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseStatus").value("OK"))
                .andExpect(jsonPath("$.pizzaOrder.orderStatus").value("SUBMITTED"))
                .andExpect(jsonPath("$.pizzaOrder.code").value(orderCode))
                .andExpect(jsonPath("$.pizzaOrder.price").value(11.0))
                .andExpect(jsonPath("$.pizzaOrder.pizzaOrderItems").isArray())
                .andExpect(jsonPath("$.pizzaOrder.pizzaOrderItems[0].price").value(5.0))
                .andExpect(jsonPath("$.pizzaOrder.pizzaOrderItems[0].doughType").value("STANDARD"))
                .andExpect(jsonPath("$.pizzaOrder.pizzaOrderItems[0].pizzaRecipe.name").value("Margherita"))
                .andExpect(jsonPath("$.pizzaOrder.pizzaOrderItems[0].pizzaRecipe.description").value("Pomodoro, mozzarella, origano"))
                .andExpect(jsonPath("$.pizzaOrder.pizzaOrderItems[0].pizzaRecipe.defaultPrice").value(5.0));

    }

    @Test
    @DisplayName("GET /orders/monitor  with invalid code returns ORDER_NOT_FOUND")
    void testGetOrderStatus2() throws Exception {

        String orderCode = "hello";
        PizzaOrderResponse pizzaOrderResponse = PizzaOrderResponse.builder()
                .responseStatus(AwesomePizzaResponseStatus.ORDER_NOT_FOUND)
                .build();
        when(ordersService.getOrderStatus(orderCode)).thenReturn(pizzaOrderResponse);

        mockMvc.perform(get("/orders/monitor/{code}", orderCode)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseStatus").value("ORDER_NOT_FOUND"))
                .andExpect(jsonPath("$.pizzaOrder").isEmpty());

    }




    @Test
    @DisplayName("POST /orders/new Successfully make new order")
    void testMakeNewOrder1() throws Exception {

        NewPizzaOrderItemDto newPizzaOrderItemDto = new NewPizzaOrderItemDto();
        newPizzaOrderItemDto.setPizzaRecipeId(1L);
        newPizzaOrderItemDto.setDoughType(DoughType.STANDARD);

        NewPizzaOrderDto newPizzaOrderDto = new NewPizzaOrderDto();
        newPizzaOrderDto.getPizzaOrderItems().add(newPizzaOrderItemDto);

        PizzaOrderItemDto pizzaOrderItemDto= PizzaOrderItemDto.builder()
                .pizzaRecipe(new PizzaRecipeDto(TestSamples.pizzaRecipeMargheritaSample()))
                .doughType(DoughType.STANDARD)
                .price(TestSamples.pizzaRecipeMargheritaSample().getDefaultPrice())
                .build();

        String orderCode = "AWSPZ-12345678";
        PizzaOrderDto cratedOrder = PizzaOrderDto.builder()
                .code(orderCode)
                .price(7.0)
                .orderStatus(OrderStatus.SUBMITTED)
                .pizzaOrderItems(List.of(pizzaOrderItemDto))
                .build();

        PizzaOrderResponse response = PizzaOrderResponse.builder()
                .pizzaOrder(cratedOrder)
                .responseStatus(AwesomePizzaResponseStatus.OK)
                .build();


        when(ordersService.makeNewOrder(any(NewPizzaOrderDto.class))).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.post("/orders/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPizzaOrderDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseStatus").value("OK"))
                .andExpect(jsonPath("$.pizzaOrder.orderStatus").value("SUBMITTED"))
                .andExpect(jsonPath("$.pizzaOrder.code").value(orderCode))
                .andExpect(jsonPath("$.pizzaOrder.price").value(7.0))
                .andExpect(jsonPath("$.pizzaOrder.pizzaOrderItems").isArray())
                .andExpect(jsonPath("$.pizzaOrder.pizzaOrderItems[0].price").value(5.0))
                .andExpect(jsonPath("$.pizzaOrder.pizzaOrderItems[0].doughType").value("STANDARD"))
                .andExpect(jsonPath("$.pizzaOrder.pizzaOrderItems[0].pizzaRecipe.name").value("Margherita"))
                .andExpect(jsonPath("$.pizzaOrder.pizzaOrderItems[0].pizzaRecipe.description").value("Pomodoro, mozzarella, origano"))
                .andExpect(jsonPath("$.pizzaOrder.pizzaOrderItems[0].pizzaRecipe.defaultPrice").value(5.0));

    }

    @Test
    @DisplayName("POST /orders/new Fail making new order with no pizzas")
    void testMakeNewOrder2() throws Exception {
        NewPizzaOrderDto invalidDto = new NewPizzaOrderDto();
        mockMvc.perform(MockMvcRequestBuilders.post("/orders/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(ordersService);
    }


}
