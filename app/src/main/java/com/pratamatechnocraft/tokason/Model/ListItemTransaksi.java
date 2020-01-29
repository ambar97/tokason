package com.pratamatechnocraft.tokason.Model;

public class ListItemTransaksi {
    private String kdTransaksi;
    private String noInvoice;
    private String totalHarga;
    private String tanggalTransaksi;

    public ListItemTransaksi(String kdTransaksi,String noInvoice, String totalHarga, String tanggalTransaksi) {
        this.kdTransaksi = kdTransaksi;
        this.noInvoice = noInvoice;
        this.totalHarga = totalHarga;
        this.tanggalTransaksi = tanggalTransaksi;
    }

    public String getNoInvoice() {
        return noInvoice;
    }

    public String getTotalHarga() {
        return totalHarga;
    }

    public String getTanggalTransaksi() {
        return tanggalTransaksi;
    }

    public String getKdTransaksi() {
        return kdTransaksi;
    }
}
