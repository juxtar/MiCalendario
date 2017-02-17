package de.kevoundfreun.micalendario.receivers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.Calendar;
import java.util.Locale;

import de.kevoundfreun.micalendario.MainActivity;
import de.kevoundfreun.micalendario.R;
import de.kevoundfreun.micalendario.clases.Actividad;
import de.kevoundfreun.micalendario.clases.MyBundle;
import de.kevoundfreun.micalendario.servicios.OnActividadNotificadaListener;

/**
 * Created by Juxtar on 15/02/2017.
 */

public class ReceptorAlarma extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        ByteArrayInputStream bis = new ByteArrayInputStream(intent.getByteArrayExtra("bundle"));
        ObjectInput in = null;
        MyBundle myBundle = null;
        try {
            in = new ObjectInputStream(bis);
            myBundle = (MyBundle) in.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        Actividad actividad = myBundle.getActividad();
        Calendar horario = myBundle.getHorario();
        Log.d("ReceptorAlarma", actividad.getNombre());

        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent notificationPIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setContentTitle(actividad.getNombre())
                .setContentText("La actividad comenzará a las " +
                        String.format(Locale.getDefault(), "%02d", horario.get(Calendar.HOUR_OF_DAY)) +
                        ":" +
                        String.format(Locale.getDefault(), "%02d", horario.get(Calendar.MINUTE)))
                .setSmallIcon(R.drawable.ic_calendar_in_a_circle_interface_symbol)
                .setContentIntent(notificationPIntent)
                .setAutoCancel(true);

        SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(context);
        String strRingtonePreference = preferencias.getString("pref_ringtone", "DEFAULT_SOUND");
        notificationBuilder.setSound(Uri.parse(strRingtonePreference));

        NotificationManager manager = (NotificationManager) context.getSystemService(Context
                .NOTIFICATION_SERVICE);

        manager.notify(1, notificationBuilder.build());
        _listener.onActividadNotificada();
    }

    static OnActividadNotificadaListener _listener;

    public static void setOnActividadNotificadaListener(OnActividadNotificadaListener listener) {
        _listener = listener;
    }
}
