package awesome.pizza.service;

import awesome.pizza.model.dto.PizzaRecipeDto;
import awesome.pizza.model.entities.PizzaRecipe;
import awesome.pizza.repository.IPizzaRecipeRepository;
import awesome.pizza.service.restaurant.PizzaRecipeService;
import awesome.pizza.utils.TestSamples;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
class PizzaRecipeServiceTest {

    @Mock
    private IPizzaRecipeRepository pizzaRecipeRepository;

    @InjectMocks
    private PizzaRecipeService pizzaRecipeService;

    @Test
    @DisplayName("Get all active recipes sorted by name")
    void testGetAllActiveRecipes() {

        when(pizzaRecipeRepository.findByActiveTrue())
                .thenReturn(List.of(TestSamples.pizzaRecipeMarinaraSample(),TestSamples.pizzaRecipeMargheritaSample()));

        List<PizzaRecipeDto> activePizzas = pizzaRecipeService.getAllActiveRecipes();
        assertEquals(2, activePizzas.size());
        assertEquals("Margherita", activePizzas.get(0).getName());
        assertEquals("Marinara", activePizzas.get(1).getName());
    }

    @Test
    @DisplayName("Define a new pizza recipe")
    void testNewPizzaRecipe() {
        PizzaRecipeDto inputDto = PizzaRecipeDto.builder()
                .name("Diavola")
                .description("Pomodoro, mozzarella, salame piccante")
                .defaultPrice(6.0)
                .build();

        PizzaRecipe savedPizzaRecipe = new PizzaRecipe();
        savedPizzaRecipe.setId(10L);
        savedPizzaRecipe.setName("Diavola");
        savedPizzaRecipe.setDescription("Pomodoro, mozzarella, salame piccante");
        savedPizzaRecipe.setDefaultPrice(6.0);

        when(pizzaRecipeRepository.save(any(PizzaRecipe.class))).thenReturn(savedPizzaRecipe);

        PizzaRecipeDto result = pizzaRecipeService.newPizzaRecipe(inputDto);

        assertEquals("Diavola", result.getName());
        assertEquals("Pomodoro, mozzarella, salame piccante", result.getDescription());
        assertEquals(6.0, result.getDefaultPrice());
    }
}
