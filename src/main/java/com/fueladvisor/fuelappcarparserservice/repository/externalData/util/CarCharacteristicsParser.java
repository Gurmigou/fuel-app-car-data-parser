package com.fueladvisor.fuelappcarparserservice.repository.externalData.util;

import com.fueladvisor.fuelappcarparserservice.model.carCharacteristics.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Optional;

import static com.fueladvisor.fuelappcarparserservice.model.carCharacteristics.CarDriveType.getTypeByParsedName;
import static com.fueladvisor.fuelappcarparserservice.model.carCharacteristics.CarFuelConsumptionType.getFuelConsumptionTypeByParsedName;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
import static java.util.Objects.requireNonNull;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CarCharacteristicsParser {
    private final CarBody carBody;
    private final CarEngine carEngine;
    private final CarTransmission carTransmission;
    private final CarSpeed carSpeed;
    private final CarFuel carFuel;

    private final Document document;
    private final Car car;

    public static Optional<CarCharacteristicsParser> ofCharacteristics(String url, Car car) {
        try {
            var document = Jsoup.connect(url).get();
            return Optional.of(
                    new CarCharacteristicsParser(
                            new CarBody(),
                            new CarEngine(),
                            new CarTransmission(),
                            new CarSpeed(),
                            new CarFuel(),
                            document,
                            car
                    )
            );
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    public Car getCar() {
        CarParams params = CarParams.builder()
                .carBody(carBody)
                .carEngine(carEngine)
                .carTransmission(carTransmission)
                .carSpeed(carSpeed)
                .carFuel(carFuel)
                .build();
        this.car.setCarParams(params);
        return this.car;
    }

    public CarCharacteristicsParser parseWeight() {
        Optional<String> weightOption = parseElement("td:contains(Масса)");
        weightOption.ifPresent(weight -> carBody.setWeight(parseInt(weight)));
        return this;
    }

    public CarCharacteristicsParser parseLengthAndWidthAndHeight() {
        Optional<String> xyzOptional = parseElement("td:contains(Габариты кузова)");
        xyzOptional.ifPresent(xyz -> {
            // split x, y, z dimensions of the car
            String[] dimensions = xyz.split("\\sx\\s");

            carBody.setLength(parseInt(dimensions[0]));
            carBody.setWidth(parseInt(dimensions[1]));
            carBody.setHeight(parseInt(dimensions[2]));
        });
        return this;
    }

    public CarCharacteristicsParser parseClearance() {
        Optional<String> clearanceOptional = parseElement("td:contains(Клиренс)");
        clearanceOptional.ifPresent(clearance -> carBody.setClearance(parseInt(clearance)));
        return this;
    }

    public CarCharacteristicsParser parseEngineCapacity() {
        Optional<String> capacityOptional = parseElement("td:contains(Объем)");
        capacityOptional.ifPresent(capacity -> carEngine.setCapacity(parseDouble(capacity)));
        return this;
    }

    public CarCharacteristicsParser parseHorsePower() {
        Optional<String> horsePowerOptional = parseElement("td:contains(Мощность)");
        horsePowerOptional.ifPresent(horsePower -> carEngine.setHorsePower(parseInt(horsePower)));
        return this;
    }

    public CarCharacteristicsParser parseTorque() {
        Optional<String> torqueOptional = parseElement("td:contains(Максимальный крутящий момент)");
        torqueOptional.ifPresent(torque -> carEngine.setTorque(parseInt(torque)));
        return this;
    }

    public CarCharacteristicsParser parseTransmissionType() {
        Optional<String> transmissionTypeOptional = parseElement("td:contains(Привод)");
        transmissionTypeOptional.ifPresent(transmissionTypeParsed -> {
            CarDriveType type = getTypeByParsedName(transmissionTypeParsed.split("\\s")[0]);
            carTransmission.setDriveType(type);
        });
        return this;
    }

    public CarCharacteristicsParser parseAccelerationZeroToHundred() {
        Optional<String> accelerationOptional = parseElement("td:contains(Время разгона)");
        accelerationOptional.ifPresent(acceleration ->
                carSpeed.setAccelerationZeroToHundred(parseDouble(acceleration)));
        return this;
    }

    public CarCharacteristicsParser parseMaxSpeed() {
        Optional<String> maxSpeedOptional = parseElement("td:contains(Максимальная скорость)");
        maxSpeedOptional.ifPresent(maxSpeed -> carSpeed.setMaxSpeed(parseInt(maxSpeed)));
        return this;
    }

    public CarCharacteristicsParser parseFuelTankVolume() {
        Optional<String> tankVolumeOptional = parseElement("td:contains(Объем топливного бака)");
        tankVolumeOptional.ifPresent(tankVolume -> carFuel.setTankVolume(parseInt(tankVolume)));
        return this;
    }

    public CarCharacteristicsParser parseFuelType() {
        Optional<String> fuelTypeOptional = parseElement("td:contains(Тип топлива)");
        fuelTypeOptional.ifPresent(fuelType -> {
            CarFuelConsumptionType fuelConsumptionType = getFuelConsumptionTypeByParsedName(fuelType);
            carFuel.setFuelType(fuelConsumptionType);
        });
        return this;
    }

    public CarCharacteristicsParser parseConsumptionInCity() {
        Optional<String> inCityConsOptional = parseElement(
                "td:contains(Расход топлива в городском цикле)");
        inCityConsOptional.ifPresent(inCityCons ->
                carFuel.setConsumptionInCity(parseDouble(inCityCons)));
        return this;
    }

    public CarCharacteristicsParser parseConsumptionOutsideCity() {
        Optional<String> outsideCityConsOptional = parseElement(
                "td:contains(Расход топлива за городом)");
        outsideCityConsOptional.ifPresent(outsideCityCons ->
                carFuel.setConsumptionOutsideCity(parseDouble(outsideCityCons)));
        return this;
    }

    public CarCharacteristicsParser parseConsumptionAverage() {
        Optional<String> averageCityConsOptional = parseElement(
                "td:contains(Расход топлива в смешанном цикле)");
        averageCityConsOptional.ifPresent(averageCityCons ->
                carFuel.setConsumptionAverage(parseDouble(averageCityCons)));
        return this;
    }

    public CarCharacteristicsParser parseCarType() {
        Optional<String> type = parseElement("td:contains(Тип кузова)");
        type.ifPresent(car::setType);
        return this;
    }

    public CarCharacteristicsParser parseReleaseStartAndEnd() {
        Optional<String> startAndEndYearOptional = parseElement("td:contains(Период выпуска)");
        startAndEndYearOptional.ifPresent(startAndEndYear -> {
            String[] years = startAndEndYear.split("\\s");

            if (isNumber(years[0]))
                car.setReleaseStartYear(parseInt(years[0]));
            else
                car.setReleaseStartYear(null);

            if (isNumber(years[1]))
                car.setReleaseEndYear(parseInt(years[1]));
            else
                car.setReleaseEndYear(null);
        });
        return this;
    }

    private Optional<String> parseElement(String selectQuery) {
        try {
            String text = requireNonNull(requireNonNull(
                    document
                            .clone()
                            .select(selectQuery)
                            .first())
                    .siblingElements()
                    .last())
                    .text();
            return Optional.of(text);
        } catch (NullPointerException e) {
            return Optional.empty();
        }
    }

    private boolean isNumber(String s) {
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (ch < '0' || ch > '9')
                return false;
        }
        return true;
    }
}