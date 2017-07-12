package jp.ac.titech.itpro.sdl.simplemap;

import java.net.URL;
import java.net.HttpURLConnection;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import com.google.android.gms.maps.OnMapReadyCallback;
import android.text.TextUtils;
import com.google.android.gms.maps.model.Marker;


import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import android.view.View.OnClickListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

import android.widget.CompoundButton;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;


import android.support.v4.widget.DrawerLayout;

import org.xmlpull.v1.XmlPullParser;
import android.util.Xml;


import java.util.ArrayList;

public class MainActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, OnClickListener{
    private final static String TAG = "MainActivity";

    private GoogleMap googleMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private boolean requestingLocationUpdate;
    private LatLng location;
    private enum UpdatingState {STOPPED, REQUESTING, STARTED}
    private UpdatingState state = UpdatingState.STOPPED;

    private final static String[] PERMISSIONS = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private final static int REQCODE_PERMISSIONS = 1111;

    static private int URLtocb1, URLtocb2, URLtocb3, URLtocb4, URLtocb5, URLtocb6, URLtocb7, URLtocb8, URLtocb9;

    private CheckBox mCBJ1;
    private CheckBox mCBJ2;
    private CheckBox mCBJ3;
    private CheckBox mCBJ4;
    private CheckBox mCBJ5;
    private CheckBox mCBJ6;
    private CheckBox mCBJ7;
    private CheckBox mCBJ8;
    private CheckBox mCBJ9;


    double Lat;
    double Long;
    private Marker dMarker = null;
    private Marker gMarker1 = null;
    private Marker gMarker2 = null;
    private Marker gMarker3 = null;
    private Marker gMarker4 = null;
    private Marker gMarker5 = null;
    private Marker gMarker6 = null;
    private Marker gMarker7 = null;
    private Marker gMarker8 = null;
    private Marker gMarker9 = null;
    private ArrayList<String> Llatilong;
    int n = 1;
    int i = 0;
    static double DD;
    static int DI = 1;
    static String Dist[] = new String [3];
    String LatLongDis[] = new String [40]; //4n=name 4n+1=lat 4n+2=long 4n+3=dis ...
    float max_dis = 10000000;
    static String gURL;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.openDrawer(android.support.v4.view.GravityCompat.START);
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_fragment);

        android.support.design.widget.NavigationView navigationView = (android.support.design.widget.NavigationView) findViewById(R.id.nav_view);

        View header=navigationView.getHeaderView(0);
        final TextView email_tv = (TextView)header.findViewById(R.id.email);
        final TextView name_tv = (TextView)header.findViewById(R.id.name);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                map.moveCamera(CameraUpdateFactory.zoomTo(15));
                googleMap = map;
                // タップした時のリスナーをセット
