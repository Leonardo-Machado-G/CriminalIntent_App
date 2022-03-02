package com.jcarlosprofesor.criminalintent;
import androidx.fragment.app.Fragment;
import android.content.Context;
import android.content.Intent;
import java.util.UUID;
public class CrimeActivity extends SingleFragmentActivity {

    //Declaro una variable para el envio de un ID
    public static final String EXTRA_CRIME_ID = "crime_id";

    //Metodo heredado que devuelve un fragment
    @Override
    protected Fragment createFragment() {
        return CrimeFragment.newInstance((UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID));
    }

    //Metodo para devolver un intent con un ID
    public static Intent newIntent(Context packageContext, UUID crimeId){
        return new Intent(packageContext,CrimeActivity.class).putExtra(EXTRA_CRIME_ID,crimeId);
    }

}