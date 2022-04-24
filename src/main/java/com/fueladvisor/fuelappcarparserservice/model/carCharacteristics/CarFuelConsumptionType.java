package com.fueladvisor.fuelappcarparserservice.model.carCharacteristics;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum CarFuelConsumptionType {
    PETROL("petrol"),
    GAS("gas"),
    DIESEL("diesel"),
    ELECTRICITY("electricity");

    private final String name;

    public static CarFuelConsumptionType getFuelConsumptionTypeByParsedName(String parsedType) {
        if (parsedType.equals("Дизельное топливо"))
            return DIESEL;
        if (parsedType.equals("Газ"))
            return GAS;
        if (parsedType.equals("Бензин"))
            return PETROL;
        return ELECTRICITY;
    }
}
