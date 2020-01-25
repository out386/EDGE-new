package com.edge2.transitions;

import android.widget.TextView;

import androidx.transition.ChangeBounds;
import androidx.transition.ChangeTransform;
import androidx.transition.TransitionSet;

public class MoveTransition extends TransitionSet {
    private TextResize textResize;
    private TextView tv;

    public MoveTransition(TextView v1) {
        setOrdering(ORDERING_TOGETHER);
        addTransition(new ChangeBounds())
                .addTransition(new ChangeTransform());
        if (v1 != null) {
            tv = v1;
            textResize = new TextResize();
            textResize.addTarget(v1);
            addTransition(textResize);
        }
    }

    public void onDestroy() {
        if (tv != null) {
            textResize.removeTarget(tv);
            textResize = null;
            tv = null;
        }
    }
}
