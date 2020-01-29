package com.pratamatechnocraft.tokason.Adapter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelperSqlLiteKeranjang extends SQLiteOpenHelper {
    public static final String TABLE_NAME1 = "dat_pelanggan_pilih";
    public static final String KD_PILIH = "_kd_pilih";
    public static final String KD_PELANGGAN = "kd_pelanggan";
    public static final String NAMA_PELANGGAN = "nama_pelanggan";
    public static final String NOTELP_PELANGGAN = "notelp_pelanggan";
    public static final String ALAMAT_PELANGGAN = "alamat_pelanggan";
    public static final String TANGGALTERDAFTAR_PELANGGAN = "tanggalterdaftar_pelanggan";
    public static final String TABLE_NAME = "data_keranjang";
    public static final String KD_KERANJANG = "_kd_keranjang";
    public static final String KD_BARANG = "kd_barang";
    public static final String NAMA_BARANG = "nama_barang";
    public static final String HARGA_BARANG = "harga_barang";
    public static final String URL_GAMBAR_BARANG = "url_gambar_barang";
    public static final String QTY = "qty";
    public static final String STOK = "stok";
    public static final String CATATAN = "catatan";
    private static final String db_name ="penjualan.db";
    private static final int db_version=4;

    // Perintah SQL untuk membuat tabel database baru
    private static final String db_create = "create table "
            + TABLE_NAME + "("
            + KD_KERANJANG +" integer primary key autoincrement, "
            + KD_BARANG+ " varchar(100) not null, "
            + NAMA_BARANG+ " varchar(50) not null, "
            + HARGA_BARANG+ " varchar(100) not null, "
            + URL_GAMBAR_BARANG+ " varchar(255) not null, "
            + QTY+ " varchar(100) not null, "
            + STOK+ " varchar(100) not null, "
            +CATATAN+ " text not null);";

    private static final String db_create1 = "create table "
            + TABLE_NAME1 + "("
            + KD_PILIH +" integer primary key autoincrement, "
            + KD_PELANGGAN + " varchar(100) not null,"
            + NAMA_PELANGGAN + " varchar(100) not null,"
            + NOTELP_PELANGGAN + " varchar(100) not null,"
            + ALAMAT_PELANGGAN + " text not null,"
            + TANGGALTERDAFTAR_PELANGGAN + " varchar(100) not null);";

    public DBHelperSqlLiteKeranjang(Context context) {
        super(context, db_name, null, db_version);
        // Auto generated
    }

    //mengeksekusi perintah SQL di atas untuk membuat tabel database baru
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(db_create);
        db.execSQL(db_create1);
    }

    // dijalankan apabila ingin mengupgrade database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DBHelperSqlLiteKeranjang.class.getName(),"Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME1);
        onCreate(db);

    }
}
