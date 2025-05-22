package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
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
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class SearchFragment extends Fragment {
    private String option;
    private RequestQueue requestQueue;
    private String keywordinput ;
    private String distanceinput;
    private boolean localcheck ;
    private boolean freeshipcheck ;
    private boolean Newcheck ;
    private boolean Usedcheck ;
    private boolean Unspecifiedcheck ;
    private boolean nearbycheck ;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private RadioButton enterzip ;
    private RadioButton current;

    private String zipcode;
    private AutoCompleteTextView zipcodeinput;
    private ArrayAdapter<String> adapter;  // Declare ArrayAdapter at the class level

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // 初始化视图和组件
        View view = inflater.inflate(R.layout.search_fragment, container, false);

        zipcodeinput = view.findViewById(R.id.ZipCodeinput);
        TextView keywordalert = view.findViewById(R.id.keywordalert);
        keywordalert.setVisibility(View.GONE);
        TextView zipalert = view.findViewById(R.id.zipalert);
        zipalert.setVisibility(View.GONE);
        EditText keyword = view.findViewById(R.id.keywordinput);
        EditText distance = view.findViewById(R.id.distanceinput);CheckBox local = view.findViewById(R.id.local);
        CheckBox freeship = view.findViewById(R.id.freeship);
        CheckBox New = view.findViewById(R.id.New);
        CheckBox Used = view.findViewById(R.id.Used);
        CheckBox Unspecified = view.findViewById(R.id.Unspecified);
        CheckBox nearby = view.findViewById(R.id.nearby);
        LinearLayout layoutAdditional = view.findViewById(R.id.layoutAdditional);
        layoutAdditional.setVisibility(View.GONE);
        enterzip = view.findViewById(R.id.radio_enter_zip);
        current = view.findViewById(R.id.current);
        Button searchbtn = view.findViewById(R.id.search);
        Button clearbtn = view.findViewById(R.id.clear);
        requestQueue = Volley.newRequestQueue(getContext());
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        Spinner categoryoptions = view.findViewById(R.id.categoryoptions);//clearbutton listener
        //get old search info
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("SearchFragmentPrefs", Context.MODE_PRIVATE);
        keyword.setText(sharedPreferences.getString("keyword", ""));
        distance.setText(sharedPreferences.getString("distance", ""));
        zipcodeinput.setText(sharedPreferences.getString("zipcode", ""));
        local.setChecked(sharedPreferences.getBoolean("localcheck", false));
        freeship.setChecked(sharedPreferences.getBoolean("freeshipcheck", false));
        nearby.setChecked(sharedPreferences.getBoolean("nearbycheck", false));
        New.setChecked(sharedPreferences.getBoolean("newcheck", false));
        Used.setChecked(sharedPreferences.getBoolean("usedcheck", false));
        Unspecified.setChecked(sharedPreferences.getBoolean("uSPECcheck", false));
        current.setChecked(sharedPreferences.getBoolean("current", false));
        enterzip.setChecked(sharedPreferences.getBoolean("enterzip", false));




        // Initialize adapter with static data
        List<String> staticData = Arrays.asList("12345", "23456", "34567");
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, staticData);
        zipcodeinput.setAdapter(adapter);

        zipcodeinput.setThreshold(1); // Trigger autocomplete for any input

        // zipcode autocomplete
        zipcodeinput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 2) { // 修改为当输入至少两个字符时触发
                    try {
                        sendRequest(s.toString());
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

                zipcode = zipcodeinput.getText().toString();
                Log.v("after", "zipcode: " + zipcode);
            }
        });

        clearbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                keyword.setText("");
                distance.setText("");
                zipcodeinput.setText("");
                local.setChecked(false);
                New.setChecked(false);
                Unspecified.setChecked(false);
                nearby.setChecked(false);
                freeship.setChecked(false);
                Used.setChecked(false);
                enterzip.setChecked(false);
                current.setChecked(false);
                categoryoptions.setSelection(0, true);
                keywordalert.setVisibility(View.GONE);
                zipalert.setVisibility(View.GONE);
            }
        });
//checkbox to boolean
        New.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Newcheck = isChecked;
            }
        });
        Used.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Usedcheck = isChecked;
            }
        });
        Unspecified.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Unspecifiedcheck = isChecked;
            }
        });
        local.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                localcheck = isChecked;
            }
        });
        freeship.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                freeshipcheck = isChecked;
            }
        });

        //distance readin
        distance.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                distanceinput=null;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                distanceinput=distance.getText().toString().trim();
            }
        });

