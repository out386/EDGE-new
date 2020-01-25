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
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.edge2.R;
import com.edge2.allevents.models.QuickItemModel;
import com.edge2.views.CustomViewOnClickedListener;
import com.edge2.views.OnClickListener;

import java.util.List;

public class QuickItemsAdapter extends RecyclerView.Adapter<QuickItemsAdapter.QuickItemsViewHolder> {
    private List<QuickItemModel> items;
    private CustomViewOnClickedListener listener;

    QuickItemsAdapter(List<QuickItemModel> items, @NonNull OnClickListener listener) {
        this.items = items;
        this.listener = new CustomViewOnClickedListener(listener);
    }

    @NonNull
    @Override
    public QuickItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.quick_items_item, parent, false);
        return new QuickItemsViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull QuickItemsViewHolder holder, int position) {
        QuickItemModel item = items.get(position);
        ImageView imageView = holder.itemImage;
        TextView nameView = holder.itemName;
        TextView descView = holder.itemDesc;
        View root = holder.rootView;

        ViewCompat.setTransitionName(imageView, "qimg" + position);
        ViewCompat.setTransitionName(nameView, "qname" + position);
        ViewCompat.setTransitionName(descView, "qdesc" + position);
        ViewCompat.setTransitionName(root, "qroot" + position);

        imageView.setImageDrawable(item.getImage());
        nameView.setText(item.getName());
        descView.setText(item.getDesc());
        holder.rootView.setOnClickListener(view ->
                listener.onClick(position, root, nameView, descView, imageView, null)
        );
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class QuickItemsViewHolder extends RecyclerView.ViewHolder {
        View rootView;
        ImageView itemImage;
        TextView itemName;
        TextView itemDesc;

        QuickItemsViewHolder(View item) {
            super(item);
            rootView = item;
            itemImage = item.findViewById(R.id.quick_image);
            itemName = item.findViewById(R.id.quick_name);
            itemDesc = item.findViewById(R.id.quick_desc);
        }
    }
}
