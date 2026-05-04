package org.openmrs.module.ugandaemr.api.dto;

import org.openmrs.Obs;

import java.util.Date;
import java.util.List;

public class StabilityCriteria {
    private String uuid;
    private Obs vlObs;
    private Obs vlDateObs;
    private Date artStartDate;
    private Obs regimenObs;
    private Integer regimenObsConceptId;
    private Obs currentRegimenObs;
    private Integer currentRegimenObsConceptId;
    private Obs regimenBeforeDTGObs;
    private Integer regimenBeforeDTGObsValueConceptId;
    private Boolean onThirdRegimen;
    private List<Obs> adherenceObs;
    private Integer conceptForClinicStage;
    private Obs sputumResultDateObs;
    private Obs sputumResultObs;
    private Integer sputumResultObsValueConceptId;
    private Integer baselineRegimenConceptId;
    private String enableCliniciansMakeStabilityDecisions;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Obs getVlObs() {
        return vlObs;
    }

    public void setVlObs(Obs vlObs) {
        this.vlObs = vlObs;
    }

    public Obs getVlDateObs() {
        return vlDateObs;
    }

    public void setVlDateObs(Obs vlDateObs) {
        this.vlDateObs = vlDateObs;
    }

    public Date getArtStartDate() {
        return artStartDate;
    }

    public void setArtStartDate(Date artStartDate) {
        this.artStartDate = artStartDate;
    }

    public Obs getRegimenObs() {
        return regimenObs;
    }

    public void setRegimenObs(Obs regimenObs) {
        this.regimenObs = regimenObs;
    }

    public Obs getCurrentRegimenObs() {
        return currentRegimenObs;
    }

    public void setCurrentRegimenObs(Obs currentRegimenObs) {
        this.currentRegimenObs = currentRegimenObs;
    }

    public Obs getRegimenBeforeDTGObs() {
        return regimenBeforeDTGObs;
    }

    public void setRegimenBeforeDTGObs(Obs regimenBeforeDTGObs) {
        this.regimenBeforeDTGObs = regimenBeforeDTGObs;
    }


    public Boolean getOnThirdRegimen() {
        return onThirdRegimen;
    }

    public void setOnThirdRegimen(Boolean onThirdRegimen) {
        this.onThirdRegimen = onThirdRegimen;
    }

    public List<Obs> getAdherenceObs() {
        return adherenceObs;
    }

    public void setAdherenceObs(List<Obs> adherenceObs) {
        this.adherenceObs = adherenceObs;
    }


    public Integer getConceptForClinicStage() {
        return conceptForClinicStage;
    }

    public void setConceptForClinicStage(Integer conceptForClinicStage) {
        this.conceptForClinicStage = conceptForClinicStage;
    }

    public Obs getSputumResultDateObs() {
        return sputumResultDateObs;
    }

    public void setSputumResultDateObs(Obs sputumResultDateObs) {
        this.sputumResultDateObs = sputumResultDateObs;
    }

    public Obs getSputumResultObs() {
        return sputumResultObs;
    }

    public void setSputumResultObs(Obs sputumResultObs) {
        this.sputumResultObs = sputumResultObs;
    }

    public Integer getBaselineRegimenConceptId() {
        return baselineRegimenConceptId;
    }

    public void setBaselineRegimenConceptId(Integer baselineRegimenConceptId) {
        this.baselineRegimenConceptId = baselineRegimenConceptId;
    }

    public String getEnableCliniciansMakeStabilityDecisions() {
        return enableCliniciansMakeStabilityDecisions;
    }

    public void setEnableCliniciansMakeStabilityDecisions(String enableCliniciansMakeStabilityDecisions) {
        this.enableCliniciansMakeStabilityDecisions = enableCliniciansMakeStabilityDecisions;
    }

    public Integer getRegimenObsConceptId() {
        return regimenObsConceptId;
    }

    public void setRegimenObsConceptId(Integer regimenObsConceptId) {
        this.regimenObsConceptId = regimenObsConceptId;
    }

    public Integer getCurrentRegimenObsConceptId() {
        return currentRegimenObsConceptId;
    }

    public void setCurrentRegimenObsConceptId(Integer currentRegimenObsConceptId) {
        this.currentRegimenObsConceptId = currentRegimenObsConceptId;
    }

    public Integer getRegimenBeforeDTGObsValueConceptId() {
        return regimenBeforeDTGObsValueConceptId;
    }

    public void setRegimenBeforeDTGObsValueConceptId(Integer regimenBeforeDTGObsValueConceptId) {
        this.regimenBeforeDTGObsValueConceptId = regimenBeforeDTGObsValueConceptId;
    }

    public Integer getSputumResultObsValueConceptId() {
        return sputumResultObsValueConceptId;
    }

    public void setSputumResultObsValueConceptId(Integer sputumResultObsValueConceptId) {
        this.sputumResultObsValueConceptId = sputumResultObsValueConceptId;
    }
}