//點了nearby才出現下面的東西
        nearby.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    nearbycheck = true;
                    layoutAdditional.setVisibility(View.VISIBLE);
                    current.setChecked(true);
                } else {
                    nearbycheck = false;
                    layoutAdditional.setVisibility(View.GONE);
                }
            }
        });

        enterzip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    current.setChecked(false);
                }
            }
        });
        //currentlocation button
        current.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    zipalert.setVisibility(View.GONE);
                    enterzip.setChecked(false);
                    //when get permisson
                    if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        GETLOCATION();
                    } else {//when permission is denied
                        // 當許可被拒絕時
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                101); // YOUR_REQUEST_CODE 是一個整數，用於標識這個請求
                    }
                }
            }
        });


        //search button 要檢查,檢查無誤才去搜尋
        searchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keywordinput = keyword.getText().toString().trim();
                String zipcode = zipcodeinput.getText().toString().trim();
                Log.v("genurl","zipcode is:"+zipcode);
                boolean isZipcodeEnabled = enterzip.isChecked();
                Log.v("genurl","enterzip:"+isZipcodeEnabled);
                Log.v("genurl","enterzip:"+zipcode.isEmpty());
                keywordalert.setVisibility(View.GONE);
                zipalert.setVisibility(View.GONE);
                if (keywordinput.isEmpty()) {
                    keywordalert.setVisibility(View.VISIBLE);
                    try {
                        Toast.makeText(getActivity(), "Please fix all fields with errors", Toast.LENGTH_SHORT).show();
                    }catch(Exception A){
                        Log.v("genurl",A.toString());
                    }

                }
                if (isZipcodeEnabled && zipcode.isEmpty()) {
                    zipalert.setVisibility(View.VISIBLE);
                    try {
                        Toast.makeText(getActivity(), "Please fix all fields with errors", Toast.LENGTH_SHORT).show();
                    }catch(Exception A){
                        Log.v("genurl",A.toString());
                    }

                }
                if (!keywordinput.isEmpty() && (current.isChecked() || (isZipcodeEnabled && !zipcode.isEmpty()))) {
                    buildurl();
                }else if (!keywordinput.isEmpty() && (!nearby.isChecked() ) ){
                    buildurl();
                }
            }
        });


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.categoryoptions, // 从 strings.xml 文件获取选项数组
                android.R.layout.simple_spinner_item); // 下拉菜单未展开时的视图
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // 下拉菜单展开时的视图

        categoryoptions.setAdapter(adapter);

        // 设置选项选择监听器
        categoryoptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (adapterView.getId() == R.id.categoryoptions) {
                    option = adapterView.getItemAtPosition(position).toString();
                    Log.d("Selected Item", option);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                option = null; // 当没有选项被选中时执行的操作
            }
        });
        //categoryoptions.setSelection(2, false); can use to default as this option
        // 初始化標籤和 ViewPager
        return view;
    }//end of create




    private void sendRequest(String query) throws UnsupportedEncodingException {
        Log.d("zipcode", "zip code request was called");
        Log.d("query", query);
        String url = "http://10.0.2.2:3000/getPostalCode?query=" + URLEncoder.encode(query, "UTF-8");
        Log.d("url", url);

        RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("url",  response);
                        getActivity().runOnUiThread(() -> {
                            updateAutoCompleteTextView(response); // 确保在主线程中更新UI
                        });
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error", error.toString());
            }
        });

        queue.add(stringRequest);
    }

    private void updateAutoCompleteTextView(String response) {
        ArrayList<String> postalCodeOptions = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++) {
                postalCodeOptions.add(jsonArray.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("autocomplete", "Postal codes: " + postalCodeOptions.toString());

        if (adapter != null) {
            adapter.clear();
            adapter.addAll(postalCodeOptions);
            adapter.notifyDataSetChanged();
        }
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() != null) {
            // Initialize adapter with static data for testing
            List<String> staticData = Arrays.asList("12345", "23456", "34567");
            adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, staticData);
            zipcodeinput.setAdapter(adapter);
        }
    }
    private void buildurl() {
        Log.v("genurl","build is called");
        StringBuilder genURL = new StringBuilder("https://svcs.ebay.com/services/search/FindingService/v1?OPERATION-NAME=findItemsAdvanced&SERVICE-VERSION=1.0.0");
        genURL.append("&SECURITY-APPNAME=PeiYiLin-peiyi-PRD-3728e2735-75510787") ;
        genURL.append("&RESPONSE-DATA-FORMAT=JSON&REST-PAYLOAD");
        genURL.append("&paginationInput.entriesPerPage=50");
        genURL.append("&keywords=");
        genURL.append(keywordinput);
        if(option!=null) {
            switch (option) {
                case "Art":
                    genURL.append("&categoryId=550");
                    break;
                case "Baby":
                    genURL.append("&categoryId=2984");
                    break;
                case "Books":
                    genURL.append("&categoryId=267");
                    break;
                case "Clothing,Shoes and Accessories":
                    genURL.append("&categoryId=11450");
                    break;
                case "Computer, Tablets and Network":
                    genURL.append("&categoryId=58058");
                    break;
                case "Health and Beauty":
                    genURL.append("&categoryId=26395");
                    break;
                case "Music":
                    genURL.append("&categoryId=11233");
                    break;
                case "Video, Games and Consoles":
                    genURL.append("&categoryId=1249");
                    break;
            }
        }
        if(zipcode!=null){
            genURL.append("&buyerPostalCode=")
                    .append(zipcode);
        }
        int i=0;
        Log.v("genurl",genURL.toString());
        if (distanceinput !=null) {
            genURL.append("&itemFilter(")
                    .append(i)
                    .append(").name=MaxDistance&itemFilter(")
                    .append(i)
                    .append(").value=")
                    .append(distanceinput);
            i++;
        }
        if(freeshipcheck){
            genURL.append("&itemFilter(")
                    .append(i)
                    .append(").name=FreeShippingOnly&itemFilter(")
                    .append(i)
                    .append(").value=true");
            i++;
        }
        if(localcheck){
            genURL.append("&itemFilter(")
                    .append(i)
                    .append(").name=LocalPickupOnly&itemFilter(")
                    .append(i)
                    .append(").value=true");
            i++;
        }
        genURL.append("&itemFilter(")
                .append(i)
                .append(").name=HideDuplicateItems&itemFilter(")
                .append(i)
                .append(").value=true");

        if(Newcheck||Usedcheck||Unspecifiedcheck){
            genURL.append("&itemFilter(")
                    .append(i)
                    .append(").name=Condition");
            int value=0;
            if(Newcheck){
                genURL.append("&itemFilter(")
                        .append(i)
                        .append(").value(")
                        .append(value)
                        .append(")=New");
                value++;
            }
            if(Usedcheck){
                genURL.append("&itemFilter(")
                        .append(i)
                        .append(").value(")
                        .append(value)
                        .append(")=Used");
                value++;
            }
            if(Unspecifiedcheck){
                genURL.append("&itemFilter(")
                        .append(i)
                        .append(").value(")
                        .append(value)
                        .append(")=Unspecified");
            }
        }
        genURL.append("&outputSelector(0)=SellerInfo&outputSelector(1)=StoreInfo");
        Log.v("genurl",genURL.toString());
        try{
            sendgenreq(genURL.toString());}
        catch(UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void sendgenreq(String GenURL) throws UnsupportedEncodingException{
        Log.d("servergenurl", "sendgenreq has called");
        String url = "http://10.0.2.2:3000/sendreqgen?url=" + URLEncoder.encode(GenURL, "UTF-8");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("serverhasresp", "have response");
                        sharejson sharedData=new sharejson();
                        sharedData.getInstance().setJsonObject(response);
                        Intent intent=new Intent(getActivity(),gensearch.class);
                        try{startActivity(intent);}
                        catch(Exception e){
                            Log.d("openerror", e.toString());
                        }

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

    }

    private void GETLOCATION() {
        Log.v("MyApp", "getlocation called ");
        if(fusedLocationProviderClient!=null&&ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Log.v("MyApp", "oncompleted called ");
                    if (task.isSuccessful()) {
                        Log.v("MyApp", "task successful");
                        Location location = task.getResult();
                        if(location!=null){
                            Log.v("MyApp", "location!=null " );
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            Geocoder geocoder=new Geocoder(getContext(), Locale.getDefault());

                            try{
                                List<Address> addresses=geocoder.getFromLocation(latitude, longitude, 1);
                                if (addresses != null && addresses.size() > 0) {
                                    Address address = addresses.get(0);
                                    // 獲取郵政編碼
                                    String zipCode = address.getPostalCode();
                                    Log.v("currnet", "Zip Code: " + zipCode);
                                    zipcode=zipCode;//update the zipcode with zipCode get from current location
                                    // 在這裡可以使用郵政編碼
                                }
                            }
                            catch (IOException e) {
                                e.printStackTrace();
                            }}
                        else {
                            Log.v("MyApp", "location is null");
                        }
                    }
                }

            });
        }}
    @Override
    public void onPause() {
        super.onPause();
        saveSearchState();
    }

    private void saveSearchState() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("SearchFragmentPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Save the state of your inputs
        editor.putString("keyword", keywordinput);
        editor.putString("distance", distanceinput);
        editor.putString("zipcode", zipcodeinput.getText().toString());
        editor.putBoolean("localcheck", localcheck);
        editor.putBoolean("freeshipcheck", freeshipcheck);
        editor.putBoolean("nearbycheck", nearbycheck);
        editor.putBoolean("newcheck", Newcheck);
        editor.putBoolean("usedcheck", Usedcheck);
        editor.putBoolean("uSPECcheck", Unspecifiedcheck);
        editor.putBoolean("current", current.isChecked());
        editor.putBoolean("enterzip", enterzip.isChecked());

        editor.apply();
    }
}
