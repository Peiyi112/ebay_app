package com.example.myapplication;

import static java.security.AccessController.getContext;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class gensearch extends AppCompatActivity {
    private List<String> mongodblist = new ArrayList<>();
    public static final int YOUR_REQUEST_CODE = 1;
    private RequestQueue requestQueue;
    private RecyclerView recyclerView ;
    private LinearLayout haveresult;
    private LinearLayout noresult;
    private LinearLayout progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gensearch);

    }
    @Override
    protected void onResume() {
        super.onResume();
        initializeView(); // 每次活動恢復時都重新初始化視圖
    }
    private void initializeView() {
        // 將原來在 onCreate 中的初始化代碼放在這裡
        haveresult = findViewById(R.id.haveresult);
        haveresult=findViewById(R.id.haveresult);
        haveresult.setVisibility(View.VISIBLE);
        recyclerView = findViewById(R.id.my_recycler_view);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        noresult=findViewById(R.id.noresult);
        noresult.setVisibility(View.GONE);
        try{;
            ActionBar actionBar = getSupportActionBar();
            SpannableString spannableTitle = new SpannableString("Search Results");
            spannableTitle.setSpan(new ForegroundColorSpan(Color.WHITE), 0, spannableTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            actionBar.setTitle(spannableTitle);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.tab_background_color)));
            Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.ic_back_arrow);
            actionBar.setHomeAsUpIndicator(upArrow);}
        catch(Exception E){
            Log.d("openerror", E.toString());
        }
        requestQueue = Volley.newRequestQueue(this);
        try{
            String url = "http://10.0.2.2:3000/getalllist";
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("servergenurl", "getmongodb has tried");
                            dealmongo(response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Volley error response", error.toString());
                        }
                    });
            requestQueue.add(jsonObjectRequest);
        }catch(Exception a){
            Log.d("can't get back wishlist", a.toString());
        }
    }

    private void dealmongo(JSONObject response) {
        try {
            JSONArray items = response.getJSONArray("data");
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                String id=item.getString("productid");
                mongodblist.add(id);
            }
            JSONObject Data = sharejson.getInstance().getJsonObject();
            dealResponse(Data);
        }catch(JSONException A){
            Log.d("MONGO JSON ERROR",A.toString());
        }
    }

    public void dealResponse(JSONObject Data){
        List<productoutline> productList=new ArrayList<>();
        try {
            Log.d("aassss", "try to deal response");

            JSONArray findItemsAdvancedResponseArray = Data.getJSONArray("findItemsAdvancedResponse");
            JSONObject finditemobject = findItemsAdvancedResponseArray.getJSONObject(0);
            JSONArray searchResultArray = finditemobject.getJSONArray("searchResult");
            JSONObject searchResultObject = searchResultArray.getJSONObject(0);
            Integer count = searchResultObject.getInt("@count");
            if (count == 0) {
                haveresult.setVisibility(View.GONE);
                noresult.setVisibility(View.VISIBLE);
            } else {
                JSONArray Items = searchResultObject.getJSONArray("item");
                for (int i = 0; i < Items.length(); i++) {
                    JSONObject item = Items.getJSONObject(i);
                    productoutline product = new productoutline();
                    String itemURL = item.getJSONArray("viewItemURL").getString(0);
                    product.setItemURL(itemURL);
                    String itemId = item.getJSONArray("itemId").getString(0);
                    product.setId(itemId);

                    if (mongodblist.contains(itemId)) {
                        product.setincart(true);
                    } else {
                        product.setincart(false);
                    }
                    product.setImageURL(item.getJSONArray("galleryURL").getString(0));
                    String origintitle = item.getJSONArray("title").getString(0);
                    if (origintitle.length() > 35) {
                        String truncatedString = origintitle.substring(0, 35) + "...";
                        product.setTitle(truncatedString);
                    } else {
                        product.setTitle(item.getJSONArray("title").getString(0));
                    }
                    if (item.has("postalCode")) {
                        JSONArray postalCodeArray = item.getJSONArray("postalCode");
                        String postalCode = postalCodeArray.getString(0); // 获取数组的第一个元素
                        product.setZipcode("Zip :" + postalCode);
                    } else {
                        product.setZipcode(""); // 如果没有 postalCode，设置为默认值
                    }
                    // 先获取 shippingInfo 数组
                    JSONArray shippingInfoArray = item.getJSONArray("shippingInfo");
// 获取第一个 shippingInfo 元素（假设每个item只有一个shippingInfo）
                    JSONObject shippingInfo = shippingInfoArray.getJSONObject(0);

// 安全地获取 shippingServiceCost 数组
                    JSONArray shippingServiceCostArray = shippingInfo.optJSONArray("shippingServiceCost");
                    if (shippingServiceCostArray != null && shippingServiceCostArray.length() > 0) {
                        JSONObject shippingServiceCost = shippingServiceCostArray.getJSONObject(0);
                        String shippingCostValue = shippingServiceCost.getString("__value__");

                        if ("0.0".equals(shippingCostValue)) {
                            product.setShippingcost("Free");
                        } else {
                            product.setShippingcost(shippingCostValue);
                        }
                    } else {
                        product.setShippingcost(""); // 设置为 "Unknown" 或其他适当的默认值
                    }


                    //condition
                    JSONArray conditionarray = item.getJSONArray("condition");
                    JSONObject conditionobject = conditionarray.getJSONObject(0);
                    JSONArray conditiondisplay = conditionobject.getJSONArray("conditionDisplayName");
                    String conditionDisplayName = conditiondisplay.getString(0);
                    product.setCondition(conditionDisplayName);

                    //price
                    JSONArray sellingStatusArray = item.getJSONArray("sellingStatus");
                    JSONObject sellingStatusObject = sellingStatusArray.getJSONObject(0);
                    JSONArray currentPriceArray = sellingStatusObject.getJSONArray("currentPrice");
                    JSONObject currentPriceObject = currentPriceArray.getJSONObject(0);
                    String price = currentPriceObject.getString("__value__");
                    product.setPrice(price);

                    //add to the list
                    productList.add(product);
                }

                try {
                    Log.d("aassss", "try to create recyview");
                    recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
                    productadapter adapter = new productadapter(this, productList);
                    recyclerView.setAdapter(adapter);
                } catch (Exception E) {
                    E.printStackTrace();
                }
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }


}