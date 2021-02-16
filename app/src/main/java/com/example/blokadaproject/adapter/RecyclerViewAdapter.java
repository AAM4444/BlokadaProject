package com.example.blokadaproject.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blokadaproject.R;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter <RecyclerViewAdapter.ViewHolder> {


    private ArrayList<String> AppName;
    private ArrayList<String> HashSum;
    private Context mContext;

    public RecyclerViewAdapter(ArrayList<String> AppName, ArrayList<String> HashSum, Context mContext) {
        this.AppName = AppName;
        this.HashSum = HashSum;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_app_info, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

//        Log.d("TAG", " " + position);
        holder.appName.setText("App = " + AppName.get(position));
        holder.appHash.setText("Hash = " + HashSum.get(position));
    }

    @Override
    public int getItemCount() {
        return AppName.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView appName;
        private TextView appHash;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            appName = itemView.findViewById(R.id.tv_app);
            appHash = itemView.findViewById(R.id.tv_hash);
        }
    }

}
