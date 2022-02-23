package com.fueladvisor.fuelappcarparserservice.model.carCharacteristics;

import lombok.*;

import javax.persistence.Embeddable;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class CarBody {
    private Integer weight;
    private Integer length;
    private Integer width;
    private Integer height;
    private Integer clearance;
}