package com.commerce.model.local;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class BasketItemRepository {

    public static ArrayList<Basket> getBusketItemList() {
        return new Gson().fromJson(basketItems, new TypeToken<ArrayList<Basket>>() {}.getType());
    }

    static String basketItems = "[\n" +
            "  {\n" +
            "    \"id\":\"basket1\",\n" +
            "    \"name\":\"Unwind long sleeve tee\",\n" +
            "    \"image\":\"women_cloth_3\",\n" +
            "    \"currency\":\"S/\",\n" +
            "    \"price\":\"200\",\n" +
            "    \"category_name\":\"Clothing\",\n" +
            "    \"color\":\"Red\",\n" +
            "    \"size\":\"Medium\",\n" +
            "    \"shop\" : {\n" +
            "      \"shop_name\":\"Nike\",\n" +
            "      \"shop_email\":\"teamps.is.cool@gmail.com\",\n" +
            "      \"shop_phone\":\"+957777777\",\n" +
            "      \"shop_website\":\"www.panacea-soft.com\",\n" +
            "      \"shop_address\":\"Alkida'a Street , Ghamdhan balding, First floor\"\n" +
            "    }\n" +
            "  },\n" +
            "  {\n" +
            "    \"id\":\"basket2\",\n" +
            "    \"name\":\"Elephant Tee\",\n" +
            "    \"image\":\"women_cloth_2\",\n" +
            "    \"currency\":\"S/\",\n" +
            "    \"price\":\"250\",\n" +
            "    \"category_name\":\"Clothing\",\n" +
            "    \"color\":\"Green\",\n" +
            "    \"size\":\"Small\",\n" +
            "    \"shop\" : {\n" +
            "      \"shop_name\":\"Nike\",\n" +
            "      \"shop_email\":\"teamps.is.cool@gmail.com\",\n" +
            "      \"shop_phone\":\"+957777777\",\n" +
            "      \"shop_website\":\"www.panacea-soft.com\",\n" +
            "      \"shop_address\":\"Alkida'a Street , Ghamdhan balding, First floor\"\n" +
            "    }\n" +
            "  },\n" +
            "  {\n" +
            "    \"id\":\"basket3\",\n" +
            "    \"name\":\"Backpack bear long tee\",\n" +
            "    \"image\":\"men_cloth_5\",\n" +
            "    \"currency\":\"S/\",\n" +
            "    \"price\":\"300\",\n" +
            "    \"category_name\":\"Clothing\",\n" +
            "    \"color\":\"Blue\",\n" +
            "    \"size\":\"Large\",\n" +
            "    \"shop\" : {\n" +
            "      \"shop_name\":\"Nike\",\n" +
            "      \"shop_email\":\"teamps.is.cool@gmail.com\",\n" +
            "      \"shop_phone\":\"+957777777\",\n" +
            "      \"shop_website\":\"www.panacea-soft.com\",\n" +
            "      \"shop_address\":\"Alkida'a Street , Ghamdhan balding, First floor\"\n" +
            "    }\n" +
            "  },\n" +
            "  {\n" +
            "    \"id\":\"basket4\",\n" +
            "    \"name\":\"Infant plushee bear hoodie\",\n" +
            "    \"image\":\"kid_cloth_1\",\n" +
            "    \"currency\":\"S/\",\n" +
            "    \"price\":\"100\",\n" +
            "    \"category_name\":\"Clothing\",\n" +
            "    \"color\":\"Pink\",\n" +
            "    \"size\":\"Small\",\n" +
            "    \"shop\" : {\n" +
            "      \"shop_name\":\"Nike\",\n" +
            "      \"shop_email\":\"teamps.is.cool@gmail.com\",\n" +
            "      \"shop_phone\":\"+957777777\",\n" +
            "      \"shop_website\":\"www.panacea-soft.com\",\n" +
            "      \"shop_address\":\"Alkida'a Street , Ghamdhan balding, First floor\"\n" +
            "    }\n" +
            "  }\n" +
            "\n" +
            "]";

}
