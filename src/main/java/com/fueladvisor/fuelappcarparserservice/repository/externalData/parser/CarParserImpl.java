package com.fueladvisor.fuelappcarparserservice.repository.externalData.parser;

import com.fueladvisor.fuelappcarparserservice.dto.CarBrandParseDto;
import com.fueladvisor.fuelappcarparserservice.model.carBrandInfo.CarBrand;
import com.fueladvisor.fuelappcarparserservice.model.carBrandInfo.CarModel;
import com.fueladvisor.fuelappcarparserservice.model.carCharacteristics.Car;
import com.fueladvisor.fuelappcarparserservice.repository.externalData.util.CarCharacteristicsParser;
import com.fueladvisor.fuelappcarparserservice.repository.externalData.util.ParsedCarDataWrapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class CarParserImpl implements CarParser {
    private static final String url = "https://www.drom.ru/catalog/";
    private static final String pureUrl = "https://www.drom.ru";

    @Override
    public List<Car> parseAllCarData() throws IOException {
        // parse page with brands names
        List<ParsedCarDataWrapper> wrappedBrand = getBrandNameAndUrlList();

        // parse a list of equipments of each model
        List<ParsedCarDataWrapper> wrappedEquipment = parseModelAndSpecAndEquipment(wrappedBrand);

        return wrappedEquipment.stream()
                .parallel()
                .map(this::mapParsedCarDataWrapperToCar)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    @Override
    public List<Car> parseCarDataByCarBrands(List<CarBrandParseDto> brandsParseDto) throws IOException {
        List<ParsedCarDataWrapper> wrappedBrand = mapCarBrandParseDtoToParsedCarDataWrapperList(brandsParseDto);

        // parse a list of equipments of each model
        List<ParsedCarDataWrapper> wrappedEquipment = parseModelAndSpecAndEquipment(wrappedBrand);

        return wrappedEquipment.stream()
                .parallel()
                .map(this::mapParsedCarDataWrapperToCar)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private List<ParsedCarDataWrapper> parseModelAndSpecAndEquipment(List<ParsedCarDataWrapper> wrappedBrandList) {
        // parse a list of models of each brand
        List<ParsedCarDataWrapper> wrappedModel = getModelNameAndUrlList(wrappedBrandList);

        // parse a list of specs of each model
        List<ParsedCarDataWrapper> wrappedSpec = getSpecNameAndUrlList(wrappedModel);

        // parse a list of equipments of each model
        return getEquipmentNameAndUrlList(wrappedSpec);
    }

    @Override
    public List<Car> parseCarDataByCarPages(List<String> urlList) throws IOException {
        List<Car> list = new ArrayList<>();
        for (String url : urlList) {
            Car car = new Car();
            Car parsedCar = CarCharacteristicsParser.ofCharacteristics(url, car)
                    .orElseThrow(IOException::new)
                    .parseCarType()
                    .parseReleaseStartAndEnd()
                    .parseWeight()
                    .parseLengthAndWidthAndHeight()
                    .parseClearance()
                    .parseEngineCapacity()
                    .parseHorsePower()
                    .parseTorque()
                    .parseTransmissionType()
                    .parseAccelerationZeroToHundred()
                    .parseMaxSpeed()
                    .parseFuelTankVolume()
                    .parseFuelType()
                    .parseConsumptionInCity()
                    .parseConsumptionOutsideCity()
                    .parseConsumptionAverage()
                    .getCar();
            list.add(parsedCar);
        }
        return list;
    }

    private List<ParsedCarDataWrapper> getBrandNameAndUrlList() throws IOException {
        return fetchParsedPage(url)
                .map(this::getListOfUrlsOfCarBrand)
                .orElseThrow(IOException::new)
                .stream()
                .parallel()
                .map(pair -> new ParsedCarDataWrapper()
                         .setCarBrand(pair.getFirst())
                         .setBrandUrl(pair.getSecond()))
                .collect(Collectors.toList());
    }

    private List<ParsedCarDataWrapper> getModelNameAndUrlList(List<ParsedCarDataWrapper> wrappers) {
        return wrappers
                .stream()
                .parallel()
                .flatMap(wrapper -> fetchParsedPage(wrapper.getBrandUrl())
                        .map(this::getListOfUrlsOfCarModel)
                        .orElseGet(Collections::emptyList)
                        .stream()
                        .map(pair -> new ParsedCarDataWrapper()
                                .mergeWrappers(wrapper)
                                .setCarModel(pair.getFirst())
                                .setModelUrl(pair.getSecond())
                        )
                )
                .collect(Collectors.toList());
    }

    private List<ParsedCarDataWrapper> getSpecNameAndUrlList(List<ParsedCarDataWrapper> wrappers) {
        return wrappers
                .stream()
                .parallel()
                .flatMap(wrapper -> fetchParsedPage(wrapper.getModelUrl())
                        .map(specPage -> getListOfUrlsOfCarSpec(specPage,"europe", "usa"))
                        .orElseGet(Collections::emptyList)
                        .stream()
                        .map(pair -> new ParsedCarDataWrapper()
                                .mergeWrappers(wrapper)
                                .setSpecName(pair.getFirst())
                                .setSpecUrl(String.format("%s%s/%s/%s/",
                                        url, wrapper.getCarBrand().getBrandName(),
                                        wrapper.getCarModel().getModelName(), pair.getSecond()))
                        )
                )
                .collect(Collectors.toList());
    }

    private List<ParsedCarDataWrapper> getEquipmentNameAndUrlList(List<ParsedCarDataWrapper> wrappers) {
        return wrappers
                .stream()
                .parallel()
                .flatMap(wrapper -> fetchParsedPage(wrapper.getSpecUrl())
                        .map(this::getListOfUrlsOfCarEquipment)
                        .orElseGet(Collections::emptyList)
                        .stream()
                        .map(pair -> new ParsedCarDataWrapper()
                                .mergeWrappers(wrapper)
                                .setEquipmentName(pair.getFirst())
                                .setEquipmentUrl(pureUrl + pair.getSecond())
                        )
                )
                .collect(Collectors.toList());
    }

    private Optional<Document> fetchParsedPage(String url) {
        try {
            return Optional.of(Jsoup.connect(url).get());
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    private List<Pair<CarBrand, String>> getListOfUrlsOfCarBrand(Document mainPage) {
        String brandUrlClass = "e4ojbx43";
        // TODO: 21.02.2022 добавить селениум
        return getListOfUrlsHelper(mainPage, brandUrlClass, CarBrand::new);
    }

    private List<Pair<CarModel, String>> getListOfUrlsOfCarModel(Document brandPage) {
        String modelUrlClass = "e64vuai0";
        return getListOfUrlsHelper(brandPage, modelUrlClass, CarModel::new);
    }

    private List<Pair<String, String>> getListOfUrlsOfCarSpec(Document modelPage, String... countiesFor) {
        List<Pair<String, String>> specList = new ArrayList<>();

        for (String countryFor : countiesFor) {
            Element countryBlock = modelPage.getElementById(countryFor);
            if (countryBlock == null)
                continue;

            Element specBlock = countryBlock.siblingElements().last();
            if (specBlock == null)
                continue;

            List<String> specUrls = specBlock
                    .getElementsByClass("e1ei9t6a1")
                    .stream()
                    .parallel()
                    .map(Element::attributes)
                    .map(attributes -> attributes.get("href"))
                    .collect(Collectors.toList());

            List<String> specNames = specBlock
                    .getElementsByClass("e1ei9t6a2")
                    .stream()
                    .parallel()
                    .map(element -> getSpecOfCar(element.text()))
                    .collect(Collectors.toList());

            // assert that specUrls.size() == specNames.size();

            for (int i = 0; i < specUrls.size(); i++) {
                specList.add(
                        Pair.of(specNames.get(i), specUrls.get(i))
                );
            }
        }

        return specList;
    }

    private <T> List<Pair<T, String>> getListOfUrlsHelper(
            Document document, String classParse, Function<String, T> func) {

        return document.getElementsByClass(classParse)
                .stream()
                .parallel()
                .map(element -> {
                    T wrapper = func.apply(element.text());
                    String url = element.attributes().get("href");
                    return Pair.of(wrapper, url);
                })
                .collect(Collectors.toList());
    }

    protected List<Pair<String, String>> getListOfUrlsOfCarEquipment(Document equipmentPage) {
        return equipmentPage.getElementsByClass("b-table_align_center")
                .stream()
                .parallel()
                .map(wrapper -> wrapper
                        .siblingElements()
                        .get(1)
                        .siblingElements()
                        .first())
                .map(element -> {
                    String equipmentUrl = Objects.requireNonNull(element)
                            .attributes()
                            .get("href");
                    String equipmentName = element.text();

                    return Pair.of(equipmentName, equipmentUrl);
                })
                .collect(Collectors.toList());
    }

    /**
     * Example input:
     * Audi A6 (C7) 11.2010 - 11.2014
     */
    private String getSpecOfCar(String content) {
        String[] words = content.split("\\s+");
        String specWithBrackets;

        if (words.length == 6) {
            specWithBrackets = words[2];
            int rightBracket = specWithBrackets.indexOf(')');

            if (rightBracket > 0)
                return specWithBrackets.substring(1, rightBracket);

        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 2; i < words.length - 3; i++) {
                sb.append(words[i]);

                if (i != words.length - 4)
                    sb.append(' ');
            }
            specWithBrackets = sb.toString();
        }

        return specWithBrackets;
    }

    private Car parseCarCharacteristics(String characteristicsUrl, Car car) throws IOException {
        return CarCharacteristicsParser.ofCharacteristics(characteristicsUrl, car)
                .orElseThrow(IOException::new)
                .parseCarType()
                .parseReleaseStartAndEnd()
                .parseWeight()
                .parseLengthAndWidthAndHeight()
                .parseClearance()
                .parseEngineCapacity()
                .parseHorsePower()
                .parseTorque()
                .parseTransmissionType()
                .parseAccelerationZeroToHundred()
                .parseMaxSpeed()
                .parseFuelTankVolume()
                .parseFuelType()
                .parseConsumptionInCity()
                .parseConsumptionOutsideCity()
                .parseConsumptionAverage()
                .getCar();
    }

    private Optional<Car> mapParsedCarDataWrapperToCar(ParsedCarDataWrapper data) {
        // set corresponding brand of model
        data.getCarModel().setBrand(data.getCarBrand());

        Car car = Car.builder()
                .carModel(data.getCarModel())
                .spec(data.getSpecName())
                .equipment(data.getEquipmentName())
                .build();
        try {
            return Optional.of(parseCarCharacteristics(data.getEquipmentUrl(), car));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    private List<ParsedCarDataWrapper> mapCarBrandParseDtoToParsedCarDataWrapperList(
            List<CarBrandParseDto> parseDtoList) {

        return parseDtoList.stream()
                .map(carBrandParseDto -> {
                    ParsedCarDataWrapper parsedCarDataWrapper = new ParsedCarDataWrapper();
                    parsedCarDataWrapper.setCarBrand(new CarBrand(carBrandParseDto.getBrandName()));
                    parsedCarDataWrapper.setBrandUrl(carBrandParseDto.getBrandUrl());
                    return parsedCarDataWrapper;
                })
                .collect(Collectors.toList());
    }
}