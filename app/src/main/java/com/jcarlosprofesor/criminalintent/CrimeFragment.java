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
    private EditText mSuspectField;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private Button mTimeButton;

    //Declaro una variable para la ruta de la foto
    private File mPhotoFile;

    //Creamos la variable que implementara la interface
    private Callbacks mCallbacks;

    /*Interface requerida para las activity que quieran albergar un fragment.*/
    public interface Callbacks{
        void onCrimeUpdated(Crime crime);
    }

    //Instancio constantes para el intercambio de datos
    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";

    //Constante para el código de petición
    private static final int REQUEST_DATE = 0;

    //Constante para el código de peticion del contacto
    private static final int REQUEST_CONTACT = 1;

    //Constante para la peticion de tomar una foto
    private static final int REQUEST_PHOTO = 2;

    //Constante para la peticion del timepicker
    private static final int REQUEST_TIME = 3;

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
        this.mTimeButton = (Button) view.findViewById(R.id.crime_time);
        this.mSuspectField = (EditText) view.findViewById(R.id.edit_text_suspect);


        //Obtengo la informacion del crime
        this.mTitleField.setText(this.mCrime.getTitle());
        this.mDateButton.setText(this.mCrime.getDate().toString());
        this.mSolvedCheckBox.setChecked(this.mCrime.isSolved());
        this.mCrime.setSuspect(this.mCrime.getSuspect() != null ? this.mCrime.getSuspect(): null);
        this.mSuspectField.setText(this.mCrime.getSuspect());
        this.mTimeButton.setText("Time: " + this.mCrime.getDate().getHours() + ":" + this.mCrime.getDate().getMinutes());

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
                //MediaStore provee una coleccion indexada de medios como audio, imagenes
                //Contiene definiciones para URI es el contrato entre el proveedor de medios y las aplicaciones
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT,uri);

                //Informacion que es devuelta de un intent, mediante un intentfilter
                List<ResolveInfo> cameraActivities =
                        getActivity()
                        .getPackageManager()//PackManager obtiene el tipo de informacion relacionada con los paquete instalados
                        .queryIntentActivities( //Query obtiene todas las actividades que puedan realizar el intent
                                captureImage, //Lo asocio el intent
                                PackageManager.MATCH_DEFAULT_ONLY); //Establezco un flag para un package

                //Recorro la lista de resolveinfo
                for(ResolveInfo activity : cameraActivities){

                    getActivity() //Contexto
                    .grantUriPermission( //Otorgo permiso temporal a un URI
                            activity.activityInfo.packageName, //Obtengo el nombre del paquete del resolveinfo
                            uri, //Asocio el fileprovider
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION); //Flag de permiso de escritura

                }

                //Envio la informacion del intent y la variable asociada
                startActivityForResult(captureImage,REQUEST_PHOTO);

            }

        });
        this.mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            //Metodo que se ejecuta cuando cambia el valor del edittext
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
                updateCrime();
            }

            @Override
            public void afterTextChanged(Editable s) {}

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

                //Instancio un intent con la accion send
                Intent intent = new Intent(Intent.ACTION_SEND);

                //Defino un tipo de dato MIME
                intent.setType("text/plain");

                //Inserto el reporte en el texto
                intent.putExtra(Intent.EXTRA_TEXT, getCrimeReport());

                //Inserto el sujeto
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));

                //Creamos un chooser para que aparezcan opciones de eleccion
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
        this.mSuspectField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            //Metodo para detectar el cambio del widget
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                //Establezco el valor del sospechoso
                mCrime.setSuspect(charSequence.toString());

            }

            @Override
            public void afterTextChanged(Editable editable) {}

        });
        this.mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Instancio un fragmentmanager  para administrar los fragment
                FragmentManager fragmentManager = getParentFragmentManager();

                //Defino un nuevo fragment que sera un dialogo para timepicker
                TimerPickerFragment dialog = TimerPickerFragment.newIntance(mCrime.getDate());

                //Define la relación llamador / llamado entre 2 fragmentos
                dialog.setTargetFragment(CrimeFragment.this,REQUEST_TIME);

                //Llamamos al metodo para crear el dialog y mostrarlo
                dialog.show(fragmentManager,DIALOG_TIME);

            }

        });

        //Retorno la view
        return view;

    }

    //Sobreescribimos el método onActivityResult para recuperar el extra, fijamos la fecha en el objeto Crime
    //y actualizamos el texto del boton
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        //Si el codigo revuelto es distinto a result volvermos
        if(resultCode != Activity.RESULT_OK){
            return;
        }

        //Si resultcode vale 0 insertamos una nueva fecha al crimen y al button
        if (requestCode == REQUEST_DATE){

            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            this.mCrime.setDate(date);
            this.mDateButton.setText(this.mCrime.getDate().toString());
            this.mTimeButton.setText("TIME: " + (this.mCrime.getDate().getHours() + ":" + (this.mCrime.getDate().getMinutes())));

        } else if (requestCode == REQUEST_TIME){

            //Establezco una nueva hora para ambos buttons y actualizo la fecha del crime
            Date time = (Date) data.getSerializableExtra(TimerPickerFragment.EXTRA_TIME);
            this.mCrime.setDate(time);
            this.mDateButton.setText(this.mCrime.getDate().toString());
            this.mTimeButton.setText("TIME: " + (this.mCrime.getDate().getHours() + ":" + (this.mCrime.getDate().getMinutes())));

        }

        //Añadimos el tratamiento del resultado devuelto por la app Contactos
        else if (requestCode == REQUEST_CONTACT && data != null){

            //Instancio un Uri y obtengo la fecha
            Uri contactUri = data.getData();

            //Especificamos el campo para el que queremos sus valores
            String[] queryFields = new String[]{
                    ContactsContract.Contacts.DISPLAY_NAME
            };

            //Ejecutamos la consulta
            Cursor c = getActivity().getContentResolver().query(
                    contactUri,          //Asociamos el Uri con la fecha
                    queryFields,         //Asociamos la constante
                    null,
                    null,
                    null);

            try{

                //Comprobamos que hemos obtenidos resultados
                if(c.getCount() == 0){ return;}

                //Desplazamos el cursor al principio
                c.moveToFirst();

                //Insertamos el nombre del sospechoso
                String suspect = c.getString(0);

                //Cambiamos los datos del crime
                this.mCrime.setSuspect(suspect);

                //Llamamos al metodo para actualizar el crime
                updateCrime();

            }finally {

                //Cerramos el cursor
                c.close();

            }

        //Añadimos el tratamiento de la foto
        } else if (requestCode == REQUEST_PHOTO){

            //Obtengo el file provider mediante el standard RFC 2396 y le asocio el archivo
            Uri uri = FileProvider.getUriForFile(getActivity(),
                    "com.jcarlosprofesor.criminalintent.fileprovider",
                    this.mPhotoFile);

            //Quitamos los permisos del provider de escritura
            getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            //Actualizamos el crime
            updateCrime();

            //Actulizamos la foto
            updatePhotoView();

        }

    }

    //Metodo que se ejecuta cuando la aplicacion entra en segundo plano
    @Override
    public void onPause() {
        super.onPause();

        //Actualizamos el crime
        CrimeLab.get(getActivity()).updateCrime(mCrime);

    }

    //Metodo que nos va a construir el informe de un crime en ejecucion
    private String getCrimeReport(){

        //Si el crime esta resuelto obtengo un string u otro
        String solvedString = this.mCrime.isSolved() ?
                getString(R.string.crime_report_solved) :
                getString(R.string.crime_report_unsolved);

        //Defino un tipo de fecha
        String dateString = DateFormat.format( "EEE MMM dd",mCrime.getDate()).toString();

        //Obtengo el sospechoso
        String suspect = mCrime.getSuspect();

        //Si es nulo obtengo uno u otro string
        suspect = suspect == null ?
                getString(R.string.crime_report_no_suspect) :
                getString(R.string.crime_report_suspect, suspect);

        //Retorno un string con la informacion del reporte
        return getString(R.string.crime_report,
                this.mCrime.getTitle(),dateString,solvedString,suspect);

    }

    //Metodo para cargar el objeto Bitmap en el ImageView
    private void updatePhotoView(){

        //Si el archivo es nulo o si no existe quitamos el contenido foto
        if(this.mPhotoFile == null || !this.mPhotoFile.exists()){
            this.mPhotoView.setImageDrawable(null);
        }else{

            //Instanciamos un bitmap y escalamos la imagen segun la ruta del archivo
            Bitmap bitmap = PictureUtils.getScaledBitmap(
                    this.mPhotoFile.getPath(),
                    getActivity());

            //Asociamos el bitmap a la view
            this.mPhotoView.setImageBitmap(bitmap);

        }

    }

    //Metodo para actualizar el crime
    private void updateCrime(){
        CrimeLab.get(getActivity()).updateCrime(this.mCrime);

        //Envio el crime mediante la interfaz
        this.mCallbacks.onCrimeUpdated(this.mCrime);
    }

}
