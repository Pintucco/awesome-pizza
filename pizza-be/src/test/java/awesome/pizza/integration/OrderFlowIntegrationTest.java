package awesome.pizza.integration;

import awesome.pizza.model.dto.*;
import awesome.pizza.model.entities.DoughType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static awesome.pizza.model.entities.OrderStatus.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@Transactional
class OrderFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void monitorOrderWithInvalidCode() throws Exception {
        String orderCode = "hello";
        mockMvc.perform(get("/orders/monitor/{code}", orderCode)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseStatus").value("ORDER_NOT_FOUND"))
                .andExpect(jsonPath("$.pizzaOrder").isEmpty());
    }

    @Test
    void makeAnOrderAndMonitor() throws Exception {
        testGetNextOrderWithEmptyQueue();
        List<PizzaRecipeDto> availablePizzaRecipes = testFetchPizzaRecipes();
        String submittedOrderCode1 = testSubmitNewOrder(availablePizzaRecipes).toLowerCase()+"    ";
        testSubmittedOrderMonitoring(submittedOrderCode1);
        String submittedOrderCode2 = testSubmitNewOrder(availablePizzaRecipes);
        testSubmittedOrderMonitoring(submittedOrderCode2);
        Long nextOrderId = testGetNextOrderWithGivenCode(submittedOrderCode1);
        testOrderAcceptance(nextOrderId);
        testInProgressOrderMonitoring(submittedOrderCode1);
        testSubmittedOrderMonitoring(submittedOrderCode2);
        testOrdersInProgress(1);
        testOrderConclusion(nextOrderId);
        testConcludedMonitoring(submittedOrderCode1);
        testOrdersInProgress(0);
        nextOrderId = testGetNextOrderWithGivenCode(submittedOrderCode2);
        testOrderRefusal(nextOrderId);
        testOrdersInProgress(0);
        testRefusedMonitoring(submittedOrderCode2);
    }

