package awesome.pizza.config;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
@Getter
public class AwesomePizzaException  extends RuntimeException {

    private static final long serialVersionUID = 1L;

    protected final String code;

    protected final String customMessage;

    public AwesomePizzaException(ErrorEnum errorEnum, String customMessage) {
        super(customMessage);
        this.code = errorEnum.name();
        this.customMessage = customMessage;
    }

    public AwesomePizzaException(ErrorEnum errorEnum) {
        this.code = errorEnum.name();
        this.customMessage = errorEnum.getMessage();
    }
}
