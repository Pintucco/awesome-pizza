package awesome.pizza.controller;

import awesome.pizza.model.dto.AwesomePizzaResponseStatus;
import awesome.pizza.model.dto.PizzaOrderDto;
import awesome.pizza.model.dto.PizzaOrderResponse;
import awesome.pizza.model.dto.PizzaRecipeDto;
import awesome.pizza.model.entities.OrderStatus;
import awesome.pizza.service.restaurant.PizzaRecipeService;
import awesome.pizza.service.restaurant.RestaurantService;
import awesome.pizza.utils.TestSamples;
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
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RestaurantController.class)
@AutoConfigureMockMvc(addFilters = false)
class RestaurantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RestaurantService restaurantService;

    @MockBean
    private PizzaRecipeService pizzaRecipeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /restaurant/next returns next order if available")
    void testNextOrder1() throws Exception {
        PizzaOrderDto pizzaOrderDto= TestSamples.pizzaOrderDtoSample();
        pizzaOrderDto.getPizzaOrderItems().add(TestSamples.pizzaOrderItemDtoSample(TestSamples.pizzaRecipeMargheritaSample()));
        pizzaOrderDto.getPizzaOrderItems().add(TestSamples.pizzaOrderItemDtoSample(TestSamples.pizzaRecipeMarinaraSample()));
        when(restaurantService.getNextOrder()).thenReturn(Optional.of(pizzaOrderDto));

        mockMvc.perform(get("/restaurant/next"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pizzaOrder.id").value(pizzaOrderDto.getId()))
                .andExpect(jsonPath("$.pizzaOrder.code").value(pizzaOrderDto.getCode()))
                .andExpect(jsonPath("$.pizzaOrder.price").value(pizzaOrderDto.getPrice()))
                .andExpect(jsonPath("$.pizzaOrder.orderStatus").value(pizzaOrderDto.getOrderStatus().name()))
                .andExpect(jsonPath("$.pizzaOrder.pizzaOrderItems").isArray())
                .andExpect(jsonPath("$.pizzaOrder.pizzaOrderItems[0].pizzaRecipe.name").value("Margherita"))
                .andExpect(jsonPath("$.responseStatus").value("OK"));
    }

    @Test
    @DisplayName("GET /restaurant/next returns EMPTY_QUEUE when no order")
    void nextOrder2() throws Exception {
        when(restaurantService.getNextOrder()).thenReturn(Optional.empty());

        mockMvc.perform(get("/restaurant/next"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseStatus").value("EMPTY_QUEUE"));
    }

    @Test
    @DisplayName("GET /restaurant/orders-in-progress")
    void getOrdersInProgress() throws Exception {
        PizzaOrderDto pizzaOrderDto= TestSamples.pizzaOrderDtoSample();
        pizzaOrderDto.setOrderStatus(OrderStatus.PIZZA_IN_PROGRESS);
        pizzaOrderDto.getPizzaOrderItems().add(TestSamples.pizzaOrderItemDtoSample(TestSamples.pizzaRecipeMargheritaSample()));
        pizzaOrderDto.getPizzaOrderItems().add(TestSamples.pizzaOrderItemDtoSample(TestSamples.pizzaRecipeMarinaraSample()));
        when(restaurantService.getOrdersInProgress()).thenReturn(List.of(pizzaOrderDto));

        mockMvc.perform(get("/restaurant/orders-in-progress"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pizzaOrders[0].id").value(1L))
                .andExpect(jsonPath("$.responseStatus").value("OK"));
    }

    @Test
    @DisplayName("POST /restaurant/accept")
    void acceptOrdes1() throws Exception {
        PizzaOrderDto pizzaOrderDto= TestSamples.pizzaOrderDtoSample();
        pizzaOrderDto.setOrderStatus(OrderStatus.PIZZA_IN_PROGRESS);
        pizzaOrderDto.getPizzaOrderItems().add(TestSamples.pizzaOrderItemDtoSample(TestSamples.pizzaRecipeMargheritaSample()));
        pizzaOrderDto.getPizzaOrderItems().add(TestSamples.pizzaOrderItemDtoSample(TestSamples.pizzaRecipeMarinaraSample()));

        PizzaOrderResponse response = PizzaOrderResponse.builder()
                .pizzaOrder(pizzaOrderDto)
                .responseStatus(AwesomePizzaResponseStatus.OK)
                .build();
        when(restaurantService.acceptOrder(1L)).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.post("/restaurant/accept/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pizzaOrder.id").value(pizzaOrderDto.getId()))
                .andExpect(jsonPath("$.pizzaOrder.code").value(pizzaOrderDto.getCode()))
                .andExpect(jsonPath("$.pizzaOrder.price").value(pizzaOrderDto.getPrice()))
                .andExpect(jsonPath("$.pizzaOrder.orderStatus").value(pizzaOrderDto.getOrderStatus().name()))
                .andExpect(jsonPath("$.pizzaOrder.pizzaOrderItems").isArray())
                .andExpect(jsonPath("$.pizzaOrder.pizzaOrderItems[0].pizzaRecipe.name").value("Margherita"))
                .andExpect(jsonPath("$.responseStatus").value("OK"));
    }

    @Test
    @DisplayName("POST /restaurant/refuse")
    void testRefuseOrder() throws Exception {
        PizzaOrderDto pizzaOrderDto= TestSamples.pizzaOrderDtoSample();
        pizzaOrderDto.setOrderStatus(OrderStatus.REFUSED);
        pizzaOrderDto.getPizzaOrderItems().add(TestSamples.pizzaOrderItemDtoSample(TestSamples.pizzaRecipeMargheritaSample()));
        pizzaOrderDto.getPizzaOrderItems().add(TestSamples.pizzaOrderItemDtoSample(TestSamples.pizzaRecipeMarinaraSample()));

        PizzaOrderResponse response = PizzaOrderResponse.builder()
                .pizzaOrder(pizzaOrderDto)
                .responseStatus(AwesomePizzaResponseStatus.OK)
                .build();
        when(restaurantService.refuseOrder(1L)).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.post("/restaurant/refuse/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pizzaOrder.id").value(pizzaOrderDto.getId()))
                .andExpect(jsonPath("$.pizzaOrder.code").value(pizzaOrderDto.getCode()))
                .andExpect(jsonPath("$.pizzaOrder.price").value(pizzaOrderDto.getPrice()))
                .andExpect(jsonPath("$.pizzaOrder.orderStatus").value(pizzaOrderDto.getOrderStatus().name()))
                .andExpect(jsonPath("$.pizzaOrder.pizzaOrderItems").isArray())
                .andExpect(jsonPath("$.pizzaOrder.pizzaOrderItems[0].pizzaRecipe.name").value("Margherita"))
                .andExpect(jsonPath("$.responseStatus").value("OK"));
    }

    @Test
    @DisplayName("POST /restaurant/conclude")
    void testConcludeOrder() throws Exception {
        PizzaOrderDto pizzaOrderDto= TestSamples.pizzaOrderDtoSample();
        pizzaOrderDto.setOrderStatus(OrderStatus.CONCLUDED);
        pizzaOrderDto.getPizzaOrderItems().add(TestSamples.pizzaOrderItemDtoSample(TestSamples.pizzaRecipeMargheritaSample()));
        pizzaOrderDto.getPizzaOrderItems().add(TestSamples.pizzaOrderItemDtoSample(TestSamples.pizzaRecipeMarinaraSample()));

        PizzaOrderResponse response = PizzaOrderResponse.builder()
                .pizzaOrder(pizzaOrderDto)
                .responseStatus(AwesomePizzaResponseStatus.OK)
                .build();
        when(restaurantService.concludeOrder(1L)).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.post("/restaurant/conclude/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pizzaOrder.id").value(pizzaOrderDto.getId()))
                .andExpect(jsonPath("$.pizzaOrder.code").value(pizzaOrderDto.getCode()))
                .andExpect(jsonPath("$.pizzaOrder.price").value(pizzaOrderDto.getPrice()))
                .andExpect(jsonPath("$.pizzaOrder.orderStatus").value(pizzaOrderDto.getOrderStatus().name()))
                .andExpect(jsonPath("$.pizzaOrder.pizzaOrderItems").isArray())
                .andExpect(jsonPath("$.pizzaOrder.pizzaOrderItems[0].pizzaRecipe.name").value("Margherita"))
                .andExpect(jsonPath("$.responseStatus").value("OK"));
    }

    @Test
    @DisplayName("POST /restaurant/new-pizza-recipe")
    void testNewPizzaRecipe() throws Exception {
        PizzaRecipeDto inputDto = new PizzaRecipeDto( TestSamples.pizzaRecipeMargheritaSample());

        when(pizzaRecipeService.newPizzaRecipe(any())).thenReturn(inputDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/restaurant/new-pizza-recipe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Margherita"));
    }




}
