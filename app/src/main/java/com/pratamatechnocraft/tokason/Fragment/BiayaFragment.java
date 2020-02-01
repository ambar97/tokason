package com.pratamatechnocraft.tokason.Fragment;

import android.annotation.SuppressLint;
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
import com.pratamatechnocraft.tokason.Adapter.AdapterRecycleViewDataBiaya;
import com.pratamatechnocraft.tokason.Model.BaseUrlApiModel;
import com.pratamatechnocraft.tokason.Model.ListItemBiaya;
import com.pratamatechnocraft.tokason.R;
import com.pratamatechnocraft.tokason.Service.SessionManager;
import com.pratamatechnocraft.tokason.FormBiayaActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressLint("ValidFragment")
public class BiayaFragment extends Fragment {

    private RecyclerView recyclerViewDataBiaya;
    private AdapterRecycleViewDataBiaya adapterDataBiaya;
    LinearLayout noDataBiaya, koneksiDataBiaya;
    SwipeRefreshLayout refreshDataBiaya;
    FloatingActionButton fabTambahDataBiaya;
    ProgressBar progressBarDataBiaya;
    Button cobaLagiDataBiaya;
    SessionManager sessionManager;
    private Boolean statusFragment = false;

    private List<ListItemBiaya> listItemBiayas;

    BaseUrlApiModel baseUrlApiModel = new BaseUrlApiModel();
    private String baseUrl=baseUrlApiModel.getBaseURL();
    private static String API_URL = "";

    private int jenisBiaya;
    HashMap<String, String> user;
    NavigationView navigationView;

    @SuppressLint("ValidFragment")
    public BiayaFragment(int jenisBiaya) {
        this.jenisBiaya = jenisBiaya;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.fragment_biaya, container, false);
        navigationView = getActivity().findViewById(R.id.nav_view);
//        navigationView.setCheckedItem(R.id.nav_biaya);
        noDataBiaya = view.findViewById( R.id.noDataBiaya );
        refreshDataBiaya = view.findViewById(R.id.refreshDataBiaya);
        fabTambahDataBiaya = view.findViewById( R.id.fabTambahDataBiaya );
        cobaLagiDataBiaya = view.findViewById( R.id.cobaLagiBiaya );
        koneksiDataBiaya = view.findViewById( R.id.koneksiDataBiaya );
        progressBarDataBiaya = view.findViewById( R.id.progressBarDataBiaya );
        recyclerViewDataBiaya = view.findViewById(R.id.recycleViewDataBiaya);

        sessionManager = new SessionManager( getContext() );
        user = sessionManager.getUserDetail();

