package com.ifelseelif.servlets;

import com.ifelseelif.database.models.Organization;
import com.ifelseelif.services.OrganizationServiceImp;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("organizations/*")
public class OrganizationServlet extends Servlet<Organization, OrganizationServiceImp> {
    public OrganizationServlet() {
        super(new OrganizationServiceImp(), Organization.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
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
            return Integer.parseInt(splits[1]);
        } catch (Exception ignored) {
        }
        return null;
    }
}
