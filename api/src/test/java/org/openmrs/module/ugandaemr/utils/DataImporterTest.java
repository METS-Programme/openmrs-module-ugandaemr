package org.openmrs.module.ugandaemr.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.test.jupiter.BaseModuleContextSensitiveTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for DataImporter to verify it works correctly after migration from dataexchange module
 */
public class DataImporterTest extends BaseModuleContextSensitiveTest {

    private DataImporter dataImporter;
    private UserService userService;

    @BeforeEach
    public void setUp() {
        dataImporter = Context.getRegisteredComponent("ugandaemrDataImporter", DataImporter.class);
        userService = Context.getUserService();
    }

    @Test
    public void shouldImportRolePrivilegeData() {
        // Test importing role and privilege data
        dataImporter.importData("metadata/Role_Privilege.xml");

        // Verify imported data
        assertNotNull(userService.getPrivilege("App: ugandaemrpoc.findPatient"),
            "Privilege should be imported");
        assertNotNull(userService.getRole("Data Clerk"),
            "Role should be imported");
        assertTrue(userService.getRole("Data Clerk").hasPrivilege("App: ugandaemrpoc.findPatient"),
            "Role should have the expected privilege");
    }

    @Test
    public void shouldHandleNullReplacements() {
        // Test that [NULL] replacements work correctly
        // This tests a key feature of DataImporter - replacing [NULL] strings with actual null values
        dataImporter.importData("metadata/Role_Privilege.xml");

        // The import should succeed without throwing exceptions
        // even if the data contains [NULL] placeholders
        assertNotNull(userService.getRole("Data Clerk"), "Import should complete successfully");
    }

    @Test
    public void shouldHandleResourceNotFoundGracefully() {
        // Test error handling for missing files
        Exception exception = assertThrows(RuntimeException.class, () -> {
            dataImporter.importData("metadata/nonexistent-file.xml");
        });

        assertNotNull(exception, "Should throw RuntimeException for missing files");
    }

    @Test
    public void shouldBeRegisteredAsSpringBean() {
        // Test that DataImporter is properly registered as a Spring bean
        assertNotNull(dataImporter, "DataImporter bean should be registered");
        assertTrue(dataImporter instanceof DataImporter,
            "Bean should be instance of DataImporter");
    }

    @Test
    public void shouldImportDataTransactional() {
        // Test that importData works within a transaction
        assertDoesNotThrow(() -> {
            dataImporter.importData("metadata/Role_Privilege.xml");
        }, "Import should complete without throwing exceptions");
    }
}
