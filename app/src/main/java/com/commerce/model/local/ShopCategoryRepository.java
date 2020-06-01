package com.commerce.model.local;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * Created by Panacea-Soft on 1/7/18.
 * Contact Email : teamps.is.cool@gmail.com
 * Website : http://www.panacea-soft.com
 */
public class ShopCategoryRepository {

    public static ArrayList<ShopCategory> getShopCategoryList() {
        return new Gson().fromJson(shopCategories, new TypeToken<ArrayList<ShopCategory>>() {}.getType());
    }

    static String shopCategories = "[\n" +
            "  {\n" +
            "    \"name\":\"Men Fashion\",\n" +
            "    \"image\":\"men_category\",\n" +
            "    \"count\":\"200\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\":\"Women Fashion\",\n" +
            "    \"image\":\"women_category\",\n" +
            "    \"count\":\"300\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\":\"Kids Fashion\",\n" +
            "    \"image\":\"kids_category\",\n" +
            "    \"count\":\"240\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\":\"Sport Ware Fashion\",\n" +
            "    \"image\":\"sport_category\",\n" +
            "    \"count\":\"250\"\n" +
            "  }\n" +
            "]";
}
