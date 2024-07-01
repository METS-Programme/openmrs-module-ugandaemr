<style type="text/css">
img {
    width: 100px;
    height: auto;
}
</style>
<script>
    function queryRestData(url, method, data) {
        var responseData = null;
        jq.ajax({
            type: method,
            url: '/' + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/" + url,
            dataType: "json",
            contentType: "application/json",
            accept: "application/json",
            async: false,
            data: data,
            success: function (response) {
                responseData = response;
            },
            error: function (response) {
                responseData = response
            }
        });
        return responseData;
    }

    if (jQuery) {
        jq(document).ready(function () {
            let searchParams = new URLSearchParams(window.location.search)
            var patientId= searchParams.get('patientId')
            var visitId= searchParams.get('visitId')
            var encounterId= ""

            if(searchParams.get('encounterId')!==null && searchParams.get('encounterId')!=="null"){
                encounterId="&encounter=" + searchParams.get('encounterId');
            }
            var stability_url = "stabilitycriteria?patient=" + patientId + "&visit=" + visitId + encounterId
            var global_property_url = "systemsetting?q=ugandaemr.dsdm.allowClinicalOverrideDSDMPatientStability&v=custom:(property,uuid,value)"

            var enableCliniciansMakeStabilityDecisions = queryRestData(global_property_url).results[0].value;
            if (enableCliniciansMakeStabilityDecisions !== true && enableCliniciansMakeStabilityDecisions!=="true") {
                var stability = queryRestData(stability_url)

                if (stability != null && stability.hasOwnProperty("results")) {
                    var stabilityResults = stability.results[0];

                    jq("#vl").html(stabilityResults.vlObs)
                    jq("#vlDate").html(stabilityResults.vlDateObs)
                    jq("#art_start_date").html(stabilityResults.artStartDate)
                    jq("#regimen").html(stabilityResults.regimenObsConceptId)
                    jq("#regimen_started_date").html(stabilityResults.regimenObs.encounter.encounterDatetime)

                    if (stabilityResults.currentRegimenObsConceptId === stabilityResults.baselineRegimenConceptId) {
                        jq("#current_regimen_start_date").html(stabilityResults.regimenObs.encounter.encounterDatetime)
                    } else {
                        jq("#current_regimen_start_date").html("")
                    }

                    if (stabilityResults.regimenBeforeDTGObs != null) {
                        jq("#regimen_before_dtg").html(stabilityResults.regimenBeforeDTGObsValueConceptId)
                        jq("#regimen_started_date_before_dtg").html(regimenBeforeDTGObs.encounter.encounterDatetime)
                    }

                    jq("#on_third_line").html(stabilityResults.onThirdRegimen)

                    stabilityResults.adherenceObs.forEach((it, index) => {
                        jq("#adherence").append(it.valueCoded.name)
                    });

                    jq("#clinic_staging").html(stabilityResults.conceptForClinicStage)
                    jq("#sputum_date").html(stabilityResults.sputumResultDateObs.valueDate)
                    jq("#teatment_start_date").html(stabilityResults.sputumResultDateObs.encounter.encounterDatetime)
                    jq("#sputum_value").html(stabilityResults.sputumResultObsValueConceptId)
                }
            } else {
                jq("#stability_criteria").remove();
            }
        })
    }
</script>
<div id="stability_criteria">
    <div class="stability">
        <div id="vl"></div>

        <div id="vlDate"></div>

        <div id="art_start_date"></div>

        <div id="regimen"></div>

        <div id="regimen_started_date"></div>

        <div id="current_regimen_start_date"></div>

        <div id="regimen_before_dtg"></div>

        <div id="regimen_started_date_before_dtg"></div>

        <div id="on_third_line"></div>

        <div id="adherence"></div>

        <div id="clinic_staging"></div>

        <div id="sputum_date"></div>

        <div id="teatment_start_date"></div>

        <div id="sputum_value"></div>
    </div>
    <obs class="horizontal" conceptId="5090" labelText="Height" showUnits="uicommons.units.centimeters"
         unitsCssClass="append-to-value"/>
</div>