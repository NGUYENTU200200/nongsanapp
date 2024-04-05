package com.example.appbanhang.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.appbanhang.Adapter.GioHangAdapter;
import com.example.appbanhang.Model.EventBus.TinhTongEvent;
import com.example.appbanhang.Model.GioHang;
import com.example.appbanhang.R;
import com.example.appbanhang.Utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.List;

public class GioHangActivity extends AppCompatActivity {

    TextView gioHangTrong,sum_Sp;
    Toolbar toolbar;
    RecyclerView recyclerViewGioHang;
    Button buy;
    GioHangAdapter gioHangAdapter;
    long sum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gio_hang);
        initView();
        initControl();
        tinhTongTien();
    }

    private void tinhTongTien() {
        sum = 0;
        for(int i=0;i<Utils.mangMuahang.size();i++){
            sum = sum + (Utils.mangMuahang.get(i).getGiasp()*Utils.mangMuahang.get(i).getSl());
        }
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        sum_Sp.setText(decimalFormat.format(sum));
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
        recyclerViewGioHang.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewGioHang.setLayoutManager(layoutManager);

        if (Utils.mangGiohang.isEmpty()) {
            gioHangTrong.setVisibility(View.VISIBLE);
        }else{
            gioHangAdapter = new GioHangAdapter(getApplicationContext(),Utils.mangGiohang);
            recyclerViewGioHang.setAdapter(gioHangAdapter);
        }
        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),ThanhToanActivity.class);
                i.putExtra("tongtien",sum);
                Utils.mangGiohang.clear();
                startActivity(i);
            }
        });
    }

    private void initView() {
        gioHangTrong = findViewById(R.id.gioHang_tvTrong);
        sum_Sp = findViewById(R.id.tv_TongTien);
        toolbar = findViewById(R.id.toolbar_GioHang);
        recyclerViewGioHang = findViewById(R.id.recycleView_gioHang);
        buy = findViewById(R.id.gioHang_MuaHang);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
    //Cần xem lại
    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void eventTinhTien(TinhTongEvent event){
        if(event != null){
            tinhTongTien();
        }
    }

}