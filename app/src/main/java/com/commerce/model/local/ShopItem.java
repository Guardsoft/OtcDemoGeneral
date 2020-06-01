package com.commerce.model.local;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ShopItem {

    @SerializedName("name")
    public String name;

    @SerializedName("image")
    public String imageName;

    @SerializedName("currency")
    public String currency;

    @SerializedName("price")
    public String price;

    @SerializedName("original_price")
    public String originalPrice;

    @SerializedName("category_name")
    public String categoryName;

    @SerializedName("rating_count")
    public String ratingCount;

    @SerializedName("total_rating")
    public String totalRating;

    @SerializedName("discount")
    public String discount;

    @SerializedName("is_liked")
    public Boolean isLiked;

    @SerializedName("description")
    public String description;

    @SerializedName("image_list")
    public List<ShopItem.Image> imageList;

    @SerializedName("shop")
    public ShopItem.Shop shop;

    @SerializedName("view_count")
    public String viewCount;

    public ShopItem(String name, String imageName, String currency, String price, String originalPrice, String categoryName, String ratingCount, String totalRating, String discount, Boolean isLiked, String description, List<Image> imageList, Shop shop, String viewCount) {
        this.name = name;
        this.imageName = imageName;
        this.currency = currency;
        this.price = price;
        this.originalPrice = originalPrice;
        this.categoryName = categoryName;
        this.ratingCount = ratingCount;
        this.totalRating = totalRating;
        this.discount = discount;
        this.isLiked = isLiked;
        this.description = description;
        this.imageList = imageList;
        this.shop = shop;
        this.viewCount = viewCount;
    }

    public class Image {
        @SerializedName("image_name")
        public String imageName;
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
