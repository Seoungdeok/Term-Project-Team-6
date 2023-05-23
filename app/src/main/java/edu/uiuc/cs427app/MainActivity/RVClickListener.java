package edu.uiuc.cs427app.MainActivity;

import android.view.View;

/**
 * Defines an interface for listening for clicks in {@link androidx.recyclerview.widget.RecyclerView}
 *
 * @author Kyr Nastahunin
 */
public interface RVClickListener {
    /**
     * Method to be implemented to define behavior of the {@link androidx.recyclerview.widget.RecyclerView}
     * item that was clicked.
     *
     * @param view A reference to the UI object that was clicked.
     * @param position The index of the item in the {@link androidx.recyclerview.widget.RecyclerView}
     *                 that was clicked.
     */
    public void onClick(View view, int position);
}
