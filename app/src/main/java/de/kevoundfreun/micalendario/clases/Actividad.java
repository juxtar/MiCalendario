package de.kevoundfreun.micalendario.clases;

import android.widget.ArrayAdapter;

import com.alamkanak.weekview.WeekViewEvent;

import java.util.ArrayList;

/**
 * Created by ke on 12/2/2017.
 */

public class Actividad {
    String nombre;
    //TODO: agregar lugar
    ArrayList<Horario> horarios;

    public Actividad(String nombre, ArrayList<Horario> horarios) {
        this.nombre = nombre;
        this.horarios = horarios;
    }

    public Actividad() {
        this.horarios = new ArrayList<Horario>();

    }

    public String getNombre() {
        return nombre;
    }

    public ArrayList<Horario> getHorarios() {
        return horarios;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setHorarios(ArrayList<Horario> horarios) {
        this.horarios = horarios;
    }

    public ArrayList<WeekViewEvent> toWeekViewEvents(){
        ArrayList<WeekViewEvent> listaEventos = new ArrayList<>();

        for(Horario h : horarios){
            listaEventos.addAll(h.toWeekViewEvents());
        }

        return listaEventos;
    }
}
