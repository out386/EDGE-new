package com.edge2.views;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.edge2.R;

@SuppressWarnings("ViewConstructor")
public class ContactsView extends RelativeLayout {
    private Context context;
    private String name;
    private long number;
    private boolean isNotLast;

    public ContactsView(Context context, String name, long number, boolean isNotLast) {
        super(context, null);
        this.context = context;
        this.number = number;
        this.name = name;
        this.isNotLast = isNotLast;
        init();
    }

    private void init() {
        View v = inflate(context, R.layout.event_details_contacts_item, this);
        ImageView waView = v.findViewById(R.id.contacts_whatsapp);
        ImageView phView = v.findViewById(R.id.contacts_phone);
        TextView nameTv = v.findViewById(R.id.contacts_name);
        TextView numberTv = v.findViewById(R.id.contacts_number);

        nameTv.setText(name);
        String numStr = "+91 " + number;
        numberTv.setText(numStr);

        waView.setOnClickListener(view -> {
            Intent i = new Intent("android.intent.action.MAIN");
            i.setComponent(
                    new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
            i.putExtra("jid", "91" + number + "@s.whatsapp.net");
            PackageManager pm = context.getPackageManager();
            if (pm.resolveActivity(i, PackageManager.MATCH_DEFAULT_ONLY) != null)
                context.startActivity(i);
            else
                Toast.makeText(context, "WhatsApp is not installed.", Toast.LENGTH_SHORT)
                        .show();
        });
        phView.setOnClickListener(view -> {
            Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number));
            PackageManager pm = context.getPackageManager();
            if (i.resolveActivity(pm) != null)
                context.startActivity(i);
            else
                Toast.makeText(context, "No phone app found.", Toast.LENGTH_SHORT)
                        .show();
        });

        if (isNotLast) {
            View divider = v.findViewById(R.id.contacts_divider);
            divider.setVisibility(VISIBLE);
        }

    }
}
