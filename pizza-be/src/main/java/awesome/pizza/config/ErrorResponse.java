package awesome.pizza.config;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ErrorResponse {

    private String errorCode;
    private String errorMessage;
    private String detailedMessage;

    public ErrorResponse(AwesomePizzaException awesomePizzaException){
        this.errorCode=awesomePizzaException.getCode();
        this.errorMessage=awesomePizzaException.getMessage();
    }

}