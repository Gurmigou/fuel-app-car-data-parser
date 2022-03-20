package com.fueladvisor.fuelappcarparserservice.model.carCharacteristics;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum CarForMarket {
    EUROPE("Europe"),
    USA("USA");

    private final String country;
}
