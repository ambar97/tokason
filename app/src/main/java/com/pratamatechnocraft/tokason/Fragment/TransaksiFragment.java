package com.pratamatechnocraft.tokason.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pratamatechnocraft.tokason.Adapter.AdapterRecycleViewDataTransaksi;
import com.pratamatechnocraft.tokason.Adapter.DBDataSourceKeranjang;
import com.pratamatechnocraft.tokason.Model.BaseUrlApiModel;
import com.pratamatechnocraft.tokason.Model.ListItemTransaksi;
import com.pratamatechnocraft.tokason.R;
import com.pratamatechnocraft.tokason.Service.SessionManager;
import com.pratamatechnocraft.tokason.TransaksiBaruActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@SuppressLint("ValidFragment")
public class TransaksiFragment extends Fragment{

    Integer menuTab,jenisTransaksi;
    private RecyclerView recyclerViewDataTransaksi;
    private AdapterRecycleViewDataTransaksi adapterDataTransaksi;
    LinearLayout noDataTransaksi, koneksiDataTransaksi, linearLayoutTotalPiutangAtauHutang;
    SwipeRefreshLayout refreshDataTransaksi;
    ProgressBar progressBarDataTransaksi;
    Button cobaLagiDataTransaksi;
    FloatingActionButton fabTransaksiBaru;
    TextView txtTotalPiutangAtauHutang;
    private DBDataSourceKeranjang dbDataSourceKeranjang;

    private List<ListItemTransaksi> listItemTransaksis;

    BaseUrlApiModel baseUrlApiModel = new BaseUrlApiModel();
    private String baseUrl=baseUrlApiModel.getBaseURL();
    private static String API_URL ="";
    private Boolean statusFragment = false;
    private int checkedPeriode=0;
    private String pilihPeriode="Hari Ini";

    SessionManager sessionManager;
    HashMap<String, String> user;

    NavigationView navigationView;

    public TransaksiFragment(Integer menuTab, Integer jenisTransaksi) {
        this.menuTab = menuTab;
        this.jenisTransaksi = jenisTransaksi;
    }

