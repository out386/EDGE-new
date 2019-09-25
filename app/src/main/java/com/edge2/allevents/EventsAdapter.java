package com.edge2.allevents;

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
import androidx.recyclerview.widget.RecyclerView;

import com.edge2.R;
import com.edge2.allevents.models.EventModel;

import java.util.List;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventsViewHolder> {
    private List<EventModel> items;
    private OnEventsClickedListener listener;

    EventsAdapter(List<EventModel> items, OnEventsClickedListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EventsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.events_item, parent, false);
        return new EventsViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull EventsViewHolder holder, int position) {
        EventModel item = items.get(position);
        ImageView imageView = holder.eventImage;
        imageView.setImageDrawable(item.getImage());
        holder.eventName.setText(item.getName());
        holder.eventSubCount.setText(item.getNumEvents());
        holder.rootView.setOnClickListener(view ->
                listener.onEventClicked(position)
        );
        holder.eventButton.setOnClickListener(view ->
                listener.onEventClicked(position)
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

    interface OnEventsClickedListener {
        void onEventClicked(int position);
    }
}
