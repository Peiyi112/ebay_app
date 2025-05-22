package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WishlistFragment extends Fragment implements wishlistadapter.OnItemDeletedListener{
    private RequestQueue requestQueue;
    private RecyclerView recyclerView;
    private TextView cost;
    private TextView noone;
    private Double Cost=0.0;
    private TextView itemnumber;
    private Integer Itemnumber=0;
    private LinearLayout total;
    private wishlistadapter adapter;
    private List<productoutline> productList = new ArrayList<>();
    @Override
    public void onItemDeleted(double price) {
        updateTotalAfterDeletion(price);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.wishlist_fragment, container, false);
        recyclerView = view.findViewById(R.id.my_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new wishlistadapter(getContext(), productList, this);
        recyclerView.setAdapter(adapter);
        total=view.findViewById(R.id.total);
         cost=view.findViewById(R.id.costotal);
        itemnumber=view.findViewById(R.id.itemtotal);
         noone=view.findViewById(R.id.noone);
        noone.setVisibility(View.GONE);
        requestQueue = Volley.newRequestQueue(getContext());
        fetchWishlist();
        return view;
    }

    private void fetchWishlist() {
        String url = "http://10.0.2.2:3000/getalllist";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                this::dealmongo,
                error -> Log.e("Volley error response", error.toString()));
        requestQueue.add(jsonObjectRequest);
    }

    private void dealmongo(JSONObject response) {

        try {
            JSONArray items = response.getJSONArray("data");
            if(items.length()==0){
                noone.setVisibility(View.VISIBLE);
                total.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
            }
            else {
                productList.clear();
                Itemnumber = items.length();
                for (int i = 0; i < items.length(); i++) {
                    JSONObject item = items.getJSONObject(i);
                    productoutline product = new productoutline();
                    product.setId(item.getString("productid"));
                    product.setImageURL(item.getString("image"));
                    product.setPrice(item.getString("price"));
                    Cost += Double.parseDouble(item.getString("price"));
                    product.setTitle(item.getString("title"));
                    // 确保字符串字段没有前导或尾随空格
                    product.setincart(true);

                    product.setCondition(item.getString("condition"));
                    product.setZipcode(item.getString("zipcode"));
                    product.setShippingcost(item.getString("shippingcost"));

                    productList.add(product);
                }
                updateTotal(); // 更新總價格和項目數量顯示
                adapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            Log.d("MONGO JSON ERROR", e.toString());
        }

    }
    public void updateTotalAfterDeletion(double price) {
        Cost -= price;
        Itemnumber--;
        updateTotal();
    }

    // 更新總價格和項目數量的顯示
    private void updateTotal() {
        if(Itemnumber==0){
            noone.setVisibility(View.VISIBLE);
            total.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
        }
        cost.setText(String.format("$%.2f", Cost));
        itemnumber.setText("Wishlist Total(" + Itemnumber + " items)");
    }
}

