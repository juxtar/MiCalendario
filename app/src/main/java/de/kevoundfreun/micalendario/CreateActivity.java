package de.kevoundfreun.micalendario;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import de.kevoundfreun.micalendario.clases.Actividad;
import de.kevoundfreun.micalendario.clases.Horario;

public class CreateActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    View dialogView;
    NumberPicker np_hs_ini;
    NumberPicker np_min_ini;
    NumberPicker np_hs_fin;
    NumberPicker np_min_fin;
    HorarioAdapter adapter;

    Actividad actividad = new Actividad();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_create);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_horario);
        ListView listaHorarios = (ListView) findViewById(R.id.lv_lista_horarios);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = createDialog();
                cargarDialog();
                dialog.show();
            }
        });

        adapter = new HorarioAdapter(this, actividad.getHorarios());

        listaHorarios.setAdapter(adapter);

        // Database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Auth
        mAuth = FirebaseAuth.getInstance();
    }

    private void cargarDialog(){

        np_hs_ini = (NumberPicker) dialogView.findViewById(R.id.np_hs_inicio);
        np_min_ini = (NumberPicker) dialogView.findViewById(R.id.np_min_inicio);
        np_hs_fin = (NumberPicker) dialogView.findViewById(R.id.np_hs_fin);
        np_min_fin = (NumberPicker) dialogView.findViewById(R.id.np_min_fin);


        np_hs_ini.setMinValue(0);
        np_hs_ini.setMaxValue(23);
        np_min_ini.setMinValue(0);
        np_min_ini.setMaxValue(59);

        np_hs_fin.setMinValue(0);
        np_hs_fin.setMaxValue(23);
        np_min_fin.setMinValue(0);
        np_min_fin.setMaxValue(59);


        np_hs_ini.setWrapSelectorWheel(false);
        np_min_ini.setWrapSelectorWheel(false);
        np_hs_fin.setWrapSelectorWheel(false);
        np_min_fin.setWrapSelectorWheel(false);

        np_hs_ini.setFormatter(new DoubleDigitFormatter());
        np_min_ini.setFormatter(new DoubleDigitFormatter());
        np_hs_fin.setFormatter(new DoubleDigitFormatter());
        np_min_fin.setFormatter(new DoubleDigitFormatter());


    }


    private Dialog createDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateActivity.this);
        // Get the layout inflater
        LayoutInflater inflater = getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        dialogView = inflater.inflate(R.layout.horario_create, null, false);
        builder.setView(dialogView)
                // Add action buttons
                .setTitle("Horario")
                .setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //TODO: guardar horarios
                        Horario horario;
                        ArrayList<Integer> dias = new ArrayList<Integer>();
                        String hs_inicio;
                        String hs_final;

                        cargarDias(dias);

                        hs_inicio = String.format("%02d",((NumberPicker) dialogView.findViewById(R.id.np_hs_inicio)).getValue()) +":"+
                                String.format("%02d",((NumberPicker) dialogView.findViewById(R.id.np_min_inicio)).getValue());
                        hs_final = String.format("%02d",((NumberPicker) dialogView.findViewById(R.id.np_hs_fin)).getValue()) +":"+
                                String.format("%02d",((NumberPicker) dialogView.findViewById(R.id.np_min_fin)).getValue());

                        horario = new Horario(hs_inicio,hs_final,dias);
                        actividad.getHorarios().add(horario);
                        adapter.notifyDataSetChanged();
                        if(((ArrayList<Horario>) actividad.getHorarios()).size()>0) {
                            Log.v("HORARIOS", (((ArrayList<Horario>) actividad.getHorarios()).get(0)).toString());
                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        return builder.create();
    }

    private class DoubleDigitFormatter implements NumberPicker.Formatter{
        @Override
        public String format(int i) {
            return String.format("%02d", i);
        }
    }

    private void cargarDias(ArrayList<Integer> dias){
        ArrayList<CheckBox> ch_dias = new ArrayList<CheckBox>();
        ch_dias.add((CheckBox) dialogView.findViewById(R.id.domingo));
        ch_dias.add((CheckBox) dialogView.findViewById(R.id.lunes));
        ch_dias.add((CheckBox) dialogView.findViewById(R.id.martes));
        ch_dias.add((CheckBox) dialogView.findViewById(R.id.miercoles));
        ch_dias.add((CheckBox) dialogView.findViewById(R.id.jueves));
        ch_dias.add((CheckBox) dialogView.findViewById(R.id.viernes));
        ch_dias.add((CheckBox) dialogView.findViewById(R.id.sabado));
        for(int i=0; i<7;i++){
            if(ch_dias.get(i).isChecked()){
                dias.add(Integer.valueOf(i));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {
            FirebaseUser usuario = mAuth.getCurrentUser();
            if (usuario != null) {
                agregarActividad(usuario.getUid());
                Snackbar snackbar = Snackbar
                        .make(findViewById(R.id.activity_create),
                                getString(R.string.success_actividad), Snackbar.LENGTH_LONG);

                snackbar.show();
                finish();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void agregarActividad(String uid) {
        EditText nombreActividad = (EditText) findViewById(R.id.et_nombre);
        actividad.setNombre(nombreActividad.getText().toString());
        mDatabase.child("users").child(uid).child("actividades").push()
                .setValue(actividad);
    }
}
