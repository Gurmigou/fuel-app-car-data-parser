package com.fueladvisor.fuelappcarparserservice.model.carCharacteristics;

import lombok.*;

import javax.persistence.Embeddable;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class CarSpeed {
    private Double accelerationZeroToHundred;
    private Integer maxSpeed;
}