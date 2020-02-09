package com.edge2.views.people;

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

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.edge2.R;

public class PeopleView extends LinearLayout {
    private Context context;
    private String name;
    private String role;
    private int imgRes;
    private String imgUrl;
    private long phoneNo;

    public PeopleView(Context context, PeopleModel member) {
        super(context);
        this.context = context;
        name = member.getName();
        role = member.getRole();
        imgRes = member.getImgRes();
        phoneNo = member.getPhoneNo();
        init();
    }

    public PeopleView(Context context, String name, String imgUrl) {
        super(context);
        this.context = context;
        this.name = name;
        this.imgUrl = imgUrl;
        init();
    }

    private void init() {
        inflate(context, R.layout.people_item, this);
        ImageView imgView = findViewById(R.id.team_img);
        TextView nameView = findViewById(R.id.team_name);
        TextView roleView = findViewById(R.id.team_role);
        LinearLayout phWaHolder = findViewById(R.id.team_ph_wa_holder);
        ImageView phView = findViewById(R.id.team_ph);
        ImageView waView = findViewById(R.id.team_wa);
        nameView.setText(name);
        if (imgUrl == null) {
            imgView.setImageResource(imgRes);
        } else {
            Glide.with(context)
                    .load(imgUrl)
                    .placeholder(R.drawable.loading)
                    .into(imgView);
        }

        if (role != null) {
            roleView.setText(role);
            roleView.setVisibility(VISIBLE);
        }

        if (phoneNo != 0) {
            phWaHolder.setVisibility(VISIBLE);

            waView.setOnClickListener(view -> {
                Intent i = new Intent("android.intent.action.MAIN");
                i.setComponent(
                        new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
                i.putExtra("jid", "91" + phoneNo + "@s.whatsapp.net");
                PackageManager pm = context.getPackageManager();
                if (pm.resolveActivity(i, PackageManager.MATCH_DEFAULT_ONLY) != null)
                    context.startActivity(i);
                else
                    Toast.makeText(context, "WhatsApp is not installed.", Toast.LENGTH_SHORT)
                            .show();
            });
            phView.setOnClickListener(view -> {
                Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNo));
                PackageManager pm = context.getPackageManager();
                if (i.resolveActivity(pm) != null)
                    context.startActivity(i);
                else
                    Toast.makeText(context, "No phone app found.", Toast.LENGTH_SHORT)
                            .show();
            });
        }
    }
}
