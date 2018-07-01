package com.squadron.philip.journaly;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by philip on 30/06/2018.
 */

public class JournalAdapter extends RecyclerView.Adapter<JournalAdapter.JournalViewHolder> {

    final private ListItemClickListener mOnClickListener;
    final private String[] listItem;

    public interface ListItemClickListener {
        public void onItemClickListener(int position);
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

        public void bind(int position){
            Log.e("pos", String.valueOf(position));
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onItemClickListener(clickedPosition);
        }
    }

    public JournalAdapter(String[] items, ListItemClickListener listItemClickListener ){
        mOnClickListener = listItemClickListener;
        listItem = items;
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

        //viewHolder.viewHolderIndex.setText("ViewHolder index: " + viewHolderCount);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull JournalViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return listItem.length;
    }

}
