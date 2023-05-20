package org.bgjug.jprime.registration.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Speaker {

    private final String name;

    private final String email;

    private final String twitter;

    private final String company;

    private final String firstName;

    private final String lastName;

    private final boolean featured;

    private boolean printed;

    @JsonCreator
    public Speaker(@JsonProperty("name") String name, @JsonProperty("email") String email,
        @JsonProperty("twitter") String twitter, @JsonProperty("company") String company,
        @JsonProperty("firstName") String firstName, @JsonProperty("lastName") String lastName,
        @JsonProperty("featured") boolean featured) {
        this.name = name;
        this.email = email;
        this.twitter = twitter;
        this.company = company;
        this.firstName = firstName;
        this.lastName = lastName;
        this.featured = featured;
    }

    public boolean isPrinted() {
        return printed;
    }

    public void setPrinted(boolean printed) {
        this.printed = printed;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getTwitter() {
        return twitter;
    }

    public String getCompany() {
        return company;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public boolean isFeatured() {
        return featured;
    }

}
