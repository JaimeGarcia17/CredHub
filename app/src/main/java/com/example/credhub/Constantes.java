package com.example.credhub;

/**
 * Created by Jaime García on 08,febrero,2019
 */

public class Constantes {

    public static final String TABLE_NAME = "credenciales";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";

    public static final String CREATE_TABLE = "CREATE TABLE " +TABLE_NAME+ " ("
            +COLUMN_ID+" TEXT, "+COLUMN_USERNAME+" TEXT,"+COLUMN_PASSWORD+ " TEXT)";
    public static final String DROP_TABLE = "DROP TABLE IF EXISTS credenciales";
    public static final String FIND_ALL = "SELECT * FROM " + Constantes.TABLE_NAME+ " ORDER BY id ASC";

    public static final int OK = 0;
    public static final int EMPTY = -1;

    public static final String LOG_DB= "[DB]";
    public static final String LOG_DB_ERROR= "[DB ERROR]";
    public static final String LOG_PASSWORD = "[PASSWORD]";
    public static final String LOG_PASSWORD_ERROR = "[PASSWORD ERROR]";

    public static final int MIN = 33; /* ! */
    public static final int MAX = 126; /* ~ */
    public static final int LENGTH= 10;

    /*public static final String PREFERENCES = "SharedPreferences";*/

}
