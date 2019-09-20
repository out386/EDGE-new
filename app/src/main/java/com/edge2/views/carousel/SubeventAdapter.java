package com.edge2.views.carousel;

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

import com.bumptech.glide.Glide;
import com.edge2.R;

import java.util.List;

public class SubeventAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<SubeventNameModel> items;
    private OnItemClickListener listener;

    SubeventAdapter(List<SubeventNameModel> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? 0 : 1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                      int viewType) {
        if (viewType != 0) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.carousel_play_image_item, parent, false);

            return new EventsViewHolder(v);
        }

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.carousel_blank_item, parent, false);
        return new BlankViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof EventsViewHolder) {
            EventsViewHolder vh = (EventsViewHolder) holder;
            SubeventNameModel item = items.get(position);

            Glide.with(vh.image.getContext())
                    .load(item.getImg())
                    .into(vh.image);
            vh.caption.setText(item.getName());
            vh.root.setOnClickListener(v -> {
                if (listener != null)
                    listener.onItemClicked(item);
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public interface OnItemClickListener {
        void onItemClicked(SubeventNameModel item);
    }

    class EventsViewHolder extends RecyclerView.ViewHolder {
        private TextView caption;
        private ImageView image;
        private View root;

        EventsViewHolder(View v) {
            super(v);
            caption = v.findViewById(R.id.carousel_caption);
            image = v.findViewById(R.id.carousel_image);
            root = v;
        }
    }

    class BlankViewHolder extends RecyclerView.ViewHolder {
        BlankViewHolder(View v) {
            super(v);
        }
    }
}
