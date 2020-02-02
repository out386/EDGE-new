package com.edge2.views;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.edge2.R;

@SuppressWarnings("ViewConstructor")
public class ContactsView extends RelativeLayout {
    private Context context;
    private String name;
    private long number;
    private boolean isNotLast;

    public ContactsView(Context context) {
        this(context, null);
    }

    public ContactsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ContactsView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ContactsView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        setFromAttrs(attrs);
        init(null);
    }

    public ContactsView(Context context, String name, long number, boolean isNotLast) {
        this(context, name, null, number, isNotLast);
    }

    public ContactsView(Context context, String name, String desc, long number, boolean isNotLast) {
        super(context, null);
        this.context = context;
        this.number = number;
        this.name = name;
        this.isNotLast = isNotLast;
        init(desc);
    }

    private void setFromAttrs(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs,
                R.styleable.ContactsView, 0, 0);
        name = a.getString(R.styleable.ContactsView_contact_name);
        String numStr = a.getString(R.styleable.ContactsView_contact_number);
        if (numStr != null)
            number = Long.parseLong(numStr);
        isNotLast = a.getBoolean(R.styleable.ContactsView_is_not_last, false);
        a.recycle();
    }

    private void init(@Nullable String desc) {
        View v = inflate(context, R.layout.contacts_item, this);
        ImageView waView = v.findViewById(R.id.contacts_whatsapp);
        ImageView phView = v.findViewById(R.id.contacts_phone);
        TextView nameTv = v.findViewById(R.id.contacts_name);
        TextView numberTv = v.findViewById(R.id.contacts_number);

        nameTv.setText(name);
        if (desc == null) {
            if (number == -1) {
                numberTv.setVisibility(GONE);
            } else {
                String numStr = "+91 " + number;
                numberTv.setText(numStr);
            }
        } else {
            numberTv.setText(desc);
        }

        if (number == -1) {
            waView.setVisibility(GONE);
            phView.setVisibility(GONE);
        } else {
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
        }

        if (isNotLast) {
            View divider = v.findViewById(R.id.contacts_divider);
            divider.setVisibility(VISIBLE);
        }

    }
}
