package com.ifelseelif.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ifelseelif.servlets.models.Body;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/error500")
public class ErrorServlet extends HttpServlet {
    private final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Body body = new Body("something went wrong");
        String response = objectMapper.writeValueAsString(body);

        PrintWriter out = resp.getWriter();
        resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        out.print(response);
        out.flush();
    }
}
