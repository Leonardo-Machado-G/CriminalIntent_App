package com.jcarlosprofesor.criminalintent;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;

public class TimerPickerFragment extends DialogFragment {

    //Instanciamos una variable para intercambiar informacion
    public static final String ARG_TIME = "time";
    public static final String EXTRA_TIME = "time";

    //Metodo para instanciar el fragment
    public static TimerPickerFragment newIntance(Date date){
        Bundle args = new Bundle();
        args.putSerializable(ARG_TIME,date);
        TimerPickerFragment fragment = new TimerPickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    //Metodo que es ejecutado al usar .show
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        //Instancio una view y la asocio con su layout
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_time,null);

        //Obtengo el date de newintance
        Date date = (Date) getArguments().getSerializable(ARG_TIME);

        //Instancio un calendar
        Calendar calendar = Calendar.getInstance();

        //Asocio la fecha a calendar
        calendar.setTime(date);

        //Obtengo las horas y minutos
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);

        //Instancio el dialog y lo defino
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                //Instancio una fecha y le asigno el tiempo
                Date time = new Date();
                time.setHours(selectedHour);
                time.setMinutes(selectedMinute);
                sendResult(Activity.RESULT_OK,time);

            }

        }, hour, minute, true);

        //Asigno un title y muestro el fragment
        timePickerDialog.setTitle(R.string.time_picker_title);
        timePickerDialog.show();
        return timePickerDialog;

    }

    //Metodo que actualiza el fragment llamando a activityresult
    private void sendResult(int resultCode, Date time){

        //Devuelve el fragmento de destino establecido por setTargetFragment
        if(getTargetFragment() == null){
            return;
        }

        //Recibe el resultado de una llamada anterior a startActivityForResult
        getTargetFragment()
                .onActivityResult(getTargetRequestCode(),
                resultCode,
                new Intent().putExtra(EXTRA_TIME,time));

    }

}