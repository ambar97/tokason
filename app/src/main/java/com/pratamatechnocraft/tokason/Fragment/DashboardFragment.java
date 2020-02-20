package com.pratamatechnocraft.tokason.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import com.pratamatechnocraft.tokason.Model.BaseUrlApiModel;
import com.pratamatechnocraft.tokason.R;
import com.pratamatechnocraft.tokason.Service.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DashboardFragment extends Fragment {

    Context context;
    private CardView kliktransaksijual, kliktransaksibeli, klikbarang, klikkategori, klikuser, kliklapharian, kliklapbulanan, kliklaptahunan, kliklaplabarugi, klikbiaya,klikprofile;
    NavigationView navigationView;
    SessionManager sessionManager;
    View view;
    TextView smsCountTxt, txtJatuhTempo, txtNamaOutlet;
    int pendingSMSCount = 0;
    BaseUrlApiModel baseUrlApiModel = new BaseUrlApiModel();
    private String baseUrl=baseUrlApiModel.getBaseURL();
    private static final String API_URL_LOAD = "api/user?api=dataprofile&kd_user=";
    private static final String API_URL = "api/user";
    SwipeRefreshLayout swipeRefreshLayout;
    ViewFlipper viewFlipper;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getActivity();
        sessionManager = new SessionManager( getContext() );

        HashMap<String, String> user = sessionManager.getUserDetail();
        navigationView = getActivity().findViewById( R.id.nav_view );
        if (Integer.parseInt( user.get( sessionManager.LEVEL_USER ) )==0){
            view = inflater.inflate( R.layout.fragment_dashboard, container, false);
            viewFlipper = view.findViewById(R.id.vlipper);
            int image[] = {R.drawable.slide1,R.drawable.slide1,R.drawable.slide1,R.drawable.slide1};
            for (int images:image){
                flipper(images);
            }
            kliktransaksijual = view.findViewById(R.id.cardhometransaksipenjualan);
            kliktransaksibeli = view.findViewById(R.id.cardhometransaksipembelian);
            klikbarang = view.findViewById(R.id.cardhomebarang);
            klikkategori = view.findViewById(R.id.cardhomekategori);
            klikuser = view.findViewById(R.id.cardhomeuser);
            kliklapharian = view.findViewById(R.id.cardhomelapharian);
            kliklapbulanan = view.findViewById(R.id.cardhomelapbulanan);
            kliklaptahunan = view.findViewById(R.id.cardhomelaptahunan);
            kliklaplabarugi = view.findViewById(R.id.cardhomelaplabarugi);
            klikprofile = view.findViewById(R.id.cardhomeprofile);
            txtJatuhTempo = view.findViewById(R.id.tv_jatuh_tempo);
            txtNamaOutlet = view.findViewById(R.id.tv_nama_outlet);
            swipeRefreshLayout = view.findViewById(R.id.refreshProfile);
//            klikbiaya = view.findViewById(R.id.cardhomebiaya);


            final String kdUser = user.get(sessionManager.KD_USER);
            loadProfile(kdUser);

            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    loadProfile(kdUser);
                }
            });

            klikprofile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ProfileFragment profileFragment= new ProfileFragment();

                    getFragmentManager().beginTransaction()
                            .replace(R.id.screen_area, profileFragment )
                            .addToBackStack(null)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit();
                }
            });
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

//            klikbiaya.setOnClickListener( new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    TabLayoutFragmentBiaya tabLayoutFragmentBiaya= new TabLayoutFragmentBiaya();
//
//                    getFragmentManager().beginTransaction()
//                            .replace(R.id.screen_area, tabLayoutFragmentBiaya )
//                            .addToBackStack(null)
//                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//                            .commit();
//                }
//            } );

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

//            kliklaptahunan.setOnClickListener( new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    LaporanFragment laporanFragment = new LaporanFragment(2);
//
//                    getFragmentManager().beginTransaction()
//                            .replace(R.id.screen_area, laporanFragment)
//                            .addToBackStack(null)
//                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//                            .commit();
//                }
//            } );
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

    public void flipper(int image){
        ImageView imageView = new ImageView(getContext());
        imageView.setBackgroundResource(image);
        viewFlipper.addView(imageView);
        viewFlipper.setFlipInterval(6000);
        viewFlipper.setAutoStart(true);
        viewFlipper.setInAnimation(getContext(), android.R.anim.slide_in_left);
        viewFlipper.setOutAnimation(getContext(),android.R.anim.slide_out_right);
//        viewFlipper.setOutAnimation(getContext(),android.R.anim.);
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

    private void loadProfile(final String kd_user){
        StringRequest stringRequest = new StringRequest( Request.Method.GET, baseUrl+API_URL_LOAD+kd_user,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d("TAG", "onResponse: "+response);
                            final JSONObject userprofile = new JSONObject(response);
                            txtJatuhTempo.setText(userprofile.getString("tgl_jatuh_tempo"));
                            txtNamaOutlet.setText("Toko "+userprofile.getString("nama_outlet"));
                            if (userprofile.getString("status").equals("1")) {
                                if (userprofile.getString("kode_referal").equals("")) {
                                    checkReferral(kd_user);
                                    Log.d("A", "onResponse: DIALOG");
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Periksa koneksi & coba lagi1", Toast.LENGTH_SHORT).show();
                            Log.e("TAG", "onResponse: ", e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Periksa koneksi & coba lagi", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        RequestQueue requestQueue = Volley.newRequestQueue( context );
        requestQueue.add( stringRequest );

        swipeRefreshLayout.setRefreshing(false);
    }

    public void checkReferral(final String kd_user){
        final ArrayList arrayList = new ArrayList();
        final String string="";
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Dari mana Anda tahu aplikasi ini?");
        builder
                .setSingleChoiceItems(R.array.survey_referral, 0, null)
                .setPositiveButton("Kirim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ListView lw = ((AlertDialog)dialogInterface).getListView();
                        Object checkedItem = lw.getAdapter().getItem(lw.getCheckedItemPosition());
                        String kd;
                        if (checkedItem.equals("Media Sosial")){
                            kd = "876797";
                        }else if(checkedItem.equals("Surat Kabar")) {
                            kd = "suratkabar";
                        } else {
                            kd = "oranglain";
                        }
                        kirimReferal(kd, kd_user);
                    }
                })
                .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        builder.create().show();
    }

    void kirimReferal(final String kd, final String kd_user) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, baseUrl+API_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d("KIRIMREFERAL", "onResponse: " + response);
                    JSONObject jsonObject = new JSONObject(response);
                    String kode = jsonObject.getString("kode");
                    String pesan = jsonObject.getString("pesan");
                    if (kode.equals("1")) {
                        Toast.makeText(context, pesan, Toast.LENGTH_SHORT).show();
                    } else if (kode.equals("2")) {
                        Toast.makeText(context, pesan, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Periksa koneksi & coba lagi1", Toast.LENGTH_SHORT).show();
                    Log.e("TAG", "onResponse: ", e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Periksa koneksi & coba lagi1", Toast.LENGTH_SHORT).show();
                Log.e("TAG", "onResponse: ", error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user", kd_user);
                params.put("kode_referal", kd);
                params.put("api", "updatereferal");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

}
