package com.pratamatechnocraft.tokason;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import com.google.android.material.textfield.TextInputLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.pratamatechnocraft.tokason.Adapter.AdapterRecycleViewDetailTransaksi;
import com.pratamatechnocraft.tokason.Adapter.DBDataSourceKeranjang;
import com.pratamatechnocraft.tokason.Model.BaseUrlApiModel;
import com.pratamatechnocraft.tokason.Model.ListItemDetailTransaksi;
import com.pratamatechnocraft.tokason.BluetoothPrinter.BluetoothPrinter;
import com.pratamatechnocraft.tokason.Service.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InvoiceActivity extends AppCompatActivity {
    BaseUrlApiModel baseUrlApiModel = new BaseUrlApiModel();
    private String baseUrl=baseUrlApiModel.getBaseURL();
    private ProgressDialog progress;
    private AlertDialog alertDialog,alertDialog1;
    private AdapterRecycleViewDetailTransaksi adapterRecycleViewDetailTransaksi;
    private Button buttonBayarDetailTransaksi;
    private RecyclerView recyclerViewDetailTransaksi;
    private SwipeRefreshLayout refreshInvoice;
    private TextView txtNoInvoiceDetailTransaksi, txtTanggalTransaksiDetail, txtNamaKasirDetailTransaksi,txtStatusTransaksiDetailTransaksi,txtSubTotalDetailInvoice;
    private TextView txtDiskonRupiahDetailInvoice, txtDiskonDetailInvoice, txtPajakDetailInvoice,txtPajakRupiahDetailInvoice,txtTotalDetailInvoice,txtBayarDetailInvoice, txtKembaliDetailInvoice, txtNamaPelangganTransaksiDetail;
    private TableRow pelangganTransaksiDetail;
    private TextInputLayout inputLayoutBayarTransaksi;
    private EditText inputBayarTransaksi;
    private static final String API_URL = "api/transaksi?api=transaksidetail&kd_transaksi=";
    Intent intent;
    private List<ListItemDetailTransaksi> listItemDetailTransaksis;
    android.app.AlertDialog dialog;
    LayoutInflater inflater;
    View dialogView;
    Bitmap b;
    String jenisTransaksi, statusTransaksi;
    private DBDataSourceKeranjang dbDataSourceKeranjang;
    private String pajak, diskon;
    DecimalFormat formatter = new DecimalFormat("#,###,###");
    BluetoothAdapter btAdapter = null;
    private String jmlItemPrint;
    private int lenghtDetail;
    SessionManager sessionManager;
    HashMap<String, String> printer;

    String namaOutlet, alamatOutlet, noTelp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_invoice );
        sessionManager = new SessionManager( this );
        printer=sessionManager.getPrinter();
        intent = getIntent();

        if (intent.getBooleanExtra("done",true)){
            transaksiDone();
        }

        progress = new ProgressDialog(this);

        Toolbar ToolBarAtas2 = (Toolbar)findViewById(R.id.toolbar_invoice);
        setSupportActionBar(ToolBarAtas2);
        ToolBarAtas2.setLogoDescription("Detail Invoice");
        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back_black_24dp);
        upArrow.setColorFilter(ContextCompat.getColor(this, R.color.colorIcons), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerViewDetailTransaksi = (RecyclerView) findViewById(R.id.recycleViewDetailTransaksi);
        recyclerViewDetailTransaksi.setHasFixedSize(true);
        recyclerViewDetailTransaksi.setLayoutManager(new LinearLayoutManager(this));

        refreshInvoice = findViewById( R.id.refreshInvoice );

        pelangganTransaksiDetail = findViewById(R.id.pelangganTransaksiDetail);

        txtNoInvoiceDetailTransaksi = findViewById( R.id.txtNoInvoiceDetailTransaksi );
        txtTanggalTransaksiDetail = findViewById( R.id.txtTanggalTransaksiDetail );
        txtStatusTransaksiDetailTransaksi = findViewById( R.id.txtStatusTransaksiDetailTransaksi );
        txtSubTotalDetailInvoice = findViewById( R.id.txtSubTotalDetailInvoice);
        txtDiskonRupiahDetailInvoice = findViewById( R.id.txtDiskonRupiahDetailInvoice);
        txtDiskonDetailInvoice = findViewById( R.id.txtDiskonDetailInvoice);
        txtPajakDetailInvoice = findViewById( R.id.txtPajakDetailInvoice);
        txtPajakRupiahDetailInvoice = findViewById( R.id.txtPajakRupiahDetailInvoice);
        txtTotalDetailInvoice = findViewById( R.id.txtTotalDetailInvoice);
        txtBayarDetailInvoice = findViewById( R.id.txtBayarDetailInvoice);
        txtKembaliDetailInvoice = findViewById( R.id.txtKembaliDetailInvoice);
        txtNamaPelangganTransaksiDetail = findViewById(R.id.txtNamaPelangganTransaksiDetail);
        txtNamaKasirDetailTransaksi = findViewById( R.id.txtNamaKasirDetailTransaksi );
        buttonBayarDetailTransaksi = findViewById( R.id.buttonBayarDetailTransaksi );

        buttonBayarDetailTransaksi.setVisibility( View.GONE );

        listItemDetailTransaksis = new ArrayList<>();
        adapterRecycleViewDetailTransaksi = new AdapterRecycleViewDetailTransaksi( listItemDetailTransaksis, this);

        loadDetail( intent.getStringExtra( "kdTransaksi" ) );

        refreshInvoice.setOnRefreshListener( new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                listItemDetailTransaksis.clear();
                adapterRecycleViewDetailTransaksi.notifyDataSetChanged();
                loadDetail(intent.getStringExtra( "kdTransaksi" ));
            }
        } );

        buttonBayarDetailTransaksi.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBayarDialog();
            }
        } );

        recyclerViewDetailTransaksi.setAdapter( adapterRecycleViewDetailTransaksi );
    }

    private void showBayarDialog(){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Inputkan Pembayaran");

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_bayar, null ,false);
        inputBayarTransaksi = viewInflated.findViewById(R.id.inputBayarTransaksi);
        inputLayoutBayarTransaksi = viewInflated.findViewById(R.id.inputLayoutBayarTransaksi);

        inputBayarTransaksi.addTextChangedListener( new InvoiceActivity.MyTextWatcher( inputBayarTransaksi) );

        builder.setView(viewInflated);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!validateBayar()){
                    return;
                }else {
                    progress.setMessage("Mohon Ditunggu, Sedang diProses.....");
                    progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progress.setIndeterminate(false);
                    progress.setCanceledOnTouchOutside(false);
                    prosesBayar(inputBayarTransaksi.getText().toString(), String.valueOf(Double.parseDouble(inputBayarTransaksi.getText().toString()) - Double.parseDouble(txtTotalDetailInvoice.getText().toString().replace("Rp. ", "").replace(".", ""))));
                    inputBayarTransaksi.onEditorAction(EditorInfo.IME_ACTION_DONE);
                    dialog.dismiss();
                }
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_activity_invoice,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
/*            case R.id.icon_edit1:
                dbDataSourceKeranjang = new DBDataSourceKeranjang( this );
                dbDataSourceKeranjang.open();
                dbDataSourceKeranjang.deleteAll();
                dbDataSourceKeranjang.deletePelangganPilihAll();
                Intent i = new Intent(InvoiceActivity.this, TransaksiBaruActivity.class );
                i.putExtra( "kdTransaksi",intent.getStringExtra( "kdTransaksi" ) );
                i.putExtra( "noInvoice",intent.getStringExtra( "noInvoice" ) );
                i.putExtra( "type", jenisTransaksi );
                i.putExtra( "form","1");
                i.putExtra( "pajak",pajak);
                i.putExtra( "diskon",diskon);
                i.putExtra( "statusTransaksi",statusTransaksi);
                startActivity(i);
                finish();
                return true;
            case R.id.icon_hapus1:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage("Yakin Ingin Menghapus Data Ini ??");
                alertDialogBuilder.setPositiveButton("Iya",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                deleteInvoice(intent.getStringExtra( "kdTransaksi" ));
                            }
                        });

                alertDialogBuilder.setNegativeButton("Tidak",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                return true;*/
            case R.id.icon_bagikan_invoice:
                dialogBagikanInvoice();
                return true;
            case R.id.icon_print_invoice:
                printInvoice();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadDetail(String kdTransaksi){
        refreshInvoice.setRefreshing(true);
        StringRequest stringRequest = new StringRequest( Request.Method.GET, baseUrl+API_URL+kdTransaksi,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        Log.d("INVOICE ", "onResponse: "+response);
                        final JSONObject invoicedetail = new JSONObject(response);
                        namaOutlet = invoicedetail.getString("nama_outlet");
                        alamatOutlet = invoicedetail.getString("alamat_outlet");
                        noTelp = invoicedetail.getString("no_telp");
                        pajak=invoicedetail.getString("pajak");
                        diskon=invoicedetail.getString("diskon");
                        if (invoicedetail.getString( "status" ).equals( "0" )){
                            txtStatusTransaksiDetailTransaksi.setText( "Lunas" );
                            buttonBayarDetailTransaksi.setVisibility( View.GONE );
                        }else{
                            txtStatusTransaksiDetailTransaksi.setText( "Belum Lunas" );
                            buttonBayarDetailTransaksi.setVisibility( View.VISIBLE );
                        }
                        if (invoicedetail.getString( "jenis_transaksi" ).equals( "0" )){
                            txtNoInvoiceDetailTransaksi.setText(  "#PL"+invoicedetail.getString( "no_invoice" ));
                            pelangganTransaksiDetail.setVisibility(View.VISIBLE);
                        }else{
                            txtNoInvoiceDetailTransaksi.setText(  "#PB"+invoicedetail.getString( "no_invoice" ));
                            pelangganTransaksiDetail.setVisibility(View.GONE);
                        }

                        jenisTransaksi=invoicedetail.getString( "jenis_transaksi" );
                        statusTransaksi=invoicedetail.getString("status");

                        txtTanggalTransaksiDetail.setText( invoicedetail.getString( "tgl_transaksi" ) );
                        txtSubTotalDetailInvoice.setText(  "Rp. "+invoicedetail.getString( "harga_total" ));
                        jmlItemPrint=invoicedetail.getString("jml_item");

                        JSONArray detailinvoice = invoicedetail.getJSONArray("detailinvoice");
                        lenghtDetail = detailinvoice.length();
                        for (int i = 0; i<detailinvoice.length(); i++){
                            JSONObject detailInvoiceObject = detailinvoice.getJSONObject( i );
                            String harga;
                            String namaBarang;
                            if (invoicedetail.getString( "jenis_transaksi" ).equals( "0" )){
                                harga=detailInvoiceObject.getString( "harga_jual_detail" );
                            }else{
                                harga=detailInvoiceObject.getString( "harga_beli_detail" );
                            }

                            if (!detailInvoiceObject.getString("catatan").equals("")){
                                namaBarang=detailInvoiceObject.getString( "nama_barang" )+" ("+detailInvoiceObject.getString("catatan")+")";
                            }else{
                                namaBarang=detailInvoiceObject.getString( "nama_barang" );
                            }

                            ListItemDetailTransaksi listItemDetailTransaksi = new ListItemDetailTransaksi(
                                    namaBarang,
                                    detailInvoiceObject.getString( "qty" ),
                                    harga
                            );

                            Log.d( "TAG", "nama_barang: "+detailInvoiceObject.getString( "nama_barang" ) );

                            listItemDetailTransaksis.add( listItemDetailTransaksi );
                            adapterRecycleViewDetailTransaksi.notifyDataSetChanged();
                        }

//                        txtNamaPelangganTransaksiDetail.setText(invoicedetail.getString( "nama_pelanggan" ));
                        txtNamaPelangganTransaksiDetail.setText("-");

                        txtNamaKasirDetailTransaksi.setText( invoicedetail.getString( "nama_user" ) );

                        txtDiskonDetailInvoice.setText( "Diskon ("+invoicedetail.getString( "diskon" )+"%) :" );
                        txtDiskonRupiahDetailInvoice.setText( "Rp. "+invoicedetail.getString( "diskon_rupiah" ) );
                        txtPajakDetailInvoice.setText( "Pajak ("+invoicedetail.getString( "pajak" )+"%) :" );
                        txtPajakRupiahDetailInvoice.setText( "Rp. "+invoicedetail.getString( "pajak_rupiah" ) );
                        txtTotalDetailInvoice.setText("Rp. "+invoicedetail.getString( "total" ));
                        txtBayarDetailInvoice.setText("Rp. "+invoicedetail.getString( "bayar" ));
                        txtKembaliDetailInvoice.setText("Rp. "+invoicedetail.getString( "kembali" ));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(InvoiceActivity.this, "Periksa koneksi & coba lagi", Toast.LENGTH_SHORT).show();
                    }
                    refreshInvoice.setRefreshing( false );
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(InvoiceActivity.this, "Periksa koneksi & coba lagi1", Toast.LENGTH_SHORT).show();
                    refreshInvoice.setRefreshing( false );
                }
            }
        );

        RequestQueue requestQueue = Volley.newRequestQueue( InvoiceActivity.this );
        requestQueue.add( stringRequest );
    }

    private void deleteInvoice(String kdTransaksi){
        StringRequest stringRequest = new StringRequest( Request.Method.GET, baseUrl+"api/transaksi?api=delete&kd_transaksi="+kdTransaksi,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String kode = jsonObject.getString("kode");
                            if (kode.equals("1")) {
                                finish();
                                Toast.makeText(InvoiceActivity.this, "Berhasil Menghapus Trnsaksi", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(InvoiceActivity.this, "Gagal Menghapus Trnsaksi", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(InvoiceActivity.this, "Periksa koneksi & coba lagi", Toast.LENGTH_SHORT).show();
                        }
                        refreshInvoice.setRefreshing( false );
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(InvoiceActivity.this, "Periksa koneksi & coba lagi1", Toast.LENGTH_SHORT).show();
                        refreshInvoice.setRefreshing( false );
                    }
                }
        );

        RequestQueue requestQueue = Volley.newRequestQueue( InvoiceActivity.this );
        requestQueue.add( stringRequest );
    }

    private void prosesBayar(final String bayar, final String kembali) {
        progress.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, baseUrl+"api/transaksi", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String kode = jsonObject.getString("kode");
                    if (kode.equals("1")) {
                        finish();
                        Toast.makeText(InvoiceActivity.this, "Berhasil Bayar", Toast.LENGTH_SHORT).show();

                    }else{
                        Toast.makeText(InvoiceActivity.this, "Gagal Bayar", Toast.LENGTH_SHORT).show();
                    }
                    progress.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d( "TAG", e.toString() );
                    Toast.makeText(InvoiceActivity.this, "Periksa koneksi & coba lagi", Toast.LENGTH_SHORT).show();
                    progress.dismiss();
                }
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(InvoiceActivity.this, "Periksa koneksi & coba lagi1", Toast.LENGTH_SHORT).show();
                progress.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put( "kd_transaksi", intent.getStringExtra( "kdTransaksi" ) );
                params.put( "bayar", bayar );
                params.put( "kembali", kembali );
                params.put( "api", "bayar" );
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void bagikan(WebView webView){
        Picture picture = webView.capturePicture();
        b = Bitmap.createBitmap(
                picture.getWidth(), picture.getHeight(), Bitmap.Config.ARGB_8888);
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
        try {
            File file =  new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "invoice_no_"+txtNoInvoiceDetailTransaksi.getText()+"_"+System.currentTimeMillis()+".jpg");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.close();

            bmpUri = FileProvider.getUriForFile(InvoiceActivity.this, "com.pratamatechnocraft.sisirKayuManis.fileprovider", file);

        } catch (IOException e) {
            Log.d("TAG", "getBitmapFromDrawable: "+e);
            e.printStackTrace();
        }
        return bmpUri;
    }

    private void transaksiDone(){
        Button buttonOkDialogSelesai;
        ImageButton imageButtonShareDialog,imageButtonPrintDialog;
        TextView txtKembalianDone;
        dialog = new android.app.AlertDialog.Builder(this).create();
        inflater = dialog.getLayoutInflater();
        dialogView = inflater.inflate(R.layout.fragment_dialog_transaksi_selesai, null);
        dialog.setView(dialogView);
        dialog.setCancelable(true);

        buttonOkDialogSelesai = dialogView.findViewById(R.id.buttonOkDialogSelesai);

        imageButtonPrintDialog = dialogView.findViewById(R.id.imageButtonPrintDialog);
        imageButtonShareDialog = dialogView.findViewById(R.id.imageButtonShareDialog);

        txtKembalianDone = dialogView.findViewById(R.id.txtKembalianDone);

        if (intent.getStringExtra( "form" ).equals("0")){
            txtKembalianDone.setVisibility(View.VISIBLE);
            txtKembalianDone.setText("dan dibayarkan dengan kembalian sebesar Rp. "+formatter.format(Double.parseDouble(intent.getStringExtra("kembali"))));
        }else{
            txtKembalianDone.setVisibility(View.GONE);
        }

        buttonOkDialogSelesai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });

        imageButtonPrintDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printInvoice();
                dialog.dismiss();
            }
        });

        imageButtonShareDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               dialog.dismiss();
               dialogBagikanInvoice();
            }
        });


        dialog.show();
    }

    private void dialogBagikanInvoice(){
        Button buttonBatalDialogBagikan, buttonOkDialogBagikan;
        dialog = new android.app.AlertDialog.Builder(this).create();
        inflater = dialog.getLayoutInflater();
        dialogView = inflater.inflate(R.layout.fragment_dialog_bagikan_invoice, null);
        dialog.setTitle("Bagikan");
        dialog.setView(dialogView);
        dialog.setCancelable(true);

        buttonOkDialogBagikan = dialogView.findViewById(R.id.buttonOkDialogBagikan);
        buttonBatalDialogBagikan = dialogView.findViewById(R.id.buttonBatalDialogBagikan);

        final WebView webViewBagikan = (WebView) dialogView.findViewById(R.id.webViewBagikan);
        webViewBagikan.setWebViewClient(new WebViewClient());
        Log.d("TAG", "dialogBagikanInvoice: "+intent.getStringExtra( "kdTransaksi" ));
        webViewBagikan.loadUrl(baseUrl+"print_invoice?no_invoice="+intent.getStringExtra( "kdTransaksi" ));
        buttonOkDialogBagikan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bagikan(webViewBagikan);
                dialog.dismiss();

            }
        });

        buttonBatalDialogBagikan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    /*INPUT*/
    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.inputBayarTransaksi:
                    validateBayar();
                    break;
            }
        }
    }

    private boolean validateBayar() {
        if (!inputBayarTransaksi.getText().toString().trim().isEmpty()){
            if (Double.parseDouble(inputBayarTransaksi.getText().toString())<Double.parseDouble(txtTotalDetailInvoice.getText().toString().replace("Rp. ", "").replace(".",""))) {
                inputLayoutBayarTransaksi.setError("Uang yang dibayarkan kurang");
                requestFocus(inputBayarTransaksi);
                return false;
            } else {
                inputLayoutBayarTransaksi.setErrorEnabled(false);
            }

        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
           getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void printInvoice(){
        if (sessionManager.isPrinter()==false){
            alert("Gagal Tersambung!!", "Setting Printer terlebih dahulu");
        }else {
            btAdapter = BluetoothAdapter.getDefaultAdapter();
            if (!btAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 6);
            } else {
                BluetoothDevice mBtDevice = btAdapter.getRemoteDevice(printer.get(sessionManager.ADDRESS_BLUETOOTH));
                final BluetoothPrinter mPrinter = new BluetoothPrinter(mBtDevice);
                mPrinter.connectPrinter(new BluetoothPrinter.PrinterConnectListener() {

                    @Override
                    public void onConnected() {
                        DecimalFormat formatter = new DecimalFormat("#,###,###");
                        
                        mPrinter.printHead(namaOutlet, alamatOutlet, noTelp);
                        mPrinter.printCustom(mPrinter.leftRightAlign("Tanggal", txtTanggalTransaksiDetail.getText().toString(),printer.get(sessionManager.UKURAN_KERTAS)), 0, 1);
                        mPrinter.printCustom(mPrinter.leftRightAlign("No Invoice", txtNoInvoiceDetailTransaksi.getText().toString(),printer.get(sessionManager.UKURAN_KERTAS)), 0, 1);
                        if(txtNoInvoiceDetailTransaksi.getText().toString().contains("#PL")){
                            mPrinter.printCustom(mPrinter.leftRightAlign("Pelanggan", txtNamaPelangganTransaksiDetail.getText().toString(),printer.get(sessionManager.UKURAN_KERTAS)), 0, 1);
                        }
                        mPrinter.printCustom(mPrinter.leftRightAlign("Kasir", txtNamaKasirDetailTransaksi.getText().toString(),printer.get(sessionManager.UKURAN_KERTAS)), 0, 1);
                        mPrinter.printCustom(mPrinter.leftRightAlign("Jml Item", jmlItemPrint,printer.get(sessionManager.UKURAN_KERTAS)), 0, 1);
                        mPrinter.printCustom(mPrinter.leftRightAlign("Status", txtStatusTransaksiDetailTransaksi.getText().toString(),printer.get(sessionManager.UKURAN_KERTAS)), 0, 1);
                        mPrinter.printBatas(printer.get(sessionManager.UKURAN_KERTAS));
                        mPrinter.printCustom(mPrinter.leftRightAlign("Barang", "Total Harga",printer.get(sessionManager.UKURAN_KERTAS)), 1, 1);
                        mPrinter.printBatas(printer.get(sessionManager.UKURAN_KERTAS));
                        mPrinter.setAlign(102);
                        for (int i = 0; i < lenghtDetail; i++) {
                            mPrinter.setAlign(102);
                            ListItemDetailTransaksi listItemDetailTransaksi = listItemDetailTransaksis.get(i);
                            int subTotal = Integer.parseInt(listItemDetailTransaksi.getQty()) * Integer.parseInt(listItemDetailTransaksi.getHarga());
                            mPrinter.printText(mPrinter.leftRightAlign(listItemDetailTransaksi.getNamaBarang(), "", printer.get(sessionManager.UKURAN_KERTAS)));
                            mPrinter.printNewLine();
                            mPrinter.printCustom(mPrinter.leftRightAlign(listItemDetailTransaksi.getQty() + " x @Rp." + formatter.format(Double.parseDouble(listItemDetailTransaksi.getHarga())), "Rp. " + formatter.format(Double.parseDouble(String.valueOf(subTotal))), printer.get(sessionManager.UKURAN_KERTAS)), 0, 1);
                        }
                        mPrinter.setAlign(100);
                        mPrinter.printBatas(printer.get(sessionManager.UKURAN_KERTAS));
                        mPrinter.printCustom(mPrinter.leftRightAlign("Sub Total", txtSubTotalDetailInvoice.getText().toString(), printer.get(sessionManager.UKURAN_KERTAS)), 1, 1);
                        mPrinter.printCustom(mPrinter.leftRightAlign(txtDiskonDetailInvoice.getText().toString().replace(":", ""), txtDiskonRupiahDetailInvoice.getText().toString(), printer.get(sessionManager.UKURAN_KERTAS)), 1, 1);
                        mPrinter.printCustom(mPrinter.leftRightAlign(txtPajakDetailInvoice.getText().toString().replace(":", ""), txtPajakRupiahDetailInvoice.getText().toString(), printer.get(sessionManager.UKURAN_KERTAS)), 1, 1);
                        mPrinter.printCustom(mPrinter.leftRightAlign("Total", txtTotalDetailInvoice.getText().toString(), printer.get(sessionManager.UKURAN_KERTAS)), 1, 1);
                        mPrinter.printCustom(mPrinter.leftRightAlign("Tunai", txtBayarDetailInvoice.getText().toString(), printer.get(sessionManager.UKURAN_KERTAS)), 1, 1);
                        mPrinter.printCustom(mPrinter.leftRightAlign("Kembali", txtKembaliDetailInvoice.getText().toString(), printer.get(sessionManager.UKURAN_KERTAS)), 1, 1);
                        mPrinter.printBatas(printer.get(sessionManager.UKURAN_KERTAS));
                        mPrinter.printCustom("Terima kasih atas kunjungan anda", 0, 1);
                        mPrinter.printCustom("Semoga puas dengan layanan kami", 0, 1);
                        mPrinter.printNewLine();
                        mPrinter.printBatasDua(printer.get(sessionManager.UKURAN_KERTAS));
                        mPrinter.printNewLine();
                        mPrinter.printNewLine();

                        /*if(txtNoInvoiceDetailTransaksi.getText().toString().contains("#PL")) {
                            mPrinter.printHead();
                            mPrinter.printCustom(mPrinter.leftRightAlign("Tanggal", txtTanggalTransaksiDetail.getText().toString(), printer.get(sessionManager.UKURAN_KERTAS)), 0, 1);
                            mPrinter.printCustom(mPrinter.leftRightAlign("No Invoice", txtNoInvoiceDetailTransaksi.getText().toString(), printer.get(sessionManager.UKURAN_KERTAS)), 0, 1);
                            mPrinter.printCustom(mPrinter.leftRightAlign("Pelanggan", txtNamaPelangganTransaksiDetail.getText().toString(), printer.get(sessionManager.UKURAN_KERTAS)), 0, 1);
                            mPrinter.printCustom(mPrinter.leftRightAlign("Kasir", txtNamaKasirDetailTransaksi.getText().toString(), printer.get(sessionManager.UKURAN_KERTAS)), 0, 1);
                            mPrinter.printCustom(mPrinter.leftRightAlign("Jml Item", jmlItemPrint, printer.get(sessionManager.UKURAN_KERTAS)), 0, 1);
                            mPrinter.printCustom(mPrinter.leftRightAlign("Status", txtStatusTransaksiDetailTransaksi.getText().toString(), printer.get(sessionManager.UKURAN_KERTAS)), 0, 1);
                            mPrinter.printBatas(printer.get(sessionManager.UKURAN_KERTAS));
                            mPrinter.printCustom(mPrinter.leftRightAlign("Barang", "Kuantitas", printer.get(sessionManager.UKURAN_KERTAS)), 1, 1);
                            mPrinter.setAlign(102);
                            mPrinter.printBatas(printer.get(sessionManager.UKURAN_KERTAS));
                            for (int i = 0; i < lenghtDetail; i++) {
                                mPrinter.setAlign(102);
                                ListItemDetailTransaksi listItemDetailTransaksi = listItemDetailTransaksis.get(i);
                                mPrinter.printText(mPrinter.leftRightAlign(listItemDetailTransaksi.getNamaBarang() + " - "+listItemDetailTransaksi.getQty(), "", printer.get(sessionManager.UKURAN_KERTAS)));
                                mPrinter.printNewLine();
                            }
                            mPrinter.printBatas(printer.get(sessionManager.UKURAN_KERTAS));
                            mPrinter.printNewLine();
                            mPrinter.printNewLine();
                            mPrinter.printBatasDua(printer.get(sessionManager.UKURAN_KERTAS));
                            mPrinter.printNewLine();
                            mPrinter.printNewLine();
                        }*/

                    }

                    @Override
                    public void onFailed() {
                        alert("Gagal Tersambung!!", "Setting Printer terlebih dahulu");
                        Log.d("BluetoothPrinter", "Conection failed");
                    }

                });
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 6) {
            if (resultCode == RESULT_OK) {
                printInvoice();
            }
            if (resultCode == RESULT_CANCELED) {
                // Request denied by user, or an error was encountered while
                // attempting to enable bluetooth
            }
        }
    }

    private void alert(String title, String message){
        androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                alertDialog1.dismiss();
            }
        });

        alertDialog1 = alertDialogBuilder.create();
        alertDialog1.show();
    }

}
