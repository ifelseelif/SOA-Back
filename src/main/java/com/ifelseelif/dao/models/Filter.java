package com.ifelseelif.dao.models;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class Filter {
    private int pageIndex;
    private int pageSize;
    private List<String> sortingParams;
    private Map<String, String[]> filters;

    public Filter(int pageIndex, int pageSize, List<String> sortingParams, Map<String, String[]> filters) {
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        this.sortingParams = sortingParams;
        this.filters = filters;
    }


}
