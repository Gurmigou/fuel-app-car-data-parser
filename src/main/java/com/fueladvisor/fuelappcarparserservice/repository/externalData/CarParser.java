package com.fueladvisor.fuelappcarparserservice.repository.externalData;

import com.fueladvisor.fuelappcarparserservice.model.carCharacteristics.Car;

import java.io.IOException;
import java.util.List;

public interface CarParser {
    List<Car> parseCarData() throws IOException;
}
