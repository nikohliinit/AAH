package com.macys.logistics.shipment.load.dto.gen.transportationmanager;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestTrailerActivityType {
    private String systemDockCommitmentID;
    private String systemStopID;
    private String systemLoadID;
    private Integer stopSequenceNumber;
    private String trailerActivityTypeEnumVal;
    private String reasonCode;
    private String activityDateTime;
    private Boolean reverseFlag;
    private Boolean updateFlag;
}
