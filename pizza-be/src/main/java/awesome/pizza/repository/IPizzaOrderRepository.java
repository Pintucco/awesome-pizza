package awesome.pizza.repository;

import awesome.pizza.model.entities.OrderStatus;
import awesome.pizza.model.entities.PizzaOrder;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface IPizzaOrderRepository extends CrudRepository<PizzaOrder, Long> {

    Optional<PizzaOrder> findByCode(String code);

    List<PizzaOrder> findByOrderStatus(OrderStatus orderStatus);


}
