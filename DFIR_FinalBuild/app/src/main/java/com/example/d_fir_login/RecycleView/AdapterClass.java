package com.example.d_fir_login.RecycleView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.d_fir_login.Model.Case;
import com.example.d_fir_login.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterClass extends RecyclerView.Adapter<AdapterClass.myViewHolder>{

    ArrayList<Case> list;
    public AdapterClass(ArrayList<Case> list){
        this.list = list;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_holder, parent, false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {

        holder.txtViewCaseId.setText(list.get(position).getCaseId());
        holder.txtViewCaseDescription.setText(list.get(position).getOfficerName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class myViewHolder extends RecyclerView.ViewHolder{

        TextView txtViewCaseId, txtViewCaseDescription;


        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            txtViewCaseId = itemView.findViewById(R.id.txtViewCaseId);
            txtViewCaseDescription = itemView.findViewById(R.id.txtViewCaseDescription);

        }
    }
}
