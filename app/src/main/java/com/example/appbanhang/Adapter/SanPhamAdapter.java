package com.example.appbanhang.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appbanhang.Activity.ChiTietActivity;
import com.example.appbanhang.InterfaceUsing.ItemClickListener;
import com.example.appbanhang.Model.SanPhamMoi;
import com.example.appbanhang.R;

import java.text.DecimalFormat;
import java.util.List;

public class SanPhamAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_DATA = 0;
    private static final int VIEW_TYPE_LOADING = 1;
    Context context;
    List<SanPhamMoi> array;

    public SanPhamAdapter(Context context, List<SanPhamMoi> array) {
        this.context = context;
        this.array = array;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_DATA) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sp, parent, false);
            return new SanPhamViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SanPhamViewHolder) {
            SanPhamViewHolder dienThoaiViewHolder = (SanPhamViewHolder) holder;
            SanPhamMoi sanPhamMoi = array.get(position);
            dienThoaiViewHolder.ten.setText(sanPhamMoi.getTensp());
            DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
            dienThoaiViewHolder.gia.setText("Price" + decimalFormat.format(Double.parseDouble(sanPhamMoi.getGiasp())));
//            dienThoaiViewHolder.mota.setText(sanPhamMoi.getMota());
            Glide.with(context).load(sanPhamMoi.getHinhanh()).into(dienThoaiViewHolder.hinhanh);
            dienThoaiViewHolder.setItemClickListener(new ItemClickListener() {
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
        } else {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return array.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_DATA;
    }

    @Override
    public int getItemCount() {
        return array.size();
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }

    public class SanPhamViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView ten, gia;
        ImageView hinhanh;
        private ItemClickListener itemClickListener;

        public SanPhamViewHolder(@NonNull View itemView) {
            super(itemView);
            ten = itemView.findViewById(R.id.name_dt);
            gia = itemView.findViewById(R.id.gia_dt);
//            mota = itemView.findViewById(R.id.mota_dt);
            hinhanh = itemView.findViewById(R.id.anh_dt);
            itemView.setOnClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener){
            this.itemClickListener = itemClickListener;
        }
        @Override
        public void onClick(View view) {
            itemClickListener.onClick(view,getAdapterPosition(),false);
        }
    }
}
