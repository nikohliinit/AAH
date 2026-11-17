package com.macys.logistics.shipment.load.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.macys.logistics.shipment.load.dto.request.TrailerEvent;
import com.macys.logistics.shipment.load.exception.PhotoGrabberException;

import java.util.List;

public interface PhotoGrabberService {

  void uploadPhotoGrabberFiles() throws PhotoGrabberException;

  List<String> attachImagesToLoad(TrailerEvent trailerEvent) throws PhotoGrabberException, JsonProcessingException;
}
