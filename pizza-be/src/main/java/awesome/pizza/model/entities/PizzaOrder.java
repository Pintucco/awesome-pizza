package awesome.pizza.model.entities;

import awesome.pizza.service.orders.OrderCodeProvider;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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
    private Long id;

    @Column(name = "CODE")
    @Pattern(regexp = OrderCodeProvider.ORDER_CODE_REGEX, message = "Invalid code format")
    private String code;

    @Column(name = "ORDER_STATUS")
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Column(name = "PRICE")
    private Double price;

    @OneToMany(mappedBy = "pizzaOrder", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<PizzaOrderItem> pizzaOrderItems = new ArrayList<>();

    @Column(name = "SUBMITTED_AT")
    private Instant submittedAt;

    @Column(name = "ACCEPTED_REFUSED_AT")
    private Instant acceptedRefusedAt;

    @Column(name = "CONCLUDED_AT")
    private Instant concludedAt;


    @JoinColumn(name = "WORKED_BY")
    @ManyToOne(fetch = FetchType.EAGER)
    private AwesomePizzaUser workedBy;

}
