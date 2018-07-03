package com.wedevgroup.weflyhelperfordriver.adapter;


import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.wedevgroup.weflyhelperfordriver.R;
import com.wedevgroup.weflyhelperfordriver.model.Culture;
import com.wedevgroup.weflyhelperfordriver.utils.design.LetterTileProvider;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.GONE;

public class CultureAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // context
    private Context mContext;
    // list
    private ArrayList<Culture> culturesList = new ArrayList<>();
    private LetterTileProvider mLetterTileProvider;
    // recycler
    private RecyclerView mRecyclerView;


    public CultureAdapter(final Context context,
                          @NonNull RecyclerView recyclerView,
                          @NonNull ArrayList<Culture> data) {
        mContext = context;
        culturesList.addAll(data);
        mRecyclerView = recyclerView;
        mLetterTileProvider = new LetterTileProvider(mContext);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CultureViewHolder itemViewHolder = (CultureViewHolder) holder;
        final Culture culture = getItem(position);

        // avatar
        itemViewHolder.mAvatar.setVisibility(View.VISIBLE);
        itemViewHolder.mAvatar.setImageBitmap(mLetterTileProvider.getLetterTile(culture.getName()));

        // label
        itemViewHolder.mLabel.setText(culture.getName());

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_culture, parent, false);
        return new CultureViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return culturesList.size();
    }

    private Culture getItem(int position) {
        return culturesList.get(position);
    }

    private class CultureViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView mAvatar;
        private TextView mLabel;

        CultureViewHolder(View view) {
            super(view);
            mAvatar = (CircleImageView) view.findViewById(R.id.avatar);
            mLabel = (TextView) view.findViewById(R.id.name);
        }
    }



}
