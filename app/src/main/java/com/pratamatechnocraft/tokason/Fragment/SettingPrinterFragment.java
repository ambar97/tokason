package com.pratamatechnocraft.tokason.Fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.pratamatechnocraft.tokason.BluetoothPrinter.ListDevice;
import com.pratamatechnocraft.tokason.R;
import com.pratamatechnocraft.tokason.Service.SessionManager;

import java.util.HashMap;

public class SettingPrinterFragment extends Fragment {
    private TextView txtNamaBluetooth, txtAddressBluetooth;
    private Button buttonScanBluetooth, buttonSimpanSettingPrinter, buttonResetSettingPrinter;
    private RadioButton radioButton58, radioButton80, radioButton104;
    private RadioGroup radioGroupUkuranKertas;
    SessionManager sessionManager;
    HashMap<String, String> printer;
    private Boolean statusScan=false;
    NavigationView navigationView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.fragment_setting_printer, container, false);
            navigationView = getActivity().findViewById( R.id.nav_view );

            txtNamaBluetooth = view.findViewById(R.id.txtNamaBluetooth);
            txtAddressBluetooth = view.findViewById(R.id.txtAddressBluetooth);
            buttonScanBluetooth = view.findViewById(R.id.buttonScanBluetooth);
            buttonSimpanSettingPrinter = view.findViewById(R.id.buttonSimpanSettingPrinter);
            buttonResetSettingPrinter = view.findViewById(R.id.buttonResetSettingPrinter);
            radioButton58 = view.findViewById(R.id.radioButton58);
            radioButton80 = view.findViewById(R.id.radioButton80);
            radioButton104 = view.findViewById(R.id.radioButton104);
            radioGroupUkuranKertas = view.findViewById(R.id.radioGroupUkuranKertas);

            sessionManager = new SessionManager( getContext() );
            printer=sessionManager.getPrinter();

            if (sessionManager.isPrinter()==true){
                loadSetting();
            }else{
                loadSettingDefault();
            }

            buttonScanBluetooth.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int selectedId = radioGroupUkuranKertas.getCheckedRadioButtonId();
                    String ukuranKertas = null;
                    if (selectedId == R.id.radioButton58){
                        ukuranKertas="58";
                    }else if (selectedId == R.id.radioButton80){
                        ukuranKertas="80";
                    }else{
                        ukuranKertas="104";
                    }
                    Intent i = new Intent(getActivity(), ListDevice.class);
                    i.putExtra("ukuranKertas",ukuranKertas);
                    getActivity().startActivity(i);
                    statusScan=true;
                }
            });
            buttonSimpanSettingPrinter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sessionManager.clearPrefPrinter();
                    int selectedId = radioGroupUkuranKertas.getCheckedRadioButtonId();
                    String ukuranKertas = null;
                    if (selectedId == R.id.radioButton58){
                        ukuranKertas="58";
                    }else if (selectedId == R.id.radioButton80){
                        ukuranKertas="80";
                    }else{
                        ukuranKertas="104";
                    }
                    sessionManager.createSessionPrinter(txtNamaBluetooth.getText().toString(),txtAddressBluetooth.getText().toString(),ukuranKertas);
                    loadSetting();
                }
            });

            buttonResetSettingPrinter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sessionManager.clearPrefPrinter();
                    loadSettingDefault();
                }
            });
        return view;
    }

    private void loadSetting() {
        printer=sessionManager.getPrinter();
        txtNamaBluetooth.setText(printer.get(sessionManager.NAMA_BLUETOOTH));
        txtAddressBluetooth.setText(printer.get(sessionManager.ADDRESS_BLUETOOTH));
        if (Integer.parseInt(printer.get(sessionManager.UKURAN_KERTAS))==58){
            radioButton58.setChecked(true);
        }else if (Integer.parseInt(printer.get(sessionManager.UKURAN_KERTAS))==80){
            radioButton80.setChecked(true);
        }else{
            radioButton104.setChecked(true);
        }
    }

    private void loadSettingDefault() {
        txtNamaBluetooth.setText("");
        txtAddressBluetooth.setText("");
        radioButton58.setChecked(true);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Setting Printer");
        navigationView.setCheckedItem(R.id.setting_printer);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (statusScan == true) {
            loadSetting();
        }
        statusScan = false;
    }
}
