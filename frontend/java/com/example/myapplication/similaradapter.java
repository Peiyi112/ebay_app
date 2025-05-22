package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class similaradapter extends RecyclerView.Adapter<similaradapter.ViewHolder> {
    private List<similaroutline> productList;
    private Context context; // 添加這個上下文引用
    public similaradapter(Context context ,List<similaroutline> productList) {
        this.context = context;
        this.productList = productList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView productImage;
        public ImageView wishimage;
        public String productID;
        public TextView productTitle;
        public TextView productPrice;
        public TextView Daysleft;
        public TextView Shippingcost;


        public ViewHolder(View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productTitle = itemView.findViewById(R.id.title);
            productPrice = itemView.findViewById(R.id.price);
            Daysleft=itemView.findViewById(R.id.daysleft);
            Shippingcost=itemView.findViewById(R.id.shipcost);

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.similarcard, parent, false);//to show which xml align with
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        similaroutline product = productList.get(position);
        holder.productTitle.setText(product.getTitle());
        holder.productPrice.setText(product.getPrice());
        holder.Daysleft.setText(product.getdaysleft());
        holder.Shippingcost.setText(product.getShippingcost());

        Glide.with(holder.itemView.getContext())
                .load(product.getImageURL())
                .error(R.drawable.defaultimage)
                .into(holder.productImage);

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
}
