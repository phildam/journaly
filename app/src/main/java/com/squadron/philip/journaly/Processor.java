package com.squadron.philip.journaly;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squadron.philip.journaly.database.entity.JournalEntity;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by philip on 01/07/2018.
 */

public class Processor {
    public String getDayOfWeek(Date date){
        String selectedWeekDay = null;
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            String[] weekDays = new String[]{
                    "Saturday", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday",
            };
            selectedWeekDay =  weekDays[dayOfWeek];
        } catch(Exception e) {
            selectedWeekDay = null;
        }
        return selectedWeekDay;
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

    public static void firebaseListener(DatabaseReference myRef, final Intent intent){
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Object value = dataSnapshot.getValue(Object.class);
                HashMap entities = (HashMap) value;
                if(entities != null){
                    List<JournalEntity> journalEntities = (List<JournalEntity>)entities.get(intent.getStringExtra(MainActivity.USERNAME));
                    Log.e("FIREBASE", "Value is: " + journalEntities);
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("FIREBASE", "Failed to read value.", error.toException());
            }
        });
    }

}
