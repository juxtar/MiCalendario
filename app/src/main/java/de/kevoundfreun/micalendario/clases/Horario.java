package de.kevoundfreun.micalendario.clases;

import android.graphics.Color;
import android.util.Log;

import com.alamkanak.weekview.WeekViewEvent;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * Created by ke on 12/2/2017.
 */

public class Horario implements Serializable{

    String hs_inicio;
    String hs_fin;
    ArrayList<Integer> dias;

    public Horario(String hs_inicio, String hs_fin, ArrayList<Integer> dias) {
        this.hs_inicio = hs_inicio;
        this.hs_fin = hs_fin;
        this.dias = dias;
    }

    public Horario(){
        dias = new ArrayList<>();
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

    public ArrayList<WeekViewEvent> toWeekViewEvents(){

        // Parse time.
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date start = new Date();
        Date end = new Date();
        try {
            start = sdf.parse(getHs_inicio());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            end = sdf.parse(getHs_fin());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Log.d("Horario: start", start.toString());
        Log.d("Horario: end", end.toString());

        ArrayList<WeekViewEvent> listaEventos = new ArrayList<>();

        for(int i = 0;i<4;i++) {
            for (Integer dia : getDias()) {
                // Initialize start and end time.

                Calendar now = Calendar.getInstance();
                Calendar startTime = (Calendar) now.clone();
                startTime.setTimeInMillis(start.getTime());
                startTime.set(Calendar.YEAR, now.get(Calendar.YEAR));
                startTime.set(Calendar.MONTH, now.get(Calendar.MONTH));
                startTime.set(Calendar.DAY_OF_WEEK, dia+1);
                startTime.setTimeInMillis(startTime.getTimeInMillis() + i*(1000 * 24 * 3600 * 7));
                startTime.getTimeInMillis();
                Calendar endTime = (Calendar) startTime.clone();
                endTime.setTimeInMillis(startTime.getTimeInMillis() + (end.getTime() - start.getTime()));

                // Create an week view event.
                WeekViewEvent weekViewEvent = new WeekViewEvent();
                weekViewEvent.setName("");
                weekViewEvent.setStartTime(startTime);
                weekViewEvent.setEndTime(endTime);
                listaEventos.add(weekViewEvent);
            }
        }
        return listaEventos;
    }

    public String diasTexto() {
        String dias = "";
        for(Integer dia : getDias()){
            switch (dia){
                case 0:
                    dias+=" Do.";
                    break;
                case 1:
                    dias+="Lu.";
                    break;
                case 2:
                    dias+=" Ma.";
                    break;
                case 3:
                    dias+=" Mi.";
                    break;
                case 4:
                    dias+=" Ju.";
                    break;
                case 5:
                    dias+=" Vi.";
                    break;
                case 6:
                    dias+=" Sa.";
                    break;
            }
        }
        return dias;
    }
}
