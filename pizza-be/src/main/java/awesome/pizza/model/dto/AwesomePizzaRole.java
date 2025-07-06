package awesome.pizza.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AwesomePizzaRole implements GrantedAuthority {

    private RoleEnum role;

    @Override
    public String getAuthority() {
        return role.toString();
    }

}
