package com.pratamatechnocraft.tokason.Adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.pratamatechnocraft.tokason.Model.ModelKeranjang;
import com.pratamatechnocraft.tokason.Model.ModelPelangganPilih;

import org.json.JSONArray;

import java.util.ArrayList;

public class DBDataSourceKeranjang {
    private SQLiteDatabase database;
    private DBHelperSqlLiteKeranjang dbHelperSqlLiteKeranjang;
    private String[] allColumns1 = {
            dbHelperSqlLiteKeranjang.KD_PILIH,
            dbHelperSqlLiteKeranjang.KD_PELANGGAN,
            dbHelperSqlLiteKeranjang.NAMA_PELANGGAN,
            dbHelperSqlLiteKeranjang.NOTELP_PELANGGAN,
            dbHelperSqlLiteKeranjang.ALAMAT_PELANGGAN,
            dbHelperSqlLiteKeranjang.TANGGALTERDAFTAR_PELANGGAN,
    };
    private String[] allColumns = {
            dbHelperSqlLiteKeranjang.KD_KERANJANG,
            dbHelperSqlLiteKeranjang.KD_BARANG,
            dbHelperSqlLiteKeranjang.NAMA_BARANG,
            dbHelperSqlLiteKeranjang.HARGA_BARANG,
            dbHelperSqlLiteKeranjang.URL_GAMBAR_BARANG,
            dbHelperSqlLiteKeranjang.QTY,
            dbHelperSqlLiteKeranjang.STOK,
            dbHelperSqlLiteKeranjang.CATATAN
    };

    public DBDataSourceKeranjang(Context context){ dbHelperSqlLiteKeranjang = new DBHelperSqlLiteKeranjang(context); }

    public void open() throws SQLException { database = dbHelperSqlLiteKeranjang.getWritableDatabase(); }

    public void close() {
        dbHelperSqlLiteKeranjang.close();
    }

    public ModelKeranjang createModelKeranjang(String kd_barang,String nama_barang,String harga_barang,String url_gambar_barang, String qty, String stok, String catatan) {

        ContentValues values = new ContentValues();
        values.put(dbHelperSqlLiteKeranjang.KD_BARANG, kd_barang);
        values.put(dbHelperSqlLiteKeranjang.NAMA_BARANG, nama_barang);
        values.put(dbHelperSqlLiteKeranjang.HARGA_BARANG, harga_barang);
        values.put(dbHelperSqlLiteKeranjang.URL_GAMBAR_BARANG, url_gambar_barang);
        values.put(dbHelperSqlLiteKeranjang.QTY, qty);
        values.put(dbHelperSqlLiteKeranjang.STOK, stok);
        values.put(dbHelperSqlLiteKeranjang.CATATAN, catatan);

        long insertId = database.insert(dbHelperSqlLiteKeranjang.TABLE_NAME, null,
                values);

        Cursor cursor = database.query(dbHelperSqlLiteKeranjang.TABLE_NAME,
                allColumns, dbHelperSqlLiteKeranjang.KD_KERANJANG + " = " + insertId, null,
                null, null, null);

        cursor.moveToFirst();

        ModelKeranjang newKeranjang = cursorToKeranjang(cursor);

        cursor.close();

        return newKeranjang;
    }

    public ModelPelangganPilih createModelPelangganPilih(String kd_pelanggan, String nama_pelanggan, String notelp_pelanggan, String alamat_pelanggan, String tanggalterdaftar_pelanggan) {

        ContentValues values = new ContentValues();
        values.put(dbHelperSqlLiteKeranjang.KD_PELANGGAN, kd_pelanggan);
        values.put(dbHelperSqlLiteKeranjang.NAMA_PELANGGAN, nama_pelanggan);
        values.put(dbHelperSqlLiteKeranjang.NOTELP_PELANGGAN, notelp_pelanggan);
        values.put(dbHelperSqlLiteKeranjang.ALAMAT_PELANGGAN, alamat_pelanggan);
        values.put(dbHelperSqlLiteKeranjang.TANGGALTERDAFTAR_PELANGGAN, tanggalterdaftar_pelanggan);

        long insertId = database.insert(dbHelperSqlLiteKeranjang.TABLE_NAME1, null,
                values);

        Cursor cursor = database.query(dbHelperSqlLiteKeranjang.TABLE_NAME1,
                allColumns1, dbHelperSqlLiteKeranjang.KD_PILIH + " = " + insertId, null,
                null, null, null);

        cursor.moveToFirst();

        ModelPelangganPilih newModelPelangganPilih = cursorToModelPelangganPilih(cursor);

        cursor.close();

        return newModelPelangganPilih;
    }

