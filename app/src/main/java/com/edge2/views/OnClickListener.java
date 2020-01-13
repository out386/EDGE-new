package com.edge2.views;


import android.view.View;

import androidx.annotation.Nullable;

public interface OnClickListener {
    void onClick(int position, @Nullable View root, @Nullable View view1,
                 @Nullable View view2, @Nullable View view3);
}
