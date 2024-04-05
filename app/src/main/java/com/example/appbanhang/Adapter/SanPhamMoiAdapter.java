package com.example.appbanhang.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appbanhang.Activity.ChiTietActivity;
import com.example.appbanhang.InterfaceUsing.ItemClickListener;
import com.example.appbanhang.Model.SanPhamMoi;
import com.example.appbanhang.R;

import java.util.List;

public class SanPhamMoiAdapter extends RecyclerView.Adapter<SanPhamMoiAdapter.SanPhamMoiViewHolder> {

    Context context;
    List<SanPhamMoi> list;

    public SanPhamMoiAdapter(Context context, List<SanPhamMoi> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public SanPhamMoiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sp_moi,parent,false);
        return new SanPhamMoiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SanPhamMoiViewHolder holder, int position) {
        SanPhamMoi sanPhamMoi = list.get(position);
        holder.tv_ten.setText(sanPhamMoi.getTensp());
        holder.tv_gia.setText(sanPhamMoi.getGiasp());
        Glide.with(context).load(sanPhamMoi.getHinhanh()).into(holder.hinhanh);
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int pos, boolean isLongClick) {
                if(!isLongClick){
                    Intent i = new Intent(context, ChiTietActivity.class);
                    i.putExtra("chitiet",sanPhamMoi);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class SanPhamMoiViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tv_gia, tv_ten;
        ImageView hinhanh;
        private ItemClickListener itemClickListener;

        public SanPhamMoiViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_gia = itemView.findViewById(R.id.item_sp_gia);
            tv_ten = itemView.findViewById(R.id.item_sp_ten);
            hinhanh = itemView.findViewById(R.id.item_sp_image);
            itemView.setOnClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onClick(view,getAdapterPosition(),false);
        }
    }
}
