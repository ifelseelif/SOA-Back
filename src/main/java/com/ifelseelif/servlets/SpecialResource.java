package com.ifelseelif.servlets;

import com.ifelseelif.services.ProductServiceImp;
import com.ifelseelif.servlets.exceptions.HttpException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/special")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SpecialResource {
    private final ProductServiceImp productService = new ProductServiceImp();

    @DELETE
    public Response deleteByManufactureCost(@QueryParam("manufactureCost") Long id) {
        try {
            productService.deleteByManufactureCost(id);
            return Response.ok().build();
        } catch (HttpException exception) {
            return Response.status(exception.getStatusCode(), exception.getMessage()).build();
        }
    }
}
