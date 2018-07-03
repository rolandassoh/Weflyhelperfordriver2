package com.wedevgroup.weflyhelperfordriver.presenter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.wedevgroup.weflyhelperfordriver.R;
import com.wedevgroup.weflyhelperfordriver.activity.ParcelleDetailsActivity;
import com.wedevgroup.weflyhelperfordriver.model.Parcelle;
import com.wedevgroup.weflyhelperfordriver.model.Point;
import com.wedevgroup.weflyhelperfordriver.utils.Constants;
import com.wedevgroup.weflyhelperfordriver.utils.Utils;
import com.wedevgroup.weflyhelperfordriver.utils.AppController;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by admin on 11/04/2018.
 */

public class DrawingPresenter implements Serializable{
    public GoogleMap map = null;
    Polygon polygon;
    Polyline polyline;
    PolylineOptions polylineOptions;
    AppController appController;
    private Marker origin = null;
    private Marker ref = null;
    private final String TAG = getClass().getSimpleName();
    private final ParcelleDetailsActivity act;
    private View v;
    private Point pCenter = null, pRef = null;
    boolean isRef;
    boolean isFromEditMode;
    private ArrayList<ArrayList<Point>> drawingList = new ArrayList<>(); // use to allow back btn
    private ArrayList<Point> pointsParcels = new ArrayList<Point>();
    private static ArrayList<ArrayList<Point>> savDrawing = new ArrayList<>(); // use to allow back btn
    private static ArrayList<Point> savPoints = new ArrayList<Point>();
    private Marker uMarker;


    public DrawingPresenter(@NonNull final ParcelleDetailsActivity activity, @NonNull final GoogleMap map, @NonNull View view){
        this.act = activity;
        this.map = map;
        this.v = view;
        appController = AppController.getInstance();
    }


    public void onClear(){
        clearPolygon();
        clearPolyline();
        clearMarker(); // clear map too
        pointsParcels.clear();
        drawingList.clear();

        pRef = null;
        pCenter = null;

    }

    private void clearMarker() {
        if (origin != null){
            origin.remove();
            origin = null;
        }

        if (ref != null){
            ref.remove();
            ref = null;
        }

        map.clear();
    }


    private void clearPolyline() {
        /*Remove hold polygone*/
        if (polyline != null){
            polyline.remove();
            polylineOptions = null;
        }
    }

    private void  clearPolygon(){
        if (polygon != null) {
            polygon.remove();
            polygon = null;
        }
    }

    public void addDrawing(@NonNull ArrayList<Point> list) {
        if (map != null){
            removeCenter();
            removeReference(false, true);

            if (list.size() == 1) {

                // origin
                try {
                    // will crash onMulti touch for funny
                    origin = map.addMarker(new MarkerOptions()
                            .position(list.get(list.size() - 1).getLatLng())
                            .draggable(false)
                            .visible(true)
                            .icon(BitmapDescriptorFactory.fromBitmap(Utils.resizeMapIcons(R.drawable.point_mk, 50, 50, act))));
                }catch (Exception e){
                    e.printStackTrace();
                }


            } else if(list.size() == 2){
                // add Polyline


                // draw pointsParcels or polylines
                if (polylineOptions == null) {
                    polylineOptions = new PolylineOptions();
                    polylineOptions.color(ContextCompat.getColor(act, R.color.colorTransparentAccent)).width(5);
                }
                polylineOptions.addAll(getPointsAsLatLng(list));
                polyline = map.addPolyline(polylineOptions);

                // add Marker
                addMarkerOnMap(list);


            } else {
                // add Polygon
                ArrayList<LatLng> newList = new ArrayList<>();
                newList.addAll(getPointsAsLatLng(list));

                // Fix empty list issue
                try {
                    PolygonOptions polygonOptions = new PolygonOptions();
                    polygonOptions.addAll(newList);
                    polygonOptions.strokeColor(act.getResources().getColor(R.color.colorTintAccent)).strokeWidth(5).fillColor(act.getResources().getColor(R.color.colorTransparentAccent));
                    polygon = map.addPolygon(polygonOptions);

                    centerCamera(newList);
                    // add Marker
                    addMarkerOnMap(list);
                }catch (Exception e){
                    e.printStackTrace();
                }



            }

        }

    }

    private void centerCamera(@NonNull ArrayList<LatLng> newList) {

        if (map!= null){
            try {
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (LatLng dm : newList){
                    builder.include(dm);
                }
                LatLngBounds bounds = builder.build();

                int width = act.getResources().getDisplayMetrics().widthPixels;
                int height = act.getResources().getDisplayMetrics().heightPixels;
                int padding = (int) (width * 0.12); // offset from edges of the map 12% of screen

                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

                map.moveCamera(cu);
            } catch (Exception e ){
                e.printStackTrace();
            }
        }
    }


