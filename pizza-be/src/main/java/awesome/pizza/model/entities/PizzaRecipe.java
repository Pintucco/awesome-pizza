package awesome.pizza.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "PIZZA_RECIPE")
@Getter
@Setter
@NoArgsConstructor
@SequenceGenerator(name = "sequence", sequenceName = "SEQ_PIZZA_RECIPE", allocationSize = 1)
public class PizzaRecipe {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence")
    @Column(name = "ID")
    protected Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "DEFAULT_PRICE")
    private Double defaultPrice;

    @Column(name = "ACTIVE")
    private Boolean active = true;
}
