package com.ifelseelif.dao.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;

@Embeddable
@Data
@NoArgsConstructor
public class Address {
    private String zipCode;

    @Embedded
    private Location town;
}
