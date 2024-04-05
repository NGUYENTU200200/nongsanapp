package com.example.appbanhang.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appbanhang.Model.DonHang;
import com.example.appbanhang.R;

import java.util.List;

public class DonHangAdapter extends RecyclerView.Adapter<DonHangAdapter.DonHangViewHolder> {
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();

    Context context;
    List<DonHang> list;

    public DonHangAdapter(Context context, List<DonHang> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public DonHangViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_don_hang,parent,false);
        return new DonHangViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DonHangViewHolder holder, int position) {
        DonHang donHang = list.get(position);
        holder.tv_donHang.setText("Đơn hàng" + donHang.getId()+"");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
        holder.recyclerView_chitiet.getContext(),LinearLayoutManager.VERTICAL,false);
        linearLayoutManager.setInitialPrefetchItemCount(donHang.getItem().size());
        ChiTietAdapter chiTietAdapter = new ChiTietAdapter(donHang.getItem(),context);
        holder.recyclerView_chitiet.setLayoutManager(linearLayoutManager);
        holder.recyclerView_chitiet.setAdapter(chiTietAdapter);
        holder.recyclerView_chitiet.setRecycledViewPool(viewPool);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class DonHangViewHolder extends RecyclerView.ViewHolder {

        TextView tv_donHang;
        RecyclerView recyclerView_chitiet;

        public DonHangViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_donHang = itemView.findViewById(R.id.id_donHang);
            recyclerView_chitiet = itemView.findViewById(R.id.recycleView_chitiet);
        }
    }
}
