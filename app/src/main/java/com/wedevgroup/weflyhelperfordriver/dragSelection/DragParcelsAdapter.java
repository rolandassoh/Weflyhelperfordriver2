package com.wedevgroup.weflyhelperfordriver.dragSelection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wedevgroup.weflyhelperfordriver.R;
import com.wedevgroup.weflyhelperfordriver.activity.ParcelleDetailsActivity;
import com.wedevgroup.weflyhelperfordriver.model.Company;
import com.wedevgroup.weflyhelperfordriver.model.Parcelle;
import com.wedevgroup.weflyhelperfordriver.utils.AppController;
import com.wedevgroup.weflyhelperfordriver.utils.Constants;
import com.wedevgroup.weflyhelperfordriver.utils.Utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by flisar on 03.03.2017.
 */

public class DragParcelsAdapter extends RecyclerView.Adapter<DragParcelsAdapter.ViewHolder>
{
    private ItemClickListener mClickListener;
    private CopyOnWriteArrayList<Parcelle> parcelles = new CopyOnWriteArrayList<>();
    private AppController appController;
    View rLayout;
    Activity act;
    Context ctx;
    Company company;
    private final String TAG = getClass().getSimpleName();

    private HashSet<Integer> mSelected;

    public DragParcelsAdapter(Activity act, @NonNull CopyOnWriteArrayList<Parcelle> parcelles, @NonNull final View view, @NonNull Company cp)
    {
        this.act = act;
        this.rLayout = view;
        this.parcelles.addAll(parcelles);
        this.company = cp;
        mSelected = new HashSet<Integer>();
        appController = AppController.getInstance();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(act).inflate(R.layout.row_parcel, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        try {
            ctx = holder.item.getContext();
            holder.parcel = (Parcelle) parcelles.get(position);
            final Parcelle parcelle = holder.parcel;
            //Made copy for OnClickListener

            holder.tvName.setText(ctx.getString(R.string.parcelle_num) +
                    " " + String.valueOf(holder.parcel.getParcelleId()));
            if (mSelected.contains(position))
                holder.ivCheck.setVisibility(View.VISIBLE);
            else
                holder.ivCheck.setVisibility(View.GONE);

            if(!holder.parcel.getImageUrl().equals("")){
                Picasso.with(holder.item.getContext()).load(holder.parcel.getImageUrl()).into(holder.ivParcel);
            }else{
                holder.ivParcel.setImageBitmap(holder
                        .parcel
                        .getDefaultImage(holder.item.getContext()));

            }

            holder.item.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (appController != null){
                        Utils utils = new Utils();
                        Intent edIntent = new Intent(getCtx(), ParcelleDetailsActivity.class);
                        Bundle b = new Bundle();
                        parcelle.setCompany(company);
                        b.putSerializable("parcelObj", parcelle);
                        edIntent.putExtras(b);
                        getCtx().startActivity(edIntent);
                        utils.animActivityOpen(act);
                    }else
                        Utils.showToast(getCtx(), R.string.error_no_internet, rLayout);

                    if (AppController.getInstance().isNetworkAvailable())
                    {

                    }else {

                    }
                    return false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount()
    {
        return parcelles.size();
    }

    // ----------------------
    // Selection
    // ----------------------

    public void toggleSelection(int pos)
    {
        if (mSelected.contains(pos))
            mSelected.remove(pos);
        else
            mSelected.add(pos);
        notifyItemChanged(pos);
    }

    public void select(int pos, boolean selected)
    {
        if (selected)
            mSelected.add(pos);
        else
            mSelected.remove(pos);
        notifyItemChanged(pos);
    }

    public void selectRange(int start, int end, boolean selected)
    {
        for (int i = start; i <= end; i++)
        {
            if (selected)
                mSelected.add(i);
            else
                mSelected.remove(i);
        }
        notifyItemRangeChanged(start, end - start + 1);
    }

    public void deselectAll()
    {
        // this is not beautiful...
        mSelected.clear();
        notifyDataSetChanged();
    }

    public void selectAll()
    {
        for (int i = 0; i < getItemCount(); i++)
            mSelected.add(i);
        notifyDataSetChanged();
    }

    public int getCountSelected()
    {
        return mSelected.size();
    }

    public HashSet<Integer> getSelection()
    {
        return mSelected;
    }

    // ----------------------
    // Click Listener
    // ----------------------

    public void setClickListener(ItemClickListener itemClickListener)
    {
        mClickListener = itemClickListener;
    }

    public interface ItemClickListener
    {
        void onItemClick(View view, int position);
        boolean onItemLongClick(View view, int position);
    }

    // ----------------------
    // ViewHolder
    // ----------------------

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener
    {
        public TextView tvName;
        public Parcelle parcel;
        public ImageView ivParcel;
        public ImageView ivCheck;
        public View item;

        public ViewHolder(View itemView)
        {
            super(itemView);
            tvName                   = (TextView) itemView.findViewById(R.id.itemParcelName);
            ivParcel = (ImageView) itemView.findViewById(R.id.itemImage);
            ivCheck = (ImageView) itemView.findViewById(R.id.itemCheckImg);

            // save for Context
            item = itemView;
            itemView.setOnClickListener(this);
            //itemView.setOnLongClickListener(this); --> go to Details
        }

        @Override
        public void onClick(View view)
        {
            if (mClickListener != null)
                mClickListener.onItemClick(view, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view)
        {
            if (mClickListener != null)
                return mClickListener.onItemLongClick(view, getAdapterPosition());
            return false;
        }
    }

    public @NonNull CopyOnWriteArrayList<Parcelle> getItemSelected(){
        CopyOnWriteArrayList<Parcelle> list = new CopyOnWriteArrayList<>();

        try {
            for (Integer dm :mSelected){
                list.add(parcelles.get(dm));
            }

            for (Parcelle parc :list){
                parc.setDateSurvol(AppController.getDateSelected());
                parc.setFlYear(AppController.getFlyYear());
                parc.setFlMonth(AppController.getFlyMonth());
                parc.setFlDay(AppController.getFlyDay());
                parc.setCompany(company);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;

    }

    public Context getCtx() {
        if (ctx == null)
            return act.getApplicationContext();
        return ctx;
    }


}
