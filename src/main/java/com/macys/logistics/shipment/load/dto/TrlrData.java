package com.macys.logistics.shipment.load.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TrlrData {
    @JsonProperty("TRLR_NBR")
    private Long trlrNbr;
    @JsonProperty("CARR_TRLR_NBR")
    private String carrTrlrNbr;
    @JsonProperty("TRLR_CLASS_NBR")
    private Long trlrClassNbr;
    @JsonProperty("TRLR_TYP_NBR")
    private Integer trlrTypNbr;
    @JsonProperty("TRLR_STAT_NBR")
    private Integer trlrStatNbr;
    @JsonProperty("VND_NBR")
    private Long vndNbr;
    @JsonProperty("LOC_NBR")
    private Integer locNbr;
    @JsonProperty("CREATE_TS")
    private String createTs;
    @JsonProperty("CREATE_USERID_V")
    private String createUseridV;
    @JsonProperty("LAST_UPD_TS")
    private String lastUpdTs;
    @JsonProperty("LAST_UPD_USERID_V")
    private String lastUpdUseridV;
    @JsonProperty("VERSION_NBR")
    private Integer versionNbr;
    @JsonProperty("CURR_LOC_NBR")
    private Integer currLocNbr;
    @JsonProperty("CURR_YARD_NBR")
    private Long currYardNbr;
    @JsonProperty("CURR_YARD_AREA_NBR")
    private Long currYardAreaNbr;
}

