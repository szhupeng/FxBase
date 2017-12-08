package space.zhupeng.fxbase.widget.adapter.listener;

import android.view.View;

import space.zhupeng.fxbase.widget.adapter.BaseAdapter;

public abstract class OnItemLongClickListener extends SimpleClickListener {
    @Override
    public void onItemClick(BaseAdapter adapter, View view, int position) {

    }

    @Override
    public void onItemLongClick(BaseAdapter adapter, View view, int position) {
        onSimpleItemLongClick(adapter, view, position);
    }

    @Override
    public void onItemChildClick(BaseAdapter adapter, View view, int position) {

    }

    @Override
    public void onItemChildLongClick(BaseAdapter adapter, View view, int position) {
    }

    public abstract void onSimpleItemLongClick(BaseAdapter adapter, View view, int position);
}
