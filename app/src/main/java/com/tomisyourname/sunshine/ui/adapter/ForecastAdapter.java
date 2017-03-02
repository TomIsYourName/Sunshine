package com.tomisyourname.sunshine.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tomisyourname.sunshine.R;
import com.tomisyourname.sunshine.ui.DetailActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zain on 02/03/2017.
 */

public class ForecastAdapter extends RecyclerView.Adapter {

  private List<String> data;

  public ForecastAdapter(List<String> data) {
    this.data = data;
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View convertView = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.list_item_forecast, null);
    return new ItemViewHolder(convertView);
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
    final String forecast = data.get(position);
    final Context context = itemViewHolder.convertView.getContext();
    itemViewHolder.textView.setText(forecast);
    itemViewHolder.convertView.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT, forecast);
        context.startActivity(intent);
      }

    });

  }

  @Override
  public int getItemCount() {
    return data == null ? 0 : data.size();
  }

  public void clear() {
    if(data != null) {
      data.clear();
    } else {
      data = new ArrayList<>();
    }
  }

  public void addAll(List<String> newData) {
    if(data != null) {
      data.addAll(newData);
    } else {
      data = newData;
    }
  }

  class ItemViewHolder extends RecyclerView.ViewHolder {

    public final View convertView;
    public final TextView textView;

    public ItemViewHolder(View itemView) {
      super(itemView);
      convertView = itemView;
      textView = (TextView) convertView.findViewById(R.id.tv_list_item_forecast);
    }
  }

}
