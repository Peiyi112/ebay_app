package com.example.myapplication;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;



public class ProductsFragment extends Fragment {
    private Shareviewmodel viewModel;
    private RequestQueue requestQueue;


    public static ProductsFragment newInstance() {
        return new ProductsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_products, container, false);
        LinearLayout progressBar = view.findViewById(R.id.progressBar);
        LinearLayout ALL=view.findViewById(R.id.all);
        progressBar.setVisibility(View.VISIBLE);
        ALL.setVisibility(View.GONE);
        LinearLayout imagegallery=view.findViewById(R.id.imagegallery);
        requestQueue = Volley.newRequestQueue(getContext());
        DetailActivity activity = (DetailActivity) getActivity();
        viewModel = new ViewModelProvider(requireActivity()).get(Shareviewmodel.class);
        List<String> imageUrls = new ArrayList<>();

        Log.d("servergenurl", "hello");
        if (activity != null) {
            Log.d("servergenurl", "senddetailreq has called");
            try {
                String productId = activity.getProductId();
                String url = "http://10.0.2.2:3000/detail?itemID=" + URLEncoder.encode(productId, "UTF-8");
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    // 首先获取 Item 对象
                                    Productmodule product = parseResponseToProductModule(response);
                                    viewModel.setProduct(product);
                                    JSONObject item = response.getJSONObject("Item");
                                    boolean havebrand=false;
                                    // 解析产品图片

                                    JSONArray pictureUrls = item.getJSONArray("PictureURL");
                                    for (int i = 0; i < pictureUrls.length(); i++) {
                                        try {
                                            String imageUrl = pictureUrls.getString(i);
                                            ImageView imageView = new ImageView(getContext());
                                            // 设置ImageView的布局参数，例如宽高和边距
                                            int width = getResources().getDisplayMetrics().widthPixels; // 获取屏幕宽度
                                            int height = width; // 假设图片宽高比为 1:1
                                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
                                            layoutParams.setMargins(10, 0, 10, 20); // 在图片下方添加边距
                                            imageView.setLayoutParams(layoutParams);
                                            // 使用Glide或其他库加载图片
                                            Glide.with(getContext())
                                                    .load(imageUrl)
                                                    .into(imageView);

                                            imagegallery.addView(imageView);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }




                                    String title = item.getString("Title");
                                    TextView titleTextView = view.findViewById(R.id.title);
                                    titleTextView.setText(title);

                                    JSONObject currentPrice = item.getJSONObject("CurrentPrice");
                                    double price = currentPrice.getDouble("Value");

                                    TextView priceTextView = view.findViewById(R.id.pricewithshipping);
                                    JSONObject shippingCostSummary = item.getJSONObject("ShippingCostSummary");
                                    if(shippingCostSummary.has("ShippingServiceCost")) {
                                        JSONObject shippingServiceCost = shippingCostSummary.getJSONObject("ShippingServiceCost");
                                        double shippingValue = shippingServiceCost.getDouble("Value");

                                        String priceText;
                                        if (shippingValue == 0) {
                                            priceText = "$" + price + " With Free Shipping";
                                        } else {
                                            priceText = "$" + price + " With $" + shippingValue + " Shipping";
                                        }
                                        priceTextView.setText(priceText);
                                    }
                                    else{
                                            Log.d("servergenurl", "there is fucking no shipping cost");
                                        }

                                        TextView price1 = view.findViewById(R.id.price);
                                        price1.setText("$" + price);


                                        try {
                                            JSONObject itemSpecifics = item.getJSONObject("ItemSpecifics");
                                            JSONArray nameValueList = itemSpecifics.getJSONArray("NameValueList");
                                            LinearLayout specificationsLayout = view.findViewById(R.id.specificationsLayout);

                                            for (int i = 0; i < nameValueList.length(); i++) {
                                                JSONObject nameValueItem = nameValueList.getJSONObject(i);
                                                String name = nameValueItem.getString("Name");
                                                JSONArray valueArray = nameValueItem.getJSONArray("Value");
                                                TextView brand1 = view.findViewById(R.id.brand);
                                                if (name.equals("Brand")) {
                                                    havebrand=true;
                                                    brand1.setText(valueArray.getString(0));
                                                }
                                                if (valueArray.length() > 0) {
                                                    String value = valueArray.getString(0); // 获取第一个值

                                                    // 创建 TextView 并设置属性
                                                    TextView textView = new TextView(getContext());
                                                    textView.setLayoutParams(new LinearLayout.LayoutParams(
                                                            ViewGroup.LayoutParams.WRAP_CONTENT,
                                                            ViewGroup.LayoutParams.WRAP_CONTENT));
                                                    textView.setText("\u2022" + value);
                                                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

                                                    // 将 TextView 添加到布局中
                                                    specificationsLayout.addView(textView);
                                                }
                                            }if(!havebrand){
                                                LinearLayout brandtotal=view.findViewById(R.id.brandtotal);
                                                brandtotal.setVisibility(View.GONE);
                                            }
                                        } catch (JSONException e) {
                                            Log.e("JSON Parse Error", e.toString());
                                        }

                                } catch (JSONException e) {
                                    Log.e("JSON Parse Error", e.toString());
                                }
                                progressBar.setVisibility(View.GONE);
                                ALL.setVisibility(View.VISIBLE);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("Volley error response", error.toString());
                            }
                        });

