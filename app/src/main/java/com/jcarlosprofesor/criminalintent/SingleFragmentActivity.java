package com.jcarlosprofesor.criminalintent;
import android.os.Bundle;
import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
public abstract  class SingleFragmentActivity extends AppCompatActivity {

    //Definimos un metodo abstracto para heredar
    protected abstract Fragment createFragment();

    //Creamos un metodo que permita obtener el archivo de layout a usar
    @LayoutRes
    protected int getLayoutResId(){return R.layout.activity_fragment;}

    //Metodo que pertenece al ciclo de vida de una activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Para especificar el archivo de layout a inyectar llamamos al metodo
        setContentView(getLayoutResId());

        //Instancio un fragmentManager
        FragmentManager fragmentManager = getSupportFragmentManager();

        //Serializo el fragment
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);

        //Si el fragment no existe accedo
        if(fragment == null){

            //Instancio el fragment segun el metodo abstracto
            fragment = createFragment();

            //Mediante el manager añado el fragment a la view
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container,fragment)
                    .commit();

        }

    }

}
