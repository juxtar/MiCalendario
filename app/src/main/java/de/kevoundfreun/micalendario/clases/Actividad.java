package de.kevoundfreun.micalendario.clases;

import android.graphics.Color;
import android.os.Parcelable;
import android.widget.ArrayAdapter;

import com.alamkanak.weekview.WeekViewEvent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by ke on 12/2/2017.
 */

public class Actividad implements Serializable{
    String nombre;
    //TODO: agregar lugar
    ArrayList<Horario> horarios;
    String id;
    int color;

    public Actividad(String nombre, ArrayList<Horario> horarios) {
        this.nombre = nombre;
        this.horarios = horarios;
        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        this.color = color;
    }

    public Actividad() {
        this.horarios = new ArrayList<>();
        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
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

    public void agregarId(String id) { this.id = id; }

    public String obtenerId() { return this.id; }

    public ArrayList<WeekViewEvent> toWeekViewEvents(){
        ArrayList<WeekViewEvent> listaEventos = new ArrayList<>();

        for(Horario h : horarios){
            listaEventos.addAll(h.toWeekViewEvents());
        }

        return listaEventos;
    }

    public static Actividad fromHashMap(HashMap<String, Object> hashMap) {
        // Firebase guarda las actividades como HashMaps
        Actividad actividad = new Actividad();

        // Nombre es solo un String
        actividad.setNombre((String)hashMap.get("nombre"));
        // Color es solo un Long
        actividad.setColor(((Long)hashMap.get("color")).intValue());

        // Para horarios debemos ir recorriendo la lista y casteando respectivamente
        ArrayList<HashMap<String, Object>> horariosHash = (ArrayList<HashMap<String, Object>>)
                hashMap.get("horarios");
        ArrayList<Horario> horarios = new ArrayList<>();
        for (HashMap<String, Object> h : horariosHash) {
            Horario horario = new Horario();
            horario.setHs_fin((String)h.get("hs_fin"));
            horario.setHs_inicio((String)h.get("hs_inicio"));
            // Al llegar a la lista de dias, ya no hace falta seguir casteando
            ArrayList<Integer> dias = arrayLongToIntegers((ArrayList<Long>) h.get("dias"));
            horario.setDias(dias);
            horarios.add(horario);
        }
        actividad.setHorarios(horarios);
        return actividad; // Devolvemos la actividad generada
    }

    private static ArrayList<Integer> arrayLongToIntegers(ArrayList<Long> lista) {
        int nInts = lista.size();
        ArrayList<Integer> ints = new ArrayList<>(nInts);
        for (int i=0;i<nInts;++i) {
            ints.add(lista.get(i).intValue());
        }
        return ints;
    }

    @Override
    public String toString() {
        return nombre;
    }

    public Calendar calcularProximoHorario() {
        long now = Calendar.getInstance().getTimeInMillis();
        long milisProximoHorario = 0;
        for (Horario h : horarios) {
            List<WeekViewEvent> eventos = h.toWeekViewEvents();
            int i = 1;
            long milisProximoWeekViewEvent = eventos.get(0).getStartTime().getTimeInMillis() - now;
            while (milisProximoWeekViewEvent < (1000 * 24 * 3600 * 7) && (i < eventos.size())) {
                WeekViewEvent e = eventos.get(i);
                milisProximoWeekViewEvent = e.getStartTime().getTimeInMillis() - now;
                i++;
                if ((milisProximoHorario == 0) && (milisProximoWeekViewEvent > 0) )
                    milisProximoHorario = milisProximoWeekViewEvent;
                if ((milisProximoWeekViewEvent < milisProximoHorario) && (milisProximoWeekViewEvent > 0))
                    milisProximoHorario = milisProximoWeekViewEvent;
            }
        }
        Calendar proximoHorario = Calendar.getInstance();
        proximoHorario.setTimeInMillis(milisProximoHorario);
        return proximoHorario;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Actividad)) return false;

        Actividad actividad = (Actividad) o;

        if (obtenerId() != null ? !obtenerId().equals(actividad.obtenerId()) : actividad.obtenerId() != null)
            return false;
        if (getNombre() != null ? !getNombre().equals(actividad.getNombre()) : actividad.getNombre() != null)
            return false;
        return getColor() == actividad.getColor();

    }
}
