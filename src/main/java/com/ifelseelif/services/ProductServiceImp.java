package com.ifelseelif.services;

import com.ifelseelif.Constants;
import com.ifelseelif.database.models.Coordinates;
import com.ifelseelif.database.models.Organization;
import com.ifelseelif.database.models.Product;
import com.ifelseelif.database.repositories.OrganizationRepositoryImp;
import com.ifelseelif.database.repositories.ProductRepositoryImp;
import com.ifelseelif.services.interfaces.ProductService;
import com.ifelseelif.services.interfaces.Service;
import com.ifelseelif.servlets.exceptions.BadRequestException;

import java.lang.reflect.Field;
import java.util.*;

public class ProductServiceImp extends Service<Product> implements ProductService {
    private final OrganizationRepositoryImp organizationRepository = new OrganizationRepositoryImp();
    private ProductRepositoryImp productRepository;
    private List<String> productPropertiesNames;


    public ProductServiceImp() {
        super(new ProductRepositoryImp(), Product.class);
        this.productRepository = (ProductRepositoryImp) repository;
    }

    public void save(Product product) throws BadRequestException {
        validate(product);

        int manufacturerId = product.getManufacturer().getId();
        Optional<Organization> optionalOrganization = organizationRepository.getById(manufacturerId);
        if (optionalOrganization.isPresent()) {
            product.setManufacturer(optionalOrganization.get());
        } else {
            throw new BadRequestException("Organization didn't present with this id");
        }
        product.setCreationDate(new java.sql.Date(Calendar.getInstance().getTime().getTime()));
        super.save(product);
    }

    @Override
    protected List<String> getPropertiesName() {
        if (productPropertiesNames != null) {
            return productPropertiesNames;
        }

        productPropertiesNames = new ArrayList<>();
        for (Field field : Product.class.getDeclaredFields()) {
            productPropertiesNames.add(field.getName());
        }

        productPropertiesNames.remove("coordinates");
        productPropertiesNames.remove("manufacturer");
        for (Field field : Organization.class.getDeclaredFields()) {
            productPropertiesNames.add(Constants.manufacturerPrefix + field.getName());
        }
        for (Field field : Coordinates.class.getDeclaredFields()) {
            productPropertiesNames.add(Constants.coordinatesPrefix + field.getName());
        }

        return productPropertiesNames;
    }

    @Override
    protected void validate(Product product) throws BadRequestException {
        String errorMessage = null;
        if (product.getName() == null || product.getName().isEmpty()) errorMessage = "Name should not be empty";
        if (product.getCoordinates() == null || product.getCoordinates().getX() == null)
            errorMessage = "Coordinate x should not be empty";
        if (product.getPrice() == null || product.getPrice() <= 0)
            errorMessage = "Price should not be empty and be greater than 0";
        if (product.getManufactureCost() == null)
            errorMessage = "Manufacture cost should not be empty";
        if (product.getUnitOfMeasure() == null)
            errorMessage = "Unit of measure should not be empty";
        if (product.getManufacturer() == null || product.getManufacturer().getId() == 0)
            errorMessage = "Manufacture id should not be empty";

        if (errorMessage != null) {
            throw new BadRequestException(errorMessage);
        }
    }

    @Override
    public boolean updateById(Object productId, Product product) throws BadRequestException {
        Optional<Organization> optionalOrganization = organizationRepository.getById(product.getManufacturer().getId());
        if (!optionalOrganization.isPresent()) return false;

        product.setId((Long) productId);
        product.setManufacturer(optionalOrganization.get());
        return super.updateById(productId, product);
    }

    @Override
    public void deleteByManufactureCost(String[] manufactureCosts) throws BadRequestException {
        Long manufactureCost = null;
        for (String manufactureCostString :
                manufactureCosts) {
            try {
                manufactureCost = Long.parseLong(manufactureCostString);
            } catch (Exception ignored) {
            }
        }
        if (manufactureCost == null) throw new BadRequestException("provide manufacture cost");
        productRepository.deleteByManufactureCost(manufactureCost);
    }
}
