package org.openmrs.module.ugandaemr.web.resource;

import org.openmrs.Location;
import org.openmrs.Order;
import org.openmrs.api.OrderService;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientqueueing.model.PatientQueue;
import org.openmrs.module.ugandaemr.api.UgandaEMRService;
import org.openmrs.module.ugandaemr.web.customdto.ApproveOrder;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.validation.ValidateUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.openmrs.module.ugandaemr.UgandaEMRConstants.QUEUE_STATUS_SENT_TO_LAB;

@Resource(name = RestConstants.VERSION_1 + "/approveorder", supportedClass = ApproveOrder.class, supportedOpenmrsVersions = {"1.9.* - 9.*"})
public class ApproveOrderResource extends DelegatingCrudResource<ApproveOrder> {

    @Override
    public ApproveOrder newDelegate() {
        throw new ResourceDoesNotSupportOperationException("Operation not supported");
    }

    @Override
    public ApproveOrder save(ApproveOrder TestResult) {
        throw new ResourceDoesNotSupportOperationException("Operation not supported");
    }

    @Override
    public Object create(SimpleObject propertiesToCreate, RequestContext context) throws ResponseException {
        OrderService orderService = Context.getOrderService();
        List<Order> orders = new ArrayList<>();
        Location location = null;
        Location queueRoom = null;

        String[] orderIds = propertiesToCreate.get("orders").toString().split(",");
        String locationUuid = propertiesToCreate.get("location");
        String queueRoomUuid = propertiesToCreate.get("queueRoom");

        if (locationUuid != null) {
            location = Context.getLocationService().getLocationByUuid(locationUuid);
        }

        if (queueRoomUuid != null) {
            queueRoom = Context.getLocationService().getLocationByUuid(queueRoomUuid);
        }


        for (String uuid : orderIds) {
            Order test = orderService.getOrderByUuid(uuid);
            orders.add(orderService.updateOrderFulfillerStatus(test, Order.FulfillerStatus.COMPLETED, test.getFulfillerComment()));
        }

        ApproveOrder delegate = new ApproveOrder();

        delegate.setOrders(orders);
        if (orders.size() > 0) {
            delegate.setUuid(orders.get(0).getEncounter().getUuid());
            if (location != null || queueRoom != null) {
                PatientQueue patientQueue = Context.getService(UgandaEMRService.class).sendPatientBackToClinician(orders.get(0).getEncounter(), orders.get(0).getEncounter().getLocation(), location, QUEUE_STATUS_SENT_TO_LAB);
                if (patientQueue != null) {
                    delegate.setPatientQueue(patientQueue);
                }
            }
        }


        ValidateUtil.validate(delegate);
        SimpleObject ret = (SimpleObject) ConversionUtil.convertToRepresentation(delegate, context.getRepresentation());
        // add the 'type' discriminator if we support subclasses
        if (hasTypesDefined()) {
            ret.add(RestConstants.PROPERTY_FOR_TYPE, getTypeName(delegate));
        }

        return ret;
    }

    @Override
    public Object update(String uuid, SimpleObject propertiesToUpdate, RequestContext context) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException("Operation not supported");
    }

    @Override
    public ApproveOrder getByUniqueId(String uniqueId) {
        throw new ResourceDoesNotSupportOperationException("Operation not supported");
    }

    @Override
    public NeedsPaging<ApproveOrder> doGetAll(RequestContext context) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException("Operation not supported");
    }

    @Override
    public List<Representation> getAvailableRepresentations() {
        return Arrays.asList(Representation.DEFAULT, Representation.FULL);
    }

    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
        if (rep instanceof DefaultRepresentation) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("orders");
            description.addProperty("patientQueue");
            description.addSelfLink();
            return description;
        } else if (rep instanceof FullRepresentation) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("orders", Representation.REF);
            description.addProperty("patientQueue", Representation.REF);
            description.addSelfLink();
            description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
            return description;
        } else if (rep instanceof RefRepresentation) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("orders", Representation.REF);
            description.addProperty("patientQueue", Representation.REF);
            description.addSelfLink();
            return description;
        }
        return null;
    }

    @Override
    protected void delete(ApproveOrder TestResult, String s, RequestContext requestContext) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException("Operation not supported");
    }

    @Override
    public void purge(ApproveOrder TestResult, RequestContext requestContext) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException("Operation not supported");
    }

    @Override
    public DelegatingResourceDescription getCreatableProperties() throws ResourceDoesNotSupportOperationException {
        DelegatingResourceDescription description = new DelegatingResourceDescription();
        description.addProperty("orders");
        return description;
    }

    @Override
    protected PageableResult doSearch(RequestContext context) {
        throw new ResourceDoesNotSupportOperationException("Operation not supported");
    }
}
