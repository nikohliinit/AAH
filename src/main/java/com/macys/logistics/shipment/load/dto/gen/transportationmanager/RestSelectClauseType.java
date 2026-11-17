package com.macys.logistics.shipment.load.dto.gen.transportationmanager;

import lombok.Data;

import java.util.List;

@Data
public class RestSelectClauseType {

    protected List<String> name;
    protected List<RestRetrieveChildCollectionType> collection;

}