//                googleMap.setOnMapClickListener(new OnMapClickListener() {
//                    @Override
//                    public void onMapClick(LatLng tapLocation) {
//
//                        // TODO Auto-generated method stub
//                        String id = gMarker.getId();
//                        if (id.equals("m0")) {
//                            // マーカー削除
//                            gMarker.remove();
//                            Toast.makeText(getApplicationContext(), "マーカー削除!", Toast.LENGTH_LONG).show();
//                        }
//
//                    }
//                });

                // 長押しのリスナーをセット
                googleMap.setOnMapLongClickListener(new OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(LatLng long_pushLocation) {

//                        // TODO Auto-generated method stub
//                        // タッチ地点と目的地との最短距離の計算

                        if(dMarker != null) dMarker.remove();
                        //tapされた位置の緯度経度
                        location = new LatLng(long_pushLocation.latitude, long_pushLocation.longitude);
                        dMarker = googleMap.addMarker(new MarkerOptions().position(location).title(""+long_pushLocation.latitude+" :"+ long_pushLocation.longitude));
                        //googleMap.moveCamera(CameraUpdateFactory.zoomTo(15f));
                        float[] results = new float[1];
                        Location.distanceBetween(long_pushLocation.latitude, long_pushLocation.longitude, Lat, Long, results);
                        int result = java.lang.Float.compare(results[0]/1000,max_dis);
                        if(result == -1) {
                            max_dis = results[0];
                            email_tv.setText("現在地からの距離"+String.valueOf(String.format("%.1f", max_dis))+"m");
                        }
                        Dist[0]   = String.valueOf(String.format("%.6f",long_pushLocation.latitude));
                        Dist[1]   = String.valueOf(String.format("%.6f",long_pushLocation.longitude));
                        Dist[2]   = String.valueOf(results[0]);
                        DD = Double.parseDouble(Dist[2]);
                        System.out.println(DD);
                        Toast.makeText(getApplicationContext(), "現在地からの距離：" + ( (Float)(results[0]) ).toString() + "m", Toast.LENGTH_SHORT).show();

                    }
                });

            }

        });

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        Button btClick = (Button) findViewById(R.id.btClick);
        RefreshListener listener = new RefreshListener();
        btClick.setOnClickListener(listener);


        mCBJ1 = (CheckBox) findViewById(R.id.cbJ1);
        mCBJ1.setChecked(false);
        mCBJ1.setOnClickListener(this);
        mCBJ2 = (CheckBox) findViewById(R.id.cbJ2);
        mCBJ2.setChecked(false);
        mCBJ2.setOnClickListener(this);
        mCBJ3 = (CheckBox) findViewById(R.id.cbJ3);
        mCBJ3.setChecked(false);
        mCBJ3.setOnClickListener(this);
        mCBJ4 = (CheckBox) findViewById(R.id.cbJ4);
        mCBJ4.setChecked(false);
        mCBJ4.setOnClickListener(this);
        mCBJ5 = (CheckBox) findViewById(R.id.cbJ5);
        mCBJ5.setChecked(false);
        mCBJ5.setOnClickListener(this);
        mCBJ6 = (CheckBox) findViewById(R.id.cbJ6);
        mCBJ6.setChecked(false);
        mCBJ6.setOnClickListener(this);
        mCBJ7 = (CheckBox) findViewById(R.id.cbJ7);
        mCBJ7.setChecked(false);
        mCBJ7.setOnClickListener(this);
        mCBJ8 = (CheckBox) findViewById(R.id.cbJ8);
        mCBJ8.setChecked(false);
        mCBJ8.setOnClickListener(this);
        mCBJ9 = (CheckBox) findViewById(R.id.cbJ9);
        mCBJ9.setChecked(false);
        mCBJ9.setOnClickListener(this);


    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        if (state != UpdatingState.STARTED && googleApiClient.isConnected()) {
            //startLocationUpdate(true);
        }else {
            state = UpdatingState.REQUESTING;
        }
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        if (state == UpdatingState.STARTED)
            stopLocationUpdate();
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected");
        if (state == UpdatingState.REQUESTING)
            startLocationUpdate(true);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged: " + location);
        googleMap.animateCamera(CameraUpdateFactory
                .newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
        Lat  = location.getLatitude();
        Long = location.getLongitude();
        System.out.println("------------------------------");
        System.out.println(Lat);
        System.out.println(Long);
        System.out.println("------------------------------");
        test1();
    }

    @Override
    public void onRequestPermissionsResult(int reqCode,
                                           @NonNull String[] permissions, @NonNull int[] grants) {
        Log.d(TAG, "onRequestPermissionsResult");
        switch (reqCode) {
        case REQCODE_PERMISSIONS:
            startLocationUpdate(false);
            break;
        }
    }

    private void startLocationUpdate(boolean reqPermission) {
        Log.d(TAG, "startLocationUpdate: " + reqPermission);
        for (String permission : PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                if (reqPermission)
                    ActivityCompat.requestPermissions(this, PERMISSIONS, REQCODE_PERMISSIONS);
                else
                    Toast.makeText(this, getString(R.string.toast_requires_permission, permission),
                            Toast.LENGTH_SHORT).show();
                return;
            }
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        Log.d(TAG, "stopLocationUpdate");
        state = UpdatingState.STOPPED;
    }

    private void stopLocationUpdate() {
        Log.d(TAG, "stopLocationUpdate");
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        state = UpdatingState.STOPPED;
    }

    /******************************************
     * ボタンをクリックしたときのリスナクラス
     ******************************************/
    private class RefreshListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        XmlPullParser xmlPullParser = Xml.newPullParser();
                        tabelog(String.valueOf(Lat), String.valueOf(Long));
                        URL url = new URL(gURL);

                        System.out.println(gURL);
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        xmlPullParser.setInput(con.getInputStream(), "UTF-8");
                        int eventType;
                        int Tname = 0;
                        int TLat  = 0;
                        int TLng  = 0;


                        while ((eventType = xmlPullParser.next()) != XmlPullParser.END_DOCUMENT) {
                            if (eventType == XmlPullParser.START_TAG && xmlPullParser.getName().equals("name")) {
                                LatLongDis[4*Tname] = xmlPullParser.nextText();
                                Tname++;
                                if(Tname == 10) Tname = 0;
                                //System.out.println(LatLongDis[4*n]);
                            }
                            if (eventType == XmlPullParser.START_TAG && xmlPullParser.getName().equals("latitude")) {
                                LatLongDis[4*TLat+1] = xmlPullParser.nextText();
                                TLat++;
                                if(TLat == 10) TLat = 0;
                                //System.out.println(LatLongDis[4*n+1]);
                            }

                            if (eventType == XmlPullParser.START_TAG && xmlPullParser.getName().equals("longitude")) {
                                LatLongDis[4*TLng+2] = xmlPullParser.nextText();
                                //System.out.println(LatLongDis[4*n+2]);
                                TLng++;
                                if(TLng == 10) TLng = 0;
                            }
                        }

                    } catch (Exception ex) {
                        System.out.println("error...");
                    }
                }
            }).start();
            startLocationUpdate(true);
            state = UpdatingState.REQUESTING;
        }
    }



     // 地名を入れて経路を検索
    private void test1(){
        android.support.design.widget.NavigationView navigationView = (android.support.design.widget.NavigationView) findViewById(R.id.nav_view);
        final android.view.Menu menuNav = navigationView.getMenu();
        final android.view.MenuItem share_1  = menuNav.findItem(R.id.nav_1);
        final android.view.MenuItem share_2  = menuNav.findItem(R.id.nav_2);
        final android.view.MenuItem share_3  = menuNav.findItem(R.id.nav_3);
        final android.view.MenuItem share_4  = menuNav.findItem(R.id.nav_4);
        final android.view.MenuItem share_5  = menuNav.findItem(R.id.nav_5);
        final android.view.MenuItem share_6  = menuNav.findItem(R.id.nav_6);
        final android.view.MenuItem share_7  = menuNav.findItem(R.id.nav_7);
        final android.view.MenuItem share_8  = menuNav.findItem(R.id.nav_8);
        final android.view.MenuItem share_9  = menuNav.findItem(R.id.nav_9);
        float[] results = new float[1];


        if(gMarker1 != null) gMarker1.remove();
        if(gMarker2 != null) gMarker2.remove();
        if(gMarker3 != null) gMarker3.remove();
        if(gMarker4 != null) gMarker4.remove();
        if(gMarker5 != null) gMarker5.remove();
        if(gMarker6 != null) gMarker6.remove();
        if(gMarker7 != null) gMarker7.remove();
        if(gMarker8 != null) gMarker8.remove();
        if(gMarker9 != null) gMarker9.remove();


        share_1.setTitle("");
        share_2.setTitle("");
        share_3.setTitle("");
        share_4.setTitle("");
        share_5.setTitle("");
        share_6.setTitle("");
        share_7.setTitle("");
        share_8.setTitle("");
        share_9.setTitle("");



        //@@@@@@  1   @@@@@@@@
        if(LatLongDis[0]!=null&&LatLongDis[1]!=null&&LatLongDis[2]!=null) {
            double wlat1 =  java.lang.Double.valueOf(LatLongDis[1]) - 0.00010695*java.lang.Double.valueOf(LatLongDis[1])  + 0.000017464*java.lang.Double.valueOf(LatLongDis[2]) + 0.0046017;
            double wlng1 =  java.lang.Double.valueOf(LatLongDis[2]) - 0.000046038*java.lang.Double.valueOf(LatLongDis[1]) - 0.000083043*java.lang.Double.valueOf(LatLongDis[2]) + 0.010040;
            LatLng new_location1 = new LatLng(wlat1, wlng1);
            Location.distanceBetween(wlat1, wlng1, Lat, Long, results);
            share_1.setTitle(LatLongDis[0]+":"+String.valueOf(String.format("%.1f",results[0]))+"m");
            if(gMarker1 != null) gMarker1.remove();
            //gMarker1 = googleMap.addMarker(new MarkerOptions().position(new_location1).title(LatLongDis[0]+ " #"+LatLongDis[1]+"/"+LatLongDis[2]));
            MarkerOptions options1 = new MarkerOptions();
            options1.position(new_location1);
            BitmapDescriptor icon_1 = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
            options1.icon(icon_1);
            gMarker1 = googleMap.addMarker(options1);
        }

        //@@@@@@  2   @@@@@@@@
        if(LatLongDis[4]!=null&&LatLongDis[5]!=null&&LatLongDis[6]!=null) {
            double wlat2 =  java.lang.Double.valueOf(LatLongDis[5]) - 0.00010695*java.lang.Double.valueOf(LatLongDis[5])  + 0.000017464*java.lang.Double.valueOf(LatLongDis[6]) + 0.0046017;
            double wlng2 =  java.lang.Double.valueOf(LatLongDis[6]) - 0.000046038*java.lang.Double.valueOf(LatLongDis[5]) - 0.000083043*java.lang.Double.valueOf(LatLongDis[6]) + 0.010040;
            LatLng new_location2 = new LatLng(wlat2, wlng2);
            Location.distanceBetween(wlat2, wlng2, Lat, Long, results);
            share_2.setTitle(LatLongDis[4]+":"+String.valueOf(String.format("%.1f",results[0]))+"m");
            if(gMarker2 != null) gMarker2.remove();
            //gMarker2 = googleMap.addMarker(new MarkerOptions().position(new_location2).title(LatLongDis[4]+ " #"+LatLongDis[5]+"/"+LatLongDis[6]));
            MarkerOptions options2 = new MarkerOptions();
            options2.position(new_location2);
            BitmapDescriptor icon_2 = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);
            options2.icon(icon_2);
            gMarker2 = googleMap.addMarker(options2);
        }

        //@@@@@@  3   @@@@@@@@
        if(LatLongDis[8]!=null&&LatLongDis[9]!=null&&LatLongDis[10]!=null) {
            double wlat3 =  java.lang.Double.valueOf(LatLongDis[9]) - 0.00010695*java.lang.Double.valueOf(LatLongDis[9])  + 0.000017464*java.lang.Double.valueOf(LatLongDis[10]) + 0.0046017;
            double wlng3 =  java.lang.Double.valueOf(LatLongDis[10]) - 0.000046038*java.lang.Double.valueOf(LatLongDis[9]) - 0.000083043*java.lang.Double.valueOf(LatLongDis[10]) + 0.010040;
            LatLng new_location3 = new LatLng(wlat3, wlng3);
            Location.distanceBetween(wlat3, wlng3, Lat, Long, results);
            share_3.setTitle(LatLongDis[8]+":"+String.valueOf(String.format("%.1f",results[0]))+"m");
            if(gMarker3 != null) gMarker3.remove();
            //gMarker3 = googleMap.addMarker(new MarkerOptions().position(new_location3).title(LatLongDis[8]+ " #"+LatLongDis[9]+"/"+LatLongDis[10]));
            MarkerOptions options3 = new MarkerOptions();
            options3.position(new_location3);
            BitmapDescriptor icon_3 = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
            options3.icon(icon_3);
            gMarker3 = googleMap.addMarker(options3);
        }

        //@@@@@@  4   @@@@@@@@
        if(LatLongDis[12]!=null&&LatLongDis[13]!=null&&LatLongDis[14]!=null) {
            double wlat4 =  java.lang.Double.valueOf(LatLongDis[13]) - 0.00010695*java.lang.Double.valueOf(LatLongDis[13])  + 0.000017464*java.lang.Double.valueOf(LatLongDis[14]) + 0.0046017;
            double wlng4 =  java.lang.Double.valueOf(LatLongDis[14]) - 0.000046038*java.lang.Double.valueOf(LatLongDis[13]) - 0.000083043*java.lang.Double.valueOf(LatLongDis[14]) + 0.010040;
            LatLng new_location4 = new LatLng(wlat4, wlng4);
            Location.distanceBetween(wlat4, wlng4, Lat, Long, results);
            share_4.setTitle(LatLongDis[12]+":"+String.valueOf(String.format("%.1f",results[0]))+"m");
            if(gMarker4 != null) gMarker4.remove();
            //gMarker4 = googleMap.addMarker(new MarkerOptions().position(new_location4).title(LatLongDis[12]+ " #"+LatLongDis[13]+"/"+LatLongDis[14]));
            MarkerOptions options4 = new MarkerOptions();
            options4.position(new_location4);
            BitmapDescriptor icon_4 = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN);
            options4.icon(icon_4);
            gMarker4 = googleMap.addMarker(options4);
        }

        //@@@@@@  5   @@@@@@@@
        if(LatLongDis[16]!=null&&LatLongDis[17]!=null&&LatLongDis[18]!=null) {
            double wlat5 =  java.lang.Double.valueOf(LatLongDis[17]) - 0.00010695*java.lang.Double.valueOf(LatLongDis[17])  + 0.000017464*java.lang.Double.valueOf(LatLongDis[18]) + 0.0046017;
            double wlng5 =  java.lang.Double.valueOf(LatLongDis[18]) - 0.000046038*java.lang.Double.valueOf(LatLongDis[17]) - 0.000083043*java.lang.Double.valueOf(LatLongDis[18]) + 0.010040;
            LatLng new_location5 = new LatLng(wlat5, wlng5);
            Location.distanceBetween(wlat5, wlng5, Lat, Long, results);
            share_5.setTitle(LatLongDis[16]+":"+String.valueOf(String.format("%.1f",results[0]))+"m");
            if(gMarker5 != null) gMarker5.remove();
            //gMarker5 = googleMap.addMarker(new MarkerOptions().position(new_location5).title(LatLongDis[16]+ " #"+LatLongDis[17]+"/"+LatLongDis[18]));
            MarkerOptions options5 = new MarkerOptions();
            options5.position(new_location5);
            BitmapDescriptor icon_5 = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA);
            options5.icon(icon_5);
            gMarker5 = googleMap.addMarker(options5);
        }

        //@@@@@@  6   @@@@@@@@
        if(LatLongDis[20]!=null&&LatLongDis[21]!=null&&LatLongDis[22]!=null) {
            double wlat6 =  java.lang.Double.valueOf(LatLongDis[21]) - 0.00010695*java.lang.Double.valueOf(LatLongDis[21])  + 0.000017464*java.lang.Double.valueOf(LatLongDis[22]) + 0.0046017;
            double wlng6 =  java.lang.Double.valueOf(LatLongDis[22]) - 0.000046038*java.lang.Double.valueOf(LatLongDis[21]) - 0.000083043*java.lang.Double.valueOf(LatLongDis[22]) + 0.010040;
            LatLng new_location6 = new LatLng(wlat6, wlng6);
            Location.distanceBetween(wlat6, wlng6, Lat, Long, results);
            share_6.setTitle(LatLongDis[20]+":"+String.valueOf(String.format("%.1f",results[0]))+"m");
            if(gMarker6 != null) gMarker6.remove();
            //gMarker6 = googleMap.addMarker(new MarkerOptions().position(new_location6).title(LatLongDis[20]+ " #"+LatLongDis[21]+"/"+LatLongDis[22]));
            MarkerOptions options6 = new MarkerOptions();
            options6.position(new_location6);
            BitmapDescriptor icon_6 = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);
            options6.icon(icon_6);
            gMarker6 = googleMap.addMarker(options6);
        }


        //@@@@@@  7   @@@@@@@@
        if(LatLongDis[24]!=null&&LatLongDis[25]!=null&&LatLongDis[26]!=null) {
            double wlat7 =  java.lang.Double.valueOf(LatLongDis[25]) - 0.00010695*java.lang.Double.valueOf(LatLongDis[25])  + 0.000017464*java.lang.Double.valueOf(LatLongDis[26]) + 0.0046017;
            double wlng7 =  java.lang.Double.valueOf(LatLongDis[26]) - 0.000046038*java.lang.Double.valueOf(LatLongDis[25]) - 0.000083043*java.lang.Double.valueOf(LatLongDis[26]) + 0.010040;
            LatLng new_location7 = new LatLng(wlat7, wlng7);
            Location.distanceBetween(wlat7, wlng7, Lat, Long, results);
            share_7.setTitle(LatLongDis[24]+":"+String.valueOf(String.format("%.1f",results[0]))+"m");
            if(gMarker7 != null) gMarker7.remove();
            //gMarker7 = googleMap.addMarker(new MarkerOptions().position(new_location7).title(LatLongDis[24]+ " #"+LatLongDis[25]+"/"+LatLongDis[26]));
            MarkerOptions options7 = new MarkerOptions();
            options7.position(new_location7);
            BitmapDescriptor icon_7 = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);
            options7.icon(icon_7);
            gMarker7 = googleMap.addMarker(options7);
        }


        //@@@@@@  8   @@@@@@@@
        if(LatLongDis[28]!=null&&LatLongDis[29]!=null&&LatLongDis[30]!=null) {
            double wlat8 =  java.lang.Double.valueOf(LatLongDis[29]) - 0.00010695*java.lang.Double.valueOf(LatLongDis[29])  + 0.000017464*java.lang.Double.valueOf(LatLongDis[30]) + 0.0046017;
            double wlng8 =  java.lang.Double.valueOf(LatLongDis[30]) - 0.000046038*java.lang.Double.valueOf(LatLongDis[29]) - 0.000083043*java.lang.Double.valueOf(LatLongDis[30]) + 0.010040;
            LatLng new_location8 = new LatLng(wlat8, wlng8);
            Location.distanceBetween(wlat8, wlng8, Lat, Long, results);
            share_8.setTitle(LatLongDis[28]+":"+String.valueOf(String.format("%.1f",results[0]))+"m");
            if(gMarker8 != null) gMarker8.remove();
            //gMarker8 = googleMap.addMarker(new MarkerOptions().position(new_location8).title(LatLongDis[28]+ " #"+LatLongDis[29]+"/"+LatLongDis[30]));
            MarkerOptions options8 = new MarkerOptions();
            options8.position(new_location8);
            BitmapDescriptor icon_8 = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE);
            options8.icon(icon_8);
            gMarker8 = googleMap.addMarker(options8);
        }


        //@@@@@@  9   @@@@@@@@
        if(LatLongDis[32]!=null&&LatLongDis[33]!=null&&LatLongDis[34]!=null) {
            double wlat9 =  java.lang.Double.valueOf(LatLongDis[33]) - 0.00010695*java.lang.Double.valueOf(LatLongDis[33])  + 0.000017464*java.lang.Double.valueOf(LatLongDis[34]) + 0.0046017;
            double wlng9 =  java.lang.Double.valueOf(LatLongDis[34]) - 0.000046038*java.lang.Double.valueOf(LatLongDis[33]) - 0.000083043*java.lang.Double.valueOf(LatLongDis[34]) + 0.010040;
            LatLng new_location9 = new LatLng(wlat9, wlng9);
            Location.distanceBetween(wlat9, wlng9, Lat, Long, results);
            share_9.setTitle(LatLongDis[32]+":"+String.valueOf(String.format("%.1f",results[0]))+"m");
            if(gMarker9 != null) gMarker9.remove();
            //gMarker9 = googleMap.addMarker(new MarkerOptions().position(new_location9).title(LatLongDis[32]+ " #"+LatLongDis[33]+"/"+LatLongDis[34]));
            MarkerOptions options9 = new MarkerOptions();
            options9.position(new_location9);
            BitmapDescriptor icon_9 = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET);
            options9.icon(icon_9);
            gMarker9 = googleMap.addMarker(options9);
        }

        for(int q =0; q<40; q++) {
            LatLongDis[q] = null;
        }


