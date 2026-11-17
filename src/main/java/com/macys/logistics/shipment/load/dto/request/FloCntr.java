package com.macys.logistics.shipment.load.dto.request;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FloCntr  {

	private Long orgnLocNbr;

    private Long apptNbr;
    
    private List<ShipmentContainerInfo> shipmentContainerInfo;

    private String apptSchdArvlTs;

    private String userId;

}
