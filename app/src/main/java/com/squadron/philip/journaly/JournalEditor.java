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
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.squadron.philip.journaly.model.NoteModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class JournalEditor extends FragmentActivity {

    TextView info;
    EditText content;
    // ImageButton edit;
    ImageButton save;
    ImageButton delete;
    ImageButton share;
    ImageButton place;
    static DatabaseHelper dbHelper;
    static NoteModel noteModel;
    boolean autoSave;
    static String EDIT="EDIT";
    static FragmentTransaction transaction=null;
    int PLACE_PICKER_REQUEST = 1;
    int GALLERY_REQUEST_CODE = 9;
    View view;
    private ImageButton gallery;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.journal_editor);

        dbHelper = new DatabaseHelper(this);
        componentLoader();
        Bundle bundle=getIntent().getExtras();
        if (bundle != null){
            noteModel=(NoteModel)bundle.getSerializable(NoteModel.NOTECONSTANT);
        }else{
            noteModel=new NoteModel(0," "," "," "," ");
        }
        noteProcessor(noteModel);
        notebuttonsProcessor();

    }

    public void componentLoader(){
        info=(TextView)findViewById(R.id.noteitemtextview);

        content=(EditText)findViewById(R.id.noteItemContent);
        delete=(ImageButton)findViewById(R.id.noteitemdelete);
        share=(ImageButton)findViewById(R.id.noteitemshare);
        save=(ImageButton)findViewById(R.id.noteitemsave);
        place=(ImageButton)findViewById(R.id.place);
        gallery = (ImageButton)findViewById(R.id.gallery);

    }

    public void noteProcessor(NoteModel noteModel){
        content.setText(noteModel.getContent());
        info.setText("Last Modified Date: "+noteModel.getDatemodified());
    }

    public String getCurrentDateInfo(){
        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm");
        Log.d("philcorp","Calendardate "+df.format(calendar.getTime()));
        String theDate=df.format(calendar.getTime());
        return theDate;
    }

    public void notebuttonsProcessor(){
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dbHelper.updateNote(noteModel.getId(), " ",
                        content.getText().toString(), noteModel.getDate(), getCurrentDateInfo()) > 0){
                    Toast.makeText(JournalEditor.this, "Note saved", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(JournalEditor.this, "An error occured, Note could not be saved.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                try {
                    startActivityForResult(builder.build(JournalEditor.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
            }
        });

//        delete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
//                DeleteNoteFragment deleteNoteFrag=new DeleteNoteFragment();
//                deleteNoteFrag.show(transaction, "Delete");
//
//            }
//        });
//
//        share.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent sendIntent = new Intent();
//                sendIntent.setAction(Intent.ACTION_SEND);
//                sendIntent.putExtra(Intent.EXTRA_TEXT,
//                "Journal: "+content.getText().toString()+"\n"+
//                "shared from Journaly");
//                sendIntent.setType("text/plain");
//                sendIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(sendIntent);
//            }
//        });


    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();

            }
        }

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            Bitmap thumbnail = data.getParcelableExtra("data");
            Uri fullPhotoUri = data.getData();

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

    public static class DeleteNoteFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Delete Note")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if(dbHelper.deleteNote(noteModel.getId()) > 0){
                                Toast.makeText(getContext(), "Note deleted", Toast.LENGTH_SHORT).show();

                                Intent it=new Intent(getContext(),DrugNote.class);
                                startActivity(it);

                            }
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

            Toast.makeText(getContext(), " "+hourOfDay, Toast.LENGTH_SHORT).show();
        }
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
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            String[] weekDays = new String[]{
                    "Saturday", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday",
            };

            String weekDay = weekDays[dayOfWeek];
            Toast.makeText(getContext(), ""+weekDay, Toast.LENGTH_SHORT).show();
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
            if (dbHelper.updateNote(noteModel.getId(), " ",
                    content.getText().toString(), noteModel.getDate(), getCurrentDateInfo()) > 0) {
                Toast.makeText(JournalEditor.this, "Note Auto-saved", Toast.LENGTH_SHORT).show();
            }
        }

    }


}
