package com.example.myapplication;

import com.google.android.gms.common.data.DataHolder;

import org.json.JSONObject;

public class sharejson {
    public static final sharejson ourInstance = new sharejson();
    public JSONObject jsonObject;
    public static sharejson getInstance() {
        return ourInstance;
    }

    public sharejson() {
    }
    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }
}
