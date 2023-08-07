package com.example.todoapp.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.Activity.CongViecActivity;
import com.example.todoapp.Model.GhiChu;
import com.example.todoapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class GhiChuAdapter extends RecyclerView.Adapter<GhiChuAdapter.ViewHolder> {
    Context context;
    ArrayList<GhiChu> list;

    public GhiChuAdapter(Context context, ArrayList<GhiChu> list) {
        this.context = context;
        this.list = list;
    }


    public void setFil(ArrayList<GhiChu> fillll) {
        this.list = fillll;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ghichu, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String ghiChuTen = list.get(position).getTen();
        holder.txtGhiChuItem.setText(list.get(position).getTen());

        holder.lnGhiChu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CongViecActivity.class);
                intent.putExtra("name", holder.txtGhiChuItem.getText().toString().trim());
                context.startActivity(intent);
            }
        });

        holder.txtDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context)
                        .setTitle("Delete")
                        .setMessage("Bạn có chắc muốn xóa ghi chú này không ?")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Lấy reference đến phần tử cần xóa
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                String uid = user.getUid();
                                DatabaseReference mData = FirebaseDatabase.getInstance().getReference(uid).child(ghiChuTen);
                                mData.removeValue()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // Xóa thành công
                                                Toast.makeText(context, "Xóa thành công !", Toast.LENGTH_SHORT).show();
                                                list.remove(position);
                                                setFil(list);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Xóa thất bại
                                                Toast.makeText(context, "Xóa thất bại !", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            }
                        }).setNegativeButton("Hủy", null)
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtGhiChuItem, txtDelete;
        public LinearLayout lnGhiChu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDelete = itemView.findViewById(R.id.txtDelete);
            txtGhiChuItem = itemView.findViewById(R.id.txtGhiChuItem);
            lnGhiChu = itemView.findViewById(R.id.lnGhiChu);

        }
    }

    public void removeItem(int index) {
        list.remove(index);
        notifyItemRemoved(index);
    }

    public void undoItem(GhiChu ghiChu, int index) {
        list.add(index, ghiChu);
        notifyItemInserted(index);
    }
}
