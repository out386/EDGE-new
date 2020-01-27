package com.edge2.upcoming.recycler;

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

import android.net.Uri;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.edge2.R;
import com.edge2.allevents.models.BannerItemsModel;
import com.edge2.html.RulesTagHandler;
import com.edge2.views.CustomViewOnClickedListener;
import com.edge2.views.OnClickListener;

import java.util.List;

public class UpcomingAdapter extends RecyclerView.Adapter<UpcomingAdapter.EventsViewHolder> {
    private List<BannerItemsModel> items;
    private CustomViewOnClickedListener listener;

    public UpcomingAdapter(List<BannerItemsModel> items, @NonNull OnClickListener listener) {
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
        BannerItemsModel item = items.get(position);
        View root = holder.rootView;
        TextView name = holder.eventName;
        TextView desc = holder.eventDesc;
        ImageView imageView = holder.eventIcon;
        ImageView button = holder.eventButton;
        name.setText(item.getName());

        Html.TagHandler tagHandler = new RulesTagHandler(desc.getContext(), R.style.TextHeaderMid);
        Spanned descText =  HtmlCompat.fromHtml(
                item.getDesc(), HtmlCompat.FROM_HTML_MODE_COMPACT, null, tagHandler);
        desc.setText(descText);
        desc.setMaxLines(3);

        ViewCompat.setTransitionName(imageView, "upcicon" + position);
        ViewCompat.setTransitionName(name, "upcname" + position);
        ViewCompat.setTransitionName(desc, "upcdesc" + position);
        ViewCompat.setTransitionName(root, "upcroot" + position);
        ViewCompat.setTransitionName(button, "upcbutton" + position);

        Uri iconUri = item.getIconUri();
        if (iconUri != null) {
            Glide.with(imageView.getContext())
                    .load(iconUri)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(imageView);
        } else {
            imageView.setImageResource(R.drawable.ic_edge_large);
        }

        root.setOnClickListener(view ->
                listener.onClick(position, root, imageView, name, desc, button)
        );
        holder.eventButton.setOnClickListener(view ->
                listener.onClick(position, root, imageView, name, desc, button)
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
