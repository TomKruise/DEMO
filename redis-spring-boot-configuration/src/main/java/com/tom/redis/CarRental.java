package com.tom.redis;

import java.util.*;
import java.text.SimpleDateFormat;

import static java.util.Collections.sort;

public class CarRental {
    public static Boolean canScheduleAll(Collection<RentalTime> rentalTimes) {
        ArrayList<RentalTime> times = (ArrayList<RentalTime>) rentalTimes;
        sortList(times);
        if (times.size() != 1) {
            return false;
        }
        return true;
    }

    private static void sortList(ArrayList<RentalTime> times) {
        while (times.size() > 1) {
            RentalTime rentalTime = times.get(times.size()-1);
            RentalTime nextRentalTime = times.get(times.size()-2);

            Date rentalTimeEnd = rentalTime.getEnd();
            Date nextRentalTimeStart = nextRentalTime.getStart();

            if (rentalTimeEnd.compareTo(nextRentalTimeStart) > 0) {
                times.remove(nextRentalTime);
            } else {
                times.remove(rentalTime);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("d/M/y H:m");

        ArrayList<RentalTime> rentalTimes = new ArrayList<RentalTime>();
        rentalTimes.add(new RentalTime(sdf.parse("03/05/2020 19:00"), sdf.parse("03/05/2020 20:30")));
        rentalTimes.add(new RentalTime(sdf.parse("03/05/2020 22:10"), sdf.parse("03/05/2020 22:30")));
        rentalTimes.add(new RentalTime(sdf.parse("03/05/2020 20:30"), sdf.parse("03/05/2020 22:00")));

        System.out.println(CarRental.canScheduleAll(rentalTimes));
    }
}

class RentalTime {
    private Date start, end;
    
    public RentalTime(Date start, Date end) {
        this.start = start;
        this.end = end;
    }
    
    public Date getStart() {
        return this.start;
    }
    
    public Date getEnd() {
        return this.end;
    } 
}