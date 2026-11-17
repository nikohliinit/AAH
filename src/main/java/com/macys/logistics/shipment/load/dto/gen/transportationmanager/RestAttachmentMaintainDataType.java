
package com.macys.logistics.shipment.load.dto.gen.transportationmanager;

import java.util.List;
import javax.xml.datatype.XMLGregorianCalendar;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class RestAttachmentMaintainDataType {

	protected String systemAttachmentID;
	protected String attachmentActionEnumVal;
	protected String attachmentEntityKey;
	protected String attachmentEntityEnumVal;
	protected String fileData;
    protected String fileType;
    protected String fileName;
    protected String name;
	protected String attachmentCategoryCode;
	protected String description;


}
