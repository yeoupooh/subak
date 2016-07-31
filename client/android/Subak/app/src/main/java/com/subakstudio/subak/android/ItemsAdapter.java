package com.subakstudio.subak.android;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jinwoomin on 7/31/16.
 */
public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder> {

    private final List<Item> mItems;

    public ItemsAdapter(List<Item> items) {
        this.mItems = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Item item = mItems.get(position);
        holder.mTitle.setText(item.getTitle());
        holder.mArtist.setText(item.getSubtitle());
//        holder.itemView.setTag(R.string.tag_item_title, item.getTitle());
//        holder.itemView.setTag(R.string.tag_item_subtitle, item.getSubtitle());
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.textViewTitle)
        TextView mTitle;

        @BindView(R.id.textViewArtist)
        TextView mArtist;

        public ViewHolder(View view) {
            super(view);

            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
//            EventBus.getDefault().post(new BookSelectedEvent(
//                    (Long)view.getTag(R.string.tag_book_id),
//                    (String)view.getTag(R.string.tag_book_title)));
        }
    }
}
