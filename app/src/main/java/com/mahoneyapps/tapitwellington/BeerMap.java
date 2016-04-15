package com.mahoneyapps.tapitwellington;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Brendan on 3/23/2016.
 */
public class BeerMap extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMarkerClickListener,
        LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentUserLocation;
    private Location lastLocation;
    String mLatitudeText;
    String mLongitudeText;
    Context mContext = getActivity();
    private final int MY_PERMISSIONS_REQUEST = 2;
    private static View view1;


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view1 != null) {
            ViewGroup parent = (ViewGroup) view1.getParent();

            if (parent != null)
                parent.removeView(view1);
        }

        if (container != null) {
            container.removeAllViews();
        }

        try {
            view1 = inflater.inflate(R.layout.activity_maps, container, false);
        } catch (InflateException e){

        }

        AppCompatActivity activity = ((AppCompatActivity) getActivity());

        TextView toolbar = (TextView)activity.findViewById(R.id.toolbar_title);
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Risaltype.ttf");
        toolbar.setTypeface(typeface);
        toolbar.setText("Beer Map");

        MapFragment mapFragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return view1;

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity()).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

//        addListeners();

    }

    @Override
    public void onStart() {
        super.onStart();

        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();

        if ((mGoogleApiClient != null) && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng hmm = new LatLng(0, 0);
        mMap.addMarker(new MarkerOptions().position(hmm).title("Test").snippet("Yup Yup Where it at"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hmm, 14.0f));

        addPubToMap(googleMap, "Garage Project", -41.2952851, 174.7678296, "Brewery");
        addPubToMap(googleMap, "The Malthouse", -41.2935125, 174.7816054, "Pub");
        addPubToMap(googleMap, "Black Dog", -41.2933809, 174.7833346, "Brewery");
        addPubToMap(googleMap, "The Rogue and Vagabond", -41.2934317, 174.774445, "Pub");
        addPubToMap(googleMap, "Parrotdog", -41.2968188, 174.780145, "Brewery");
        addPubToMap(googleMap, "Fork and Brewer", -41.2892497, 174.7756269, "BrewPub");
        addPubToMap(googleMap, "Little Beer Quarter", -41.2907123, 174.7749561, "Pub");
        addPubToMap(googleMap, "Southern Cross", -41.2965, 174.7744654, "Pub");

        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

                String title = marker.getTitle();
                FragmentTransaction ft = ((FragmentActivity) getActivity()).getFragmentManager()
                        .beginTransaction();
                TabLayout layout = (TabLayout) getActivity().findViewById(R.id.tab_layout);
                switch (title) {

                    case "Garage Project":
                        layout.getTabAt(1).select();
                        ft.replace(R.id.frame, new GarageProject()).addToBackStack(null).commit();
                        break;
                    case "Black Dog":
                        layout.getTabAt(1).select();
                        ft.replace(R.id.frame, new BlackDog()).addToBackStack(null).commit();
                        break;
                    case "The Malthouse":
                        layout.getTabAt(1).select();
                        ft.replace(R.id.frame, new Malthouse()).addToBackStack(null).commit();
                        break;
                    case "The Rogue and Vagabond":
                        layout.getTabAt(1).select();
                        ft.replace(R.id.frame, new RogueAndVagabond()).addToBackStack(null).commit();
                        break;
                    case "Parrotdog":
                        layout.getTabAt(1).select();
                        ft.replace(R.id.frame, new Parrotdog()).commit();
                        break;
                    case "Fork and Brewer":
                        layout.getTabAt(1).select();
                        ft.replace(R.id.frame, new NewFork()).commit();
                        break;
                    case "Little Beer Quarter":
                        layout.getTabAt(1).select();
                        ft.replace(R.id.frame, new LittleBeerQuarter()).commit();
                        break;
                    case "Southern Cross":
                        layout.getTabAt(1).select();
                        ft.replace(R.id.frame, new SouthernCross()).commit();

                }
            }
        });
    }

    public GoogleMap addPubToMap(GoogleMap map, String name, double lat, double lng, String snippet) {
        mMap = map;
        LatLng lattylong = new LatLng(lat, lng);
        String pubName = name;
        String snip = snippet;
        mMap.addMarker(new MarkerOptions().position(lattylong).title(pubName).snippet(snip));

        return mMap;
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){


        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // TODO: Consider calling
            //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.

//            lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//            if (lastLocation == null){
//                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
//                        != PackageManager.PERMISSION_GRANTED){

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST);



        } else {

            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String provider = String.valueOf(locationManager.getBestProvider(criteria, false));
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
            // lastlocation works but location doesn't, edit code appropriately
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            Location location = locationManager.getLastKnownLocation(provider);

            locationManager.requestLocationUpdates(provider, 1000, 0, this);

            properZoom(lastLocation);
        }
        } else {

            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String provider = String.valueOf(locationManager.getBestProvider(criteria, false));
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            Location location = locationManager.getLastKnownLocation(provider);

            locationManager.requestLocationUpdates(provider, 1000, 0, this);

            properZoom(lastLocation);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                    Criteria criteria = new Criteria();
                    String provider = String.valueOf(locationManager.getBestProvider(criteria, true));
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for Activity#requestPermissions for more details.
                        return;
                    }
                    lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    Location location = locationManager.getLastKnownLocation(provider);

                    locationManager.requestLocationUpdates(provider, 1000, 0, this);

                    properZoom(lastLocation);

                } else {

                }

            }
        }
    }


    private void properZoom(Location lastLocation) {
        CameraPosition cameraPosition = CameraPosition.builder()
                .target(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()))
//                .target(new LatLng(-41.2952851, 174.7678296))
                .zoom(14f)
                .build();


        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);

        }


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}