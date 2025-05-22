package com.example.myapplication;

public class similaroutline

{   private String id;
    private String title;

    private String shippingcost;

    private String price;
    private String daysleft;
    private String imageURL;
    public void setdaysleft(String daysleft){
        this.daysleft=daysleft;
    }
    public String getdaysleft(){
        return this.daysleft;
    }
    public void setId(String id){
        this.id=id;
    }
    public String getId(){
        return this.id;
    }

    public void setTitle(String title){
        this.title=title;
    }
    public String getTitle(){
        return this.title;
    }

    public void setShippingcost(String shippingcost){
        this.shippingcost=shippingcost;
    }
    public String getShippingcost(){
        return this.shippingcost;
    }

    public void setPrice(String price){
        this.price=price;
    }
    public String getPrice(){
        return this.price;
    }
    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
    public String getImageURL(){
        return this.imageURL;
    }

}

