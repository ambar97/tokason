package com.pratamatechnocraft.tokason.Fragment;

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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.pratamatechnocraft.tokason.FormBarangActivity;
import com.pratamatechnocraft.tokason.Model.BaseUrlApiModel;
import com.pratamatechnocraft.tokason.Model.ListItemDataBarang;
import com.pratamatechnocraft.tokason.R;
import com.pratamatechnocraft.tokason.Service.SessionManager;
//import com.pratamatechnocraft.silaporanpenjualan.TambahSuratMasukActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataBarangFragment extends Fragment {

    private RecyclerView recyclerViewDataBarang;
    private AdapterRecycleViewDataBarang adapterDataBarang;
    LinearLayout noDataBarang, koneksiDataBarang;
    SwipeRefreshLayout refreshDataBarang;
    FloatingActionButton floatingActionButton1;
    ProgressBar progressBarDataBarang;
    Button cobaLagiDataBarang;
    SessionManager sessionManager;
    private Boolean statusFragment = false;

    private List<ListItemDataBarang> listItemDataBarangs;

    BaseUrlApiModel baseUrlApiModel = new BaseUrlApiModel();
    private String baseUrl=baseUrlApiModel.getBaseURL();
    private static String API_URL;
    private int checkedKategori=0;
    private String pilihKategori="Semua";
    HashMap<String, String> user;
    NavigationView navigationView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.fragment_data_barang, container, false);
        navigationView = getActivity().findViewById( R.id.nav_view );
        noDataBarang = view.findViewById( R.id.noDataBarang );
        refreshDataBarang = (SwipeRefreshLayout) view.findViewById(R.id.refreshDataBarang);
        floatingActionButton1 = view.findViewById( R.id.floatingActionButton );
        cobaLagiDataBarang = view.findViewById( R.id.cobaLagiBarang );
        koneksiDataBarang = view.findViewById( R.id.koneksiDataBarang );
        progressBarDataBarang = view.findViewById( R.id.progressBarDataBarang );
        recyclerViewDataBarang = (RecyclerView) view.findViewById(R.id.recycleViewDataBarang);

        sessionManager = new SessionManager( getContext() );
        user = sessionManager.getUserDetail();


        refreshDataBarang.setOnRefreshListener( new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadDataBarang(pilihKategori,user.get(SessionManager.KD_OUTLET));
            }
        } );

        cobaLagiDataBarang.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                koneksiDataBarang.setVisibility( View.GONE );
                progressBarDataBarang.setVisibility( View.VISIBLE );
                loadDataBarang(pilihKategori,user.get(SessionManager.KD_OUTLET));
            }
        } );


        floatingActionButton1.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), FormBarangActivity.class);
                i.putExtra( "type","tambah" );
                i.putExtra( "typedua","" );
                i.putExtra( "typetiga","" );
                getContext().startActivity(i);
            }
        } );

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Data Barang");
        setHasOptionsMenu( true );
        loadDataBarang(pilihKategori,user.get(SessionManager.KD_OUTLET));
        statusFragment=false;
        navigationView.setCheckedItem(R.id.nav_barang);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ic_kategori:
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Pilih Kategori");

                StringRequest stringRequest1 = new StringRequest( Request.Method.GET, baseUrl+"api/kategori?api=kategoriall&kd_outlet="+user.get(SessionManager.KD_OUTLET),
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
                                            loadDataBarang(pilihKategori,user.get(SessionManager.KD_OUTLET));
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

                RequestQueue requestQueue1 = Volley.newRequestQueue( getContext() );
                requestQueue1.add( stringRequest1 );

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
                adapterDataBarang.getFilter().filter(s);
                return false;
            }
        } );
        
        searchView.setQueryHint("Search");

    }

    @Override
    public void onPause() {
        super.onPause();
        statusFragment=true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (statusFragment) {
            loadDataBarang(pilihKategori,user.get(SessionManager.KD_OUTLET));
        }
    }

    private void loadDataBarang(String kategori, String kdOutlet){
        if (kategori.equals("Semua")){
            API_URL = "api/barang?api=barangall&kd_outlet="+kdOutlet;
        }else{
            API_URL="api/barang?api=barangkategori&kategori="+kategori+"&kd_outlet="+kdOutlet;
        }
        refreshDataBarang.setEnabled( true );
        listItemDataBarangs = new ArrayList<>();
        listItemDataBarangs.clear();

        StringRequest stringRequest = new StringRequest( Request.Method.GET, baseUrl+API_URL,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getInt( "jml_data" )==0){
                            noDataBarang.setVisibility( View.VISIBLE );
                        }else{
                            noDataBarang.setVisibility( View.GONE );
                            JSONArray data = jsonObject.getJSONArray("data");
                            for (int i = 0; i<data.length(); i++){
                                JSONObject barangobject = data.getJSONObject( i );

                                ListItemDataBarang listItemDataBarang = new ListItemDataBarang(
                                        barangobject.getString( "kd_barang"),
                                        barangobject.getString( "nama_barang" ),
                                        barangobject.getString( "stok" ),
                                        barangobject.getString( "harga_jual"),
                                        barangobject.getString( "gambar_barang")
                                );

                                listItemDataBarangs.add( listItemDataBarang );
                            }
                        }
                        refreshDataBarang.setRefreshing( false );
                        progressBarDataBarang.setVisibility( View.GONE );
                        koneksiDataBarang.setVisibility( View.GONE);
                        setUpRecycleView();
                    }catch (JSONException e){
                        e.printStackTrace();
                        refreshDataBarang.setRefreshing( false );
                        progressBarDataBarang.setVisibility( View.GONE );
                        noDataBarang.setVisibility( View.GONE );
                        setUpRecycleView();
                        listItemDataBarangs.clear();
                        adapterDataBarang.notifyDataSetChanged();
                        koneksiDataBarang.setVisibility( View.VISIBLE );
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    refreshDataBarang.setRefreshing( false );
                    progressBarDataBarang.setVisibility( View.GONE );
                    noDataBarang.setVisibility( View.GONE );
                    setUpRecycleView();
                    listItemDataBarangs.clear();
                    adapterDataBarang.notifyDataSetChanged();
                    koneksiDataBarang.setVisibility( View.VISIBLE );
                }
            }
        );

        RequestQueue requestQueue = Volley.newRequestQueue( getContext() );
        requestQueue.add( stringRequest );
    }

    private void setUpRecycleView() {
        recyclerViewDataBarang.setHasFixedSize(true);
        recyclerViewDataBarang.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterDataBarang = new AdapterRecycleViewDataBarang( listItemDataBarangs, getContext(),0,null);
        recyclerViewDataBarang.setAdapter( adapterDataBarang );
        adapterDataBarang.notifyDataSetChanged();
    }
}
