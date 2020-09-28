package com.example.pronunciationapp;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SoundAdapter extends RecyclerView.Adapter<SoundAdapter.SoundViewHolder> {

    String [] pinyin = {"a", "an", "ang", "hang", "zhou", "yi", "er", "san", "si", "wu", "liu", "qi", "ba", "jiu", "shi", "frefr", "yi", "er", "san", "si", "wu", "liu", "qi", "ba", "jiu", "shi", "frefr"};


    public static class SoundViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView textView;
        public SoundViewHolder(TextView v) {
            super(v);
            textView = v;
        }
    }
    @NonNull
    @Override
    public SoundViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        System.out.println(parent);
        TextView v = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sound_view, parent, false);
        SoundViewHolder vh = new SoundViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull SoundViewHolder holder, int position) {
        holder.textView.setText(pinyin[position]);
    }


    @Override
    public int getItemCount() {
        return pinyin.length;
    }
}
