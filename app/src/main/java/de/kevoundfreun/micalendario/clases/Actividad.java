package de.kevoundfreun.micalendario.clases;

import android.widget.ArrayAdapter;

import com.alamkanak.weekview.WeekViewEvent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ke on 12/2/2017.
 */

public class Actividad implements Serializable{
    String nombre;
    //TODO: agregar lugar
    ArrayList<Horario> horarios;

    public Actividad(String nombre, ArrayList<Horario> horarios) {
        this.nombre = nombre;
        this.horarios = horarios;
    }

    public Actividad() {
        this.horarios = new ArrayList<>();
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

    public static Actividad fromHashMap(HashMap<String, Object> hashMap) {
        // Firebase guarda las actividades como HashMaps
        Actividad actividad = new Actividad();

        // Nombre es solo un string
        actividad.setNombre((String)hashMap.get("nombre"));

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
}
