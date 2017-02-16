package de.kevoundfreun.micalendario.clases;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.net.MulticastSocket;
import java.util.Calendar;

/**
 * Created by Juxtar on 15/02/2017.
 */

public class MyBundle implements Serializable {
    private Actividad actividad;
    private Calendar horario;

    public MyBundle(Actividad actividad, Calendar horario) {
        this.actividad = actividad;
        this.horario = horario;
    }

    public Actividad getActividad() {
        return actividad;
    }

    public void setActividad(Actividad actividad) {
        this.actividad = actividad;
    }

    public Calendar getHorario() {
        return horario;
    }

    public void setHorario(Calendar horario) {
        this.horario = horario;
    }
}
