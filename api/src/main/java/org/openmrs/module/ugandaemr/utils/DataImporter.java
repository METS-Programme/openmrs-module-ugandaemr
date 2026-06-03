/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.ugandaemr.utils;

/**
 * Interface for DataImporter to support Spring proxying
 * This interface is needed because the implementation uses @Transactional
 * which creates a JDK dynamic proxy that requires an interface
 */
public interface DataImporter {

    /**
     * Import data from an XML file path
     * The path can be relative to the classpath or an absolute file system path
     *
     * @param filePath the path to the XML file containing the data to import
     */
    void importData(String filePath);
}
