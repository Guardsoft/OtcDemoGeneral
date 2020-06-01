package com.commerce.model.local;

import com.google.gson.annotations.SerializedName;

public class Basket {

    @SerializedName("name")
    public String name;

    @SerializedName("image")
    public String image;

    @SerializedName("currency")
    public String currency;

    @SerializedName("price")
    public String price;

    @SerializedName("category_name")
    public String category_name;

    @SerializedName("color")
    public String color;

    @SerializedName("size")
    public String size;

    @SerializedName("shop")
    public Basket.Shop shop;

    public Basket(String name, String image, String currency, String price, String category_name, String color, String size, Shop shop) {
        this.name = name;
        this.image = image;
        this.currency = currency;
        this.price = price;
        this.category_name = category_name;
        this.color = color;
        this.size = size;
        this.shop = shop;
    }

    public class Shop {
        @SerializedName("shop_name")
        public String shopName;

        @SerializedName("shop_email")
        public String shopEmail;

        @SerializedName("shop_phone")
        public String shopPhone;

        @SerializedName("shop_website")
        public String shopWebsite;

        @SerializedName("shop_address")
        public String shopAddress;
    }

}
