package com.pratamatechnocraft.tokason;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class LupaSandiActivity extends AppCompatActivity {
    EditText editTextUsernameLupa;
    Button btnLanjutLupaSandi;
    String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lupa_sandi);

        Toolbar toolbar = findViewById(R.id.toolbar_lupasandi);
        setSupportActionBar(toolbar);
        this.setTitle("Lupa kata sandi ?");
        toolbar.setSubtitleTextColor( ContextCompat.getColor(this, R.color.colorIcons) );
        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back_black_24dp);
        upArrow.setColorFilter(ContextCompat.getColor(this, R.color.colorIcons), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editTextUsernameLupa = findViewById(R.id.editTextUsernameLupa);
        btnLanjutLupaSandi = findViewById(R.id.btnLanjutLupaSandi);

        btnLanjutLupaSandi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumber = editTextUsernameLupa.getText().toString().trim();
                if (phoneNumber.isEmpty()){
                    editTextUsernameLupa.setError("Tidak boleh kosong !!");
                }else{
                    sendVerificationCode(phoneNumber);
                }
            }
        });
    }

    private void sendVerificationCode(String phoneNumber){
        if (phoneNumber.substring(0,1).equals("0")){
            phoneNumber.replace("0","+62");
        }else if(!phoneNumber.substring(0,1).equals("0") || !phoneNumber.substring(0,3).equals("+62")){
            phoneNumber = "+62"+phoneNumber;
        }

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                120,
                TimeUnit.SECONDS,
                this,
                mCall
        );
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCall = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            Log.d("LupaSandiActivity", ""+ phoneAuthCredential);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Log.w("LupaSandiActivity" , "Tidak Berhasil: "+ e);

        }

        @Override
        public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
            Log.d("LupaSandiActivity" , "Verification id : " + verificationId);
            Intent intent = new Intent(LupaSandiActivity.this , VerifikasiActivity.class);
            intent.putExtra("verificationId" , verificationId);
            intent.putExtra("username" , phoneNumber);
            intent.putExtra("from" , "lupa");
            startActivity(intent);
            finish();
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
