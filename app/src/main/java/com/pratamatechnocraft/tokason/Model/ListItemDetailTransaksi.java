package com.pratamatechnocraft.tokason.Model;

public class ListItemDetailTransaksi {
    String namaBarang, qty, harga;

    public ListItemDetailTransaksi(String namaBarang, String qty, String harga) {
        this.namaBarang = namaBarang;
        this.qty = qty;
        this.harga = harga;
    }

    public String getNamaBarang() {
        return namaBarang;
    }

    public String getQty() {
        return qty;
    }

    public String getHarga() {
        return harga;
    }
}