    private ModelKeranjang cursorToKeranjang(Cursor cursor)
    {
        ModelKeranjang modelKeranjang = new ModelKeranjang();
        modelKeranjang.setKdKeranjang(cursor.getLong(0));
        modelKeranjang.setKdBarang(cursor.getString(1));
        modelKeranjang.setNamaBrang(cursor.getString(2));
        modelKeranjang.setHargaBarang(cursor.getInt(3));
        modelKeranjang.setUrlGambarBarang(cursor.getString(4));
        modelKeranjang.setQty(cursor.getInt(5));
        modelKeranjang.setStok(cursor.getInt(6));
        modelKeranjang.setCatatan(cursor.getString(7));

        return modelKeranjang;
    }

    private ModelPelangganPilih cursorToModelPelangganPilih(Cursor cursor)
    {
        ModelPelangganPilih modelPelangganPilih = new ModelPelangganPilih();
        modelPelangganPilih.setKdPilih(cursor.getLong(0));
        modelPelangganPilih.setKdPelanggan(cursor.getString(1));
        modelPelangganPilih.setNamaPelanggan(cursor.getString(2));
        modelPelangganPilih.setNoTelpPelanggan(cursor.getString(3));
        modelPelangganPilih.setAlamatPelanggan(cursor.getString(4));
        modelPelangganPilih.setTanggalTerdaftarPelanggan(cursor.getString(5));

        return modelPelangganPilih;
    }

    public ArrayList<ModelKeranjang> getAllKeranjang() {
        ArrayList<ModelKeranjang> modelKeranjangs = new ArrayList<ModelKeranjang>();

        Cursor cursor = database.query(DBHelperSqlLiteKeranjang.TABLE_NAME,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ModelKeranjang modelKeranjang = cursorToKeranjang(cursor);
            modelKeranjangs.add(modelKeranjang);
            cursor.moveToNext();
        }
        cursor.close();
        return modelKeranjangs;
    }

    public ArrayList<ModelPelangganPilih> getAllModelPelangganPilih() {
        ArrayList<ModelPelangganPilih> modelPelangganPilihs = new ArrayList<ModelPelangganPilih>();

        Cursor cursor = database.query(DBHelperSqlLiteKeranjang.TABLE_NAME1,
                allColumns1, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ModelPelangganPilih modelPelangganPilih = cursorToModelPelangganPilih(cursor);
            modelPelangganPilihs.add(modelPelangganPilih);
            cursor.moveToNext();
        }
        cursor.close();
        return modelPelangganPilihs;
    }

    public ModelKeranjang getKeranjang(String id){
        ModelKeranjang modelKeranjang = new ModelKeranjang();

        Cursor cursor = database.query(DBHelperSqlLiteKeranjang.TABLE_NAME, allColumns, "kd_barang ="+id, null, null, null, null);
        //ambil data yang pertama
        cursor.moveToFirst();
        //masukkan data cursor ke objek barang
        modelKeranjang = cursorToKeranjang(cursor);
        //tutup sambungan
        cursor.close();
        //return barang
        return modelKeranjang;
    }

    public Boolean cekKeranjang(String kdBarang)
    {
        //select query
        Cursor cursor = database.rawQuery("select count(*) from data_keranjang where kd_barang='" + kdBarang + "'", null);
        //ambil data yang pertama
        cursor.moveToFirst();
        int count= cursor.getInt(0);
        //tutup sambungan
        cursor.close();
        if (count>0){
            return true;
        }else{
            return false;
        }
    }

    public boolean totalKeranjang()
    {
        //select query
        Cursor cursor = database.rawQuery("select * from data_keranjang", null);
        //ambil data yang pertama
        cursor.moveToFirst();
        int count= cursor.getCount();
        //tutup sambungan
        cursor.close();
        if (count>0){
            return true;
        }else{
            return false;
        }
    }

    public boolean totalPelangganPilih()
    {
        //select query
        Cursor cursor = database.rawQuery("select * from dat_pelanggan_pilih", null);
        //ambil data yang pertama
        cursor.moveToFirst();
        int count= cursor.getCount();
        //tutup sambungan
        cursor.close();
        if (count>0){
            return true;
        }else{
            return false;
        }
    }

