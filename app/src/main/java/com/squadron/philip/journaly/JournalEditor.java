package com.squadron.philip.journaly;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squadron.philip.journaly.database.AppDatabase;
import com.squadron.philip.journaly.database.entity.JournalEntity;

import java.util.Calendar;
import java.util.Date;


public class JournalEditor extends FragmentActivity {

    private TextView info;
    private EditText content;
    private ImageButton save;
    private ImageButton place;
    private TextView selectedPlace;
    private LinearLayout preview;

    private int PLACE_PICKER_REQUEST = 1;
    private ImageButton gallery;
    private static JournalEntity journalEntity;
    private AppDatabase mAppDatabase;
    private AppExecutors appExecutors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.journal_editor);
        appExecutors = new AppExecutors();

        initializeComponents();
        showPreview();

        mAppDatabase  = AppDatabase.getInstance(getApplicationContext());

        if (getIntent() != null && getIntent().hasExtra(MainActivity.EDITOR)){
            journalEntity = (JournalEntity)getIntent().getSerializableExtra(MainActivity.EDITOR);

            info.setText("Created at: " + journalEntity.getDateAdded().toGMTString());
        } else {
            journalEntity=new JournalEntity();
            journalEntity.setDateAdded(new Date());
            info.setText("Date: " + new Date().toGMTString());
        }
        initEditor(journalEntity);

    }

    @Override
    protected void onResume() {
        super.onResume();
        showPreview();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
                selectedPlace.setText(place.getName());
                journalEntity.setLocation(place.getName().toString());
                showPreview();
            }
        }

    }

    public void initEditor(JournalEntity journalEntity){
        info.setText(journalEntity.getLastModifiedDate().toGMTString());
        content.setText(journalEntity.getContent());
        selectedPlace.setText(journalEntity.getLocation());
    }

    public void initializeComponents(){
        info=(TextView)findViewById(R.id.noteitemtextview);
        content=(EditText)findViewById(R.id.noteItemContent);
        save=(ImageButton)findViewById(R.id.noteitemsave);
        place=(ImageButton)findViewById(R.id.place);
        selectedPlace = (TextView)findViewById(R.id.selectedPlace);
        preview = (LinearLayout) findViewById(R.id.preview);
    }

    public void showPreview() {
        if(!selectedPlace.getText().toString().isEmpty()) {
            preview.setVisibility(View.VISIBLE);
        } else {
            preview.setVisibility(View.GONE);
        }
    }

    public void showTimePicker(View view) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void showDatePicker(View view) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }


    public void saveJournal(View view) {
        String content = this.content.getText().toString();
        Date date = new Date();
        int hourOfDay = date.getHours();
        int minutes = date.getMinutes();
        String am0rPm = hourOfDay > 12 && hourOfDay <= 24 ? "PM" : "AM";


        if (journalEntity.getTime().isEmpty()){
            journalEntity.setTime(formatter(hourOfDay)+ ":" + formatter(minutes)+am0rPm);
        }

        if (content.isEmpty()) {
            Toast.makeText(this, "Add some content to save", Toast.LENGTH_SHORT).show();
        } else {
            journalEntity.setContent(content);
                appExecutors.diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        if(getIntent() != null && getIntent().hasExtra(MainActivity.EDITOR)) {
                            mAppDatabase.journalDao().updateJournal(journalEntity);
                        } else {
                            mAppDatabase.journalDao().InsertJournal(journalEntity);
                        }
                        finish();
                    }
                });

            }

    }


    public void firebaseListener(DatabaseReference myRef){
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Object value = dataSnapshot.getValue(Object.class);
                Log.d("FIREBASE", "Value is: " + value.toString());
            }

            @Override
            public void onCancelled(DatabaseError error) {

                Log.w("FIREBASE", "Failed to read value.", error.toException());
            }
        });
    }


    public void addLocation(View view) {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(JournalEditor.this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }


    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String am0rPm = hourOfDay > 12 && hourOfDay <= 24 ? "PM" : "AM";
            journalEntity.setTime(formatter(hourOfDay)+" : "+ formatter(minute)+ " "+ am0rPm);
        }
    }


    private static String formatter(int item){
        String iTstr = String.valueOf(item);
        if(iTstr.length() < 2){
            return "0"+item;
        }
        return iTstr;
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            journalEntity.setDateAdded(new Date(year, month, day));
        }
     }

    }


