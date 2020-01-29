package com.pratamatechnocraft.tokason.Model;

public class ListItemDataUser {
    private String kdUser, namaUser, noTelp, levelUser, fotoUser;

    public ListItemDataUser(String kdUser, String namaUser, String noTelp, String levelUser, String fotoUser) {
        this.kdUser = kdUser;
        this.namaUser = namaUser;
        this.noTelp = noTelp;
        this.levelUser = levelUser;
        this.fotoUser = fotoUser;
    }

    public String getKdUser() {
        return kdUser;
    }

    public String getNamaUser() {
        return namaUser;
    }

    public String getNoTelp() {
        return noTelp;
    }

    public String getLevelUser() {
        return levelUser;
    }

    public  String getFotoUser(){
        return fotoUser;
    }
}
