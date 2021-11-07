package com.ifelseelif.database.models;


import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Data
@NoArgsConstructor
@Embeddable
public class Coordinates {
    @NotNull
    private Float x;

    private float y; //Поле не может быть null
}