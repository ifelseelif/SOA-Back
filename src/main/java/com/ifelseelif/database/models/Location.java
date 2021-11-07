package com.ifelseelif.database.models;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Embeddable
@Data
@NoArgsConstructor
public class Location {
    private double x;
    private int y;

    @NotNull
    private Double z;
}
