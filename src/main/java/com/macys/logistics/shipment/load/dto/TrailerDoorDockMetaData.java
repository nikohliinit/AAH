package com.macys.logistics.shipment.load.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TrailerDoorDockMetaData  {

    @JsonProperty("TableID")
    private Integer tableId;

    @JsonProperty("TableName")
    private String tableName;

    @JsonProperty("TxnID")
    private String txnId;

    @JsonProperty("EntityName")
    private String entityName;

    @JsonProperty("OperationName")
    private String operationName;   // INSERT / UPDATE / DELETE

    @JsonProperty("FileName")
    private String fileName;

    @JsonProperty("FileOffset")
    private Long fileOffset;

    @JsonProperty("TimeStamp")
    private String timeStamp;

    @JsonProperty("Oracle ROWID")
    private String oracleRowId;

    @JsonProperty("CSN")
    private String csn;

    @JsonProperty("RecordStatus")
    private String recordStatus;
}