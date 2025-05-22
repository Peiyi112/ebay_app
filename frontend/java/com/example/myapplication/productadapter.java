package com.example.myapplication;

import static androidx.core.content.ContextCompat.startActivity;

import static com.example.myapplication.gensearch.YOUR_REQUEST_CODE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.jar.JarException;

public class productadapter extends RecyclerView.Adapter<productadapter.ViewHolder> {
    private List<productoutline> productList;
    public JSONObject PostData;
    private Context context; // 添加這個上下文引用
    public productadapter(Context context ,List<productoutline> productList) {
        this.context = context;
        this.productList = productList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView productImage;
        public ImageView wishimage;

        public String productID;
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
            wishimage=itemView.findViewById(R.id.WISHPHOTO);


        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.productcard, parent, false);//to show which xml align with
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
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
        if(product.getincart()) {
            holder.wishimage.setImageResource(R.drawable.cart_off);
        }else{
            holder.wishimage.setImageResource(R.drawable.cart_plus);
        }
        holder.productImage.setOnClickListener(new View.OnClickListener() {//go to detail
            @Override
            public void onClick(View v) {
                // 使用 holder.getAdapterPosition() 來獲取正確的位置
                int currentPosition = holder.getAdapterPosition();
                Intent intent = new Intent(context, DetailActivity.class);
                Log.d("aassss","check whether valid id:"+product.getId());
                intent.putExtra("PRODUCT_Id", product.getId());
                Log.d("aassss","check whether title id:"+product.getTitle());
                intent.putExtra("PRODUCT_Title", product.getTitle());
                intent.putExtra("whetherinlist",product.getincart());
                intent.putExtra("PRODUCT_url", product.getitemURL());
                PostData = new JSONObject();
                try {//prepare for detail
                    PostData.put("productid", product.getId());
                    PostData.put("image",product.getImageURL());
                    PostData.put("title", product.getTitle());
                    PostData.put("price", product.getPrice());
                    PostData.put("shippingcost", product.getShippingcost());
                    PostData.put("condition", product.getCondition());
                    PostData.put("zipcode", product.getZipcode());
                }catch(JSONException A){
                    Log.d("aassss",A.toString());
                }
                intent.putExtra("foraddwishilist",PostData.toString());

                try {
                    if (context instanceof Activity) {
                        ((Activity) context).startActivityForResult(intent, YOUR_REQUEST_CODE);
                    } else {
                        context.startActivity(intent);
                    }
                } catch (Exception e) {
                    Log.d("aassss", "try to GO TO DETAIL PAGE");
                    Log.d("aassss", e.toString());
                }
            }
        });
        holder.wishimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPosition = holder.getAdapterPosition();
                Log.d("build_postdata", "wishlist on click");
                if(product.getincart()){//already in wishlist
                    try {
                        String url = "http://10.0.2.2:3000/delete/" + URLEncoder.encode(product.getId(), "UTF-8");
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, url, null,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Log.d("Response", response.toString());
                                        notifyItemChanged(currentPosition);
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("Error.Response", error.toString());
                            }
                        });
                        RequestQueue requestQueue = Volley.newRequestQueue(holder.itemView.getContext());
                        requestQueue.add(jsonObjectRequest);
                        product.setincart(false);
                        String titlecut=product.getTitle().substring(0,10) + "...";
                        Toast.makeText(context, titlecut+"was removed from wishlist", Toast.LENGTH_SHORT).show();
                    }catch(UnsupportedEncodingException R){
                        Log.d("ENCODE_PROBLEM", R.toString());
                    }

                }
                else {//not already in wishlist
                     PostData = new JSONObject();
                    Log.e("aaaaaaa", "sc"+product.getShippingcost()+product.getCondition()+product.getZipcode());
                    try {
                        PostData.put("productid", product.getId());
                        PostData.put("image", product.getImageURL());
                        PostData.put("title", product.getTitle());
                        PostData.put("price", product.getPrice());
                        PostData.put("shippingcost", product.getShippingcost());
                        PostData.put("condition", product.getCondition());
                        PostData.put("zipcode", product.getZipcode());
                    } catch (Exception a) {
                        Log.d("build_postdata", a.toString());
                    }
                    try {
                        String url = "http://10.0.2.2:3000/addto";
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, PostData,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Log.d("Response", response.toString());
                                        notifyItemChanged(currentPosition);
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("Error.Response", error.toString());
                            }
                        });
                        RequestQueue requestQueue = Volley.newRequestQueue(holder.itemView.getContext());
                        requestQueue.add(jsonObjectRequest);
                        String titlecut=product.getTitle().substring(0,10) + "...";
                        Toast.makeText(context, titlecut+"was added to wishlist", Toast.LENGTH_SHORT).show();
                        product.setincart(true);
                    } catch (Exception e) {
                        Log.e("RequestError", e.toString());
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
}
