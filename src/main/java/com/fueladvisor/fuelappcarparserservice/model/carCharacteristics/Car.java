package com.fueladvisor.fuelappcarparserservice.model.carCharacteristics;

import com.fueladvisor.fuelappcarparserservice.model.carBrandInfo.CarModel;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String spec;
    private String type;
    private String equipment;

//    @Enumerated(EnumType.ORDINAL)
//    private CarForMarket forMarket;

    @Column(name = "release_start_year")
    private Integer releaseStartYear;
    @Column(name = "relase_end_year")
    private Integer releaseEndYear;

    @Embedded
    private CarParams carParams;

    @ManyToOne
    private CarModel carModel;
}