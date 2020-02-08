package com.pratamatechnocraft.tokason.Fragment;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.Fragment;
import androidx.core.content.FileProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pratamatechnocraft.tokason.Adapter.AdapterRecycleViewBiayaPengeluaran;
import com.pratamatechnocraft.tokason.Model.BaseUrlApiModel;
import com.pratamatechnocraft.tokason.Model.ListItemBiaya;
import com.pratamatechnocraft.tokason.R;
import com.pratamatechnocraft.tokason.Service.SessionManager;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class LaporanLabaRugiFragment extends Fragment {
    private SimpleDateFormat dateFormatter;
    private RecyclerView recycleViewBiayaPengeluaran,recycleViewBiayaPengeluaranTetap;
    private AdapterRecycleViewBiayaPengeluaran adapterRecycleViewBiayaPengeluaran, adapterRecycleViewBiayaPengeluaranTetap;
    private SwipeRefreshLayout refreshLabaRugi;
    private TextView txtBulanLabaRugi, txtIncome, txtExpense, txtNetIncome, txtPendapatan, txtLabaKotor, txtJumlahPengeluaranLainnya, txtTotalBiaya, txtTotalBiayaTidakTetap,txtTotalBiayaTetap, txtLabaBersih;

    private List<ListItemBiaya> listItemBiayas, listItemBiayasTetap;

    BaseUrlApiModel baseUrlApiModel = new BaseUrlApiModel();
    private String baseUrl=baseUrlApiModel.getBaseURL();
    Calendar newCalendar = Calendar.getInstance();
    int selectedMonthV;
    int selectedYearV;
    private WebView myWebView;
    private LinearLayout linearLayoutBackgroundLabaRugi;
    String url;
    private static final int PERMISSIONS_STORAGE_CODE = 1000;
    SessionManager sessionManager;
    HashMap<String, String> user;
    NavigationView navigationView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_laporan_laba_rugi, container, false);
        navigationView = getActivity().findViewById( R.id.nav_view );

        refreshLabaRugi = (SwipeRefreshLayout) view.findViewById(R.id.refreshLabaRugi);
        recycleViewBiayaPengeluaran = (RecyclerView) view.findViewById(R.id.recycleViewBiayaPengeluaran);
        recycleViewBiayaPengeluaranTetap = (RecyclerView) view.findViewById(R.id.recycleViewBiayaPengeluaranTetap);
        selectedMonthV=newCalendar.get(Calendar.MONTH);
        selectedYearV=newCalendar.get(Calendar.YEAR);

        sessionManager = new SessionManager( getContext() );
        user = sessionManager.getUserDetail();

        myWebView = view.findViewById(R.id.webviewLaporanLaba);
        linearLayoutBackgroundLabaRugi = view.findViewById(R.id.linearLayoutBackgroundLabaRugi);

        /*TEXT VIEW*/
        txtIncome = (TextView) view.findViewById(R.id.txtIncome);
        txtExpense = (TextView) view.findViewById(R.id.txtExpense);
        txtNetIncome = (TextView) view.findViewById(R.id.txtNetIncome);
        txtPendapatan = (TextView) view.findViewById(R.id.txtPendapatan);
        txtLabaKotor = (TextView) view.findViewById(R.id.txtLabaKotor);
        txtJumlahPengeluaranLainnya = (TextView) view.findViewById(R.id.txtJumlahPengeluaranLainnya);
        txtTotalBiaya = (TextView) view.findViewById(R.id.txtTotalBiaya);
        txtLabaBersih = (TextView) view.findViewById(R.id.txtLabaBersih);
        txtBulanLabaRugi = (TextView) view.findViewById(R.id.txtBulanLabaRugi);
        txtTotalBiayaTidakTetap = (TextView) view.findViewById(R.id.txtTotalBiayaTidakTetap);
        txtTotalBiayaTetap = (TextView) view.findViewById(R.id.txtTotalBiayaTetap);


        /*DATE PICKER*/
        dateFormatter = new SimpleDateFormat("MMMM yyyy", Locale.US);

        refreshLabaRugi.setOnRefreshListener( new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadLabaRugi(selectedMonthV, selectedYearV);
            }
        } );

        txtBulanLabaRugi.setText(dateFormatter.format(newCalendar.getTime()));

        return view;
    }

        @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
            selectedMonthV=newCalendar.get(Calendar.MONTH);
            selectedYearV=newCalendar.get(Calendar.YEAR);
        getActivity().setTitle("Laporan Laba Rugi");
        setHasOptionsMenu(true);
        loadLabaRugi(selectedMonthV,selectedYearV);
        navigationView.setCheckedItem(R.id.nav_laporan_labarugi);
    }

    private void showDateDialog() {
        MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(getContext(), new MonthPickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(int selectedMonth, int selectedYear) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(selectedYear, selectedMonth, Calendar.DAY_OF_MONTH);
                txtBulanLabaRugi.setText(dateFormatter.format(newDate.getTime()));
                selectedMonthV=selectedMonth;
                selectedYearV=selectedYear;
                loadLabaRugi(selectedMonth, selectedYear);
            }
        }, selectedYearV, selectedMonthV);

        builder.setMinYear(1990)
                .setMaxYear(2030)
                .setTitle("Pilih Bulan : ")
                .build()
                .show();
    }

    private void loadLabaRugi(int bulan, int tahun){
        url=baseUrl+"print_laba_rugi?&export=0&bulan="+(bulan+1)+"&tahun="+tahun+"&kd_outlet="+user.get(SessionManager.KD_OUTLET);
        myWebView.loadUrl(baseUrl+"print_laba_rugi?&export=1&bulan="+(bulan+1)+"&tahun="+tahun+"&kd_outlet="+user.get(SessionManager.KD_OUTLET));
        Log.d("BULAN", "loadLabaRugi: "+url);
        refreshLabaRugi.setRefreshing( true );
        listItemBiayas = new ArrayList<>();
        listItemBiayasTetap = new ArrayList<>();
        StringRequest stringRequest = new StringRequest( Request.Method.GET, baseUrl+"api/transaksi?api=laporan&bulan="+(bulan+1)+"&tahun="+tahun+"&lap=laplabarugi&kd_outlet="+user.get(SessionManager.KD_OUTLET),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            JSONObject dataObject = jsonObject.getJSONObject("data");
                            DecimalFormat formatter = new DecimalFormat("#,###,###");

                            if(dataObject.getInt("net_income")<0){
                                linearLayoutBackgroundLabaRugi.setBackgroundResource(R.color.red_500);
                            }else{
                                linearLayoutBackgroundLabaRugi.setBackgroundResource(R.color.light_blue_A100);
                            }

                            txtIncome.setText("Rp. "+formatter.format(Double.parseDouble(dataObject.getString("income"))));
                            txtExpense.setText(String.valueOf("Rp. "+formatter.format(Double.parseDouble(dataObject.getString("totalbiaya")))));
                            txtJumlahPengeluaranLainnya.setText("Rp. "+formatter.format(Double.parseDouble(dataObject.getString("expense"))));
                            txtPendapatan.setText("Rp. "+formatter.format(Double.parseDouble(dataObject.getString("income"))));
                            txtLabaKotor.setText("Rp. "+formatter.format(Double.parseDouble(dataObject.getString("income"))));
                            txtNetIncome.setText("Rp. "+formatter.format(Double.parseDouble(dataObject.getString("net_income"))));
                            txtLabaBersih.setText("Rp. "+formatter.format(Double.parseDouble(dataObject.getString("net_income"))));
                            txtTotalBiaya.setText("Rp. "+formatter.format(Double.parseDouble(dataObject.getString("totalbiaya"))));
                            txtTotalBiayaTetap.setText("Rp. "+formatter.format(Double.parseDouble(dataObject.getString("totalbiayatetap"))));
                            txtTotalBiayaTidakTetap.setText("Rp. "+formatter.format(Double.parseDouble(dataObject.getString("totalbiayatidaktetap"))));

                            JSONArray bebanBiaya = dataObject.getJSONArray("data_biaya");
                            for (int i = 0; i<bebanBiaya.length(); i++){
                                JSONObject biayaPengeluaran = bebanBiaya.getJSONObject( i );

                                ListItemBiaya listItemBiaya = new ListItemBiaya(
                                        biayaPengeluaran.getString( "kd_biaya"),
                                        biayaPengeluaran.getString( "nama_biaya" ),
                                        "Rp. "+formatter.format(Double.parseDouble(biayaPengeluaran.getString( "jumlah_biaya" ))),
                                        biayaPengeluaran.getString( "tgl_biaya" )
                                );

                                listItemBiayas.add( listItemBiaya );
                            }

                            JSONArray bebanBiayatetap = dataObject.getJSONArray("data_biayatetap");
                            for (int i = 0; i<bebanBiayatetap.length(); i++){
                                JSONObject biayaPengeluarantetap = bebanBiayatetap.getJSONObject( i );

                                ListItemBiaya listItemBiayaTetap = new ListItemBiaya(
                                        biayaPengeluarantetap.getString( "kd_biaya"),
                                        biayaPengeluarantetap.getString( "nama_biaya" ),
                                        "Rp. "+formatter.format(Double.parseDouble(biayaPengeluarantetap.getString( "jumlah_biaya" ))),
                                        biayaPengeluarantetap.getString( "tgl_biaya" )
                                );

                                listItemBiayasTetap.add( listItemBiayaTetap );
                            }

                            refreshLabaRugi.setRefreshing( false );
                            setUpRecycleView();
                        }catch (JSONException e){
                            e.printStackTrace();
                            refreshLabaRugi.setRefreshing( false );
                            setUpRecycleView();
                            listItemBiayas.clear();
                            listItemBiayasTetap.clear();
                            adapterRecycleViewBiayaPengeluaran.notifyDataSetChanged();
                            adapterRecycleViewBiayaPengeluaranTetap.notifyDataSetChanged();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        refreshLabaRugi.setRefreshing( false );
                        setUpRecycleView();
                        listItemBiayas.clear();
                        listItemBiayasTetap.clear();
                        adapterRecycleViewBiayaPengeluaran.notifyDataSetChanged();
                        adapterRecycleViewBiayaPengeluaranTetap.notifyDataSetChanged();
                    }
                }
        );

        RequestQueue requestQueue = Volley.newRequestQueue( getContext() );
        requestQueue.add( stringRequest );

    }

    private void setUpRecycleView(){
        recycleViewBiayaPengeluaran.setHasFixedSize(true);
        recycleViewBiayaPengeluaran.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterRecycleViewBiayaPengeluaran = new AdapterRecycleViewBiayaPengeluaran( listItemBiayas, getContext());
        recycleViewBiayaPengeluaran.setAdapter( adapterRecycleViewBiayaPengeluaran );
        adapterRecycleViewBiayaPengeluaran.notifyDataSetChanged();

        recycleViewBiayaPengeluaranTetap.setHasFixedSize(true);
        recycleViewBiayaPengeluaranTetap.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterRecycleViewBiayaPengeluaranTetap = new AdapterRecycleViewBiayaPengeluaran( listItemBiayasTetap, getContext());
        recycleViewBiayaPengeluaranTetap.setAdapter( adapterRecycleViewBiayaPengeluaranTetap );
        adapterRecycleViewBiayaPengeluaranTetap.notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_laporan, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ic_print:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    createWebPrintJob(myWebView);
                }
                return true;
            case R.id.ic_bagikan_laporan:
                bagikan();
                return true;
            case R.id.ic_export_laporan:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (getContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_DENIED){
                        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        getActivity().requestPermissions(permissions, PERMISSIONS_STORAGE_CODE);
                    }else{
                        export(url);
                    }
                }else{
                    export(url);
                }
                return true;
            case R.id.ic_datepicker:
                showDateDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void export(String url) {

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);

        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "Laporan_Laba_Rugi_Periode_"+txtBulanLabaRugi.getText()+System.currentTimeMillis()+".xlsx");

        DownloadManager manager = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void createWebPrintJob(WebView webView) {

        PrintAttributes printAttributes = new PrintAttributes.Builder()
                .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                .setMinMargins(PrintAttributes.Margins.NO_MARGINS).build();

        PrintManager printManager = (PrintManager) getActivity()
                .getSystemService(Context.PRINT_SERVICE);

        PrintDocumentAdapter printAdapter =
                webView.createPrintDocumentAdapter("Laporan_Laba_Rugi_Periode_"+txtBulanLabaRugi.getText());

        String jobName = getString(R.string.app_name) + "Print Laba Rugi";

        printManager.print(jobName, printAdapter, printAttributes);
    }

    private void bagikan(){
        Picture picture = myWebView.capturePicture();
        Bitmap b = Bitmap.createBitmap(
                picture.getWidth()-800, picture.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        picture.draw(c);
        Uri bmpUri = getBitmapFromDrawable(b);
        final Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/jpg");
        /*final File photoFile = new File(getFilesDir(), "foo.jpg");*/
        shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);

        startActivity(Intent.createChooser(shareIntent, "Share image using"));
    }

    // Method when launching drawable within Glide
    public Uri getBitmapFromDrawable(Bitmap bmp){

        // Store image to default external storage directory
        Uri bmpUri = null;
        File file;
        try {
            file =  new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Laporan_Laba_Rugi_Periode_"+txtBulanLabaRugi.getText()+"_"+System.currentTimeMillis()+".jpg");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.close();

            bmpUri = FileProvider.getUriForFile(getContext(), "com.pratamatechnocraft.tokason.fileprovider", file);

        } catch (IOException e) {
            Log.d("TAG", "getBitmapFromDrawable: "+e);
            e.printStackTrace();
        }
        return bmpUri;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSIONS_STORAGE_CODE:{
                if (grantResults.length > 0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    export(url);
                }
            }
        }
    }
}




