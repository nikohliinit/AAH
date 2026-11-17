
package com.macys.logistics.shipment.load.dto.gen.transportationmanager;

import java.util.List;
import com.macys.logistics.shipment.load.client.HasResponseHeader;
import lombok.Data;

@Data
public class RestProcessMaintainEntityAttachmentResponse  implements HasResponseHeader{

	
	protected ApiHeaderType apiHeader;
	
	protected ResponseHeaderType responseHeader;
	
	protected List<String> systemAttachmentID;
	
	protected List<RestAttachmentMaintainDataType> attachmentMaintainData;

}
