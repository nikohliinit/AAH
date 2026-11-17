package com.macys.logistics.shipment.load.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestLoadCreateDataType {

    protected String systemLoadID;
    protected String loadDescription;
    protected String systemPlanID;
    protected String carrierCode;
    protected String serviceCode;
    protected String equipmentTypeCode;
    protected String tractorEquipmentTypeCode;
    protected String domicileCode;
    protected String domicileTrailerCode;
    protected String loadScheduledDateTime;
    protected Boolean loadCompositionApprovedFlag;
    protected String trailerNumber;
    protected String driverCode;
    protected String sealNumber;
    protected String trailerOwnerCarrierCode;
    protected String trailerLicenseNumber;
    protected String tractorNumber;
    protected String tractorOwnerCarrierCode;
    protected String tractorLicenseNumber;
    protected String driverLicenseNumber;
    protected String inGateDateTime;
    protected Boolean loadScheduleCompletedFlag;
    protected String loadStartDateTime;
    protected String loadEndDateTime;
    protected BigDecimal totalLoadingHours;
    protected BigDecimal totalUnloadingHours;
    protected BigDecimal totalDrivingHours;
    protected BigDecimal totalWaitHoursForLoad;
    protected BigDecimal totalOnDutyHours;
    protected BigDecimal totalOffDutyHours;
    protected BigDecimal weekendHolidayBreakHours;
    protected String planningStatusEnumVal;
    protected Boolean applyResourceConstraintsFlag;
    protected Boolean includeInResourceUsageCountFlag;
    protected String resourceProjectTypeCode;
    protected Boolean validateCarrierServiceEquipmentFlag;
}
