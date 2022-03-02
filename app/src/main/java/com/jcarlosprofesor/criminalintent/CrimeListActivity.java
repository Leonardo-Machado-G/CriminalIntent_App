package com.jcarlosprofesor.criminalintent;
import android.content.Intent;
import androidx.fragment.app.Fragment;
public class CrimeListActivity extends SingleFragmentActivity
        implements CrimeListFragment.Callbacks, CrimeFragment.Callbacks{

    //Metodo heredado para instanciar un nuevo fragment
    @Override
    protected Fragment createFragment() {return new CrimeListFragment();}

    //Metodo para devolver un layout
    @Override
    protected int getLayoutResId() {return R.layout.activity_masterdetail;}

    //Metodo para reemplazar un fragment o insertar uno nuevo
    @Override
    public void onCrimeSelected(Crime crime) {

        //Comprobamos si existe un detail_fragment_container
        if(findViewById(R.id.detail_fragment_container) == null){

            //Instanciamos un CrimePagerActivity con un ID
            startActivity(CrimePagerActivity.newIntent(this, crime.getId()));

        }else{

            //Instaciamos un crimefragment con un ID
            Fragment newDetail = CrimeFragment.newInstance(crime.getId());

            //Reemplazamos el anterior por un nuevo fragment
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.detail_fragment_container, newDetail)
                    .commit();

        }

    }

    //Metodo para actualizar un listfragment con otro crime
    @Override
    public void onCrimeUpdated(Crime crime) {

        //Instanciamos un listfragment y le asociamos mediante un manager un ID
        CrimeListFragment listFragment = (CrimeListFragment)getSupportFragmentManager()
                                         .findFragmentById(R.id.fragment_container);

        //Actualizamos su UI
        listFragment.updateUI();

    }

}
