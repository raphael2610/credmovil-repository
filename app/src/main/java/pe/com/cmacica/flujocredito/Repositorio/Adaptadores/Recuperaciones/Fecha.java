package pe.com.cmacica.flujocredito.Repositorio.Adaptadores.Recuperaciones;


import android.app.DatePickerDialog;
import android.widget.DatePicker;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import pe.com.cmacica.flujocredito.Utilitarios.Dialogos.DateDialog;

/**
 * Created by faqf on 14/08/2017.
 */

public class Fecha  extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
   public String Resulado;

public void MostrarFecha()
    {
        FragmentManager Frag = getSupportFragmentManager();
        DialogFragment picker = new DateDialog();
        picker.show(Frag, "DatePicker");
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        actualizarFecha(year, month, dayOfMonth);
    }

    public String actualizarFecha(int ano, int mes, int dia) {
        // Setear en el textview la fecha

        mes += 1;
        String cdia, cmes;
        if (dia < 10) {
            cdia = "0" + String.valueOf(dia);
        } else {
            cdia = String.valueOf(dia);
        }
        if (mes < 10) {
            cmes = "0" + String.valueOf(mes);
        } else {
            cmes = String.valueOf(mes);
        }
        Resulado=ano + "-" + cmes + "-" + cdia;
       return Resulado;
    }
}
