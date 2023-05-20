package org.bgjug.jprime.registration;

import java.net.MalformedURLException;
import java.net.URL;

import org.bgjug.jprime.registration.api.LoginApi;
import org.bgjug.jprime.registration.api.SpeakerApi;
import org.bgjug.jprime.registration.api.TicketApi;
import org.bgjug.jprime.registration.api.VisitorApi;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

public class RestClientFactory {

    private final RestClientBuilder restClientBuilder;

    private static RestClientFactory instance;

    RestClientFactory() {
        URL url;
        try {
            url = new URL("http://localhost:8080");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        restClientBuilder = RestClientBuilder.newBuilder()
            .property("microprofile.rest.client.disable.default.mapper", Boolean.TRUE)
            .baseUrl(url)
            .followRedirects(true);

        instance = this;
    }

    private static RestClientFactory getInstance() {
        if (instance == null) {
            new RestClientFactory();
        }
        return instance;
    }

    public static LoginApi loginApi() {
        return getInstance().restClientBuilder.build(LoginApi.class);
    }

    public static VisitorApi visitorApi() {
        return getInstance().restClientBuilder.build(VisitorApi.class);
    }

    public static TicketApi ticketApi() {
        return getInstance().restClientBuilder.build(TicketApi.class);
    }

    public static SpeakerApi speakerApi() {
        return getInstance().restClientBuilder.build(SpeakerApi.class);
    }
}
