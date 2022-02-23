package com.fueladvisor.fuelappcarparserservice.model.carBrandInfo;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "car_model")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false, name = "model_name", unique = true)
    private String modelName;

    @ManyToOne
    private CarBrand brand;

    public CarModel(String modelName) {
        this.modelName = modelName;
    }
}
