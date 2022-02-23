package com.fueladvisor.fuelappcarparserservice.model.carCharacteristics;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CarDriveType {
    FRONT_WHEEL("front-wheel"),
    REAR_WHEEL("rear-wheel"),
    FOUR_WHEEL("four-wheel");

    private final String name;
}