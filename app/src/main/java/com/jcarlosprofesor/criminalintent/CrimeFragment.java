package com.jcarlosprofesor.criminalintent;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;
public class CrimeFragment extends Fragment {

    //Declaro los widgets necesarios
    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private Button mReportButton;
    private Button mSuspectButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;

    //Declaro una variable para la ruta de la foto
    private File mPhotoFile;

    //Creamos la variable que implementara la interface
    private Callbacks mCallbacks;

    /*Interface requerida para las activity que quieran albergar un fragment.*/
    public interface Callbacks{

        void onCrimeUpdated(Crime crime);

    }

    //Instancio constantes para el DataPickerFragment y el ID
    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";

    //Constante para el código de petición
    private static final int REQUEST_DATE = 0;

    //Constante para el código de peticion del contacto
    private static final int REQUEST_CONTACT = 1;

    //Constante para la peticion de tomar una foto
    private static final int REQUEST_PHOTO = 2;

    //Metodo para obtener el contexto tras introducir el fragment
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.mCallbacks = (Callbacks) context;
    }

    //Metodo ejecutado para desligar el fragment de la activity
    @Override
    public void onDetach() {
        super.onDetach();
        this.mCallbacks = null;
    }

    //Metodo para instanciar un fragment con un ID
    public static CrimeFragment newInstance(UUID crimeId){
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID,crimeId);
        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    //Metodo que se ejecuta segun el ciclo de vida de una fragment
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Instancio un ID obteniendo el ID del intent
        UUID crimeId = (UUID)getArguments().getSerializable(ARG_CRIME_ID);
        this.mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);

        //Instanciamos la variable para la ruta de la foto
        this.mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);

    }

    //Metodo que se ejecuta segun el ciclo de vida de una fragment
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Instancio una view y la asocio a un layout
        View view = inflater.inflate(R.layout.fragment_crime, container,false);

        //Serializamos los widget
        this.mPhotoView = (ImageView)view.findViewById(R.id.crime_photo);
        this.mPhotoButton = (ImageButton)view.findViewById(R.id.crime_camera);
        this.mTitleField = (EditText) view.findViewById(R.id.crime_title);
        this.mDateButton = (Button) view.findViewById(R.id.crime_date);
        this.mSolvedCheckBox = (CheckBox) view.findViewById(R.id.crime_solved);
        this.mReportButton = (Button) view.findViewById(R.id.crime_report);
        this.mSuspectButton = (Button) view.findViewById(R.id.crime_suspect);

        //Obtengo la informacion del crime
        this.mTitleField.setText(this.mCrime.getTitle());
        this.mDateButton.setText(this.mCrime.getDate().toString());
        this.mSolvedCheckBox.setChecked(this.mCrime.isSolved());
        this.mCrime.setSuspect(this.mCrime.getSuspect() != null ? this.mCrime.getSuspect(): null);

        //Invocamos el metodo para cargar la imagen
        updatePhotoView();

        //Creamos el intent que da tratamiento a la toma de la foto
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //Codigo para dar comportamiento al boton
        final Intent pickContact = new Intent(Intent.ACTION_PICK,ContactsContract.Contacts.CONTENT_URI);

        //Defino los comportamientos de los widget mediante listener
        this.mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Obtengo el file provider mediante el standard RFC 2396 y le asocio el archivo
                Uri uri = FileProvider.getUriForFile(getActivity(),
                        "com.jcarlosprofesor.criminalintent.fileprovider",
                        mPhotoFile);

                //Inserto en el intent el provider
                //MediaStore provee una coleccion indexada de medios como audio, imagenes...
                //Contiene definiciones para URI es el contrato entre el proveedor de medios y las aplicaciones
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT,uri);

                //Informacion que es devuelta de un intent, mediante un intentfilter
                List<ResolveInfo> cameraActivities =
                        getActivity()
                        .getPackageManager()//PackManager obtiene el tipo de informacion relacionada con los paquete instalados
                        .queryIntentActivities( //Query obtiene todas las actividades que puedan realizar el intent
                                captureImage,
                                PackageManager.MATCH_DEFAULT_ONLY); //Establezco un flag para un package

                //Recorro la lista de resolveinfo
                for(ResolveInfo activity : cameraActivities){

                    getActivity() //Contexto
                    .grantUriPermission( //Otorgo permiso temporal a un URI
                            activity.activityInfo.packageName, //Obtengo el nombre del paquete del resolveinfo
                            uri,
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION); //Flag de permiso de escritura

                }

                //Envio la informacion del intent y la variable asociada
                startActivityForResult(captureImage,REQUEST_PHOTO);

            }

        });
        this.mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            //Metodo que se ejecuta cuando cambia el valor del edittext
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
                updateCrime();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });
        this.mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Instancio un fragmentmanager del padre
                FragmentManager fragmentManager = getParentFragmentManager();

                //Instancio un datepicker mediante la fecha del crime
                DatePickerFragment dialog = DatePickerFragment.newInstante(mCrime.getDate());

                //Establecemos el fragment destino
                dialog.setTargetFragment(CrimeFragment.this,REQUEST_DATE);

                //Muestro el datepicker
                dialog.show(fragmentManager,DIALOG_DATE);
            }

        });
        this.mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                //Cambio la variable del crime y lo actualizo
                mCrime.setSolved(isChecked);
                updateCrime();
            }

        });
        this.mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                intent.putExtra(Intent.EXTRA_SUBJECT,
                        getString(R.string.crime_report_subject));
                //Creamos un chooser para asegurarnos que el usuario usa
                //siempre le aparezcan opciones de eleccion
                intent = Intent.createChooser(intent,getString(R.string.send_report));
                startActivity(intent);
            }
        });
        this.mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Envio el intent y el valor de su variable asociada
                startActivityForResult(pickContact,REQUEST_CONTACT);

            }

        });

        //Retorno la view
        return view;

    }

    //Sobreescribimos el método onActivityResult para recuperar el extra, fijamos la fecha en el objeto Crime
    //y actualizamos el texto del boton
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        //
        if(resultCode != Activity.RESULT_OK){
            return;
        }

        //
        if (requestCode == REQUEST_DATE){

            //
            this.mCrime.setDate((Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE));

            //
            updateCrime();

            //
            this.mDateButton.setText(mCrime.getDate().toString());

        }

        //añadimos el tratamiento del resultado devuelto por la app Contactos
        else if (requestCode == REQUEST_CONTACT && data != null){

            //
            Uri contactUri = data.getData();

            //Especificamos el campo para el que queremos que la consulta
            //devuelva valores
            String[] queryFields = new String[]{
                    ContactsContract.Contacts.DISPLAY_NAME
            };

            //Ejecutamos la consulta
            Cursor c = getActivity().getContentResolver().query(
                    contactUri,
                    queryFields,
                    null,
                    null,
                    null);

            try{

                //Comprobamos que hemos obtenidos resultados
                if(c.getCount() == 0){ return;}

                //Extraemos la primera columna
                //Es el nombre del sospechoso
                c.moveToFirst();

                //
                String suspect = c.getString(0);

                //
                this.mCrime.setSuspect(suspect);

                //
                updateCrime();

                //
                this.mSuspectButton.setText(suspect);

            }finally {

                //
                c.close();

            }

        //
        } else if (requestCode == REQUEST_PHOTO){

            //
            Uri uri = FileProvider.getUriForFile(getActivity(),
                    "com.jcarlosprofesor.criminalintent.fileprovider",
                    this.mPhotoFile);

            //
            getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            //
            updateCrime();

            //
            updatePhotoView();

        }

    }

    //Sobreescribimos el metodo onPause para asegurarnos que las instancias
    //modificadas de Crime son guardadas antes de que CrimeFragment finalice

    @Override
    public void onPause() {
        super.onPause();
        //Llamamos al método que hemos implemantado en CrimeLab para actualizar
        //un crimen
        CrimeLab.get(getActivity())
                .updateCrime(mCrime);
    }

    //metodo que nos va a construir el informe de un crime concreto en ejecucion
    private String getCrimeReport(){
        String solvedString = null;
        if(this.mCrime.isSolved()){
            solvedString = getString(R.string.crime_report_solved);
        }
        else{
            solvedString = getString(R.string.crime_report_unsolved);
        }
        String dateFormat = "EEE MMM dd";
        String dateString = DateFormat.format(dateFormat,mCrime.getDate()).toString();

        String suspect = mCrime.getSuspect();
        if(suspect == null){
            suspect = getString(R.string.crime_report_no_suspect);
        }else{
            suspect = getString(R.string.crime_report_suspect, suspect);
        }
        String report = getString(R.string.crime_report,
                mCrime.getTitle(),dateString,solvedString,suspect);
        return report;
    }

    //metodo para cargat el objeto Bitmap en el ImageView
    private void updatePhotoView(){

        //
        if(this.mPhotoFile == null || !this.mPhotoFile.exists()){

            mPhotoView.setImageDrawable(null);

        }else{

            //
            Bitmap bitmap = PictureUtils.getScaledBitmap(
                    this.mPhotoFile.getPath(), getActivity());
            this.mPhotoView.setImageBitmap(bitmap);
        }
    }

    //
    private void updateCrime(){

        CrimeLab.get(getActivity()).updateCrime(this.mCrime);
        this.mCallbacks.onCrimeUpdated(this.mCrime);

    }
}
