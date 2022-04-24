package com.fueladvisor.fuelappcarparserservice.repository.externalData.parser;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class CarParserImplLite extends CarParserImpl {

    @Override
    protected List<Pair<String, String>> getListOfUrlsOfCarEquipment(Document equipmentPage) {
        int[] wrapped = {0};

        return equipmentPage.getElementsByClass("b-table_align_center")
                .stream()
                .parallel()
                .map(wrapper -> {
                    Element element = null;
                    if (wrapped[0] % 3 == 0) {
                        element = wrapper
                                .children()
                                .get(1)
                                .children()
                                .first();
                    }
                    wrapped[0]++;
                    return element;
                })
                .filter(Objects::nonNull)
                .map(element -> {
                    String equipmentUrl = Objects.requireNonNull(element)
                            .attributes()
                            .get("href");
                    String equipmentName = element.text();
                    return Pair.of(equipmentName, equipmentUrl);
                })
                .collect(Collectors.toList());
    }
}