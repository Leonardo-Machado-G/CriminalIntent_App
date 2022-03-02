package com.jcarlosprofesor.criminalintent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import java.util.List;
import java.util.UUID;
public class CrimePagerActivity extends AppCompatActivity implements CrimeFragment.Callbacks{

    //Declaro una lista, viewpager y una variable para obtener el id del crime
    private static final String EXTRA_CRIME_ID = "crime_id";
    private ViewPager2 mViewPager;
    private List<Crime> mCrimes;

    //Metodo que se ejecuta segun el ciclo de vida del fragment
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Asocio a la view un layout
        setContentView(R.layout.activity_crime_pager);

        //Definimos un UUID obtengo del intent
        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);

        //Traemos la lista de elemento Crime contenida en CrimeLab
        this.mCrimes = CrimeLab.get(this).getCrimes();

        //Creamos el objeto mViewPager que mostrara los crimenes
        this.mViewPager = (ViewPager2) findViewById(R.id.activity_crime_pager_view_pager);

        //Seteamos el adaptador necesario para leer los objetos Crime de la lista
        this.mViewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override

            //Metodo llamado para crear los fragments en funcion de la posicion
            public Fragment createFragment(int position) {
                return CrimeFragment.newInstance(mCrimes.get(position).getId());
            }

            //Defino la cantidad de views
            @Override
            public int getItemCount() {return mCrimes.size();}

        });

        //Establezco el primer elemento de la lista al iniciar el viewpager
        for(int i = 0; i < mCrimes.size(); i++){

            if (this.mCrimes.get(i).getId().equals(crimeId)) {

                this.mViewPager.setCurrentItem(i);

            }

        }

    }

    //Metodo para ejecutar la fragmentactivity con un ID en el intent
    public static Intent newIntent(Context packageContext, UUID crimeId){
        return new Intent(packageContext,CrimePagerActivity.class).putExtra(EXTRA_CRIME_ID,crimeId);
    }

    //Metodo para actualizar el fragment heredado
    @Override
    public void onCrimeUpdated(Crime crime) {

    }

}
