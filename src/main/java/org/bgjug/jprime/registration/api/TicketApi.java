package org.bgjug.jprime.registration.api;

import javax.ws.rs.CookieParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/ticket")
@Produces(MediaType.APPLICATION_JSON)
public interface TicketApi {

    @POST
    @Path("{ticket}")
    Response confirmVisitorRegistration(@PathParam("ticket") String ticket,
        @CookieParam("JSESSIONID") String session);

}
