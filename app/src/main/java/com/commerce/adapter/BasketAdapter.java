package com.commerce.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.commerce.model.local.Basket;
import com.commerce.util.Utils;
import com.pax.jemv.demo.R;

import java.util.List;

public class BasketAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<Basket> basketList;
    private OnItemClickListener itemClickListener;
    private String currency;
    private int total = 0;

    public interface OnItemClickListener {
        void onItemClick(View view, Basket obj, int position);

        void onDeleteClick(View view, Basket obj, int position);

        void onPriceChange(String currency, int subTotal);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.itemClickListener = mItemClickListener;
    }

    public BasketAdapter(List<Basket> basketList) {
        this.basketList = basketList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.basket_item, parent, false);

        return new PlaceViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        if (viewHolder instanceof PlaceViewHolder) {

            Basket basket = basketList.get(position);
            currency = basket.currency;
            PlaceViewHolder holder = (PlaceViewHolder) viewHolder;
            holder.itemNameTextView.setText(basket.name);

            Context context = holder.holderCardView.getContext();

            int id = Utils.getDrawableInt(context, basket.image);
            Utils.setImageToImageView(context, holder.itemImageView, id);


            String priceStr = basket.currency + " " + basket.price;
            holder.priceTextView.setText(priceStr);

            holder.attributeTextView.setText(basket.size);

            try {
                int value = Integer.parseInt(holder.qtyTextView.getText().toString());
                int price = Integer.parseInt(basket.price);
                int subTotal = value * price;
                String subTotalStr = basket.currency + " " + subTotal;

                total += subTotal;

                holder.subTotalTextView.setText(subTotalStr);
            }catch (Exception ignored) {}

            holder.minusImageView.setOnClickListener(v -> {

                try {
                    int value = Integer.parseInt(holder.qtyTextView.getText().toString());

                    if (value > 1) {
                        value -= 1;
                    }

                    holder.qtyTextView.setText(String.valueOf(value));

                    String itemPriceStr = holder.priceTextView.getText().toString();
                    if(!itemPriceStr.equals("")) {

                        int price = convertPriceStrToInt(itemPriceStr);
                        int originalSubTotal = convertPriceStrToInt(holder.subTotalTextView.getText().toString());
                        total -= originalSubTotal;

                        int subTotal = value * price;
                        String subTotalStr = basket.currency + " " + subTotal;
                        holder.subTotalTextView.setText(subTotalStr);

                        total += subTotal;
                        itemClickListener.onPriceChange(basket.currency, total);

                    }

                } catch (Exception ignored) {
                }
            });

            holder.plusImageView.setOnClickListener(v -> {

                try {
                    int value = Integer.parseInt(holder.qtyTextView.getText().toString());

                    value += 1;

                    holder.qtyTextView.setText(String.valueOf(value));

                    String itemPriceStr = holder.priceTextView.getText().toString();
                    if(!itemPriceStr.equals("")) {

                        int price = convertPriceStrToInt(itemPriceStr);
                        int originalSubTotal = convertPriceStrToInt(holder.subTotalTextView.getText().toString());
                        total -= originalSubTotal;

                        int subTotal = value * price;
                        String subTotalStr = basket.currency + " " + subTotal;
                        holder.subTotalTextView.setText(subTotalStr);

                        total += subTotal;
                        itemClickListener.onPriceChange(basket.currency, total);

                    }

                } catch (Exception ignored) {
                }
            });

        }
    }

    private int convertPriceStrToInt(String priceStr) {
        int price = 0;
        try {
            String lPriceStr = priceStr.replace(currency,"").replace(" ","");
            price = Integer.parseInt(lPriceStr);
        }catch (Exception ignored){}

        return price;
    }
    @Override
    public int getItemCount() {
        return basketList.size();
    }

    public class PlaceViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImageView;
        TextView itemNameTextView;
        TextView priceTextView;
        CardView holderCardView;
        ImageView deleteImageView;
        TextView subTotalTextView;
        TextView attributeTextView;
        ImageView minusImageView;
        ImageView plusImageView;
        TextView qtyTextView;

        PlaceViewHolder(View view) {
            super(view);

            itemImageView = view.findViewById(R.id.itemImageView);
            itemNameTextView = view.findViewById(R.id.itemNameTextView);
            priceTextView = view.findViewById(R.id.priceTextView);
            holderCardView = view.findViewById(R.id.holderCardView);
            deleteImageView = view.findViewById(R.id.deleteImageView);
            subTotalTextView = view.findViewById(R.id.subTotalTextView);
            attributeTextView = view.findViewById(R.id.attributeTextView);
            minusImageView = view.findViewById(R.id.minusImageView);
            plusImageView = view.findViewById(R.id.plusImageView);
            qtyTextView = view.findViewById(R.id.qtyTextView);

        }
    }

}
