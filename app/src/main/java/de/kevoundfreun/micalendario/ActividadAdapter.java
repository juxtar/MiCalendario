package de.kevoundfreun.micalendario;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import de.kevoundfreun.micalendario.clases.Actividad;

/**
 * Created by ke on 14/2/2017.
 */

public class ActividadAdapter extends ArrayAdapter<Actividad>{

    public ActividadAdapter(Context context, ArrayList<Actividad> actividades) {
        super(context, 0, actividades);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Actividad actividad = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fila_actividad_listview, parent, false);
        }

        // Lookup view for data population
        TextView tvActividad = (TextView) convertView.findViewById(R.id.textView_nombre_actividad);

        // Populate the data into the template view using the data object
        String actividad_nombre = actividad.getNombre();
        tvActividad.setText(actividad_nombre);

        // Return the completed view to render on screen
        return convertView;
    }

}
