package awesome.pizza.service.security;

import awesome.pizza.model.dto.AwesomePizzaUserDetails;
import awesome.pizza.model.entities.AwesomePizzaUser;
import awesome.pizza.repository.IAwesomePizzaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationFacade {

    private final IAwesomePizzaUserRepository awesomePizzaUserRepository;

    public AwesomePizzaUser getLoggedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try {
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = (UsernamePasswordAuthenticationToken) authentication;
            AwesomePizzaUserDetails awesomePizzaUserDetails = (AwesomePizzaUserDetails) usernamePasswordAuthenticationToken.getPrincipal();
            return awesomePizzaUserRepository.findById(awesomePizzaUserDetails.getUserId()).orElse(null);
        } catch (Exception e) {
            return null;
        }
    }
}
