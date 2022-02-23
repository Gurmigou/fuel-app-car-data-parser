package com.fueladvisor.fuelappcarparserservice.model.carCharacteristics;

import com.fueladvisor.fuelappcarparserservice.model.carBrandInfo.CarModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
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

    @Enumerated(EnumType.ORDINAL)
    private CarForCountryType forCountry;

    @Column(name = "release_year_start")
    private Integer releaseYearStart;
    @Column(name = "relase_year_end")
    private Integer releaseYearEnd;

    @Embedded
    private CarParams carParams;

    @ManyToOne
    private CarModel carModel;
}