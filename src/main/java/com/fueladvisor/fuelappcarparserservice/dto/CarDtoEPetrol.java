package com.fueladvisor.fuelappcarparserservice.dto;

import com.fueladvisor.fuelappcarparserservice.model.carCharacteristics.CarFuel;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarDtoEPetrol {
    private String brand;
    private String model;
    private String specification;
    private String equipment;
    private Integer releaseStartYear;
    private Integer releaseEndYear;
    private CarFuel carFuelInfo;
}
