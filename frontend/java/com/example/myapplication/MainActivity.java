package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.LinearLayout;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBarinmain = getSupportActionBar();
        //Toast.makeText(MainActivity.this, "from activity", Toast.LENGTH_SHORT).show();
        SpannableString spannableTitle = new SpannableString("Product Search");
        spannableTitle.setSpan(new ForegroundColorSpan(Color.WHITE), 0, spannableTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        actionBarinmain.setTitle(spannableTitle);
        actionBarinmain.setDisplayHomeAsUpEnabled(false);
        actionBarinmain.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.tab_background_color)));
        setContentView(R.layout.activity_main); // 确保使用了正确的布局文件
        TabLayout tabLayout = findViewById(R.id.tabbar);
        String[] tabTitles = {"SEARCH", "WISHLIST"};

        for (int i = 0; i < 2; i++) {
            TabLayout.Tab tab = tabLayout.newTab();
            View customTab = LayoutInflater.from(this).inflate(R.layout.tabatom, null);
            TextView tabText = customTab.findViewById(R.id.tab_text);
            tabText.setText(tabTitles[i]);
            tab.setCustomView(customTab);
            tabLayout.addTab(tab);
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
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // 可以根据需要实现
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // 可以根据需要实现
            }
        });
    }

    private void setupViewPager(ViewPager2 viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        adapter.addFragment(new SearchFragment());
        adapter.addFragment(new WishlistFragment());
        viewPager.setAdapter(adapter);
    }

    // ViewPagerAdapter 类的实现需要确保正确
}
