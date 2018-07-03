package com.wedevgroup.weflyhelperfordriver.activity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.wedevgroup.weflyhelperfordriver.R;
import com.wedevgroup.weflyhelperfordriver.adapter.CultureAdapter;
import com.wedevgroup.weflyhelperfordriver.model.Parcelle;
import com.wedevgroup.weflyhelperfordriver.model.Point;
import com.wedevgroup.weflyhelperfordriver.presenter.DBActivity;
import com.wedevgroup.weflyhelperfordriver.presenter.DrawingPresenter;
import com.wedevgroup.weflyhelperfordriver.presenter.LocationPresenter;
import com.wedevgroup.weflyhelperfordriver.service.LocationProviderService;
import com.wedevgroup.weflyhelperfordriver.utils.Constants;
import com.wedevgroup.weflyhelperfordriver.utils.Utils;
import com.wedevgroup.weflyhelperfordriver.utils.AppController;

import java.util.ArrayList;

import devlight.io.library.ntb.NavigationTabBar;

public class ParcelleDetailsActivity extends DBActivity {

    private LocationPresenter locPresenter;
    private DrawingPresenter draPresenter;
    private GoogleMap map = null;
    public  MapView mapView;
    private LinearLayout liMain;
    private Toolbar toolbar;
    View vMap, vDetails;
    private final String TAG = getClass().getSimpleName();


