package com.example.appbanhang.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appbanhang.R;
import com.example.appbanhang.Retrofit.ApiBanHang;
import com.example.appbanhang.Retrofit.RetrofitClient;
import com.example.appbanhang.Utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.greenrobot.eventbus.ThreadMode;
import org.greenrobot.eventbus.android.AndroidLogger;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DangKiActivity extends AppCompatActivity {
    EditText userName, userEmail, userPass,userSdt, userRepass;
    ImageView seeView;
    AppCompatButton dangKi;
    TextView dangNhap;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    String email,pass;
    ApiBanHang apiBanHang;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_ki);
        initView();
        dangKi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dangKiwithServer();
                dangKiwithFirebase();
            }
        });

        dangNhap.setOnClickListener(task ->{
           Intent i = new Intent(DangKiActivity.this,DangNhapActivity.class);
           startActivity(i);
        });
        seeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userPass.setInputType(InputType.TYPE_CLASS_TEXT);
            }
        });
    }

    private void dangKiwithServer() {
            String str_email = userEmail.getText().toString().trim();
            String str_pass = userPass.getText().toString().trim();
            String str_repass = userRepass.getText().toString().trim();
            String str_mobile = userSdt.getText().toString().trim();
            String str_usrename= userName.getText().toString().trim();

            if (TextUtils.isEmpty(str_email)){
                Toast.makeText(getApplicationContext(), "Bạn chưa nhập email", Toast.LENGTH_SHORT).show();
            }
            else if (TextUtils.isEmpty(str_pass)){
                Toast.makeText(getApplicationContext(), "Bạn chưa nhập mật khẩu", Toast.LENGTH_SHORT).show();
            }else if (TextUtils.isEmpty(str_repass)){
                Toast.makeText(getApplicationContext(), "Bạn chưa nhập Repass", Toast.LENGTH_SHORT).show();
            }else if (TextUtils.isEmpty(str_mobile)){
                Toast.makeText(getApplicationContext(), "Bạn chưa nhập số điện thoại", Toast.LENGTH_SHORT).show();
            }else if (TextUtils.isEmpty(str_usrename)){
                Toast.makeText(getApplicationContext(), "Bạn chưa nhập tên", Toast.LENGTH_SHORT).show();
            }
            else {
                if (str_pass.equals(str_repass)){
                    compositeDisposable.add(apiBanHang.dangky(str_email,str_pass,str_repass,str_mobile)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    userModel -> {
                                        if (userModel.isSuccess()){
                                            Utils.currentUser.setEmail(str_email);
                                            Utils.currentUser.setPass(str_pass);
                                            Intent intent = new Intent(getApplicationContext(),DangNhapActivity.class);
                                            startActivity(intent);

                                            finish();
                                        }else {
                                            Toast.makeText(getApplicationContext(), userModel.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    },
                                    throwable -> {
                                        Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                            ));
                }else {
                    Toast.makeText(this, "Mật khẩu không trùng khớp", Toast.LENGTH_SHORT).show();
                }
            }

        }

    private void dangKiwithFirebase() {
        email = userEmail.getText().toString().trim();
        pass = userPass.getText().toString().trim();
        firebaseAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(task -> {
           if(task.isSuccessful()){
               Toast.makeText(this, "Thành công", Toast.LENGTH_SHORT).show();
               Intent i = new Intent(DangKiActivity.this,DangNhapActivity.class);
               startActivity(i);
           }else{
               Log.d("SADASDAS", task.getException().toString());
           }
        });
    }

    private void initView() {
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);

        userEmail = findViewById(R.id.edt_email_resigter);
        userName = findViewById(R.id.edt_name_resigter);
        userPass = findViewById(R.id.edt_pass_register);
        seeView  = findViewById(R.id.seePass_register);
        userSdt = findViewById(R.id.edt_sdt_resigter);
        userRepass = findViewById(R.id.edt_pass2_register);
        dangKi = findViewById(R.id.btndk);
        dangNhap = findViewById(R.id.txt_login_resgiter);
    }
}