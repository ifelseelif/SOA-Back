package com.ifelseelif.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ifelseelif.database.models.Product;
import com.ifelseelif.services.ProductServiceImp;
import com.ifelseelif.services.interfaces.ProductService;
import com.ifelseelif.servlets.exceptions.HttpException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/products/*")
public class ProductServlet extends Servlet<Product, ProductServiceImp> {
    private final ProductService productService;

    public ProductServlet() {
        super(new ProductServiceImp(), Product.class);
        this.productService = (ProductService) service;
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        super.doGet(req, resp);
    }

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
    }
}
