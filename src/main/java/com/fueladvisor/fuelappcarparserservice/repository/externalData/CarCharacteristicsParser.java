package com.fueladvisor.fuelappcarparserservice.repository.externalData;

import com.fueladvisor.fuelappcarparserservice.model.carCharacteristics.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

public class CarCharacteristicsParser {
    private CarParams carParams;
    private final CarEngine carEngine;
    private final CarTransmission carTransmission;
    private final CarSpeed carSpeed;
    private final CarFuel carFuel;
    private final Document document;


    private CarCharacteristicsParser(CarEngine carEngine,
                                     CarTransmission carTransmission,
                                     CarSpeed carSpeed, CarFuel carFuel,
                                     Document document) {

        this.carEngine = carEngine;
        this.carTransmission = carTransmission;
        this.carSpeed = carSpeed;
        this.carFuel = carFuel;
        this.document = document;
    }

    public static Optional<CarCharacteristicsParser> ofURL(String url) {
        try {
            var document = Jsoup.connect(url).get();
            return Optional.of(
                    new CarCharacteristicsParser(
                            new CarEngine(), new CarTransmission(), new CarSpeed(), new CarFuel(), document)
            );
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    public static void main(String[] args) throws IOException {
        CarCharacteristicsParser.ofURL("https://www.drom.ru/catalog/volkswagen/polo/236896/")
                .orElseThrow(IOException::new)
                .parseCarType();
    }

    public CarCharacteristicsParser parseWeight() {
        return null;
    }

    public CarCharacteristicsParser parseLength() {
        return null;
    }

    public CarCharacteristicsParser parseWidth() {
        return null;
    }

    public CarCharacteristicsParser parseHeight() {
        return null;
    }

    public CarCharacteristicsParser parseClearance() {
        return null;
    }

    public CarCharacteristicsParser parseEngineCapacity() {
        return null;
    }

    public CarCharacteristicsParser parseHorsePower() {
        return null;
    }

    public CarCharacteristicsParser parseTorque() {
        return null;
    }

    public CarCharacteristicsParser parseTransmissionType() {
        return null;
    }

    public CarCharacteristicsParser parseAccelerationZeroToHundred() {
        return null;
    }

    public CarCharacteristicsParser parseMaxSpeed() {
        return null;
    }

    public CarCharacteristicsParser parseFuelTankVolume() {
        return null;
    }

    public CarCharacteristicsParser parseFuelType() {
        return null;
    }

    public CarCharacteristicsParser parseConsumptionInCity() {
        return null;
    }

    public CarCharacteristicsParser parseConsumptionOutsideCity() {
        return null;
    }

    public CarCharacteristicsParser parseConsumptionAverage() {
        return null;
    }

    public CarCharacteristicsParser parseCarType() {


//        "td:contains(Тип кузова)"
    }

    public CarCharacteristicsParser parseReleaseStart() {
        return null;
    }

    public CarCharacteristicsParser parseReleaseEnd() {
        return null;
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
}
