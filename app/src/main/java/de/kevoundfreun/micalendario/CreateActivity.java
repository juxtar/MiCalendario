package de.kevoundfreun.micalendario;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Toast;

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
    EditText nombre_actividad;

    CheckBox lunes;
    CheckBox martes;
    CheckBox miercoles;
    CheckBox jueves;
    CheckBox viernes;
    CheckBox sabado;
    CheckBox domingo;

    Actividad actividad = new Actividad();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_create);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_horario);
        ListView listaHorarios = (ListView) findViewById(R.id.lv_lista_horarios);
        final Intent intent = getIntent();
        EditText nombreActividad = (EditText) findViewById(R.id.et_nombre);

        // Database
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // Auth
        mAuth = FirebaseAuth.getInstance();


        //TODO: CORROBAR QUE ESTE IF Y LAS FUNCIONALIDADES DENTRO ESTAN BIEN UBICADAS

        //CARGA CON INFORMACION LA PANTALLA SI ESTOY MODIFICANDO ACTIVIDAD
        if(intent.hasExtra("Actividad_cargada")){
            //Levanto la actividad desde el intent
            actividad = (Actividad) intent.getSerializableExtra("Actividad_cargada");
            //seteo el nombre de la actividad en el editText
            nombreActividad.setText(actividad.getNombre());
            //seteo la lista de horarios de la actividad en el listview
            adapter = new HorarioAdapter(this, actividad.getHorarios());
        }
        else{
            adapter = new HorarioAdapter(this, actividad.getHorarios());
        }
        listaHorarios.setAdapter(adapter);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = createDialog(null);
                cargarDialog();
                dialog.show();
            }
        });
        listaHorarios.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                Dialog dialog = createDialog(actividad.getHorarios().get(i));
                cargarDialog();
                cargarDefaultsDialog(actividad.getHorarios().get(i));
                dialog.show();
                return false;
            }
        });

    }

    //Metodo para cargar el dialogo para crear el horario
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


        /*np_hs_ini.setWrapSelectorWheel(false);
        np_min_ini.setWrapSelectorWheel(false);
        np_hs_fin.setWrapSelectorWheel(false);
        np_min_fin.setWrapSelectorWheel(false);*/

        np_hs_ini.setFormatter(new DoubleDigitFormatter());
        np_min_ini.setFormatter(new DoubleDigitFormatter());
        np_hs_fin.setFormatter(new DoubleDigitFormatter());
        np_min_fin.setFormatter(new DoubleDigitFormatter());


    }

    //Metodo para cargar el dialogo para MODIFICAR el horario CON INFORMACION DEL LISTVIEW
    private void cargarDefaultsDialog(Horario horario){
        np_hs_ini = (NumberPicker) dialogView.findViewById(R.id.np_hs_inicio);
        np_min_ini = (NumberPicker) dialogView.findViewById(R.id.np_min_inicio);
        np_hs_fin = (NumberPicker) dialogView.findViewById(R.id.np_hs_fin);
        np_min_fin = (NumberPicker) dialogView.findViewById(R.id.np_min_fin);

        np_hs_ini.setValue(Integer.parseInt(horario.getHs_inicio().substring(0,2)));
        np_min_ini.setValue(Integer.parseInt(horario.getHs_inicio().substring(4)));
        np_hs_fin.setValue(Integer.parseInt(horario.getHs_fin().substring(0,2)));
        np_min_fin.setValue(Integer.parseInt(horario.getHs_fin().substring(4)));

        domingo = (CheckBox) dialogView.findViewById(R.id.domingo);
        lunes = (CheckBox) dialogView.findViewById(R.id.lunes);
        martes = (CheckBox) dialogView.findViewById(R.id.martes);
        miercoles = (CheckBox) dialogView.findViewById(R.id.miercoles);
        jueves = (CheckBox) dialogView.findViewById(R.id.jueves);
        viernes = (CheckBox) dialogView.findViewById(R.id.viernes);
        sabado = (CheckBox) dialogView.findViewById(R.id.sabado);

        ArrayList<Integer> dias = horario.getDias();
        for(Integer i: dias){
            switch (i){
                case 0:
                    domingo.setChecked(true);
                    break;
                case 1:
                    lunes.setChecked(true);
                    break;
                case 2:
                    martes.setChecked(true);
                    break;
                case 3:
                    miercoles.setChecked(true);
                    break;
                case 4:
                    jueves.setChecked(true);
                    break;
                case 5:
                    viernes.setChecked(true);
                    break;
                case 6:
                    sabado.setChecked(true);
                    break;
            }
        }
    }


    private Dialog createDialog(final Horario oldHorario){
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
                        Horario horario;
                        ArrayList<Integer> dias = new ArrayList<Integer>();
                        String hs_inicio;
                        String hs_final;

                        cargarDias(dias);
                        int h_inicio = ((NumberPicker) dialogView.findViewById(R.id.np_hs_inicio)).getValue();
                        int h_final = ((NumberPicker) dialogView.findViewById(R.id.np_hs_fin)).getValue();
                        int m_inicio = ((NumberPicker) dialogView.findViewById(R.id.np_min_inicio)).getValue();
                        int m_final = ((NumberPicker) dialogView.findViewById(R.id.np_min_fin)).getValue();


                        if (verificarHoras(h_inicio, h_final, m_inicio, m_final) && dias.size()>0 ) {
                            hs_inicio = String.format("%02d", ((NumberPicker) dialogView.findViewById(R.id.np_hs_inicio)).getValue()) + ":" +
                                    String.format("%02d", ((NumberPicker) dialogView.findViewById(R.id.np_min_inicio)).getValue());
                            hs_final = String.format("%02d", ((NumberPicker) dialogView.findViewById(R.id.np_hs_fin)).getValue()) + ":" +
                                    String.format("%02d", ((NumberPicker) dialogView.findViewById(R.id.np_min_fin)).getValue());


                            if (oldHorario == null) {
                                horario = new Horario(hs_inicio, hs_final, dias);
                                if (verificarSuperpuesta(horario,actividad.getHorarios())){
                                    actividad.getHorarios().add(horario);
                                    if (((ArrayList<Horario>) actividad.getHorarios()).size() > 0) {
                                        Toast.makeText(getApplicationContext(), "Cantidad de actividades: " + String.valueOf(((ArrayList<Horario>) actividad.getHorarios()).size()), Toast.LENGTH_SHORT).show();
                                        Log.v("HORARIOS", String.valueOf(((ArrayList<Horario>) actividad.getHorarios()).size()));
                                    }
                                }
                                else{
                                    Toast.makeText(getApplicationContext(),"Las actividades no pueden superponerse.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                ArrayList listanueva = (ArrayList)actividad.getHorarios().clone();
                                listanueva.remove(oldHorario);
                                if(verificarSuperpuesta(new Horario(hs_inicio,hs_final,dias), listanueva)) {
                                    oldHorario.setDias(dias);
                                    oldHorario.setHs_inicio(hs_inicio);
                                    oldHorario.setHs_fin(hs_final);
                                }
                                else{
                                    Toast.makeText(getApplicationContext(),"Las actividades no pueden superponerse.", Toast.LENGTH_SHORT).show();
                                }
                            }

                            adapter.notifyDataSetChanged();
                            dialog.dismiss();
                        }
                        else{
                            String error = "";
                            if (!(dias.size()>0)) {
                                error += "Debe ingresar por lo menos un día para la actividad.\n";
                            }
                            if (!(verificarHoras(h_inicio,h_final,m_inicio,m_final))){
                                    error += "La hora ingresada es incorrecta, asegurese que la hora de inicio sea distinta y menor que la de final. \n";
                            }
                            Toast.makeText(getApplicationContext(),
                                    error,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        return builder.create();
    }
    // Método que busca verificar que una misma actividad no tenga dos horarios superpuestos
    private boolean verificarSuperpuesta(Horario horario, ArrayList<Horario> horarios) {
        boolean result;
        //Verificamos que hay algún horario cargado ya en la actividad.
        if(horarios.size()== 0){
            return true;
        }
        //Obtenemos las horas y minutos de inicio y final del nuevo horario que se está queriendo agregar
        int horario_inicio_hora = Integer.parseInt(horario.getHs_inicio().substring(0,2));
        int horario_inicio_min = Integer.parseInt(horario.getHs_inicio().substring(3));
        int horario_fin_hora = Integer.parseInt(horario.getHs_fin().substring(0,2));
        int horario_fin_min = Integer.parseInt(horario.getHs_fin().substring(3));
        //Recorremos la lista de horarios de la actividad.
        for (Horario h : horarios) {
            //Obtenemos las horas y minutos de inicio y fin de cada elemento que está en la actividad.
            int h_inicio_hora = Integer.parseInt(h.getHs_inicio().substring(0,2));
            int h_inicio_min = Integer.parseInt(h.getHs_inicio().substring(3));
            int h_fin_hora = Integer.parseInt(h.getHs_fin().substring(0,2));
            int h_fin_min = Integer.parseInt(h.getHs_fin().substring(3));
            /*Comparamos cada día de la lista de días del nuevo horario con cada uno de los días de la lista de
            días del horario con el que vamos a comparar*/
            for (int x : horario.getDias()) {
                for (int y : h.getDias()) {
                    //Si son el mismo día, realizamos las validaciones.
                    if (x == y) {
                        if ((horario_inicio_hora < h_inicio_hora)) {
                            if (horario_fin_hora > h_inicio_hora) {
                                return false;
                            }
                            else{
                                if((horario_fin_hora == h_inicio_hora) && (horario_fin_min >= h_inicio_min)){
                                    return false;
                                }
                                else{
                                    if (horario_fin_hora > h_fin_hora){
                                        return false;
                                    }
                                    else{
                                        if((horario_fin_hora == h_fin_hora) && (horario_fin_min > h_fin_min)){
                                            return false;
                                        }
                                    }
                                }

                            }
                        }
                        else {
                            if (horario_inicio_hora == h_inicio_hora) {
                                if (horario_fin_min >= h_inicio_min){
                                    return false;
                                }
                            }
                            else{
                                if (horario_inicio_hora < h_fin_hora){
                                    return false;
                                }
                                else{
                                    if(horario_inicio_hora == h_fin_hora){
                                        if(horario_inicio_min < h_fin_min){
                                            return false;
                                        }
                                    }

                                }

                            }
                        }
                    }
                }
            }
        }
        return true;
    }


    private boolean verificarHoras(int h_inicio, int h_final, int m_inicio, int m_final) {
        if (h_inicio<h_final){
            return true;
        }
        else{
            if(h_inicio == h_final && m_inicio < m_final){
                return true;
            }
        }
        return false;
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
                String uid = usuario.getUid();
                nombre_actividad = (EditText) findViewById(R.id.et_nombre);
                String nombre = nombre_actividad.getText().toString();

                if(nombre.matches("")){
                    Toast.makeText(getApplicationContext(),"Ingrese un nombre para la Actividad",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    if (actividad.getHorarios().size() > 0) {
                        if (getIntent().hasExtra("Actividad_cargada")) {
                            actualizarActividad(uid, actividad);
                        } else {
                            agregarActividad(uid);
                            Snackbar snackbar = Snackbar
                                    .make(findViewById(R.id.activity_create),
                                            getString(R.string.success_actividad), Snackbar.LENGTH_LONG);

                            snackbar.show();
                            finish();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Ingrese al menos un horario para la Actividad",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void agregarActividad(String uid) {
        nombre_actividad = (EditText) findViewById(R.id.et_nombre);
        actividad.setNombre(nombre_actividad.getText().toString());
        mDatabase.child("users").child(uid).child("actividades").push()
                    .setValue(actividad);
    }


    private void actualizarActividad(String uid, Actividad act){
        String idAct = ((Actividad) getIntent().getSerializableExtra("Actividad_cargada")).obtenerId();
        EditText nombreActividad = (EditText) findViewById(R.id.et_nombre);
        actividad.setNombre(nombreActividad.getText().toString());

        mDatabase.child("users").child(uid).child("actividades").child(idAct).setValue(actividad);
        finish();
    }
}
