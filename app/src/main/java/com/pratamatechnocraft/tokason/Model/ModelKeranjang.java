package com.pratamatechnocraft.tokason.Model;

public class ModelKeranjang {
    long kdKeranjang;
    String kdBarang;
    String namaBrang;
    int stok;
    int qty;
    int HargaBarang;
    String urlGambarBarang;
    String catatan;

    public String getCatatan() {
        return catatan;
    }

    public void setCatatan(String catatan) {
        this.catatan = catatan;
    }

    public int getStok() {
        return stok;
    }

    public void setStok(int stok) {
        this.stok = stok;
    }

    public long getKdKeranjang() {
        return kdKeranjang;
    }

    public String getKdBarang() {
        return kdBarang;
    }

    public int getQty() {
        return qty;
    }

    public String getNamaBrang() {
        return namaBrang;
    }

    public int getHargaBarang() {
        return HargaBarang;
    }

    public String getUrlGambarBarang() {
        return urlGambarBarang;
    }

    public void setKdKeranjang(long kdKeranjang) {
        this.kdKeranjang = kdKeranjang;
    }

    public void setKdBarang(String kdBarang) {
        this.kdBarang = kdBarang;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public void setNamaBrang(String namaBrang) {
        this.namaBrang = namaBrang;
    }

    public void setHargaBarang(int hargaBarang) {
        HargaBarang = hargaBarang;
    }

    public void setUrlGambarBarang(String urlGambarBarang) {
        this.urlGambarBarang = urlGambarBarang;
    }
}
