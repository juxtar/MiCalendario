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
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import de.kevoundfreun.micalendario.clases.Actividad;

public class ListActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    View dialogView;
    ActividadAdapter adapter;
    ArrayList<Actividad> lista_actividades;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_list);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_horario);

        //Auth
        mAuth = FirebaseAuth.getInstance();

        //Acceso Database
        mDatabase = FirebaseDatabase.getInstance().getReference();


        FirebaseUser user = mAuth.getCurrentUser();
        //Levantar lista de actividades del usuario

        Intent intent_actividades = getIntent();
        lista_actividades = (ArrayList<Actividad>) intent_actividades.getSerializableExtra("Actividades");

        // Creamos el adapter para mostrar la lista de actividades por el listView
        adapter = new ActividadAdapter(ListActivity.this,lista_actividades);
        ListView lv_lista_actividades = (ListView) findViewById(R.id.lv_lista_actividades);
        lv_lista_actividades.setAdapter(adapter);

        lv_lista_actividades.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(), "Actividad: "+lista_actividades.get(i).toString(), Toast.LENGTH_SHORT).show();
                Dialog dialog = createDialog(lista_actividades.get(i));
                dialog.show();

                return false;
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: lograr que la lista en ListActivity se actualice automaticamente despues de agregar una actividad desde este fab
                // por ahora hay que volver al calendario y volver a entrar para que la lista se actualice
                Intent intent_crear_actividad = new Intent(ListActivity.this, CreateActivity.class);
                startActivity(intent_crear_actividad);
            }
        });

    }

    private Dialog createDialog(final Actividad act) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ListActivity.this);
        // Get the layout inflater
        LayoutInflater inflater = getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        dialogView = inflater.inflate(R.layout.dialog_modificar_borrar, null, false);
        builder.setView(dialogView)
                // Add action buttons
                .setTitle("Actividad")
                .setMessage("¿Qué desea hacer con la actividad "+act.toString()+"?")
                .setPositiveButton(R.string.modificar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getApplicationContext(), "MODIFICAR Actividad", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ListActivity.this, CreateActivity.class);
                        intent.putExtra("Actividad_cargada",act);
                        startActivity(intent);

                        //para que haga refresh en la lista de actividades del listView
                        adapter.notifyDataSetChanged();

                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.eliminar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String idAct = act.obtenerId();
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        String uid = firebaseUser.getUid();

                        Toast.makeText(getApplicationContext(), "ELIMINAR Actividad", Toast.LENGTH_SHORT).show();
                        //TODO: actualizar la lista de actividades -> ahora hay que reiniciar la app para que cambios tengan efecto
                        eliminarActividad(uid,idAct);
                        dialog.dismiss();
                    }
                });
        return builder.create();
    }

    private void eliminarActividad(String uid, String idAct) {
        mDatabase.child("users").child(uid).child("actividades").child(idAct).setValue(null);
        for(Actividad ac : lista_actividades){
            if(ac.obtenerId() == idAct){
                lista_actividades.remove(ac);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_back) {
            finish();
        }
        return true;
    }
}
