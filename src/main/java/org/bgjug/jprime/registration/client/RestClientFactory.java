package org.bgjug.jprime.registration.client;

import javax.swing.*;
import javax.ws.rs.core.Response;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

import org.bgjug.jprime.registration.api.LoginApi;
import org.bgjug.jprime.registration.api.SpeakerApi;
import org.bgjug.jprime.registration.api.TicketApi;
import org.bgjug.jprime.registration.api.VisitorApi;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

public class RestClientFactory {

    private final RestClientBuilder restClientBuilder;

    private static RestClientFactory instance;

    private static final SecuredCallRequestFilter SECURED_CALL_REQUEST_FILTER =
        new SecuredCallRequestFilter();

    private final LoginApi loginApi;

    private final VisitorApi visitorApi;

    private final TicketApi ticketApi;

    private final SpeakerApi speakerApi;

    RestClientFactory() {
        Optional<String> restEndPointUrl =
            ConfigProvider.getConfig().getOptionalValue("org.bgjug.jprime.registration.url", String.class);
        URL url;
        try {
            url = new URL(restEndPointUrl.orElse("http://localhost:8080"));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        restClientBuilder = RestClientBuilder.newBuilder()
            .property("microprofile.rest.client.disable.default.mapper", Boolean.TRUE)
            .baseUrl(url)
            .followRedirects(true);

        loginApi = restClientBuilder.build(LoginApi.class);
        visitorApi = restClientBuilder.register(SECURED_CALL_REQUEST_FILTER).build(VisitorApi.class);
        ticketApi = restClientBuilder.register(SECURED_CALL_REQUEST_FILTER).build(TicketApi.class);
        speakerApi = restClientBuilder.register(SECURED_CALL_REQUEST_FILTER).build(SpeakerApi.class);

        instance = this;
    }

    private static RestClientFactory getInstance() {
        if (instance == null) {
            new RestClientFactory();
        }
        return instance;
    }

    public static LoginApi loginApi() {
        return getInstance().loginApi;
    }

    public static VisitorApi visitorApi() {
        return getInstance().visitorApi;
    }

    public static TicketApi ticketApi() {
        return getInstance().ticketApi;
    }

    public static SpeakerApi speakerApi() {
        return getInstance().speakerApi;
    }

    public static boolean initializeCredentials(String userName, String password) {
        try (Response r = loginApi().login(userName, password, "Submit")) {
            if (r.getStatus() != 302 || !r.getHeaderString("Location").contains("admin")) {
                return false;
            }

            SECURED_CALL_REQUEST_FILTER.initialize(loginApi(), userName, password,
                r.getCookies().get("JSESSIONID").getValue());
        }
        return true;
    }
}
