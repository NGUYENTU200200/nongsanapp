package com.example.appbanhang.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appbanhang.Model.Item;
import com.example.appbanhang.R;

import java.util.List;

public class ChiTietAdapter extends RecyclerView.Adapter<ChiTietAdapter.ChiTietViewHolder> {
    List<Item> itemList;
    Context context;

    public ChiTietAdapter(List<Item> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }

    @NonNull
    @Override
    public ChiTietViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chitiet,parent,false);
        return new ChiTietViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChiTietViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.ten.setText(item.getTensp());
        holder.sl.setText("Số lượng " + item.getSoluong()+"");
        Glide.with(context).load(item.getHinhanh()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ChiTietViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView ten,sl;

        public ChiTietViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_imgchitiet);
            ten = itemView.findViewById(R.id.tenSp_chitiet);
            sl = itemView.findViewById(R.id.sl_chitiet);
        }
    }
}
