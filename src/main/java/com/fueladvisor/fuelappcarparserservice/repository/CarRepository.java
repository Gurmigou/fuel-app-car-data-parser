package com.fueladvisor.fuelappcarparserservice.repository;

import com.fueladvisor.fuelappcarparserservice.model.carCharacteristics.Car;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarRepository extends CrudRepository<Car, Integer> {
}
