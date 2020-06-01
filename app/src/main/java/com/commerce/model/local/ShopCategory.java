package com.commerce.model.local;

import com.google.gson.annotations.SerializedName;

public class ShopCategory {

    @SerializedName("name")
    public String name;

    @SerializedName("image")
    public String imageName;

    @SerializedName("count")
    public String count;

    public ShopCategory(String name, String imageName, String count) {
        this.name = name;
        this.imageName = imageName;
        this.count = count;
    }

}
