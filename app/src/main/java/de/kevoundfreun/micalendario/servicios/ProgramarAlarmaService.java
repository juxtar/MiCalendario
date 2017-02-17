package de.kevoundfreun.micalendario.servicios;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import de.kevoundfreun.micalendario.MainActivity;
import de.kevoundfreun.micalendario.clases.Actividad;
import de.kevoundfreun.micalendario.clases.Horario;
import de.kevoundfreun.micalendario.clases.MyBundle;
import de.kevoundfreun.micalendario.receivers.ReceptorAlarma;

/**
 * Created by Juxtar on 15/02/2017.
 */

public class ProgramarAlarmaService extends Service {
    ArrayList<Actividad> actividades;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent,flags,startId);
        actividades = (ArrayList<Actividad>) intent.getSerializableExtra("Actividades");
        Log.d("AlarmaService", "Servicio iniciado...");

        long tiempoCreacion = (long)intent.getSerializableExtra("TiempoActual");
        if (tiempoCreacion > Calendar.getInstance().getTimeInMillis()) {
            Log.d("AlarmaService", "...por mi");
            programarProximaAlarma();
        }

        return Service.START_REDELIVER_INTENT;
    }

    private void programarProximaAlarma() {
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);

        final String ALARMA = "de.kevoundfreun.micalendario.MainActivity.ReceptorAlarma";
        IntentFilter intentFilter = new IntentFilter(ALARMA);
        OnActividadNotificadaListener onActividadNotificadaListener = new OnActividadNotificadaListener() {
            @Override
            public void onActividadNotificada() {
                programarProximaAlarma();
            }
        };
        ReceptorAlarma.setOnActividadNotificadaListener(onActividadNotificadaListener);
        ReceptorAlarma receptor = new ReceptorAlarma();
        registerReceiver(receptor, intentFilter);

        Intent alarmaIntent = new Intent(this, ReceptorAlarma.class);
        HashMap<String, Object> proxActividad = MainActivity.calcularProximaActividad(actividades);
        SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String strTiempoPreference = preferencias.getString("pref_tiempos", "5");
        long tiempoPreference = Long.parseLong(strTiempoPreference);
        long milisAntesAAvisar = tiempoPreference * 60000;
        if (proxActividad == null)
            return;
        Calendar horario = (Calendar)proxActividad.get("horario");
        Actividad act = (Actividad)proxActividad.get("actividad");
        if ((horario.getTimeInMillis() - milisAntesAAvisar) < (Calendar.getInstance().getTimeInMillis() + 1000)) {
            return;
        }
        Log.d("Alarma", act.getNombre());
        MyBundle bundle = new MyBundle((Actividad)proxActividad.get("actividad"), (Calendar)proxActividad.get("horario"));
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(bundle);
            out.flush();
            byte[] data = bos.toByteArray();
            alarmaIntent.putExtra("bundle", data);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        PendingIntent pi = PendingIntent.getBroadcast(this.getApplicationContext(), 1,
                alarmaIntent, 0);
        Calendar cal = (Calendar)proxActividad.get("horario");
        cal.setTimeInMillis(cal.getTimeInMillis() - milisAntesAAvisar);
        cal.getTimeInMillis(); // Work-around lazy updating
        Log.d("Alarma", "Avisar previos: "+tiempoPreference);
        Log.d("Alarma", "Va a sonar a las: "+cal.getTimeInMillis());
        am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
