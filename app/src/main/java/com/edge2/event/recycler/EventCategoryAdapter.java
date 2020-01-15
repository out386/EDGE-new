package com.edge2.event.recycler;

/*
 * Copyright (C) 2019 Ritayan Chakraborty <ritayanout@gmail.com>
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
import com.edge2.R;
import com.edge2.event.EventCategoryModel;
import com.edge2.views.CustomViewOnClickedListener;
import com.edge2.views.OnClickListener;

import java.util.List;

public class EventCategoryAdapter extends RecyclerView.Adapter<EventCategoryAdapter.EventsViewHolder> {
    private List<EventCategoryModel> items;
    private CustomViewOnClickedListener listener;

    public EventCategoryAdapter(List<EventCategoryModel> items, @NonNull OnClickListener listener) {
        this.items = items;
        this.listener = new CustomViewOnClickedListener(listener);
    }

    @NonNull
    @Override
    public EventsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_category_item, parent, false);
        return new EventsViewHolder(item.findViewById(R.id.eventcat_root));
    }

    @Override
    public void onBindViewHolder(@NonNull EventsViewHolder holder, int position) {
        EventCategoryModel item = items.get(position);
        View root = holder.rootView;
        TextView name = holder.eventName;
        TextView count = holder.eventDesc;
        ImageView imageView = holder.eventIcon;
        holder.eventName.setText(item.getName());
        holder.eventDesc.setText(item.getDesc());

        ViewCompat.setTransitionName(imageView, "caticon" + position);
        ViewCompat.setTransitionName(name, "catname" + position);
        ViewCompat.setTransitionName(count, "catdesc" + position);
        ViewCompat.setTransitionName(root, "catroot" + position);

        Glide.with(imageView.getContext())
                .load(item.getIcon())
                .into(imageView);

        root.setOnClickListener(view ->
                listener.onClick(position, root, imageView, name, count)
        );
        holder.eventButton.setOnClickListener(view ->
                listener.onClick(position, root, imageView, name, count)
        );
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class EventsViewHolder extends RecyclerView.ViewHolder {
        View rootView;
        ImageView eventIcon;
        ImageView eventButton;
        TextView eventName;
        TextView eventDesc;

        EventsViewHolder(View item) {
            super(item);
            rootView = item;
            eventIcon = item.findViewById(R.id.eventcat_icon);
            eventButton = item.findViewById(R.id.eventcat_button);
            eventName = item.findViewById(R.id.eventcat_name);
            eventDesc = item.findViewById(R.id.eventcat_desc);
        }
    }

}
