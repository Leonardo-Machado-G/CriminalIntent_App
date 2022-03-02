package com.jcarlosprofesor.criminalintent.database;

//Clase contenedora de constantes que definen nombres de URI, tablas y columnas
public class CrimeDbSchema {

    //Clase interna que define la estructura de nuestra tabla
    public static final class CrimeTable{

        //Especificamos el nombre de la table dentro de la BD
        public static final String NAME = "crimes";

        //Describimos las columnas que forman parte de la tabla
        public static final class Cols{

            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String SOLVED = "solved";
            public static final String SUSPECT = "suspect";

        }

    }

}
