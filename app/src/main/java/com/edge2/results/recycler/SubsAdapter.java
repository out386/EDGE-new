package com.edge2.results.recycler;

/*
 * Copyright (C) 2020 Ritayan Chakraborty <ritayanout@gmail.com>
 *
 * This file is part of EDGE-new
 *
 * EDGE-new is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * EDGE-new is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with EDGE-new.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.edge2.R;
import com.edge2.results.ResultsModel;
import com.edge2.views.CustomViewOnClickedListener;
import com.edge2.views.OnClickListener;

import java.util.List;

public class SubsAdapter extends RecyclerView.Adapter<SubsAdapter.EventsViewHolder> {
    private List<ResultsModel.ScreenItem> items;
    private CustomViewOnClickedListener listener;

    public SubsAdapter(List<ResultsModel.ScreenItem> items, @NonNull OnClickListener listener) {
        this.items = items;
        this.listener = new CustomViewOnClickedListener(listener);
    }

    @NonNull
    @Override
    public EventsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.events_item, parent, false);
        return new EventsViewHolder(item.findViewById(R.id.event_root));
    }

    @Override
    public void onBindViewHolder(@NonNull EventsViewHolder holder, int position) {
        ResultsModel.ScreenItem item = items.get(position);
        View root = holder.rootView;
        TextView name = holder.screenName;
        TextView desc = holder.screenDesc;
        ImageView imageView = holder.screenImage;
        String descStr = item.getDesc();

        name.setText(item.getName());
        if (descStr == null || "".equals(descStr)) {
            desc.setVisibility(View.GONE);
        } else {
            desc.setVisibility(View.VISIBLE);
            desc.setText(descStr);
        }

        imageView.setColorFilter(imageView.getContext().getColor(R.color.lIconFillRed));
        Glide.with(imageView.getContext())
                .load(item.getIconUriString())
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(imageView);

        ViewCompat.setTransitionName(imageView, "img" + position);
        ViewCompat.setTransitionName(name, "name" + position);
        ViewCompat.setTransitionName(root, "root" + position);

        root.setOnClickListener(view ->
                listener.onClick(position, root, imageView, name, desc, null)
        );
        holder.screenButton.setOnClickListener(view ->
                listener.onClick(position, root, imageView, name, desc, null)
        );
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public List<ResultsModel.ScreenItem> getItems() {
        return items;
    }

    static class EventsViewHolder extends RecyclerView.ViewHolder {
        View rootView;
        ImageView screenImage;
        ImageView screenButton;
        TextView screenName;
        TextView screenDesc;

        EventsViewHolder(View item) {
            super(item);
            rootView = item;
            screenImage = item.findViewById(R.id.event_image);
            screenButton = item.findViewById(R.id.event_button);
            screenName = item.findViewById(R.id.event_name);
            screenDesc = item.findViewById(R.id.event_num_sub);
        }
    }

}
