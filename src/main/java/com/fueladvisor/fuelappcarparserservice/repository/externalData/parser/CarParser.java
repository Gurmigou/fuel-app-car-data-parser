package com.fueladvisor.fuelappcarparserservice.repository.externalData.parser;

import com.fueladvisor.fuelappcarparserservice.dto.CarBrandParseDto;
import com.fueladvisor.fuelappcarparserservice.model.carCharacteristics.Car;

import java.io.IOException;
import java.util.List;

public interface CarParser {
    List<Car> parseAllCarData() throws IOException;

    List<Car> parseCarDataByCarBrands(List<CarBrandParseDto> brandsParseDto) throws IOException;

    List<Car> parseCarDataByCarPages(List<String> urlList) throws IOException;
}
