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

    static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

    public static boolean isValidDate(String dateStr) {
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(dateStr);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public static String convertDateToString(Date date) {
        return dateFormat.format(date.getTime());
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
