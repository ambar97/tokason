package com.pratamatechnocraft.tokason.Fragment;

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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pratamatechnocraft.tokason.Adapter.AdapterRecycleViewDataPelanggan;
import com.pratamatechnocraft.tokason.FormPelangganActivity;
import com.pratamatechnocraft.tokason.Model.BaseUrlApiModel;
import com.pratamatechnocraft.tokason.Model.ListItemDataPelanggan;
import com.pratamatechnocraft.tokason.R;
import com.pratamatechnocraft.tokason.Service.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataPelangganFragment extends Fragment {

    private RecyclerView recyclerViewDataPelanggan;
    private AdapterRecycleViewDataPelanggan adapterDataPelanggan;
    LinearLayout noDataPelanggan, koneksiDataPelanggan;
    SwipeRefreshLayout refreshDataPelanggan;
    FloatingActionButton fabTambahDataPelanggan;
    ProgressBar progressBarDataPelanggan;
    Button cobaLagiDataPelanggan;
    SessionManager sessionManager;
    HashMap<String, String> user;
    NavigationView navigationView;

    private Boolean statusFragment = false;

    private List<ListItemDataPelanggan> listItemDataPelanggans;

    BaseUrlApiModel baseUrlApiModel = new BaseUrlApiModel();
    private String baseUrl=baseUrlApiModel.getBaseURL();
    private static final String API_URL = "api/pelanggan?api=pelangganall&kd_outlet=";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.fragment_data_pelanggan, container, false);
        navigationView = getActivity().findViewById( R.id.nav_view );

        noDataPelanggan = view.findViewById( R.id.noDataPelanggan );
        refreshDataPelanggan = (SwipeRefreshLayout) view.findViewById(R.id.refreshDataPelanggan);
        fabTambahDataPelanggan = view.findViewById( R.id.fabTambahDataPelanggan );
        cobaLagiDataPelanggan = view.findViewById( R.id.cobaLagiPelanggan );
        koneksiDataPelanggan = view.findViewById( R.id.koneksiDataPelanggan );
        progressBarDataPelanggan = view.findViewById( R.id.progressBarDataPelanggan );
        recyclerViewDataPelanggan = (RecyclerView) view.findViewById(R.id.recycleViewDataPelanggan);

        sessionManager = new SessionManager( getContext() );
        user = sessionManager.getUserDetail();

        refreshDataPelanggan.setOnRefreshListener( new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                listItemDataPelanggans.clear();
                adapterDataPelanggan.notifyDataSetChanged();
                loadDataPelanggan(user.get(SessionManager.KD_OUTLET));
            }
        } );

        cobaLagiDataPelanggan.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                koneksiDataPelanggan.setVisibility( View.GONE );
                progressBarDataPelanggan.setVisibility( View.VISIBLE );
                loadDataPelanggan(user.get(SessionManager.KD_OUTLET));
            }
        } );

        fabTambahDataPelanggan.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), FormPelangganActivity.class);
                i.putExtra( "type","tambah" );
                i.putExtra( "typedua","" );
                getContext().startActivity(i);
            }
        } );

        return view;
    }



    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Data Pelanggan");
        setHasOptionsMenu( true );
        loadDataPelanggan(user.get(SessionManager.KD_OUTLET));
        statusFragment=false;
        navigationView.setCheckedItem(R.id.nav_pelanggan);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.ic_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener( new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapterDataPelanggan.getFilter().filter(s);
                return false;
            }
        } );
        searchView.setQueryHint("Cari: Nama, No Telp");

    }

    @Override
    public void onPause() {
        super.onPause();
        statusFragment=true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (statusFragment){
            loadDataPelanggan(user.get(SessionManager.KD_OUTLET));
        }
    }

    private void loadDataPelanggan(String kdOutlet){
        listItemDataPelanggans = new ArrayList<>();

        StringRequest stringRequest = new StringRequest( Request.Method.GET, baseUrl+API_URL+kdOutlet,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getInt( "jml_data" )==0){
                                noDataPelanggan.setVisibility( View.VISIBLE );
                            }else{
                                noDataPelanggan.setVisibility( View.GONE );
                                JSONArray data = jsonObject.getJSONArray("data");
                                for (int i = 0; i<data.length(); i++){
                                    JSONObject pelangganobject = data.getJSONObject( i );

                                    ListItemDataPelanggan listItemDataPelanggan = new ListItemDataPelanggan(
                                            pelangganobject.getString( "kd_pelanggan"),
                                            pelangganobject.getString( "nama_pelanggan" ),
                                            pelangganobject.getString( "no_telp_pelanggan" ),
                                            pelangganobject.getString( "alamat_pelanggan" ),
                                            pelangganobject.getString( "tgl_ditambahkan")
                                    );

                                    listItemDataPelanggans.add( listItemDataPelanggan );
                                    //adapterDataPelanggan.notifyDataSetChanged();
                                }
                            }
                            refreshDataPelanggan.setRefreshing( false );
                            progressBarDataPelanggan.setVisibility( View.GONE );
                            koneksiDataPelanggan.setVisibility( View.GONE);
                            setUpRecycleView();
                        }catch (JSONException e){
                            e.printStackTrace();
                            Log.d( "TAG", e.toString() );
                            refreshDataPelanggan.setRefreshing( false );
                            progressBarDataPelanggan.setVisibility( View.GONE );
                            noDataPelanggan.setVisibility( View.GONE );
                            setUpRecycleView();
                            listItemDataPelanggans.clear();
                            adapterDataPelanggan.notifyDataSetChanged();
                            koneksiDataPelanggan.setVisibility( View.VISIBLE );
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Log.d( "TAG", error.toString() );
                        refreshDataPelanggan.setRefreshing( false );
                        progressBarDataPelanggan.setVisibility( View.GONE );
                        noDataPelanggan.setVisibility( View.GONE );
                        setUpRecycleView();
                        listItemDataPelanggans.clear();
                        adapterDataPelanggan.notifyDataSetChanged();
                        koneksiDataPelanggan.setVisibility( View.VISIBLE );
                    }
                }
        );

        RequestQueue requestQueue = Volley.newRequestQueue( getContext() );
        requestQueue.add( stringRequest );
    }

    private void setUpRecycleView(){
        recyclerViewDataPelanggan.setHasFixedSize(true);
        recyclerViewDataPelanggan.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterDataPelanggan = new AdapterRecycleViewDataPelanggan( listItemDataPelanggans, getContext(), 0);
        recyclerViewDataPelanggan.setAdapter( adapterDataPelanggan );
        adapterDataPelanggan.notifyDataSetChanged();
    }
}
