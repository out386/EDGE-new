package com.edge2.results.recycler.views;

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
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edge2.R;
import com.edge2.results.ResultsModel;
import com.edge2.utils.Misc;

public class MemberView extends RelativeLayout {
    public MemberView(Context context) {
        super(context, null);
    }

    public MemberView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public MemberView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr, 0);
    }

    public MemberView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public MemberView(Context context, ResultsModel.Member member) {
        super(context);
        init(member);
    }

    private void init(ResultsModel.Member member) {
        if (member == null)
            return;

        View v = inflate(getContext(), R.layout.result_member_item, this);
        TextView nameTv = v.findViewById(R.id.res_mem_name);
        TextView detailsTv = v.findViewById(R.id.res_mem_details);

        nameTv.setText(member.getName());
        String details = getDetailsString(member);
        if (details == null) {
            detailsTv.setVisibility(GONE);
        } else {
            detailsTv.setText(details);
            detailsTv.setVisibility(VISIBLE);
        }
    }

    private String getDetailsString(ResultsModel.Member member) {
        StringBuilder college = new StringBuilder();
        StringBuilder stream = new StringBuilder();

        if (member.getDept() != null || member.getYear() != 0) {
            stream.append("- ");
            if (member.getDept() != null) {
                stream.append(member.getDept());
                if (member.getYear() > 0) {
                    stream.append(", ")
                            .append(Misc.numToStr(member.getYear()))
                            .append(" year");
                }
            } else if (member.getYear() > 0) {
                stream.append(Misc.numToStr(member.getYear()))
                        .append(" year");
            }
        }

        if (member.getClg() != null) {
            college.append("- ");
            college.append(member.getClg());
            if (stream.length() > 0)
                college.append("\n")
                        .append(stream);
        } else {
            if (stream.length() > 0)
                college.append(stream);
        }
        return college.length() == 0 ? null : college.toString();
    }
}
