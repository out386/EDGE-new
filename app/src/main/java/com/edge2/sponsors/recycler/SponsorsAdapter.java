package com.edge2.sponsors.recycler;

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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.edge2.R;
import com.edge2.sponsors.SponsorsModel;

import java.util.List;

public class SponsorsAdapter extends RecyclerView.Adapter<SponsorsAdapter.SponsorsViewHolder> {
    private List<SponsorsModel> items;

    public SponsorsAdapter(List<SponsorsModel> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public SponsorsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sponsors_item, parent, false);
        return new SponsorsViewHolder(item.findViewById(R.id.event_root));
    }

    @Override
    public void onBindViewHolder(@NonNull SponsorsViewHolder holder, int position) {
        SponsorsModel item = items.get(position);
        ImageView imageView = holder.sponsorImage;
        TextView roleView = holder.sponsorRole;

        holder.sponsorName.setText(item.getName());

        String role = item.getRole();
        if (role != null) {
            roleView.setText(role);
            roleView.setVisibility(View.VISIBLE);
        } else {
            roleView.setText(null);
            roleView.setVisibility(View.GONE);
        }

        Glide.with(imageView.getContext())
                .load(item.getImgUrl())
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(imageView);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class SponsorsViewHolder extends RecyclerView.ViewHolder {
        ImageView sponsorImage;
        TextView sponsorName;
        TextView sponsorRole;

        SponsorsViewHolder(View item) {
            super(item);
            sponsorImage = item.findViewById(R.id.sponsor_image);
            sponsorName = item.findViewById(R.id.sponsor_name);
            sponsorRole = item.findViewById(R.id.sponsor_role);
        }
    }

}
