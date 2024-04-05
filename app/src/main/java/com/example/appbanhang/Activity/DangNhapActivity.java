package com.example.appbanhang.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DangNhapActivity extends AppCompatActivity {
    TextView dangKi,resetpass;
    EditText edt_email_login,edt_pass_login;
    AppCompatButton button;
    ApiBanHang apiBanHang;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    private boolean isLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_nhap);
        initView();
        initControl();
    }

    private void initControl() {
        dangKi.setOnClickListener(task->{
            Intent i = new Intent(DangNhapActivity.this,DangKiActivity.class);
            startActivity(i);
        });
        resetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),ResetPassActivity.class);
                startActivity(i);
            }
        });

        button.setOnClickListener(task1->{
            String email = edt_email_login.getText().toString().trim();
            String pass = edt_pass_login.getText().toString().trim();
            loginServer(email,pass);
            loginFirebase(email,pass);
        });

        resetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent reset = new Intent(getApplicationContext(), ResetPassActivity.class);
                startActivity(reset);
            }
        });
    }

    private void loginServer(String email, String pass) {
        if (TextUtils.isEmpty(email)){
            Toast.makeText(getApplicationContext(), "Bạn chưa nhập email", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(pass)){
            Toast.makeText(getApplicationContext(), "Bạn chưa nhập mật khẩu", Toast.LENGTH_SHORT).show();
        }else {
            Paper.book().write("email", email);
            Paper.book().write("pass", pass);
            compositeDisposable.add(apiBanHang.dangNhap(email,pass)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            userModel -> {
                                if (userModel.isSuccess() == true){
                                    isLogin = true;
                                    Paper.book().write("isLogin",isLogin);
                                    Utils.currentUser = userModel.getList().get(0);
                                    Paper.book().write("user",userModel.getList().get(0));
                                    Intent intent= new Intent(getApplicationContext(),MainActivity.class);
                                    startActivity(intent);
                                }
                            },
                            throwable -> {
                                Log.d("tetsadasst", throwable.getMessage() );}
                    ));
        }
    }

    private void loginFirebase(String email, String pass){

        if (TextUtils.isEmpty(email)){
            Toast.makeText(getApplicationContext(), "Bạn chưa nhập email", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(pass)){
            Toast.makeText(getApplicationContext(), "Bạn chưa nhập mật khẩu", Toast.LENGTH_SHORT).show();
        }else {
            firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(DangNhapActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                        Paper.book().write("email", email);
                        Paper.book().write("pass", pass);
                    }
                }
            });
        }
    }

    private void login(String email, String pass){
        firebaseAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(DangNhapActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();


                }
            }
        });
    }

    private void initView() {
        Paper.init(this);
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        dangKi = findViewById(R.id.lnkRegister);
        edt_email_login = findViewById(R.id.edtloginEmail);
        edt_pass_login = findViewById(R.id.edtloginPwd);
        button = findViewById(R.id.btnlogin);
        resetpass = findViewById(R.id.login_repass);

        if (Paper.book().read("email") != null && Paper.book().read("pass")!= null){
            edt_email_login.setText(Paper.book().read("email"));
            edt_pass_login.setText(Paper.book().read("pass"));
            if(Paper.book().read("isLogin")!=null){
                boolean flag = Paper.book().read("isLogin");
                if(flag){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
//                            Paper.book().write("isLogin",false);
//                            loginServer(Paper.book().read("email"),Paper.book().read("pass"));
                        }
                    },1000);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Utils.currentUser.getEmail() !=null && Utils.currentUser.getPass() !=null){
            edt_email_login.setText(Utils.currentUser.getEmail());
            edt_pass_login.setText(Utils.currentUser.getPass());
        }
    }
}