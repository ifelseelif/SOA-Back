package com.ifelseelif.servlets;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.ifelseelif.services.interfaces.Service;
import com.ifelseelif.servlets.exceptions.BadRequestException;
import com.ifelseelif.servlets.exceptions.HttpException;
import com.ifelseelif.servlets.models.Body;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

public abstract class Servlet<T, ServiceImp extends Service<T>> extends HttpServlet {
    protected final XmlMapper xmlMapper = new XmlMapper();
    protected final Service<T> service;
    private final Class<T> typeParameterClass;


    protected Servlet(ServiceImp serviceImp, Class<T> typeClass) {
        service = serviceImp;
        typeParameterClass = typeClass;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                Map<String, String[]> parameterMap = req.getParameterMap();
                List<T> tList;

                tList = service.getAll(parameterMap);
                resp.setStatus(HttpServletResponse.SC_OK);
                writeBody(resp, tList);
                return;
            }
        } catch (HttpException httpException) {
            resp.setStatus(httpException.getStatusCode());
            writeString(resp, httpException.getMessage());
            return;
        }

        resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo != null && !pathInfo.equals("/")) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            writeString(resp, "Wrong path");
            return;
        }

        T body = GetRequestBody(req);
        if (body == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeString(resp, "Can't save object, because it contains invalid format");
            return;
        }

        com.ifelseelif.servlets.models.Body response;
        try {
            service.save(body);
            resp.setStatus(HttpServletResponse.SC_OK);
            response = new com.ifelseelif.servlets.models.Body("saved");
        } catch (BadRequestException exception) {
            resp.setStatus(exception.getStatusCode());
            writeString(resp, exception.getMessage());
            return;
        }
        writeBody(resp, response);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Object organizationId = getIdFromPath(req);
        if (organizationId == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        T body = GetRequestBody(req);
        if (body == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeString(resp, "Can't save object, because it contains invalid properties");
            return;
        }

        try {
            if (service.updateById(organizationId, body)) {
                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (BadRequestException e) {
            resp.setStatus(e.getStatusCode());
            writeString(resp, e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Object id = getIdFromPath(req);
        if (id == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            writeString(resp, "");
            return;
        }
        try {
            if (service.deleteById(id)) {
                resp.setStatus(HttpServletResponse.SC_OK);
                writeString(resp, "deleted");
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                writeString(resp, "not deleted");
            }
        } catch (HttpException e) {
            resp.setStatus(e.getStatusCode());
            writeString(resp, e.getMessage());
        }
    }

    protected abstract Object getIdFromPath(HttpServletRequest req);

    protected T GetRequestBody(HttpServletRequest req) {
        try {
            return xmlMapper.readValue(req.getReader(), typeParameterClass);
        } catch (Exception ignored) {
            return null;
        }
    }

    protected <Body> void writeBody(HttpServletResponse resp, Body body) throws IOException {
        String xml = xmlMapper.writeValueAsString(body);

        PrintWriter out = resp.getWriter();
        resp.setContentType("application/json; charset=utf-8");
        resp.setCharacterEncoding("UTF-8");
        out.print(xml);
        out.flush();
    }

    protected void writeString(HttpServletResponse resp, String message) throws IOException {
        Body body = new Body(message);
        String xml = xmlMapper.writeValueAsString(body);


        PrintWriter out = resp.getWriter();
        resp.setContentType("application/json; charset=utf-8");
        resp.setCharacterEncoding("UTF-8");
        out.print(xml);
        out.flush();
    }

}
