package com.fueladvisor.fuelappcarparserservice.model.carCharacteristics;

import lombok.*;

import javax.persistence.Embeddable;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class CarFuel {
    private Integer tankVolume;
    private CarFuelConsumptionType fuelType;
    private Double consumptionInCity;
    private Double consumptionOutsideCity;
    private Double consumptionAverage;
}
