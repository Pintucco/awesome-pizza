package awesome.pizza.repository;

import awesome.pizza.model.entities.AwesomePizzaUser;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface IAwesomePizzaUserRepository extends CrudRepository<AwesomePizzaUser, Long> {

    Optional<AwesomePizzaUser> findByUsername(String username);



}
