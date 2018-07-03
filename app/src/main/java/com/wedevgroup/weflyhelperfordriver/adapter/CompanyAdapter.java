package com.wedevgroup.weflyhelperfordriver.adapter;

/**
 * Created by Obrina.KIMI on 1/15/2018.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.squareup.picasso.Picasso;
import com.wedevgroup.weflyhelperfordriver.R;
import com.wedevgroup.weflyhelperfordriver.activity.AllParcelsActivity;
import com.wedevgroup.weflyhelperfordriver.model.Company;
import com.wedevgroup.weflyhelperfordriver.presenter.DBActivity;
import com.wedevgroup.weflyhelperfordriver.utils.AppController;
import com.wedevgroup.weflyhelperfordriver.utils.Constants;
import com.wedevgroup.weflyhelperfordriver.utils.Utils;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by nafiou on 8/25/17.
 */

public class CompanyAdapter extends RecyclerView.Adapter<CompanyAdapter.CompagnyViewHolder> {
    private CopyOnWriteArrayList<Company> companies = new CopyOnWriteArrayList<>();
    DBActivity act;
    private Context ctx;
    RelativeLayout rLayout;
    AppController appController;
    private final  String TAG = getClass().getSimpleName();

    public CompanyAdapter(final @NonNull DBActivity act, @NonNull CopyOnWriteArrayList<Company> companies, @NonNull RelativeLayout rLayout)
    {
        this.companies.addAll(companies);
        this.act = act;
        this.rLayout = rLayout;
        appController = AppController.getInstance();
    }


    @Override
    public CompagnyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_company, parent,false);
        CompagnyViewHolder compagnyViewHolder = new CompagnyViewHolder(view, act);
        return compagnyViewHolder;
    }

    @Override
    public void onBindViewHolder(final CompagnyViewHolder holder, final int position) {
        try{
            holder.company = (Company) companies.get(position);
            //Made copy for OnClickListener
            final Company company = holder.company;
            ctx = holder.itemView.getContext();


            holder.name.setText(company.getName());
            holder.address.setText(company.getAddress());

            if (company.getParcelCount() == 0)
                holder.parcelCount.setText(getCtx().getString(R.string.no_one));
            else
                holder.parcelCount.setText(String.valueOf(company.getParcelCount()));



            if(!holder.company.getImageUrl().equals("")){
                Picasso.with(getCtx()).load(holder.company.getImageUrl()).into(holder.image);
            }else{
                holder.image.setImageDrawable(new IconicsDrawable(getCtx(), FontAwesome.Icon.faw_building2)
                        .color(ContextCompat.getColor(getCtx(),R.color.white))
                        .paddingDp(Constants.DRAWER_ICON_PADDING)
                        .sizeDp(Constants.ITEM_ICON_SIZE));
            }


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (!(company.getParcelCount() == 0)){
                        if (appController != null){
                            if (appController.isNetworkAvailable()){
                                try {
                                    Intent edIntent = new Intent(getCtx(), AllParcelsActivity.class);
                                    Bundle b = new Bundle();
                                    b.putSerializable("compObj", company);
                                    edIntent.putExtras(b);
                                    appController.launchActivity(edIntent, act, true);
                                }catch (Exception e){
                                    e.printStackTrace();
                                }

                            }else
                                Utils.showToast(getCtx(), R.string.error_no_internet, rLayout);
                        }
                    }


                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }



    @Override
    public int getItemCount() {
        return companies.size();
    }




    public static class CompagnyViewHolder extends RecyclerView.ViewHolder
    {
        Activity activity;
        public Company company;
        public ImageView image;
        public TextView address;
        public TextView parcelCount;
        public TextView name;
        public View itemView;


        public CompagnyViewHolder(View itemView, final Activity activity){
            super(itemView);
            this.activity    = activity;
            this.itemView    = itemView;
            image            =          (ImageView) itemView.findViewById(R.id.imgView);
            name             =          (TextView) itemView.findViewById(R.id.nameTView);
            address          =          (TextView) itemView.findViewById(R.id.addressTView);
            parcelCount      =          (TextView) itemView.findViewById(R.id.parcelCountTView);



        }
    }

    public Context getCtx() {
        if (ctx == null)
            return act.getApplicationContext();
        return ctx;
    }

    public void setCtx(@NonNull Context ctx) {
        this.ctx = ctx;
    }

}
