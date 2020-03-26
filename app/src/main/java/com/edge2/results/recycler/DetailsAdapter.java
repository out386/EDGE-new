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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.edge2.R;
import com.edge2.results.ResultsModel;

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
        holder.pos.setText(String.valueOf(item.getRank()));
        if (item.getTName() != null && !item.getTName().isEmpty()) {
            holder.team.setVisibility(View.VISIBLE);
            holder.team.setText(item.getTName());
        } else {
            holder.team.setVisibility(View.GONE);
        }

        List<ResultsModel.Member> members = item.getMembers();
        StringBuilder nameStr = new StringBuilder(200);
        nameStr.append("<ul>");
        for (ResultsModel.Member member : members) {
            nameStr.append("<li>");
            nameStr.append(member.getName());
            nameStr.append("</li>");
        }
        nameStr.append("</ul>");

        holder.names.setText(HtmlCompat.fromHtml(nameStr.toString(), HtmlCompat.FROM_HTML_MODE_COMPACT));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ResultsViewHolder extends RecyclerView.ViewHolder {
        TextView pos;
        TextView team;
        TextView names;

        ResultsViewHolder(@NonNull View item) {
            super(item);
            this.pos = item.findViewById(R.id.res_pos);
            this.team = item.findViewById(R.id.res_team);
            this.names = item.findViewById(R.id.res_names);
        }
    }

}
