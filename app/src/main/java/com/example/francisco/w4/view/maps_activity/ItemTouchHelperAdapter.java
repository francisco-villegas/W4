package com.example.francisco.w4.view.maps_activity;

/**
 * Created by FRANCISCO on 28/08/2017.
 */

public interface ItemTouchHelperAdapter {
    boolean onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);
}
