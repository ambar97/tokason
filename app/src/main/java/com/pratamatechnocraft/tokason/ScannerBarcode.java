package com.pratamatechnocraft.tokason;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.Result;
import com.pratamatechnocraft.tokason.Adapter.DBDataSourceKeranjang;
import com.pratamatechnocraft.tokason.Model.BaseUrlApiModel;
import com.pratamatechnocraft.tokason.Model.ModelKeranjang;
import com.pratamatechnocraft.tokason.Service.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScannerBarcode  extends AppCompatActivity implements ZXingScannerView.ResultHandler{
    private ZXingScannerView mScannerView;
    private DBDataSourceKeranjang dbDataSourceKeranjang;
    ModelKeranjang modelKeranjang=null;
    private Integer jenis_transaksi;
    private androidx.appcompat.app.AlertDialog alertDialog1;
    BaseUrlApiModel baseUrlApiModel = new BaseUrlApiModel();
    private String baseUrl=baseUrlApiModel.getBaseURL();
    Intent intent;
    SessionManager sessionManager;
    HashMap<String, String> user;
    private static final String API_URL = "api/barang?api=caribarang&barcode=";
    ImageButton imgBtnFlash, imgBtnGantiKamera;
    TextView txtFlash;
    private int statusCamera=0;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner_barcode);
        // Programmatically initialize the scanner view
        mScannerView = findViewById(R.id.mScannerView);
        intent = getIntent();
        sessionManager = new SessionManager( this );
        user = sessionManager.getUserDetail();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera(statusCamera);
        /*mScannerView.setFlash(false);
        mScannerView.setAutoFocus(true);*/
        txtFlash = findViewById(R.id.txtFlash);
        imgBtnFlash = findViewById(R.id.imgBtnFlash);
        imgBtnGantiKamera = findViewById(R.id.imgBtnGantiKamera);

        imgBtnFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mScannerView.getFlash()==true){
                    mScannerView.setFlash(false);
                    imgBtnFlash.setImageResource(R.drawable.ic_flash_off_black_24dp);
                    txtFlash.setText("Flash OFF");
                }else{
                    mScannerView.setFlash(true);
                    imgBtnFlash.setImageResource(R.drawable.ic_flash_on_black_24dp);
                    txtFlash.setText("Flash ON");
                }
            }
        });

        imgBtnGantiKamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (statusCamera==1){
                    statusCamera=0;
                    mScannerView.stopCamera();
                    mScannerView.startCamera(0);
                    mScannerView.setFlash(false);
                    mScannerView.setAutoFocus(true);
                }else{
                    statusCamera=1;
                    mScannerView.stopCamera();
                    mScannerView.startCamera(1);
                    mScannerView.setFlash(false);
                    mScannerView.setAutoFocus(true);
                }
            }
        });

    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        Log.v("TAG", rawResult.getText()); // Prints scan results
        // Prints the scan format (qrcode, pdf417 etc.)
        Log.v("TAG", rawResult.getBarcodeFormat().toString());

        cariBarang(rawResult.getText());

        // If you would like to resume scanning, call this method below:
        mScannerView.resumeCameraPreview(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Register ourselves as a handler for scan results.
        mScannerView.setResultHandler(this);
        // Start camera on resume
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Stop camera on pause
        mScannerView.stopCamera();
    }

    private void cariBarang(final String barcode){
        StringRequest stringRequest = new StringRequest( Request.Method.GET, baseUrl+API_URL+barcode+"&kd_outlet="+user.get(SessionManager.KD_OUTLET),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            final JSONObject barangdetail = new JSONObject(response);
                            if(intent.getStringExtra("type").equals("2")){
                                if (barangdetail.getInt("kode") == 1) {
                                    Intent resultIntent = new Intent();
                                    resultIntent.putExtra("barcodeTerdeteksi", barcode);
                                    setResult(RESULT_OK, resultIntent);
                                    finish();
                                } else {
                                    alert("Peringatan !!", "Barcode ini sudah ada!!");
                                }
                            }else {
                                jenis_transaksi = Integer.parseInt(intent.getStringExtra( "jenis_transaksi" ));
                                if (barangdetail.getInt("kode") == 1) {
                                    alert("Peringatan !!", "Barang tidak ditemukan !!");
                                } else {
                                    if (jenis_transaksi == 0) {
                                        if (barangdetail.getString("stok").equals("0")) {
                                            alert("Peringatan !!", "Stok Kosong !!");
                                        } else {
                                            dbDataSourceKeranjang = new DBDataSourceKeranjang(ScannerBarcode.this);
                                            dbDataSourceKeranjang.open();
                                            if (dbDataSourceKeranjang.cekKeranjang(barangdetail.getString("kd_barang")) == false) {
                                                modelKeranjang = dbDataSourceKeranjang.createModelKeranjang(
                                                        barangdetail.getString("kd_barang"),
                                                        barangdetail.getString("nama_barang"),
                                                        barangdetail.getString("harga_jual"),
                                                        barangdetail.getString("gambar_barang"),
                                                        "1",
                                                        barangdetail.getString("stok"),
                                                        ""
                                                );
                                                finish();
                                            } else {
                                                ModelKeranjang modelKeranjang1 = dbDataSourceKeranjang.getKeranjang(barangdetail.getString("kd_barang"));
                                                if (barangdetail.getString("stok").equals(String.valueOf(modelKeranjang1.getQty()))) {
                                                    alert("Peringatan !!", "Kuantitas Melebihi Batas Stok");
                                                } else {
                                                    dbDataSourceKeranjang.updateBarang(barangdetail.getString("kd_barang"), modelKeranjang1.getQty(), modelKeranjang1.getCatatan());
                                                    finish();
                                                }

                                            }
                                        }
                                    } else {
                                        dbDataSourceKeranjang = new DBDataSourceKeranjang(ScannerBarcode.this);
                                        dbDataSourceKeranjang.open();
                                        if (dbDataSourceKeranjang.cekKeranjang(barangdetail.getString("kd_barang")) == false) {
                                            modelKeranjang = dbDataSourceKeranjang.createModelKeranjang(
                                                    barangdetail.getString("kd_barang"),
                                                    barangdetail.getString("nama_barang"),
                                                    barangdetail.getString("harga_beli"),
                                                    barangdetail.getString("gambar_barang"),
                                                    "1",
                                                    barangdetail.getString("stok"),
                                                    ""
                                            );
                                            finish();
                                        } else {
                                            ModelKeranjang modelKeranjang1 = dbDataSourceKeranjang.getKeranjang(barangdetail.getString("kd_barang"));
                                            dbDataSourceKeranjang.updateBarang(barangdetail.getString("kd_barang"), modelKeranjang1.getQty(), modelKeranjang1.getCatatan());
                                            finish();
                                        }
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ScannerBarcode.this, "Periksa koneksi & coba lagi", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ScannerBarcode.this, "Periksa koneksi & coba lagi1", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        RequestQueue requestQueue = Volley.newRequestQueue( ScannerBarcode.this );
        requestQueue.add( stringRequest );
    }

    private void alert(String title, String message){
        androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(ScannerBarcode.this);
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
