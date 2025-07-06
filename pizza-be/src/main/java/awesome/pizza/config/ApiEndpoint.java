package awesome.pizza.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApiEndpoint {

    ORDERS("orders",false),
    RESTAURANT("restaurant", true),
    LOOKUP("lookup", true);

    public final String path;
    private final boolean secured;

}
