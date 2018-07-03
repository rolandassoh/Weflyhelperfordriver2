package com.wedevgroup.weflyhelperfordriver.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.haibin.calendarviewproject.colorful.MainActivity;
import com.squareup.picasso.Picasso;
import com.wedevgroup.weflyhelperfordriver.R;
import com.wedevgroup.weflyhelperfordriver.model.Parcelle;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Obrina.KIMI on ic_jam_48/27/2017.
 */

public class ParcelleImgViewAdapter extends RecyclerView.Adapter<ParcelleImgViewAdapter.ViewHolder> {
    private MainActivity activity;
    private boolean isPhotoNull  = false;
    private CopyOnWriteArrayList<Parcelle> parcelleList = new CopyOnWriteArrayList<>();
    private Context ctx;

    public ParcelleImgViewAdapter(final MainActivity act, @NonNull final CopyOnWriteArrayList<Parcelle> data) {
        activity = act;
        parcelleList.addAll(data);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_calendar_item, parent,false);
        ViewHolder viewHolder = new ViewHolder(view, activity);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        try{
            ctx = holder.itemCardView.getContext();
            holder.parcel = (Parcelle) parcelleList.get(position);

            if(!holder.parcel .getImageUrl().equals("")){
                Picasso.with(ctx).load(holder.parcel.getImageUrl()).into(holder.image);
            }else{
                holder.image.setImageBitmap(holder.parcel.getDefaultImage(ctx));

            }
            String title = ctx.getString(R.string.parcelle_num) + " " + holder.parcel.getParcelleId();
            holder.title.setText(title);

            holder.itemCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //      go to details activity
                    Parcelle parc = parcelleList.get(position);
                    activity.showDialog(parc);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return parcelleList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        Activity activity;
        CardView itemCardView;
        Parcelle parcel;
        TextView title;
        ImageView image;

        public ViewHolder(View itemView, @NonNull final Activity activity){
            super(itemView);
            this.activity = activity;
            itemCardView = (CardView) itemView.findViewById(R.id.card_view_layout);

            title = (TextView) itemView
                    .findViewById(R.id.list_item_expandable_title);
            image = (ImageView) itemView
                    .findViewById(R.id.list_item_expandable_image);
        }

    }
}
