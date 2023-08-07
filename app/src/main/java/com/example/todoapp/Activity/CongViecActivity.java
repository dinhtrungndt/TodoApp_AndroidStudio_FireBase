package com.example.todoapp.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.Adapter.ChonListGcAdapter;
import com.example.todoapp.Adapter.CongViecAdapter;
import com.example.todoapp.Model.CongViec;
import com.example.todoapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CongViecActivity extends AppCompatActivity {
    private TextView txtCv, txtDelete;
    private RecyclerView rcvCv;
    private ArrayList<CongViec> list;
    private CongViecAdapter adapter;
    private FloatingActionButton clickList;
    private String rong;
    private LinearLayout lnBack;
    private SearchView searchView;
    private BottomNavigationView bottomNavigationView;
    DatabaseReference mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cong_viec);

        Anhxa();
        loadData();
        nhanData();
        onClick();
    }

    private void Anhxa() {
        txtCv = findViewById(R.id.txtCv);
        lnBack = findViewById(R.id.lnBack);
        rcvCv = findViewById(R.id.rcvCv);
        clickList = findViewById(R.id.fab);
        txtDelete = findViewById(R.id.txtDelete);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        searchView = findViewById(R.id.searchView);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                fileListSearch(newText);
                return true;
            }
        });
    }

    private void fileListSearch(String Text) {
        ArrayList<CongViec> fileList = new ArrayList<>();
        for (CongViec item : list) {
            if (item.getTen().toLowerCase().contains(Text.toLowerCase())) {
                fileList.add(item);
            }
        }

        if (fileList.isEmpty()) {
            Toast.makeText(this, "Không có tên này trong danh sách !", Toast.LENGTH_SHORT).show();
        } else {
            adapter.setFil(fileList);
        }
    }

    private void nhanData() {
        // Lay Cv
        Intent intent = getIntent();
        rong = intent.getStringExtra("name");
        txtCv.setText(rong);
    }

    private void loadData() {
        list = new ArrayList<>();
        adapter = new CongViecAdapter(this, list);
        rcvCv.setAdapter(adapter);
        rcvCv.setLayoutManager(new LinearLayoutManager(this));

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rcvCv.addItemDecoration(itemDecoration);
    }

    private void onClick() {

        clickList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        lnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CongViecActivity.this, GhiChuActivity.class));
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        if (item.getItemId() == R.id.home) {
                            startActivity(new Intent(CongViecActivity.this, GhiChuActivity.class));
                            Toast.makeText(CongViecActivity.this, "Trang chủ !!!", Toast.LENGTH_SHORT).show();
                            return true;
                        }
                        if (item.getItemId() == R.id.load) {
                            startActivity(new Intent(CongViecActivity.this, CongViecActivity.class));
                            finish();
                            Toast.makeText(CongViecActivity.this, "Load thành công !!!", Toast.LENGTH_SHORT).show();
                            return true;
                        }
                        if (item.getItemId() == R.id.person) {
                            startActivity(new Intent(CongViecActivity.this, ProfileActivity.class));
                            Toast.makeText(CongViecActivity.this, "Cập nhập thông tin !", Toast.LENGTH_SHORT).show();
                            return true;
                        }
                        if (item.getItemId() == R.id.logOut) {
                            Toast.makeText(CongViecActivity.this, "Bạn đã đăng xuất !!!", Toast.LENGTH_SHORT).show();
                            SharedPreferences sharedPreferences = getSharedPreferences("saveLogin", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.clear();
                            editor.commit();

                            Intent intent = new Intent(CongViecActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                            return true;
                        }
                        return false;
                    }
                });


    }

    private void showDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheet_congviec);

        LinearLayout upLayout = dialog.findViewById(R.id.lnUpload);
        LinearLayout editLayout = dialog.findViewById(R.id.lnEdit);
        LinearLayout diachiLayout = dialog.findViewById(R.id.lnDiaChi);
        LinearLayout shareLayout = dialog.findViewById(R.id.lnShare);

        upLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUp();
                dialog.dismiss();
            }
        });
        editLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChonlist();
                dialog.dismiss();
            }
        });
        diachiLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDiaChi();
                dialog.dismiss();
            }
        });
        shareLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showShare();
                Toast.makeText(CongViecActivity.this, "Đã share thành công trên mạng !", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void showChonlist() {
        LayoutInflater inflater = LayoutInflater.from(CongViecActivity.this);
        View dialogView = inflater.inflate(R.layout.dialog_chonlist_congviec, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(CongViecActivity.this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();

        Button btnHuy = dialog.findViewById(R.id.btnHuy);
        btnHuy.setOnClickListener(view -> {
            dialog.dismiss();
            Toast.makeText(CongViecActivity.this, "Bạn đã hủy chỉnh sửa !", Toast.LENGTH_SHORT).show();
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        mData = FirebaseDatabase.getInstance().getReference(uid);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> list = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String title = dataSnapshot.getKey();
                    list.add(title);
                }

                RecyclerView recyclerView = dialogView.findViewById(R.id.recyclerView);
                recyclerView.setLayoutManager(new LinearLayoutManager(CongViecActivity.this));
                ChonListGcAdapter adapter = new ChonListGcAdapter(list, new ChonListGcAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(String title) {
                        dialog.dismiss();
                        showEditDialog(title);
                    }
                });
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("showChonlist", "DatabaseError: " + error.getMessage());
            }
        });
    }



    private void showEditDialog(String title) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_edit_congviec);

        EditText edtUp = dialog.findViewById(R.id.edtCvUp);
        CheckBox cbCheck = dialog.findViewById(R.id.cbCheck);
        Button btnHuyUp = dialog.findViewById(R.id.btnHuyUp);
        Button btnAddUp = dialog.findViewById(R.id.btnAddUp);
        edtUp.setText(title);

        btnHuyUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Toast.makeText(CongViecActivity.this, "Bạn đã hủy chỉnh sửa !", Toast.LENGTH_SHORT).show();
            }
        });

        btnAddUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newTitle = edtUp.getText().toString().trim();
                boolean isChecked = cbCheck.isChecked();

                if (!newTitle.isEmpty()) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = user.getUid();
                    mData = FirebaseDatabase.getInstance().getReference(uid);

                    // Remove the old item and add the new one to Firebase Realtime Database
                    mData.child(title).removeValue();
                    mData.child(newTitle).setValue(isChecked ? "1" : "0").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(CongViecActivity.this, "Bạn đã sửa thành công !", Toast.LENGTH_SHORT).show();
                                loadDataEdit();
                            } else {
                                Toast.makeText(CongViecActivity.this, "Lỗi sửa !", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    dialog.dismiss();
                } else {
                    Toast.makeText(CongViecActivity.this, "Bạn chưa nhập tên ghi chú mới !", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }

    private void loadDataEdit() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        DatabaseReference mData = FirebaseDatabase.getInstance().getReference(uid);

        mData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String ten = dataSnapshot.getKey();
                    list.add(new CongViec(ten, false));
                }
                adapter.setFil(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Error", error.toString());
            }
        });
    }

    private void showShare() {
        final Dialog dialog = new Dialog(CongViecActivity.this);
        dialog.setContentView(R.layout.dialog_share);

        WebView gifWebView = dialog.findViewById(R.id.gifWebView);
        gifWebView.getSettings().setLoadWithOverviewMode(true);
        gifWebView.getSettings().setUseWideViewPort(true);
        gifWebView.loadUrl("https://www.google.com/url?sa=i&url=https%3A%2F%2Fthtantai2.edu.vn%2Fanh-gif-cute%2F&psig=AOvVaw3FmkKWxZiUyNaiC_ON3dvM&ust=1681066453368000&source=images&cd=vfe&ved=0CBEQjRxqFwoTCJCN49j6mv4CFQAAAAAdAAAAABAO");

        dialog.show();
    }

    private void showDiaChi() {
        LayoutInflater inflater = LayoutInflater.from(CongViecActivity.this);
        View dialogView = inflater.inflate(R.layout.dialog_diachi, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(CongViecActivity.this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showUp() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_congviec);

        EditText edtCvUp = dialog.findViewById(R.id.edtCvUp);
        CheckBox cbCheck = dialog.findViewById(R.id.cbCheck);
        Button btnAddUp = dialog.findViewById(R.id.btnAddUp);
        Button btnHuyUp = dialog.findViewById(R.id.btnHuyUp);

        btnAddUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = edtCvUp.getText().toString().trim();
                Boolean check = cbCheck.isChecked();
                CongViec cv = new CongViec(name, check);
                mData.child(rong).push().setValue(cv);

                Toast.makeText(CongViecActivity.this, "Thêm thành công !", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        btnHuyUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Toast.makeText(CongViecActivity.this, "Bạn đã hủy thêm", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            mData = FirebaseDatabase.getInstance().getReference(uid);
            mData.child(rong).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    CongViec cv = snapshot.getValue(CongViec.class);
                    list.add(cv);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

}