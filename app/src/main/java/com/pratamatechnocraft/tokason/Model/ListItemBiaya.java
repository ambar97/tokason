package com.pratamatechnocraft.tokason.Model;

public class ListItemBiaya {
    private String kdBiaya;
    private String namaBiaya;
    private String jmlBiaya;
    private String tanggalBiaya;

    public ListItemBiaya(String kdBiaya, String namaBiaya, String jmlBiaya, String tanggalBiaya) {
        this.kdBiaya = kdBiaya;
        this.namaBiaya = namaBiaya;
        this.jmlBiaya = jmlBiaya;
        this.tanggalBiaya = tanggalBiaya;
    }

    public String getKdBiaya() {
        return kdBiaya;
    }

    public String getNamaBiaya() {
        return namaBiaya;
    }

    public String getJmlBiaya() {
        return jmlBiaya;
    }

    public String getTanggalBiaya() {
        return tanggalBiaya;
    }

}
