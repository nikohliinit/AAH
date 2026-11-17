package com.macys.logistics.shipment.load.dto.gen.transportationmanager;

import com.macys.logistics.shipment.load.client.HasResponseHeader;
import lombok.Data;

import java.util.List;

@Data
public class ShipmentResponse  implements HasResponseHeader {

        private ResponseHeaderType responseHeader;
        private PageInfo page;
        private List<ShipmentData> data;

}
