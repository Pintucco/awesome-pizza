package awesome.pizza.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "PIZZA_ORDER_ITEM")
@Getter
@Setter
@NoArgsConstructor
@SequenceGenerator(name = "sequence", sequenceName = "SEQ_PIZZA_ORDER_ITEM", allocationSize = 1)
public class PizzaOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence")
    @Column(name = "ID")
    private Long id;

    @JoinColumn(name = "ORDER_ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private PizzaOrder pizzaOrder;

    @Column(name = "PRICE")
    private Double price;

    @Column(name = "DOUGH_TYPE")
    @Enumerated(EnumType.STRING)
    private DoughType doughType;

    @JoinColumn(name = "PIZZA_RECIPE")
    @ManyToOne(fetch = FetchType.EAGER)
    private PizzaRecipe pizzaRecipe;


}
