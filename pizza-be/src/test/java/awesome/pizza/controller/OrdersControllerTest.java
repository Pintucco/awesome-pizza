package awesome.pizza.controller;

import awesome.pizza.TestSamples;
import awesome.pizza.model.dto.*;
import awesome.pizza.model.entities.DoughType;
import awesome.pizza.model.entities.OrderStatus;
import awesome.pizza.service.OrdersService;
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
    @DisplayName("Retrieve submitted order")
    void testGetOrderStatus1() throws Exception {

        String orderCode = "AWSPZ-12345678";
        PizzaOrderDto pizzaOrderDto = PizzaOrderDto.builder()
                .code(orderCode)
                .price(11.0)
                .pizzaRecipe(new PizzaRecipeDto(TestSamples.pizzaRecipeMargheritaSample()))
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
                .andExpect(jsonPath("$.pizzaOrder.pizzaRecipe.name").isNotEmpty())
                .andExpect(jsonPath("$.pizzaOrder.pizzaRecipe.description").isNotEmpty())
                .andExpect(jsonPath("$.pizzaOrder.pizzaRecipe.defaultPrice").isNotEmpty());

    }

    @Test
    @DisplayName("Retrieve submitted order with invalid code returns ORDER_NOT_FOUND")
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
    @DisplayName("Successfully make new order")
    void testMakeNewOrder1() throws Exception {

        NewOrderDto newOrderDto = new NewOrderDto();
        newOrderDto.setPizzaRecipeId(1L);
        newOrderDto.setDoughType(DoughType.STANDARD);

        String orderCode = "AWSPZ-12345678";
        PizzaOrderDto cratedOrder = PizzaOrderDto.builder()
                .code(orderCode)
                .price(7.0)
                .pizzaRecipe(new PizzaRecipeDto(TestSamples.pizzaRecipeMargheritaSample()))
                .orderStatus(OrderStatus.SUBMITTED)
                .build();

        PizzaOrderResponse response = PizzaOrderResponse.builder()
                .pizzaOrder(cratedOrder)
                .responseStatus(AwesomePizzaResponseStatus.OK)
                .build();


        when(ordersService.makeNewOrder(any(NewOrderDto.class))).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.post("/orders/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newOrderDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseStatus").value("OK"))
                .andExpect(jsonPath("$.pizzaOrder.orderStatus").value("SUBMITTED"))
                .andExpect(jsonPath("$.pizzaOrder.code").value(orderCode))
                .andExpect(jsonPath("$.pizzaOrder.price").value(7.0))
                .andExpect(jsonPath("$.pizzaOrder.pizzaRecipe.name").isNotEmpty())
                .andExpect(jsonPath("$.pizzaOrder.pizzaRecipe.description").isNotEmpty())
                .andExpect(jsonPath("$.pizzaOrder.pizzaRecipe.defaultPrice").isNotEmpty());
    }

    @Test
    @DisplayName("Fail making new order with missing pizza recipe id")
    void testMakeNewOrder2() throws Exception {
        NewOrderDto invalidDto = new NewOrderDto();
        mockMvc.perform(MockMvcRequestBuilders.post("/orders/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(ordersService);
    }


}
