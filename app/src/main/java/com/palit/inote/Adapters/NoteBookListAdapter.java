package com.palit.inote.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.palit.inote.R;

public class NoteBookListAdapter extends RecyclerView.Adapter<NoteBookListAdapter.MyViewHolder> {

    private String[] mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView noteBookName, pageNo;
        public MyViewHolder(View view) {
            super(view);
           noteBookName = view.findViewById(R.id.notebook_name);
           pageNo = view.findViewById(R.id.no_of_pages);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public NoteBookListAdapter(String[] myDataset) {
        mDataset = myDataset;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_notebooks, viewGroup, false);

        return new MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull NoteBookListAdapter.MyViewHolder myViewHolder, int i) {


    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
