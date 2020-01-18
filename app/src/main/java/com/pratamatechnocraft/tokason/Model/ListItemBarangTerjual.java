package com.pratamatechnocraft.tokason.Model;

public class ListItemBarangTerjual {
    private String namaBarang,hargaJual,jmlTerjual,subTotalTerjual;

    public ListItemBarangTerjual(String namaBarang, String hargaJual, String jmlTerjual, String subTotalTerjual) {
        this.namaBarang = namaBarang;
        this.hargaJual = hargaJual;
        this.jmlTerjual = jmlTerjual;
        this.subTotalTerjual = subTotalTerjual;
    }

    public String getNamaBarang() {
        return namaBarang;
    }

    public String getHargaJual() {
        return hargaJual;
    }

    public String getJmlTerjual() {
        return jmlTerjual;
    }

    public String getSubTotalTerjual() {
        return subTotalTerjual;
    }
}
