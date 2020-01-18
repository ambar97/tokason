package com.pratamatechnocraft.tokason.Model;

public class ListItemDataBarang {
    private String kdBarang, namaBarang, stokBarang, hargaBarang, gambarBarang;

    public ListItemDataBarang(String kdBarang, String namaBarang, String stokBarang, String hargaBarang, String gambarBarang) {
        this.kdBarang = kdBarang;
        this.namaBarang = namaBarang;
        this.stokBarang = stokBarang;
        this.hargaBarang = hargaBarang;
        this.gambarBarang = gambarBarang;
    }

    public String getKdBarang() {
        return kdBarang;
    }

    public String getNamaBarang() {
        return namaBarang;
    }

    public String getStokBarang() {
        return stokBarang;
    }

    public String getHargaBarang() {
        return hargaBarang;
    }

    public  String getGambarBarang(){
        return gambarBarang;
    }
}
