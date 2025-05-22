package com.example.myapplication;

public class productoutline
{   private String id;
    private String title;
    private String zipcode;
    private String shippingcost;
    private String condition;
    private String price;
    private String itemURL;
    private boolean incart;

    private String imageURL;

    public void setId(String id){
        this.id=id;
    }
    public String getId(){
        return this.id;
    }

    public void setItemURL(String itemURL){
        this.itemURL=itemURL;
    }
    public String getitemURL(){
        return this.itemURL;
    }
    public void setincart(boolean incart){
        this.incart=incart;
    }
    public boolean getincart(){
        return this.incart;
    }

    public void setTitle(String title){
        this.title=title;
    }
    public String getTitle(){
        return this.title;
    }
    public void setZipcode(String zipcode){
        this.zipcode=zipcode;
    }
    public String getZipcode(){
        return this.zipcode;
    }
    public void setShippingcost(String shippingcost){
        this.shippingcost=shippingcost;
    }
    public String getShippingcost(){
        return this.shippingcost;
    }
    public void setCondition(String condition){
        this.condition=condition;
    }
    public String getCondition(){
        return this.condition;
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
