package com.ifelseelif.database.models;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.UniqueElements;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически

    @NotNull
    @NotEmpty
    private String name; //Поле не может быть null, Строка не может быть пустой

    @NotNull
    @Length(max = 1707, message = "Длина строки не должна быть больше 1707")
    @UniqueElements
    private String fullName; //Значение поля должно быть больше 0

    @Enumerated(EnumType.STRING)
    @NotNull
    private OrganizationType type; //Поле не может быть null

    @NotNull
    @Embedded
    private Address postalAddress;
}
