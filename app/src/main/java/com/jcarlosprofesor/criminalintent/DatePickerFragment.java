package com.jcarlosprofesor.criminalintent;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

//Clase que va a definir la instancia de nuestro dialogo
public class DatePickerFragment extends DialogFragment {

    //Creamos la constante para identificar la fecha dentro del bundle
    private static  final String ARG_DATE = "date";

    //Creamos la constante para identificar la fecha que pasamos en el intent
    public static final String EXTRA_DATE = "intent_date";

    //Declaro un datepicker
    private DatePicker mDatePicker;

    //Creamos el metodo newInstance para el bundle
    public static DatePickerFragment newInstante(Date date){
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE,date);
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    //Metodo llamado para presentar el DialogFragment en pantalla
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        //Instancio una fecha
        Date date = (Date) getArguments().getSerializable(ARG_DATE);

        //Instancio un calendar
        Calendar calendar = Calendar.getInstance();

        //Asocio la fecha
        calendar.setTime(date);

        //Obtengo los datos del calendar
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        //Instancio una view y la asocio a un layout
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_date,null);

        //Serializo datepicker
        this.mDatePicker = (DatePicker) view.findViewById(R.id.dialog_data_picker);

        //Inicia un el estado sin un listener
        this.mDatePicker.init(year,month,day,null);

        //Retorno un dialog con los datos asociados
        return new AlertDialog.Builder(getActivity())
                .setView(view)                                  //Asocio la view
                .setTitle(R.string.date_picker_title)                      //Asocio el nombre
                .setPositiveButton(android.R.string.ok,         //Asocio el nombre de Ok en el boton
                        new DialogInterface.OnClickListener() { //Defino un listener
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //Obtengo los datos del datepicker
                                int year = mDatePicker.getYear();
                                int month = mDatePicker.getMonth();
                                int day = mDatePicker.getDayOfMonth();

                                //Instancio una fecha
                                Date date = new GregorianCalendar(year,month,day).getTime();

                                //Devuelvo una fecha con la variable ok
                                sendResult(Activity.RESULT_OK,date);
                            }
                        })

                .create();

    }

    //Metodo para llamar a onactivity result de crimefragment y definir un comportamiento
    private void sendResult(int resultCode, Date date){

        //Si no hay asociado un fragment retorna
        if(getTargetFragment() == null){
            return;
        }

        getTargetFragment()                     //Devuelve el fragment asociado en settarget
                .onActivityResult(              //Llamo al metodo del fragment
                        getTargetRequestCode(), //Inserta el valor insertado en sertarget
                        resultCode,new Intent() //Inserto el codigo del parametro y el intent
                                .putExtra(EXTRA_DATE,date));

    }

}
