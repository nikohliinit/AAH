package com.macys.logistics.shipment.load.service;

import com.macys.logistics.shipment.load.dto.inbound.orders.OrderShipmentMessage;
import com.macys.logistics.shipment.load.exception.TmsLoadProcessException;

/**
 * Service interface for order processing and TMS integration.
 * Handles Create, Update, and Delete operations for shipment orders.
 */
public interface OrderService {

    /**
     * Create a new order in TMS system.
     * Corresponds to TIBCO transCode="A" (Add).
     *
     * @param orderMessage The order shipment message containing order details
     * @throws TmsLoadProcessException if order creation fails
     */
    void createOrder(OrderShipmentMessage orderMessage) throws TmsLoadProcessException;

    /**
     * Update an existing order in TMS system.
     * Corresponds to TIBCO transCode="M" (Modify).
     *
     * @param orderMessage The order shipment message containing updated order details
     * @throws TmsLoadProcessException if order update fails
     */
    void updateOrder(OrderShipmentMessage orderMessage) throws TmsLoadProcessException;

    /**
     * Delete an existing order from TMS system.
     * Corresponds to TIBCO transCode="D" (Delete).
     *
     * @param orderMessage The order shipment message containing order identifier
     * @throws TmsLoadProcessException if order deletion fails
     */
    void deleteOrder(OrderShipmentMessage orderMessage) throws TmsLoadProcessException;
}
