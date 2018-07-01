package com.squadron.philip.journaly;

import android.util.Log;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by philip on 01/07/2018.
 */

public class Processor {
    public String getDayOfWeek(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        String[] weekDays = new String[]{
                "Saturday", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday",
        };
       return weekDays[dayOfWeek];
    }

    public static int imageSelector(int position){
            int[] pic = new int[]{
                R.drawable.j1, R.drawable.j2, R.drawable.j3, R.drawable.j4,
                    R.drawable.j5, R.drawable.j6, R.drawable.j7, R.drawable.j8,
                    R.drawable.j9, R.drawable.j10, R.drawable.j11, R.drawable.j12,
                    R.drawable.j13, R.drawable.j14, R.drawable.j15, R.drawable.j16,
                    R.drawable.j17, R.drawable.j18, R.drawable.j19, R.drawable.j20
            };

            return pic[position > 19 || position < 0 ?  0 : position];
    }
}
