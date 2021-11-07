# SOA-Back

## Разработать веб-сервис на базе сервлета, реализующий управление коллекцией объектов, и клиентское веб-приложение, предоставляющее интерфейс к разработанному веб-сервису. В коллекции необходимо хранить объекты класса Product, описание которого приведено ниже:

```
public class Product {
    private Long id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private java.util.Date creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private Float price; //Поле может быть null, Значение поля должно быть больше 0
    private Long manufactureCost; //Поле может быть null
    private UnitOfMeasure unitOfMeasure; //Поле не может быть null
    private Organization manufacturer; //Поле может быть null
}
public class Coordinates {
    private Float x; //Поле не может быть null
    private float y;
}
public class Organization {
    private int id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private String fullName; //Длина строки не должна быть больше 1707, Значение этого поля должно быть уникальным, Поле может быть null
    private OrganizationType type; //Поле не может быть null
    private Address postalAddress; //Поле может быть null
}
public class Address {
    private String zipCode; //Поле может быть null
    private Location town; //Поле может быть null
}
public class Location {
    private double x;
    private int y;
    private Double z; //Поле не может быть null
}
public enum UnitOfMeasure {
    METERS,
    SQUARE_METERS,
    PCS,
    LITERS,
    MILLILITERS;
}
public enum OrganizationType {
    COMMERCIAL,
    PUBLIC,
    GOVERNMENT;
}
```

## Веб-сервис должен удовлетворять следующим требованиям:

- API, реализуемый сервисом, должен соответствовать рекомендациям подхода RESTful.
- Необходимо реализовать следующий базовый набор операций с объектами коллекции: добавление нового элемента, получение элемента по ИД, обновление элемента, удаление элемента, получение массива элементов.
- Операция, выполняемая над объектом коллекции, должна определяться методом HTTP-запроса.
- Операция получения массива элементов должна поддерживать возможность сортировки и фильтрации по любой комбинации полей класса, а также возможность постраничного вывода результатов выборки с указанием размера и порядкового номера выводимой страницы.
- Все параметры, необходимые для выполнения операции, должны передаваться в URL запроса.
- Данные коллекции, которыми управляет веб-сервис, должны храниться в реляционной базе данных.
- Информация об объектах коллекции должна передаваться в формате xml.
- В случае передачи сервису данных, нарушающих заданные на уровне класса ограничения целостности, сервис должен возвращать код ответа http, соответствующий произошедшей ошибке.
- Веб-сервис должен быть "упакован" в веб-приложение, которое необходимо развернуть на сервере приложений Payara.

## Помимо базового набора, веб-сервис должен поддерживать следующие операции над объектами коллекции:

- Удалить все объекты, значение поля manufactureCost которого эквивалентно заданному.
- Вернуть один (любой) объект, значение поля unitOfMeasure которого является минимальным.
- Вернуть массив объектов, значение поля name которых содержит заданную подстроку.
Эти операции должны размещаться на отдельных URL.
