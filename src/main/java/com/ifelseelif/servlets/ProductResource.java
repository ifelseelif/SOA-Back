package com.ifelseelif.servlets;

import com.ifelseelif.services.ProductServiceImp;
import com.ifelseelif.servlets.exceptions.BadRequestException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/products")
@Produces(MediaType.TEXT_XML)
@Consumes(MediaType.TEXT_XML)
public class ProductResource {
    private final ProductServiceImp productService = new ProductServiceImp();

    @GET
    public Response Get() {
        return Response.status(Response.Status.OK).entity("Kek").build();
    }
/*
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        super.doPost(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPut(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (req.getParameterMap().containsKey("manufactureCost")) {
            try {
                String[] manufactureCostValues = req.getParameterMap().get("manufactureCost");
                productService.deleteByManufactureCost(manufactureCostValues);
            } catch (HttpException e) {
                resp.setStatus(e.getStatusCode());
                writeString(resp, e.getMessage());
            }
            return;
        }
        super.doDelete(req, resp);
    }

    @Override
    protected Object getIdFromPath(HttpServletRequest req) {
        String pathInfo = req.getPathInfo();

        String[] splits = pathInfo != null ? pathInfo.split("/") : new String[0];

        if (splits.length != 2) {
            return null;
        }
        try {
            return Long.parseLong(splits[1]);
        } catch (Exception ignored) {
        }
        return null;
    }*/
}
