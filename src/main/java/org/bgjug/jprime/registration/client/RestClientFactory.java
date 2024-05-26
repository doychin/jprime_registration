package org.bgjug.jprime.registration.client;

import javax.ws.rs.core.Response;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidParameterException;

import org.apache.commons.lang3.StringUtils;
import org.bgjug.jprime.registration.api.LoginApi;
import org.bgjug.jprime.registration.api.SpeakerApi;
import org.bgjug.jprime.registration.api.TicketApi;
import org.bgjug.jprime.registration.api.VisitorApi;
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

    RestClientFactory(String address) {
        URL url;
        try {
            url = new URL(address);
        } catch (MalformedURLException e) {
            throw new InvalidParameterException("RestClientFactory requires a valid address");
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
            throw new IllegalStateException("RestClientFactory has not been initialized");
        }
        return instance;
    }

    private static synchronized RestClientFactory getInstance(String address) {
        if (instance == null) {
            if (StringUtils.isBlank(address)) {
                throw new InvalidParameterException("RestClientFactory requires a valid address");
            }
            new RestClientFactory(address);
        }
        return instance;
    }

    public static LoginApi loginApi(String address) {
        return getInstance(address).loginApi;
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

    public static boolean initializeCredentials(String url, String userName, String password) {
        try (Response r = loginApi(url).login(userName, password, "Submit")) {
            if (r.getStatus() != 302 || !r.getHeaderString("Location").contains("admin")) {
                return false;
            }

            SECURED_CALL_REQUEST_FILTER.initialize(loginApi(url), userName, password,
                r.getCookies().get("JSESSIONID").getValue());
        }
        return true;
    }
}
