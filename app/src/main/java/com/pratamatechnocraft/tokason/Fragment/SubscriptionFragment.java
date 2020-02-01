package com.pratamatechnocraft.tokason.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.pratamatechnocraft.tokason.Model.BaseUrlApiModel;
import com.pratamatechnocraft.tokason.R;
import com.pratamatechnocraft.tokason.Service.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SubscriptionFragment extends Fragment {

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private TextView txtBukti, infoRekening;
    private Bitmap bitmap;
    private BottomSheetDialog bottomSheetDialog;
    private Button btnUpload, galeri, kamera, btnPilihFoto;
    private ImageView imgBukti;
    SessionManager sessionManager;
    private String kd_user;
    Context context;


    private BaseUrlApiModel baseUrlApiModel = new BaseUrlApiModel();
    private String baseUrl=baseUrlApiModel.getBaseURL();
    private static final String API_URL = "api/user";


    public SubscriptionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = getActivity();
        return inflater.inflate(R.layout.fragment_subscription, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(context);
        HashMap<String, String> user = sessionManager.getUserDetail();
        kd_user = user.get(SessionManager.KD_USER);

        btnUpload = view.findViewById(R.id.btn_upload);
        imgBukti = view.findViewById(R.id.img_bukti);
        txtBukti = view.findViewById(R.id.txt_bukti);
        btnPilihFoto = view.findViewById(R.id.pilih_foto);
        infoRekening = view.findViewById(R.id.info_rekening);
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
//        detailHarga.setText(formatRupiah.format((double)hargarumah));
        int i = 150000+Integer.parseInt(kd_user);
        String harga = formatRupiah.format((double)i);
        String string = infoRekening.getText().toString()+ harga;

        infoRekening.setText(string);

        btnPilihFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                klikPilihFoto();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fotoBukti = txtBukti.getText().toString().trim();
                if (!fotoBukti.isEmpty()) {
                    uploadBukti(kd_user, fotoBukti);
                } else {
                    Toast.makeText(context, "foto tidak boleh kosong", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    /*FOTO*/
    private void klikPilihFoto() {
        View view = getLayoutInflater().inflate(R.layout.fragment_bottom_sheet_dialog_tambah_foto, null);
        bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(view);

        galeri = view.findViewById( R.id.galeri1 );
        kamera = view.findViewById( R.id.kamera1 );
        galeri.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
                pilihFoto();
            }
        } );
        kamera.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
                takePicture();
            }
        } );

        bottomSheetDialog.show();
    }
    private void pilihFoto(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent,"Pilih Foto"),1);
    }
    private void takePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult( requestCode, resultCode, data );
        if (requestCode==1 && resultCode== Activity.RESULT_OK && data!=null && data.getData() !=null){
            Uri filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap( context.getContentResolver(),filePath );
                imgBukti.setImageBitmap( bitmap );
                txtBukti.setText( getStringImage( bitmap ) );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap mImageBitmap = (Bitmap) extras.get("data");
            imgBukti.setImageBitmap(mImageBitmap);
            txtBukti.setText( getStringImage( mImageBitmap ) );
        }
    }
    private String getStringImage(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(  );
        bitmap.compress( Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream );
        byte[] imageByteArray =  byteArrayOutputStream.toByteArray();

        return Base64.encodeToString( imageByteArray, Base64.DEFAULT );
    }
    /*FOTO*/

    private void uploadBukti(final String id_user, final String foto) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, baseUrl + API_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d("GGGGGG", "onResponse: "+response);
                    JSONObject jsonObject = new JSONObject(response);
                    String kode = jsonObject.getString("kode");
                    String msg = jsonObject.getString("pesan");
                    if (kode.equals("1")) {
                        WaitingConfirmationFragment waitingConfirmationFragment = new WaitingConfirmationFragment();

                        getFragmentManager().beginTransaction()
                                .replace(R.id.subscription_container, waitingConfirmationFragment )
                                .addToBackStack(null)
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .commit();

                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(context, LoginActivity.class);
//                        startActivity(intent);
//                        getActivity().finish();
                    } else {
                        Toast.makeText(context, msg , Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Periksa Koneksi dan Coba lagi" , Toast.LENGTH_SHORT).show();
                    Log.d("TAG", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("kd_user", id_user);
                params.put("foto", foto);
                params.put("api", "subscription");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

}
