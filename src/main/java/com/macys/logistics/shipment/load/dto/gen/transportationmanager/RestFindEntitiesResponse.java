package com.macys.logistics.shipment.load.dto.gen.transportationmanager;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestFindEntitiesResponse {
	protected ApiHeaderType apiHeader;
	protected ResponseHeaderType responseHeader;
	protected PageClauseType page;
	protected List<StopType> data;


}
