package com.example.todoapp.Touch;

import android.graphics.Canvas;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.Adapter.GhiChuAdapter;

public class RecyclerTouch extends ItemTouchHelper.SimpleCallback {
    private ItemTouch listener;

    public RecyclerTouch(int dragDirs, int swipeDirs, ItemTouch listener) {
        super(dragDirs, swipeDirs);
        this.listener = listener;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        if (listener != null){
            listener.onSwiped(viewHolder);
        }
    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        if (viewHolder != null){
            View  view = ((GhiChuAdapter.ViewHolder)viewHolder).lnGhiChu;
            getDefaultUIUtil().onSelected(view);
        }
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View  view = ((GhiChuAdapter.ViewHolder)viewHolder).lnGhiChu;
        getDefaultUIUtil().onDraw(c,recyclerView,view,dX,dY,actionState,isCurrentlyActive);
    }

    @Override
    public void onChildDrawOver(@NonNull Canvas c, @NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View  view = ((GhiChuAdapter.ViewHolder)viewHolder).lnGhiChu;
        getDefaultUIUtil().onDrawOver(c,recyclerView,view,dX,dY,actionState,isCurrentlyActive);
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        View  view = ((GhiChuAdapter.ViewHolder)viewHolder).lnGhiChu;
        getDefaultUIUtil().clearView(view);
    }
}
