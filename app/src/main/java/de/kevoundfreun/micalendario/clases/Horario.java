package de.kevoundfreun.micalendario.clases;

import com.alamkanak.weekview.WeekViewEvent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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

        ArrayList<WeekViewEvent> listaEventos = new ArrayList<>();

        for(Integer dia : getDias()) {
            // Initialize start and end time.
            Calendar now = Calendar.getInstance();
            Calendar startTime = (Calendar) now.clone();
            startTime.setTimeInMillis(start.getTime());
            startTime.set(Calendar.YEAR, now.get(Calendar.YEAR));
            startTime.set(Calendar.MONTH, now.get(Calendar.MONTH));
            startTime.set(Calendar.DAY_OF_WEEK, dia);
            Calendar endTime = (Calendar) startTime.clone();
            endTime.setTimeInMillis(end.getTime());
            endTime.set(Calendar.YEAR, startTime.get(Calendar.YEAR));
            endTime.set(Calendar.MONTH, startTime.get(Calendar.MONTH));
            endTime.set(Calendar.DAY_OF_WEEK, dia);

            // Create an week view event.
            WeekViewEvent weekViewEvent = new WeekViewEvent();
            weekViewEvent.setName("");
            weekViewEvent.setStartTime(startTime);
            weekViewEvent.setEndTime(endTime);
            //weekViewEvent.setColor(Color.parseColor(getColor()));
            listaEventos.add(weekViewEvent);
        }
        return listaEventos;
    }
}
