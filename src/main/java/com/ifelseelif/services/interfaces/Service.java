package com.ifelseelif.services.interfaces;

import com.ifelseelif.Constants;
import com.ifelseelif.database.models.Filter;
import com.ifelseelif.database.repositories.interfaces.Repository;
import com.ifelseelif.servlets.exceptions.BadRequestException;

import java.util.*;

public abstract class Service<T> {
    protected Repository<T> repository;

    protected abstract List<String> getPropertiesName();

    protected abstract void validate(T t) throws BadRequestException;

    protected Service(Repository<T> repository, Class<T> type) {
        this.repository = repository;
    }

    public List<T> getAll(Map<String, String[]> parameterMap) throws BadRequestException {
        int pageIndex = getIntValue(parameterMap, "pageIndex", 0);
        int pageSize = getIntValue(parameterMap, "pageSize", 10);
        List<String> sortingParams = getSortParams(parameterMap.getOrDefault("sort", new String[]{}));
        Map<String, String[]> filters = getFilters(parameterMap);
        Filter filter = new Filter(pageIndex, pageSize, sortingParams, filters);
        return repository.getAll(filter);
    }

    private List<String> getSortParams(String[] sorts) {
        List<String> result = new ArrayList<>();
        List<String> productPropertiesName = getPropertiesName();
        for (String order : sorts) {
            String[] values = order.split(Constants.divider);
            if (values.length == 0) continue;
            if (productPropertiesName.contains(values[0])) {
                result.add(order);
            }
        }
        return result;
    }

    private Map<String, String[]> getFilters(Map<String, String[]> parameterMap) {
        Map<String, String[]> filters = new HashMap<>();
        for (String propertyName : getPropertiesName()) {
            if (!parameterMap.containsKey(propertyName)) continue;
            filters.put(propertyName, parameterMap.get(propertyName));
        }

        return filters;
    }

    private int getIntValue(Map<String, String[]> parameterMap, String name, int defaultValue) throws BadRequestException {
        int value = defaultValue;
        if (parameterMap.containsKey(name)) {
            try {
                value = Integer.parseInt(String.join("", parameterMap.get(name)));
            } catch (Exception ignored) {
                throw new BadRequestException("Parameters is invalid");
            }
        }

        return value;
    }

    public void save(T body) throws BadRequestException {
        validate(body);
        repository.save(body);
    }

    public Optional<T> getById(Object id) {
        return repository.getById(id);
    }

    public boolean updateById(Object id, T body) throws BadRequestException {
        validate(body);

        Optional<T> optionalT = getById(id);
        if (optionalT.isPresent()) {
            repository.update(body);
            return true;
        }
        return false;
    }

    public boolean deleteById(Object id) throws BadRequestException {
        return repository.deleteById(id);
    }
}
