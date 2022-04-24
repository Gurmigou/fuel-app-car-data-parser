package com.fueladvisor.fuelappcarparserservice.service;

import com.fueladvisor.fuelappcarparserservice.dto.CarBrandParseDto;
import com.fueladvisor.fuelappcarparserservice.dto.CarDto;
import com.fueladvisor.fuelappcarparserservice.dto.CarDtoEPetrol;
import com.fueladvisor.fuelappcarparserservice.model.carCharacteristics.Car;
import com.fueladvisor.fuelappcarparserservice.repository.CarRepository;
import com.fueladvisor.fuelappcarparserservice.repository.externalData.parser.CarParser;
import com.fueladvisor.fuelappcarparserservice.repository.externalData.parser.CarParserImplLite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CarService {
    private final CarParser carParser;
    private final CarRepository carRepository;

    @Autowired
    public CarService(CarParserImplLite carParserImplLite, CarRepository carRepository) {
        this.carParser = carParserImplLite;
        this.carRepository = carRepository;
    }

    @Transactional(rollbackOn = Exception.class)
    public List<Car> parseCarData() throws IOException {
        List<Car> cars = carParser.parseAllCarData();
        carRepository.saveAll(cars);
        return cars;
    }

    @Transactional(rollbackOn = Exception.class)
    public List<CarDto> parseCarDataByCarPages(List<String> urlList) throws IOException {
        List<Car> cars = carParser.parseCarDataByCarPages(urlList);
        carRepository.saveAll(cars);

        return cars.stream()
                .map(this::mapCarToCarDto)
                .collect(Collectors.toList());
    }

    @Transactional(rollbackOn = Exception.class)
    public List<CarDtoEPetrol> parseCarDataByCarBrands(List<CarBrandParseDto> brandsParseDto) throws IOException {
        List<Car> cars = carParser.parseCarDataByCarBrands(brandsParseDto);
        carRepository.saveAll(cars);

        return cars.stream()
                .map(this::mapCarToCarEPetrolDto)
                .collect(Collectors.toList());
    }

    private CarDto mapCarToCarDto(Car car) {
        return CarDto.builder()
                .brand(car.getCarModel().getBrand().getBrandName())
                .model(car.getCarModel().getModelName())
                .specification(car.getSpec())
                .equipment(car.getEquipment())
                .releaseStartYear(car.getReleaseStartYear())
                .releaseEndYear(car.getReleaseEndYear())
                .carParams(car.getCarParams())
                .build();
    }

    private CarDtoEPetrol mapCarToCarEPetrolDto(Car car) {
        return CarDtoEPetrol.builder()
                .brand(car.getCarModel().getBrand().getBrandName())
                .model(car.getCarModel().getModelName())
                .specification(car.getSpec())
                .equipment(car.getEquipment())
                .releaseStartYear(car.getReleaseStartYear())
                .releaseEndYear(car.getReleaseEndYear())
                .carFuelInfo(car.getCarParams().getCarFuel())
                .build();
    }
}