package org.bgjug.jprime.registration.client;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.bgjug.jprime.registration.api.LoginApi;

public class SecuredCallRequestFilter implements ClientRequestFilter {

    private String cookie = null;

    private LocalDateTime lastUsed;

    private String userName;

    private String password;

    private LoginApi loginApi;

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        if (StringUtils.isEmpty(cookie) || lastUsed.isBefore(LocalDateTime.now().minusMinutes(5))) {
            cookie = performLogin(userName, password);
        }

        if (StringUtils.isNotEmpty(cookie)) {
            lastUsed = LocalDateTime.now();
            requestContext.getHeaders().computeIfAbsent("Cookie", k-> new ArrayList<>()).add(new Cookie("JSESSIONID", cookie));
        }
    }

    private String performLogin(String userName, String password) {
        try (Response r = loginApi.login(userName, password, "Submit")) {
            if (r.getStatus() != 302 || !r.getHeaderString("Location").contains("admin")) {
                return null;
            }

            return r.getCookies().get("JSESSIONID").getValue();
        }
    }

    public void initialize(LoginApi loginApi, String userName, String password, String jsessionid) {
        lastUsed = LocalDateTime.now();
        this.loginApi = loginApi;
        this.userName = userName;
        this.password = password;
        this.cookie = jsessionid;
    }
}
