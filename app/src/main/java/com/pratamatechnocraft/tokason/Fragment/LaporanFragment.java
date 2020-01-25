package com.pratamatechnocraft.tokason.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pratamatechnocraft.tokason.Adapter.AdapterRecycleViewBarangTerjual;
import com.pratamatechnocraft.tokason.Model.BaseUrlApiModel;
import com.pratamatechnocraft.tokason.Model.ListItemBarangTerjual;
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

@SuppressLint("ValidFragment")
public class LaporanFragment extends Fragment{
    private   TextView txtTanggalHarian;
    private TextView txtBulan, txtTahun, txtJmlTransaksi, txtPendapatanLaporanPenjualan, txtHargaTotalBarangTerjual,txtDataKosongLapPenjualan;
    private LinearLayout LinearLayoutLapHarian,LinearLayoutLapBulanan,LinearLayoutLapTahunan;
    private SimpleDateFormat dateFormatter;
    private Integer jenisLaporan;
    Calendar newCalendar = Calendar.getInstance();
    int selectedDayV;
    int selectedMonthV;
    int selectedYearV;
    DateRangePickerFragment dateRangePickerFragment;
    private WebView myWebView;
    private SwipeRefreshLayout refreshLaporan;
    private RecyclerView recycleViewBarangTerjual;
    private AdapterRecycleViewBarangTerjual adapterRecycleViewBarangTerjual;
    private List<ListItemBarangTerjual> listItemBarangTerjuals;
    BaseUrlApiModel baseUrlApiModel = new BaseUrlApiModel();
    private String baseUrl=baseUrlApiModel.getBaseURL();
    private String tanggalDari, tanggalSampai;
    String url;
    SessionManager sessionManager;
    private static final int PERMISSIONS_STORAGE_CODE = 1000;
    HashMap<String, String> user;
    NavigationView navigationView;

    public LaporanFragment(Integer jenisLaporan) {this.jenisLaporan = jenisLaporan;}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_laporan_penjualan, container, false);
        navigationView = getActivity().findViewById( R.id.nav_view );
        selectedDayV=newCalendar.get(Calendar.DAY_OF_MONTH);
        selectedMonthV=newCalendar.get(Calendar.MONTH);
        selectedYearV=newCalendar.get(Calendar.YEAR);

        sessionManager = new SessionManager( getContext() );
        user = sessionManager.getUserDetail();

        myWebView = view.findViewById(R.id.webviewLaporan);

        /*LINEAR LAYOUT*/
        LinearLayoutLapBulanan = view.findViewById(R.id.LinearLayoutLapBulanan);
        LinearLayoutLapHarian = view.findViewById(R.id.LinearLayoutLapHarian);
        LinearLayoutLapTahunan = view.findViewById(R.id.LinearLayoutLapTahunan);

        /*TEXT VIEW*/
        txtTanggalHarian=view.findViewById(R.id.txtTanggalHarian);
        txtBulan=view.findViewById(R.id.txtBulan);
        txtTahun=view.findViewById(R.id.txtTahun);
        txtJmlTransaksi=view.findViewById(R.id.txtJmlTransaksi);
        txtPendapatanLaporanPenjualan=view.findViewById(R.id.txtPendapatanLaporanPenjualan);
        txtHargaTotalBarangTerjual=view.findViewById(R.id.txtHargaTotalBarangTerjual);
        txtDataKosongLapPenjualan=view.findViewById(R.id.txtDataKosongLapPenjualan);

        recycleViewBarangTerjual = (RecyclerView) view.findViewById(R.id.recycleViewBarangTerjual);
        recycleViewBarangTerjual.setHasFixedSize(true);
        recycleViewBarangTerjual.setLayoutManager(new LinearLayoutManager(getContext()));

        refreshLaporan = view.findViewById( R.id.refreshLaporan );

        listItemBarangTerjuals = new ArrayList<>();
        adapterRecycleViewBarangTerjual = new AdapterRecycleViewBarangTerjual( listItemBarangTerjuals, getContext());



        if(jenisLaporan==0){
            LinearLayoutLapBulanan.setVisibility(View.GONE);
            LinearLayoutLapTahunan.setVisibility(View.GONE);
            LinearLayoutLapHarian.setVisibility(View.VISIBLE);
            dateFormatter = new SimpleDateFormat("dd MMMM yyyy ", Locale.US);
            tanggalDari=dateFormatter.format(newCalendar.getTime());
            tanggalSampai=dateFormatter.format(newCalendar.getTime());
            txtTanggalHarian.setText(dateFormatter.format(newCalendar.getTime())+" - "+dateFormatter.format(newCalendar.getTime()));

            refreshLaporan.setOnRefreshListener( new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    listItemBarangTerjuals.clear();
                    adapterRecycleViewBarangTerjual.notifyDataSetChanged();
                    loadLaporan(tanggalDari,tanggalSampai,null,null,0);
                }
            } );
        }else if(jenisLaporan==1){
            LinearLayoutLapBulanan.setVisibility(View.VISIBLE);
            LinearLayoutLapTahunan.setVisibility(View.GONE);
            LinearLayoutLapHarian.setVisibility(View.GONE);
            dateFormatter = new SimpleDateFormat("MMMM yyyy ", Locale.US);
            txtBulan.setText(dateFormatter.format(newCalendar.getTime()));

            refreshLaporan.setOnRefreshListener( new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    listItemBarangTerjuals.clear();
                    adapterRecycleViewBarangTerjual.notifyDataSetChanged();
                    loadLaporan(null,null,String.valueOf((selectedMonthV+1)),String.valueOf(selectedYearV),1);
                }
            } );