    public void updateBarang(String kdBarang, int qty, String catatan)
    {
        //ambil id barang
        String strFilter = "kd_barang=" + kdBarang;
        //memasukkan ke content values
        ContentValues args = new ContentValues();
        //masukkan data sesuai dengan kolom pada database
        args.put(DBHelperSqlLiteKeranjang.QTY, qty+1);
        args.put(DBHelperSqlLiteKeranjang.CATATAN, catatan);
        //update query
        database.update(DBHelperSqlLiteKeranjang.TABLE_NAME, args, strFilter, null);
    }

    public void updateBarangTypeDua(String kdBarang, int qty, String catatan)
    {
        //ambil id barang
        String strFilter = "kd_barang=" + kdBarang;
        //memasukkan ke content values
        ContentValues args = new ContentValues();
        //masukkan data sesuai dengan kolom pada database
        args.put(DBHelperSqlLiteKeranjang.QTY, qty);
        args.put(DBHelperSqlLiteKeranjang.CATATAN, catatan);
        //update query
        database.update(DBHelperSqlLiteKeranjang.TABLE_NAME, args, strFilter, null);
    }

    // delete barang sesuai ID
    public void deleteBarang(long id)
    {
        String strFilter = "_kd_keranjang=" + id;
        database.delete(DBHelperSqlLiteKeranjang.TABLE_NAME, strFilter, null);
    }

    public void deleteAll()
    {
        database.delete(DBHelperSqlLiteKeranjang.TABLE_NAME, null, null);
    }

    public void updatePelangganPilih(String kdPelanggan, String nama_pelanggan, String notelp_pelanggan, String alamat_pelanggan, String tanggalterdaftar_pelanggan) {

        ContentValues values = new ContentValues();
        values.put(dbHelperSqlLiteKeranjang.KD_PELANGGAN, kdPelanggan);
        values.put(dbHelperSqlLiteKeranjang.NAMA_PELANGGAN, nama_pelanggan);
        values.put(dbHelperSqlLiteKeranjang.NOTELP_PELANGGAN, notelp_pelanggan);
        values.put(dbHelperSqlLiteKeranjang.ALAMAT_PELANGGAN, alamat_pelanggan);
        values.put(dbHelperSqlLiteKeranjang.TANGGALTERDAFTAR_PELANGGAN, tanggalterdaftar_pelanggan);
        //update query
        database.update(DBHelperSqlLiteKeranjang.TABLE_NAME1, values, null, null);
    }

    public void deletePelangganPilihAll()
    {
        database.delete(DBHelperSqlLiteKeranjang.TABLE_NAME1, null, null);
    }

    public JSONArray getArrayKdBarangKeranjang() {
        String[] kd_barangKeranjang = {dbHelperSqlLiteKeranjang.KD_BARANG};
        Cursor cursor = database.query(DBHelperSqlLiteKeranjang.TABLE_NAME, kd_barangKeranjang, null, null, null, null, null);
        JSONArray resultSet = new JSONArray(  );
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            int totalColumn = cursor.getColumnCount();
            for (int i = 0; i < totalColumn; i++) {
                resultSet.put( cursor.getString(i) );
                cursor.moveToNext();
            }
        }

        cursor.close();
        Log.d("TAG_NAME", resultSet.toString());
        return resultSet;
    }

    public JSONArray getArrayQtyKeranjang() {
        String[] qtyKeranjang = {dbHelperSqlLiteKeranjang.QTY};
        Cursor cursor = database.query(DBHelperSqlLiteKeranjang.TABLE_NAME, qtyKeranjang, null, null, null, null, null);
        JSONArray resultSet = new JSONArray(  );
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            int totalColumn = cursor.getColumnCount();
            for (int i = 0; i < totalColumn; i++) {
                resultSet.put( cursor.getString(i) );
                cursor.moveToNext();
            }
        }

        cursor.close();
        Log.d("TAG_NAME", resultSet.toString());
        return resultSet;
    }

    public JSONArray getArrayCatatanKeranjang() {
        String[] catatanKeranjang = {dbHelperSqlLiteKeranjang.CATATAN};
        Cursor cursor = database.query(DBHelperSqlLiteKeranjang.TABLE_NAME, catatanKeranjang, null, null, null, null, null);
        JSONArray resultSet = new JSONArray(  );
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            int totalColumn = cursor.getColumnCount();
            for (int i = 0; i < totalColumn; i++) {
                resultSet.put( cursor.getString(i) );
                cursor.moveToNext();
            }
        }

        cursor.close();
        Log.d("TAG_NAME", resultSet.toString());
        return resultSet;
    }
}
