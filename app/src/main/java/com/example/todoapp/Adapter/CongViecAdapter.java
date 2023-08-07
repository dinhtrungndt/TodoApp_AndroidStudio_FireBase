package com.example.todoapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.Model.CongViec;
import com.example.todoapp.R;

import java.util.ArrayList;

public class CongViecAdapter extends RecyclerView.Adapter<CongViecAdapter.ViewHolder> {
    Context context;
    ArrayList<CongViec> list;

    public CongViecAdapter(Context context, ArrayList<CongViec> list) {
        this.context = context;
        this.list = list;
    }

    public void setFil(ArrayList<CongViec> fillll) {
        this.list = fillll;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_congviec, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String ghiChuTen = list.get(position).getTen();
        holder.txtCv.setText(list.get(position).getTen());
        holder.rdoCheck.setChecked(list.get(position).isCheck());
        holder.rdoCheck.setEnabled(true);

        holder.rdoCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        holder.txtDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtCv, txtDelete;
        CheckBox rdoCheck;
        LinearLayout rcvCv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCv = itemView.findViewById(R.id.txtCv);
            txtDelete = itemView.findViewById(R.id.txtDelete);
            rdoCheck = itemView.findViewById(R.id.rdoCheck);
            rcvCv = itemView.findViewById(R.id.rcvCv);

        }
    }
}
