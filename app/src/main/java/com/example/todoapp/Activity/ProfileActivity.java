package com.example.todoapp.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.todoapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {
    private Button btnUpdateE, btnUpdateP;
    private EditText edtEmail, edtPass;
    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton fab;
    //    DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Anhxa();
        onClick();

    }

    private void Anhxa() {
        edtEmail = findViewById(R.id.edtEmail);
        edtPass = findViewById(R.id.edtPass);
        btnUpdateE = findViewById(R.id.btnUpdateE);
        btnUpdateP = findViewById(R.id.btnUpdateP);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        fab = findViewById(R.id.fab);
        
    }

    private void onClick() {
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        if (item.getItemId() == R.id.home) {
                            startActivity(new Intent(ProfileActivity.this, GhiChuActivity.class));
                            Toast.makeText(ProfileActivity.this, "Trang chủ !!!", Toast.LENGTH_SHORT).show();
                            return true;
                        }
                        if (item.getItemId() == R.id.load) {
                            Toast.makeText(ProfileActivity.this, "Không có danh sách để Load !!!", Toast.LENGTH_SHORT).show();
                            return true;
                        }
                        if (item.getItemId() == R.id.person) {
                            Toast.makeText(ProfileActivity.this, "Bạn đang ở profile !!!", Toast.LENGTH_SHORT).show();
                            return true;
                        }
                        if (item.getItemId() == R.id.logOut) {
                            Toast.makeText(ProfileActivity.this, "Bạn đã đăng xuất !!!", Toast.LENGTH_SHORT).show();
                            SharedPreferences sharedPreferences = getSharedPreferences("saveLogin", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.clear();
                            editor.commit();

                            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                            return true;
                        }
                        return false;
                    }
                });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        btnUpdateE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newEmail = edtEmail.getText().toString().trim();
                user.updateEmail(newEmail)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ProfileActivity.this, "Đổi Email thành công !", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        btnUpdateP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newPass = edtPass.getText().toString().trim();
                user.updatePassword(newPass)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ProfileActivity.this, "Đổi Password thành công !", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ProfileActivity.this, "Vui lòng về trang chủ để thêm !", Toast.LENGTH_SHORT).show();
            }
        });
        
    }

//                String newEmail = edtEmail.getText().toString().trim();
//                String oldPass = edtPassOld.getText().toString().trim();
//                String newPass = edtPassNew.getText().toString().trim();
//
//                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//
//                user.updateEmail(newEmail)
//                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if (task.isSuccessful()) {
//                                    // Thành công
//                                    Toast.makeText(getApplicationContext(), "Đổi Email thành công !", Toast.LENGTH_SHORT).show();
//
//                                    // Xác thực lại tài khoản người dùng
//                                    FirebaseAuth.getInstance().signInWithEmailAndPassword(newEmail, newPass)
//                                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                                                @Override
//                                                public void onComplete(@NonNull Task<AuthResult> task) {
//                                                    if (task.isSuccessful()) {
//                                                        // Xác thực thành công
//                                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                                                        user.updatePassword(newPass)
//                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                                    @Override
//                                                                    public void onComplete(@NonNull Task<Void> task) {
//                                                                        if (task.isSuccessful()) {
//                                                                            // Thành công
//                                                                            Toast.makeText(getApplicationContext(), "Đổi mật khẩu thành công !", Toast.LENGTH_SHORT).show();
//                                                                        } else {
//                                                                            // Lỗi xảy ra
//                                                                            Toast.makeText(getApplicationContext(), "Đổi mật khẩu thất bại !!!.", Toast.LENGTH_SHORT).show();
//                                                                            Log.e("ProfileActivity", "Lỗi đổi mật khẩu: " + task.getException().getMessage());
//                                                                        }
//                                                                    }
//                                                                });
//                                                    } else {
//
//                                                        // Lỗi xảy ra
//                                                        Toast.makeText(getApplicationContext(), "Xác thực tài khoản thất bại !!!.", Toast.LENGTH_SHORT).show();
//                                                        Log.e("ProfileActivity", "Lỗi xác thực tài khoản: " + task.getException().getMessage());
//                                                    }
//                                                }
//                                            });
//
//                                } else {
//                                    // Lỗi xảy ra
//                                    Toast.makeText(getApplicationContext(), "Đổi Email thất bại !!!.", Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });
//            }
//        });
//
//    }


}