package com.example.credhub;

import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Vector;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.ksoap2.HeaderProperty;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

/**
 * Created by Jaime García on 13,febrero,2019
 */
public class EndPoint {

    private static HttpTransportSE androidHttpTransport;
    private static List<HeaderProperty> headerList_basicAuth;
    private static final String WS_NAMESPACE = "http://sdm_webrepo/";
    private static final String WS_METHOD_LIST = "ListCredentials";
    private static final String WS_METHOD_IMPORT = "ImportRecord";
    private static final String WS_METHOD_EXPORT = "ExportRecord";

    String BASIC_AUTH_USERNAME = InicioSesion.usernameLogin;
    String BASIC_AUTH_PASSWORD = InicioSesion.passwordLogin;

    public void establecerConexion( String[] args ) {

        try {
            // Establish test code behaviour
            boolean USE_HTTPS;
            boolean USE_BASIC_AUTH;

            URL urlWebService;

            if (args.length == 1 && args[0].toLowerCase().equals("http")) // HTTP
            {
                USE_HTTPS = false;
                USE_BASIC_AUTH = false;
            } else if (args.length == 1 && args[0].toLowerCase().equals("http+auth")) // HTTP + basic authentication
            {
                USE_HTTPS = false;
                USE_BASIC_AUTH = true;
            } else if (args.length == 1 && args[0].toLowerCase().equals("https+auth")) // HTTPS + basic authentication
            {
                USE_HTTPS = true;
                USE_BASIC_AUTH = true;
            } else // Show help
            {
                System.out.println("Usage: \n\tSDM_WebRepo_ClientTest http \t\t(HTTP server)" +
                        "\n\tSDM_WebRepo_ClientTest http+auth \t(HTTP server + basic authentication)" +
                        "\n\tSDM_WebRepo_ClientTest https+auth \t(HTTPS server + basic authentication)");

                return;
            }

            // Set webservice url and TLS parameters
            if (!USE_HTTPS) {
                // Set HTTP URL
                androidHttpTransport = new HttpTransportSE("http://10.0.2.2/SDM/WebRepo?wsdl");
            } else {

                // Initialize TLS context
                SSLContext sc = SSLContext.getInstance("TLSv1.2");
                sc.init(null, null, new java.security.SecureRandom()); // *Set 2nd argument to NULL for default trust managers
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

                // Set HTTPS URL
                androidHttpTransport = new HttpTransportSE("https://10.0.2.2/SDM/WebRepo?wsdl");
            }

            // Activate basic authentication
            if (USE_BASIC_AUTH) {
                headerList_basicAuth = new ArrayList<HeaderProperty>();
                String strUserPass = BASIC_AUTH_USERNAME + ":" + BASIC_AUTH_PASSWORD;
                headerList_basicAuth.add(new HeaderProperty("Authorization", "Basic " + org.kobjects.base64.Base64.encode(strUserPass.getBytes())));
            } else {
                headerList_basicAuth = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public Vector<SoapPrimitive> listarRegistros() {

        try {

            // Read list of all record identifiers stored on the repository
            SoapObject request = new SoapObject(WS_NAMESPACE, WS_METHOD_LIST);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);
            androidHttpTransport.call("\"" + WS_NAMESPACE + WS_METHOD_LIST + "\"", envelope, headerList_basicAuth);
            Vector<SoapPrimitive> listIds = new Vector<SoapPrimitive>();
            if (envelope.getResponse() instanceof Vector) // 2+ elements
                listIds.addAll((Vector<SoapPrimitive>) envelope.getResponse());
            else if (envelope.getResponse() instanceof SoapPrimitive) // 1 element
                listIds.add((SoapPrimitive) envelope.getResponse());
            System.out.println("List of records stored on the repo: ");
            for (int i = 0; i < listIds.size(); i++) {
                System.out.println("- " + listIds.get(i).toString());
            }
            System.out.println();
            return listIds;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    public void exportarRegistro( String id, String username, String password ) {

        try {
            // Export new record
            SoapObject request = new SoapObject(WS_NAMESPACE, WS_METHOD_EXPORT);
            PropertyInfo propId = new PropertyInfo();
            propId.name = "arg0";
            propId.setValue(id);
            propId.type = PropertyInfo.STRING_CLASS;
            request.addProperty(propId);
            PropertyInfo propUser = new PropertyInfo();
            propUser.name = "arg1";
            propUser.setValue(username);
            propUser.type = PropertyInfo.STRING_CLASS;
            request.addProperty(propUser);
            PropertyInfo propPass = new PropertyInfo();
            propPass.name = "arg2";
            propPass.setValue(password);
            propPass.type = PropertyInfo.STRING_CLASS;
            request.addProperty(propPass);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);
            androidHttpTransport.call("\"" + WS_NAMESPACE + WS_METHOD_EXPORT + "\"", envelope, headerList_basicAuth);
            System.out.println("Export result: " + envelope.getResponse().toString());
            System.out.println();
            Log.i("WebService", "Todo OK");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public Vector<SoapPrimitive> importarRegistro( String id ) {

        try {
            Vector<SoapPrimitive> listIds = listarRegistros();
            if (listIds.size() > 0) {

                for (int i = 0; i < listIds.size(); i++) {
                    if (listIds.get(i).getValue().equals(id)) {

                        SoapObject request = new SoapObject(WS_NAMESPACE, WS_METHOD_IMPORT);
                        PropertyInfo propId = new PropertyInfo();
                        propId.name = "arg0";
                        propId.setValue(listIds.get(i).toString());
                        propId.type = PropertyInfo.STRING_CLASS;
                        request.addProperty(propId);
                        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                        envelope.setOutputSoapObject(request);
                        androidHttpTransport.call("\"" + WS_NAMESPACE + WS_METHOD_IMPORT + "\"", envelope, headerList_basicAuth);

                        Vector<SoapPrimitive> importedRecord = (Vector<SoapPrimitive>) envelope.getResponse();
                        if (importedRecord.size() == 3) {

                            System.out.println("Record imported successfully: ");
                            System.out.println("ID: " + importedRecord.get(0));
                            System.out.println("Username: " + importedRecord.get(1));
                            System.out.println("Password: " + importedRecord.get(2));
                            return importedRecord;
                        } else
                            System.out.println("Import error - " + importedRecord.get(0));
                        return null;
                    } else System.out.println("Import aborted - No records found on the repo");
                    System.out.println();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return null;
    }

    public boolean enLinea( String modoComunicacion ) {

        try {

            URL url = new URL("http://10.0.2.2/SDM/WebRepo?wsdl");
            URL urlhttps = new URL("https://10.0.2.2/SDM/WebRepo?wsdl");

            HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
            HttpsURLConnection urlchttps = (HttpsURLConnection) urlhttps.openConnection();

            urlc.setRequestProperty("Connection", "close");
            urlchttps.setRequestProperty("Connection", "close");

            String strUserPass = BASIC_AUTH_USERNAME + ":" + BASIC_AUTH_PASSWORD;
            String basicAuth = "Basic " + new String(org.kobjects.base64.Base64.encode(strUserPass.getBytes()));

            switch (modoComunicacion) {
                case "http":
                    break;
                case "http+auth":
                    urlc.setRequestProperty("Authorization", basicAuth);
                    break;
                case "https+auth":
                    urlchttps.setRequestProperty("Authorization", basicAuth);
                    break;
            }

            if (modoComunicacion.equals("http") || modoComunicacion.equals("http+auth")) {
                urlc.setConnectTimeout(2000);
                urlc.connect();

                if (urlc.getResponseCode() == 200) {
                    return true;
                } else {
                    Log.d("NO INTERNET", "NO INTERNET");
                    return false;
                }
            } else {
                urlchttps.setConnectTimeout(2000);
                urlchttps.connect();

                if (urlchttps.getResponseCode() == 200) {
                    return true;
                } else {
                    Log.d("NO INTERNET", "NO INTERNET");
                    return false;
                }

            }


        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


}
