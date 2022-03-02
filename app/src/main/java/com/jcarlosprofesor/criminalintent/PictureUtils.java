package com.jcarlosprofesor.criminalintent;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

//Clase para administrar imagenes
public class PictureUtils {

    //Metodo para estimar el tamaño del PhotoView
    //Comprueba el tamaño de pantalla y reduce el de la imagen hasta que coinciden
    public static Bitmap getScaledBitmap (String path, Activity activity){

        //Definimos un punto con dos coordenadas
        Point size = new Point();

        activity.getWindowManager()  //Obtenemos un window manager
                .getDefaultDisplay() //Devuelve la pantalla
                .getSize(size);      //Obtenemos el tamaño de la imagen

        //Devolvemos el metodo getscaled con los parametros obtenidos
        return getScaledBitmap(path,size.x,size.y);

    }

    //Metodo para escalar las imagenes
    public static Bitmap getScaledBitmap(String path, int destWidth, int destHeight){

        //Leemos las dimensiones de la imagen en disco
        BitmapFactory.Options options = new BitmapFactory.Options();

        //Al ser verdadero el decodificador es nulo y no devuelve un bitmap
        options.inJustDecodeBounds = true;

        //Decodifica una ruta de archivo en un mapa de bits
        BitmapFactory.decodeFile(path, options);

        //Insertamos las dimensiones
        float scrWidth = options.outWidth;
        float srcHeight = options.outHeight;

        //Averiguamos cuanto reducir el tamaño
        int intSampleSize = 1;

        //Si las dimensiones de options son mayores que las de parametro accedemos
        if(srcHeight > destHeight || scrWidth > destWidth){

            //Creamos dos variables y obtenemos la proporcion entre altura y anchura
            float heightScale = srcHeight / destHeight;
            float widthScale = scrWidth / destWidth;

            //Insertamos cuanto reducir la escala y redondeamos en funcion de si altura > anchura
            intSampleSize = Math.round(heightScale > widthScale ? heightScale: widthScale);

        }

        //Instaciamos un nuevo options
        options = new BitmapFactory.Options();

        //Si el valor es mayor que 1 mostramos una imagen pequeña
        options.inSampleSize = intSampleSize;

        //Volvemos a leerlo y creamos el mapa de bits definitivo
        return BitmapFactory.decodeFile(path,options);

    }

}