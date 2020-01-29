package com.pratamatechnocraft.tokason;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pratamatechnocraft.tokason.Adapter.AdapterRecycleViewDataPelanggan;
import com.pratamatechnocraft.tokason.Model.BaseUrlApiModel;
import com.pratamatechnocraft.tokason.Model.ListItemDataPelanggan;
import com.pratamatechnocraft.tokason.Service.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PelangganTransaksiActivity extends AppCompatActivity {

    private RecyclerView recyclerViewPelangganTransaksi;
    private AdapterRecycleViewDataPelanggan adapterRecycleViewPelangganTransaksi;
    LinearLayout noPelangganTransaksi, koneksiPelangganTransaksi;
    SwipeRefreshLayout refreshPelangganTransaksi;
    FloatingActionButton floatingActionButton1;
    ProgressBar progressBarPelangganTransaksi;
    Button cobaLagiPelangganTransaksi;
    SessionManager sessionManager;
    private Intent intent;

    private List<ListItemDataPelanggan> listItemDataPelanggans;

    BaseUrlApiModel baseUrlApiModel = new BaseUrlApiModel();
    private String baseUrl=baseUrlApiModel.getBaseURL();
    private static String API_URL;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_pelanggan_transaksi );
        intent=getIntent();

        Toolbar toolbar = findViewById(R.id.toolbarpelanggantransaksi);
        setSupportActionBar(toolbar);
        this.setTitle("Pelanggan");
        toolbar.setSubtitleTextColor( ContextCompat.getColor(this, R.color.colorIcons) );
        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.ic_close_white_24dp);
        upArrow.setColorFilter(ContextCompat.getColor(this, R.color.colorIcons), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        noPelangganTransaksi = findViewById( R.id.noPelangganTransaksi );
        refreshPelangganTransaksi = findViewById(R.id.refreshPelangganTransaksi);
        floatingActionButton1 = findViewById( R.id.floatingActionButton );
        cobaLagiPelangganTransaksi = findViewById( R.id.cobaLagiPelanggan );
        koneksiPelangganTransaksi = findViewById( R.id.koneksiPelangganTransaksi );
        progressBarPelangganTransaksi = findViewById( R.id.progressBarPelangganTransaksi );
        recyclerViewPelangganTransaksi = findViewById(R.id.recycleViewPelangganTransaksi);

        sessionManager = new SessionManager( this );
        final HashMap<String, String> user = sessionManager.getUserDetail();

        loadPelangganTransaksi(user.get(SessionManager.KD_OUTLET));

        refreshPelangganTransaksi.setOnRefreshListener( new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                listItemDataPelanggans.clear();
                loadPelangganTransaksi(user.get(SessionManager.KD_OUTLET));
            }
        } );

        cobaLagiPelangganTransaksi.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                koneksiPelangganTransaksi.setVisibility( View.GONE );
                progressBarPelangganTransaksi.setVisibility( View.VISIBLE );
                loadPelangganTransaksi(user.get(SessionManager.KD_OUTLET));
            }
        } );

        floatingActionButton1.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent i = new Intent(PelangganTransaksiActivity.this, FormPelangganActivity.class);
                i.putExtra( "type","tambah" );
                i.putExtra( "typedua","keranjang" );
                startActivity(i);
            }
        } );
    }

    private void loadPelangganTransaksi(String kdOutlet){
        API_URL = "api/pelanggan?api=pelangganall&kd_outlet="+kdOutlet;
        refreshPelangganTransaksi.setEnabled( true );
        listItemDataPelanggans = new ArrayList<>();
        StringRequest stringRequest = new StringRequest( Request.Method.GET, baseUrl+API_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getInt( "jml_data" )==0){
                                noPelangganTransaksi.setVisibility( View.VISIBLE );
                            }else{
                                noPelangganTransaksi.setVisibility( View.GONE );
                                JSONArray data = jsonObject.getJSONArray("data");
                                for (int i = 0; i<data.length(); i++){
                                    JSONObject pelangganobject = data.getJSONObject( i );

                                    ListItemDataPelanggan itemDataPelanggan = new ListItemDataPelanggan(
                                            pelangganobject.getString( "kd_pelanggan"),
                                            pelangganobject.getString( "nama_pelanggan" ),
                                            pelangganobject.getString( "no_telp_pelanggan" ),
                                            pelangganobject.getString( "alamat_pelanggan" ),
                                            pelangganobject.getString( "tgl_ditambahkan")
                                    );

                                    listItemDataPelanggans.add( itemDataPelanggan );
                                }
                            }
                            refreshPelangganTransaksi.setRefreshing( false );
                            progressBarPelangganTransaksi.setVisibility( View.GONE );
                            koneksiPelangganTransaksi.setVisibility( View.GONE);
                            setUpRecycleView();
                        }catch (JSONException e){
                            e.printStackTrace();
                            refreshPelangganTransaksi.setRefreshing( false );
                            progressBarPelangganTransaksi.setVisibility( View.GONE );
                            noPelangganTransaksi.setVisibility( View.GONE );
                            setUpRecycleView();
                            listItemDataPelanggans.clear();
                            adapterRecycleViewPelangganTransaksi.notifyDataSetChanged();
                            koneksiPelangganTransaksi.setVisibility( View.VISIBLE );
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        refreshPelangganTransaksi.setRefreshing( false );
                        progressBarPelangganTransaksi.setVisibility( View.GONE );
                        noPelangganTransaksi.setVisibility( View.GONE );
                        setUpRecycleView();
                        listItemDataPelanggans.clear();
                        adapterRecycleViewPelangganTransaksi.notifyDataSetChanged();
                        koneksiPelangganTransaksi.setVisibility( View.VISIBLE );
                    }
                }
        );

        RequestQueue requestQueue = Volley.newRequestQueue( this);
        requestQueue.add( stringRequest );
    }

    private void setUpRecycleView() {
        recyclerViewPelangganTransaksi.setHasFixedSize(true);
        recyclerViewPelangganTransaksi.setLayoutManager(new LinearLayoutManager(this));
        adapterRecycleViewPelangganTransaksi = new AdapterRecycleViewDataPelanggan( listItemDataPelanggans, PelangganTransaksiActivity.this,1);
        recyclerViewPelangganTransaksi.setAdapter( adapterRecycleViewPelangganTransaksi );
        adapterRecycleViewPelangganTransaksi.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_search,menu);
        MenuItem searchItem = menu.findItem(R.id.ic_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener( new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapterRecycleViewPelangganTransaksi.getFilter().filter(s);
                return false;
            }
        } );

        searchView.setQueryHint("Search");
        return true;
    }
}
