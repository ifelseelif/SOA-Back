package com.ifelseelif.database.models;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;

@Embeddable
@Data
@NoArgsConstructor
public class Address {
    @NotNull
    private String zipCode;

    @NotNull
    @Embedded
    private Location town;
}
