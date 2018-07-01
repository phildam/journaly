package com.squadron.philip.journaly;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squadron.philip.journaly.database.entity.JournalEntity;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by philip on 30/06/2018.
 */

public class JournalAdapter extends RecyclerView.Adapter<JournalAdapter.JournalViewHolder> {

    final private ListItemClickListener mOnClickListener;
    private List<JournalEntity> journalList;

    public interface ListItemClickListener {
        public void onItemClickListener(JournalEntity journalEntity);
    }

    class JournalViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView journalDate;
        public TextView journalTime;
        public TextView journalLocation;
        public TextView journalUpdateTime;
        public TextView journalText;
        public ImageView journalMainPhoto;
        public JournalViewHolder(ConstraintLayout itemView) {
            super(itemView);
            journalDate = (TextView) itemView.findViewById(R.id.journal_date);
            journalTime = (TextView) itemView.findViewById(R.id.journal_time);
            journalLocation = (TextView) itemView.findViewById(R.id.journal_location);
            journalUpdateTime = (TextView) itemView.findViewById(R.id.journal_update_time);
            journalText = (TextView) itemView.findViewById(R.id.journal_text);
            journalMainPhoto = (ImageView) itemView.findViewById(R.id.journal_Image);

            itemView.setOnClickListener(this);
        }

        private String dateFormatter(Date date){
            SimpleDateFormat formattedDate = new SimpleDateFormat("dd/MM/yyyy");
            return formattedDate.format(date);
        }


        public void bind(JournalEntity journal){

            String journalContent = journal.getContent().trim().replace('\n', ' ');
            int randomImageSelector = Processor.imageSelector(
                    new Random().nextInt((20 - 0) + 1)
            );

            journalDate.setText(new Processor().getDayOfWeek(journal.getDateAdded()));
            journalTime.setText(journal.getTime() );
            journalLocation.setText(journal.getLocation());
            journalUpdateTime.setText(dateFormatter(journal.getLastModifiedDate()));
            journalText.setText(journalContent.length() > 200 ?
                journalContent.substring(0, 160)+"..." : journalContent );


            try {
                File file = new File(new URI(journal.getImageUrl()));
                Log.d("FILE", file.getAbsolutePath());
                if(file.exists()){
                    journalMainPhoto.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
                } else {
                    throw new FileNotFoundException();
                }

                journalMainPhoto.setImageResource(randomImageSelector);
            } catch (Exception e){
                journalMainPhoto.setImageResource(randomImageSelector);
            }

        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onItemClickListener(journalList.get(clickedPosition));
        }
    }

    public JournalAdapter(List<JournalEntity> journals, ListItemClickListener listItemClickListener ){
        mOnClickListener = listItemClickListener;
        journalList = journals;
    }

    public List<JournalEntity> getJournals(){
        return journalList;
    }

    public void setJournals(List<JournalEntity> journals) {
        this.journalList = journals;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public JournalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.journal;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        ConstraintLayout view = (ConstraintLayout)inflater.inflate(layoutIdForListItem,
                parent, shouldAttachToParentImmediately);

        JournalViewHolder viewHolder = new JournalViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull JournalViewHolder holder, int position) {
        holder.bind(journalList.get(position));
    }

    @Override
    public int getItemCount() {
        return journalList.size();
    }

}
