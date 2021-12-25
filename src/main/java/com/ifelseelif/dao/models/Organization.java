package com.ifelseelif.dao.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически

    private String name; //Поле не может быть null, Строка не может быть пустой
    private String fullName; //Значение поля должно быть больше 0

    @Enumerated(EnumType.STRING)
    private OrganizationType type; //Поле не может быть null

    @Embedded
    private Address postalAddress;
}
