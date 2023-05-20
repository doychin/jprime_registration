package org.bgjug.jprime.registration.api;

import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/visitor")
@Produces(MediaType.APPLICATION_JSON)
public interface VisitorApi {

    @GET
    @Path("{branch}")
    Response allVisitors(@PathParam("branch") String branch, @CookieParam("JSESSIONID") String session);

    @GET
    @Path("{branch}/{ticket}")
    Response visitorByTicket(@PathParam("branch") String branch, @PathParam("ticket") String ticket,
        @CookieParam("JSESSIONID") String session);

    @POST
    @Path("search/{branch}")
    Response visitorSearch(@PathParam("branch") String branch, VisitorSearch visitorSearch,
        @CookieParam("JSESSIONID") String session);
}
