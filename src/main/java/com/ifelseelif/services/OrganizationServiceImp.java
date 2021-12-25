package com.ifelseelif.services;

import com.ifelseelif.Constants;
import com.ifelseelif.dao.OrganizationDao;
import com.ifelseelif.dao.models.Address;
import com.ifelseelif.dao.models.Filter;
import com.ifelseelif.dao.models.Location;
import com.ifelseelif.dao.models.Organization;
import com.ifelseelif.servlets.exceptions.HttpException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrganizationServiceImp {

    private List<String> organizationPropertiesNames;
    private OrganizationDao organizationDao;

    public OrganizationServiceImp() {
        organizationDao = new OrganizationDao();
    }

    public List<Organization> getAll(Map<String, String[]> parameterMap) throws HttpException {
        int pageIndex = getIntValue(parameterMap, "pageIndex", 0);
        int pageSize = getIntValue(parameterMap, "pageSize", 10);
        List<String> sortingParams = getSortParams(parameterMap.getOrDefault("sort", new String[]{}));
        Map<String, String[]> filters = getFilters(parameterMap);
        Filter filter = new Filter(pageIndex, pageSize, sortingParams, filters);
        return organizationDao.findAllFiltering(filter);
    }

    public void save(Organization body) throws HttpException {
        validate(body);
        organizationDao.save(body);
    }

    public Organization getById(int id) throws HttpException {
        Organization organization = organizationDao.findById(id);
        if (organization == null) throw new HttpException("Organization with id:=" + id + " not found", 400);
        return organization;
    }

    public boolean updateById(int id, Organization body) throws HttpException {
        validate(body);

        getById(id);
        organizationDao.update(body);
        return true;
    }

    public void deleteById(int id) throws HttpException {
        Organization organization = getById(id);
        organizationDao.delete(organization);
    }

    private List<String> getPropertiesName() {
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

    private List<String> getSortParams(String[] sorts) throws HttpException {
        List<String> result = new ArrayList<>();
        List<String> propertiesName = getPropertiesName();
        for (String order : sorts) {
            String[] values = order.split(Constants.divider);
            if (values.length == 0) throw new HttpException("Invalid sort param, it can not be zero", 400);
            if (propertiesName.contains(values[0])) {
                result.add(order);
            } else {
                throw new HttpException("Invalid name sort" + values[0], 400);
            }
        }

        return result;
    }

    private Map<String, String[]> getFilters(Map<String, String[]> parameterMap) throws HttpException {
        Map<String, String[]> filters = new HashMap<>();
        List<String> propertiesName = getPropertiesName();
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            if (entry.getKey().equals("sort") || entry.getKey().equals("pageIndex") || entry.getKey().equals("pageSize"))
                continue;
            if (!propertiesName.contains(entry.getKey())) {
                throw new HttpException("Invalid name filter " + entry.getKey(), 400);
            }
            filters.put(entry.getKey(), entry.getValue());
        }

        return filters;
    }

    private int getIntValue(Map<String, String[]> parameterMap, String name, int defaultValue) throws HttpException {
        int value = defaultValue;
        if (parameterMap.containsKey(name)) {
            try {
                value = Integer.parseInt(String.join("", parameterMap.get(name)));
            } catch (Exception ignored) {
                throw new HttpException("Parameters is invalid", 400);
            }
        }

        return value;
    }

    protected void validate(Organization organization) throws HttpException {
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
            throw new HttpException(errorMessage, 400);
        }
    }
}
