package de.kevoundfreun.micalendario.clases;

import java.util.ArrayList;

/**
 * Created by ke on 12/2/2017.
 */

public class Horario {

    String hs_inicio;
    String hs_fin;
    ArrayList<Integer> dias;

    public Horario(String hs_inicio, String hs_fin, ArrayList<Integer> dias) {
        this.hs_inicio = hs_inicio;
        this.hs_fin = hs_fin;
        this.dias = dias;
    }

    public String getHs_inicio() {
        return hs_inicio;
    }

    public void setHs_inicio(String hs_inicio) {
        this.hs_inicio = hs_inicio;
    }

    public String getHs_fin() {
        return hs_fin;
    }

    public void setHs_fin(String hs_fin) {
        this.hs_fin = hs_fin;
    }

    public ArrayList<Integer> getDias() {
        return dias;
    }

    public void setDias(ArrayList<Integer> dias) {
        this.dias = dias;
    }

    @Override
    public String toString() {
        return "Horario{" +
                "hs_inicio='" + hs_inicio + '\'' +
                ", hs_fin='" + hs_fin + '\'' +
                ", dias=" + dias +
                '}';
    }
}
