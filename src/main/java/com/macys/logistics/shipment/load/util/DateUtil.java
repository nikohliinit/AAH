package com.macys.logistics.shipment.load.util;

import lombok.extern.log4j.Log4j2;
import lombok.SneakyThrows;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.datatype.Duration;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

@Log4j2
public class DateUtil {
	private static Duration plus1Minute;
	private static Duration plus20Days;

	static {
		try {
			plus1Minute = DatatypeFactory.newInstance().newDurationDayTime(Boolean.TRUE, 0, 0, 1, 0);
			plus20Days = DatatypeFactory.newInstance().newDurationDayTime(Boolean.TRUE, 20, 0, 0, 0);

		} catch (DatatypeConfigurationException ignored) {
			log.error("Error Occured");
		}
	}

	private DateUtil() {
		throw new IllegalStateException("Date Utility class");
	}

	public static LocalDateTime stringToLocalDate(String date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT);
		return LocalDateTime.parse(date, formatter);
	}

	public static XMLGregorianCalendar roundOffMinutes(XMLGregorianCalendar date) {
		int minutes = date.getMinute();
		int roundedMinutes = (minutes < 30) ? 0 : 30;
		date.setMinute(roundedMinutes);
		date.setSecond(0);
		return date;
	}

	public static XMLGregorianCalendar removeTimezoneXML(XMLGregorianCalendar date) {
		XMLGregorianCalendar result = (XMLGregorianCalendar) date.clone();
		result.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
		return result;
	}

	public static XMLGregorianCalendar convertToLocalTimezone(String date, BigDecimal timezoneOffset)
			throws DatatypeConfigurationException {
		LocalDateTime localDateTime = LocalDateTime.parse(date, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
		ZonedDateTime estDateTime = localDateTime.atZone(ZoneId.of("America/New_York"));
		ZonedDateTime localDateWithOffset = estDateTime.plusHours(timezoneOffset.longValue());
		GregorianCalendar gcal = GregorianCalendar.from(localDateWithOffset);
		return DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
	}

	public static XMLGregorianCalendar scheduledToArrivalTime(XMLGregorianCalendar date)
			throws DatatypeConfigurationException {
		int year = date.getYear();
		int month = date.getMonth();
		int day = date.getDay();
		GregorianCalendar gcal = new GregorianCalendar();
		gcal.set(year, month - 1, day, 23, 59, 00);

		return DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
	}

	public static XMLGregorianCalendar convertStringToXMLDate(String date) throws DatatypeConfigurationException {
		LocalDateTime localDateTime = stringToLocalDate(date);
		GregorianCalendar gcal = GregorianCalendar
				.from(localDateTime.atZone(ZoneId.systemDefault()));
		return DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
	}

	public static XMLGregorianCalendar addMinutesToDate(String date, int minutes)
			throws DatatypeConfigurationException {
		LocalDateTime localDateTime = stringToLocalDate(date);
		localDateTime = localDateTime.plusMinutes(minutes);
		GregorianCalendar gcal = GregorianCalendar
				.from(localDateTime.atZone(ZoneId.systemDefault()));
		return DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
	}

	public static XMLGregorianCalendar addMinutesToDate(Date date, int minutes) throws DatatypeConfigurationException {
		GregorianCalendar gcal = new GregorianCalendar();
		gcal.setTime(date);
		gcal.add(Calendar.MINUTE, minutes);
		return DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
	}

	public static XMLGregorianCalendar addDaysToDate(Date date, int days) throws DatatypeConfigurationException {
		GregorianCalendar gcal = new GregorianCalendar();
		gcal.setTime(date);
		gcal.add(Calendar.DAY_OF_MONTH, days);
		return DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
	}

	public static XMLGregorianCalendar addDaysToDate(String date, int days) throws DatatypeConfigurationException {
		LocalDateTime localDateTime = stringToLocalDate(date);
		localDateTime = localDateTime.plusDays(days);
		GregorianCalendar gcal = GregorianCalendar
				.from(localDateTime.atZone(ZoneId.systemDefault()));
		return DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
	}

	public static String addMinutesToDateString(String date, int minutes) {
		LocalDateTime localDateTime = stringToLocalDate(date);
		localDateTime = localDateTime.plusMinutes(minutes);
		String convertedDate = localDateTime.toString();
		if (localDateTime.getSecond() == 0 && localDateTime.getNano() == 0) {
			convertedDate += ":00";
		}
		return convertedDate;
	}

	public static String addMinutesToDateString(Date date, int minutes) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MINUTE, minutes);
		Date updatedDate = calendar.getTime();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		return formatter.format(updatedDate);
	}

	@SneakyThrows
	public static Date getSimpleDate(XMLGregorianCalendar xmlGregorianCalendar) {
		return xmlGregorianCalendar.toGregorianCalendar().getTime();
	}

	/**
	 * Get current date in XML format
	 *
	 * @return
	 */
	@SneakyThrows
	public static XMLGregorianCalendar getCurrentXMLDate() {

		GregorianCalendar c = new GregorianCalendar();
		c.setTime(Date.from(Instant.now()));
		return DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
	}

	/**
	 * Derive pickupFromDate from apptStartTime;
	 *
	 * @param apptStartTime
	 * @return
	 */
	@SneakyThrows
	public static XMLGregorianCalendar getPickupFromDateTime(Date apptStartTime) {

		GregorianCalendar c = new GregorianCalendar();
		c.setTime(apptStartTime);

		return DatatypeFactory.newInstance().newXMLGregorianCalendar(c);

	}

	/**
	 * Derive pickupToDate from apptStartTime + 20 days ;
	 * 
	 * @param apptStartTime
	 * @return
	 */
	@SneakyThrows
	public static XMLGregorianCalendar getPickupToDateTime(Date apptStartTime) {

		GregorianCalendar c = new GregorianCalendar();
		c.setTime(apptStartTime);

		XMLGregorianCalendar xmlDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
		xmlDate.add(plus20Days);
		return xmlDate;
	}

	/**
	 * Derive DeliveryFromDate from apptStartTime + 1minute;
	 *
	 * @param apptStartTime
	 * @return
	 */
	@SneakyThrows
	public static XMLGregorianCalendar getDeliveryFromDateTime(Date apptStartTime) {

		GregorianCalendar c = new GregorianCalendar();
		c.setTime(apptStartTime);

		XMLGregorianCalendar xmlDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
		xmlDate.add(plus1Minute);

		return xmlDate;
	}

	/**
	 * Derive DeliveryToDate from apptStartTime + 20days + 1minute
	 *
	 * @param apptStartTime
	 * @return
	 */
	@SneakyThrows
	public static XMLGregorianCalendar getDeliveryToDateTime(Date apptStartTime) {

		GregorianCalendar c = new GregorianCalendar();
		c.setTime(apptStartTime);

		XMLGregorianCalendar xmlDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
		xmlDate.add(plus20Days);
		xmlDate.add(plus1Minute);

		return xmlDate;
	}

	@SneakyThrows
	public static XMLGregorianCalendar stringToXMLDateByFormat(String date) {
		return DatatypeFactory.newInstance().newXMLGregorianCalendar(date);
	}

	public static XMLGregorianCalendar normalizeDate(XMLGregorianCalendar date) {
		if (date != null) {
			// Remove milliseconds
			date.setMillisecond(DatatypeConstants.FIELD_UNDEFINED);

			// Set timezone to undefined
			date.setTimezone(DatatypeConstants.FIELD_UNDEFINED);

			// Clear fractional seconds
			date.setFractionalSecond(null);
		}
		return date;
	}

	@SneakyThrows
	public static String xmlCalendertoStringByFormat(final XMLGregorianCalendar xmlDate, String format) {
		String formattedDate = "";
		DateFormat formatter = new SimpleDateFormat(format);
		if (xmlDate != null) {
			formattedDate = formatter.format(xmlDate.toGregorianCalendar().getTime());
		}
		return formattedDate;
	}
}
