package org.openmrs.module.ugandaemr;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.module.ugandaemr.utils.DataImporter;
import org.openmrs.test.jupiter.BaseModuleContextSensitiveTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RolePrivilegeImportTest extends BaseModuleContextSensitiveTest {


    protected static final String ROLE_PRIVILLEGE_DATASET_XML = "metadata/Role_Privilege.xml";

    UserService userService;


    @BeforeEach
    public void runBeforeAllTests() {
        userService = Context.getUserService();
    }

    @Test
    public void shouldImportWithOutAnyError() {
        DataImporter dataImporter = Context.getRegisteredComponent("ugandaemrDataImporter", DataImporter.class);
        dataImporter.importData(ROLE_PRIVILLEGE_DATASET_XML);
        assertNotNull(userService.getPrivilege("App: ugandaemrpoc.findPatient"));
        assertNotNull(userService.getRole("Data Clerk"));
        assertTrue(userService.getRole("Data Clerk").getPrivileges().contains(userService.getPrivilege("App: ugandaemrpoc.findPatient")));
    }
}
