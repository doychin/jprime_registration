package org.bgjug.jprime.registration.api;

public class SpeakerSearch {
    private final String firstName;

    private final String lastName;

    private final String email;

    public SpeakerSearch(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

}
