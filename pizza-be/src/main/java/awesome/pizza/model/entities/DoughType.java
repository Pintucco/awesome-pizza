package awesome.pizza.model.entities;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@RequiredArgsConstructor
@Getter
public enum DoughType implements Serializable {

    STANDARD ("Standard"),
    GLUTEN_FREE ("Gluten free"),
    CEREALS("Cereali");

    private final String description;


}
