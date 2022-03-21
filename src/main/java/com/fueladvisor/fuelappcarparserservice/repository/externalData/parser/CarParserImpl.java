package com.fueladvisor.fuelappcarparserservice.repository.externalData.parser;

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

    public static void main(String[] args) throws IOException {
        var app = new CarParserImpl();
        List<Car> cars = app.parseCarData();
        cars.forEach(System.out::println);
    }

    @Override
    public List<Car> parseCarData() throws IOException {

        long before = System.currentTimeMillis();

        // parse page with brands names
        var wrappedBrand = getBrandNameAndUrlList();

        // parse a list of models of each brand
        var wrappedModel = getModelNameAndUrlList(wrappedBrand);

        // parse a list of specs of each model
        var wrappedSpec = getSpecNameAndUrlList(wrappedModel);

        long after = System.currentTimeMillis();

        System.out.println(after - before);

        // parse a list of equipments of each model
        var wrappedEquipment = getEquipmentNameAndUrlList(wrappedSpec);

        return wrappedEquipment.stream()
                .map(this::mapParsedCarDataWrapperToCar)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private List<ParsedCarDataWrapper> getBrandNameAndUrlList() throws IOException {
        return fetchParsedPage(url)
                .map(this::getListOfUrlsOfCarBrand)
                .orElseThrow(IOException::new)
                .stream()
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
        var specList = new ArrayList<Pair<String, String>>();

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
                    .map(Element::attributes)
                    .map(attributes -> attributes.get("href"))
                    .collect(Collectors.toList());

            List<String> specNames = specBlock
                    .getElementsByClass("e1ei9t6a2")
                    .stream()
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
                .map(element -> {
                    T wrapper = func.apply(element.text());
                    String url = element.attributes().get("href");
                    return Pair.of(wrapper, url);
                })
                .collect(Collectors.toList());
    }

    private List<Pair<String, String>> getListOfUrlsOfCarEquipment(Document equipmentPage) {
        return equipmentPage.getElementsByClass("b-table_align_center")
                .stream()
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
            var sb = new StringBuilder();
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
//                .parseForMarket()
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
}