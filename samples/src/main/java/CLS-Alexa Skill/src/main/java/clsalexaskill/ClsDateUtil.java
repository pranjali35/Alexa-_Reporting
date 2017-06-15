package clsalexaskill;

import java.util.Calendar;
import java.util.Date;

public class ClsDateUtil {

	 private static final String[] DAYS_OF_MONTH = {
	            "1st",
	            "2nd",
	            "3rd",
	            "4th",
	            "5th",
	            "6th",
	            "7th",
	            "8th",
	            "9th",
	            "10th",
	            "11th",
	            "12th",
	            "13th",
	            "14th",
	            "15th",
	            "16th",
	            "17th",
	            "18th",
	            "19th",
	            "20th",
	            "21st",
	            "22nd",
	            "23rd",
	            "24th",
	            "25th",
	            "26th",
	            "27th",
	            "28th",
	            "29th",
	            "30th",
	            "31st"
	    };

	    private static final String[] MONTHS = {
	            "January",
	            "February",
	            "March",
	            "April",
	            "May",
	            "June",
	            "July",
	            "August",
	            "September",
	            "October",
	            "November",
	            "December"
	    };

	    private static final String[] DAYS_OF_WEEK = {
	            "Sunday",
	            "Monday",
	            "Tuesday",
	            "Wednesday",
	            "Thursday",
	            "Friday",
	            "Saturday"
	    };
	    
	    public static String getFormattedDate(Date date) {
	        Date today = new Date();

	        Calendar todayCal = Calendar.getInstance();
	        todayCal.setTime(today);
	        Calendar dateCal = Calendar.getInstance();
	        dateCal.setTime(date);
	        if (todayCal.get(Calendar.YEAR) == dateCal.get(Calendar.YEAR)) {
	            return DAYS_OF_WEEK[dateCal.get(Calendar.DAY_OF_WEEK) - 1] + ' '
	                    + MONTHS[dateCal.get(Calendar.MONTH)] + ' '
	                    + DAYS_OF_MONTH[dateCal.get(Calendar.DATE) - 1];
	        } else {
	            return DAYS_OF_WEEK[dateCal.get(Calendar.DAY_OF_WEEK) - 1] + ' '
	                    + (dateCal.get(Calendar.MONTH) + 1) + '/' + dateCal.get(Calendar.DATE) + '/'
	                    + dateCal.get(Calendar.YEAR);
	        }
	    }


}
