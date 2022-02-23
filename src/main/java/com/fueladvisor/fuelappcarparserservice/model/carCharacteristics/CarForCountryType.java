package com.fueladvisor.fuelappcarparserservice.model.carCharacteristics;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum CarForCountryType {
    EUROPE("Europe"),
    USA("USA");

    private final String country;
}
