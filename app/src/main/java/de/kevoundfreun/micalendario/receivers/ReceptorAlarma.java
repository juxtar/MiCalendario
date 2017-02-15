package de.kevoundfreun.micalendario.receivers;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import de.kevoundfreun.micalendario.MainActivity;
import de.kevoundfreun.micalendario.R;
import de.kevoundfreun.micalendario.clases.Actividad;

/**
 * Created by Juxtar on 15/02/2017.
 */

public class ReceptorAlarma extends BroadcastReceiver {
    private List<Actividad> actividades;

    @Override
    public void onReceive(Context context, Intent intent) {
        HashMap<String, Object> proxActividad = (HashMap<String, Object>) intent.getSerializableExtra("proxActividad");
        Actividad actividad = (Actividad) proxActividad.get("actividad");
        Calendar horario = (Calendar)proxActividad.get("horario");

        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent notificationPIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setContentTitle(actividad.getNombre())
                .setContentText("La actividad comenzará a las "+horario.get(Calendar.HOUR)+":"+horario.get(Calendar.MINUTE))
                .setSmallIcon(R.drawable.ic_notifications_active_black_24dp)
                .setContentIntent(notificationPIntent)
                .setAutoCancel(true);
        NotificationManager manager = (NotificationManager) context.getSystemService(Context
                .NOTIFICATION_SERVICE);

        manager.notify(1, notificationBuilder.build());
    }

    public void setActividades(List<Actividad> actividades) {
        this.actividades = actividades;
    }
}
