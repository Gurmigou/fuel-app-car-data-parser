package com.fueladvisor.fuelappcarparserservice.repository.externalData;

import com.fueladvisor.fuelappcarparserservice.model.carBrandInfo.CarBrand;
import com.fueladvisor.fuelappcarparserservice.model.carBrandInfo.CarModel;
import lombok.Getter;

@Getter
public class ParsedCarDataWrapper {
    private CarBrand carBrand;
    private String brandUrl;

    private CarModel carModel;
    private String modelUrl;

    private String specName;
    private String specUrl;

    private String equipmentName;
    private String equipmentUrl;

    public ParsedCarDataWrapper setCarBrand(CarBrand carBrand) {
        this.carBrand = carBrand;
        return this;
    }

    public ParsedCarDataWrapper setBrandUrl(String brandUrl) {
        this.brandUrl = brandUrl;
        return this;
    }

    public ParsedCarDataWrapper setCarModel(CarModel carModel) {
        this.carModel = carModel;
        return this;
    }

    public ParsedCarDataWrapper setModelUrl(String modelUrl) {
        this.modelUrl = modelUrl;
        return this;
    }

    public ParsedCarDataWrapper setSpecName(String specName) {
        this.specName = specName;
        return this;
    }

    public ParsedCarDataWrapper setSpecUrl(String specUrl) {
        this.specUrl = specUrl;
        return this;
    }

    public ParsedCarDataWrapper setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
        return this;
    }

    public ParsedCarDataWrapper setEquipmentUrl(String equipmentUrl) {
        this.equipmentUrl = equipmentUrl;
        return this;
    }

    public ParsedCarDataWrapper mergeWrappers(ParsedCarDataWrapper another) {
        this.carBrand = another.carBrand;
        this.brandUrl = another.brandUrl;
        this.carModel = another.carModel;
        this.modelUrl = another.modelUrl;
        this.specName = another.specName;
        this.specUrl = another.specUrl;
        this.equipmentName = another.equipmentName;
        this.equipmentUrl = another.equipmentUrl;
        return this;
    }
}