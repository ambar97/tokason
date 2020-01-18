package com.pratamatechnocraft.tokason.Fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.navigation.NavigationView;

import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.cardview.widget.CardView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pratamatechnocraft.tokason.Service.SessionManager;
import com.pratamatechnocraft.tokason.R;

import java.util.HashMap;
import java.util.zip.Inflater;

public class DashboardFragment extends Fragment {

    private CardView kliktransaksijual, kliktransaksibeli, klikbarang, klikkategori, klikuser, kliklapharian, kliklapbulanan, kliklaptahunan, kliklaplabarugi, klikbiaya;
    NavigationView navigationView;
    SessionManager sessionManager;
    View view;
    TextView smsCountTxt;
    int pendingSMSCount = 0;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        sessionManager = new SessionManager( getContext() );
        HashMap<String, String> user = sessionManager.getUserDetail();
        navigationView = getActivity().findViewById( R.id.nav_view );
        if (Integer.parseInt( user.get( sessionManager.LEVEL_USER ) )==0){
            view = inflater.inflate( R.layout.fragment_dashboard, container, false);
            kliktransaksijual = view.findViewById(R.id.cardhometransaksipenjualan);
            kliktransaksibeli = view.findViewById(R.id.cardhometransaksipembelian);
            klikbarang = view.findViewById(R.id.cardhomebarang);
            klikkategori = view.findViewById(R.id.cardhomekategori);
            klikuser = view.findViewById(R.id.cardhomeuser);
            kliklapharian = view.findViewById(R.id.cardhomelapharian);
            kliklapbulanan = view.findViewById(R.id.cardhomelapbulanan);
            kliklaptahunan = view.findViewById(R.id.cardhomelaptahunan);
            kliklaplabarugi = view.findViewById(R.id.cardhomelaplabarugi);
            klikbiaya = view.findViewById(R.id.cardhomebiaya);


            kliktransaksijual.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TabLayoutFragment tabLayoutFragment = new TabLayoutFragment(0);

                    getFragmentManager().beginTransaction()
                            .replace(R.id.screen_area, tabLayoutFragment )
                            .addToBackStack(null)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit();
                }
            });

            kliktransaksibeli.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TabLayoutFragment tabLayoutFragment = new TabLayoutFragment(1);

                    getFragmentManager().beginTransaction()
                            .replace(R.id.screen_area, tabLayoutFragment )
                            .addToBackStack(null)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit();
                }
            });

            klikbarang.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DataBarangFragment dataBarangFragment = new DataBarangFragment();

                    getFragmentManager().beginTransaction()
                            .replace(R.id.screen_area, dataBarangFragment )
                            .addToBackStack(null)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit();
                }
            });

            klikuser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DataUserFragment dataUserFragment = new DataUserFragment();

                    getFragmentManager().beginTransaction()
                            .replace(R.id.screen_area, dataUserFragment )
                            .addToBackStack(null)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit();
                }
            });

            klikkategori.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DataKategoriBarangFragment dataKategoriBarangFragment = new DataKategoriBarangFragment();

                    getFragmentManager().beginTransaction()
                            .replace(R.id.screen_area, dataKategoriBarangFragment )
                            .addToBackStack(null)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit();
                }
            });

            klikbiaya.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TabLayoutFragmentBiaya tabLayoutFragmentBiaya= new TabLayoutFragmentBiaya();

                    getFragmentManager().beginTransaction()
                            .replace(R.id.screen_area, tabLayoutFragmentBiaya )
                            .addToBackStack(null)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit();
                }
            } );

            kliklaplabarugi.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LaporanLabaRugiFragment laporanLabaRugiFragment = new LaporanLabaRugiFragment();

                    getFragmentManager().beginTransaction()
                            .replace(R.id.screen_area, laporanLabaRugiFragment )
                            .addToBackStack(null)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit();
                }
            } );

            kliklapharian.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LaporanFragment laporanFragment = new LaporanFragment(0);

                    getFragmentManager().beginTransaction()
                            .replace(R.id.screen_area, laporanFragment)
                            .addToBackStack(null)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit();
                }
            } );

            kliklapbulanan.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LaporanFragment laporanFragment = new LaporanFragment(1);

                    getFragmentManager().beginTransaction()
                            .replace(R.id.screen_area, laporanFragment)
                            .addToBackStack(null)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit();
                }
            } );

            kliklaptahunan.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LaporanFragment laporanFragment = new LaporanFragment(2);

                    getFragmentManager().beginTransaction()
                            .replace(R.id.screen_area, laporanFragment)
                            .addToBackStack(null)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit();
                }
            } );
        }else{
            view = inflater.inflate( R.layout.fragment_dashboard_kasir, container, false);
            kliktransaksijual = view.findViewById(R.id.cardhometransaksipenjualan);
            kliklapharian = view.findViewById(R.id.cardhomelapharian);
            kliklapbulanan = view.findViewById(R.id.cardhomelapbulanan);
            kliklaptahunan = view.findViewById(R.id.cardhomelaptahunan);
            navigationView = getActivity().findViewById( R.id.nav_view );

            kliktransaksijual.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TabLayoutFragment tabLayoutFragment = new TabLayoutFragment(0);

                    getFragmentManager().beginTransaction()
                            .replace(R.id.screen_area, tabLayoutFragment )
                            .addToBackStack(null)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit();
                }
            });

            kliklapharian.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LaporanFragment laporanFragment = new LaporanFragment(0);

                    getFragmentManager().beginTransaction()
                            .replace(R.id.screen_area, laporanFragment)
                            .addToBackStack(null)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit();
                }
            } );

            kliklapbulanan.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LaporanFragment laporanFragment = new LaporanFragment(1);

                    getFragmentManager().beginTransaction()
                            .replace(R.id.screen_area, laporanFragment)
                            .addToBackStack(null)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit();
                }
            } );

            kliklaptahunan.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LaporanFragment laporanFragment = new LaporanFragment(2);

                    getFragmentManager().beginTransaction()
                            .replace(R.id.screen_area, laporanFragment)
                            .addToBackStack(null)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit();
                }
            } );
        }

        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle(R.string.app_name);
        navigationView.setCheckedItem(R.id.nav_dashboard);
        setHasOptionsMenu( true );
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_dashboard, menu);

        final MenuItem menuItem = menu.findItem(R.id.action_notifications);

        View actionView = menuItem.getActionView();
        smsCountTxt = (TextView) actionView.findViewById(R.id.notification_badge);

        setupBadge();

        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_notifications: {

                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupBadge() {

        if (smsCountTxt != null) {
            if (pendingSMSCount == 0) {
                if (smsCountTxt.getVisibility() != View.GONE) {
                    smsCountTxt.setVisibility(View.GONE);
                }
            } else {
                smsCountTxt.setText(String.valueOf(Math.min(pendingSMSCount, 99)));
                if (smsCountTxt.getVisibility() != View.VISIBLE) {
                    smsCountTxt.setVisibility(View.VISIBLE);
                }
            }
        }
    }
}
