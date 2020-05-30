package com.culqi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.otc.model.response.retrieve.TransactionsItem;
import com.pax.jemv.demo.R;

import java.util.List;

public class SalesTodayAdapter extends BaseAdapter {

    List<TransactionsItem> items;
    private Context context;


    public SalesTodayAdapter(Context context, List<TransactionsItem> items){
        this.context = context;
        this.items = items;
    }


    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return this.items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).
                    inflate(R.layout.layout_item_sales, viewGroup, false);
        }

        TransactionsItem currentItem = (TransactionsItem) getItem(position);

        ImageView ivSalesBrand = convertView.findViewById(R.id.iv_sales_brand);
        ImageView ivSalesState = convertView.findViewById(R.id.iv_sales_state);
        TextView tvCardNumber = convertView.findViewById(R.id.tv_sales_card_number);
        TextView tvBanck = convertView.findViewById(R.id.tv_sales_bank);
        TextView tvAmount = convertView.findViewById(R.id.tv_sales_amount);
        TextView tvState = convertView.findViewById(R.id.tv_sales_state);


        String status = "unknow";
        if (currentItem.getStatus().equals("AUTHORIZED")) {
            status = "Aprobado";
            ivSalesState.setImageResource(R.drawable.ic_state_approved_circle);
        }

        if (currentItem.getStatus().equals("VOIDED")) {
            status = "Anulado";
            ivSalesState.setImageResource(R.drawable.ic_state_voided_circle);
        }

        if (currentItem.getStatus().equals("DENIED")) {
            status = "Denegado";
            ivSalesState.setImageResource(R.drawable.ic_state_denied_circle);
        }

        // brand
        if (currentItem.getBrand().equals("visa")) {
            ivSalesBrand.setImageResource(R.drawable.ic_visa);
        }

        if (currentItem.getBrand().equals("mastercard")) {
            ivSalesBrand.setImageResource(R.drawable.ic_mastercard);
        }


        tvCardNumber.setText(currentItem.getLast4digits());
        tvBanck.setText(currentItem.getAcquirer());
        tvAmount.setText("S/ " + currentItem.getOrderAmount());
        tvState.setText(status);


        return convertView;
    }

}
