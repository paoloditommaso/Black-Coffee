package org.blackcoffee.commons.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Duration class
 */
public class Duration {


	    static Pattern days = Pattern.compile("^([0-9]+)d$");
	    static Pattern hours = Pattern.compile("^([0-9]+)h$");
	    static Pattern minutes = Pattern.compile("^([0-9]+)mi?n$");
	    static Pattern seconds = Pattern.compile("^([0-9]+)s$");
	    static Pattern millis = Pattern.compile("^([0-9]+)(ms)?$");

	    
	    long value;
	    
	    private Duration() { 
	    	
	    }
	    
	    public long millis() { 
	    	return value;
	    }
	    
	    public int secs() { 
	    	return (int) (value / 1000);
	    }
	    
	    public int mins() { 
	    	return secs() / 60;
	    }
	    
	    public int hours() { 
	    	return mins() / 60;
	    }
	    
	    public int days() {
	    	return hours() / 24;
	    }
	    /**
	     * Parse a duration
	     * @param duration 3h, 2mn, 7s
	     * @return The number of seconds
	     */
	    public static Duration parse(String duration) {

	    	Duration result = new Duration();
	    	
	        result.value = -1;
	        if (days.matcher(duration).matches()) {
	            Matcher matcher = days.matcher(duration);
	            matcher.matches();
	            result.value = Integer.parseInt(matcher.group(1)) * (60 * 60) * 24 * 1000;
	        } 
	        else if (hours.matcher(duration).matches()) {
	            Matcher matcher = hours.matcher(duration);
	            matcher.matches();
	            result.value = Integer.parseInt(matcher.group(1)) * (60 * 60) * 1000;
	        } 
	        else if (minutes.matcher(duration).matches()) {
	            Matcher matcher = minutes.matcher(duration);
	            matcher.matches();
	            result.value = Integer.parseInt(matcher.group(1)) * (60) * 1000;
	        } 
	        else if (seconds.matcher(duration).matches()) {
	            Matcher matcher = seconds.matcher(duration);
	            matcher.matches();
	            result.value = Integer.parseInt(matcher.group(1)) * 1000;
	        } 
	        else if (millis.matcher(duration).matches()) {
	            Matcher matcher = millis.matcher(duration);
	            matcher.matches();
	            result.value = Integer.parseInt(matcher.group(1)) ;
	        }

	        if (result.value == -1) {
	            throw new IllegalArgumentException("Invalid duration pattern : " + duration);
	        }
	        
	        return result;
	    }
	    
	    
	    public String toString() { 
	    	return String.format("Duration[%s ms]", value);
	    }
	    
}
