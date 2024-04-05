package com.example.appbanhang.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.metrics.Event;
import android.speech.tts.UtteranceProgressListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appbanhang.InterfaceUsing.ImageClickListener;
import com.example.appbanhang.Model.EventBus.TinhTongEvent;
import com.example.appbanhang.Model.GioHang;
import com.example.appbanhang.R;
import com.example.appbanhang.Utils.Utils;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.util.List;

public class GioHangAdapter extends RecyclerView.Adapter<GioHangAdapter.GioHangViewHolder> {

    Context context;
    List<GioHang> gioHangList;

    public GioHangAdapter(Context context, List<GioHang> gioHangList) {
        this.context = context;
        this.gioHangList = gioHangList;
    }

    @NonNull
    @Override
    public GioHangViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_giohang, parent,false);
        return new GioHangViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GioHangViewHolder holder, int position) {
        GioHang gioHang = gioHangList.get(position);
        holder.item_gioHang_sl.setText(String.valueOf(gioHang.getSl()));
        holder.item_gioHang_ten.setText(gioHang.getTensp());
        Glide.with(context).load(gioHang.getHinhsp()).into(holder.item_gioHang_image);
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        holder.item_gioHang_gia.setText(decimalFormat.format(gioHang.getGiasp()));
        long sum = gioHang.getSl() * gioHang.getGiasp();
        holder.item_gioHang_sumgia.setText(decimalFormat.format(sum));
        holder.setImageClickListener(new ImageClickListener() {
            @Override
            public void onImageClick(View view, int pos, int giatri) {
                if (giatri == 2) {
                    if(gioHangList.get(pos).getSl()>1){
                        int newsl = gioHangList.get(pos).getSl() - 1;
                        gioHangList.get(pos).setSl(newsl);
                        holder.item_gioHang_sl.setText(gioHangList.get(pos).getSl()+"");
                        long sum = gioHang.getSl() * gioHang.getGiasp();
                        holder.item_gioHang_sumgia.setText(decimalFormat.format(sum));
                        EventBus.getDefault().postSticky(new TinhTongEvent());
                    } else if (gioHangList.get(pos).getSl()==1) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getRootView().getContext());
                        builder.setTitle("Thông báo");
                        builder.setMessage("Bạn có muốn xóa sản phẩm này ra khỏi giỏ hàng?");
                        builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Utils.mangGiohang.remove(pos);
                                notifyDataSetChanged();
                                EventBus.getDefault().postSticky(new TinhTongEvent());
                            }
                        });
                        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });

                    }
                } else if (giatri == 1) {
                    if(gioHangList.get(pos).getSl()<11){
                        int newsl = gioHangList.get(pos).getSl() + 1;
                        gioHangList.get(pos).setSl(newsl);
                        notifyDataSetChanged();
                    }
                }
            }
        });
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b==true){
                    Utils.mangMuahang.add(gioHang);
                    EventBus.getDefault().postSticky(new TinhTongEvent());
                }else{
                    for(int i = 0; i< Utils.mangMuahang.size();i++){
                        if(Utils.mangMuahang.get(i).getIdsp() == gioHang.getIdsp()){
                            Utils.mangMuahang.remove(i);
                            EventBus.getDefault().postSticky(new TinhTongEvent());
                        }
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return gioHangList.size();
    }

    public class GioHangViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView item_gioHang_image,item_Tru,item_Cong;
        TextView item_gioHang_ten, item_gioHang_gia, item_gioHang_sl, item_gioHang_sumgia;
        ImageClickListener imageClickListener;
        CheckBox checkBox;

        public GioHangViewHolder(@NonNull View itemView) {
            super(itemView);
            item_Tru = itemView.findViewById(R.id.gioHang_tru);
            item_Cong = itemView.findViewById(R.id.gioHang_cong);
            item_gioHang_image = itemView.findViewById(R.id.gioHang_image);
            item_gioHang_ten = itemView.findViewById(R.id.gioHang_tensp);
            item_gioHang_gia = itemView.findViewById(R.id.gioHang_gia);
            item_gioHang_sumgia = itemView.findViewById(R.id.gioHang_tongGia);
            item_gioHang_sl = itemView.findViewById(R.id.gioHang_soluong);
            checkBox = itemView.findViewById(R.id.cb_gioHang);

            item_Cong.setOnClickListener(this);
            item_Tru.setOnClickListener(this);
        }

        public void setImageClickListener(ImageClickListener imageClickListener) {
            this.imageClickListener = imageClickListener;
        }

        @Override
        public void onClick(View view) {
            if(view == item_Cong) {
                imageClickListener.onImageClick(view,getAdapterPosition(),1);
            } else if (view == item_Tru) {
                imageClickListener.onImageClick(view,getAdapterPosition(),2);
            }
        }


    }
}
