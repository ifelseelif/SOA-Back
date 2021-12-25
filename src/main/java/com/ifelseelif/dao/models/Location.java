package com.ifelseelif.dao.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Embeddable
@Data
@NoArgsConstructor
public class Location {
    private double x;
    private int y;
    private Double z;
}
