package org.openmrs.module.ugandaemr.metadata.core.location;

import java.util.Arrays;
import java.util.List;

import org.openmrs.api.context.Context;
import org.openmrs.module.metadatadeploy.descriptor.LocationDescriptor;
import org.openmrs.module.metadatadeploy.descriptor.LocationTagDescriptor;

public class LocationOrganization {

	public static LocationDescriptor PARENT = new LocationDescriptor(){

		@Override
		public String uuid() {
			return "629d78e9-93e5-43b0-ad8a-48313fd99117";
		}

		@Override
		public String description() {
			return "Health Center Location";
		}

		@Override
		public String name() {
			String DEFAULT_HEALTH_CENTER_NAME = "Health Center Name";

			org.openmrs.Location location = Context.getLocationService().getLocationByUuid("629d78e9-93e5-43b0-ad8a-48313fd99117");
			String name = (location != null) ? location.getName() : DEFAULT_HEALTH_CENTER_NAME;

			return name;
		}

		@Override
		public List<LocationTagDescriptor> tags() {

			return Arrays.asList(
					LocationTags.ORGANIZATION
			);

		}

	};

}
