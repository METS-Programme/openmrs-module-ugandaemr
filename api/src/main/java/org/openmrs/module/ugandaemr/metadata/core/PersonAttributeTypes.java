package org.openmrs.module.ugandaemr.metadata.core;

import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.module.metadatadeploy.descriptor.PersonAttributeTypeDescriptor;

/**
 * Constants for all defined person attribute types
 * Created by ssmusoke on 09/01/2016.
 */
public class PersonAttributeTypes {

    // ===== Existing (kept) =====

    public static PersonAttributeTypeDescriptor MARITAL_STATUS = new PersonAttributeTypeDescriptor() {
        @Override
        public double sortWeight() {
            return 0;
        }

        @Override
        public Class<?> format() {
            return Concept.class;
        }

        @Override
        public String name() {
            return "Marital Status";
        }

        @Override
        public String description() {
            return "Marital status of this person";
        }

        @Override
        public String uuid() {
            return "8d871f2a-c2cc-11de-8d13-0010c6dffd0f";
        }

        @Override
        public boolean searchable() {
            return true;
        }
    };

    public static PersonAttributeTypeDescriptor NATIONALITY = new PersonAttributeTypeDescriptor() {
        @Override
        public double sortWeight() {
            return 0;
        }

        @Override
        public Class<?> format() {
            return Concept.class;
        }

        @Override
        public String name() {
            return "Natioinality";
        } // kept as provided

        @Override
        public String description() {
            return "The Nationality of the patient";
        }

        @Override
        public String uuid() {
            return "dec484be-1c43-416a-9ad0-18bd9ef28929";
        }

        @Override
        public boolean searchable() {
            return true;
        }
    };

    public static PersonAttributeTypeDescriptor HEALTH_CENTER = new PersonAttributeTypeDescriptor() {
        @Override
        public double sortWeight() {
            return 3;
        }

        @Override
        public Class<?> format() {
            return Location.class;
        }

        @Override
        public String name() {
            return "Health Center";
        }

        @Override
        public String description() {
            return "Specific Location of this person's home health center";
        }

        @Override
        public String uuid() {
            return "8d87236c-c2cc-11de-8d13-0010c6dffd0f";
        }
    };

    public static PersonAttributeTypeDescriptor HEALTH_FACILITY_DISTRICT = new PersonAttributeTypeDescriptor() {
        @Override
        public double sortWeight() {
            return 6;
        }

        @Override
        public String name() {
            return "Health Facility District";
        }

        @Override
        public String description() {
            return "District/region in which this patient' home health center resides";
        }

        @Override
        public String uuid() {
            return "8d872150-c2cc-11de-8d13-0010c6dffd0f";
        }
    };

    public static PersonAttributeTypeDescriptor TELEPHONE_NUMBER_2 = new PersonAttributeTypeDescriptor() {
        @Override
        public double sortWeight() {
            return 8;
        }

        @Override
        public String name() {
            return "Alternate Telephone Number";
        }

        @Override
        public String description() {
            return "Alternate Telephone number";
        }

        @Override
        public String uuid() {
            return "8c44d411-285f-46c6-9f17-c2f919823b34";
        }

        @Override
        public boolean searchable() {
            return true;
        }
    };

    public static PersonAttributeTypeDescriptor TELEPHONE_NUMBER_3 = new PersonAttributeTypeDescriptor() {
        @Override
        public double sortWeight() {
            return 10;
        }

        @Override
        public String name() {
            return "Second Alternate Telephone Number";
        }

        @Override
        public String description() {
            return "Second Alternate Telephone number";
        }

        @Override
        public String uuid() {
            return "a00eda65-2f66-4fda-a683-c1787eb626a9";
        }

        @Override
        public boolean searchable() {
            return true;
        }
    };

    public static PersonAttributeTypeDescriptor OCCUPATION = new PersonAttributeTypeDescriptor() {
        @Override
        public double sortWeight() {
            return 12;
        }

        @Override
        public String name() {
            return "Occupation";
        }

        @Override
        public String description() {
            return "Occupation";
        }

        @Override
        public String uuid() {
            return "b0868a16-4f8e-43da-abfc-6338c9d8f56a";
        }
    };

