package com.pratamatechnocraft.tokason;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.pratamatechnocraft.tokason.Adapter.AdapterRecycleViewDataBarang;
import com.pratamatechnocraft.tokason.Model.BaseUrlApiModel;
import com.pratamatechnocraft.tokason.Model.ListItemDataBarang;
import com.pratamatechnocraft.tokason.Service.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BarangTransaksiActivity extends AppCompatActivity {

    private RecyclerView recyclerViewBarangTransaksi;
    private AdapterRecycleViewDataBarang adapterRecycleViewBarangTransaksi;
    LinearLayout noBarangTransaksi, koneksiBarangTransaksi;
    SwipeRefreshLayout refreshBarangTransaksi;
    FloatingActionButton floatingActionButton1;
    ProgressBar progressBarBarangTransaksi;
    Button cobaLagiBarangTransaksi;
    private Intent intent;

    private List<ListItemDataBarang> listItemDataBarangs;

    BaseUrlApiModel baseUrlApiModel = new BaseUrlApiModel();
    private String baseUrl=baseUrlApiModel.getBaseURL();
    private static String API_URL;
    private int checkedKategori=0;
    private String pilihKategori="Semua";
    SessionManager sessionManager;
    private String kdOutlet;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_barang_transaksi );
        intent=getIntent();

        Toolbar toolbar = findViewById(R.id.toolbarbarangtransaksi);
        setSupportActionBar(toolbar);
        this.setTitle("Barang");
        toolbar.setSubtitleTextColor( ContextCompat.getColor(this, R.color.colorIcons) );
        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.ic_close_white_24dp);
        upArrow.setColorFilter(ContextCompat.getColor(this, R.color.colorIcons), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        noBarangTransaksi = findViewById( R.id.noBarangTransaksi );
        refreshBarangTransaksi = findViewById(R.id.refreshBarangTransaksi);
        floatingActionButton1 = findViewById( R.id.floatingActionButton );
        cobaLagiBarangTransaksi = findViewById( R.id.cobaLagiBarang );
        koneksiBarangTransaksi = findViewById( R.id.koneksiBarangTransaksi );
        progressBarBarangTransaksi = findViewById( R.id.progressBarBarangTransaksi );
        recyclerViewBarangTransaksi = findViewById(R.id.recycleViewBarangTransaksi);

        sessionManager = new SessionManager( this );
        HashMap<String, String> user = sessionManager.getUserDetail();
        kdOutlet = user.get(SessionManager.KD_OUTLET);

        loadBarangTransaksi(pilihKategori,kdOutlet);

        refreshBarangTransaksi.setOnRefreshListener( new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                listItemDataBarangs.clear();
                loadBarangTransaksi(pilihKategori,kdOutlet);
            }
        } );

        cobaLagiBarangTransaksi.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                koneksiBarangTransaksi.setVisibility( View.GONE );
                progressBarBarangTransaksi.setVisibility( View.VISIBLE );
                loadBarangTransaksi(pilihKategori,kdOutlet);
            }
        } );

        if (Integer.parseInt( user.get(SessionManager.LEVEL_USER) )==0){
            floatingActionButton1.setVisibility(View.VISIBLE);
        }else{
            floatingActionButton1.setVisibility(View.GONE);
        }


        floatingActionButton1.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent i = new Intent(BarangTransaksiActivity.this, FormBarangActivity.class);
                i.putExtra( "type","tambah" );
                i.putExtra( "typedua","keranjang" );
                if (intent.getStringExtra( "type" ).equals( "0" )){
                    i.putExtra( "typetiga","penjualan" );
                }else{
                    i.putExtra( "typetiga","pembelian" );
                }
                startActivity(i);
            }
        } );
    }

    private void loadBarangTransaksi(String kategori, String kdOutlet){
        if (kategori.equals("Semua")){
            API_URL = "api/barang?api=barangall&kd_outlet="+kdOutlet;
        }else{
            API_URL="api/barang?api=barangkategori&kategori="+kategori+"&kd_outlet="+kdOutlet;
        }
        refreshBarangTransaksi.setEnabled( true );
        listItemDataBarangs = new ArrayList<>();
        StringRequest stringRequest = new StringRequest( Request.Method.GET, baseUrl+API_URL,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getInt( "jml_data" )==0){
                            noBarangTransaksi.setVisibility( View.VISIBLE );
                        }else{
                            noBarangTransaksi.setVisibility( View.GONE );
                            JSONArray data = jsonObject.getJSONArray("data");
                            for (int i = 0; i<data.length(); i++){
                                JSONObject barangobject = data.getJSONObject( i );

                                if(intent.getStringExtra( "type" ).equals( "0" )){
                                    ListItemDataBarang itemDataBarang = new ListItemDataBarang(
                                            barangobject.getString( "kd_barang"),
                                            barangobject.getString( "nama_barang" ),
                                            barangobject.getString( "stok" ),
                                            barangobject.getString( "harga_jual"),
                                            barangobject.getString( "gambar_barang")
                                    );

                                    listItemDataBarangs.add( itemDataBarang );
                                }else{
                                    ListItemDataBarang itemDataBarang = new ListItemDataBarang(
                                            barangobject.getString( "kd_barang"),
                                            barangobject.getString( "nama_barang" ),
                                            barangobject.getString( "stok" ),
                                            barangobject.getString( "harga_beli"),
                                            barangobject.getString( "gambar_barang")
                                    );
                                    listItemDataBarangs.add( itemDataBarang );
                                }
                            }
                        }
                        refreshBarangTransaksi.setRefreshing( false );
                        progressBarBarangTransaksi.setVisibility( View.GONE );
                        koneksiBarangTransaksi.setVisibility( View.GONE);
                        setUpRecycleView();
                    }catch (JSONException e){
                        e.printStackTrace();
                        refreshBarangTransaksi.setRefreshing( false );
                        progressBarBarangTransaksi.setVisibility( View.GONE );
                        noBarangTransaksi.setVisibility( View.GONE );
                        setUpRecycleView();
                        listItemDataBarangs.clear();
                        adapterRecycleViewBarangTransaksi.notifyDataSetChanged();
                        koneksiBarangTransaksi.setVisibility( View.VISIBLE );
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    refreshBarangTransaksi.setRefreshing( false );
                    progressBarBarangTransaksi.setVisibility( View.GONE );
                    noBarangTransaksi.setVisibility( View.GONE );
                    setUpRecycleView();
                    listItemDataBarangs.clear();
                    adapterRecycleViewBarangTransaksi.notifyDataSetChanged();
                    koneksiBarangTransaksi.setVisibility( View.VISIBLE );
                }
            }
        );

        RequestQueue requestQueue = Volley.newRequestQueue( this);
        requestQueue.add( stringRequest );
    }

    private void setUpRecycleView() {
        recyclerViewBarangTransaksi.setHasFixedSize(true);
        recyclerViewBarangTransaksi.setLayoutManager(new LinearLayoutManager(this));
        adapterRecycleViewBarangTransaksi = new AdapterRecycleViewDataBarang( listItemDataBarangs, BarangTransaksiActivity.this,1, Integer.parseInt(intent.getStringExtra("type")));
        recyclerViewBarangTransaksi.setAdapter( adapterRecycleViewBarangTransaksi );
        adapterRecycleViewBarangTransaksi.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            case R.id.ic_kategori:
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Pilih Kategori");

                StringRequest stringRequest1 = new StringRequest( Request.Method.GET, baseUrl+"api/kategori?api=kategoriall&kd_outlet="+kdOutlet,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                final int[] kategorinya = {checkedKategori};
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray data = jsonObject.getJSONArray("data");
                                final CharSequence[] items = new CharSequence[data.length()+1];
                                items[0]="Semua";
                                for (int i = 0; i<data.length(); i++){
                                    JSONObject kategoriobject = data.getJSONObject( i );
                                    items[i+1]= kategoriobject.getString( "nama_kategori");
                                }

                                builder.setSingleChoiceItems(items, checkedKategori, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // user checked an item
                                        pilihKategori =String.valueOf(items[which]);
                                        kategorinya[0]=which;
                                    }
                                });

                                // add OK and Cancel buttons
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // user clicked OK
                                        loadBarangTransaksi(pilihKategori,kdOutlet);
                                        checkedKategori=kategorinya[0];
                                    }
                                });
                                builder.setNegativeButton("Cancel", null);

                                AlertDialog alert = builder.create();
                                alert.show();
                            }catch (JSONException e){
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
                );

                RequestQueue requestQueue1 = Volley.newRequestQueue( this );
                requestQueue1.add( stringRequest1 );

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
        menuInflater.inflate(R.menu.menu_data_barang,menu);
        MenuItem searchItem = menu.findItem(R.id.ic_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener( new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapterRecycleViewBarangTransaksi.getFilter().filter(s);
                return false;
            }
        } );

        searchView.setQueryHint("Search");
        return true;
    }
}
