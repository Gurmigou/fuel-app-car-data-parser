package com.fueladvisor.fuelappcarparserservice.model.carCharacteristics;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum CarFuelConsumptionType {
    PETROL("petrol"),
    GAS("gas"),
    ELECTRICITY("electricity");

    private final String name;
}
