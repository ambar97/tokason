package com.pratamatechnocraft.tokason;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.pratamatechnocraft.tokason.Fragment.DashboardFragment;
import com.pratamatechnocraft.tokason.Fragment.DataBarangFragment;
import com.pratamatechnocraft.tokason.Fragment.DataKategoriBarangFragment;
import com.pratamatechnocraft.tokason.Fragment.DataPelangganFragment;
import com.pratamatechnocraft.tokason.Fragment.DataUserFragment;
import com.pratamatechnocraft.tokason.Fragment.DateRangePickerFragment;
import com.pratamatechnocraft.tokason.Fragment.LaporanFragment;
import com.pratamatechnocraft.tokason.Fragment.LaporanLabaRugiFragment;
import com.pratamatechnocraft.tokason.Fragment.ProfileFragment;
import com.pratamatechnocraft.tokason.Fragment.SettingPrinterFragment;
import com.pratamatechnocraft.tokason.Fragment.TabLayoutFragment;
import com.pratamatechnocraft.tokason.Fragment.TabLayoutFragmentBiaya;
import com.pratamatechnocraft.tokason.Model.BaseUrlApiModel;
import com.pratamatechnocraft.tokason.Service.Config;
import com.pratamatechnocraft.tokason.Service.SessionManager;


import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, DateRangePickerFragment.OnDateRangeSelectedListener {

    public static String urlGambar = "";
    public static TextView namaUser,levelUser;
    public static ImageView fotoUser;
    public Fragment fragment = null;
    int fragmentLast;
    NavigationView navigationView;
    SessionManager sessionManager;
    HashMap<String, String> user=null;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    BaseUrlApiModel baseUrlApiModel = new BaseUrlApiModel();
    private String baseUrl=baseUrlApiModel.getBaseURL();
    private static final String API_URL_LOAD = "api/user?api=profile&kd_user=";

    private static final String TAG = MainActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        DrawerLayout drawer = (DrawerLayout) findViewById( R.id.drawer_layout );
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close );
        //noinspection deprecation
        drawer.setDrawerListener( toggle );
        toggle.syncState();

        displaySelectedScreen( R.id.nav_dashboard );

        sessionManager = new SessionManager( this );
        sessionManager.checkLogin();
        user = sessionManager.getUserDetail();

        navigationView = (NavigationView) findViewById( R.id.nav_view );


        View headerView = navigationView.getHeaderView(0);
        namaUser = headerView.findViewById( R.id.textViewNamaUser );
        fotoUser =  headerView.findViewById( R.id.imageViewFotoUser );
        levelUser = headerView.findViewById( R.id.textViewLevelUser );

        loadProfile(user.get( sessionManager.KD_USER ));

        if (Integer.parseInt( user.get( sessionManager.LEVEL_USER ) )==0){
            levelUser.setText( "Owner" );
            navigationView.inflateMenu( R.menu.activity_main_drawer );
        }else{
            levelUser.setText( "Kasir" );
            navigationView.inflateMenu( R.menu.activity_main_drawer_kasir );
        }

        navigationView.setNavigationItemSelectedListener( this );

        namaUser.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        } );

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( MainActivity.this,  new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String newToken = instanceIdResult.getToken();
                Log.e("newToken",newToken);
                refreshToken(newToken);
            }
        });


        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
                }
            }
        };
    }

    private void refreshToken(final String newToken) {
        // sending gcm token to server
        StringRequest stringRequest = new StringRequest( Request.Method.POST, baseUrl+"api/user", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String kode = jsonObject.getString("kode");
                    if (kode.equals("1")) {
                        Log.e( TAG, "sendRegistrationToServer: " + newToken );
                    }else {
                        Log.e( TAG, "sendRegistrationToServer: GAGAL" );
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("token_api_key", newToken);
                params.put("kd_user", user.get( SessionManager.KD_USER ));
                params.put("api", "retoken");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById( R.id.drawer_layout );
        if (drawer.isDrawerOpen( GravityCompat.START )) {
            drawer.closeDrawer( GravityCompat.START );
            sessionManager.checkLogin();
        } else {
            super.onBackPressed();
            sessionManager.checkLogin();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        displaySelectedScreen( item.getItemId() );
        return true;
    }

    private void displaySelectedScreen(int itemId) {
        int id = itemId;
        if (id == R.id.nav_dashboard) {
            fragment = new DashboardFragment();
        } else if (id == R.id.nav_transaksi_penjualan) {
            fragment = new TabLayoutFragment(0);
        } else if (id == R.id.nav_transaksi_pembelian) {
            fragment = new TabLayoutFragment(1);
        } else if (id == R.id.nav_biaya) {
            fragment = new TabLayoutFragmentBiaya();
        } else if (id == R.id.nav_laporan_harian) {
            fragment = new LaporanFragment(0);
        } else if (id == R.id.nav_laporan_bulanan) {
            fragment = new LaporanFragment(1);
        } else if (id == R.id.nav_laporan_tahunan) {
            fragment = new LaporanFragment(2);
        }else if (id == R.id.nav_laporan_labarugi) {
            fragment = new LaporanLabaRugiFragment();
        }else if (id == R.id.nav_barang) {
            fragment = new DataBarangFragment();
        }else if (id == R.id.nav_kategori_barang) {
            fragment = new DataKategoriBarangFragment();
        }else if (id == R.id.nav_user) {
            fragment = new DataUserFragment();
        }else if (id == R.id.nav_pelanggan) {
            fragment = new DataPelangganFragment();
        }else if (id == R.id.nav_profile) {
            fragment = new ProfileFragment();
        }else if(id == R.id.setting_printer){
            fragment = new SettingPrinterFragment();
        }else if (id == R.id.nav_logout) {
            sessionManager.logout();
        }

        if (fragment != null) {
            if (fragmentLast!=id){
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.screen_area, fragment);
                ft.addToBackStack(null);
                ft.commit();
            }else if(id==R.id.nav_dashboard){
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.screen_area, fragment);
                ft.addToBackStack(null);
                ft.commit();
            }
        }

        fragmentLast = id;

        DrawerLayout drawer = (DrawerLayout) findViewById( R.id.drawer_layout );
        drawer.closeDrawer( GravityCompat.START );
    }

    private void loadProfile(String kd_user){
        StringRequest stringRequest = new StringRequest( Request.Method.GET, baseUrl+API_URL_LOAD+kd_user,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        final JSONObject userprofile = new JSONObject(response);
                        namaUser.setText( userprofile.getString( "nama_depan" )+" "+userprofile.getString( "nama_belakang" ) );
                        urlGambar = baseUrl+String.valueOf( userprofile.getString( "foto" )  );
                        Glide.with(MainActivity.this)
                                // LOAD URL DARI INTERNET
                                .load(urlGambar)
                                // LOAD GAMBAR AWAL SEBELUM GAMBAR UTAMA MUNCUL, BISA DARI LOKAL DAN INTERNET
                                .into(fotoUser);
                        if(userprofile.getString("status_user").equals("0")){

                        }else if(userprofile.getString("status_user").equals("1")){

                        }else if(userprofile.getString("status_user").equals("2")){

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "Periksa koneksi & coba lagi1", Toast.LENGTH_SHORT).show();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(MainActivity.this, "Periksa koneksi & coba lagi", Toast.LENGTH_SHORT).show();
                }
            }
        );

        RequestQueue requestQueue = Volley.newRequestQueue( MainActivity.this );
        requestQueue.add( stringRequest );
    }

    @Override
    public void onDateRangeSelected(int startDay, int startMonth, int startYear, int endDay, int endMonth, int endYear) {

    }


}
