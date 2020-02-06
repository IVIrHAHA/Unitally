package com.example.unitally.tools;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

public class ExpandViewBehaviour extends CoordinatorLayout.Behavior {

    public ExpandViewBehaviour(){}

    public ExpandViewBehaviour(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onDependentViewChanged(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull View dependency) {
        return super.onDependentViewChanged(parent, child, dependency);
    }
}
