package com.example.todoapp.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.Adapter.ChonListGcAdapter;
import com.example.todoapp.Adapter.GhiChuAdapter;
import com.example.todoapp.Model.GhiChu;
import com.example.todoapp.R;
import com.example.todoapp.Touch.ItemTouch;
import com.example.todoapp.Touch.RecyclerTouch;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GhiChuActivity extends AppCompatActivity implements ItemTouch {

    private RecyclerView rcvListGhiChu;
    private FloatingActionButton clickList;
    private ArrayList<GhiChu> list;
    private GhiChuAdapter adapter;
    private DatabaseReference mData;
    private Switch aSwitch;
    private boolean nightMODE;
    private SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private CoordinatorLayout actiGhiChu;
    private SearchView searchView;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ghi_chu);

        switchLayout();
        Anhxa();
        loadData();
        onClick();
        onClickCv();
        ItemDelete();
    }

    private void switchLayout() {
        aSwitch = findViewById(R.id.switcher);

        sharedPreferences = getSharedPreferences("MODE", MODE_PRIVATE);
        nightMODE = sharedPreferences.getBoolean("night", false);

        if (nightMODE) {
            aSwitch.setChecked(true);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        aSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nightMODE) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    editor = sharedPreferences.edit();
                    editor.putBoolean("night", false);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    editor = sharedPreferences.edit();
                    editor.putBoolean("night", true);
                }
                editor.apply();
                finish();
            }
        });
    }

    private void Anhxa() {
        rcvListGhiChu = findViewById(R.id.rcvListGhiChu);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        clickList = findViewById(R.id.fab);
        actiGhiChu = findViewById(R.id.actiGhiChu);
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
        ArrayList<GhiChu> fileList = new ArrayList<>();
        for (GhiChu item : list) {
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

    private void loadData() {
        list = new ArrayList<>();
        adapter = new GhiChuAdapter(this, list);
        rcvListGhiChu.setAdapter(adapter);
        rcvListGhiChu.setLayoutManager(new LinearLayoutManager(this));

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rcvListGhiChu.addItemDecoration(itemDecoration);
    }

    public void ItemDelete() {
        ItemTouchHelper.SimpleCallback simpleCallback = new RecyclerTouch(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(rcvListGhiChu);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof GhiChuAdapter.ViewHolder) {
            GhiChu ghiChuDelete = list.get(viewHolder.getAdapterPosition());
            int indexDelete = viewHolder.getAdapterPosition();

            // removeItem
            adapter.removeItem(indexDelete);

            // undo
            Snackbar snackbar = Snackbar.make(actiGhiChu, ghiChuDelete + "removed", Snackbar.LENGTH_LONG);
            snackbar.setAction("Hoàn tác", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    adapter.undoItem(ghiChuDelete, indexDelete);
                    if (indexDelete == 0 || indexDelete == list.size() - 1) {
                        rcvListGhiChu.scrollToPosition(indexDelete);
                    }
                }
            });
            snackbar.setText("Bạn đã xóa thành công");
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String uid = user.getUid();
            mData = FirebaseDatabase.getInstance().getReference(uid);
            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference(uid);
            databaseRef.child(ghiChuDelete.getTen()).removeValue();
        }
    }

    private void onClick() {
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        if (item.getItemId() == R.id.home) {
                            Toast.makeText(GhiChuActivity.this, "Bạn đang ở Trang Chủ !!!", Toast.LENGTH_SHORT).show();
                            return true;
                        }
                        if (item.getItemId() == R.id.load) {
                            startActivity(new Intent(GhiChuActivity.this, GhiChuActivity.class));
                            Toast.makeText(GhiChuActivity.this, "Load thành công !!!", Toast.LENGTH_SHORT).show();
                            return true;
                        }
                        if (item.getItemId() == R.id.person) {
                            startActivity(new Intent(GhiChuActivity.this, ProfileActivity.class));
                            Toast.makeText(GhiChuActivity.this, "Cập nhập thông tin !", Toast.LENGTH_SHORT).show();
                            return true;
                        }
                        if (item.getItemId() == R.id.logOut) {
                            Toast.makeText(GhiChuActivity.this, "Bạn đã đăng xuất !!!", Toast.LENGTH_SHORT).show();
                            SharedPreferences sharedPreferences = getSharedPreferences("saveLogin", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.clear();
                            editor.commit();

                            Intent intent = new Intent(GhiChuActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                            return true;
                        }

                        return false;
                    }
                });
        clickList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
    }

    private void showDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheet_ghichu);

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
                Toast.makeText(GhiChuActivity.this, "Đã share thành công trên mạng !", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void showChonlist() {
        LayoutInflater inflater = LayoutInflater.from(GhiChuActivity.this);
        View dialogView = inflater.inflate(R.layout.dialog_chonlist_ghichu, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(GhiChuActivity.this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();
        Button btnHuy = dialog.findViewById(R.id.btnHuy);
        btnHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Toast.makeText(GhiChuActivity.this, "Bạn đã hủy chỉnh sửa !", Toast.LENGTH_SHORT).show();
            }
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
                recyclerView.setLayoutManager(new LinearLayoutManager(GhiChuActivity.this));
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
        dialog.setContentView(R.layout.dialog_ghichu);

        EditText edtUp = dialog.findViewById(R.id.edtUp);
        Button btnHuyUp = dialog.findViewById(R.id.btnHuyUp);
        Button btnAddUp = dialog.findViewById(R.id.btnAddUp);
        edtUp.setText(title);
        btnHuyUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Toast.makeText(GhiChuActivity.this, "Bạn đã hủy chỉnh sửa !", Toast.LENGTH_SHORT).show();
            }
        });
        btnAddUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newTitle = edtUp.getText().toString().trim();
                if (!newTitle.isEmpty()) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = user.getUid();
                    mData = FirebaseDatabase.getInstance().getReference(uid);
                    mData.child(title).removeValue();
                    mData.child(newTitle).setValue("0").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(GhiChuActivity.this, "Bạn đã sửa thành công !", Toast.LENGTH_SHORT).show();
                                loadDataEdit();
                            } else {
                                Toast.makeText(GhiChuActivity.this, "Lỗi sửa !", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    dialog.dismiss();
                } else {
                    Toast.makeText(GhiChuActivity.this, "Bạn chưa nhập tên ghi chú mới !", Toast.LENGTH_SHORT).show();
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
                    list.add(new GhiChu(ten));
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
        final Dialog dialog = new Dialog(GhiChuActivity.this);
        dialog.setContentView(R.layout.dialog_share);

        WebView gifWebView = dialog.findViewById(R.id.gifWebView);
        gifWebView.getSettings().setLoadWithOverviewMode(true);
        gifWebView.getSettings().setUseWideViewPort(true);
        gifWebView.loadUrl("https://www.google.com/url?sa=i&url=https%3A%2F%2Fthtantai2.edu.vn%2Fanh-gif-cute%2F&psig=AOvVaw3FmkKWxZiUyNaiC_ON3dvM&ust=1681066453368000&source=images&cd=vfe&ved=0CBEQjRxqFwoTCJCN49j6mv4CFQAAAAAdAAAAABAO");

        dialog.show();
    }

    private void showDiaChi() {
        LayoutInflater inflater = LayoutInflater.from(GhiChuActivity.this);
        View dialogView = inflater.inflate(R.layout.dialog_diachi, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(GhiChuActivity.this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showUp() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_ghichu);

        EditText edtUp = dialog.findViewById(R.id.edtUp);
        Button btnAddUp = dialog.findViewById(R.id.btnAddUp);
        Button btnHuyUp = dialog.findViewById(R.id.btnHuyUp);

        btnAddUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ten = edtUp.getText().toString().trim();
                mData.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String CHANNEL_ID = "my_channel_id";
                        int notificationId = 1;
                        NotificationChannel channel = null;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            channel = new NotificationChannel(CHANNEL_ID, "My Channel", NotificationManager.IMPORTANCE_DEFAULT);
                        }
                        NotificationManager manager = getSystemService(NotificationManager.class);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            manager.createNotificationChannel(channel);
                        }

                        if (dataSnapshot.hasChild(ten)) {
                            // Tên ghi chú đã tồn tại
                            Toast.makeText(GhiChuActivity.this, "Tên ghi chú đã tồn tại!", Toast.LENGTH_SHORT).show();
                        } else {
                            // Tên ghi chú chưa tồn tại, thêm vào Firebase Database
                            mData.child(ten).setValue(123465);
                            Toast.makeText(GhiChuActivity.this, "Thêm thành công !", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            // Tạo đối tượng NotificationCompat.Builder
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(GhiChuActivity.this, CHANNEL_ID)
                                    .setSmallIcon(R.drawable.logo_todo)
                                    .setContentTitle(ten)
                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                            String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                            NotificationCompat.Builder builderTime = new NotificationCompat.Builder(GhiChuActivity.this, CHANNEL_ID)
                                    .setSmallIcon(R.drawable.logo_todo)
                                    .setContentTitle(ten)
                                    .setContentText("Thời gian hiện tại: " + currentTime)
                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

// Gửi thông báo đến NotificationManager
                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(GhiChuActivity.this);
                            notificationManager.notify(notificationId, builderTime.build());


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Xử lý lỗi nếu có
                    }
                });

            }
        });

        btnHuyUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Toast.makeText(GhiChuActivity.this, "Bạn đã hủy thêm", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    public void onClickCv() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            mData = FirebaseDatabase.getInstance().getReference(uid);
            mData.addChildEventListener(new ChildEventListener() {

                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    String value = snapshot.getKey();
                    list.add(new GhiChu(value));
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bottom_nav_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                // Show a dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Settings");
                builder.setMessage("This is the settings page");
                builder.setPositiveButton("OK", null);
                builder.show();
                return true;
            case R.id.load:

                return true;
            case R.id.person:

                return true;
            case R.id.logOut:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}