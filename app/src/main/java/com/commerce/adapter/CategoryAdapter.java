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

import com.commerce.model.local.ShopCategory;
import com.commerce.util.Utils;
import com.pax.jemv.demo.R;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ShopCategory> itemArrayList;
    private OnItemClickListener itemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, ShopCategory obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.itemClickListener = mItemClickListener;
    }

    public CategoryAdapter(ArrayList<ShopCategory> itemArrayList) {
        this.itemArrayList = itemArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false);

        return new PlaceViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder reholder, int position) {

        if(reholder instanceof PlaceViewHolder) {
            PlaceViewHolder holder = (PlaceViewHolder) reholder;
            ShopCategory item = itemArrayList.get(position);

            holder.categoryNameTextView.setText(item.name);

            String productCount = item.count + " Products";
            holder.productCountTextView.setText(productCount);

            Context context = holder.placeHolderCardView.getContext();

            int id = Utils.getDrawableInt(context, item.imageName);
            Utils.setImageToImageView(context, holder.itemImageView, id);


            holder.placeHolderCardView.setOnClickListener(view -> {
                if(itemClickListener != null) {
                    itemClickListener.onItemClick(view, itemArrayList.get(position), position);
                }
            });

        }

    }

    @Override
    public int getItemCount() {
        return itemArrayList.size();
    }

    public class PlaceViewHolder extends RecyclerView.ViewHolder {

        public CardView placeHolderCardView;
        public ImageView itemImageView;
        public TextView categoryNameTextView;
        public TextView productCountTextView;


        public PlaceViewHolder(View view) {
            super(view);

            categoryNameTextView = view.findViewById(R.id.categoryNameTextView);
            itemImageView = view.findViewById(R.id.itemImageView);
            productCountTextView = view.findViewById(R.id.productCountTextView);
            placeHolderCardView = view.findViewById(R.id.placeHolderCardView);
        }
    }

}
