package com.ifelseelif.dao.models;


import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Data
@NoArgsConstructor
@Embeddable
public class Coordinates {
    private Float x;

    private float y; //Поле не может быть null
}