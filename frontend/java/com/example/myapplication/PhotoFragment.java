package com.example.myapplication;

import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

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

public class PhotoFragment extends Fragment {
    private RequestQueue requestQueue;
    private PhotoViewModel mViewModel;

    public static PhotoFragment newInstance() {
        return new PhotoFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_photo, container, false);
        DetailActivity activity = (DetailActivity) getActivity();
        requestQueue = Volley.newRequestQueue(getContext());
        if (activity != null) {
            Log.d("servergenurl", "photofragment has called");
            try {
                String title = activity.getProductTitle();
                String url = "http://10.0.2.2:3000/phototab?title=" + URLEncoder.encode(title, "UTF-8");
                Log.d("servergenurl", url.toString());
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d("servergenurl", "photofragment has tried");
                                try {
                                    Log.d("servergenurl", "photofragment has tried");
                                    JSONArray items = response.getJSONArray("items");
                                    LinearLayout imagesContainer = view.findViewById(R.id.images_container);

                                    for (int i = 0; i < items.length(); i++) {
                                        JSONObject item = items.getJSONObject(i);
                                        String imageUrl = item.getString("link");

                                        CardView cardView = new CardView(getContext());
                                        LinearLayout.LayoutParams cardLayoutParams = new LinearLayout.LayoutParams(
                                                LinearLayout.LayoutParams.MATCH_PARENT,
                                                LinearLayout.LayoutParams.WRAP_CONTENT
                                        );
                                        cardLayoutParams.setMargins(10, 10, 10, 10);
                                        cardView.setLayoutParams(cardLayoutParams);
                                        cardView.setCardElevation(8); // 设置阴影大小
                                        cardView.setRadius(8); // 设置圆角半径

                                        ImageView imageView = new ImageView(getContext());
                                        int width = getResources().getDisplayMetrics().widthPixels; // 获取屏幕宽度
                                        int height = width; // 假设图片宽高比为 4:3

                                        CardView.LayoutParams layoutParams = new CardView.LayoutParams(
                                                CardView.LayoutParams.MATCH_PARENT,
                                                height
                                        );
                                        imageView.setLayoutParams(layoutParams);

                                        // 使用Glide或其他库加载图片
                                        Glide.with(view.getContext())
                                                .load(imageUrl)
                                                .override(width, height)
                                                .fitCenter()
                                                .into(imageView);

                                        cardView.addView(imageView); // 将 ImageView 添加到 CardView
                                        imagesContainer.addView(cardView); // 将 CardView 添加到外部容器
                                    }
                                } catch (JSONException R) {
                                    Log.d("photofragmentjson", R.toString());
                                }
                            }},
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("Volley error response", error.toString());
                            }
                        });
                requestQueue.add(jsonObjectRequest);
            } catch (Exception r) {
                Log.d("photofragment", r.toString());
            }

        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(PhotoViewModel.class);
        // TODO: Use the ViewModel
    }

}