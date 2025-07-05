package awesome.pizza.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "PIZZA_ORDER")
@Getter
@Setter
@NoArgsConstructor
@SequenceGenerator(name = "sequence", sequenceName = "SEQ_PIZZA_ORDER", allocationSize = 1)
public class PizzaOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence")
    @Column(name = "ID")
    protected Long id;

    @Column(name = "CODE")
    private String code;

    @Column(name = "ORDER_STATUS")
    private OrderStatus orderStatus;

    @Column(name = "PRICE")
    private Double price;

    @Column(name = "DOUGH_TYPE")
    @Enumerated(EnumType.STRING)
    private DoughType doughType;

    @JoinColumn(name = "PIZZA_RECIPE")
    @ManyToOne(fetch = FetchType.EAGER)
    private PizzaRecipe pizzaRecipe;

}