//        }else{
//            LinearLayoutLapBulanan.setVisibility(View.GONE);
//            LinearLayoutLapTahunan.setVisibility(View.VISIBLE);
//            LinearLayoutLapHarian.setVisibility(View.GONE);
//            dateFormatter = new SimpleDateFormat("yyyy ", Locale.US);
//            txtTahun.setText(dateFormatter.format(newCalendar.getTime()));
//
//            refreshLaporan.setOnRefreshListener( new SwipeRefreshLayout.OnRefreshListener() {
//                @Override
//                public void onRefresh() {
//                    listItemBarangTerjuals.clear();
//                    adapterRecycleViewBarangTerjual.notifyDataSetChanged();
//                    loadLaporan(null,null,null,String.valueOf(selectedYearV),2);
//                }
//            } );
        }

        recycleViewBarangTerjual.setAdapter( adapterRecycleViewBarangTerjual );

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        setHasOptionsMenu(true);
        if(jenisLaporan==0){
            navigationView.setCheckedItem(R.id.nav_laporan_harian);
            getActivity().setTitle("Laporan Harian");
            loadLaporan(tanggalDari,tanggalSampai,null,null,0);
        }else if(jenisLaporan==1){
            navigationView.setCheckedItem(R.id.nav_laporan_bulanan);
            getActivity().setTitle("Laporan Bulanan");
            loadLaporan(null,null,String.valueOf((selectedMonthV+1)),String.valueOf(selectedYearV),1);
//        }else{
//            navigationView.setCheckedItem(R.id.nav_laporan_tahunan);
//            getActivity().setTitle("Laporan Tahunan");
//            loadLaporan(null,null,null,String.valueOf(selectedYearV),2);
        }
    }

    private void showDateDialog(){
        if(jenisLaporan==0){
            dateRangePickerFragment= DateRangePickerFragment.newInstance((DateRangePickerFragment.OnDateRangeSelectedListener) getContext(),false);
            dateRangePickerFragment.setOnDateRangeSelectedListener(new DateRangePickerFragment.OnDateRangeSelectedListener() {
                @Override
                public void onDateRangeSelected(int startDay, int startMonth, int startYear, int endDay, int endMonth, int endYear) {
                    Calendar start = Calendar.getInstance();
                    start.set(startYear, startMonth, startDay);
                    Calendar ends = Calendar.getInstance();
                    ends.set(endYear, endMonth, endDay);
                    tanggalDari=dateFormatter.format(start.getTime());
                    tanggalSampai=dateFormatter.format(ends.getTime());
                    txtTanggalHarian.setText(dateFormatter.format(start.getTime())+" - "+dateFormatter.format(ends.getTime()));
                    loadLaporan(tanggalDari,tanggalSampai,null,null,0);
                }
            });
            dateRangePickerFragment.show(getActivity().getSupportFragmentManager(),"datePicker");
        }else if(jenisLaporan==1){
            MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(getContext(), new MonthPickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(int selectedMonth, int selectedYear) {
                    Calendar newDate = Calendar.getInstance();
                    newDate.set(selectedYear, selectedMonth, Calendar.DAY_OF_MONTH);
                    txtBulan.setText(dateFormatter.format(newDate.getTime()));
                    selectedMonthV=selectedMonth;
                    selectedYearV=selectedYear;
                    loadLaporan(null,null,String.valueOf((selectedMonth+1)),String.valueOf(selectedYear),1);
                }
            }, selectedYearV, selectedMonthV);

            builder.setMinYear(1990)
                    .setMaxYear(2030)
                    .setTitle("Pilih Bulan : ")
                    .build()
                    .show();
//        }else {
//            MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(getContext(), new MonthPickerDialog.OnDateSetListener() {
//                @Override
//                public void onDateSet(int selectedMonth, int selectedYear) {
//                    Calendar newDate = Calendar.getInstance();
//                    newDate.set(selectedYear, selectedMonth, Calendar.DAY_OF_YEAR);
//                    txtTahun.setText(dateFormatter.format(newDate.getTime()));
//                    selectedMonthV=selectedMonth;
//                    selectedYearV=selectedYear;
//                    loadLaporan(null,null,null,String.valueOf(selectedYear),2);
//                }
//            }, selectedYearV, selectedMonthV);
//
//            builder.showYearOnly()
//                    .setTitle("Pilih Tahun : ")
//                    .setYearRange(1990, 2030)
//                    .build()
//                    .show();
        }
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
            case R.id.ic_bagikan_laporan:
                bagikan();
                return true;
            case R.id.ic_datepicker:
                showDateDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void createWebPrintJob(WebView webView) {

        PrintAttributes printAttributes = new PrintAttributes.Builder()
                .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                .setMinMargins(PrintAttributes.Margins.NO_MARGINS).build();

        PrintManager printManager = (PrintManager) getActivity()
                .getSystemService(Context.PRINT_SERVICE);

        String namaDocumnent = null;

        if (jenisLaporan==0){
            namaDocumnent = "Laporan_Harian_Periode_"+txtTanggalHarian.getText();
        }else if(jenisLaporan==1){
            namaDocumnent ="Laporan_Bulanan_Periode_"+txtBulan.getText();
//        }else {
//            namaDocumnent ="Laporan_Tahunan_Periode_"+txtTahun.getText();
        }

        PrintDocumentAdapter printAdapter =
                webView.createPrintDocumentAdapter(namaDocumnent);

        String jobName = getString(R.string.app_name) + "Print Laba Rugi";

        printManager.print(jobName, printAdapter, printAttributes);
    }

    private void bagikan(){
        Picture picture = myWebView.capturePicture();
        Bitmap b = Bitmap.createBitmap(
                picture.getWidth()-900, picture.getHeight(), Bitmap.Config.ARGB_8888);
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
            if (jenisLaporan==0){
                file =  new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Laporan_Harian_Periode_"+txtTanggalHarian.getText()+"_"+System.currentTimeMillis()+".jpg");
            }else{
                file =  new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Laporan_Bulanan_Periode_"+txtBulan.getText()+"_"+System.currentTimeMillis()+".jpg");
//            }else {
//                file =  new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Laporan_Tahunan_Periode_"+txtTahun.getText()+"_"+System.currentTimeMillis()+".jpg");
            }
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.close();

            bmpUri = FileProvider.getUriForFile(getContext(), "com.pratamatechnocraft.sisirKayuManis.fileprovider", file);

        } catch (IOException e) {
            Log.d("TAG", "getBitmapFromDrawable: "+e);
            e.printStackTrace();
        }
        return bmpUri;
    }


    private void loadLaporan(String dari,String sampai,String bulan,String tahun, Integer jenisLaporan){
        listItemBarangTerjuals.clear();
        adapterRecycleViewBarangTerjual.notifyDataSetChanged();
        Log.d("TAG", "loadLaporan: "+bulan);
        refreshLaporan.setRefreshing(true);
        String API_URL = null;
        if (jenisLaporan==0){
            API_URL="api/transaksi?api=laporan&lap=harian&dari="+dari+"&sampai="+sampai;
            myWebView.loadUrl(baseUrl+"print_laporan?lap=harian&export=1&dari="+dari+"&sampai="+sampai+"&kd_outlet="+user.get(SessionManager.KD_OUTLET));
            url=baseUrl+"print_laporan?lap=harian&export=0&dari="+dari+"&sampai="+sampai+"&kd_outlet="+user.get(SessionManager.KD_OUTLET);
        }else{
            API_URL="api/transaksi?api=laporan&lap=bulanan&bulan="+bulan+"&tahun="+tahun;
            myWebView.loadUrl(baseUrl+"print_laporan?lap=bulanan&export=1&bulan="+bulan+"&tahun="+tahun+"&kd_outlet="+user.get(SessionManager.KD_OUTLET));
            url=baseUrl+"print_laporan?lap=bulanan&export=0&bulan="+bulan+"&tahun="+tahun+"&kd_outlet="+user.get(SessionManager.KD_OUTLET);
//        }else{
//            API_URL="api/transaksi?api=laporan&lap=tahunan&tahun="+tahun;
//            myWebView.loadUrl(baseUrl+"print_laporan?lap=tahunan&export=1&tahun="+tahun+"&kd_outlet="+user.get(SessionManager.KD_OUTLET));
//            url=baseUrl+"print_laporan?lap=tahunan&export=0&tahun="+tahun+"&kd_outlet="+user.get(SessionManager.KD_OUTLET);
        }
        StringRequest stringRequest = new StringRequest( Request.Method.GET, baseUrl+API_URL+"&kd_outlet="+user.get(SessionManager.KD_OUTLET),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            final JSONObject jsonObject = new JSONObject(response);
                            DecimalFormat formatter = new DecimalFormat("#,###,###");
                            JSONObject dataObject = jsonObject.getJSONObject("data");

                            if (dataObject.getInt("jml_transaksi")!=0){
                                txtDataKosongLapPenjualan.setVisibility(View.GONE);
                            }else{
                                txtDataKosongLapPenjualan.setVisibility(View.VISIBLE);
                            }

                            txtJmlTransaksi.setText(dataObject.getString("jml_transaksi"));
                            txtPendapatanLaporanPenjualan.setText("Rp. "+formatter.format(Double.parseDouble(dataObject.getString("pendapatan"))));
                            txtHargaTotalBarangTerjual.setText("Rp. "+formatter.format(Double.parseDouble(dataObject.getString("total_harga_semua"))));

                            JSONArray barangTerjual = dataObject.getJSONArray("barangTerjual");
                            for (int i = 0; i<barangTerjual.length(); i++){
                                JSONObject barangTerjualJSONObject = barangTerjual.getJSONObject( i );
                                ListItemBarangTerjual listItemBarangTerjual = new ListItemBarangTerjual(
                                        barangTerjualJSONObject.getString( "nama_barang" ),
                                        barangTerjualJSONObject.getString( "harga_jual_detail" ),
                                        barangTerjualJSONObject.getString( "qty"),
                                        String.valueOf(barangTerjualJSONObject.getInt("qty")*barangTerjualJSONObject.getInt("harga_jual_detail"))
                                );

                                listItemBarangTerjuals.add( listItemBarangTerjual );
                                adapterRecycleViewBarangTerjual.notifyDataSetChanged();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Periksa koneksi & coba lagi", Toast.LENGTH_SHORT).show();
                        }
                        refreshLaporan.setRefreshing( false );
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("ERRORLAPORAN", "onErrorResponse: ", error);
                        Toast.makeText(getContext(), "Periksa koneksi & coba lagi1", Toast.LENGTH_SHORT).show();
                        refreshLaporan.setRefreshing( false );
                    }
                }
        );

        RequestQueue requestQueue = Volley.newRequestQueue( getContext() );
        requestQueue.add( stringRequest );
    }

    private void export(String url) {

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);

        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        if (jenisLaporan==0){
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "Laporan_Harian_Periode_"+txtTanggalHarian.getText()+"_"+System.currentTimeMillis()+".xlsx");
        }else{
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "Laporan_Bulanan_Periode_"+txtBulan.getText()+"_"+System.currentTimeMillis()+".xlsx");
//        }else {
//            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "Laporan_Tahunan_Periode_"+txtTahun.getText()+"_"+System.currentTimeMillis()+".xlsx");
        }


        DownloadManager manager = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }
}
