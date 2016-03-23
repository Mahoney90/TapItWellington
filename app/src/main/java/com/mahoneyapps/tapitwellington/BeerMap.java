package com.mahoneyapps.tapitwellington;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_maps, container, false);

        MapFragment mapFragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return view;

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
        Log.d("on start", "starting");
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
        FragmentManager fm = getActivity().getFragmentManager();
        Fragment fragment = (fm.findFragmentById(R.id.map));
        FragmentTransaction ft = fm.beginTransaction();
        ft.remove(fragment);
        ft.commit();
    }

//    private void addListeners() {
//        mMap.setOnInfoWindowClickListener(this);
//        mMap.setOnMapLongClickListener(this);
//        mMap.setOnMapClickListener(this);
//        mMap.setOnMarkerClickListener(this);
//    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng hmm = new LatLng(0, 0);
        mMap.addMarker(new MarkerOptions().position(hmm).title("Test").snippet("Yup Yup Where it at"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hmm, 14.0f));

        addPubToMap(googleMap, "Garage Project", -41.2952851, 174.7678296, "Brewery");
        addPubToMap(googleMap, "The Malthouse", -41.2935125, 174.7816054, "Pub");
        addPubToMap(googleMap, "The Hop Garden", -41.2971731, 174.7829048, "Pub");
        addPubToMap(googleMap, "The Rogue and Vagabond", -41.2934317, 174.774445, "Pub");
        addPubToMap(googleMap, "Parrotdog", -41.2968188, 174.780145, "Brewery");
        addPubToMap(googleMap, "Fork and Brewer", -41.2892497, 174.7756269, "BrewPub");
        addPubToMap(googleMap, "Little Beer Quarter", -41.2907123, 174.7749561, "Pub");

        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Log.d("on info click", "woo");
                String title = marker.getTitle();
                FragmentTransaction ft = ((FragmentActivity) getActivity()).getFragmentManager()
                        .beginTransaction();
                switch (title){

                    case "Garage Project":
                        ft.replace(R.id.map, new GarageProject()).addToBackStack(null).commit();
                        break;
//                    case "The Hop Garden":
//                        ft.replace(R.id.map, new HopGarden()).commit();
//                        break;
//                    case "The Malthouse":
//                        ft.replace(R.id.map, new Malthouse()).commit();
//                        break;
//                    case "The Rogue and Vagabond":
//                        ft.replace(R.id.map, new RogueAndVagabond()).commit();
//                        break;
//                    case "Parrotdog":
//                        ft.replace(R.id.map, new Parrotdog()).commit();
//                        break;
//                    case "Fork and Brewer":
//                        ft.replace(R.id.map, new ForkAndBrewer()).commit();
//                        break;
//                    case "Little Beer Quarter":
//                        ft.replace(R.id.map, new LittleBeerQuarter()).commit();
//                        break;

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
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            Log.d("no permission", "no permission");
            // TODO: Consider calling
            //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.

            lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (lastLocation == null){
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED){


                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
                    lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    properZoom(lastLocation);

                }

            }

        } else {
            Log.d("permission", "yes permission");
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String provider = String.valueOf(locationManager.getBestProvider(criteria, false));
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
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
            Location location = locationManager.getLastKnownLocation(provider);
            Log.d("location", String.valueOf(location));
            locationManager.requestLocationUpdates(provider, 1000, 0, this);
            Log.d("Cam", String.valueOf(lastLocation));
            properZoom(location);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 2: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
                    Criteria criteria = new Criteria();
                    String provider = String.valueOf(locationManager.getBestProvider(criteria, true));
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
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
                    locationManager.requestLocationUpdates(provider, 1000, 0, this);
                    Log.d("Cam", String.valueOf(lastLocation));
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
        Log.d("Camera", String.valueOf(cameraPosition));

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