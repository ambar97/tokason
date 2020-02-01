package com.pratamatechnocraft.tokason.Service;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.pratamatechnocraft.tokason.LoginActivity;
import com.pratamatechnocraft.tokason.MainActivity;

import java.util.HashMap;

public class SessionManager {
    private SharedPreferences sharedPreferences;
    private SharedPreferences sharedPreferencesPrinter;
    private SharedPreferences.Editor editor;
    private SharedPreferences.Editor editorPrinter;
    public Context context;

    private static final String PREF_NAME = "LOGIN";
    private static final String PREF_PRINTER_NAME = "PRINTER";
    private static final String LOGIN = "IS_LOGIN";
    public static final String KD_USER = "KD_USER";
    public static final String KD_OUTLET = "KD_OUTLET";
    public static final String LEVEL_USER = "LEVEL_USER";
    public static final String NAMA_BLUETOOTH = "NAMA_BLUETOOTH";
    public static final String ADDRESS_BLUETOOTH = "ADDRESS_BLUETOOTH";
    public static final String UKURAN_KERTAS = "UKURAN_KERTAS";
    private static final String PRINTER = "IS_PRINTER";


    public SessionManager(Context context) {
        this.context = context;
        int PRIVATE_MODE = 0;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();
        sharedPreferencesPrinter = context.getSharedPreferences(PREF_PRINTER_NAME, PRIVATE_MODE);
        editorPrinter = sharedPreferencesPrinter.edit();
    }

    public void createSessionPrinter(String namaBluetooth, String addressBluetooth, String ukuranKertas) {
        editorPrinter.putBoolean(PRINTER, true);
        editorPrinter.putString(NAMA_BLUETOOTH, namaBluetooth);
        editorPrinter.putString(ADDRESS_BLUETOOTH, addressBluetooth);
        editorPrinter.putString(UKURAN_KERTAS, ukuranKertas);
        editorPrinter.apply();
    }

    public void clearPrefPrinter() {
        editorPrinter.clear();
        editorPrinter.commit();
    }

    public HashMap<String, String> getPrinter() {
        HashMap<String, String> printer = new HashMap<>();
        printer.put(NAMA_BLUETOOTH, sharedPreferencesPrinter.getString(NAMA_BLUETOOTH, null));
        printer.put(ADDRESS_BLUETOOTH, sharedPreferencesPrinter.getString(ADDRESS_BLUETOOTH, null));
        printer.put(UKURAN_KERTAS, sharedPreferencesPrinter.getString(UKURAN_KERTAS, null));
        return printer;
    }

    public boolean isPrinter() {
        return sharedPreferencesPrinter.getBoolean(PRINTER, false);
    }

    public void createSession(String kd_user, String level_user, String kd_outlet) {
        editor.putBoolean(LOGIN, true);
        editor.putString(KD_USER, kd_user);
        editor.putString(KD_OUTLET, kd_outlet);
        editor.putString(LEVEL_USER, level_user);
        editor.apply();
    }

    public boolean isLoggin() {
        return sharedPreferences.getBoolean(LOGIN, false);
    }

    public void checkLogin() {
        if (!this.isLoggin()) {
            Intent intent = new Intent(context, LoginActivity.class);
            context.startActivity(intent);
            ((MainActivity) context).finish();
        }
    }

    public HashMap<String, String> getUserDetail() {
        HashMap<String, String> user = new HashMap<>();
        user.put(KD_OUTLET, sharedPreferences.getString(KD_OUTLET, null));
        user.put(KD_USER, sharedPreferences.getString(KD_USER, null));
        user.put(LEVEL_USER, sharedPreferences.getString(LEVEL_USER, null));

        return user;
    }

    public void logout() {
        editor.clear();
        editor.commit();
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
        ((MainActivity) context).finish();

    }
}
