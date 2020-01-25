package com.edge2.team;

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

import com.edge2.R;

public class MemberView extends LinearLayout {
    private Context context;
    private String name;
    private String role;
    private int imgRes;
    private long phoneNo;

    public MemberView(Context context, Data.MemberModel member) {
        super(context);
        this.context = context;
        name = member.name;
        role = member.role;
        imgRes = member.imgRes;
        phoneNo = member.phoneNo;
        init();
    }

    private void init() {
        inflate(context, R.layout.team_item, this);
        ImageView imgView = findViewById(R.id.team_img);
        TextView nameView = findViewById(R.id.team_name);
        TextView roleView = findViewById(R.id.team_role);
        ImageView phView = findViewById(R.id.team_ph);
        ImageView waView = findViewById(R.id.team_wa);
        imgView.setImageResource(imgRes);
        nameView.setText(name);
        roleView.setText(role);

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
