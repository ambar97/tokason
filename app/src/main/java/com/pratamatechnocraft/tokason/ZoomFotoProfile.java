package com.pratamatechnocraft.tokason;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.pratamatechnocraft.tokason.Model.BaseUrlApiModel;
import com.pratamatechnocraft.tokason.Service.SessionManager;
import com.pratamatechnocraft.tokason.Fragment.ProfileFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ZoomFotoProfile extends AppCompatActivity {
    public static ImageView fotoUser;
    Intent i;
    BottomSheetDialog bottomSheetDialog;
    private Button galeri,kamera;
    SessionManager sessionManager;
    HashMap<String, String> user=null;
    BaseUrlApiModel baseUrlApiModel = new BaseUrlApiModel();
    private String baseUrl=baseUrlApiModel.getBaseURL();
    private static final String API_URL_EDITDANUBAH = "api/user";
    private Bitmap bitmap;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Bitmap mImageBitmap;
    private String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_zoom_foto_profile );
        fotoUser = findViewById( R.id.fotoUser );

        sessionManager = new SessionManager(this);
        user = sessionManager.getUserDetail();

        Toolbar ToolBarAtas2 = (Toolbar)findViewById(R.id.toolbar_zoomfoto);
        setSupportActionBar(ToolBarAtas2);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle( "Foto Profil" );

        i = getIntent();
        Glide.with(this)
            // LOAD URL DARI INTERNET
            .load(i.getStringExtra( "fotoProfil" ))
            // LOAD GAMBAR AWAL SEBELUM GAMBAR UTAMA MUNCUL, BISA DARI LOKAL DAN INTERNET
            .into(fotoUser);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_zoom_foto,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.editFotoProfil:
                View view = getLayoutInflater().inflate(R.layout.fragment_bottom_sheet_dialog_edit_foto, null);
                bottomSheetDialog = new BottomSheetDialog(this);
                bottomSheetDialog.setContentView(view);

                galeri = view.findViewById( R.id.galeri );
                kamera = view.findViewById( R.id.kamera );
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
                return true;
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return false;
    }

    private void pilihFoto(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent,"Pilih Foto"),1);
    }

    private void takePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult( requestCode, resultCode, data );
        if (requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData() !=null){
            Uri filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap( getContentResolver(),filePath );
                ZoomFotoProfile.fotoUser.setImageBitmap( bitmap );
                MainActivity.fotoUser.setImageBitmap( bitmap );
                ProfileFragment.profile_image.setImageBitmap( bitmap );

                uploadFoto(i.getStringExtra( "kdUser" ), getStringImage( bitmap ));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            mImageBitmap = (Bitmap) extras.get("data");
            ZoomFotoProfile.fotoUser.setImageBitmap( mImageBitmap );
            MainActivity.fotoUser.setImageBitmap( mImageBitmap );
            ProfileFragment.profile_image.setImageBitmap( mImageBitmap );

            uploadFoto(i.getStringExtra( "kdUser" ), getStringImage( mImageBitmap ));
        }
    }

    private String getStringImage(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(  );
        bitmap.compress( Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream );
        byte[] imageByteArray =  byteArrayOutputStream.toByteArray();
        String encodedImage = Base64.encodeToString( imageByteArray, Base64.DEFAULT );

        return encodedImage;
    }

    private void uploadFoto(final String kdUser, final String picture) {
        final ProgressDialog progressDialog = new ProgressDialog( this );
        progressDialog.setMessage( "Uploading......" );
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest( Request.Method.POST, baseUrl+API_URL_EDITDANUBAH, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String kode = jsonObject.getString("kode");
                    if (kode.equals("1")) {
                        Toast.makeText(ZoomFotoProfile.this, "Ubah Foto Profil Berhasil!", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }else{
                        Toast.makeText(ZoomFotoProfile.this, "Ubah Foto Profil Gagal!", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ZoomFotoProfile.this, "Ubah Foto Profil Gagal!", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                progressDialog.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("kd_user", kdUser);
                params.put("foto", picture);
                params.put("api", "ubahfoto");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
