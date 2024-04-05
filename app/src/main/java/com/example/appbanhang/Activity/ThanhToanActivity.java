package com.example.appbanhang.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import com.example.appbanhang.Model.CreateOrder;
import com.example.appbanhang.R;
import com.example.appbanhang.Retrofit.ApiBanHang;
import com.example.appbanhang.Retrofit.RetrofitClient;
import com.example.appbanhang.Utils.Utils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import vn.momo.momo_partner.AppMoMoLib;
import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

public class ThanhToanActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextView tongTien,email,sdt;
    EditText diaChi;
    AppCompatButton bt_datHang,btnmommo,btnzalopay;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiBanHang apiBanHang;
    long sum;
    int total;
    int iddonhang;
    private String amount = "10000";
    private String fee = "0";
    int environment = 0;
    private String merchantName = "Thanh toán đơn hàng";
    private String merchantCode = "SCB01";
    private String merchantNameLabel = "Nhà cung cấp";
    private String description = "Mua hàng online";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thanh_toan);
        AppMoMoLib.getInstance().setEnvironment(AppMoMoLib.ENVIRONMENT.DEVELOPMENT);
        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // ZaloPay SDK Init
        ZaloPaySDK.init(2553, Environment.SANDBOX);
        initView();
        initControl();
        countItem();
    }

    private void countItem() {
        total = 0;
        for(int i=0;i<Utils.mangMuahang.size();i++){
            total += Utils.mangMuahang.get(i).getSl();
        }
    }

    //Get token through MoMo app
    private void requestPayment(String iddonhang) {
        AppMoMoLib.getInstance().setAction(AppMoMoLib.ACTION.PAYMENT);
        AppMoMoLib.getInstance().setActionType(AppMoMoLib.ACTION_TYPE.GET_TOKEN);

        Map<String, Object> eventValue = new HashMap<>();
        eventValue.put("merchantname", merchantName); //Tên đối tác. được đăng ký tại https://business.momo.vn. VD: Google, Apple, Tiki , CGV Cinemas
        eventValue.put("merchantcode", merchantCode); //Mã đối tác, được cung cấp bởi MoMo tại https://business.momo.vn
        eventValue.put("amount", amount); //Kiểu integer
        eventValue.put("orderId", iddonhang); //uniqueue id cho Bill order, giá trị duy nhất cho mỗi đơn hàng
        eventValue.put("orderLabel", iddonhang); //gán nhãn

        //client Optional - bill info
        eventValue.put("merchantnamelabel", "Dịch vụ");//gán nhãn
        eventValue.put("fee", "0"); //Kiểu integer
        eventValue.put("description", description); //mô tả đơn hàng - short description

        //client extra data
        eventValue.put("requestId",  merchantCode+"merchant_billId_"+System.currentTimeMillis());
        eventValue.put("partnerCode", merchantCode);
        //Example extra data
        JSONObject objExtraData = new JSONObject();
        try {
            objExtraData.put("site_code", "008");
            objExtraData.put("site_name", "CGV Cresent Mall");
            objExtraData.put("screen_code", 0);
            objExtraData.put("screen_name", "Special");
            objExtraData.put("movie_name", "Kẻ Trộm Mặt Trăng 3");
            objExtraData.put("movie_format", "2D");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        eventValue.put("extraData", objExtraData.toString());

        eventValue.put("extra", "");
        AppMoMoLib.getInstance().requestMoMoCallBack(this, eventValue);


    }
    //Get token callback from MoMo app an submit to server side
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == AppMoMoLib.getInstance().REQUEST_CODE_MOMO && resultCode == -1) {
            if(data != null) {
                if(data.getIntExtra("status", -1) == 0) {
                    //TOKEN IS AVAILABLE
                    Log.d("thành công",data.getStringExtra("message"));
                    String token = data.getStringExtra("data"); //Token response
                    compositeDisposable.add(apiBanHang.Updatemomo(iddonhang,token)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                messagerModel -> {
                                    if (messagerModel.isSuccess()){
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                },
                                    throwable -> {
                                    Log.d("erro",throwable.getMessage());
                                    }
                            ));

                    String phoneNumber = data.getStringExtra("phonenumber");
                    String env = data.getStringExtra("env");
                    if(env == null){
                        env = "app";
                    }

                    if(token != null && !token.equals("")) {
                        // TODO: send phoneNumber & token to your server side to process payment with MoMo server
                        // IF Momo topup success, continue to process your order
                    } else {
                        Log.d("thành công","không thành công");
                    }
                } else if(data.getIntExtra("status", -1) == 1) {
                    //TOKEN FAIL
                    String message = data.getStringExtra("message") != null?data.getStringExtra("message"):"Thất bại";
                    Log.d("thành công","không thành công");
                } else if(data.getIntExtra("status", -1) == 2) {
                    //TOKEN FAIL
                    Log.d("thành công","không thành công");
                } else {
                    //TOKEN FAIL
                    Log.d("thành công","không thành công");
                }
            } else {
                Log.d("thành công","không thành công");
            }
        } else {
            Log.d("thành công","không thành công");
        }
    }

    private void initControl() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        sum = getIntent().getLongExtra("tongtien", 0);

        tongTien.setText(decimalFormat.format(sum));
        email.setText(Utils.currentUser.getEmail());
        sdt.setText(Utils.currentUser.getMobile());
        bt_datHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String diachi = diaChi.getText().toString().trim();
                if (TextUtils.isEmpty(diachi)) {
                    Toast.makeText(ThanhToanActivity.this, "Bạn chưa nhập địa chỉ", Toast.LENGTH_SHORT).show();
                } else {
                    String email = Utils.currentUser.getEmail();
                    String sdt = Utils.currentUser.getMobile();
                    int id = Utils.currentUser.getId();
                    compositeDisposable.add(apiBanHang.createOrder(email, sdt, String.valueOf(sum), id, diachi, total, new Gson().toJson(Utils.mangMuahang))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    MessagerModel -> {
                                        Log.d("asdasdawqe", email+sdt+sum+id+diachi+total+new Gson().toJson(Utils.mangMuahang));
                                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(i);
                                        finish();
                                        Utils.mangGiohang.clear();
                                    }


                            ));
                }
            }
        });

        btnmommo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String diachi = diaChi.getText().toString().trim();
                if (TextUtils.isEmpty(diachi)) {
                    Toast.makeText(ThanhToanActivity.this, "Bạn chưa nhập địa chỉ", Toast.LENGTH_SHORT).show();
                } else {
                    String email = Utils.currentUser.getEmail();
                    String sdt = Utils.currentUser.getMobile();
                    int id = Utils.currentUser.getId();
                    compositeDisposable.add(apiBanHang.createOrder(email, sdt, String.valueOf(sum), id, diachi, total, new Gson().toJson(Utils.mangMuahang))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    MessagerModel -> {
                                        Log.d("asdasdawqe", email+sdt+sum+id+diachi+total+new Gson().toJson(Utils.mangMuahang));
                                        requestPayment(MessagerModel.getIddonhang());
                                        iddonhang = Integer.parseInt(MessagerModel.getIddonhang());
                                        Utils.mangGiohang.clear();
                                    }


                            ));
                }
            }
        });

        btnzalopay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String diachi = diaChi.getText().toString().trim();
                if (TextUtils.isEmpty(diachi)) {
                    Toast.makeText(ThanhToanActivity.this, "Bạn chưa nhập địa chỉ", Toast.LENGTH_SHORT).show();
                } else {
                    String email = Utils.currentUser.getEmail();
                    String sdt = Utils.currentUser.getMobile();
                    int id = Utils.currentUser.getId();
                    compositeDisposable.add(apiBanHang.createOrder(email, sdt, String.valueOf(sum), id, diachi, total, new Gson().toJson(Utils.mangMuahang))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    MessagerModel -> {
                                        Log.d("asdasdawqe", email+sdt+sum+id+diachi+total+new Gson().toJson(Utils.mangMuahang));
                                        requestZalo();
                                        iddonhang = Integer.parseInt(MessagerModel.getIddonhang());
                                        Utils.mangGiohang.clear();
                                    }


                            ));
                }

            }
        });
    }

    private void requestZalo() {
        CreateOrder orderApi = new CreateOrder();

        try {
            JSONObject data = orderApi.createOrder("10000");
            String code = data.getString("return_code");
            Log.d("test",code);
            if (code.equals("1")) {
                String token = data.getString("zp_trans_token");
                Log.d("test",token);
                ZaloPaySDK.getInstance().payOrder(ThanhToanActivity.this, token, "demozpdk://app", new PayOrderListener() {
                    @Override
                    public void onPaymentSucceeded(String s, String s1, String s2) {
                        compositeDisposable.add(apiBanHang.Updatemomo(iddonhang, token)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        messagerModel -> {
                                            if (messagerModel.isSuccess()) {
                                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        },
                                        throwable -> {
                                            Log.d("erro", throwable.getMessage());
                                        }
                                ));
                    }

                    @Override
                    public void onPaymentCanceled(String s, String s1) {

                    }

                    @Override
                    public void onPaymentError(ZaloPayError zaloPayError, String s, String s1) {

                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        toolbar = findViewById(R.id.toolbar_Thanhtoan);
        tongTien = findViewById(R.id.tongTien_tt);
        email = findViewById(R.id.email_tt);
        sdt = findViewById(R.id.sdt_tt);
        diaChi = findViewById(R.id.diaChi_tt);
        bt_datHang = findViewById(R.id.bt_Dathang_tt);
        btnmommo = findViewById(R.id.btnmomo);
        btnzalopay = findViewById(R.id.btnzalopay);


    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
    }
}