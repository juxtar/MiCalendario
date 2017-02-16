package de.kevoundfreun.micalendario;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.usage.UsageEvents;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import de.kevoundfreun.micalendario.clases.Actividad;
import de.kevoundfreun.micalendario.clases.MyBundle;
import de.kevoundfreun.micalendario.receivers.ReceptorAlarma;

public class MainActivity extends AppCompatActivity implements WeekView.EventLongPressListener, WeekView.EventClickListener, MonthLoader.MonthChangeListener {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    WeekView mWeekView;

    ArrayList<Actividad> actividades;
    ArrayList<WeekViewEvent> weekViewEvents;
    FirebaseUser usuario;
    ReceptorAlarma receptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CreateActivity.class);
                startActivity(intent);
            }
        });

        // Setear Receptor de Alarma
        receptor = new ReceptorAlarma();

        //Auth
        mAuth = FirebaseAuth.getInstance();
        usuario = mAuth.getCurrentUser();

        // Habilitar Modo Offline
        if (mDatabase == null)
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        //Acceso Database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Get a reference for the week view in the layout.
        mWeekView = (WeekView) findViewById(R.id.weekView);

        // Set an action when any event is clicked.
        mWeekView.setOnEventClickListener(this);

        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // month every time the month changes on the week view.
        mWeekView.setMonthChangeListener(this);

        // Set long press listener for events.
        mWeekView.setEventLongPressListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void onResume(){
        super.onResume();
        weekViewEvents = new ArrayList<>();
        actividades = new ArrayList<>();
        if(usuario != null){
            Query query = mDatabase.child("users").child(usuario.getUid()).child("actividades");
            query.orderByKey().addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Actividad actividad = Actividad.fromHashMap((HashMap<String, Object>) dataSnapshot.getValue());
                    actividad.agregarId(dataSnapshot.getKey());

                    ArrayList<WeekViewEvent> activityEvents = actividad.toWeekViewEvents();
                    for (WeekViewEvent e : activityEvents) {
                        e.setName(actividad.getNombre());
                        e.setColor(actividad.getColor());
                    }

                    weekViewEvents.addAll(activityEvents);
                    actividades.add(actividad);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mWeekView.notifyDatasetChanged();
                    receptor.setActividades(actividades);
                    if(actividades.size()!= 0)
                        programarProximaAlarma();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Log.d("MainActivity", "Stop");
            Intent intent_settings = new Intent(this, SettingsActivity.class);
            startActivity(intent_settings);
            return true;
        }
        if (id == R.id.action_logout) {
            mAuth.signOut();
            finish();
            return true;
        }
        if (id == R.id.action_ver_actividades) {
            Intent actividad_intent = new Intent(MainActivity.this, ListActivity.class);
            actividad_intent.putExtra("Actividades", actividades);
            startActivity(actividad_intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        ArrayList<WeekViewEvent> eventos = new ArrayList<>();
        for (WeekViewEvent event: weekViewEvents) {
            WeekViewEvent e = new WeekViewEvent(event.getId(), event.getName(), (Calendar) event.getStartTime().clone(), (Calendar) event.getEndTime().clone());
            e.setColor(event.getColor());
            Calendar startTime = e.getStartTime();
            startTime.set(Calendar.MONTH, newMonth-1);
            startTime.set(Calendar.DAY_OF_WEEK, event.getStartTime().get(Calendar.DAY_OF_WEEK));
            startTime.set(Calendar.YEAR, newYear);
            startTime.getTimeInMillis();
            e.setStartTime(startTime);
            Calendar endTime = e.getEndTime();
            endTime.set(Calendar.MONTH, newMonth-1);
            endTime.set(Calendar.DAY_OF_WEEK, event.getEndTime().get(Calendar.DAY_OF_WEEK));
            endTime.set(Calendar.YEAR, newYear);
            endTime.getTimeInMillis();
            e.setEndTime(endTime);
            eventos.add(e);
        }
        return eventos;
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {

    }

    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {

    }

    @Override
    public void onBackPressed(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(R.string.exit_dialog)
                .setPositiveButton(R.string.exit_confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        // Create the AlertDialog object and return it
        builder.create().show();
    }

    private void programarProximaAlarma() {
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);

        final String ALARMA = "de.kevoundfreun.micalendario.MainActivity.ReceptorAlarma";
        IntentFilter intentFilter = new IntentFilter(ALARMA);
        ReceptorAlarma receptor = new ReceptorAlarma();
        registerReceiver(receptor, intentFilter);

        Intent alarmaIntent = new Intent(this, ReceptorAlarma.class);
        HashMap<String, Object> proxActividad = calcularProximaActividad(actividades);
        Actividad act = (Actividad)proxActividad.get("actividad");
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
        SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String strTiempoPreference = preferencias.getString("pref_tiempos", "5");
        long tiempoPreference = Long.parseLong(strTiempoPreference);
        long minutosAntesAAvisar = tiempoPreference * 60000;
        cal.setTimeInMillis(cal.getTimeInMillis() - minutosAntesAAvisar);
        cal.getTimeInMillis(); // Work-around lazy updating
        Log.d("Alarma", "Avisar previos: "+tiempoPreference);
        Log.d("Alarma", "Va a sonar a las: "+cal.getTimeInMillis());
        am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
    }

    static public HashMap<String, Object> calcularProximaActividad(List<Actividad> actividades) {
        HashMap<String, Object> result = new HashMap<>();
        // Inicializo con proximo evento de la primera actividad
        if (actividades.isEmpty())
            return null;
        long milisProximaActividad = actividades.get(0).calcularProximoHorario().getTimeInMillis();
        result.put("actividad", actividades.get(0));
        for (Actividad a : actividades) {
            long milisProximoHorario = a.calcularProximoHorario().getTimeInMillis();
            if (milisProximoHorario < milisProximaActividad) {
                milisProximaActividad = milisProximoHorario;
                result.put("actividad", a);
            }
        }
        long now = Calendar.getInstance().getTimeInMillis();
        Calendar proximoHorario = Calendar.getInstance();
        proximoHorario.setTimeInMillis(milisProximaActividad + now);
        result.put("horario", proximoHorario);
        return result;
    }
}
