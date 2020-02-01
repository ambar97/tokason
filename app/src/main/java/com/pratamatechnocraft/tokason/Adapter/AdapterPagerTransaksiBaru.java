package com.pratamatechnocraft.tokason.Adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.annotation.NonNull;
import com.google.android.material.textfield.TextInputLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pratamatechnocraft.tokason.BarangTransaksiActivity;
import com.pratamatechnocraft.tokason.ScannerBarcode;
import com.pratamatechnocraft.tokason.InvoiceActivity;
import com.pratamatechnocraft.tokason.Model.BaseUrlApiModel;
import com.pratamatechnocraft.tokason.Model.ModelKeranjang;
import com.pratamatechnocraft.tokason.Model.ModelPelangganPilih;
import com.pratamatechnocraft.tokason.PelangganTransaksiActivity;
import com.pratamatechnocraft.tokason.R;
import com.pratamatechnocraft.tokason.Service.SessionManager;
import com.pratamatechnocraft.tokason.TransaksiBaruActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class AdapterPagerTransaksiBaru extends PagerAdapter {

    private int[] layouts=null;
    private LayoutInflater layoutInflater;
    private Context context;
    private RecyclerView recyclerViewKeranjang;
    private AdapterRecycleViewKeranjang adapterRecycleViewKeranjang;
    private DBDataSourceKeranjang dbDataSourceKeranjang;
    private ArrayList<ModelKeranjang> modelKeranjangs;
    private ArrayList<ModelPelangganPilih> modelPelangganPilihs;
    private TextView noDataKeranjang,txtJmlItemKeranjang, txtHargaTotalKeranjang, txtKodePelanggan, txtKodePelangganHidden, txtNamaPelangganTransaksi, txtNotelpPelangganTransaksi, txtAlamatPelangganTransaksi, txtTotalHargaSemuaTransaksi;
    private Button tambahBarangKeKeranjang,buttonSimpanTransaksi,buttonBayarTransaksi,buttonPilihPelanggan;
    private TextInputLayout inputLayoutPajakTransaksi, inputLayoutDiskonTransaksi, inputLayoutBayarTransaksi;
    private EditText inputPajakTransaksi, inputDiskonTransaksi, inputBayarTransaksi;
    private ImageButton imageButtonScanBarcode;
    private int type,form;
    private ProgressDialog progress;
    SessionManager sessionManager;
    HashMap<String, String> user=null;
    private BaseUrlApiModel baseUrlApiModel = new BaseUrlApiModel();
    private String baseUrl=baseUrlApiModel.getBaseURL(), kdTransaksi, statusTransaksi;
    private static final String API_URL = "api/transaksi";
    private int jmlItem;
    private int totalHarga;
    private double kembali, diskon ,pajak, totalHargaSemua;
    private String pajakTxt, diskonTxt, bayarTxt="0";
    DecimalFormat decimalFormat;

    public AdapterPagerTransaksiBaru(int[] layouts, Context context, int type, int form, String kdTransaksi, String pajakTxt, String diskonTxt, String statusTransaksi) {
        this.layouts = layouts;
        layoutInflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        this.context = context;
        this.type = type;
        this.form = form;
        this.kdTransaksi =kdTransaksi;
        this.pajakTxt=pajakTxt;
        this.diskonTxt=diskonTxt;
        this.statusTransaksi=statusTransaksi;
    }

    @Override
    public int getCount() {
        return layouts.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view==o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        final DecimalFormat decimalFormat = new DecimalFormat("#,###,###");
        final View view = layoutInflater.inflate( layouts[position],container,false );
        container.addView( view );
        sessionManager = new SessionManager( context );
        user = sessionManager.getUserDetail();
        progress = new ProgressDialog(context);
        dbDataSourceKeranjang = new DBDataSourceKeranjang(context);
        dbDataSourceKeranjang.open();
        modelKeranjangs = dbDataSourceKeranjang.getAllKeranjang();
        noDataKeranjang = view.findViewById( R.id.noDataKeranjang );
        txtJmlItemKeranjang = view.findViewById( R.id.txtJmlItemKeranjang );
        txtHargaTotalKeranjang = view.findViewById( R.id.txtHargaTotallKeranjang );
        recyclerViewKeranjang = view.findViewById( R.id.recycleViewKeranjang );
        tambahBarangKeKeranjang =view.findViewById( R.id.tambahBarangKeKeranjang );

        buttonBayarTransaksi = view.findViewById( R.id.buttonBayarTransaksi);
        buttonSimpanTransaksi = view.findViewById( R.id.buttonSimpanTransaksi );
        inputLayoutPajakTransaksi = view.findViewById(R.id.inputLayoutPajakTransaksi);
        inputPajakTransaksi = view.findViewById(R.id.inputPajakTransaksi);
        inputLayoutDiskonTransaksi = view.findViewById(R.id.inputLayoutDiskonTransaksi);
        inputDiskonTransaksi = view.findViewById(R.id.inputDiskonTransaksi);
        txtTotalHargaSemuaTransaksi = view.findViewById(R.id.txtTotalHargaSemuaTransaksi);

        txtKodePelanggan = view.findViewById(R.id.txtKodePelanggan);
        txtKodePelangganHidden = view.findViewById(R.id.txtKodePelangganHidden);
        txtNamaPelangganTransaksi = view.findViewById(R.id.txtNamaPelangganTransaksi);
        txtNotelpPelangganTransaksi = view.findViewById(R.id.txtNotelpPelangganTransaksi);
        txtAlamatPelangganTransaksi = view.findViewById(R.id.txtAlamatPelangganTransaksi);
        buttonPilihPelanggan = view.findViewById(R.id.buttonPilihPelanggan);

        imageButtonScanBarcode = view.findViewById(R.id.imageButtonScanBarcode);

        final int pertama, kedua, ketiga;

        if(getCount()==3){
            pertama=0;
            kedua=1;
            ketiga=2;
        }else{
            pertama=4;
            kedua=0;
            ketiga=1;
        }

        if (position==pertama){
//            dbDataSourceKeranjang.close();
//            dbDataSourceKeranjang.open();
//            ModelPelangganPilih modelPelangganPilih;
//            modelPelangganPilihs = dbDataSourceKeranjang.getAllModelPelangganPilih();
//            if (modelPelangganPilihs.size()!=0){
//                modelPelangganPilih = modelPelangganPilihs.get(0);
//                Date date = null;
//                try {
//                    date = new SimpleDateFormat("yyyy-MM-dd").parse(modelPelangganPilih.getTanggalTerdaftarPelanggan());
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//                String formatedDate = new SimpleDateFormat("yyyyMMdd").format(date);
//                txtKodePelanggan.setText("#PLG"+formatedDate+modelPelangganPilih.getKdPelanggan());
//                txtKodePelangganHidden.setText(modelPelangganPilih.getKdPelanggan());
//                txtNamaPelangganTransaksi.setText(modelPelangganPilih.getNamaPelanggan());
//                txtNotelpPelangganTransaksi.setText(modelPelangganPilih.getNoTelpPelanggan());
//                txtAlamatPelangganTransaksi.setText(modelPelangganPilih.getAlamatPelanggan());
//            }
//            buttonPilihPelanggan.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent( context, PelangganTransaksiActivity.class);
//                    context.startActivity(intent);
//                }
//            });
        }else if (position==kedua){
            recyclerViewKeranjang.setHasFixedSize(true);
            recyclerViewKeranjang.setLayoutManager(new LinearLayoutManager(context));
            adapterRecycleViewKeranjang = new AdapterRecycleViewKeranjang( modelKeranjangs, context, txtJmlItemKeranjang, txtHargaTotalKeranjang, type);
            recyclerViewKeranjang.setAdapter( adapterRecycleViewKeranjang );
            adapterRecycleViewKeranjang.notifyDataSetChanged();

            if (adapterRecycleViewKeranjang.getItemCount()==0){
                noDataKeranjang.setVisibility( View.VISIBLE );
                recyclerViewKeranjang.setVisibility( View.GONE );
            }

            tambahBarangKeKeranjang.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent( context, BarangTransaksiActivity.class);
                    if (type==0){
                        intent.putExtra("type", "0" );
                    }else{
                        intent.putExtra( "type", "1" );
                    }
                    context.startActivity(intent);
                }
            } );

            imageButtonScanBarcode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent( context, ScannerBarcode.class);
                    if (type==0){
                        intent.putExtra( "jenis_transaksi", "0" );
                    }else{
                        intent.putExtra( "jenis_transaksi", "1" );
                    }
                    intent.putExtra( "type", "1" );
                    context.startActivity(intent);
                }
            });
        }else if(position==ketiga){
//            final String[] kdPelanggang = new String[1];
            totalHarga=0;
            jmlItem=0;
            dbDataSourceKeranjang.close();
            dbDataSourceKeranjang.open();
            ModelKeranjang modelKeranjang;
            modelKeranjangs = dbDataSourceKeranjang.getAllKeranjang();
            for (int i=0;i<modelKeranjangs.size();i++){
                modelKeranjang = modelKeranjangs.get( i );
                int subTotal = modelKeranjang.getHargaBarang() * modelKeranjang.getQty();
                jmlItem=jmlItem+modelKeranjang.getQty();
                totalHarga=totalHarga+subTotal;
            }
            diskon = (Double.parseDouble(diskonTxt)/100)*(totalHarga);
            pajak = (Double.parseDouble(pajakTxt)/100)*(totalHarga-diskon);
            inputPajakTransaksi.setText(pajakTxt);
            inputDiskonTransaksi.setText(diskonTxt);
            totalHargaSemua = (totalHarga-diskon)+pajak;
            txtTotalHargaSemuaTransaksi.setText("Rp. "+String.valueOf(decimalFormat.format(totalHargaSemua)));

            inputPajakTransaksi.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(s.length() != 0){
                        txtTotalHargaSemuaTransaksi = view.findViewById(R.id.txtTotalHargaSemuaTransaksi);
                        inputPajakTransaksi = view.findViewById(R.id.inputPajakTransaksi);
                        pajak = (Double.parseDouble(s.toString())/100)*(totalHarga-diskon);
                        pajakTxt = s.toString();
                        totalHargaSemua = (totalHarga-diskon)+pajak;
                        txtTotalHargaSemuaTransaksi.setText("Rp. "+String.valueOf(decimalFormat.format(totalHargaSemua)));
                    }else{
                        txtTotalHargaSemuaTransaksi = view.findViewById(R.id.txtTotalHargaSemuaTransaksi);
                        inputPajakTransaksi = view.findViewById(R.id.inputPajakTransaksi);
                        pajak = (0/100)*(totalHarga-diskon);
                        pajakTxt = "0";
                        totalHargaSemua = (totalHarga-diskon)+pajak;
                        txtTotalHargaSemuaTransaksi.setText("Rp. "+String.valueOf(decimalFormat.format(totalHargaSemua)));
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            inputDiskonTransaksi.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(s.length() != 0){
                        txtTotalHargaSemuaTransaksi = view.findViewById(R.id.txtTotalHargaSemuaTransaksi);
                        inputDiskonTransaksi = view.findViewById(R.id.inputDiskonTransaksi);
                        diskon=(Double.parseDouble(s.toString())/100)*totalHarga;
                        diskonTxt = s.toString();
                        pajak = (Double.parseDouble(pajakTxt)/100)*(totalHarga-diskon);
                        totalHargaSemua = (totalHarga-diskon)+pajak;
                        txtTotalHargaSemuaTransaksi.setText("Rp. "+String.valueOf(decimalFormat.format(totalHargaSemua)));
                    }else{
                        txtTotalHargaSemuaTransaksi = view.findViewById(R.id.txtTotalHargaSemuaTransaksi);
                        inputDiskonTransaksi = view.findViewById(R.id.inputDiskonTransaksi);
                        diskon=(0/100)*(totalHarga);
                        diskonTxt = "0";
                        pajak = (Double.parseDouble(pajakTxt)/100)*(totalHarga-diskon);
                        totalHargaSemua = (totalHarga-diskon)+pajak;
                        txtTotalHargaSemuaTransaksi.setText("Rp. "+String.valueOf(decimalFormat.format(totalHargaSemua)));
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });


            buttonSimpanTransaksi.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    progress.setMessage("Mohon Ditunggu, Sedang diProses.....");
                    progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progress.setIndeterminate(false);
                    progress.setCanceledOnTouchOutside(false);

                    if (ketiga==2){
                        dbDataSourceKeranjang.close();
                        dbDataSourceKeranjang.open();
                        ModelPelangganPilih modelPelangganPilih;
                        modelPelangganPilihs =dbDataSourceKeranjang.getAllModelPelangganPilih();
                        modelPelangganPilih = modelPelangganPilihs.get(0);
//                        kdPelanggang[0] =modelPelangganPilih.getKdPelanggan();
                    }else{
//                        kdPelanggang[0] = "";
                    }

                    try {
                        prosesKeDB(
                                user.get( SessionManager.KD_USER),
                                "0",
                                String.valueOf( jmlItem ),
                                diskonTxt,
                                pajakTxt,
                                String.valueOf(totalHarga),
                                String.valueOf(0),
                                String.valueOf(0),
                                "1",
                                convertObjectArrayToString(dbDataSourceKeranjang.getArrayCatatanKeranjang(),","),
                                String.valueOf( type ),
                                convertObjectArrayToString(dbDataSourceKeranjang.getArrayKdBarangKeranjang(),","),
                                convertObjectArrayToString(dbDataSourceKeranjang.getArrayQtyKeranjang(),",")
                        );
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } );

            buttonBayarTransaksi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Inputkan Pembayaran");

                    View viewInflated = LayoutInflater.from(context).inflate(R.layout.dialog_bayar, null ,false);
                    inputBayarTransaksi = viewInflated.findViewById(R.id.inputBayarTransaksi);
                    inputLayoutBayarTransaksi = viewInflated.findViewById(R.id.inputLayoutBayarTransaksi);

                    inputBayarTransaksi.addTextChangedListener( new AdapterPagerTransaksiBaru.MyTextWatcher( inputBayarTransaksi) );

                    builder.setView(viewInflated);

                    builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            kembali=Double.parseDouble(inputBayarTransaksi.getText().toString())-totalHargaSemua;
                            if (!validateBayar()){
                                return;
                            }else {
                                progress.setMessage("Mohon Ditunggu, Sedang diProses.....");
                                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                progress.setIndeterminate(false);
                                progress.setCanceledOnTouchOutside(false);

                                if (ketiga==2){
                                    dbDataSourceKeranjang.close();
                                    dbDataSourceKeranjang.open();
                                    ModelPelangganPilih modelPelangganPilih;
                                    modelPelangganPilihs =dbDataSourceKeranjang.getAllModelPelangganPilih();
                                    modelPelangganPilih = modelPelangganPilihs.get(0);
//                                    kdPelanggang[0] =modelPelangganPilih.getKdPelanggan();
                                }else{
//                                    kdPelanggang[0] = "";
                                }
                                try {
                                    prosesKeDB(
                                            user.get( SessionManager.KD_USER),
                                            "0",
                                            String.valueOf(jmlItem),
                                            diskonTxt,
                                            pajakTxt,
                                            String.valueOf(totalHarga),
                                            inputBayarTransaksi.getText().toString(),
                                            String.valueOf(kembali),
                                            "0",
                                            convertObjectArrayToString(dbDataSourceKeranjang.getArrayCatatanKeranjang(),","),
                                            String.valueOf( type ),
                                            convertObjectArrayToString(dbDataSourceKeranjang.getArrayKdBarangKeranjang(),","),
                                            convertObjectArrayToString(dbDataSourceKeranjang.getArrayQtyKeranjang(),",")
                                    );
                                    inputBayarTransaksi.onEditorAction(EditorInfo.IME_ACTION_DONE);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
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
            });


        }
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        View view = (View) object;
        container.removeView( view );
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    private void prosesKeDB(final String kdUser, final String kdPelanggan, final String jmlItem, final String diskon, final String pajak, final String hargaTotal, final String bayar, final String kembali, final String status, final String catatan, final String jenisTransaksi, final String kdBarangKeranjang, final String qtyKeranjang) {
        progress.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, baseUrl+API_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String kode = jsonObject.getString("kode");
                    if (kode.equals("1")) {
                        if(form==0){
                            Toast.makeText(context, "Transaksi Berhasil", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(context, "Transaksi Berhasil diEdit", Toast.LENGTH_SHORT).show();
                        }
                        Intent i = new Intent(context, InvoiceActivity.class);
                        i.putExtra("done", true);
                        i.putExtra("kdTransaksi", jsonObject.getString( "kd_transaksi" ));
                        i.putExtra("form", status);
                        i.putExtra("kembali", jsonObject.getString( "kembali" ));
                        context.startActivity(i);
                        ((TransaksiBaruActivity)context).finish();
                    }else{
                        if(form==0){
                            Toast.makeText(context, "Transaksi Gagal", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(context, "Transaksi Gagal diEdit", Toast.LENGTH_SHORT).show();
                        }
                    }
                    progress.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d( "TAG", e.toString() );
                    Toast.makeText(context, "Periksa koneksi & coba lagi", Toast.LENGTH_SHORT).show();
                    progress.dismiss();
                }
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(context, "Periksa koneksi & coba lagi1", Toast.LENGTH_SHORT).show();
                progress.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                if (form==0){
                    params.put("kd_user", kdUser);
                    params.put("kd_outlet", user.get(SessionManager.KD_OUTLET));
                    params.put("kd_pelanggan", kdPelanggan);
                    params.put("jml_item", jmlItem);
                    params.put("diskon", diskon);
                    params.put("pajak", pajak);
                    params.put("harga_total", hargaTotal);
                    params.put("bayar", bayar);
                    params.put("kembali", kembali);
                    params.put("status", status);
                    params.put("catatan", catatan);
                    params.put("jenis_transaksi", jenisTransaksi);
                    params.put("kd_barang_keranjang", kdBarangKeranjang);
                    params.put("qty_keranjang", qtyKeranjang);
                    params.put( "api", "tambah" );
                }else{
                    params.put("kd_transaksi", kdTransaksi);
                    params.put("kd_pelanggan", kdPelanggan);
                    params.put("jml_item", jmlItem);
                    params.put("diskon", diskon);
                    params.put("pajak", pajak);
                    params.put("harga_total", hargaTotal);
                    params.put("bayar", bayar);
                    params.put("kembali", kembali);
                    params.put("status", status);
                    params.put("catatan", catatan);
                    params.put("kd_barang_keranjang", kdBarangKeranjang);
                    params.put("qty_keranjang", qtyKeranjang);
                    params.put( "api", "edit" );
                }
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    private static String convertObjectArrayToString(JSONArray arr, String delimiter) throws JSONException {
        StringBuilder sb = new StringBuilder();
        for (int i=0;i<arr.length();i++){
            sb.append(arr.getString( i )).append(delimiter);
        }
        return sb.substring(0, sb.length() - 1);
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
            if (Double.parseDouble(inputBayarTransaksi.getText().toString())<totalHargaSemua) {
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
            ((TransaksiBaruActivity)context).getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}
