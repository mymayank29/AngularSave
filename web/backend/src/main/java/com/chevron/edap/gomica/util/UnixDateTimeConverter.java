package com.chevron.edap.gomica.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class UnixDateTimeConverter {
	
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
		      .withZone(ZoneId.of("UTC"));
	
	private static final DateTimeFormatter formatterNoTime = DateTimeFormatter.ofPattern("yyyy-MM-dd")
		      .withZone(ZoneId.of("UTC"));
	
	public static final String epochMilliToDateTimeString(String epochMilli) {
		return epochMilli != null ? formatter.format(Instant.ofEpochMilli(Long.parseLong(epochMilli))) : null;
	}
	
	public static final String epochMilliToDateString(String epochMilli) {
		return epochMilli != null ? formatterNoTime.format(Instant.ofEpochMilli(Long.parseLong(epochMilli))) : null;
	}
}
