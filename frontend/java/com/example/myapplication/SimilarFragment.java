package com.example.myapplication;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.AdapterView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SimilarFragment extends Fragment {
    private similaradapter adapter;
    private String currentSortCriteria = "name";
    private RequestQueue requestQueue;
    private RecyclerView recyclerView;
    private List<similaroutline> similarproductList;
    private String title;
    private String pricevalue;
    private String url;
    private String dayextract;
    private JSONObject butitnowprice;
    public static SimilarFragment newInstance() {
        return new SimilarFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // 初始化视图和组件
        View view = inflater.inflate(R.layout.fragment_similar, container, false);
        recyclerView = view.findViewById(R.id.recycleview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        similarproductList = new ArrayList<>();
        adapter = new similaradapter(getContext(), similarproductList);
        recyclerView.setAdapter(adapter);
        requestQueue = Volley.newRequestQueue(getContext());
        DetailActivity activity = (DetailActivity) getActivity();
        if (activity != null) {
            String productId = activity.getProductId();
            try {
                Log.e("callisme", "try to call req");
                String url = "http://10.0.2.2:3000/similarproducts?itemID=" + URLEncoder.encode(productId, "UTF-8");
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                        response -> parseResponse(response),
                        error -> Log.e("Volley error response", error.toString())
                );

                requestQueue.add(jsonObjectRequest);
            } catch(UnsupportedEncodingException ex) {
                Log.d("encode error", ex.toString());
            }
        }

        Spinner ad = view.findViewById(R.id.ad);
        ArrayAdapter<CharSequence> adapterAd =
                ArrayAdapter.createFromResource(getContext(),
                        R.array.ad,
                        android.R.layout.simple_spinner_item);
        adapterAd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ad.setAdapter(adapterAd);
        ad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // 当用户选择下拉列表中的某个项目时执行的操作
                String selectedItem = parent.getItemAtPosition(position).toString();
                Log.v("genurl", "change of ad pick: " + selectedItem);
                String sortOrder = parent.getItemAtPosition(position).toString();
                sortList(currentSortCriteria, sortOrder);
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 当没有项目被选中时执行的操作
            }
        });
        Spinner sortby = view.findViewById(R.id.sortby);
        ArrayAdapter<CharSequence> adapterSortBy =
                ArrayAdapter.createFromResource(getContext(),
                        R.array.Sortby,
                        android.R.layout.simple_spinner_item);
        adapterSortBy.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortby.setAdapter(adapterSortBy);
        sortby.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentSortCriteria = parent.getItemAtPosition(position).toString();
                sortList(currentSortCriteria, ad.getSelectedItem().toString());
                if ("Default".equals(currentSortCriteria)) {
                    ad.setEnabled(false); // 禁用第二个 Spinner
                    ad.setSelection(0); // 可以选择重置第二个 Spinner 的选中项
                } else {
                    ad.setEnabled(true); // 启用第二个 Spinner
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 当没有项目被选中时执行的操作
            }

        });
        return view;

    }

    private void parseResponse(JSONObject response) {
        try{

            JSONObject getSimilarItemsResponse = response.getJSONObject("getSimilarItemsResponse");
            JSONObject itemRecommendations = getSimilarItemsResponse.getJSONObject("itemRecommendations");
            JSONArray items = itemRecommendations.getJSONArray("item");

            similarproductList.clear();
            for(int i=0;i<items.length();i++) {
                JSONObject item = items.getJSONObject(i);
                similaroutline product = new similaroutline();
                if(item.getString("title")!=null){
                title = item.getString("title");
                product.setTitle(title);}
                if(item.getString("imageURL")!=null){
                    url = item.getString("imageURL");
                    product.setImageURL(url);}
                JSONObject shippingCost = item.getJSONObject("shippingCost");
                double shipvalue = shippingCost.getDouble("__value__");
                String shipcost = "" + shipvalue;
                if (shipvalue == 0) {
                    shipcost = "Free Shipping";
                }
                product.setShippingcost(shipcost);
                if(item.getJSONObject("buyItNowPrice")!=null){
                butitnowprice = item.getJSONObject("buyItNowPrice");
                pricevalue = butitnowprice.getString("__value__");
                    product.setPrice(pricevalue);
                }
                if(item.getString("timeLeft")!=null){
                    String Dayobject = item.getString("timeLeft");
                    int start = Dayobject.indexOf("P") + 1;
                    int END = Dayobject.indexOf("D");
                    dayextract = Dayobject.substring(start, END);
                    product.setdaysleft(dayextract+" Days Left");
                }

                similarproductList.add(product);
            }
            if(getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }

        }catch(JSONException A){
            Log.d("Parse problem", A.toString());
        }
        catch(Exception a){
            Log.d("Parse problem exception",a.toString());
        }
    }

    private void sortList(String sortCriteria,String sortOrder){
        Comparator<similaroutline> comparator;
        switch(sortCriteria){
            case "Name":
                comparator = Comparator.comparing(similaroutline::getTitle);
                break;
            case "Price":
                comparator = Comparator.comparingDouble(o -> Double.parseDouble(o.getPrice()));
                break;
            case "Days":
                comparator = Comparator.comparingDouble(o -> Double.parseDouble(o.getdaysleft().substring(0,2)));
                break;
            case "Default":
                comparator = Comparator.comparing(similaroutline::getTitle);
                break;
            default:
                comparator = Comparator.comparing(similaroutline::getTitle); // 默认排序
                break;
        }
        if ("Descending".equals(sortOrder)) {
            comparator = comparator.reversed();
        }
        try {
            similarproductList.sort(comparator);

            adapter.notifyDataSetChanged();
        }catch(Exception w){
            Log.d("what the hell is days",w.toString());
        }
    }


}
