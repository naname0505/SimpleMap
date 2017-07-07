package jp.ac.titech.itpro.sdl.simplemap;

import java.io.IOException;
import java.net.URL;
import java.net.HttpURLConnection;

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



import org.json.JSONObject;

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
    String LatiLongDis [] = new String [30]; //0:1lat, 1;1long, 2;1dis, 3:2lat...
    float max_dis = 10000;



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
        name_tv.setText("現在地からの最短距離");
        email_tv.setText(String.valueOf("no set"));

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
                        LatLng new_location = new LatLng(long_pushLocation.latitude, long_pushLocation.longitude);
                        gMarker = googleMap.addMarker(new MarkerOptions().position(new_location).title(""+long_pushLocation.latitude+" :"+ long_pushLocation.longitude));
                        CameraPosition cameraPos = new CameraPosition.Builder().target(long_pushLocation).zoom(15).build();
                        MarkerOptions options = new MarkerOptions();
                        options.position(long_pushLocation);
                        // TODO Auto-generated method stub
                        // タッチ地点と目的地との最短距離の計算
                        float[] results = new float[1];
                        Location.distanceBetween(long_pushLocation.latitude, long_pushLocation.longitude, Lat, Long, results);
                        int result = java.lang.Float.compare(results[0]/1000,max_dis);
                        if(result == -1) {
                            max_dis = results[0]/1000;
                            email_tv.setText(String.valueOf(String.format("%.4f", max_dis))+"Km");
                        }
                        LatiLongDis[n]   = String.valueOf(String.format("%.6f",long_pushLocation.latitude));
                        LatiLongDis[n+1] = String.valueOf(String.format("%.6f",long_pushLocation.longitude));
                        LatiLongDis[n+2] = String.valueOf(String.format("%.4f", results[0]/1000));

                        switch (n){
                            case 1:
                                share_1.setTitle("目的地まで : "+LatiLongDis[n+2]+"Km");
                                BitmapDescriptor icon_1 = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
                                options.icon(icon_1);
                                break;
                            case 2:
                                share_2.setTitle("目的地まで : "+LatiLongDis[n+2]+"Km");
                                BitmapDescriptor icon_2 = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);
                                options.icon(icon_2);
                                break;
                            case 3:
                                share_3.setTitle("目的地まで : "+LatiLongDis[n+2]+"Km");
                                BitmapDescriptor icon_3 = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
                                options.icon(icon_3);
                                break;
                            case 4:
                                share_4.setTitle("目的地まで : "+LatiLongDis[n+2]+"Km");
                                BitmapDescriptor icon_4 = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN);
                                options.icon(icon_4);
                                break;
                            case 5:
                                share_5.setTitle("目的地まで : "+LatiLongDis[n+2]+"Km");
                                BitmapDescriptor icon_5 = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA);
                                options.icon(icon_5);
                                break;
                            case 6:
                                share_6.setTitle("目的地まで : "+LatiLongDis[n+2]+"Km");
                                BitmapDescriptor icon_6 = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);
                                options.icon(icon_6);
                                break;
                            case 7:
                                share_7.setTitle("目的地まで : "+LatiLongDis[n+2]+"Km");
                                BitmapDescriptor icon_7 = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
                                options.icon(icon_7);
                                break;
                            case 8:
                                share_8.setTitle("目的地まで : "+LatiLongDis[n+2]+"Km");
                                BitmapDescriptor icon_8 = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE);
                                options.icon(icon_8);
                                break;
                            case 9:
                                share_9.setTitle("目的地まで : "+LatiLongDis[n+2]+"Km");
                                BitmapDescriptor icon_9 = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET);
                                options.icon(icon_9);
                                break;
                            case 10:
                                share_10.setTitle("目的地まで : "+LatiLongDis[n+2]+"Km");
                                BitmapDescriptor icon_10 = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);
                                options.icon(icon_10);
                                Toast.makeText(getApplicationContext(), "欲張りすぎ...", Toast.LENGTH_LONG).show();
                                n = 0;
                                break;

                        }
                        googleMap.addMarker(options);
                        n = n + 1;
                        Toast.makeText(getApplicationContext(), "現在地からの距離：" + ( (Float)(results[0]/1000) ).toString() + "Km", Toast.LENGTH_LONG).show();

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
            //startLocationUpdate(true);
            //state = UpdatingState.REQUESTING;
            tabelog();

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
     *      JsonをパースするためにライブラリにJacksonを追加しています。
     ******************************************************************************/

        public static void tabelog() {
            // アクセスキー
            String acckey = "412ef37422f2ca18584801e4d25ce0a3";
            // 緯度
            String lat = "35.670082";
            // 経度
            String lon = "139.763267";
            // 範囲
            String range = "1";
            // 返却形式
            String format = "xml";
            // エンドポイント
            String gnaviRestUri = "https://api.gnavi.co.jp/RestSearchAPI/20150630/";
            String prmFormat = "?format=" + format;
            String prmKeyid = "&keyid=" + acckey;
            String prmLat = "&latitude=" + lat;
            String prmLon = "&longitude=" + lon;
            String prmRange = "&range=" + range;

            // URI組み立て
            StringBuffer uri = new StringBuffer();
            uri.append(gnaviRestUri);
            uri.append(prmFormat);
            uri.append(prmKeyid);
            uri.append(prmLat);
            uri.append(prmLon);
            uri.append(prmRange);

            // API実行、結果を取得し出力

            getNodeList(uri.toString());
        }

        public static void getNodeList(String url){
            try {
                XmlPullParser xmlPullParser = Xml.newPullParser();
                URL restSearch = new URL(url);
                java.net.URLConnection connection = restSearch.openConnection();
                System.out.println("connection");
                //http.setRequestMethod("GET");
                xmlPullParser.setInput(connection.getInputStream(), "UTF-8");
                //http.connect();

                System.out.println("success");
                //Jackson
                //JSONObject json = new JSONObject(http.getInputStream().toString()).getJSONObject("attribu");
                // JSONArray  json = new JSONObject(http.getInputStream().toString()).getJSONArray("@attributes");

                String a[] = new String[10];
                int counter = 0;
                System.out.println("success111111111111");
                for(int e = xmlPullParser.getEventType(); e != XmlPullParser.END_DOCUMENT; e = xmlPullParser.next()){
                    if(e == XmlPullParser.START_TAG && xmlPullParser.getName().equals("name")){
                        a[counter] = xmlPullParser.nextText();
                        System.out.println(a[counter]);
                        System.out.println(counter);
                        counter++;
                    }
                }

                //String data1 = json.getString("attributes");

                System.out.println("success22222222222222222");
                //System.out.println(data1);

            } catch (Exception e){
                Log.d("XmlPullParserSampleUrl", "Error");
            }
        }



//
//        private static void viewJsonNode(JsonNode nodeList) {
//            if (nodeList != null) {
//                //トータルヒット件数
//                String hitcount = "total:" + nodeList.path("total_hit_count").asText();
//                System.out.println(hitcount);
//                //restのみ取得
//                JsonNode restList = nodeList.path("rest");
//                Iterator<JsonNode> rest = restList.iterator();
//                //店舗番号、店舗名、最寄の路線、最寄の駅、最寄駅から店までの時間、店舗の小業態を出力
//                while (rest.hasNext()) {
//                    JsonNode r = rest.next();
//                    String id = r.path("id").asText();
//                    String name = r.path("name").asText();
//                    String line = r.path("access").path("line").asText();
//                    String station = r.path("access").path("station").asText();
//                    String walk = r.path("access").path("walk").asText() + "分";
//                    String categorys = "";
//
//                    for (JsonNode n : r.path("code").path("category_name_s")) {
//                        categorys += n.asText();
//                    }
//                    System.out.println(id + "¥t" + name + "¥t" + line + "¥t" + station + "¥t" + walk + "¥t" + categorys);
//                }
//            }
//        }





    }




