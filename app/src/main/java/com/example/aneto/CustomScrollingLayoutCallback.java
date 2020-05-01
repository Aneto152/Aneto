package com.example.aneto;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.wear.widget.WearableLinearLayoutManager;

public class CustomScrollingLayoutCallback extends WearableLinearLayoutManager.LayoutCallback{
    /** How much should we scale the icon at most. */
    private static final float MAX_ICON_PROGRESS = 0.99f;
    private static final float PADDING = 0.5f;

    private float progressToCenter;

    @Override
    public void onLayoutFinished(View child, RecyclerView parent) {

        // Figure out % progress from top to bottom
        float centerOffset = ((float) child.getHeight() / 2.0f) / (float) parent.getHeight();
        float yRelativeToCenterOffset = (child.getY() / parent.getHeight()) + centerOffset;

        //float bordHaut = child.getY() / parent.getHeight();
        //float bordBas = bordHaut + 2*centerOffset;

        float distanceToCenter = Math.abs(0.5f - yRelativeToCenterOffset);
        float test = Math.min(distanceToCenter*2+PADDING*(float) Math.cos(distanceToCenter*Math.PI)*distanceToCenter, MAX_ICON_PROGRESS);

        float ecart = Math.min(test,MAX_ICON_PROGRESS);

        // Normalize for center
        progressToCenter = (float) Math.cos(Math.asin(ecart));

        child.setScaleX(progressToCenter);
        child.setScaleY(progressToCenter);
    }
}