    private void addMarkerOnMap(@NonNull ArrayList<Point> list) {
        for (LatLng mDm: getPointsAsLatLng(list)){
            try {
                map.addMarker(new MarkerOptions()
                        .position(mDm)
                        .draggable(false)
                        .visible(true)
                        .icon(BitmapDescriptorFactory.fromBitmap(Utils.resizeMapIcons(R.drawable.point_mk, 50, 50, act))));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    public boolean isParcelle(){
        return pointsParcels.size() > 2;
    }

    private @NonNull ArrayList<LatLng> getPointsAsLatLng(@NonNull ArrayList<Point> list){
        // Convert points for marker funct
        ArrayList<LatLng> listAsLatLng = new ArrayList<>();
        for (Point mDm: list){
            listAsLatLng.add(mDm.getLatLng());
        }
        return listAsLatLng;
    }

    public @NonNull Point getCenter(){
        if (pCenter == null)
            pCenter = new Point();
        return pCenter;
    }

    private void removeCenter() {
        for (Point cDm: pointsParcels){
            if (cDm.isCenter()){
                pointsParcels.remove(cDm);
                break;
            }
        }
    }

    private void removeReference(boolean isEdMode, boolean withSave) {
        if (isEdMode){
            // remove Reference
            for (Point pDm: pointsParcels){
                if (pDm.isReference()){
                    if (withSave)
                        pRef = pDm;
                    pointsParcels.remove(pDm);
                    break;
                }
            }

        }else {
            // remove Reference
            for (Point pDm: pointsParcels){
                if (pDm.isReference()){
                    pRef = pDm;
                    pointsParcels.remove(pDm);
                    break;
                }
            }
        }
    }

    public Bundle onSaveInstanceState(@NonNull Bundle outState) {
        outState.putSerializable(Constants.PRESENTER_DRAWING, drawingList);
        outState.putSerializable(Constants.PRESENTER_DRAWING_POINTS, pointsParcels);

        return outState;
    }

    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        try {
            ArrayList<ArrayList<Point>> drawings;
            drawings = (ArrayList<ArrayList<Point>> ) savedInstanceState.getSerializable(Constants.PRESENTER_DRAWING);


            ArrayList<Point> points;
            points = (ArrayList<Point>) savedInstanceState.getSerializable(Constants.PRESENTER_DRAWING_POINTS);

            if (points !=null && drawings != null ){
                savPoints.clear();
                savDrawing.clear();

                // Share drawings between Presenter Instance
                savDrawing.addAll(drawings);
                savPoints.addAll(points);
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public boolean isStateAvaiable(){
        return  (savDrawing.size() > 0)  && (savPoints.size() > 0);
    }


    public void onReloadRestoreState(){
        if (savDrawing.size() > 0 && savPoints.size() > 0){
            onClear();
            pointsParcels.addAll(savPoints);
            drawingList.addAll(savDrawing);
            addDrawing(pointsParcels);
        }
    }

    public  void showDrawing(@NonNull Parcelle parcelToUpd) {
        if (map != null){
            onClear();
            pointsParcels.addAll(parcelToUpd.getPointsList());

            // remove Reference
            removeReference(true, true);

            // remove center
            removeCenter();
            isFromEditMode = true;
            addDrawing(pointsParcels);

            // Save for undo
            ArrayList<Point> newList = new ArrayList<Point>();
            newList.addAll(pointsParcels);
            drawingList.add(newList);
        }

    }

    public void cameraOnUser(@NonNull LatLng latLng, @NonNull final  GoogleMap mMap) {
        if (map != null && act != null){
            if (uMarker != null)
                uMarker.remove();

            Bitmap ob = BitmapFactory.decodeResource(act.getResources(),R.drawable.ic_location_black);
            Bitmap obm = Bitmap.createBitmap(ob.getWidth(), ob.getHeight(), ob.getConfig());
            Canvas canvas = new Canvas(obm);
            Paint paint = new Paint();
            paint.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(act, R.color.white), PorterDuff.Mode.SRC_ATOP));
            canvas.drawBitmap(ob, 0f, 0f, paint);
            uMarker = map.addMarker(new MarkerOptions()
                    .position(latLng)
                    .draggable(false)
                    .visible(true)
                    .icon(BitmapDescriptorFactory.fromBitmap(Utils.resizeMapBitmap(obm, 50, 50, act))));
            CameraPosition BONDI = new CameraPosition.Builder().target(latLng)
                    .zoom(16.5f)
                    .bearing(0)
                    .tilt(0)
                    .build();
            changeCamera(CameraUpdateFactory.newCameraPosition(BONDI),null, mMap);
        }
    }

    private void changeCamera(CameraUpdate update, GoogleMap.CancelableCallback callback, GoogleMap mMap) {
//            if (mCustomDurationToggle.isChecked()) {
        int duration = 2000;
        // The duration must be strictly positive so we make it at least 1.
        mMap.animateCamera(update, Math.max(duration, 1), callback);
//            } else {
//                mMap.animateCamera(update, callback);
//            }
//
    }
}
