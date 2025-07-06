package awesome.pizza.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApiEndpoint {

    ORDERS("orders",false),
    RESTAURANT("restaurant", true),
    RECIPES("recipes", true);

    private final String path;
    private final boolean secured;

}