    public static PersonAttributeTypeDescriptor EMAIL_ADDRESS = new PersonAttributeTypeDescriptor() {
        @Override
        public double sortWeight() {
            return 12;
        }

        @Override
        public String name() {
            return "Email Address";
        }

        @Override
        public String description() {
            return "Email Address";
        }

        @Override
        public String uuid() {
            return "5ecfd59d-5988-4aa3-84f8-8b43599fc67c";
        }

        @Override
        public boolean searchable() {
            return true;
        }
    };

    public static PersonAttributeTypeDescriptor EDUCATION_LEVEL = new PersonAttributeTypeDescriptor() {
        @Override
        public double sortWeight() {
            return 13;
        }

        @Override
        public Class<?> format() {
            return Concept.class;
        }

        @Override
        public String name() {
            return "Education Level";
        }

        @Override
        public String description() {
            return "Person Education Level";
        }

        @Override
        public String uuid() {
            return "ec6e8300-38e8-4c95-8e57-27c9cdfba104";
        }

        @Override
        public boolean searchable() {
            return true;
        }
    };

    // ===== New from provided list =====

    public static PersonAttributeTypeDescriptor RACE = new PersonAttributeTypeDescriptor() {
        @Override
        public double sortWeight() {
            return 1;
        }

        @Override
        public Class<?> format() {
            return Concept.class;
        }

        @Override
        public String name() {
            return "Race";
        }

        @Override
        public String description() {
            return "Group of persons related by common descent or heredity";
        }

        @Override
        public String uuid() {
            return "8d871386-c2cc-11de-8d13-0010c6dffd0f";
        }
    };

    public static PersonAttributeTypeDescriptor BIRTHPLACE = new PersonAttributeTypeDescriptor() {
        @Override
        public double sortWeight() {
            return 2;
        }

        @Override
        public Class<?> format() {
            return Location.class;
        } // set as Location based on description

        @Override
        public String name() {
            return "Birthplace";
        }

        @Override
        public String description() {
            return "Location of persons birth";
        }

        @Override
        public String uuid() {
            return "8d8718c2-c2cc-11de-8d13-0010c6dffd0f";
        }
    };

    public static PersonAttributeTypeDescriptor CITIZENSHIP = new PersonAttributeTypeDescriptor() {
        @Override
        public double sortWeight() {
            return 2;
        }

        @Override
        public Class<?> format() {
            return Concept.class;
        }

        @Override
        public String name() {
            return "Citizenship";
        }

        @Override
        public String description() {
            return "Country of which this person is a member";
        }

        @Override
        public String uuid() {
            return "8d871afc-c2cc-11de-8d13-0010c6dffd0f";
        }
    };

    public static PersonAttributeTypeDescriptor MOTHERS_NAME = new PersonAttributeTypeDescriptor() {
        @Override
        public double sortWeight() {
            return 4;
        }

        @Override
        public String name() {
            return "Mother's Name";
        }

        @Override
        public String description() {
            return "First or last name of this person's mother";
        }

        @Override
        public String uuid() {
            return "8d871d18-c2cc-11de-8d13-0010c6dffd0f";
        }
    };

    public static PersonAttributeTypeDescriptor TELEPHONE_NUMBER = new PersonAttributeTypeDescriptor() {
        @Override
        public double sortWeight() {
            return 7;
        }

        @Override
        public String name() {
            return "Telephone Number";
        }

        @Override
        public String description() {
            return "The telephone number for the person";
        }

        @Override
        public String uuid() {
            return "14d4f066-15f5-102d-96e4-000c29c2a5d7";
        }

        @Override
        public boolean searchable() {
            return true;
        }
    };

    public static PersonAttributeTypeDescriptor UNKNOWN_PATIENT = new PersonAttributeTypeDescriptor() {
        @Override
        public double sortWeight() {
            return 17;
        }

        @Override
        public String name() {
            return "Unknown patient";
        }

        @Override
        public String description() {
            return "Used to flag patients that cannot be identified during the check-in process";
        }

        @Override
        public String uuid() {
            return "8b56eac7-5c76-4b9c-8c6f-1deab8d3fc47";
        }
    };

