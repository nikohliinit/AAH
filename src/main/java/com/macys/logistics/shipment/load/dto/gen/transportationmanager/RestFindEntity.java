package com.macys.logistics.shipment.load.dto.gen.transportationmanager;

import lombok.Data;

import java.util.List;

@Data
public class RestFindEntity {

    protected Page page;
    protected List<RestFilterType> filter;
    protected String entityType;
    protected RestSelectClauseType select;
}