    private void testSubmittedOrderMonitoring(String orderCode) throws Exception {
        MvcResult result = mockMvc.perform(get("/orders/monitor/{code}", orderCode)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String responseBody = result.getResponse().getContentAsString();
        PizzaOrderResponse pizzaOrderResponse = objectMapper.readValue(responseBody, PizzaOrderResponse.class);
        assertNotNull(pizzaOrderResponse);
        assertEquals(AwesomePizzaResponseStatus.OK, pizzaOrderResponse.getResponseStatus());
        PizzaOrderDto pizzaOrderDto = pizzaOrderResponse.getPizzaOrder();
        assertNotNull(pizzaOrderDto);
        assertEquals(SUBMITTED, pizzaOrderDto.getOrderStatus());
        assertEquals(2, pizzaOrderDto.getPizzaOrderItems().size());
    }
    private void testInProgressOrderMonitoring(String orderCode) throws Exception {
        MvcResult result = mockMvc.perform(get("/orders/monitor/{code}", orderCode)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String responseBody = result.getResponse().getContentAsString();
        PizzaOrderResponse pizzaOrderResponse = objectMapper.readValue(responseBody, PizzaOrderResponse.class);
        assertNotNull(pizzaOrderResponse);
        assertEquals(AwesomePizzaResponseStatus.OK, pizzaOrderResponse.getResponseStatus());
        PizzaOrderDto pizzaOrderDto = pizzaOrderResponse.getPizzaOrder();
        assertNotNull(pizzaOrderDto);
        assertEquals(PIZZA_IN_PROGRESS, pizzaOrderDto.getOrderStatus());
        assertEquals(2, pizzaOrderDto.getPizzaOrderItems().size());
    }

    private void testConcludedMonitoring(String orderCode) throws Exception {
        MvcResult result = mockMvc.perform(get("/orders/monitor/{code}", orderCode)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String responseBody = result.getResponse().getContentAsString();
        PizzaOrderResponse pizzaOrderResponse = objectMapper.readValue(responseBody, PizzaOrderResponse.class);
        assertNotNull(pizzaOrderResponse);
        assertEquals(AwesomePizzaResponseStatus.OK, pizzaOrderResponse.getResponseStatus());
        PizzaOrderDto pizzaOrderDto = pizzaOrderResponse.getPizzaOrder();
        assertNotNull(pizzaOrderDto);
        assertEquals(CONCLUDED, pizzaOrderDto.getOrderStatus());
        assertEquals(2, pizzaOrderDto.getPizzaOrderItems().size());
    }

    private void testRefusedMonitoring(String orderCode) throws Exception {
        MvcResult result = mockMvc.perform(get("/orders/monitor/{code}", orderCode)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String responseBody = result.getResponse().getContentAsString();
        PizzaOrderResponse pizzaOrderResponse = objectMapper.readValue(responseBody, PizzaOrderResponse.class);
        assertNotNull(pizzaOrderResponse);
        assertEquals(AwesomePizzaResponseStatus.OK, pizzaOrderResponse.getResponseStatus());
        PizzaOrderDto pizzaOrderDto = pizzaOrderResponse.getPizzaOrder();
        assertNotNull(pizzaOrderDto);
        assertEquals(REFUSED, pizzaOrderDto.getOrderStatus());
        assertEquals(2, pizzaOrderDto.getPizzaOrderItems().size());
    }

    private void testOrderAcceptance(Long orderId) throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/restaurant/accept/" + orderId))
                .andExpect(status().isOk())
                .andReturn();
        String responseBody = result.getResponse().getContentAsString();
        PizzaOrderResponse pizzaOrderResponse = objectMapper.readValue(responseBody, PizzaOrderResponse.class);
        assertNotNull(pizzaOrderResponse);
        assertEquals(AwesomePizzaResponseStatus.OK, pizzaOrderResponse.getResponseStatus());
        assertNotNull(pizzaOrderResponse.getPizzaOrder());
        PizzaOrderDto acceptedOrder = pizzaOrderResponse.getPizzaOrder();
        assertEquals(PIZZA_IN_PROGRESS, acceptedOrder.getOrderStatus());
        assertEquals(2, acceptedOrder.getPizzaOrderItems().size());
    }
    private void testOrderConclusion(Long orderId) throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/restaurant/conclude/" + orderId))
                .andExpect(status().isOk())
                .andReturn();
        String responseBody = result.getResponse().getContentAsString();
        PizzaOrderResponse pizzaOrderResponse = objectMapper.readValue(responseBody, PizzaOrderResponse.class);
        assertNotNull(pizzaOrderResponse);
        assertEquals(AwesomePizzaResponseStatus.OK, pizzaOrderResponse.getResponseStatus());
        assertNotNull(pizzaOrderResponse.getPizzaOrder());
        PizzaOrderDto concludedOrder = pizzaOrderResponse.getPizzaOrder();
        assertEquals(CONCLUDED, concludedOrder.getOrderStatus());
        assertEquals(2, concludedOrder.getPizzaOrderItems().size());
    }
    private void testOrdersInProgress(int ordersInProgressNumber) throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/restaurant/orders-in-progress"))
                .andExpect(status().isOk())
                .andReturn();
        String responseBody = result.getResponse().getContentAsString();
        PizzaOrdersResponse pizzaOrderResponse = objectMapper.readValue(responseBody, PizzaOrdersResponse.class);
        assertNotNull(pizzaOrderResponse);
        assertEquals(AwesomePizzaResponseStatus.OK, pizzaOrderResponse.getResponseStatus());
        assertEquals(ordersInProgressNumber, pizzaOrderResponse.getPizzaOrders().size());
        assertTrue(pizzaOrderResponse.getPizzaOrders().stream().allMatch(o->o.getOrderStatus().equals(PIZZA_IN_PROGRESS)));
    }


    private void testOrderRefusal(Long orderId) throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/restaurant/refuse/" + orderId))
                .andExpect(status().isOk())
                .andReturn();
        String responseBody = result.getResponse().getContentAsString();
        PizzaOrderResponse pizzaOrderResponse = objectMapper.readValue(responseBody, PizzaOrderResponse.class);
        assertNotNull(pizzaOrderResponse);
        assertEquals(AwesomePizzaResponseStatus.OK, pizzaOrderResponse.getResponseStatus());
        assertNotNull(pizzaOrderResponse.getPizzaOrder());
        PizzaOrderDto acceptedOrder = pizzaOrderResponse.getPizzaOrder();
        assertEquals(REFUSED, acceptedOrder.getOrderStatus());
        assertEquals(2, acceptedOrder.getPizzaOrderItems().size());
    }

