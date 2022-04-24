package com.fueladvisor.fuelappcarparserservice.controller;

import com.fueladvisor.fuelappcarparserservice.dto.CarBrandParseDto;
import com.fueladvisor.fuelappcarparserservice.dto.CarDto;
import com.fueladvisor.fuelappcarparserservice.dto.CarDtoEPetrol;
import com.fueladvisor.fuelappcarparserservice.model.carCharacteristics.Car;
import com.fueladvisor.fuelappcarparserservice.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/car-data")
public class CarController {
    private final CarService carService;

    @Autowired
    public CarController(CarService carService) {
        this.carService = carService;
    }

    @PostMapping("/parse-all")
    public ResponseEntity<?> parseCars() {
        try {
            List<Car> cars = carService.parseCarData();
            return ResponseEntity
                    .ok(String.format("%d cars were parsed.", cars.size()));
        } catch (IOException e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.toString());
        }
    }

    @PostMapping("/parse-car-brand-pages")
    public ResponseEntity<?> parseCarDataByCarBrands(@RequestBody List<CarBrandParseDto> brandsParseDto) {
        try {
            List<CarDtoEPetrol> carDtoEPetrol = carService.parseCarDataByCarBrands(brandsParseDto);
            return ResponseEntity.ok(carDtoEPetrol);
        } catch (IOException e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.toString());
        }
    }

    @PostMapping("/parse-car-pages")
    public ResponseEntity<?> parseCarDataByCarPages(@RequestBody List<String> urlList) {
        try {
            List<CarDto> carDtoList = carService.parseCarDataByCarPages(urlList);
            return ResponseEntity.ok(carDtoList);
        } catch (IOException e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.toString());
        }
    }
}