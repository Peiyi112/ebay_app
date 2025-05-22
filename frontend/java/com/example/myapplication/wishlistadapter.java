package com.example.myapplication;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public class wishlistadapter extends RecyclerView.Adapter<wishlistadapter.ViewHolder> {
    private List<productoutline> productList;
    private Context context;
    private OnItemDeletedListener listener;

    public wishlistadapter(Context context, List<productoutline> productList, OnItemDeletedListener listener) {
        this.context = context;
        this.productList = productList;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView productImage;
        public ImageView wishimage;
        public TextView productTitle;
        public TextView productPrice;
        public TextView productZipcode;
        public TextView productcondition;
        public TextView productShipping;

        public ViewHolder(View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productTitle = itemView.findViewById(R.id.title);
            productPrice = itemView.findViewById(R.id.price);
            productZipcode = itemView.findViewById(R.id.zipcode);
            productcondition = itemView.findViewById(R.id.condition);
            productShipping = itemView.findViewById(R.id.shipping);
            wishimage = itemView.findViewById(R.id.WISHPHOTO);
        }
    }

    @Override
    public wishlistadapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.productcard, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(wishlistadapter.ViewHolder holder, int position) {
        productoutline product = productList.get(position);
        holder.productTitle.setText(product.getTitle());
        holder.productPrice.setText(product.getPrice());
        holder.productShipping.setText(product.getShippingcost());
        holder.productcondition.setText(product.getCondition());
        holder.productZipcode.setText(product.getZipcode());
        Glide.with(holder.itemView.getContext())
                .load(product.getImageURL())
                .error(R.drawable.defaultimage)
                .into(holder.productImage);
        holder.wishimage.setImageResource(R.drawable.cart_off);

        holder.wishimage.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            double price = Double.parseDouble(product.getPrice());
            try {
                String url = "http://10.0.2.2:3000/delete/" + URLEncoder.encode(product.getId(), "UTF-8");
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, url, null,
                        response -> {
                            Log.d("Response", response.toString());
                            notifyItemRemoved(currentPosition);
                            productList.remove(currentPosition);
                            listener.onItemDeleted(price);
                        },
                        error -> Log.e("Error.Response", error.toString()));
                RequestQueue requestQueue = Volley.newRequestQueue(holder.itemView.getContext());
                requestQueue.add(jsonObjectRequest);
                String titlecut=product.getTitle().substring(0,10) + "...";

                Toast.makeText(context, titlecut+"was removed from wishlist", Toast.LENGTH_SHORT).show();
            }catch(UnsupportedEncodingException A){
                Log.d("Response", A.toString());
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
    public interface OnItemDeletedListener {
        void onItemDeleted(double price);
    }
}