    @SuppressLint("RestrictedApi")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.fragment_transaksi, container, false);
        navigationView = getActivity().findViewById( R.id.nav_view );
        noDataTransaksi = view.findViewById( R.id.noDataTransaksi );
        refreshDataTransaksi = (SwipeRefreshLayout) view.findViewById(R.id.refreshDataTransaksi);
        cobaLagiDataTransaksi = view.findViewById( R.id.cobaLagiTransaksi );
        koneksiDataTransaksi = view.findViewById( R.id.koneksiDataTransaksi );
        fabTransaksiBaru = view.findViewById( R.id.fabTransaksiBaru );
        progressBarDataTransaksi = view.findViewById( R.id.progressBarDataTransaksi );
        linearLayoutTotalPiutangAtauHutang = view.findViewById(R.id.linearLayoutTotalPiutangAtauHutang);
        txtTotalPiutangAtauHutang = view.findViewById(R.id.txtTotalPiutangAtauHutang);

        sessionManager = new SessionManager( getContext() );
        user = sessionManager.getUserDetail();


        recyclerViewDataTransaksi = (RecyclerView) view.findViewById(R.id.recycleViewDataTransaksi);

        if (menuTab==0){
            fabTransaksiBaru.setVisibility( View.VISIBLE );
            linearLayoutTotalPiutangAtauHutang.setVisibility(View.GONE);
        }else {
            fabTransaksiBaru.setVisibility( View.GONE );
            linearLayoutTotalPiutangAtauHutang.setVisibility(View.VISIBLE);
        }

        fabTransaksiBaru.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbDataSourceKeranjang = new DBDataSourceKeranjang( getContext() );
                dbDataSourceKeranjang.open();
                dbDataSourceKeranjang.deleteAll();
                dbDataSourceKeranjang.open();
                dbDataSourceKeranjang.deletePelangganPilihAll();
                Intent i = new Intent(getContext(), TransaksiBaruActivity.class );
                if (jenisTransaksi==0) {
                    i.putExtra( "type", "0" );
                }else {
                    i.putExtra( "type", "1" );
                }
                i.putExtra("form","0");
                startActivity(i);
            }
        } );

        refreshDataTransaksi.setOnRefreshListener( new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                listItemTransaksis.clear();
                if (jenisTransaksi==0){
                    if (menuTab==0){
                        loadDataTransaksi("penjualan", pilihPeriode);
                    }else {
                        loadDataTransaksi("piutang", pilihPeriode);
                    }
                }else{
                    if (menuTab==0){
                        loadDataTransaksi("pembelian", pilihPeriode);
                    }else {
                        loadDataTransaksi("utang", pilihPeriode);
                    }
                }
            }
        } );

        cobaLagiDataTransaksi.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                koneksiDataTransaksi.setVisibility( View.GONE );
                progressBarDataTransaksi.setVisibility( View.VISIBLE );
                if (jenisTransaksi==0){
                    if (menuTab==0){
                        loadDataTransaksi("penjualan", pilihPeriode);
                    }else {
                        loadDataTransaksi("piutang", pilihPeriode);
                    }
                }else{
                    if (menuTab==0){
                        loadDataTransaksi("pembelian", pilihPeriode);
                    }else {
                        loadDataTransaksi("utang", pilihPeriode);
                    }
                }
            }
        } );

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        setHasOptionsMenu( true );
        if (jenisTransaksi==0){
            navigationView.setCheckedItem(R.id.nav_transaksi_penjualan);
            getActivity().setTitle("Trasaksi Penjualan");
            if (menuTab==0){
                loadDataTransaksi("penjualan", pilihPeriode);
            }else {
                loadDataTransaksi("piutang", pilihPeriode);
            }
        }else{
            navigationView.setCheckedItem(R.id.nav_transaksi_pembelian);
            getActivity().setTitle("Trasaksi Pembelian");
            if (menuTab==0){
                loadDataTransaksi("pembelian", pilihPeriode);
            }else {
                loadDataTransaksi("utang", pilihPeriode);
            }
        }
        statusFragment=false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_data_barang, menu);
        MenuItem searchItem = menu.findItem(R.id.ic_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener( new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapterDataTransaksi.getFilter().filter(s);
                return false;
            }
        } );
        searchView.setQueryHint("Cari: No Invoice, Tanggal");
        if (searchView.isActivated()){
            refreshDataTransaksi.setEnabled(false);
        }else{
            refreshDataTransaksi.setEnabled(true);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ic_kategori:
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Pilih Periode");
                final int[] periodePilih = {checkedPeriode};
                final CharSequence[] items= new CharSequence[]{"Hari Ini", "1 Minggu Terakhir", "1 Bulan Terakhir", "1 Tahun Terakhir", "Semua"};

                builder.setSingleChoiceItems(items, checkedPeriode, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // user checked an item
                        pilihPeriode =String.valueOf(items[which]);
                        periodePilih[0]=which;
                        Log.d("TAG", "onClick: "+pilihPeriode);
                    }
                });

                // add OK and Cancel buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // user clicked OK
                        listItemTransaksis.clear();
                        if (jenisTransaksi==0){
                            if (menuTab==0){
                                loadDataTransaksi("penjualan", pilihPeriode);
                            }else {
                                loadDataTransaksi("piutang", pilihPeriode);
                            }
                        }else{
                            if (menuTab==0){
                                loadDataTransaksi("pembelian", pilihPeriode);
                            }else {
                                loadDataTransaksi("utang", pilihPeriode);
                            }
                        }
                        checkedPeriode=periodePilih[0];

                    }
                });
                builder.setNegativeButton("Cancel", null);

                AlertDialog alert = builder.create();
                alert.show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadDataTransaksi(String jenis, String periode){
        listItemTransaksis = new ArrayList<>();
        if (jenis.equals( "penjualan" )){
            API_URL = "api/transaksi?api=penjualan";
        }else if (jenis.equals( "piutang" )){
            API_URL = "api/transaksi?api=piutang";
        }else if (jenis.equals( "pembelian" )){
            API_URL = "api/transaksi?api=pembelian";
        }else if (jenis.equals( "utang" )){
            API_URL = "api/transaksi?api=utang";
        }

        StringRequest stringRequest = new StringRequest( Request.Method.GET, baseUrl+API_URL+"&periode="+periode+"&kd_outlet="+user.get(SessionManager.KD_OUTLET),
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getInt( "jml_data" )==0){
                            noDataTransaksi.setVisibility( View.VISIBLE );
                        }else{
                            noDataTransaksi.setVisibility( View.GONE );
                            JSONArray data = jsonObject.getJSONArray("data");
                            for (int i = 0; i<data.length(); i++){
                                JSONObject transaksiobject = data.getJSONObject( i );
                                String jenis;
                                Double pajak, diskon;
                                int total;
                                diskon=(Double.parseDouble(transaksiobject.getString( "diskon" ))/100)*Double.parseDouble(transaksiobject.getString( "harga_total" ));
                                pajak=(Double.parseDouble(transaksiobject.getString( "pajak" ))/100)*(Double.parseDouble(transaksiobject.getString( "harga_total" ))-diskon);
                                total=(Integer.parseInt(transaksiobject.getString( "harga_total" ))-diskon.intValue())+pajak.intValue();
                                if(transaksiobject.getString( "jenis_transaksi" ).equals( "0" )){
                                    jenis="PL";
                                }else{
                                    jenis="PB";
                                }

                                ListItemTransaksi listItemTransaksi = new ListItemTransaksi(
                                        "#"+jenis+transaksiobject.getString( "kd_transaksi"),
                                        "#"+jenis+transaksiobject.getString( "no_invoice"),
                                        String.valueOf(total),
                                        transaksiobject.getString( "tgl_transaksi" )
                                );

                                listItemTransaksis.add( listItemTransaksi );
                            }
                        }
                        refreshDataTransaksi.setRefreshing( false );
                        progressBarDataTransaksi.setVisibility( View.GONE );
                        koneksiDataTransaksi.setVisibility( View.GONE);
                        setUpRecycleView();
                    }catch (JSONException e){

                        Log.e("ERR", "onErrorResponse: ", e);
                        e.printStackTrace();
                        refreshDataTransaksi.setRefreshing( false );
                        progressBarDataTransaksi.setVisibility( View.GONE );
                        noDataTransaksi.setVisibility( View.GONE );
                        setUpRecycleView();
                        listItemTransaksis.clear();
                        adapterDataTransaksi.notifyDataSetChanged();
                        koneksiDataTransaksi.setVisibility( View.VISIBLE );
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("ERR", "onErrorResponse: ", error);
                    error.printStackTrace();
                    refreshDataTransaksi.setRefreshing( false );
                    progressBarDataTransaksi.setVisibility( View.GONE );
                    noDataTransaksi.setVisibility( View.GONE );
                    setUpRecycleView();
                    listItemTransaksis.clear();
                    adapterDataTransaksi.notifyDataSetChanged();
                    koneksiDataTransaksi.setVisibility( View.VISIBLE );
                }
            }
        );

        RequestQueue requestQueue = Volley.newRequestQueue( getContext() );
        requestQueue.add( stringRequest );
    }

    private void setUpRecycleView(){
        recyclerViewDataTransaksi.setHasFixedSize(true);
        recyclerViewDataTransaksi.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterDataTransaksi = new AdapterRecycleViewDataTransaksi( listItemTransaksis, getContext(), jenisTransaksi, txtTotalPiutangAtauHutang);
        recyclerViewDataTransaksi.setAdapter( adapterDataTransaksi );
        adapterDataTransaksi.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        statusFragment=true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (jenisTransaksi==0) {
            if (menuTab==0){
                if (statusFragment){
                    loadDataTransaksi("penjualan", pilihPeriode);
                }
            }else {
                if (statusFragment){
                    loadDataTransaksi("piutang", pilihPeriode);
                }
            }
        }else {
            if (menuTab==0){
                if (statusFragment){
                    loadDataTransaksi("pembelian", pilihPeriode);
                }
            }else {
                if (statusFragment){
                    loadDataTransaksi("utang", pilihPeriode);
                }
            }
        }

    }
}
