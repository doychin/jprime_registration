package org.bgjug.jprime.registration;

import java.time.LocalDate;

import org.eclipse.microprofile.config.ConfigProvider;

public class Globals {

    public static final String YEAR = ConfigProvider.getConfig()
        .getOptionalValue("org.bgjug.jprime.registration.year", String.class)
        .orElse("2024");

    public static final LocalDate FIRST_DAY = ConfigProvider.getConfig()
        .getOptionalValue("org.bgjug.jprime.registration.first.day", LocalDate.class)
        .orElse(LocalDate.now());

    public static final LocalDate SECOND_DAY = ConfigProvider.getConfig()
        .getOptionalValue("org.bgjug.jprime.registration.second.day", LocalDate.class)
        .orElse(LocalDate.now().plusDays(1));
}
