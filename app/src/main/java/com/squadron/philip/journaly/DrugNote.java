package com.squadron.philip.journaly;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squadron.philip.journaly.model.NoteModel;
import com.squadron.philip.journaly.model.Singleton;

import java.util.ArrayList;
import java.util.List;

public class DrugNote extends AppCompatActivity {

    Singleton singleton= Singleton.getInstance();
    private List<NoteModel> noteList;
    DatabaseHelper dbHelper;
    Dapter adapter;
    TextView emptydisp;
    FragmentTransaction transaction=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drug_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME|ActionBar.DISPLAY_SHOW_TITLE);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setElevation(50);


        dbHelper = new DatabaseHelper(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                dbHelper.createDataBase();

            }
        }).start();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.notefab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(DrugNote.this,NoteAdder.class));
            }
        });


        final ListView listView=(ListView)findViewById(R.id.listView);
        noteList=dbHelper.getAllNotes();
        if(noteList != null){
            adapter=new Dapter(this, R.layout.notelist_items, R.id.notetitle,noteList);
        }else {
            noteList=new ArrayList<>();
            NoteModel noteModel=new NoteModel();
            noteModel.setTitle("No Note in List");
            noteList.add(noteModel);
            adapter=new Dapter(this, R.layout.notelist_items, R.id.notetitle,noteList);
        }
        emptydisp=(TextView)findViewById(R.id.displayemptylist);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                noteList=dbHelper.getAllNotes();
                adapter=new Dapter(DrugNote.this, R.layout.notelist_items, R.id.notetitle,noteList);
                listView.setAdapter(adapter);

                NoteModel notemodel =(NoteModel)listView.getItemAtPosition(position);
                JournalEditor noteAdder=new JournalEditor();
                Bundle bundle=new Bundle();
                bundle.putSerializable(NoteModel.NOTECONSTANT, notemodel);
                Intent intent=new Intent(DrugNote.this,JournalEditor.class);
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });

        if(adapter.isEmpty()){
            emptydisp.setVisibility(View.VISIBLE);
        }else{
            emptydisp.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        noteList=dbHelper.getAllNotes();
        adapter.clear();
        adapter=new Dapter(this, R.layout.list_items, R.id.notetitle,noteList);
        adapter.notifyDataSetChanged();
        if(adapter.isEmpty()){
            emptydisp.setVisibility(View.VISIBLE);
        }else{
            emptydisp.setVisibility(View.GONE);
        }
    }

    private class Dapter extends ArrayAdapter<String> {

        public Dapter(Context context, int resource, int textViewResources,
                      List list){
            //String[] object) {
            super(context, resource, textViewResources, list);
        }

        @Override
        public int getCount() {
            return noteList.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = inflater.inflate(R.layout.preslist_items, parent, false);
            List<NoteModel> items =noteList;
            ImageView iv = (ImageView) row.findViewById(R.id.noteImage);


//            TextView content = (TextView) row.findViewById(R.id.notetitle);
//            content.setSelected(true);
            TextView title=(TextView) row.findViewById(R.id.notetitle);
            TextView date=(TextView) row.findViewById(R.id.notedate);

            NoteModel md=items.get(position);
            // content.setText(md.getContent());
            title.setText(md.getTitle() != null?md.getTitle():" ");
            date.setText(md.getDate() != null?md.getDate():" ");
//            ImageSwitctery(iv, md.getTitle()!= null?md.getTitle():" ");

            return row;
        }


    }


}
