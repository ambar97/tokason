package com.pratamatechnocraft.tokason.Model;

public class ListItemDataPelanggan {
    private String kdPelanggan, namaPelanggan, noTelp, tgldiTambahkan, alamat;

    public ListItemDataPelanggan(String kdPelanggan, String namaPelanggan, String noTelp, String alamat, String tgldiTambahkan) {
        this.kdPelanggan = kdPelanggan;
        this.namaPelanggan = namaPelanggan;
        this.noTelp = noTelp;
        this.tgldiTambahkan = tgldiTambahkan;
        this.alamat = alamat;
    }

    public String getKdPelanggan() {
        return kdPelanggan;
    }

    public String getNamaPelanggan() {
        return namaPelanggan;
    }

    public String getNoTelp() {
        return noTelp;
    }

    public String getTgldiTambahkan() {
        return tgldiTambahkan;
    }

    public String getAlamat() {
        return alamat;
    }
}
