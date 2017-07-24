package com.deitel.twittersearches;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.content.Context;
import android.graphics.Canvas;
import android.view.View;

/**
 * Created by Yevgeniy on 6/27/2017.
 */

//A ReyclerView.ItemDecoration object draws decorations on a RecyclerView.
//This class defines dividers displayed between the RecyclerView items.

class ItemDivider extends RecyclerView.ItemDecoration {

    private final Drawable divider;

    //The ItemDivider subclass draws divider lines between list items.
    public ItemDivider(Context context){
        //We get android.R.attr.listDivider, which is the standard Android list-item divider used
        //by default in ListViews.
        int[] attrs = {android.R.attr.listDivider};
        divider = context.obtainStyledAttributes(attrs).getDrawable(0);
    }


    //The onDrawOver method draws the list item dividers onto the RecyclerView
    //As user scrolls through RecyclerView's items, the RecyclerView's contents are repeatedly redrawn
    //to display the items in their new positions on the screen. In this process the RecyclerView calls
    //its RecyclerView.ItemDecoration's onDrawOver method. The method receives
    //A Canvas "c" for drawing the decorations on
    //The RecyclerView object "parent" on which the Canvas "c" draws
    //The RecyclerView.State "state" which is an object that stores info passed between various RecyclerView components.
    //In this app we simply pass this value to the superclass's onDrawOver method.
    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

        //We first calculate the left & right x-coordinates that are used to specify the bound's of the Drawable to be displayed.

        //Left coordinate is determined by calling RecyclerView's getPaddingLeft which returns amount of padding
        //between the RecyclerView's left edge and its content.
        int left = parent.getPaddingLeft();

        //Right coordinate is determined by calling RecyclerView's getWidth and subtracting the result of getPaddingRight.
        int right = parent.getWidth() - parent.getPaddingRight();

        //In this loop we draw the dividers on the RecyclerView's Canvas by iterating through all but the last item
        //and drawing the dividers below each item.
        for (int i = 0; i < parent.getChildCount() - 1; ++i){

            //First we get and store the current RecyclerView item.
            View item = parent.getChildAt(i);

            //Then we calculate one divider's top y-coordinate using the item's bottom y-coordinate + item's margin.
            int top = item.getBottom() + ((RecyclerView.LayoutParams) item.getLayoutParams()).bottomMargin;
            //Then we calculate the divider's bottom y-coordinate using the item's top y-coordinate + divider's height.
            int bottom = top + divider.getIntrinsicHeight();

            //Then we draw the divider with the calculated bounds.
            divider.setBounds(left, top, right, bottom);
            //finally we draw it on the Canvas "c".
            divider.draw(c);
        }
    }
}
