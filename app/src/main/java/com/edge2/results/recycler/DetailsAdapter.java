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

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.edge2.BuildConfig;
import com.edge2.R;
import com.edge2.results.ResultsModel;
import com.edge2.results.recycler.views.MemberView;
import com.edge2.utils.Misc;

import java.util.List;

public class DetailsAdapter extends RecyclerView.Adapter<DetailsAdapter.ResultsViewHolder> {
    private List<ResultsModel.Result> items;

    public DetailsAdapter(List<ResultsModel.Result> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ResultsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_results_details, parent, false);
        return new ResultsViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultsViewHolder holder, int position) {
        ResultsModel.Result item = items.get(position);
        holder.pos.setText(Misc.numToStr(item.getRank()));
        if (item.getTName() != null && !item.getTName().isEmpty()) {
            holder.team.setVisibility(View.VISIBLE);
            holder.team.setText(item.getTName());
        } else {
            holder.team.setVisibility(View.GONE);
        }

        List<ResultsModel.Member> members = item.getMembers();
        Context context = holder.memberHolder.getContext();
        holder.memberHolder.removeAllViews();

        for (int i = 0; i < members.size(); i++) {
            MemberView memberView = new MemberView(context, members.get(i));
            holder.memberHolder.addView(memberView);
        }
        setImages(item.getImgs(), holder.memberHolder);
    }

    private void setImages(List<String> imgUrls, ViewGroup root) {
        if (imgUrls != null && imgUrls.size() > 0) {
            Context context = root.getContext();
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            int margin = context.getResources().getDimensionPixelSize(R.dimen.margin_large);
            int marginLarge = context.getResources().getDimensionPixelSize(R.dimen.margin_huge);
            int maxWidth = context.getResources().getDimensionPixelSize(R.dimen.full_width_img_max_width);
            params.setMargins(0, marginLarge, 0, margin);
            params.gravity = Gravity.CENTER_HORIZONTAL;

            for (String fileName : imgUrls) {
                String url = BuildConfig.URL_RESULT_PICS + fileName;
                ImageView imv = getResultImage(context, params, maxWidth);
                Glide.with(root.getContext())
                        .load(url)
                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                        .into(imv);
                root.addView(imv);
            }
        }
    }

    private ImageView getResultImage(Context context, LinearLayout.LayoutParams params, int maxWidth) {
        ImageView imv = new ImageView(context);
        imv.setLayoutParams(params);
        imv.setAdjustViewBounds(true);
        imv.setMaxWidth(maxWidth);
        return imv;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ResultsViewHolder extends RecyclerView.ViewHolder {
        TextView pos;
        TextView team;
        LinearLayout memberHolder;

        ResultsViewHolder(@NonNull View item) {
            super(item);
            this.pos = item.findViewById(R.id.res_pos);
            this.team = item.findViewById(R.id.res_team);
            this.memberHolder = item.findViewById(R.id.res_member_holder);
        }
    }

}
