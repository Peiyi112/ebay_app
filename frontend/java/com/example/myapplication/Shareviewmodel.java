package com.example.myapplication;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class Shareviewmodel extends ViewModel {
    private final MutableLiveData<Productmodule> productLiveData = new MutableLiveData<>();

    public void setProduct(Productmodule product) {
        productLiveData.setValue(product);
    }

    public LiveData<Productmodule> getProduct() {
        return productLiveData;
    }

}
