package org.bgjug.jprime.registration.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/login")
public interface LoginApi {

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    Response login(@FormParam("username") String user, @FormParam("password") String password,
        @FormParam("submit") String submit);
}
