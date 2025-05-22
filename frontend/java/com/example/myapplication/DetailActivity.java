package com.example.myapplication;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class DetailActivity extends AppCompatActivity {
    public String productTitle;
  public JSONObject PostData;
    public FloatingActionButton fab;
    public String productId;
    public String itemURL;
    public boolean whetherincart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deatil); // 確保這裡使用了正確的佈局文件名
        fab=findViewById(R.id.fab);
        try {
            Log.d("aassss","try to go detailactivity");
            productTitle = getIntent().getStringExtra("PRODUCT_Title");
            productId = getIntent().getStringExtra("PRODUCT_Id");
            whetherincart=getIntent().getBooleanExtra("whetherinlist",false);
            itemURL=getIntent().getStringExtra("PRODUCT_url");
            Log.d("aassss",""+whetherincart);
            if(whetherincart){
                Drawable icon = ContextCompat.getDrawable(getApplicationContext(), R.drawable.cart_off);
                icon.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                fab.setImageDrawable(icon);
            }
            else{Drawable icon = ContextCompat.getDrawable(getApplicationContext(), R.drawable.cart_plus);
                icon.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                fab.setImageDrawable(icon);}
            String PostDatainstring=getIntent().getStringExtra("foraddwishilist");
            PostData=new JSONObject(PostDatainstring);
            Log.d("aassss",getIntent().getStringExtra("foraddwishilist"));
            ActionBar actionBar = getSupportActionBar();
            SpannableString spannableTitle = new SpannableString(productTitle);
            spannableTitle.setSpan(new ForegroundColorSpan(Color.WHITE), 0, spannableTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            actionBar.setTitle(spannableTitle);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.tab_background_color)));
            Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.ic_back_arrow);
            actionBar.setHomeAsUpIndicator(upArrow);
        } catch (Exception e) {
            Log.d("openerror", e.toString());
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("build_postdata", "wishlist in detail on click");
                /**
                Intent returnIntent = new Intent();
                returnIntent.putExtra("wishlistChanged", true);
                setResult(Activity.RESULT_OK, returnIntent);
                 **/
                if (whetherincart) {//already in wishlist
                    try {
                        String url = "http://10.0.2.2:3000/delete/" + URLEncoder.encode(productId, "UTF-8");
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, url, null,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Log.d("Response", response.toString());
                                        Drawable icon = ContextCompat.getDrawable(getApplicationContext(), R.drawable.cart_plus);
                                        icon.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                                        fab.setImageDrawable(icon);
                                        whetherincart = false;
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("Error.Response", error.toString());
                            }
                        });
                        RequestQueue requestQueue = Volley.newRequestQueue(DetailActivity.this);
                        requestQueue.add(jsonObjectRequest);
                        String titlecut=productTitle.substring(0,10) + "...";
                        Toast.makeText(DetailActivity.this, titlecut+"was removed from wishlist", Toast.LENGTH_SHORT).show();
                    } catch (UnsupportedEncodingException R) {
                        Log.d("ENCODE_PROBLEM", R.toString());
                    }

                } else {//not already in wishlist

                    try {
                        String url = "http://10.0.2.2:3000/addto";

                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, PostData,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Log.d("Response", response.toString());
                                        Drawable icon = ContextCompat.getDrawable(getApplicationContext(), R.drawable.cart_off);
                                        icon.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                                        fab.setImageDrawable(icon);
                                        whetherincart = true;
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("Error.Response", error.toString());
                            }
                        });
                        RequestQueue requestQueue = Volley.newRequestQueue(DetailActivity.this);
                        requestQueue.add(jsonObjectRequest);
                        String titlecut=productTitle.substring(0,10) + "...";
                        Toast.makeText(DetailActivity.this, titlecut+"was added to wishlist", Toast.LENGTH_SHORT).show();
                        whetherincart = true;
                    } catch (Exception e) {
                        Log.e("RequestError", e.toString());
                    }
                }
            }});

                // 初始化標籤和 ViewPager
                try {
                    TabLayout tabLayout = findViewById(R.id.tabbar);
                    // 为每个标签定义不同的图标
                    int[] tabIcons = {
                            R.drawable.information_variant,
                            R.drawable.truck_delivery,
                            R.drawable.google,
                            R.drawable.equal
                    };

                    String[] tabTitles = {"PRODUCT", "SHIPPING", "PHOTOS", "SIMILAR"};

                    for (int i = 0; i < 4; i++) {
                        TabLayout.Tab tab = tabLayout.newTab();
                        View customTab = LayoutInflater.from(this).inflate(R.layout.tabatom, null);
                        TextView tabText = customTab.findViewById(R.id.tab_text);
                        ImageView tabIcon = customTab.findViewById(R.id.tab_icon);
                        tabText.setText(tabTitles[i]);
                        tabIcon.setImageResource(tabIcons[i]);
                        tab.setCustomView(customTab);
                        tabLayout.addTab(tab);
                        setTabColor(tab, i == 0);
                    }


                    ViewPager2 viewPager = findViewById(R.id.viewPager);
                    setupViewPager(viewPager);
                    viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                        @Override
                        public void onPageSelected(int position) {
                            super.onPageSelected(position);
                            tabLayout.selectTab(tabLayout.getTabAt(position));
                        }
                    });

                    // 设置 TabLayout 选项卡选择监听器
                    tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                        @Override
                        public void onTabSelected(TabLayout.Tab tab) {
                            setTabColor(tab, true); // 设置为选中颜色
                            viewPager.setCurrentItem(tab.getPosition());
                        }

                        @Override
                        public void onTabUnselected(TabLayout.Tab tab) {
                            setTabColor(tab, false);

                        }

                        @Override
                        public void onTabReselected(TabLayout.Tab tab) {
                            // 可以根据需要实现
                        }
                    });
                } catch (Exception e) {
                    Log.d("openerror", e.toString());
                }
            }


    public String getProductId() {
        return productId;
    }

    public String getProductTitle() {
        return productTitle;
    }
    private void setTabColor(TabLayout.Tab tab, boolean isSelected) {
        View customView = tab.getCustomView();
        if (customView != null) {
            ImageView tabIcon = customView.findViewById(R.id.tab_icon);
            TextView tabText = customView.findViewById(R.id.tab_text);
            if (isSelected) {
                tabIcon.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
                tabText.setTextColor(Color.WHITE);
            } else {
                tabIcon.setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_IN);
                tabText.setTextColor(Color.YELLOW);
            }
        }
    }
    private void setupViewPager(ViewPager2 viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        adapter.addFragment(new ProductsFragment());
        adapter.addFragment(new ShippingFragment());
        adapter.addFragment(new PhotoFragment());
        adapter.addFragment(new SimilarFragment());
        viewPager.setAdapter(adapter);
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_actionbar, menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_custom_icon) {
            String URL="https://www.facebook.com/sharer/sharer.php?u="+itemURL+"&hashtag=%23CSCI571Fall23AndroidApp";
            Log.d("FBURL", URL);
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(URL));
            startActivity(i);
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

