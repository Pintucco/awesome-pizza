package awesome.pizza.service.security;

import awesome.pizza.model.dto.AwesomePizzaUserDetails;
import awesome.pizza.model.dto.RoleEnum;
import awesome.pizza.model.entities.AwesomePizzaUser;
import awesome.pizza.repository.IAwesomePizzaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantUserDetailService implements UserDetailsService {

    private final IAwesomePizzaUserRepository awesomePizzaUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return awesomePizzaUserRepository.findByUsername(username).map(this::toAwesomePizzaUserDetails).orElse(null);
    }

    private AwesomePizzaUserDetails toAwesomePizzaUserDetails(AwesomePizzaUser awesomePizzaUser) {
        return new AwesomePizzaUserDetails(awesomePizzaUser.getId(),
                awesomePizzaUser.getUsername(),
                awesomePizzaUser.getPassword(),
                List.of(RoleEnum.PIZZAIOLO));
    }
}
