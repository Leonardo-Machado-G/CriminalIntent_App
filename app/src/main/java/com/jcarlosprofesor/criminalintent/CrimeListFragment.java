package com.jcarlosprofesor.criminalintent;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Date;
import java.util.List;
public class CrimeListFragment extends Fragment {

    //Defino una interface para
    public interface Callbacks{
        void onCrimeSelected(Crime crime);
    }

    //Declaro los widget necesarios
    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;
    private boolean mSubtitleVisible;

    //Declaro una variable interface para la comunicacion con el fragment
    private Callbacks mCallbacks;

    //Metodo para obtener el contexto una vez se asocia el fragment
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.mCallbacks = (Callbacks) context;
    }

    //Metodo para retirar la interface antes de ser eliminado el fragment
    @Override
    public void onDetach() {
        super.onDetach();
        this.mCallbacks = null;
    }

    //Metodo que es llamado segun el ciclo de vida del fragment
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Indico que tenemos un menu en este fragment
        setHasOptionsMenu(true);
    }

    //Declaro una clase interna privada
    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        //Declaro los widget necesarios
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private ImageView mSolvedImageView;
        private Crime mCrime;

        //Defino un constructor donde serializamos sus componentes
        public CrimeHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.list_item_crime,parent,false));
            this.mTitleTextView = (TextView) itemView.findViewById(R.id.crime_title);
            this.mDateTextView = (TextView) itemView.findViewById(R.id.crime_date);
            this.mSolvedImageView = (ImageView) itemView.findViewById(R.id.crime_solved_img);
            this.itemView.setOnClickListener(this);

        }

        //Metodo que carga el contenido de un crime al ser llamado
        public void bind(Crime crime){
            this.mCrime = crime;
            this.mTitleTextView.setText(mCrime.getTitle());
            this.mDateTextView.setText(mCrime.getDate().toString());
            this.mSolvedImageView.setVisibility(crime.isSolved()?View.VISIBLE:View.GONE);
        }

        //Metodo que se ejecuta al hacer click en la view enviando un crime
        @Override
        public void onClick(View v) {
            mCallbacks.onCrimeSelected(mCrime);
        }

    }

    //Declaro una clase interna privada
    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder>{

        //Declaro una lista de crimes privada
        private List<Crime> mCrimes;

        //Defino un constructor
        public CrimeAdapter(List<Crime> crimes){this.mCrimes = crimes;}

        //Metodo que define la view del holder
        @NonNull
        @Override
        public CrimeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new CrimeHolder(LayoutInflater.from(getActivity()),parent);
        }

        //Metodo que es llamado al estar en una determinada posicion
        @Override
        public void onBindViewHolder(@NonNull CrimeHolder holder, int position) {

            //Llamado al metodo para actualizar el contenido de sus widget
            holder.bind( this.mCrimes.get(position));

        }

        //Metodo que obtiene el tamaño de la lista
        @Override
        public int getItemCount() {return mCrimes.size();}

        //Metodo que reemplaza la lista de crimenes que se muestran
        public void setCrimes (List<Crime> crimes){this.mCrimes = crimes;}

    }

    //Metodo que actualiza la view segun el ciclo de vida del fragment
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Instancio una view y la asocio al fragment
        View view = inflater.inflate(R.layout.fragment_crime_list,container,false);

        //Serializo la recyclerview
        this.mCrimeRecyclerView = (RecyclerView) view.findViewById(R.id.crime_recycler_view);

        //Indico el recyclerview como se van a disponer las view
        this.mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Obtengo un crimelab
        CrimeLab crimeLab = CrimeLab.get(getActivity());

        //Mediante el crimelab relleno la lista
        List<Crime> crimes = crimeLab.getCrimes();

        //Inserto en el adapter los crimes
        this.mAdapter = new CrimeAdapter(crimes);

        //Asociamos el adapter a la recyclerview
        this.mCrimeRecyclerView.setAdapter(mAdapter);
        return view;

    }

    //Metodo que es llamado al salir el fragment de segundo plano
    public void onResume(){
        super.onResume();
        updateUI();
    }

    //Metodo que actualiza nuestra UI
    public void updateUI(){

        //Instancio un crimelab
        CrimeLab crimeLab = CrimeLab.get(getActivity());

        //Instancio una lista de crimes y obtengo los del crimelab
        List<Crime> crimes = crimeLab.getCrimes();

        //Si el adapter es nulo accedemos
        if(this.mAdapter == null){

            //Creamos un nuevo adapter y le insertamos los crimes
            this.mAdapter = new CrimeAdapter(crimes);

            //Asociamos el adapter a la recyclerview
            this.mCrimeRecyclerView.setAdapter(mAdapter);

        }else{

            //Actualizamos la lista que debe usar el adaptador para mostrar los crimenes
            this.mAdapter.setCrimes(crimes);

            //Notificamos que ha habido un cambio en los datos
            this.mAdapter.notifyDataSetChanged();

        }

    }

    //Inyectamos el archivo de layout del menu, en el objeto menu
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        //Asociamos el menu al fragment
        inflater.inflate(R.menu.fragment_crime_list,menu);

        //Actualizamos el menu
        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);

        //Si los subtitulos estan invisbles muestra un titulo u otro
        subtitleItem = this.mSubtitleVisible ?
                subtitleItem.setTitle(R.string.hide_subtitle):
                subtitleItem.setTitle(R.string.show_subtitle);

    }

    //Metodo que define las opciones del menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        //Declaramos un switch y obtenemos la ID del item pulsado
        switch (item.getItemId()){

            //Si el ID es newcrime
            case R.id.new_crime:

                //Instanciamos un crime
                Crime crime = new Crime();

                //Añadimos el crime
                CrimeLab.get(getActivity()).addCrime(crime);

                //Actualizamos la UI
                updateUI();

                //Enviamos el crime mediante la interfaz
                mCallbacks.onCrimeSelected(crime);
                return true;


            //Si el id es show subtitle
            case R.id.show_subtitle:

                //Invertiamos el valor de la variable
                this.mSubtitleVisible = !this.mSubtitleVisible;

                //Declaramos un cambio en el menu
                getActivity().invalidateOptionsMenu();

                //Actualizamos los subtitulos
                updateSubtitle();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }

    //Metodo para actualizar el subtitulo
    private void updateSubtitle(){

        //Obtenemos el tamaño de lai
        int crimeCount = CrimeLab.get(getActivity()).getCrimes().size();

        ((AppCompatActivity) getActivity()) //Obtenemos la activity
                .getSupportActionBar()      //Obtenemos el menu
                .setSubtitle(               //Indicamos el valor del subtitulo
                !mSubtitleVisible ?         //Si esta visible le cambiamos el valro sino, sera nulo
                null :
                getString(R.string.subtitle_format,crimeCount));

    }
}