    private Parcelle parcelle = null;
    private FloatingActionButton fabLoc, fabNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parcelle_details);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        iniViews();
        initTab();
        iniListeners();

        locPresenter = new LocationPresenter(this);
        AppController.addToDestroyList(this);

        // remove notification if exist

        try {
            NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            manager.cancel(Constants.NOTIFICATION_SERVICE_AUTO_POST_ID);
            Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            sendBroadcast(it);
        }catch (Exception e){
            e.printStackTrace();
        }

        // from Notification
        parcelle = (Parcelle) getIntent().getSerializableExtra("notif_parc");

        // from A list
        if (parcelle == null)
            parcelle = (Parcelle) getIntent().getSerializableExtra("parcelObj");

        if (parcelle == null)
            parcelle = new Parcelle();

        mapView = (MapView) vMap.findViewById(R.id.mapView);
        try {
            mapView.onCreate(savedInstanceState);
            mapView.onResume(); // needed to get the map to display immediately
            MapsInitializer.initialize(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!locPresenter.isLocationEnabled())
            locPresenter.showLocationServicesRequireDialog();
        else
            startLocationService();

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                map.getUiSettings().setAllGesturesEnabled(true);
                map.getUiSettings().setMapToolbarEnabled(false);


                draPresenter = new DrawingPresenter(ParcelleDetailsActivity.this, map, liMain);



                draPresenter.showDrawing(parcelle);

                if(draPresenter.isStateAvaiable())
                    draPresenter.onReloadRestoreState();

            }
        });
    }

    private void iniListeners() {
        fabLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onShowLocation();
            }
        });
        fabNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start Navigation
                // Check permission Before
                Point point = new Point();
                for (Point dm: parcelle.getPointsList()){
                    if (dm.isReference())
                        point = dm;
                }
                // Start Navigation
                // Check permission Before
                if (!locPresenter.isLocationEnabled())
                    locPresenter.showLocationServicesRequireDialog();
                else if(getCurrentLatitude() == Constants.DOUBLE_NULL && getCurrentLongitude() == Constants.DOUBLE_NULL)
                    Utils.showToast(ParcelleDetailsActivity.this, R.string.loading_gps, liMain);
                else{
                    try {
                        Uri gmmIntentUri = Uri.parse("google.navigation:q="+ point.getLatitude() +"," + point.getLongitude() + "" );
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void iniViews() {
        toolbar             = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }catch (Exception e){
            e.printStackTrace();
        }

        liMain              = (LinearLayout) findViewById(R.id.liMain);

        // Details
        vDetails = LayoutInflater.from(
                ParcelleDetailsActivity.this).inflate(R.layout.fragment_parcel_detail, null, false);

        // Map fragment
        vMap = LayoutInflater.from(
                ParcelleDetailsActivity.this).inflate(R.layout.fragment_parcel_map, null, false);

        fabLoc              = (FloatingActionButton) vMap.findViewById(R.id.fabLocation);
        fabNav             = (FloatingActionButton) vMap.findViewById(R.id.fabNavigation);

    }

    private void initTab() {
        final ViewPager viewPager = (ViewPager) findViewById(R.id.vp_horizontal_ntb);
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public boolean isViewFromObject(final View view, final Object object) {
                return view.equals(object);
            }

            @Override
            public void destroyItem(final View container, final int position, final Object object) {
                ((ViewPager) container).removeView((View) object);
            }

            @Override
            public Object instantiateItem(final ViewGroup container, final int position) {
                final View view;

                try {
                    if (position == 1){
                        // Details
                        view = vDetails;

                        TextView surface        = (TextView) view.findViewById(R.id.surfaceTView);
                        TextView perimeter      = (TextView) view.findViewById(R.id.perimeterTView);
                        TextView dist           = (TextView) view.findViewById(R.id.distanceTView);
                        TextView date           = (TextView) view.findViewById(R.id.dateTView);
                        TextView zone           = (TextView) view.findViewById(R.id.zoneTView);
                        TextView couche         = (TextView) view.findViewById(R.id.coucheTView);
                        TextView region         = (TextView) view.findViewById(R.id.regionTView);

                        //Compagny
                        TextView eName         = (TextView) view.findViewById(R.id.eNameTView);
                        TextView ePhone        = (TextView) view.findViewById(R.id.ePhoneTView);
                        TextView eRef          = (TextView) view.findViewById(R.id.referenceTView);
                        TextView eEmail        = (TextView) view.findViewById(R.id.eEmailTView);
                        TextView eAddress      = (TextView) view.findViewById(R.id.addressTView);
                        TextView eWebSite      = (TextView) view.findViewById(R.id.webSiteTView);

                        //Guide
                        TextView gName         = (TextView) view.findViewById(R.id.gNameTView);
                        TextView gPhone        = (TextView) view.findViewById(R.id.gTelTView);
                        TextView gEmail        = (TextView) view.findViewById(R.id.gEmailTextView);
                        RecyclerView rvCulture = (RecyclerView) view.findViewById(R.id.cultureRView);

                        CultureAdapter adapter;

                        // recycler
                        rvCulture.setLayoutManager(new LinearLayoutManager(ParcelleDetailsActivity.this, LinearLayoutManager.VERTICAL, false));
                        rvCulture.setHasFixedSize(true);
                        // adapter
                        adapter = new CultureAdapter(view.getContext(), rvCulture ,parcelle.getCultureList());
                        rvCulture.setAdapter(adapter);

                        if (parcelle != null){

                            surface.setText(Utils.formatNumber(parcelle.getSurface()) +" hec");
                            perimeter.setText(Utils.formatNumber(parcelle.getPerimetre()) + " m");
                            dist.setText(parcelle.getDistance());
                            date.setText(parcelle.getDateCreatedFormatted());
                            zone.setText(parcelle.getZone());
                            couche.setText(parcelle.getCouche());
                            region.setText(parcelle.getRegion());

                            // Company
                            eName.setText(parcelle.getCompany().getName());
                            ePhone.setText(parcelle.getCompany().getTel());
                            eRef.setText(parcelle.getCompany().getRef());
                            eEmail.setText(parcelle.getCompany().getEmail());
                            eAddress.setText(parcelle.getCompany().getAddress());
                            eWebSite.setText(parcelle.getCompany().getWebSite());

                            //Guide
                            gName.setText(parcelle.getNameGuide());
                            gEmail.setText(parcelle.getEmailGuide());
                            gPhone.setText(parcelle.getTelGuide());

                        }



                    }else{
                        // Map fragment
                        view = vMap;
                    }

                    container.addView(view);
                    return view;

                }catch (Exception e){
                    e.printStackTrace();
                    // Details
                    View mView = LayoutInflater.from(
                            getBaseContext()).inflate(R.layout.fragment_parcel_detail, null, false);
                    container.addView(mView);
                    return mView;
                }

            }
        });

        final NavigationTabBar navigationTabBar = (NavigationTabBar) findViewById(R.id.ntb_horizontal);
        final ArrayList<NavigationTabBar.Model> models = new ArrayList<>();

        models.add(
                new NavigationTabBar.Model.Builder(
                        new IconicsDrawable(this, FontAwesome.Icon.faw_map)
                                .color(ContextCompat.getColor(this,R.color.white))
                                .sizeDp(Constants.HAMBURGER_ICON_SIZE),
                        ContextCompat.getColor(ParcelleDetailsActivity.this, R.color.material_orange_700))
                        .selectedIcon(
                                new IconicsDrawable(this, FontAwesome.Icon.faw_map2)
                                        .color(ContextCompat.getColor(this,R.color.white))
                                        .sizeDp(Constants.HAMBURGER_ICON_SIZE)
                        )
                        .title(getString(R.string.map))
                        .badgeTitle("")
                        .build()
        );

        models.add(
                new NavigationTabBar.Model.Builder(
                        new IconicsDrawable(this, FontAwesome.Icon.faw_building)
                                .color(ContextCompat.getColor(this,R.color.white))
                                .sizeDp(Constants.HAMBURGER_ICON_SIZE),
                        ContextCompat.getColor(ParcelleDetailsActivity.this, R.color.material_lime_700))
                        .selectedIcon(
                                new IconicsDrawable(this, FontAwesome.Icon.faw_building2)
                                        .color(ContextCompat.getColor(this,R.color.white))
                                        .sizeDp(Constants.HAMBURGER_ICON_SIZE)
                        )
                        .title(getString(R.string.info))
                        .badgeTitle("")
                        .build()
        );

        navigationTabBar.setModels(models);
        navigationTabBar.setViewPager(viewPager, 1);
        navigationTabBar.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(final int position) {
                navigationTabBar.getModels().get(position).hideBadge();
            }

            @Override
            public void onPageScrollStateChanged(final int state) {

            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        startLocationService();
    }

    private void onShowLocation() {
        // Check permission Before
        if (!locPresenter.isLocationEnabled())
            locPresenter.showLocationServicesRequireDialog();
        else {
            if(locPresenter.canRequestLocation()){
                onDisplayLocation(getCurrentLatitude(),getCurrentLongitude(), map);
            }
        }

    }


    private void onDisplayLocation(@NonNull Double pLat, @NonNull Double pLong, @NonNull final GoogleMap map) {
        if(pLat == Constants.DOUBLE_NULL && pLong == Constants.DOUBLE_NULL)
            Utils.showToast(this, R.string.loading_gps, liMain);
        else {
            draPresenter.cameraOnUser(new LatLng(pLat, pLong), map);
        }

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (draPresenter != null){
            super.onSaveInstanceState(draPresenter.onSaveInstanceState(outState));
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        DrawingPresenter presenter = new DrawingPresenter(this, map, liMain);
        presenter.onRestoreInstanceState(savedInstanceState);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy(){
        //unregisterReceiver(broadcastReceiver);
        Intent intent = new Intent(getApplicationContext(), LocationProviderService.class);
        stopService(intent);
        super.onDestroy();
    }
}
