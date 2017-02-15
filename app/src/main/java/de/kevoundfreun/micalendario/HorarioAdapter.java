package de.kevoundfreun.micalendario;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import de.kevoundfreun.micalendario.clases.Horario;

/**
 * Created by ke on 12/2/2017.
 */

public class HorarioAdapter extends ArrayAdapter<Horario> {
    public HorarioAdapter(Context context, ArrayList<Horario> horarios) {
        super(context, 0, horarios);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final Horario horario = getItem(position);
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
                case 1:
                    dias = dias + "Lu. ";
                    break;
                case 2:
                    dias = dias + "Ma. ";
                    break;
                case 3:
                    dias = dias + "Mi. ";
                    break;
                case 4:
                    dias = dias + "Ju. ";
                    break;
                case 5:
                    dias = dias + "Vi. ";
                    break;
                case 6:
                    dias = dias + "Sa. ";
                    break;
                case 0:
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
