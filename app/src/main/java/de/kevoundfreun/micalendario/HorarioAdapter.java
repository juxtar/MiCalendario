package de.kevoundfreun.micalendario;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import de.kevoundfreun.micalendario.clases.Horario;

/**
 * Created by ke on 12/2/2017.
 */

public class HorarioAdapter extends ArrayAdapter<Horario> {
    public HorarioAdapter(Context context, ArrayList<Horario> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Horario horario = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fila_horario_listview, parent, false);
        }
        // Lookup view for data population
        TextView tvDias = (TextView) convertView.findViewById(R.id.textView_listaDias);
        TextView tvHorario = (TextView) convertView.findViewById(R.id.textView_horario);

        // Populate the data into the template view using the data object

        String dias = "";

        for(Integer dia : horario.getDias()){
            switch (dia){
                case 0:
                    dias = dias + "Lu. ";
                    break;
                case 1:
                    dias = dias + "Ma. ";
                    break;
                case 2:
                    dias = dias + "Mi. ";
                    break;
                case 3:
                    dias = dias + "Ju. ";
                    break;
                case 4:
                    dias = dias + "Vi. ";
                    break;
                case 5:
                    dias = dias + "Sa. ";
                    break;
                case 6:
                    dias = dias + "Do.";
                    break;
            }
        }

        tvDias.setText(dias);

        String hs = "De :    " + horario.getHs_inicio()+"  a  "+horario.getHs_fin();
        tvHorario.setText(hs);

        // Return the completed view to render on screen
        return convertView;
    }
}
