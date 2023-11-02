package viethung.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author itsol.hungtt
 */
public class DateUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(DateUtils.class);
    public static String DATE_TIME_FORMAT = "dd-MM-yyyy HH:mm:ss";
    public static String YYYY_MM_DD_HH_MM_SS_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static String DD_MM_YYYY_HH_MM_SS_FORMAT = "dd-MM-yyyy HH:mm:ss";
    public static String DD_MM_YYYY_HH_MM_FORMAT = "dd/MM/yyyy HH:mm";
    public static String DD_MM_YYYY_FORMAT = "dd-MM-yyyy";
    public static String TIME_ZONE = "Asia/Ho_Chi_Minh";
    public static String DATE_FORMAT = "dd/MM/yyyy";
    /*public static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)
            .withZone(ZoneId.of(TIME_ZONE));*/
    public static DateTimeFormatter FORMATTER_UTC = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)
            .withZone(ZoneId.of("UTC"));

    public final static DateTimeFormatter FORMATTER = DateTimeFormatter
            .ofPattern(DATE_TIME_FORMAT)
            .withZone(ZoneId.from(ZoneOffset.UTC));
    /**
     * Convert Instant to String with default format is dd-MM-yyyy HH:mm:ss
     *
     * @param instant
     * @return
     */
    public static String asString(Instant instant) {
        if (instant == null) {
            return null;
        }
        return FORMATTER.format(instant);
    }

    public static String asString(Instant instant, String format) {
        if (instant == null) {
            return null;
        }
        return DateTimeFormatter.ofPattern(format)
                .withZone(ZoneId.of(TIME_ZONE))
                .format(instant);
    }

    public static String asString(ZonedDateTime zdt) {
        if (zdt == null) {
            return null;
        }
        return FORMATTER.format(zdt);
    }

    public static String asString(ZonedDateTime zdt, String format) {
        if (zdt == null) {
            return null;
        }
        return DateTimeFormatter.ofPattern(format)
                .withZone(ZoneId.of(TIME_ZONE))
                .format(zdt);
    }

    public static Instant asInstant(String str) {
        return Instant.from(FORMATTER.parse(str));
    }

    public static Instant asInstant(String str, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format)
                .withZone(ZoneId.of(TIME_ZONE));
        return Instant.from(formatter.parse(str));
    }

    public static Instant asInstantUTC(String str) {
        if (StringUtils.isEmpty(str)) {
            return null;
        }
        return Instant.from(FORMATTER_UTC.parse(str));
    }

    /**
     * @param str Format dd-MM-yyyy HH:mm:ss
     * @return
     */
    public static ZonedDateTime asZonedDateTime(String str) {
        return ZonedDateTime.from(FORMATTER.parse(str));
    }

    public static ZonedDateTime asZonedDateTime(String str, String format) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format)
                .withZone(ZoneId.of(TIME_ZONE));
        return ZonedDateTime.from(dateTimeFormatter.parse(str));
    }

    public static ZonedDateTime getStartOfDay(ZonedDateTime zdt) {
        ZoneId zoneId = ZoneId.of(TIME_ZONE);
        return zdt.toLocalDate().atStartOfDay(zoneId);
    }

    public static ZonedDateTime getEndOfDay(ZonedDateTime zdt) {
        ZoneId zoneId = ZoneId.of(TIME_ZONE);
        return zdt.toLocalDate().atStartOfDay(zoneId).plusDays(1);
    }

    public static Date asDate(String str, String format) {
        if (!StringUtils.hasText(str)) {
            return null;
        }
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
            return simpleDateFormat.parse(str);
        } catch (ParseException e) {
            return null;
        }
    }

    public static Instant getStartOfDayInstant(Instant instant) {
        return getStartOfDay(asZonedDateTime(asString(instant))).toInstant();
    }

    public static Instant getEndOfDayInstant(Instant instant) {
        return getEndOfDay(asZonedDateTime(asString(instant))).toInstant();
    }

    public static Instant adjustDate(Instant instant, TemporalUnit unit, Integer value) {
        return instant.plus(value, unit);
    }

    public static Date getStartOffDay(Date date) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date getStartOffDayPlusMonth(Date date, Integer month) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.MONTH, month);
        return calendar.getTime();
    }

    public static String convertTimeFormat(String time, String inputFormat, String outputFormat) {
        if (StringUtils.isEmpty(time)) {
            return "";
        }
        try {
            String outTime = "";
            if (inputFormat.isEmpty()) inputFormat = DATE_TIME_FORMAT;
            if (outputFormat.isEmpty()) outputFormat = DATE_TIME_FORMAT;
            SimpleDateFormat simpleDateFormatInput = new SimpleDateFormat(inputFormat);
            SimpleDateFormat simpleDateFormatOutput = new SimpleDateFormat(outputFormat);
            if (!time.isEmpty()) {
                outTime = simpleDateFormatOutput.format(simpleDateFormatInput.parse(time));
            }
            return outTime;
        } catch (ParseException ex) {
            ex.printStackTrace();
            return "";
        }
    }

    public static List<String> getListDayOfMonth(ZonedDateTime date) {
        List<String> result = new ArrayList<>();
        int year = date.getYear();
        int month = date.getMonthValue();
        String strMonth = (month < 10 ? "0" : "") + month;
        int lengthOfMonth = date.toLocalDate().lengthOfMonth();
        for (int day = 1; day <= lengthOfMonth; day++) {
            String strDay = (day < 10 ? "0" : "") + day;
            result.add(year + "-" + strMonth + "-" + strDay);
        }
        return result;
    }

    public static long between(ZonedDateTime d1, ZonedDateTime d2, ChronoUnit unit) {
        return unit.between(d1, d2);
    }

    public static LocalDate getStartOfDayInstantToLocalDate(Instant instant) {
        return getStartOfDay(asZonedDateTime(asString(instant))).toLocalDate();
    }

    public static String asInstantFormatToString(String str, String fromFormat, String toFormat) {
        if (!StringUtils.hasText(str)) {
            return null;
        }
        if (fromFormat.split(" ").length < 2) {
            fromFormat = fromFormat + " HH:mm:ss";
        }
        if (str.split(" ").length < 2) {
            str = str + " 00:00:00";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(fromFormat)
                .withZone(ZoneId.of(TIME_ZONE));
        return asString(Instant.from(formatter.parse(str)), toFormat);
    }

    public static String dateToString(Date date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return format.format(date);
    }

    public static String dateToStringYYYYMMDD(Date date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    public static String dateToString(Date date, String format) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }

    public static boolean checkDateLoan(String valueDate, String maturityDate) {
        if (!StringUtils.hasText(valueDate) || !StringUtils.hasText(maturityDate)) {
            return true;
        }
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            Date checkDate = format.parse("20181227");
            Date vDate = format.parse(valueDate);
            Date mDate = format.parse(maturityDate);
            return vDate.after(checkDate) || mDate.after(checkDate);
        } catch (ParseException e) {
            return true;
        }
    }

    public static Timestamp asTimestamp(Instant instant) {
        if (instant == null) {
            return null;
        }
        return Timestamp.from(instant);
    }

    public static boolean checkDateValidMonth(String beginDate, String checkDate) {
        LocalDate begin = LocalDate.parse(beginDate, DateTimeFormatter.ofPattern("yyyy-MM-dd 00:00:00"));
        LocalDate check = LocalDate.parse(checkDate, DateTimeFormatter.ofPattern("yyyy-MM-dd 00:00:00"));
        LOGGER.debug("[Check date valid month] with REQUEST [beginDate: {}] <<>> [checkDate: {}]", beginDate, checkDate);
        if (check.isBefore(begin)) {
            return false;
        }
        Period period = Period.between(begin, check);
        int max = period.getMonths() + period.getYears() * 12;
        int maxDayCheck = check.getDayOfMonth();
        int dayValue = begin.getDayOfMonth();
        if (period.getDays() > 0 && maxDayCheck < dayValue) {
            max = max + 1;
        }
        LocalDate validDate = begin.plus(max, ChronoUnit.MONTHS);
        LOGGER.debug("[Check date valid month] with RESPONSE [validDate: {}] <<>> [result: {}]", validDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")), validDate.equals(check));
        return validDate.equals(check);
    }

    public static String getStartOfMonth(ZonedDateTime currentDate) {
        int year = currentDate.getYear();
        int month = currentDate.getMonthValue();
        String strMonth = (month < 10 ? "0" : "") + month;
        return year + "-" + strMonth + "-" + "01";
    }

    public static String getStartOfPreviousMonth(ZonedDateTime currentDate) {
        int month = currentDate.getMonthValue() - 1;
        int year = currentDate.getYear();
        if (month == 0) {
            return (year - 1) + "-12-" + "01";
        } else {
            String strMonth = (month < 10 ? "0" : "") + month;
            return year + "-" + strMonth + "-" + "01";
        }
    }

    public static String localDateTimeToString(LocalDateTime instant) {
        if (instant == null) {
            return null;
        }
        return FORMATTER.format(instant);
    }

    public static LocalDateTime stringToLocalDateTime(String strDate) {
        if (StringUtils.isEmpty(strDate)) {
            return null;
        }
        return LocalDateTime.parse(strDate, FORMATTER);
    }
    public static LocalDateTime dashDateFormatToLocalDateTime(String date) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(DD_MM_YYYY_FORMAT);

        DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder().append(dateFormatter)
                .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                .toFormatter();

        //DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDateTime.parse(date, dateTimeFormatter);
    }
    public static LocalDateTime dashDateFormatToLocalDate(String date) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(DD_MM_YYYY_FORMAT);

        DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder().append(dateFormatter)
                .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                .toFormatter();

        //DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDateTime.parse(date, dateTimeFormatter);
    }
    public static String getDayCurrent () {
    	String day = "";
        Calendar calendar = Calendar.getInstance();
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH)+1;
        int years = calendar.get(Calendar.YEAR);
        
        day = "Ngày "+dayOfMonth+" tháng "+month+" năm "+years;
        return day;
    }
}
