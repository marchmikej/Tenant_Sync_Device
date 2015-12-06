package com.tenantsync.testproject3;

/**
 * Created by Dad on 12/2/2015.
 */
public class MessageContain {
    public String date;
    public String message;
    public int direction;

    public MessageContain(String message, int direction, String created_at) {
        this.message=message;
        this.direction=direction;
        try {
            String year = created_at.substring(0, 4);
            String month = created_at.substring(5, 7);
            String day = created_at.substring(8, 10);
            String hour = created_at.substring(11, 13);
            String minute = created_at.substring(14, 16);
            date = "";
            String ampm="";
            if (month.equals("01")) {
                date = "Jan";
            } else if (month.equals("02")) {
                date = "Feb";
            } else if (month.equals("03")) {
                date = "Mar";
            } else if (month.equals("04")) {
                date = "Apr";
            } else if (month.equals("05")) {
                date = "May";
            } else if (month.equals("06")) {
                date = "Jun";
            } else if (month.equals("07")) {
                date = "Jul";
            } else if (month.equals("08")) {
                date = "Aug";
            } else if (month.equals("09")) {
                date = "Sep";
            } else if (month.equals("10")) {
                date = "Oct";
            } else if (month.equals("11")) {
                date = "Nov";
            } else if (month.equals("12")) {
                date = "Dec";
            }

            if(hour.equals("13")) {
                hour="01";
                ampm="pm";
            } else if (hour.equals("14")){
                hour="02";
                ampm="pm";
            } else if (hour.equals("15")){
                hour="03";
                ampm="pm";
            } else if (hour.equals("16")){
                hour="04";
                ampm="pm";
            } else if (hour.equals("17")){
                hour="05";
                ampm="pm";
            } else if (hour.equals("18")){
                hour="06";
                ampm="pm";
            } else if (hour.equals("19")){
                hour="07";
                ampm="pm";
            } else if (hour.equals("20")){
                hour="08";
                ampm="pm";
            } else if (hour.equals("21")){
                hour="09";
                ampm="pm";
            } else if (hour.equals("22")){
                hour="10";
                ampm="pm";
            } else if (hour.equals("23")){
                hour="11";
                ampm="pm";
            } else if (hour.equals("00")){
                hour="12";
                ampm="am";
            } else {
                ampm="am";
            }
            date = date + " " + day + "\n" + hour + ":" + minute + " " + ampm;
        }
        catch (Exception e) {
            date="";
        }
    }


}
