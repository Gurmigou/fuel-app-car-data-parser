package com.fueladvisor.fuelappcarparserservice.repository.externalData;

import com.fueladvisor.fuelappcarparserservice.model.carBrandInfo.CarBrand;
import com.fueladvisor.fuelappcarparserservice.model.carBrandInfo.CarModel;
import com.fueladvisor.fuelappcarparserservice.model.carCharacteristics.Car;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class CarParserImpl implements CarParser {
    private static final String url = "https://www.drom.ru/catalog/";

    @Override
    public List<Car> parseCarData() throws IOException {
        // parse page with brands names
        var wrappedBrand = getBrandNameAndUrlList();

        // parse a list of models of each brand
        var wrappedModel = getModelNameAndUrlList(wrappedBrand);

        // parse a list of specs of each model
        var wrappedSpec = getSpecNameAndUrlList(wrappedModel);
        int a = 5;

        // TODO: 23.02.2022
        return null;
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
                                .setSpecUrl(String.format("%s/%s/%s/%s",
                                        url, wrapper.getCarBrand().getBrandName(),
                                        wrapper.getCarModel().getModelName(), pair.getSecond()))
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

    /**
     * Example input:
     * Audi A6 (C7) 11.2010 - 11.2014
     */
    private String getSpecOfCar(String content) {
        System.out.println("Content: " + content);

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

    public static void main(String[] args) throws IOException {
        var car = new CarParserImpl();
        car.parseCarData();
    }
}
