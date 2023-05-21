package org.bgjug.jprime.registration.api;

import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;

@Path("/api/visitor")
@Produces(MediaType.APPLICATION_JSON)
public interface VisitorApi {

    @GET
    @Path("{branch}")
    Response allVisitors(@PathParam("branch") String branch);

    @GET
    @Path("{branch}/{ticket}")
    Response visitorByTicket(@PathParam("branch") String branch, @PathParam("ticket") String ticket);

    @POST
    @Path("search/{branch}")
    Response visitorSearch(@PathParam("branch") String branch, VisitorSearch visitorSearch);
}
