package com.edge2.eventdetails;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edge2.R;

@SuppressWarnings("ViewConstructor")
class ContactsView extends RelativeLayout {
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
        ImageView wpView = v.findViewById(R.id.contacts_whatsapp);
        ImageView phView = v.findViewById(R.id.contacts_phone);
        TextView nameTv = v.findViewById(R.id.contacts_name);
        TextView numberTv = v.findViewById(R.id.contacts_number);

        nameTv.setText(name);
        String numStr = "+91 " + number;
        numberTv.setText(numStr);

        wpView.setOnClickListener(view -> {
            Intent sendIntent = new Intent("android.intent.action.MAIN");
            sendIntent.setComponent(
                    new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
            sendIntent.putExtra("jid", "91" + number + "@s.whatsapp.net");
            context.startActivity(sendIntent);
        });
        phView.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number));
            context.startActivity(intent);
        });

        if (isNotLast) {
            View divider = v.findViewById(R.id.contacts_divider);
            divider.setVisibility(VISIBLE);
        }

    }
}
