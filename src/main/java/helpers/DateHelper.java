/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package helpers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author NeRooN
 */
public class DateHelper {

    static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public static boolean isValidDate(String dateStr) {
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(dateStr);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public static String convertDateToString(Calendar calendar) {
        return dateFormat.format(calendar.getTime());
    }

    private static Calendar convertStringToCalendar(String date) {
        if (!isValidDate(date)) {
            return null;
        }
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateFormat.parse(date));
            return calendar;
        } catch (ParseException ex) {
            Logger.getLogger(DateHelper.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public static String getFecha(Calendar cal) {
        return getDay(cal.get(Calendar.DAY_OF_WEEK)) + " " + cal.get(Calendar.DAY_OF_MONTH) + " " + getMonth(cal.get(Calendar.MONTH)) + " " + cal.get(Calendar.YEAR);
    }

    public static String getMonth(int month) {
        return switch (month) {
            case Calendar.JANUARY -> "Enero";
            case Calendar.FEBRUARY -> "Febrero";
            case Calendar.MARCH -> "Marzo";
            case Calendar.APRIL -> "Abril";
            case Calendar.MAY -> "Mayo";
            case Calendar.JUNE -> "Junio";
            case Calendar.JULY -> "Julio";
            case Calendar.AUGUST -> "Agosto";
            case Calendar.SEPTEMBER -> "Septiembre";
            case Calendar.OCTOBER -> "Octubre";
            case Calendar.NOVEMBER -> "Noviembre";
            case Calendar.DECEMBER -> "Diciembre";
            default -> "error";
        };
    }

    public static String getDay(int day) {

        return switch (day) {
            case Calendar.SUNDAY -> "Dom";
            case Calendar.MONDAY -> "Lun";
            case Calendar.TUESDAY -> "Mar";
            case Calendar.WEDNESDAY -> "Mier";
            case Calendar.THURSDAY -> "Jue";
            case Calendar.FRIDAY -> "Vie";
            case Calendar.SATURDAY -> "Sab";
            default -> "";
        };
    }

    public static boolean isDateCorrect(String date) {
        Calendar calendar = convertStringToCalendar(date);
        calendar.getTime();
        Calendar today = Calendar.getInstance();
        today.setTime(new Date());

        return today.before(calendar);
    }

    public static Date getDate(String date) {
        return convertStringToCalendar(date).getTime();
    }
}