    public static PersonAttributeTypeDescriptor TEST_PATIENT = new PersonAttributeTypeDescriptor() {
        @Override
        public double sortWeight() {
            return 18;
        }

        @Override
        public String name() {
            return "Test Patient";
        }

        @Override
        public String description() {
            return "Flag to describe if the patient was created to a test or not";
        }

        @Override
        public String uuid() {
            return "4f07985c-88a5-4abd-aa0c-f3ec8324d8e7";
        }
    };

    public static PersonAttributeTypeDescriptor FINGERPRINT = new PersonAttributeTypeDescriptor() {
        @Override
        public double sortWeight() {
            return 19;
        }

        @Override
        public String name() {
            return "FingerPrint";
        }

        @Override
        public String description() {
            return "This attribute is the type for person attributed which stores the ugandaemrfingerprint template";
        }

        @Override
        public String uuid() {
            return "a41339f9-5014-45f4-91d6-bab84c6c62f1";
        }
    };

    public static PersonAttributeTypeDescriptor FATHERS_NAME = new PersonAttributeTypeDescriptor() {
        @Override
        public double sortWeight() {
            return 5;
        }

        @Override
        public String name() {
            return "Father's Name";
        }

        @Override
        public String description() {
            return "Person's Father's name";
        }

        @Override
        public String uuid() {
            return "63535ca9-bb53-4a46-a14b-831788c25c10";
        }
    };

    public static PersonAttributeTypeDescriptor CARE_GIVER_NAME = new PersonAttributeTypeDescriptor() {
        @Override
        public double sortWeight() {
            return 9;
        }

        @Override
        public String name() {
            return "Care giver's Name";
        }

        @Override
        public String description() {
            return "Care givers name to the patient";
        }

        @Override
        public String uuid() {
            return "e493f4ec-51f1-4a77-8d3c-5791f8cb02cd";
        }
    };

    public static PersonAttributeTypeDescriptor CARE_GIVER_TELEPHONE_NUMBER = new PersonAttributeTypeDescriptor() {
        @Override
        public double sortWeight() {
            return 11;
        }

        @Override
        public String name() {
            return "Care giver's Telephone Number";
        }

        @Override
        public String description() {
            return "The telephone number of the person giving care";
        }

        @Override
        public String uuid() {
            return "553834ef-b3fe-4c79-826a-6d4b6978bcff";
        }

        @Override
        public boolean searchable() {
            return true;
        }
    };

    public static PersonAttributeTypeDescriptor COMMON_NAME = new PersonAttributeTypeDescriptor() {
        @Override
        public double sortWeight() {
            return 14;
        }

        @Override
        public String name() {
            return "Common Name";
        }

        @Override
        public String description() {
            return "This is the widely used name when referring to the patient";
        }

        @Override
        public String uuid() {
            return "3573b0c9-667a-48d6-862d-d5b8d84ac850";
        }
    };

    public static PersonAttributeTypeDescriptor LAND_MARK_FEATURE = new PersonAttributeTypeDescriptor() {
        @Override
        public double sortWeight() {
            return 15;
        }

        @Override
        public String name() {
            return "Land Mark Feature";
        }

        @Override
        public String description() {
            return "This is an easily visible feature when looking for the location of the patient";
        }

        @Override
        public String uuid() {
            return "56aef1a8-e4ef-476a-9998-9d2d67c3b01e";
        }
    };

    public static PersonAttributeTypeDescriptor DIRECTIONS_TO_PATIENT_HOME = new PersonAttributeTypeDescriptor() {
        @Override
        public double sortWeight() {
            return 16;
        }

        @Override
        public String name() {
            return "Directions to Patient's Home";
        }

        @Override
        public String description() {
            return "Detailed description of how one can get to the location of the patient";
        }

        @Override
        public String uuid() {
            return "01c0111d-3bbc-4de6-8c5a-1cc91e86cc38";
        }
    };

    public static PersonAttributeTypeDescriptor EMAIL = new PersonAttributeTypeDescriptor() {
        @Override
        public double sortWeight() {
            return 12;
        }

        @Override
        public String name() {
            return "email";
        }

        @Override
        public String description() {
            return "Email Address";
        }

        @Override
        public String uuid() {
            return "bd93839a-4983-11ef-afae-3cebe5bbf8ab";
        }

        @Override
        public boolean searchable() {
            return true;
        }
    };
}
