package com.fueladvisor.fuelappcarparserservice.model.carBrandInfo;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "car_brand")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarBrand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false, name = "brand_name", unique = true)
    private String brandName;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "brand")
    private List<CarModel> modelList;

    public CarBrand(String brandName) {
        this.brandName = brandName;
    }
}