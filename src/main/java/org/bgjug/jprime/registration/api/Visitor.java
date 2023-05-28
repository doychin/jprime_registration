package org.bgjug.jprime.registration.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Visitor implements Comparable<Visitor> {

    private final String name;

    private final String email;

    private final String company;

    private final String ticket;

    @JsonCreator
    public Visitor(@JsonProperty("name") String name, @JsonProperty("email") String email,
        @JsonProperty("company") String company, @JsonProperty("ticket") String ticket) {
        this.name = name;
        this.email = email;
        this.company = company;
        this.ticket = ticket;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getCompany() {
        return company;
    }

    public String getTicket() {
        return ticket;
    }

    @Override
    public int compareTo(Visitor o) {
        return getName().compareTo(o.getName());
    }
}
