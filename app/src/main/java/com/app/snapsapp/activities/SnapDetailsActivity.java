package com.app.snapsapp.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.app.snapsapp.R;
import com.app.snapsapp.models.SnapModelClass;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class SnapDetailsActivity extends BaseActivity implements OnMapReadyCallback {

    private boolean writeGranted=false;
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    private GoogleMap gmap;
    SnapModelClass model;
    ImageView imgBack, imgSnap;
    TextView tvDescription;
    MapView mapView;
    Button btnAddToBookmarks;
    boolean flag = false;
    DatabaseReference databaseReference;
    String userId="", key="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snap_details);

        Intent intent = getIntent();
        key = intent.getStringExtra("key");

        if(key.equals("home")){
            model = MainActivity.snap;
        }else if(key.equals("bookmark")){
            model = MainActivity.bookmark;
        }

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Bookmarks").child(userId);

        imgSnap = findViewById(R.id.imgSnap);
        imgBack = findViewById(R.id.imgBack);
        mapView = findViewById(R.id.mapView);
        tvDescription = findViewById(R.id.tvDescription);
        btnAddToBookmarks = findViewById(R.id.btnAddToBookmarks);

        Picasso.with(this).load(model.getImageUrl()).placeholder(R.drawable.ic_logo).into(imgSnap);
        tvDescription.setText(model.getDescription());


        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

        checkBookmark();

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnAddToBookmarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!flag){
                    databaseReference.child(model.getId()).setValue(model);
                    Toast.makeText(getApplicationContext(), "Added to bookmark", Toast.LENGTH_SHORT).show();
                    btnAddToBookmarks.setText("Remove From Bookmark");
                }else {
                    databaseReference.child(model.getId()).removeValue();
                    Toast.makeText(getApplicationContext(), "Removed from bookmark", Toast.LENGTH_SHORT).show();
                    btnAddToBookmarks.setText("Add to Bookmarks");
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;

        LatLng userLocation = new LatLng(model.getLatitude(), model.getLongitude());

        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(userLocation);
        circleOptions.radius(6000f);
        circleOptions.strokeColor(Color.RED);
        circleOptions.strokeWidth(4.0f);

        gmap.addCircle(circleOptions);
        gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,10));
        gmap.addMarker(new MarkerOptions().position(userLocation).title("Snap location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private void checkBookmark() {
        showProgressDialog("Checking details..");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                flag = false;
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    SnapModelClass snap = snapshot.getValue(SnapModelClass.class);
                    if(model.getId().equals(snap.getId())){
                        flag = true;
                        btnAddToBookmarks.setText("Remove From Bookmark");
                        break;
                    }
                }
                hideProgressDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { hideProgressDialog();}});
    }
}