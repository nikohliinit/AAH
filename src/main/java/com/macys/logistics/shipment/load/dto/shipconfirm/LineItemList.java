package com.macys.logistics.shipment.load.dto.shipconfirm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LineItemList {

    private List<LineItem> lineItem;
}
