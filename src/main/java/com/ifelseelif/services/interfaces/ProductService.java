package com.ifelseelif.services.interfaces;

import com.ifelseelif.servlets.exceptions.BadRequestException;

public interface ProductService {

    void deleteByManufactureCost(String[] manufactureCosts) throws BadRequestException;
}
