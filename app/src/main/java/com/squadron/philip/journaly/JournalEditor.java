package com.squadron.philip.journaly;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.TimeUtils;
import android.view.MenuItem;
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
import com.squadron.philip.journaly.database.AppDatabase;
import com.squadron.philip.journaly.database.entity.JournalEntity;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class JournalEditor extends FragmentActivity {

    private TextView info;
    private EditText content;
    // ImageButton edit;
    private ImageButton save;
    private ImageButton delete;
    private ImageButton share;
    private ImageButton place;
    private TextView selectedPlace;
    private LinearLayout preview;

    boolean autoSave;
    private static String EDIT="EDIT";
    private static FragmentTransaction transaction=null;
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
        delete=(ImageButton)findViewById(R.id.noteitemdelete);
        share=(ImageButton)findViewById(R.id.noteitemshare);
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

    public void share(View view) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                "Journal: "+content.getText().toString()+"\n"+
                        "shared from Journaly");
        sendIntent.setType("text/plain");
        sendIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(sendIntent);
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

    public void deleteJournal(View view) {
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        DeleteNoteFragment deleteNoteFrag=new DeleteNoteFragment();
        deleteNoteFrag.show(transaction, "Delete");

    }

    public void editJournal(View view) {

    }

    public static class DeleteNoteFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Delete Note")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
//                            if(dbHelper.deleteNote(noteModel.getId()) > 0){
//                                Toast.makeText(getContext(), "Note deleted", Toast.LENGTH_SHORT).show();
//
//                                Intent it=new Intent(getContext(),DrugNote.class);
//                                startActivity(it);
//
//                            }
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
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



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }else if(id == R.id.action_edit){
            if(!item.isChecked()){

                saveToPreference(EDIT,true,true);
                boolean flag=(Boolean)getFromPref(EDIT,true,true);
                item.setChecked(flag);

                content.setEnabled(true);
                delete.setEnabled(true);
                share.setEnabled(true);
                save.setEnabled(true);
            }else if(item.isChecked()){

                saveToPreference(EDIT,false,false);
                boolean flag=(Boolean)getFromPref(EDIT,false,false);
                flag=true;
                item.setChecked(flag);

            }

        }else if(id == R.id.action_autosave){
            if(!item.isChecked()){
                item.setChecked(true);
                autoSave=true;
            }else{
                item.setChecked(false);
                autoSave=false;
            }
        }else if(id == R.id.action_deletenote){

        }

        else if(id == R.id.action_remindnote){

        }

        else if(id == R.id.action_save){

        }else if(id == R.id.action_share){

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
        autoSave(true);
    }

    public void saveToPreference(String key, Object value,Object obj){
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        if(obj.getClass() == String.class){
            editor.putString(key,(String)value);
        }else if(obj.getClass() == Boolean.class){
            editor.putBoolean(key,(Boolean)value);
        }else if(obj.getClass() == Integer.class) {
            editor.putInt(key, (Integer) value);
        }
        editor.commit();
    }
    public Object getFromPref(String key,Object obj, Object def){
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);

        Object prefValue=null;
        if(obj.getClass() == String.class){
            String defaultValue = (String)def ;
            prefValue = sharedPref.getString(key, defaultValue);
        }else if(obj.getClass() == Boolean.class){
            Boolean defaultValue = (Boolean)def ;
            prefValue = sharedPref.getBoolean(key, defaultValue);
        }else if(obj.getClass() == Integer.class) {
            Integer defaultValue = (Integer)def ;
            prefValue = sharedPref.getInt(key, defaultValue);
        }

        return prefValue;
    }



    public void autoSave(boolean autosave){
        if(autoSave) {
//            if (dbHelper.updateNote(noteModel.getId(), " ",
//                    content.getText().toString(), noteModel.getDate(), getCurrentDateInfo()) > 0) {
//                Toast.makeText(JournalEditor.this, "Note Auto-saved", Toast.LENGTH_SHORT).show();
//            }
        }

    }


}
