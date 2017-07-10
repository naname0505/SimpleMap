package jp.ac.titech.itpro.sdl.simplemap;

import java.io.IOException;
import java.net.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.Iterator;
import android.Manifest;
import android.app.SearchManager;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.net.Uri;
import android.app.Activity;
import com.google.android.gms.maps.OnMapReadyCallback;
import android.location.Location;

import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import com.google.android.gms.maps.SupportMapFragment;

import org.xmlpull.v1.XmlPullParser;
import android.util.Xml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


import java.util.ArrayList;

public class MainActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener{
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
    double Lat;
    double Long;
    private Marker gMarker = null;
    private ArrayList<String> Llatilong;
    int n = 1;
    int i = 1;
    String Dist[] = new String [3];
    String LatiLongDis [] = new String [40]; //4n=name 4n+1=lat 4n+2=long 4n+3=dis ...
    float max_dis = 10000;
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
        final android.view.MenuItem share_10 = menuNav.findItem(R.id.nav_10);


        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                map.moveCamera(CameraUpdateFactory.zoomTo(15));
                googleMap = map;
                // タップした時のリスナーをセット
                googleMap.setOnMapClickListener(new OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng tapLocation) {
                        // tapされた位置の緯度経度
                        //location = new LatLng(tapLocation.latitude, tapLocation.longitude);
                        //googleMap.addMarker(new MarkerOptions().position(location).title(""+tapLocation.latitude+" :"+ tapLocation.longitude));
                        // googleMap.moveCamera(CameraUpdateFactory.zoomTo(15f));
                        /*
                        // TODO Auto-generated method stub
                        String id = .getId();
                        if (id.equals("m0")) {
                            // マーカー削除
                            gMarker.remove();
                            toast.makeText(getApplicationContext(), "マーカー削除!", Toast.LENGTH_LONG).show();
                        }
                        */
                    }
                });

                // 長押しのリスナーをセット
                googleMap.setOnMapLongClickListener(new OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(LatLng long_pushLocation) {

//                        // TODO Auto-generated method stub
//                        // タッチ地点と目的地との最短距離の計算
//                        float[] results = new float[1];
//                        Location.distanceBetween(long_pushLocation.latitude, long_pushLocation.longitude, Lat, Long, results);
//                        int result = java.lang.Float.compare(results[0]/1000,max_dis);
//                        if(result == -1) {
//                            max_dis = results[0]/1000;
//                            email_tv.setText(String.valueOf(String.format("%.4f", max_dis))+"Km");
//                        }
                        Dist[0]   = String.valueOf(String.format("%.6f",long_pushLocation.latitude));
                        Dist[1] = String.valueOf(String.format("%.6f",long_pushLocation.longitude));

                        switch (n+1){

                            //Dist[2] = String.valueOf(String.format("%.4f", results[0]/1000));
                            case 1:
                                LatLng new_location1 = new LatLng(java.lang.Long.valueOf(LatiLongDis[4*n+1]), java.lang.Long.valueOf(LatiLongDis[4*n+2]));
                                gMarker = googleMap.addMarker(new MarkerOptions().position(new_location1).title(""+LatiLongDis[4*n+1]+" :"+ LatiLongDis[4*n+2]));
                                MarkerOptions options1 = new MarkerOptions();
                                options1.position(new_location1);
                                share_1.setTitle("目的地まで : "+LatiLongDis[4*n+2]+"Km");
                                BitmapDescriptor icon_1 = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
                                options1.icon(icon_1);
                                googleMap.addMarker(options1);
                                n++;
                                break;
                            case 2:
                                LatLng new_location2 = new LatLng(java.lang.Long.valueOf(LatiLongDis[4*n+1]), java.lang.Long.valueOf(LatiLongDis[4*n+2]));
                                gMarker = googleMap.addMarker(new MarkerOptions().position(new_location2).title(""+LatiLongDis[4*n+1]+" :"+ LatiLongDis[4*n+2]));
                                MarkerOptions options2 = new MarkerOptions();
                                options2.position(new_location2);
                                share_2.setTitle("目的地まで : "+LatiLongDis[4*n+2]+"Km");
                                BitmapDescriptor icon_2 = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);
                                options2.icon(icon_2);
                                googleMap.addMarker(options2);
                                n++;
                                break;
                            case 3:
                                LatLng new_location3 = new LatLng(java.lang.Long.valueOf(LatiLongDis[4*n+1]), java.lang.Long.valueOf(LatiLongDis[4*n+2]));
                                gMarker = googleMap.addMarker(new MarkerOptions().position(new_location3).title(""+LatiLongDis[4*n+1]+" :"+ LatiLongDis[4*n+2]));
                                MarkerOptions options3 = new MarkerOptions();
                                options3.position(new_location3);
                                share_3.setTitle("目的地まで : "+LatiLongDis[4*n+2]+"Km");
                                BitmapDescriptor icon_3 = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
                                options3.icon(icon_3);
                                googleMap.addMarker(options3);
                                n++;
                                break;
                            case 4:
                                LatLng new_location4 = new LatLng(java.lang.Long.valueOf(LatiLongDis[4*n+1]), java.lang.Long.valueOf(LatiLongDis[4*n+2]));
                                gMarker = googleMap.addMarker(new MarkerOptions().position(new_location4).title(""+LatiLongDis[4*n+1]+" :"+ LatiLongDis[4*n+2]));
                                MarkerOptions options4 = new MarkerOptions();
                                options4.position(new_location4);
                                share_4.setTitle("目的地まで : "+LatiLongDis[4*n+2]+"Km");
                                BitmapDescriptor icon_4 = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN);
                                options4.icon(icon_4);
                                googleMap.addMarker(options4);
                                n++;
                                break;
                            case 5:
                                LatLng new_location5 = new LatLng(java.lang.Long.valueOf(LatiLongDis[4*n+1]), java.lang.Long.valueOf(LatiLongDis[4*n+2]));
                                gMarker = googleMap.addMarker(new MarkerOptions().position(new_location5).title(""+LatiLongDis[4*n+1]+" :"+ LatiLongDis[4*n+2]));
                                MarkerOptions options5 = new MarkerOptions();
                                options5.position(new_location5);
                                share_5.setTitle("目的地まで : "+LatiLongDis[4*n+2]+"Km");
                                BitmapDescriptor icon_5 = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA);
                                options5.icon(icon_5);
                                googleMap.addMarker(options5);
                                n++;
                                break;
                            case 6:
                                LatLng new_location6 = new LatLng(java.lang.Long.valueOf(LatiLongDis[4*n+1]), java.lang.Long.valueOf(LatiLongDis[4*n+2]));
                                gMarker = googleMap.addMarker(new MarkerOptions().position(new_location6).title(""+LatiLongDis[4*n+1]+" :"+ LatiLongDis[4*n+2]));
                                MarkerOptions options6 = new MarkerOptions();
                                options6.position(new_location6);
                                share_6.setTitle("目的地まで : "+LatiLongDis[4*n+2]+"Km");
                                BitmapDescriptor icon_6 = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);
                                options6.icon(icon_6);
                                googleMap.addMarker(options6);
                                n++;
                                break;
                            case 7:
                                LatLng new_location7 = new LatLng(java.lang.Long.valueOf(LatiLongDis[4*n+1]), java.lang.Long.valueOf(LatiLongDis[4*n+2]));
                                gMarker = googleMap.addMarker(new MarkerOptions().position(new_location7).title(""+LatiLongDis[4*n+1]+" :"+ LatiLongDis[4*n+2]));
                                MarkerOptions options7 = new MarkerOptions();
                                options7.position(new_location7);
                                share_7.setTitle("目的地まで : "+LatiLongDis[4*n+2]+"Km");
                                BitmapDescriptor icon_7 = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
                                options7.icon(icon_7);
                                googleMap.addMarker(options7);
                                n++;
                                break;
                            case 8:
                                LatLng new_location8 = new LatLng(java.lang.Long.valueOf(LatiLongDis[4*n+1]), java.lang.Long.valueOf(LatiLongDis[4*n+2]));
                                gMarker = googleMap.addMarker(new MarkerOptions().position(new_location8).title(""+LatiLongDis[4*n+1]+" :"+ LatiLongDis[4*n+2]));
                                MarkerOptions options8 = new MarkerOptions();
                                options8.position(new_location8);
                                share_8.setTitle("目的地まで : "+LatiLongDis[4*n+2]+"Km");
                                BitmapDescriptor icon_8 = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE);
                                options8.icon(icon_8);
                                googleMap.addMarker(options8);
                                n++;
                                break;
                            case 9:
                                LatLng new_location9 = new LatLng(java.lang.Long.valueOf(LatiLongDis[4*n+1]), java.lang.Long.valueOf(LatiLongDis[4*n+2]));
                                gMarker = googleMap.addMarker(new MarkerOptions().position(new_location9).title(""+LatiLongDis[4*n+1]+" :"+ LatiLongDis[4*n+2]));
                                MarkerOptions options9 = new MarkerOptions();
                                options9.position(new_location9);
                                share_9.setTitle("目的地まで : "+LatiLongDis[4*n+2]+"Km");
                                BitmapDescriptor icon_9 = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET);
                                options9.icon(icon_9);
                                googleMap.addMarker(options9);
                                n++;
                                break;
                            case 10:
                                LatLng new_location10 = new LatLng(java.lang.Long.valueOf(LatiLongDis[4*n+1]), java.lang.Long.valueOf(LatiLongDis[4*n+2]));
                                gMarker = googleMap.addMarker(new MarkerOptions().position(new_location10).title(""+LatiLongDis[4*n+1]+" :"+ LatiLongDis[4*n+2]));
                                MarkerOptions options10 = new MarkerOptions();
                                options10.position(new_location10);
                                share_10.setTitle("目的地まで : "+LatiLongDis[4*n+2]+"Km");
                                BitmapDescriptor icon_10 = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);
                                options10.icon(icon_10);
                                googleMap.addMarker(options10);
                                n = 0;
                                break;

                        }
                        //Toast.makeText(getApplicationContext(), "現在地からの距離：" + ( (Float)(results[0]/1000) ).toString() + "Km", Toast.LENGTH_LONG).show();

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
        Log.d(TAG, "onConnectionSuspented");
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
            startLocationUpdate(true);
            state = UpdatingState.REQUESTING;

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
                        n = 0;
                        while ((eventType = xmlPullParser.next()) != XmlPullParser.END_DOCUMENT) {
                            if (eventType == XmlPullParser.START_TAG && xmlPullParser.getName().equals("name")) {
                                LatiLongDis[4*n] = xmlPullParser.nextText();
                                System.out.println(LatiLongDis[4*n]);
                            }
                            if (eventType == XmlPullParser.START_TAG && xmlPullParser.getName().equals("latitude")) {
                                LatiLongDis[4*n+1] = xmlPullParser.nextText();
                                System.out.println(LatiLongDis[4*n+1]);
                            }

                            if (eventType == XmlPullParser.START_TAG && xmlPullParser.getName().equals("longitude")) {
                                LatiLongDis[4*n+2] = xmlPullParser.nextText();
                                System.out.println(LatiLongDis[4*n+2]);
                                n++;
                            }

                        }
                    } catch (Exception ex) {
                        System.out.println("error...");
                    }
                }
            }).start();

        }
    }



     // 地名を入れて経路を検索
    private void test1(){
        String prefixURL = "https://tabelog.com/map/?sw=";
        String start = "凌駕";

        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
        intent.setClassName("com.google.android.googlequicksearchbox",
                "com.google.android.googlequicksearchbox.SearchActivity");
        intent.putExtra(SearchManager.QUERY, prefixURL + start + "&sk=" + Lat +" "+ Long);
        startActivity(intent);

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
            String range = "1";
            // 返却形式
            String format = "xml";
            // 結果表示数
            String hit_per_page = "10";
            // エンドポイント
            String gnaviRestUri = "https://api.gnavi.co.jp/RestSearchAPI/20150630/";
            String prmFormat = "?format=" + format;
            String prmKeyid = "&keyid=" + acckey;
            String prmLat = "&latitude=" + lat;
            String prmLon = "&longitude=" + lon;
            String prmRange = "&range=" + range;
            String perPage = "&hit_per_page=" + hit_per_page;

            // URI組み立て
            StringBuffer uri = new StringBuffer();
            uri.append(gnaviRestUri);
            uri.append(prmFormat);
            uri.append(prmKeyid);
            uri.append(prmLat);
            uri.append(prmLon);
            uri.append(prmRange);
            uri.append(perPage);

            // API実行、結果を取得し出力
            gURL = uri.toString();
            //getNodeList(uri.toString());
        }


}




