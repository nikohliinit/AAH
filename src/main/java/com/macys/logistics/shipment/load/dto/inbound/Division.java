package com.macys.logistics.shipment.load.dto.inbound;

import lombok.Getter;

@Getter
public enum Division {
  MCY(71, "MCY"),
  BOS(76, "BOS"),
  BLM(72, "BLM"),
  MBK(77, "MBK");

  private final int divNumber;
  private final String abbreviation;

  Division(int divNumber, String abbreviation) {
    this.divNumber = divNumber;
    this.abbreviation = abbreviation;
  }

  public static String getDivAbbreviationByDivNumber(int divisionNumber) {
    for (Division division : Division.values()) {
      if (division.getDivNumber() == divisionNumber) {
        return division.getAbbreviation();
      }
    }
    throw new IllegalArgumentException("Invalid division number: " + divisionNumber);
  }
}
