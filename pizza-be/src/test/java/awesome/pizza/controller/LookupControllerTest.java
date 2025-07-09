package awesome.pizza.controller;

import awesome.pizza.TestSamples;
import awesome.pizza.model.dto.PizzaRecipeDto;
import awesome.pizza.service.restaurant.PizzaRecipeService;
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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LookupController.class)
@AutoConfigureMockMvc(addFilters = false)
class LookupControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @MockBean
    private PizzaRecipeService pizzaRecipeService;

    @Test
    @DisplayName("GET /lookup/all-available-pizzas returns list of active pizza recipes")
    void getAllAvailablePizzas() throws Exception {

        when(pizzaRecipeService.getAllActiveRecipes())
                .thenReturn(List.of(new PizzaRecipeDto(TestSamples.pizzaRecipeMargheritaSample()),
                        new PizzaRecipeDto(TestSamples.pizzaRecipeMarinaraSample())));


        mockMvc.perform(MockMvcRequestBuilders.get("/lookup/all-available-pizzas")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pizzaRecipesList").isArray())
                .andExpect(jsonPath("$.pizzaRecipesList[0].name").value("Margherita"))
                .andExpect(jsonPath("$.pizzaRecipesList[1].name").value("Marinara"));
    }
}