//        String prefixURL = "https://tabelog.com/map/?sw=";
//        String start = "凌駕";
//
//        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
//        intent.setClassName("com.google.android.googlequicksearchbox",
//                "com.google.android.googlequicksearchbox.SearchActivity");
//        intent.putExtra(SearchManager.QUER10Y, prefixURL + start + "&sk=" + Lat +" "+ Long);
//        startActivity(intent);

    }

    /*******************************************************************************
     * ぐるなびWebサービスのレストラン検索APIで緯度経度検索を実行しパースするプログラム
     * 注意：緯度、経度、範囲は固定で入れています。
     *　　　　アクセスキーはアカウント登録時に発行されたキーを指定してください。
     ******************************************************************************/

        public static void tabelog(String LAT, String LONG) {
            // アクセスキー
            String acckey = "412ef37422f2ca18584801e4d25ce0a3";
            // 緯度
            String lat = LAT;
            // 経度
            String lon = LONG;
            // 範囲
//            if((Dist[2])<=300) DI = 1;
//            if(300<Integer.parseInt(Dist[2]) && Integer.parseInt(Dist[2])<=500) DI = 2;
//            if(500<Integer.parseInt(Dist[2]) && Integer.parseInt(Dist[2])<=1000) DI = 3;
//            if(1000<Integer.parseInt(Dist[2]) && Integer.parseInt(Dist[2])<=2000) DI = 4;
//            if(2000<Integer.parseInt(Dist[2]) && Integer.parseInt(Dist[2])<=3000) DI = 5;

//            String range = String.valueOf(DI);
            String range = "1";
            // 返却形式
            String format = "xml";
            // 結果表示数
            String hit_per_page = "9";
            // ジャンル
            String category_l1 = "RSFST03000";
            // ジャンル
            String category_l2 = "RSFST05000";
            // ジャンル
            String category_l3 = "RSFST06000";
            // ジャンル
            String category_l4 = "RSFST08000";
            // ジャンル
            String category_l5 = "RSFST14000";
            // ジャンル
            String category_l6 = "RSFST11000";
            // ジャンル
            String category_l7 = "RSFST16000";
            // ジャンル
            String category_l8 = "RSFST18000";
            // ジャンル
            String category_l9 = "RSFST20000";
            /*
            * RSFST03000  susi
            * RSFST05000  yakiniku horumonn
            * RSFST06000  yakitori
            * RSFST08000  raamen
            * RSFST14000  tyuuka
            * RSFST11000  itariann
            * RSFST16000  curry
            * RSFST18000  cafe kanmi
            * RSFST20000  famiresu
            *
            * */

            // エンドポイント
            String gnaviRestUri = "https://api.gnavi.co.jp/RestSearchAPI/20150630/";
            String prmFormat = "?format=" + format;
            String prmKeyid = "&keyid=" + acckey;
            String prmLat = "&latitude=" + lat;
            String prmLon = "&longitude=" + lon;
            String prmRange = "&range=" + range;
            String perPage = "&hit_per_page=" + hit_per_page;
            String category1 = "&category_l=" + category_l1;
            String category2 = "&category_l=" + category_l2;
            String category3 = "&category_l=" + category_l3;
            String category4 = "&category_l=" + category_l4;
            String category5 = "&category_l=" + category_l5;
            String category6 = "&category_l=" + category_l6;
            String category7 = "&category_l=" + category_l7;
            String category8 = "&category_l=" + category_l8;
            String category9 = "&category_l=" + category_l9;

            // URI組み立て
            StringBuffer uri = new StringBuffer();
            uri.append(gnaviRestUri);
            uri.append(prmFormat);
            uri.append(prmKeyid);
            uri.append(prmLat);
            uri.append(prmLon);
            uri.append(prmRange);
            uri.append(perPage);
            if(URLtocb1==1)uri.append(category1);
            if(URLtocb2==1)uri.append(category2);
            if(URLtocb3==1)uri.append(category3);
            if(URLtocb4==1)uri.append(category4);
            if(URLtocb5==1)uri.append(category5);
            if(URLtocb6==1)uri.append(category6);
            if(URLtocb7==1)uri.append(category7);
            if(URLtocb8==1)uri.append(category8);
            if(URLtocb9==1)uri.append(category9);

            // API実行、結果を取得し出力
            gURL = uri.toString();

            //getNodeList(uri.toString());
        }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.cbJ1:
                URLtocb1 = 0;
                if(mCBJ1.isChecked()){	// チェックされている場合
                    URLtocb1 = 1;
                }
                break;
            case R.id.cbJ2:
                URLtocb2 = 0;
                if(mCBJ2.isChecked()){	// チェックされている場合
                    URLtocb2 = 1;
                }
                break;
            case R.id.cbJ3:
                URLtocb3 = 0;
                if(mCBJ3.isChecked()){	// チェックされている場合
                    URLtocb3 = 1;
                }
                break;
            case R.id.cbJ4:
                URLtocb4 = 0;
                if(mCBJ4.isChecked()){	// チェックされている場合
                    URLtocb4 = 1;
                }
                break;
            case R.id.cbJ5:
                URLtocb5 = 0;
                if(mCBJ5.isChecked()){	// チェックされている場合
                    URLtocb5 = 1;
                }
                break;
            case R.id.cbJ6:
                URLtocb6 = 0;
                if(mCBJ6.isChecked()){	// チェックされている場合
                    URLtocb6 = 1;
                }
                break;
            case R.id.cbJ7:
                URLtocb7 = 0;
                if(mCBJ7.isChecked()){	// チェックされている場合
                    URLtocb7 = 1;
                }
                break;
            case R.id.cbJ8:
                URLtocb8 = 0;
                if(mCBJ8.isChecked()){	// チェックされている場合
                    URLtocb8 = 1;
                }
                break;
            case R.id.cbJ9:
                URLtocb9 = 0;
                if(mCBJ9.isChecked()){	// チェックされている場合
                    URLtocb9 = 1;
                }
                break;
            default:
                break;
        }
    }


}




