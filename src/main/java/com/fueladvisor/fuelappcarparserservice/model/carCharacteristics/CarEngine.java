package com.fueladvisor.fuelappcarparserservice.model.carCharacteristics;

import lombok.*;

import javax.persistence.Embeddable;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class CarEngine {
    private Double capacity;
    private Double horsePower;
    private Integer torque;
}