package com.example.appbanhang.Activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appbanhang.Adapter.LoaiSpAdapter;
import com.example.appbanhang.Adapter.SanPhamMoiAdapter;
import com.example.appbanhang.Model.LoaiSp;
import com.example.appbanhang.Model.SanPhamMoi;
import com.example.appbanhang.Model.SanPhamMoiModel;
import com.example.appbanhang.Model.User;
import com.example.appbanhang.R;
import com.example.appbanhang.Retrofit.ApiBanHang;
import com.example.appbanhang.Retrofit.RetrofitClient;
import com.example.appbanhang.Utils.Utils;
import com.google.android.material.navigation.NavigationView;
import com.nex3z.notificationbadge.NotificationBadge;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    ViewFlipper viewFlipper;
    RecyclerView recyclerViewmanhinhchinh;
    NavigationView navigationView;
    ListView listViewmanhinhchinh;
    DrawerLayout drawerLayout;
    LoaiSpAdapter loaiSpAdapter;
    List<LoaiSp> mangLoaiSp;
    ApiBanHang apiBanHang;
    List<SanPhamMoi> mangSanPhammoi;
    NotificationBadge notificationBadge;
    FrameLayout frameLayout;
    ImageView search;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    SanPhamMoiAdapter sanPhamMoiAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        Paper.init(this);
        if(Paper.book().read("user")!=null){
            User user = Paper.book().read("user");
            Utils.currentUser = user;
        }
        anhxa();
        ActionBar();
        ActionviewLipper();
        if(isConnected(this)){
            ActionviewLipper();
            getLoaiSanPham();
            getEventClick();
            search();
            getSpmoi();
        }
    }

    private void search() {
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),SearchActivity.class);
                startActivity(i);
            }
        });
    }

    private void getEventClick() {
        listViewmanhinhchinh.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        Intent trangchu = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(trangchu);
                        break;
                    case 1:
                        Intent hoaqua = new Intent(getApplicationContext(), SanPhamActivity.class);
                        hoaqua.putExtra("loai",1);
                        startActivity(hoaqua);
                        break;
                    case 2:
                        Intent dokho = new Intent(getApplicationContext(), SanPhamActivity.class);
                        dokho.putExtra("loai",2);
                        startActivity(dokho);
                        break;
                    case 4:
                        Intent xemdon = new Intent(getApplicationContext(), XemDonActivity.class);
                        startActivity(xemdon);
                        break;
                    case 5:
                        Paper.book().delete("user");
                        Intent dangnhap = new Intent(getApplicationContext(),DangNhapActivity.class);
                        startActivity(dangnhap);
                        break;
                    case 6:
                        Intent live = new Intent(getApplicationContext(),JoinActivity.class);
                        startActivity(live);
                        break;
                }
            }
        });
    }

    private void getLoaiSanPham() {
        compositeDisposable.add(apiBanHang.getLoaiSp()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        loaiSpModel -> {
                            if(loaiSpModel.isSuccess()){
                                mangLoaiSp = loaiSpModel.getResult();
                                loaiSpAdapter = new LoaiSpAdapter(mangLoaiSp,getApplicationContext());
                                listViewmanhinhchinh.setAdapter(loaiSpAdapter);
                            }
                        }
                ));
    }
    private void getSpmoi(){
        apiBanHang.getSpmoi().enqueue(new Callback<SanPhamMoiModel>() {
            @Override
            public void onResponse(Call<SanPhamMoiModel> call, Response<SanPhamMoiModel> response) {
                mangSanPhammoi = response.body().getResult();
                sanPhamMoiAdapter = new SanPhamMoiAdapter(getApplicationContext(),mangSanPhammoi);
                recyclerViewmanhinhchinh.setAdapter(sanPhamMoiAdapter);
            }

            @Override
            public void onFailure(Call<SanPhamMoiModel> call, Throwable t) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    private void ActionviewLipper() {
        List<String>mangquangcao =new ArrayList<>();
        mangquangcao.add("https://mauweb.monamedia.net/thegioididong/wp-content/uploads/2017/12/banner-Le-hoi-phu-kien-800-300.png");
        mangquangcao.add("https://mauweb.monamedia.net/thegioididong/wp-content/uploads/2017/12/banner-HC-Tra-Gop-800-300.png");
        mangquangcao.add("https://mauweb.monamedia.net/thegioididong/wp-content/uploads/2017/12/banner-big-ky-nguyen-800-300.jpg");
        for (int i=0;i<mangquangcao.size();i++){
            ImageView imageView = new ImageView(getApplicationContext());
            Glide.with(getApplicationContext()).load(mangquangcao.get(i)).into(imageView);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            viewFlipper.addView(imageView);
        }
        viewFlipper.setFlipInterval(3000);
        viewFlipper.setAutoStart(true);
        Animation slide_in_right = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_in_right);
        Animation slide_out_right = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_out_right);
        viewFlipper.setInAnimation(slide_in_right);
        viewFlipper.setOutAnimation(slide_out_right);
    }

    private void ActionBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_sort_by_size);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }


    private void anhxa(){
        toolbar = findViewById(R.id.Toolbarmanhinhchinh);
        viewFlipper = findViewById(R.id.ViewFlipper);
        recyclerViewmanhinhchinh = findViewById(R.id.recyclewview);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this,3);
        recyclerViewmanhinhchinh.setLayoutManager(layoutManager);
        recyclerViewmanhinhchinh.setHasFixedSize(true);
        navigationView = findViewById(R.id.navigationview);
        listViewmanhinhchinh= findViewById(R.id.listviewanhinhchinh);
        drawerLayout = findViewById(R.id.DrawerLayout);
        mangLoaiSp = new ArrayList<>();

        search = findViewById(R.id.search_main);
        notificationBadge = findViewById(R.id.menu_sl_main);
        if (Utils.mangGiohang == null) {
            Utils.mangGiohang = new ArrayList<>();
        }else{
            int total = 0;
            for(int i=0;i<Utils.mangGiohang.size();i++){
                total += Utils.mangGiohang.get(i).getSl();
            }
            notificationBadge.setText(String.valueOf(total));
        }
        frameLayout = findViewById(R.id.frame_main);
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), GioHangActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        int total = 0;
        for(int i=0;i<Utils.mangGiohang.size();i++){
            total += Utils.mangGiohang.get(i).getSl();
        }
        notificationBadge.setText(String.valueOf(total));
    }

    private boolean isConnected(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if(wifi != null && wifi.isConnected() || mobile != null && mobile.isConnected()){
            return true;
        }else{
            return false;
        }
    }
}