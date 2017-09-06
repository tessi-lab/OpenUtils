/*
 * Copyright 2017 Tessi lab.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.tessilab.oss.openutils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * <b>MyDate is a class representing a calendar date.</b>
 * 
 * @author galaad
 *
 */
public class MyDate implements Comparable<MyDate> {

    private Integer day; // in (1,31)
    private Integer month; // in (1,12)
    private Integer year; // >= 0

    /**
     * Constructor with parameters;
     * 
     * @param day
     *            The day of the month.
     * @param month
     *            The month of the year.
     * @param year
     *            The year.
     */
    public MyDate(Integer day, Integer month, Integer year) {
        this.day = day;
        this.month = month;
        this.year = year;
    }

    // ----------
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((day == null) ? 0 : day.hashCode());
        result = prime * result + ((month == null) ? 0 : month.hashCode());
        result = prime * result + ((year == null) ? 0 : year.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MyDate other = (MyDate) obj;
        if (day == null) {
            if (other.day != null)
                return false;
        } else if (!day.equals(other.day))
            return false;
        if (month == null) {
            if (other.month != null)
                return false;
        } else if (!month.equals(other.month))
            return false;
        if (year == null) {
            if (other.year != null)
                return false;
        } else if (!year.equals(other.year))
            return false;
        return true;
    }

    // ----------

    public boolean isYearBissextile(int year) {
        if ((year % 4 == 0) && (year % 100 != 0))
            return true;
        if (year % 400 == 0)
            return true;
        return false;
    }

    /**
     * Check whether the date is possible.
     * 
     * @return TRUE if the date is possible.
     */
    public boolean isCorrect() {
        boolean monthOK = month > 0 && month <= 12;
        boolean yearOK = year >= 0 && year <= 2099;
        boolean dayOK = false;
        if (day > 0) {
            switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                if (day <= 31)
                    dayOK = true;
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                if (day <= 30)
                    dayOK = true;
                break;
            case 2:
                if (isYearBissextile(year) && day <= 29) {
                    dayOK = true;
                } else if (day <= 28) {
                    dayOK = true;
                }
                break;
            default:
                break;
            }
        }
        return dayOK && monthOK && yearOK;
    }

    // Format: YYYY-MM-DD
    @Override
    public String toString() {
        String dayString = (day < 10 ? "0" : "") + day;
        String monthString = (month < 10 ? "0" : "") + month;
        StringBuilder builder = new StringBuilder();
        for (int k = 0; k < 4 - year.toString().length(); k++) {
            builder.append("0");
        }
        builder.append(year.toString());
        String yearString = builder.toString();
        return yearString + "-" + monthString + "-" + dayString;
    }
    
    public String toFormatedString(SimpleDateFormat format) {
        Date date = new GregorianCalendar(year, month-1, day).getTime();
        return format.format(date);
    }

    /**
     * @param other
     *            Another MyDate date.
     * @return TRUE if this date is more ancient than the other date.
     */
    public boolean isMoreAncient(MyDate other) {
        boolean olderYear = year < other.year;
        boolean sameYearOlderDay = year.equals(other.year) && (month < other.month || (month.equals(other.month) && day < other.day));
        return olderYear || sameYearOlderDay;
    }

    @Override
    public int compareTo(MyDate o) {
        if (equals(o))
            return 0;
        if (isMoreAncient(o))
            return -1;
        else
            return 1;
    }
}