    private void testGetNextOrderWithEmptyQueue() throws Exception {
        mockMvc.perform(get("/restaurant/next"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseStatus").value("EMPTY_QUEUE"));
    }

    private Long testGetNextOrderWithGivenCode(String submittedOrderCode) throws Exception {
        MvcResult result = mockMvc.perform(get("/restaurant/next"))
                .andExpect(status().isOk()).andReturn();
        String responseBody = result.getResponse().getContentAsString();
        PizzaOrderResponse pizzaOrderResponse = objectMapper.readValue(responseBody, PizzaOrderResponse.class);
        assertNotNull(pizzaOrderResponse);
        assertEquals(AwesomePizzaResponseStatus.OK, pizzaOrderResponse.getResponseStatus());
        assertNotNull(pizzaOrderResponse.getPizzaOrder());
        PizzaOrderDto nextOrder = pizzaOrderResponse.getPizzaOrder();
        assertEquals(SUBMITTED, nextOrder.getOrderStatus());
        assertEquals(submittedOrderCode.trim().toUpperCase(), nextOrder.getCode().trim().toUpperCase());
        assertEquals(2, nextOrder.getPizzaOrderItems().size());
        return nextOrder.getId();
    }


    private String testSubmitNewOrder(List<PizzaRecipeDto> availablePizzaRecipes) throws Exception {
        Optional<PizzaRecipeDto> pizzaRecipeDtoOpt = availablePizzaRecipes.stream().filter(pizza -> pizza.getName().equalsIgnoreCase("funghi")).findAny();
        assertTrue(pizzaRecipeDtoOpt.isPresent());
        PizzaRecipeDto pizzaRecipeDto = pizzaRecipeDtoOpt.get();

        NewPizzaOrderItemDto orderItemDto1 = new NewPizzaOrderItemDto();
        orderItemDto1.setPizzaRecipeId(pizzaRecipeDto.getId());
        orderItemDto1.setDoughType(DoughType.CEREALS);

        NewPizzaOrderItemDto orderItemDto2 = new NewPizzaOrderItemDto();
        orderItemDto2.setPizzaRecipeId(pizzaRecipeDto.getId());
        orderItemDto2.setDoughType(DoughType.STANDARD);

        NewPizzaOrderDto newPizzaOrderDto = new NewPizzaOrderDto();
        newPizzaOrderDto.getPizzaOrderItems().add(orderItemDto1);
        newPizzaOrderDto.getPizzaOrderItems().add(orderItemDto2);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/orders/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPizzaOrderDto)))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        PizzaOrderResponse pizzaOrderResponse = objectMapper.readValue(responseBody, PizzaOrderResponse.class);
        assertNotNull(pizzaOrderResponse);
        assertEquals(AwesomePizzaResponseStatus.OK, pizzaOrderResponse.getResponseStatus());
        assertNotNull(pizzaOrderResponse.getPizzaOrder());
        PizzaOrderDto savedOrder = pizzaOrderResponse.getPizzaOrder();
        assertEquals(SUBMITTED, savedOrder.getOrderStatus());
        assertEquals(2, savedOrder.getPizzaOrderItems().size());
        Optional<PizzaOrderItemDto> orderItemDto1Opt = savedOrder.getPizzaOrderItems().stream().filter(item -> item.getDoughType().equals(DoughType.CEREALS)).findAny();
        Optional<PizzaOrderItemDto> orderItemDto2Opt = savedOrder.getPizzaOrderItems().stream().filter(item -> item.getDoughType().equals(DoughType.STANDARD)).findAny();
        assertTrue(orderItemDto1Opt.isPresent());
        assertTrue(orderItemDto2Opt.isPresent());
        PizzaOrderItemDto savedOrderItemDto1 = orderItemDto1Opt.get();
        assertEquals(pizzaRecipeDto.getId(), savedOrderItemDto1.getPizzaRecipe().getId());
        assertEquals(savedOrder.getPrice(), pizzaRecipeDto.getDefaultPrice() * 2);

        return savedOrder.getCode();
    }


    private List<PizzaRecipeDto> testFetchPizzaRecipes() throws Exception {
        MvcResult result = mockMvc.perform(get("/lookup/all-available-pizzas")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
        String responseBody = result.getResponse().getContentAsString();
        PizzaRecipes pizzaRecipes = objectMapper.readValue(responseBody, PizzaRecipes.class);
        assertNotNull(pizzaRecipes);
        assertFalse(pizzaRecipes.getPizzaRecipesList().isEmpty());
        //ananas pizza is not active
        assertTrue(pizzaRecipes.getPizzaRecipesList().stream()
                .noneMatch(pizza -> pizza.getName().toUpperCase().contains("ANANAS")));
        return pizzaRecipes.getPizzaRecipesList();
    }


}
