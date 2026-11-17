package com.macys.logistics.shipment.load.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Stop {

	private String stopId;

	@NotEmpty(message = "shippingLocationCode is mandatory can't be null or empty!!")
	private String shippingLocationCode;

	private String shippingLocationType;

	private Appointment appointment;
}
