
package com.ifelseelif.servlets;

import com.ifelseelif.dao.models.Organization;
import com.ifelseelif.services.OrganizationServiceImp;
import com.ifelseelif.servlets.exceptions.HttpException;
import com.ifelseelif.servlets.models.Body;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;


@Path("/organizations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrganizationResource {
    OrganizationServiceImp service = new OrganizationServiceImp();

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") int id) {
        try {
            return Response.ok(service.getById(id)).build();
        } catch (HttpException e) {
            Body body = new Body(e.getMessage());
            return Response.status(e.getStatusCode()).entity(body).build();
        }
    }

    @GET
    public Response getAll(@Context HttpServletRequest request) {
        try {
            List<Organization> organizationList = service.getAll(request.getParameterMap());
            return Response.ok(organizationList).build();
        } catch (HttpException e) {
            Body body = new Body(e.getMessage());
            return Response.status(e.getStatusCode()).entity(body).build();
        }
    }

    @POST
    public Response add(Organization organization) {
        try {
            service.save(organization);
            return Response.ok().build();
        } catch (HttpException e) {
            Body body = new Body(e.getMessage());
            return Response.status(e.getStatusCode()).entity(body).build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response updateById(@PathParam("id") int id, Organization organization) {
        try {
            service.updateById(id, organization);
            return Response.ok().build();
        } catch (HttpException e) {
            Body body = new Body(e.getMessage());
            return Response.status(e.getStatusCode()).entity(body).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteById(@PathParam("id") int id) {
        try {
            service.deleteById(id);
            return Response.ok().build();
        } catch (HttpException e) {
            Body body = new Body(e.getMessage());
            return Response.status(e.getStatusCode()).entity(body).build();
        }
    }
}