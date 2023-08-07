package com.example.todoapp.Touch;

import androidx.recyclerview.widget.RecyclerView;

public interface ItemTouch {
    void onSwiped(RecyclerView.ViewHolder viewHolder);
}