// Access the RequestQueue through your singleton class.
                requestQueue.add(jsonObjectRequest);
            } catch (Exception E) {
                Log.e("ENCODE response", E.toString());
            }
        }

        return view;
    }

    private Productmodule parseResponseToProductModule(JSONObject response) {
        Productmodule product = new Productmodule();
        try {
            JSONObject item = response.getJSONObject("Item");
            //
            JSONObject storefront = item.getJSONObject("Storefront");
            if(storefront.has("StoreURL")) {
                String storeURL = storefront.getString("StoreURL");
                product.setStoreURL(storeURL);
            }
            if(storefront.has("StoreName")) {
                String storeName = storefront.getString("StoreName");
                product.setStoreNAME(storeName);
            }

            JSONObject Seller = item.getJSONObject("Seller");
            if(Seller.has("FeedbackRatingStar")){
                String FeedbackRatingStar = Seller.getString("FeedbackRatingStar");
                product.setfeedbackstar(FeedbackRatingStar);
            }
            if(Seller.has("FeedbackScore")){
                String FeedbackScore = Seller.getString("FeedbackScore");
                product.setfeedbackscore(FeedbackScore);
            }
            if(Seller.has("PositiveFeedbackPercent")){
                String PositiveFeedbackPercent = Seller.getString("PositiveFeedbackPercent");
                product.setpopularity(PositiveFeedbackPercent);
            }

            JSONObject shippingsummary=item.getJSONObject("ShippingCostSummary");
            if(shippingsummary.has("ShippingServiceCost")) {
                JSONObject Shippingarray = shippingsummary.getJSONObject("ShippingServiceCost");
                Double shipcost=Shippingarray.getDouble("Value");
                if(shipcost==0){
                    product.setshipcost("Free Shipping");
                }
                else{
                    product.setshipcost("$"+shipcost);
                }
            }
            //
            if(item.has("GlobalShipping")) {
                String GlobalShipping = item.getString("GlobalShipping");
                product.setglobalship(GlobalShipping);
            }
            if(item.has("HandlingTime")) {
                String HandlingTime = item.getString("HandlingTime");
                product.sethandletime(HandlingTime);
            }
            if(item.has("ConditionDescription")) {
                String ConditionDescription = item.getString("ConditionDescription");
                product.setconditiondes(ConditionDescription);
            }



            //
            JSONObject ReturnPolicy= item.getJSONObject("ReturnPolicy");
            if(ReturnPolicy.has("Refund")){
                String Refund = ReturnPolicy.getString("Refund");
                product.setrefund(Refund);
            }
            if(ReturnPolicy.has("ReturnsWithin")){
                String ReturnsWithin = ReturnPolicy.getString("ReturnsWithin");
                product.setReturnswithin(ReturnsWithin);
            }
            if(ReturnPolicy.has("ReturnsAccepted")){
                String ReturnsAccepted = ReturnPolicy.getString("ReturnsAccepted");
                product.setpolicy(ReturnsAccepted);
                Log.e("ENCODE response", ReturnsAccepted);
            } else{
                Log.e("ENCODE response", "what the fuck didn't extract returnsaccepted right");
            }
            if(ReturnPolicy.has("ShippingCostPaidBy")){
                String ShippingCostPaidBy = ReturnPolicy.getString("ShippingCostPaidBy");
                product.setshipby(ShippingCostPaidBy);
                Log.e("ENCODE response", ShippingCostPaidBy);
            }


        }catch(JSONException R){
            Log.e("ENCODE response", R.toString());
        }
        Log.d("servergenurl", product.toString());
        return product;
    }
}

