package org.openmrs.module.ugandaemr.activator;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.FormResource;
import org.openmrs.api.DatatypeService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.FormService;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.ClobDatatypeStorage;
import org.openmrs.ui.framework.resource.ResourceFactory;
import org.openmrs.ui.framework.resource.ResourceProvider;
import org.openmrs.util.OpenmrsUtil;


import java.io.File;
import java.nio.charset.StandardCharsets;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public class JsonFormsInitializer implements Initializer {

    protected static final Log log = LogFactory.getLog(HtmlFormsInitializer.class);

    protected static final String formsPath = "jsonforms/";


    public static final String AMPATH_FORMS_UUID = "794c4598-ab82-47ca-8d18-483a8abe6f4f";
    protected String providerName;
    private String formFilePath;

    public JsonFormsInitializer(String newProviderName, String newFormFilePath) {
        this.providerName = newProviderName;
        this.formFilePath = newFormFilePath;
    }


    @Override
    public void started() {
        log.info("Setting HFE forms for " + getProviderName());

        final ResourceFactory resourceFactory = ResourceFactory.getInstance();
        final ResourceProvider resourceProvider = resourceFactory.getResourceProviders().get(getProviderName());

        // Scanning the forms resources folder
        String fileFormPathLocal=formsPath;

        // Scanning the forms resources folder
        List<String> formPaths = new ArrayList<String>();

        File formsDir;

        if (!formFilePath.equals("")) {
            fileFormPathLocal = this.formFilePath;
            formsDir = new File(fileFormPathLocal);
        } else {
            formsDir = resourceProvider.getResource(fileFormPathLocal);
        }

        if (formsDir == null || !formsDir.isDirectory()) {
            log.error("No HTML forms could be retrieved from the provided folder: " + getProviderName() + ":" + fileFormPathLocal);
            return;
        }

        for (File file : formsDir.listFiles())
            formPaths.add(fileFormPathLocal + file.getName());

        for (File file : formsDir.listFiles()) {
            try {
                load(file);
            } catch (Exception e) {
                log.error("error loading form" + file.getName(), e);
            }
        }

    }


    @Override
    public void stopped() {

    }

    protected void load(File file) throws Exception {


        DatatypeService datatypeService = Context.getDatatypeService();
        FormService formService = Context.getFormService();
        EncounterService encounterService = Context.getEncounterService();

        String jsonString = FileUtils.readFileToString(file, StandardCharsets.UTF_8.toString());
        Map<String, Object> jsonFile = new ObjectMapper().readValue(jsonString, Map.class);

        String formName = (String) jsonFile.get("name");
        if (StringUtils.isBlank(formName)) {
            throw new Exception("Form Name is required");
        }

        String formDescription = (String) jsonFile.get("description");
        boolean formPublished = (Boolean) jsonFile.get("published");
        boolean formRetired = (Boolean) jsonFile.get("retired");

        String formProcessor = (String) jsonFile.get("processor");
        boolean isEncounterForm = formProcessor == null || StringUtils.isBlank(formProcessor)
                || formProcessor.equalsIgnoreCase("EncounterFormProcessor");

        EncounterType encounterType = null;
        String formEncounterType = (String) jsonFile.get("encounterType");
        if (formEncounterType != null) {
            encounterType = encounterService.getEncounterTypeByUuid(formEncounterType);
            if (encounterType == null) {
                throw new Exception("Form Encounter type " + formEncounterType + " could not be found. Please ensure that "
                        + "this encountertype is either loaded by Iniz or loaded in the system before Iniz runs.");
            }
        }

        if (isEncounterForm && encounterType == null) {
            throw new Exception("No encounter was found for this form. You should have an \"encounter\" entry whose value "
                    + "is the id of the encounter type to use for this form, e.g., \"encounter\": \"Emergency\".");
        }

        String formVersion = (String) jsonFile.get("version");
        if (formVersion == null) {
            throw new Exception("Form Version is required");
        }

        String uuid = (String) jsonFile.get("uuid");
        // Process Form
        // ISSUE-150 If form with uuid present then update it
        Form form = formService.getFormByUuid(uuid);
        if (form != null) {
            boolean needToSaveForm = false;
            FormResource formResource = formService.getFormResource(form, "JSON schema");

            // Name
            if (!OpenmrsUtil.nullSafeEquals(form.getName(), formName)) {
                form.setName(formName);
                needToSaveForm = true;
            }

            // Description
            if (!OpenmrsUtil.nullSafeEquals(form.getDescription(), formDescription)) {
                form.setDescription(formDescription);
                needToSaveForm = true;
            }
            // Version
            if (!OpenmrsUtil.nullSafeEquals(form.getVersion(), formVersion)) {
                form.setVersion(formVersion);
                needToSaveForm = true;
            }
            // Add in schema
            // Published
            if (!OpenmrsUtil.nullSafeEquals(form.getPublished(), formPublished)) {
                form.setPublished(formPublished);
                needToSaveForm = true;
            }
            // Add to schema
            // Retired
            if (!OpenmrsUtil.nullSafeEquals(form.getRetired(), formRetired)) {
                form.setRetired(formRetired);
                if (formRetired && StringUtils.isBlank(form.getRetireReason())) {
                    form.setRetireReason("Retired by Initializer");
                }
                needToSaveForm = true;
            }
            // Add encounter to schema
            if (encounterType != null && !OpenmrsUtil.nullSafeEquals(form.getEncounterType(), encounterType)) {
                form.setEncounterType(encounterType);
                needToSaveForm = true;
            }

            if (formResource != null) {
                ClobDatatypeStorage clobData = datatypeService.getClobDatatypeStorageByUuid(formResource.getValueReference());

                if (clobData != null) {
                    clobData.setValue(jsonString);
                } else {
                    clobData = new ClobDatatypeStorage();
                    clobData.setValue(jsonString);
                    clobData.setUuid(UUID.randomUUID().toString());
                    formResource.setValueReferenceInternal(clobData.getUuid());
                    formService.saveFormResource(formResource);
                }

                datatypeService.saveClobDatatypeStorage(clobData);

            } else {
                createFormResource(form, UUID.randomUUID().toString(), jsonString);
            }

            if (needToSaveForm) {
                formService.saveForm(form);
            }
        } else {
            createNewForm(uuid, formName, formDescription, formPublished, formRetired, encounterType, formVersion,
                    jsonString);
        }
    }

    private void createNewForm(String uuid, String formName, String formDescription, Boolean formPublished,
                               Boolean formRetired, EncounterType encounterType, String formVersion, String jsonString) {
        FormService formService = Context.getFormService();
        String clobUuid = UUID.randomUUID().toString();
        Form newForm = new Form();
        newForm.setName(formName);
        newForm.setVersion(formVersion);
        newForm.setUuid(uuid);
        newForm.setDescription(formDescription);
        newForm.setRetired(formRetired);
        newForm.setPublished(formPublished);
        newForm.setEncounterType(encounterType);
        newForm = formService.saveForm(newForm);
        createFormResource(newForm, clobUuid, jsonString);
    }


    private void createFormResource(Form form, String clobUuid, String jsonString) {
        FormService formService = Context.getFormService();
        DatatypeService datatypeService = Context.getDatatypeService();
        FormResource formResource;
        formResource = new FormResource();
        formResource.setName("JSON schema");
        formResource.setForm(form);
        formResource.setValueReferenceInternal(clobUuid);
        formResource.setDatatypeClassname("AmpathJsonSchema");
        formService.saveFormResource(formResource);
        ClobDatatypeStorage clobData = new ClobDatatypeStorage();
        clobData.setUuid(clobUuid);
        clobData.setValue(jsonString);
        datatypeService.saveClobDatatypeStorage(clobData);
    }

    public String getProviderName() {
        return this.providerName;
    }

}
