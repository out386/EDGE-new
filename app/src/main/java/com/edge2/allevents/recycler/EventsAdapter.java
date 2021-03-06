package com.edge2.allevents.recycler;

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

import com.edge2.R;
import com.edge2.allevents.models.GroupsModel;
import com.edge2.views.CustomViewOnClickedListener;
import com.edge2.views.OnClickListener;

import java.util.List;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventsViewHolder> {
    private List<GroupsModel> items;
    private boolean isIntra;
    private CustomViewOnClickedListener listener;

    public EventsAdapter(List<GroupsModel> items, boolean isIntra, @NonNull OnClickListener listener) {
        this.items = items;
        this.isIntra = isIntra;
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
        GroupsModel item = items.get(position);
        View root = holder.rootView;
        TextView name = holder.eventName;
        TextView count = holder.eventSubCount;
        ImageView imageView = holder.eventImage;
        holder.eventName.setText(item.getName());
        String countStr = isIntra ? item.getNumEventsIntra() : item.getNumEventsEdge();
        holder.eventSubCount.setText(countStr);
        imageView.setImageResource(item.getImage());

        ViewCompat.setTransitionName(imageView, "img" + position);
        ViewCompat.setTransitionName(name, "name" + position);
        ViewCompat.setTransitionName(root, "root" + position);

        root.setOnClickListener(view ->
                listener.onClick(position, root, imageView, name, count, null)
        );
        holder.eventButton.setOnClickListener(view ->
                listener.onClick(position, root, imageView, name, count, null)
        );
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class EventsViewHolder extends RecyclerView.ViewHolder {
        View rootView;
        ImageView eventImage;
        ImageView eventButton;
        TextView eventName;
        TextView eventSubCount;

        EventsViewHolder(View item) {
            super(item);
            rootView = item;
            eventImage = item.findViewById(R.id.event_image);
            eventButton = item.findViewById(R.id.event_button);
            eventName = item.findViewById(R.id.event_name);
            eventSubCount = item.findViewById(R.id.event_num_sub);
        }
    }

}
