package com.fueladvisor.fuelappcarparserservice.dto;

import com.fueladvisor.fuelappcarparserservice.model.carCharacteristics.CarParams;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarDto {
    private String brand;
    private String model;
    private String specification;
    private String equipment;
    private Integer releaseStartYear;
    private Integer releaseEndYear;
    private CarParams carParams;
}
