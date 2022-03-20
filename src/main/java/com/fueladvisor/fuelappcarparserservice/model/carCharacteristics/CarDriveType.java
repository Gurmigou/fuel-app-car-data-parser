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

    public static CarDriveType getTypeByParsedName(String parsedType) {
        if (parsedType.equals("Передний"))
            return FRONT_WHEEL;
        if (parsedType.equals("Задний"))
            return REAR_WHEEL;

        // TODO: 20.03.2022 для проверки
        System.out.println(parsedType);

        return FOUR_WHEEL;
    }
}