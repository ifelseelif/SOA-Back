package com.ifelseelif.services;

import com.ifelseelif.Constants;
import com.ifelseelif.database.models.*;
import com.ifelseelif.database.repositories.OrganizationRepositoryImp;
import com.ifelseelif.services.interfaces.Service;
import com.ifelseelif.servlets.exceptions.BadRequestException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class OrganizationServiceImp extends Service<Organization> {

    private List<String> organizationPropertiesNames;

    public OrganizationServiceImp() {
        super(new OrganizationRepositoryImp(), Organization.class);
    }

    @Override
    protected List<String> getPropertiesName() {
        if (organizationPropertiesNames != null) {
            return organizationPropertiesNames;
        }

        organizationPropertiesNames = new ArrayList<>();
        for (Field field : Organization.class.getDeclaredFields()) {
            organizationPropertiesNames.add(field.getName());
        }

        organizationPropertiesNames.remove("postalAddress");
        for (Field field : Address.class.getDeclaredFields()) {
            organizationPropertiesNames.add(Constants.addressPrefix + field.getName());
        }

        organizationPropertiesNames.remove("town");
        for (Field field : Location.class.getDeclaredFields()) {
            organizationPropertiesNames.add(Constants.townPrefix + field.getName());
        }

        return organizationPropertiesNames;
    }

    @Override
    protected void validate(Organization organization) throws BadRequestException {
        String errorMessage = null;

        if (organization.getName() == null || organization.getName().isEmpty())
            errorMessage = "Name should not be empty";
        if (organization.getFullName() == null || organization.getFullName().length() >= 1707)
            errorMessage = "Full name should not be empty and length less than 1707";
        if (organization.getType() == null) errorMessage = "Type should not be empty";
        if (organization.getPostalAddress() == null) errorMessage = "Zip code should not be empty";
        if (organization.getPostalAddress().getZipCode() == null) errorMessage = "Zip code should not be empty";
        if (organization.getPostalAddress().getTown() == null) errorMessage = "Town z should not be empty";
        if (organization.getPostalAddress().getTown().getZ() == null) errorMessage = "Town z should not be empty";

        if (errorMessage != null) {
            throw new BadRequestException(errorMessage);
        }
    }
}