        refreshDataBiaya.setOnRefreshListener( new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (jenisBiaya==0){
                    adapterDataBiaya = new AdapterRecycleViewDataBiaya( listItemBiayas, getContext(), 0);
                }else{
                    adapterDataBiaya = new AdapterRecycleViewDataBiaya( listItemBiayas, getContext(), 1);
                }
                recyclerViewDataBiaya.setAdapter( adapterDataBiaya );
                listItemBiayas.clear();
                adapterDataBiaya.notifyDataSetChanged();
                loadBiaya(user.get(SessionManager.KD_OUTLET));
            }
        } );

        cobaLagiDataBiaya.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                koneksiDataBiaya.setVisibility( View.GONE );
                progressBarDataBiaya.setVisibility( View.VISIBLE );
                loadBiaya(user.get(SessionManager.KD_OUTLET));
            }
        } );

        fabTambahDataBiaya.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), FormBiayaActivity.class);
                i.putExtra( "type","tambah" );
                if (jenisBiaya==0){
                    i.putExtra( "jenisBiaya","0" );
                }else{
                    i.putExtra( "jenisBiaya","1" );
                }
                getContext().startActivity(i);
            }
        } );

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Biaya");
        setHasOptionsMenu( true );
        loadBiaya(user.get(SessionManager.KD_OUTLET));
        statusFragment=false;
    }

    @Override
    public void onPause() {
        super.onPause();
        statusFragment = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (statusFragment==true) {
            if (jenisBiaya==0){
                adapterDataBiaya = new AdapterRecycleViewDataBiaya( listItemBiayas, getContext(), 0);
            }else{
                adapterDataBiaya = new AdapterRecycleViewDataBiaya( listItemBiayas, getContext(), 1);
            }
            recyclerViewDataBiaya.setAdapter( adapterDataBiaya );
            listItemBiayas.clear();
            adapterDataBiaya.notifyDataSetChanged();
            loadBiaya(user.get(SessionManager.KD_OUTLET));

        }
    }


    private void loadBiaya(String kdOutlet){
        listItemBiayas = new ArrayList<>();
        if (jenisBiaya==0){
            API_URL = "api/biaya?api=biayatetap&kd_outlet="+kdOutlet;
        }else{
            API_URL = "api/biaya?api=biayatidaktetap&kd_outlet="+kdOutlet;
        }

        StringRequest stringRequest = new StringRequest( Request.Method.GET, baseUrl+API_URL,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getInt( "jml_data" )==0){
                            noDataBiaya.setVisibility( View.VISIBLE );
                        }else{
                            noDataBiaya.setVisibility( View.GONE );
                            JSONArray data = jsonObject.getJSONArray("data");
                            for (int i = 0; i<data.length(); i++){
                                JSONObject biayaobject = data.getJSONObject( i );

                                ListItemBiaya listItemBiaya = new ListItemBiaya(
                                        biayaobject.getString( "kd_biaya"),
                                        biayaobject.getString( "nama_biaya" ),
                                        "Rp. "+biayaobject.getString( "jumlah_biaya" ),
                                        biayaobject.getString( "tgl_biaya" )
                                );

                                listItemBiayas.add( listItemBiaya );
                            }
                        }
                        refreshDataBiaya.setRefreshing( false );
                        progressBarDataBiaya.setVisibility( View.GONE );
                        koneksiDataBiaya.setVisibility( View.GONE);
                        setUpRecycleView();
                    }catch (JSONException e){
                        e.printStackTrace();
                        Log.d( "TAG", e.toString() );
                        refreshDataBiaya.setRefreshing( false );
                        progressBarDataBiaya.setVisibility( View.GONE );
                        noDataBiaya.setVisibility( View.GONE );
                        if (jenisBiaya==0){
                            adapterDataBiaya = new AdapterRecycleViewDataBiaya( listItemBiayas, getContext(), 0);
                        }else{
                            adapterDataBiaya = new AdapterRecycleViewDataBiaya( listItemBiayas, getContext(), 1);
                        }
                        recyclerViewDataBiaya.setAdapter( adapterDataBiaya );
                        listItemBiayas.clear();
                        adapterDataBiaya.notifyDataSetChanged();
                        koneksiDataBiaya.setVisibility( View.VISIBLE );
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Log.d( "TAG", error.toString() );
                    refreshDataBiaya.setRefreshing( false );
                    progressBarDataBiaya.setVisibility( View.GONE );
                    noDataBiaya.setVisibility( View.GONE );
                    if (jenisBiaya==0){
                        adapterDataBiaya = new AdapterRecycleViewDataBiaya( listItemBiayas, getContext(), 0);
                    }else{
                        adapterDataBiaya = new AdapterRecycleViewDataBiaya( listItemBiayas, getContext(), 1);
                    }
                    recyclerViewDataBiaya.setAdapter( adapterDataBiaya );
                    listItemBiayas.clear();
                    adapterDataBiaya.notifyDataSetChanged();
                    koneksiDataBiaya.setVisibility( View.VISIBLE );
                }
            }
        );

        RequestQueue requestQueue = Volley.newRequestQueue( getContext() );
        requestQueue.add( stringRequest );
    }

    private void setUpRecycleView() {
        recyclerViewDataBiaya.setHasFixedSize(true);
        recyclerViewDataBiaya.setLayoutManager(new LinearLayoutManager(getContext()));
        if (jenisBiaya==0){
            adapterDataBiaya = new AdapterRecycleViewDataBiaya( listItemBiayas, getContext(), 0);
        }else{
            adapterDataBiaya = new AdapterRecycleViewDataBiaya( listItemBiayas, getContext(), 1);
        }
        recyclerViewDataBiaya.setAdapter( adapterDataBiaya );
        adapterDataBiaya.notifyDataSetChanged();
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
                adapterDataBiaya.getFilter().filter(s);
                return false;
            }
        } );
        searchView.setQueryHint("Cari: Nama Biaya");

    }
}
