package com.fueladvisor.fuelappcarparserservice.model.carCharacteristics;

import lombok.*;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class CarParams {
    @Embedded
    private CarBody carBody;

    @Embedded
    private CarEngine carEngine;

    @Embedded
    private CarTransmission carTransmission;

    @Embedded
    private CarSpeed carSpeed;

    @Embedded
    private CarFuel carFuel;
}
