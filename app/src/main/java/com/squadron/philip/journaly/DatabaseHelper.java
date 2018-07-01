package com.squadron.philip.journaly;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.squadron.philip.journaly.model.NoteModel;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by g33k5qu4d on 4/17/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {


    public static String DB_PATH = "/data/data/com.squadron.philip.journaly/databases/";

    public static String DB_NAME = "drulist.sqlite";
    public static final int DB_VERSION = 1;
    public static final String TB_DRUGS = "Products";

    private ContentResolver resolver;

    public static final String TABLE_NAME="dist";
    public static final String cID="_id";
    public static final String COLUMN_NAME_about="dinfo";
    public static final String COLUMN_NAME_DNAME="dname";
    public static final String COLUMN_NAME_CAUSES="causes";
    public static final String COLUMN_NAME_PREVENTION="prevention";
    public static final String COLUMN_NAME_DIAGNOSIS="diagonosis";
    public static final String COLUMN_NAME_SIGNS="signs";
    public static final String COLUMN_NAME_TREATMENT="treatment";
    public static final String COLUMN_NAME_IMAGEURL="imageurl";

    public static final String DB_PILLPOX="pillbox";
    public static final String COLUMN_PILLBOX_splshape_text="splshape_text";
    public static final String COLUMN_PILLBOX_splcolor_text="splcolor_text";
    public static final String COLUMN_PILLBOX_rxstring_new="rxstring_new";
    public static final String COLUMN_PILLBOX_medicine_name="medicine_name";
    public static final String COLUMN_PILLBOX_splcolor_new="splcolor_new";
    public static final String COLUMN_PILLBOX_AUTHOR="author";
    public static final String COLUMN_PILLBOX_splshape_new="splshape_new";


    public static final String COLUMN_DrugFact_TABLENAME = "DrugFact";
    public static final String COLUMN_DrugFact_ID = "__ID";
    public static final String COLUMN_DrugFact_TITLE = "name";
    public static final String COLUMN_DrugFact_CONTENT = "info";
    public static final String COLUMN_DrugFact_sign = "sign";
    public static final String COLUMN_DrugFact_effect = "Effect";

    private static final String FTS_VIRTUAL_TABLE = "drugs";

    private static final String DATABASE_CREATE =
            "CREATE VIRTUAL TABLE " + FTS_VIRTUAL_TABLE + " USING fts3(" +
                    cID + "," +
                    COLUMN_NAME_DNAME + "," +
                    COLUMN_NAME_about + "," +
                    COLUMN_NAME_CAUSES + "," +
                    COLUMN_NAME_PREVENTION + "," +
                    COLUMN_NAME_DIAGNOSIS + "," +
                    COLUMN_NAME_SIGNS + "," +
                    COLUMN_NAME_TREATMENT + "," +
                    COLUMN_NAME_IMAGEURL + "," +
                    " UNIQUE (" + cID + "));";



    private SQLiteDatabase myDb;
    private Context context;

    public static abstract class Notes implements BaseColumns {
        public static final String COLUMN_NOTE_TABLENAME = "noteTable";
        public static final String COLUMN_NOTE_ID = "__ID";
        public static final String COLUMN_NOTE_TITLE = "title";
        public static final String COLUMN_NOTE_CONTENT = "content";
        public static final String COLUMN_NOTE_DATECREATED = "datecreated";
        public static final String COLUMN_NOTE_DATEMODIFIED = "datemodified";
    }


    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        resolver=context.getContentResolver();
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.w("LSGURL", DATABASE_CREATE);
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TB_DRUGS);
        db.execSQL("DROP TABLE IF EXISTS " + FTS_VIRTUAL_TABLE);
        // Create tables again
        onCreate(db);
    }

    @Override
    public synchronized void close() {
        if (myDb != null) {
            myDb.close();
        }

        super.close();
    }

    private boolean checkDataBase() {
        SQLiteDatabase tempDB = null;
        try {
            String myPath = DB_PATH + DB_NAME;
            Log.e("context path", context.getFilesDir().getPath().toString());
            tempDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);

        } catch (SQLiteException e) {
            Log.e("philcorp", e.getMessage());
        }
        if (tempDB != null)
            tempDB.close();
        return tempDB != null ? true : false;
    }

    public void copyDataBase() throws IOException {
        try {
            InputStream myInput = context.getAssets().open(DB_NAME);
            String outputFileName = DB_PATH + DB_NAME;
            OutputStream myOutput = new FileOutputStream(outputFileName);

            byte[] buffer = new byte[1024];
            int length;

            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }

            myOutput.flush();
            myOutput.close();
            myInput.close();
        } catch (Exception e) {
            Log.e("philcorp", e.getMessage());
        }

    }

    public void openDataBase() throws SQLException {
        String myPath = DB_PATH + DB_NAME;
        myDb = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE | SQLiteDatabase.NO_LOCALIZED_COLLATORS);
    }

    public void createDataBase() {
        boolean dbExist = checkDataBase();

        if (dbExist) {

        } else {
            this.getReadableDatabase();
            try {
                copyDataBase();
            } catch (IOException e) {
                Log.e("g33kcreate", e.getMessage());
            }
        }
    }



    public List<NoteModel> getAllNotes(){
        List<NoteModel> list=new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor c;
        try {
            c = db.rawQuery("SELECT * FROM " + Notes.COLUMN_NOTE_TABLENAME , null);
            if (c == null)
                return null;
            c.moveToFirst();
            do {
                NoteModel noteModel = new NoteModel();
                noteModel.setId(c.getInt(c.getColumnIndexOrThrow(Notes._ID)));
                noteModel.setTitle(c.getString(c.getColumnIndexOrThrow(Notes.COLUMN_NOTE_TITLE)));
                noteModel.setContent(c.getString(c.getColumnIndexOrThrow(Notes.COLUMN_NOTE_CONTENT)));
                noteModel.setDate(c.getString(c.getColumnIndexOrThrow(Notes.COLUMN_NOTE_DATECREATED)));
                noteModel.setDatemodified(c.getString(c.getColumnIndexOrThrow(Notes.COLUMN_NOTE_DATEMODIFIED)));
                list.add(noteModel);
                Log.e("DailyamenError", "Added: "+noteModel.getTitle());
            } while (c.moveToNext());
            c.close();
        } catch (Exception e) {
            Log.e("DailyamenError", e.getMessage());
        }

        db.close();

        return list;
    }


    public long addNote(String title, String content, String datecreated,String datemodified){
        SQLiteDatabase db = this.getWritableDatabase();

// Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(Notes.COLUMN_NOTE_TITLE, title);
        values.put(Notes.COLUMN_NOTE_CONTENT, content);
        values.put(Notes.COLUMN_NOTE_DATECREATED, datecreated);
        values.put(Notes.COLUMN_NOTE_DATEMODIFIED, datemodified);

// Insert the new row, returning the primary key value of the new row
        long newRowId;
        return db.insert(
                Notes.COLUMN_NOTE_TABLENAME,
                null,
                values
        );
    }

    public NoteModel retrieveNote(int id){

        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                Notes._ID,
                Notes.COLUMN_NOTE_TITLE,
                Notes.COLUMN_NOTE_CONTENT,
                Notes.COLUMN_NOTE_DATECREATED,
                Notes.COLUMN_NOTE_DATEMODIFIED
        };

        String sortOrder =
                Notes._ID + " ASC";
        String selection = Notes._ID + " = ";
        String[] selectionArgs = { String.valueOf(id) };

        Cursor c = db.query(
                Notes.COLUMN_NOTE_TABLENAME,  // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        c.moveToFirst();
        int itemId = c.getInt(c.getColumnIndexOrThrow(Notes._ID));
        String title=c.getString(c.getColumnIndex(Notes.COLUMN_NOTE_TITLE));
        String content=c.getString(c.getColumnIndex(Notes.COLUMN_NOTE_CONTENT));
        String datecreated=c.getString(c.getColumnIndex(Notes.COLUMN_NOTE_DATECREATED));
        String datemodified=c.getString(c.getColumnIndex(Notes.COLUMN_NOTE_DATEMODIFIED));

        return new NoteModel(itemId,title,content,datecreated,datemodified);
    }

    public int updateNote(int id,String title, String content, String datecreated,String datemodified){
        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(Notes.COLUMN_NOTE_TITLE, title);
        values.put(Notes.COLUMN_NOTE_CONTENT, content);
        values.put(Notes.COLUMN_NOTE_DATECREATED, datecreated);
        values.put(Notes.COLUMN_NOTE_DATEMODIFIED, datemodified);


        String selection = Notes._ID + " like ?";
        String[] selectionArgs = { String.valueOf(id) };

        int count = db.update(
                Notes.COLUMN_NOTE_TABLENAME,
                values,
                selection,
                selectionArgs);
        return count;
    }

    public int deleteNote(int id){
        // Define 'where' part of query.
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = Notes._ID + " LIKE ?";

        String[] selectionArgs = { String.valueOf(id) };

        return db.delete(Notes.COLUMN_NOTE_TABLENAME, selection, selectionArgs);

    }

}
