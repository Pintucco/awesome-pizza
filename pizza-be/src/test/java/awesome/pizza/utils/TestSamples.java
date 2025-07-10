package awesome.pizza.utils;

import awesome.pizza.model.dto.PizzaOrderDto;
import awesome.pizza.model.dto.PizzaOrderItemDto;
import awesome.pizza.model.dto.PizzaRecipeDto;
import awesome.pizza.model.entities.DoughType;
import awesome.pizza.model.entities.OrderStatus;
import awesome.pizza.model.entities.PizzaRecipe;
import awesome.pizza.service.orders.OrderCodeProvider;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TestSamples {


    public PizzaRecipe pizzaRecipeMargheritaSample() {
        PizzaRecipe pizzaRecipe = new PizzaRecipe();
        pizzaRecipe.setId(1L);
        pizzaRecipe.setName("Margherita");
        pizzaRecipe.setDescription("Pomodoro, mozzarella, origano");
        pizzaRecipe.setDefaultPrice(5.0);
        return pizzaRecipe;
    }

    public PizzaRecipe pizzaRecipeMarinaraSample() {
        PizzaRecipe pizzaRecipe = new PizzaRecipe();
        pizzaRecipe.setId(2L);
        pizzaRecipe.setName("Marinara");
        pizzaRecipe.setDescription("Pomodoro, origano");
        pizzaRecipe.setDefaultPrice(4.0);
        return pizzaRecipe;
    }

    public  PizzaOrderDto pizzaOrderDtoSample(){
        return PizzaOrderDto.builder()
                .id(1L)
                .code(OrderCodeProvider.generateOrderCode())
                .orderStatus(OrderStatus.SUBMITTED)
                .price(15.0)
                .build();
    }

    public PizzaOrderItemDto pizzaOrderItemDtoSample(PizzaRecipe pizzaRecipe){
        return PizzaOrderItemDto.builder()
                .doughType(DoughType.STANDARD)
                .price(pizzaRecipe.getDefaultPrice())
                .pizzaRecipe(new PizzaRecipeDto(pizzaRecipe))
                .build();
    }
}
