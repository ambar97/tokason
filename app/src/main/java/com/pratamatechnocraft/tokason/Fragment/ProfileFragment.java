package com.pratamatechnocraft.tokason.Fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.pratamatechnocraft.tokason.R;
import com.pratamatechnocraft.tokason.ZoomFotoProfile;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {
    SessionManager sessionManager;
    private TextView txtNamaUserProfile,txtNamaDepan,txtNamaBelakang,txtLevelUserProfile,textnama,textnotelp,textalamat;
    private Button btnEdit,btnUbahPass,buttonBatalEdit,buttonSimpanEdit,buttonBatalUbahPass,buttonSimpanUbahPass;
    private EditText inputNamaDepan, inputNamaBelakang, inputNoTelp,inputAlamat,inputPasswordLama,inputPasswordBaru,inputPasswordBaruLagi;
    private TextInputLayout inputLayoutNamaDepan,inputLayoutNamaBelakang, inputLayoutNoTelp,inputLayoutAlamat, inputLayoutPasswordLama,inputLayoutPasswordBaru,inputLayoutPasswordBaruLagi;
    private ProgressDialog progress;
    public static ImageView profile_image;
    SwipeRefreshLayout refreshProfile;
    AlertDialog dialog;
    AlertDialog dialog1;
    LayoutInflater inflater;
    View dialogView;

    HashMap<String, String> user=null;
    BaseUrlApiModel baseUrlApiModel = new BaseUrlApiModel();
    private String baseUrl=baseUrlApiModel.getBaseURL(),urlGambarProfile;
    private static final String API_URL_LOAD = "api/user?api=profile&kd_user=";
    private static final String API_URL_EDITDANUBAH = "api/user";
    NavigationView navigationView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.fragment_profile, container, false);
        navigationView = getActivity().findViewById( R.id.nav_view );

        txtNamaUserProfile = view.findViewById( R.id.txtNamaUserProfile );
        txtNamaDepan = view.findViewById( R.id.txtNamaDepan );
        txtNamaBelakang = view.findViewById( R.id.txtNamaBelakang );
        txtLevelUserProfile = view.findViewById( R.id.txtLevelUserProfile );
        textnama = view.findViewById( R.id.textnama );
        textalamat = view.findViewById( R.id.textalamat );
        btnEdit = view.findViewById( R.id.buttonEdit );
        btnUbahPass = view.findViewById( R.id.buttonUbahPassword );
        progress = new ProgressDialog(getContext());
        profile_image = view.findViewById(R.id.profile_image);

        textnotelp = view.findViewById( R.id.textnotelp );
        profile_image = view.findViewById( R.id.profile_image );

        refreshProfile = view.findViewById( R.id.refreshProfile );

        sessionManager = new SessionManager( getContext() );
        user = sessionManager.getUserDetail();
        loadProfile(user.get( sessionManager.KD_USER ));

        refreshProfile.setOnRefreshListener( new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                clear();
                loadProfile(user.get( sessionManager.KD_USER ));
            }
        } );

        btnEdit.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFormEditProfile();
            }
        } );

        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Profil User");
        navigationView.setCheckedItem(R.id.nav_profile);
    }


    private void DialogFormEditProfile() {
        dialog = new AlertDialog.Builder(getContext()).create();
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.activity_edituser, null);
        dialog.setView(dialogView);
        dialog.setCancelable(true);
        dialog.setTitle("Edit Profile");

        buttonSimpanEdit = dialogView.findViewById( R.id.buttonSimpanEdit );
        buttonBatalEdit = dialogView.findViewById( R.id.buttonBatalEdit );

        inputLayoutNamaDepan = (TextInputLayout) dialogView.findViewById(R.id.inputLayoutNamaDepan);
        inputLayoutNamaBelakang = (TextInputLayout) dialogView.findViewById(R.id.inputLayoutNamaBelakang);
        inputLayoutNoTelp = (TextInputLayout) dialogView.findViewById(R.id.inputLayoutNoTelp);
        inputLayoutAlamat = (TextInputLayout) dialogView.findViewById(R.id.inputLayoutAlamat);
        inputNamaDepan = (EditText) dialogView.findViewById(R.id.inputNamaDepan);
        inputNamaBelakang = (EditText) dialogView.findViewById(R.id.inputNamaBelakang);
        inputNoTelp = (EditText) dialogView.findViewById(R.id.inputNoTelp);
        inputAlamat = (EditText) dialogView.findViewById(R.id.inputAlamat);

        inputNamaDepan.addTextChangedListener( new MyTextWatcher( inputNamaDepan ) );
        inputNamaBelakang.addTextChangedListener( new MyTextWatcher( inputNamaBelakang ) );
        inputNoTelp.addTextChangedListener( new MyTextWatcher( inputNoTelp ) );
        inputAlamat.addTextChangedListener( new MyTextWatcher( inputAlamat ) );

        inputNamaDepan.setText( txtNamaDepan.getText().toString().trim() );
        inputNamaBelakang.setText( txtNamaBelakang.getText().toString().trim() );
        inputNoTelp.setText( textnotelp.getText().toString().trim() );
        inputAlamat.setText( textalamat.getText().toString().trim() );

        buttonSimpanEdit.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateNamaDepan() || !validateNamaBelakang() || !validateNoTelp() || !validateAlamat()) {
                    return;
                }else{
                    progress.setMessage("Mohon Ditunggu, Sedang diProses.....");
                    progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progress.setIndeterminate(false);
                    progress.setCanceledOnTouchOutside(false);
                    prosesEditProfile(
                            inputNamaDepan.getText().toString(),
                            inputNamaBelakang.getText().toString(),
                            inputNoTelp.getText().toString(),
                            inputAlamat.getText().toString()
                    );
                }
            }
        } );

        buttonBatalEdit.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        } );

        dialog.show();
    }

    private void DialogFormUbahPass(final String passwordLama) {
        dialog1 = new AlertDialog.Builder(getContext()).create();
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.activity_ubahpass, null);
        dialog1.setView(dialogView);
        dialog1.setCancelable(true);
        dialog1.setTitle("Ubah Password");

        inputLayoutPasswordBaru = (TextInputLayout) dialogView.findViewById(R.id.inputLayoutPasswordBaru);
        inputLayoutPasswordBaruLagi = (TextInputLayout) dialogView.findViewById(R.id.inputLayoutPasswordBaruLagi);
        inputLayoutPasswordLama = (TextInputLayout) dialogView.findViewById(R.id.inputLayoutPasswordLama);
        inputPasswordBaru = (EditText) dialogView.findViewById(R.id.inputPasswordBaru);
        inputPasswordBaruLagi = (EditText) dialogView.findViewById(R.id.inputPasswordBaruLagi);
        inputPasswordLama = (EditText) dialogView.findViewById(R.id.inputPasswordLama);

        inputPasswordLama.addTextChangedListener( new MyTextWatcher1( inputPasswordLama ) );
        inputPasswordBaruLagi.addTextChangedListener( new MyTextWatcher1( inputPasswordBaruLagi ) );
        inputPasswordBaru.addTextChangedListener( new MyTextWatcher1( inputPasswordBaru ) );

        buttonSimpanUbahPass = dialogView.findViewById( R.id.buttonSimpanUbahPass );
        buttonBatalUbahPass = dialogView.findViewById( R.id.buttonBatalUbahPass );

        buttonSimpanUbahPass.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validatePasswordLama() || !validatePasswordBaruLagi() || !validatePasswordBaru()) {
                    return;
                }else if(!inputPasswordLama.getText().toString().equals( passwordLama )){
                    inputLayoutPasswordLama.setError("Password Lama Tidak Sama");
                    requestFocus(inputPasswordLama);
                }else if(!inputPasswordBaru.getText().toString().equals( inputPasswordBaruLagi.getText().toString() )){
                    inputLayoutPasswordBaruLagi.setError("Konfirmasi Password Tidak Sama");
                    requestFocus(inputPasswordBaruLagi);
                }else{
                    progress.setMessage("Mohon Ditunggu, Sedang diProses.....");
                    progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progress.setIndeterminate(false);
                    progress.setCanceledOnTouchOutside(false);
                    prosesUbahPass(user.get( sessionManager.KD_USER ),inputPasswordBaruLagi.getText().toString());
                }
            }
        } );

        buttonBatalUbahPass.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog1.dismiss();
            }
        } );

        dialog1.show();
    }

    private void prosesUbahPass(final String kdUser , final String passBaru){
        progress.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, baseUrl+API_URL_EDITDANUBAH, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String kode = jsonObject.getString("kode");
                    if (kode.equals("1")) {
                        loadProfile( sessionManager.KD_USER );
                        Toast.makeText(getContext(), "Ubah Password Berhasil", Toast.LENGTH_SHORT).show();
                        progress.dismiss();
                        dialog1.dismiss();
                    }else{
                        Toast.makeText(getContext(), "Ubah Password Gagal, Coba Lagi!", Toast.LENGTH_SHORT).show();
                        progress.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Periksa koneksi & coba lagi", Toast.LENGTH_SHORT).show();
                    progress.dismiss();
                }
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(getContext(), "Periksa koneksi & coba lagi", Toast.LENGTH_SHORT).show();
                progress.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("kd_user", kdUser);
                params.put("password_baru", passBaru);
                params.put("api", "ubahpassword");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private void prosesEditProfile(final String namaDepan, final String namaBelakang, final String noTelp, final String alamat){
        progress.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, baseUrl+API_URL_EDITDANUBAH, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String kode = jsonObject.getString("kode");
                    if (kode.equals("1")) {
                        clear();
                        loadProfile(user.get( sessionManager.KD_USER ));
                        Toast.makeText(getContext(), "Edit Profile Berhasil", Toast.LENGTH_SHORT).show();
                        progress.dismiss();
                        dialog.dismiss();
                    }else{
                        Toast.makeText(getContext(), "Edit Profile Gagal, Coba Lagi!", Toast.LENGTH_SHORT).show();
                        progress.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Periksa koneksi & coba lagi", Toast.LENGTH_SHORT).show();
                    progress.dismiss();
                }
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(getContext(), "Periksa koneksi & coba lagi", Toast.LENGTH_SHORT).show();
                progress.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("kd_user", user.get( sessionManager.KD_USER ));
                params.put("nama_depan", namaDepan);
                params.put("no_telp", noTelp);
                params.put("nama_belakang", namaBelakang);
                params.put("alamat", alamat);
                params.put("api", "editprofile");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private void loadProfile(final String kdUser){
        String API_URL = "api/user?api=dataprofile&kd_user="+kdUser;
        refreshProfile.setRefreshing(true);
        StringRequest stringRequest = new StringRequest( Request.Method.GET, baseUrl+API_URL,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        Log.d("TAG", "onResponse: "+response);
                        final JSONObject userprofile = new JSONObject(response);
                        txtNamaUserProfile.setText(  userprofile.getString( "nama_depan" )+" "+userprofile.getString( "nama_belakang" ));
                        textnama.setText( userprofile.getString( "nama_depan" )+" "+userprofile.getString( "nama_belakang" ) );
                        txtNamaDepan.setText( userprofile.getString( "nama_depan" ));
                        txtNamaBelakang.setText( userprofile.getString( "nama_belakang" ));
                        if(Integer.parseInt( userprofile.getString( "level_user" ) )==0){
                            txtLevelUserProfile.setText("Owner");
                        }else if(Integer.parseInt( userprofile.getString( "level_user" ) )==1){
                            txtLevelUserProfile.setText("Kasir");
                        }
                        textnotelp.setText( userprofile.getString( "telephone" ) );
                        textalamat.setText(  userprofile.getString( "alamat" )  );
                        urlGambarProfile = baseUrl+String.valueOf( userprofile.getString( "foto" )  );
                        profile_image.setOnClickListener( new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent i = new Intent(getContext(), ZoomFotoProfile.class);
                                i.putExtra("fotoProfil", urlGambarProfile);
                                try {
                                    i.putExtra("kdUser", userprofile.getString( "kd_user" ));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                startActivity(i);
                            }
                        } );
                        btnUbahPass.setOnClickListener( new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                try {
                                    DialogFormUbahPass(String.valueOf( userprofile.getString( "password" )  ));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } );
                        Glide.with(getContext())
                                // LOAD URL DARI INTERNET
                                .load(urlGambarProfile)
                                // LOAD GAMBAR AWAL SEBELUM GAMBAR UTAMA MUNCUL, BISA DARI LOKAL DAN INTERNET
                                .into(profile_image);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Periksa koneksi & coba lagi", Toast.LENGTH_SHORT).show();
                    }
                    refreshProfile.setRefreshing( false );
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getContext(), "Periksa koneksi & coba lagi1", Toast.LENGTH_SHORT).show();
                    Log.d("errort", "onErrorResponse: "+error);;
                    refreshProfile.setRefreshing( false );
                }
            }
        ) ;

        RequestQueue requestQueue = Volley.newRequestQueue( getContext() );
        requestQueue.add( stringRequest );
    }

    private void clear(){
        txtNamaUserProfile.setText( "" );
        textnama.setText( "" );
        textnotelp.setText( "" );
        textalamat.setText( "" );
    }

    private boolean validateNamaDepan() {
        if (inputNamaDepan.getText().toString().trim().isEmpty()) {
            inputLayoutNamaDepan.setError("Masukkan Nama Depan");
            requestFocus( inputNamaDepan );
            return false;
        } else {
            inputLayoutNamaDepan.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateNamaBelakang() {
        if (inputNamaBelakang.getText().toString().trim().isEmpty()) {
            inputLayoutNamaBelakang.setError("Masukkan Nama Belakang");
            requestFocus( inputNamaBelakang );
            return false;
        } else {
            inputLayoutNamaBelakang.setErrorEnabled(false);
        }

        return true;
    }


    private boolean validateNoTelp() {
        if (inputNoTelp.getText().toString().trim().isEmpty()) {
            inputLayoutNoTelp.setError("Masukkan Nomor Telepon");
            requestFocus(inputNoTelp);
            return false;
        } else {
            inputLayoutNoTelp.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateAlamat() {
        if (inputAlamat.getText().toString().trim().isEmpty()) {
            inputLayoutAlamat.setError("Masukkan Alamat");
            requestFocus(inputAlamat);
            return false;
        } else {
            inputLayoutAlamat.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePasswordLama() {
        if (inputPasswordLama.getText().toString().trim().isEmpty()) {
            inputLayoutPasswordLama.setError("Masukkan Password Lama");
            requestFocus(inputPasswordLama);
            return false;
        } else {
            inputLayoutPasswordLama.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePasswordBaru() {
        if (inputPasswordBaru.getText().toString().trim().isEmpty()) {
            inputLayoutPasswordBaru.setError("Masukkan Password Lama");
            requestFocus(inputPasswordBaru);
            return false;
        } else {
            inputLayoutPasswordBaru.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePasswordBaruLagi() {
        if (inputPasswordBaruLagi.getText().toString().trim().isEmpty()) {
            inputLayoutPasswordBaruLagi.setError("Masukkan Password Lama");
            requestFocus(inputPasswordBaruLagi);
            return false;
        } else {
            inputLayoutPasswordBaruLagi.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

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
                case R.id.inputNamaDepan:
                    validateNamaDepan();
                    break;
                case R.id.inputNamaBelakang:
                    validateNamaBelakang();
                    break;
                case R.id.inputNoTelp:
                    validateNoTelp();
                    break;
                case R.id.inputAlamat:
                    validateAlamat();
                    break;
            }
        }
    }

    private class MyTextWatcher1 implements TextWatcher {

        private View view;

        private MyTextWatcher1(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.inputPasswordLama:
                    validatePasswordLama();
                    break;
                case R.id.inputPasswordBaru:
                    validatePasswordBaru();
                    break;
                case R.id.inputPasswordBaruLagi:
                    validatePasswordBaruLagi();
                    break;
            }
        }
    }

}
