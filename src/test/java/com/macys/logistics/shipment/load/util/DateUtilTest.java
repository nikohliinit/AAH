package com.macys.logistics.shipment.load.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DateUtilTest {

    @Test
	void getSimpleDate_test() throws DatatypeConfigurationException {
    	XMLGregorianCalendar xmlGregorianCalendar = getXGC();
    	Date expectedDate = xmlGregorianCalendar.toGregorianCalendar().getTime();
    	Date actualDate = DateUtil.getSimpleDate(xmlGregorianCalendar);
    	assertEquals(expectedDate, actualDate);
    }
    
    @Test
	void getCurrentXMLDate_test() throws DatatypeConfigurationException {
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    	GregorianCalendar c = new GregorianCalendar();
		c.setTime(Date.from(Instant.now()));
		XMLGregorianCalendar expectedXgc = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
		String expected = formatter.format(expectedXgc.toGregorianCalendar().toZonedDateTime());
    	XMLGregorianCalendar actualDate = DateUtil.getCurrentXMLDate();
    	String actual = formatter.format(actualDate.toGregorianCalendar().toZonedDateTime());
    	assertEquals(expected, actual);
    }
    
    @Test
	void getPickupFromDateTime_test() throws DatatypeConfigurationException {
    	Date apptStartTime =  new Date();
    	GregorianCalendar c = new GregorianCalendar();
		c.setTime(apptStartTime);
		XMLGregorianCalendar expectedDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
		XMLGregorianCalendar actualDate = DateUtil.getPickupFromDateTime(apptStartTime);
    	assertEquals(expectedDate, actualDate);
    }
    
    @Test
	void getPickupToDateTime_test() throws DatatypeConfigurationException {
    	Duration plus20Days = DatatypeFactory.newInstance().newDurationDayTime(Boolean.TRUE, 20, 0, 0, 0);
    	Date apptStartTime =  new Date();
    	GregorianCalendar c = new GregorianCalendar();
		c.setTime(apptStartTime);

		XMLGregorianCalendar expectedDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
		expectedDate.add(plus20Days);
		XMLGregorianCalendar actualDate = DateUtil.getPickupToDateTime(apptStartTime);
    	assertEquals(expectedDate, actualDate);
    }
    
    @Test
	void getDeliveryFromDateTime_test() throws DatatypeConfigurationException {
    	Duration plus1Minute = DatatypeFactory.newInstance().newDurationDayTime(Boolean.TRUE, 0, 0, 1, 0);
    	Date apptStartTime =  new Date();
    	GregorianCalendar c = new GregorianCalendar();
		c.setTime(apptStartTime);

		XMLGregorianCalendar expectedDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
		expectedDate.add(plus1Minute);
		XMLGregorianCalendar actualDate = DateUtil.getDeliveryFromDateTime(apptStartTime);
    	assertEquals(expectedDate, actualDate);
    }
    
    @Test
	void getDeliveryToDateTime_test() throws DatatypeConfigurationException {
    	Duration plus20Days = DatatypeFactory.newInstance().newDurationDayTime(Boolean.TRUE, 20, 0, 0, 0);
    	Duration plus1Minute = DatatypeFactory.newInstance().newDurationDayTime(Boolean.TRUE, 0, 0, 1, 0);
    	Date apptStartTime =  new Date();
    	GregorianCalendar c = new GregorianCalendar();
		c.setTime(apptStartTime);

		XMLGregorianCalendar expectedDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
		expectedDate.add(plus20Days);
		expectedDate.add(plus1Minute);
		XMLGregorianCalendar actualDate = DateUtil.getDeliveryToDateTime(apptStartTime);
    	assertEquals(expectedDate, actualDate);
    }
    
    public XMLGregorianCalendar getXGC() throws DatatypeConfigurationException { 
    	String date = "2024-04-12 14:30:00";
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    	LocalDate ld = LocalDate.parse(date, formatter);
    	return DatatypeFactory.newInstance().newXMLGregorianCalendar(ld.toString());
    }
    
    @Test
	void minutesRoundoff_test() throws DatatypeConfigurationException {
    	XMLGregorianCalendar date = DateUtil.convertStringToXMLDate("2024-04-05T14:45:00");
    	XMLGregorianCalendar expectedDate = date;
    	int minutes = expectedDate.getMinute();
        int roundedMinutes = (minutes < 30) ? 0 :30;
        expectedDate.setMinute(roundedMinutes);
        expectedDate.setSecond(0);   	
        XMLGregorianCalendar actualDate = DateUtil.roundOffMinutes(date);
    	assertEquals(expectedDate, actualDate);
    }
    
    @Test
	void minutesRoundoff_checkZero_test() throws DatatypeConfigurationException {
    	XMLGregorianCalendar date = DateUtil.convertStringToXMLDate("2024-04-05T14:15:00");
    	XMLGregorianCalendar expectedDate = date;
    	int minutes = expectedDate.getMinute();
        int roundedMinutes = (minutes < 30) ? 0 :30;
        expectedDate.setMinute(roundedMinutes);
        expectedDate.setSecond(0);   	
        XMLGregorianCalendar actualDate = DateUtil.roundOffMinutes(date);
    	assertEquals(expectedDate, actualDate);
	}

	@Test
	void testXmlCalendertoStringByFormatSuccess() throws DatatypeConfigurationException {
		XMLGregorianCalendar xmlDate = DatatypeFactory.newInstance()
				.newXMLGregorianCalendar("2024-09-24T13:51:51.648+05:30");
		String result = DateUtil.xmlCalendertoStringByFormat(xmlDate, Constants.PICKUP_AND_DELIVERY_DATE_TIME_FORMAT);
		assertNotNull(result);
	}

	@Test
	void testXmlCalendertoStringByFormatNull() {
		XMLGregorianCalendar xmlDate = null;
		String result = DateUtil.xmlCalendertoStringByFormat(xmlDate, Constants.PICKUP_AND_DELIVERY_DATE_TIME_FORMAT);
		assertEquals("", result);
	}
}

