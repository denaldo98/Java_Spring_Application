package com.denapoli.progettoop.service;

import com.denapoli.progettoop.modello.ContributoNazione;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONValue;
import org.json.JSONObject;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Classe che carica il dataset gestendone l'accesso
 */
@Service
public class ContrNazService {
    private final static String COMMA_DELIMITER = ";"; //separatore CSV
    public static List<String> anni=new ArrayList<>(); //predispongo lista di stringhe per controllo campo contributi con anno
    private List<ContributoNazione> contributi = new ArrayList<>();     //lista di istanze della classe modellante
    public Metadata metadata;           //metadata

    /**
     * Costruttore per scaricare il dataset e fare il parsing del csv
     */
    public ContrNazService() {
        for(int i=0;i<ContributoNazione.intervalloAnni;i++)   //riempiamo la lista con gli anni gestiti
            anni.add(Integer.toString(2000+i));
        String fileCSV = "dataset.csv";
        if (Files.exists ( Paths.get ( fileCSV ) )) {       //verifico esistenza del file
            System.out.println ( "Dataset caricato da file locale" );
        } else
            try {
                URLConnection openConnection = new URL ("http://data.europa.eu/euodp/data/api/3/action/package_show?id=V7ZkhAQ536LhqVNfAeGA" ).openConnection (); //apro connessione ad url della mail
                openConnection.addRequestProperty ( "User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.90 Safari/537.36" );
                InputStream in = openConnection.getInputStream ();
                StringBuilder data = new StringBuilder ();
                String line = "";
                try {
                    //lettura JSON e salvataggio su stringa
                    InputStreamReader inR = new InputStreamReader ( in );
                    BufferedReader buf = new BufferedReader ( inR );
                    while ((line = buf.readLine ()) != null) { //basterebbe anche una sola lettura poichè il JSON è su una sola riga
                        data.append ( line );
                    }
                } finally {
                    in.close ();
                }
                //conversione StringBuilder in oggetto JSON
                JSONObject obj = (JSONObject) JSONValue.parseWithException ( data.toString () );
                JSONObject objI = (JSONObject) (obj.get ( "result" ));
                JSONArray objA = (JSONArray) (objI.get ( "resources" ));

                for (Object o : objA) { //scorro tutti gli oggetti fino a trovare quello di formato corretto
                    if (o instanceof JSONObject) {
                        JSONObject o1 = (JSONObject) o;
                        String format = (String) o1.get ( "format" ); //mi sposto all'interno del JSON per trovare l'url desiderato
                        String urlD = (String) o1.get ( "url" );

                        if (format.equals ( "http://publications.europa.eu/resource/authority/file-type/CSV" )) { //verifico che il formato sia quello desiderato
                            download ( urlD, fileCSV ); //effettuo il download
                        }
                    }
                }
                System.out.println ( "Effettuato download CSV" );
            } catch (Exception e) {
                e.printStackTrace ();
            }
        parsing ( fileCSV ); //effettuo il parsing
        metadata = new Metadata (); //costruttore classe Metadata->generiamo i metadati in modo da restituirli quando richiesto
    }


    /**
     * Metodo che effettua il download del CSV su file locale
     *
     * @param url url del CSV
     * @param fileName nome del file su cui salvare il dataset
     * @throws Exception eccezione download
     */
    private static void download(String url, String fileName) throws Exception {
            try ( InputStream in = URI.create ( url ).toURL ().openStream () ) {
                Files.copy ( in, Paths.get ( fileName ) );
            }
        }

    /**
     * Metodo che effettua il parsing del CSV
     *
     * @param fileCSV file del quale effettuare il parsing
     */
    private void parsing(String fileCSV) {
        try ( BufferedReader bffr = new BufferedReader ( new FileReader ( fileCSV ) ) ) { //inizializzo buffer per il parsing
            bffr.readLine (); // salto la prima riga poichè contiene intestazione
            String riga;
            while ((riga = bffr.readLine ()) != null) {    // leggo ogni riga del file
                riga = riga.replace ( ",", COMMA_DELIMITER ); //sostituisco le virgole con ; che utilizzerò come separatore
                String[] rigaSeparata = riga.trim ().split ( COMMA_DELIMITER ); //uso split per dividere la riga in corrispondenza dei separatori, con trim elimino i caratteri non visibili
                // prendiamo i valori dei singoli campi dalla riga
                char freq = rigaSeparata[0].trim ().charAt ( 0 );//freq è di tipo char
                String geo = rigaSeparata[1].trim ();
                String unit = rigaSeparata[2].trim ();
                String aid_instr = rigaSeparata[3].trim ();
                double[] contributo = new double[ContributoNazione.intervalloAnni];
                for (int i = 0; i < ContributoNazione.intervalloAnni; i++) {
                    contributo[i] = Double.parseDouble ( rigaSeparata[4 + i].trim () );
                }
                // prendendo i valori ottenuti dal parsing, creo un nuovo oggetto e lo inserisco nella lista
                ContributoNazione nuova = new ContributoNazione ( freq, geo, unit, aid_instr, contributo );
                contributi.add ( nuova ); //inserimento in lista
            }
        }
        catch (IOException e) {
            e.printStackTrace ();
        }
    }

     /**
     * Restituisce il dataset completo
     *
     * @return tutta la lista di oggetti
     */
    public List getData(){
        return contributi;
    }

    /**
     * Restituisce gli anni gestiti
     *
     * @return la lista di anni gestiti
     */
    public List getAnni (){
        return anni;
    }
    /**
     * Restituisce l'oggetto che corrisponde all'indice passato
     *
     * @param n indice dell'oggetto richiesto
     * @return l'oggetto corrispondente al valore di indice n
     */
    public ContributoNazione getContrNaz(int n) {//restituisce il contributo n-esimo
        if (n < contributi.size()) return contributi.get(n); //controllo che l'indice non superi la dimensione della lista
        throw new ResponseStatusException ( HttpStatus.BAD_REQUEST, "Oggetto di indice " + n + " non esiste!");
    }

    /**
     * Restituisce le statistiche relative ad un certo campo
     *
     * @param fieldName nome del campo
     * @return Map contenente le statistiche
     */
    public Map getStatistiche(String fieldName) {
        return  Statistiche.getTutteStatistiche(fieldName, getValoriCampo (fieldName));
    }

    /**
     * Restituisce le statistiche relative a tutti i campi
     *
     * @return lista di mappe contenenti le statistiche relative ad ogni campo
     */
    public List<Map> getStatistiche() {
        Field[] fields = ContributoNazione.class.getDeclaredFields();// otteniamo l'elenco di tutti gli attributi della classe
        List<Map> list = new ArrayList<>(); //inizializzo lista di mappe che conterrà le statistiche
        for (Field f : fields) {
            String fieldName = f.getName();//f è l'oggetto di tipo fieldsName estrae il nome del campo corrente
            if(fieldName.equals("contributo")) //gestione vettore di double contributo
                for( int i=0; i<ContributoNazione.intervalloAnni; i++)
                    list.add(getStatistiche(Integer.toString(2000+i) ));
                else list.add(getStatistiche(fieldName));//va ad aggiungere alla lista  la mappa che contiene le statistiche del campo fieldName

        }
        return list;
    }

    /**
     * Metodo che estrae dalla lista di oggetti la lista dei valori relativi ad un singolo campo del dataset
     *
     * @param nomeCampo campo del dataset del quale estrarre i valori(eventuale anno se si vuole contributo)
     * @return lista dei valori del campo richiesto
     */
    private List getValoriCampo(String nomeCampo) {
        List<Object> values = new ArrayList<>(); //inizializzo lista che conterrà i valori del campo
        try {
            //caso in cui nomeCampo sia un anno: verifico che sia uno degli anni gestiti
            if(anni.contains(nomeCampo)){
                for(ContributoNazione contr : contributi){
                    Object value= contr.getContributo()[Integer.parseInt(nomeCampo)-2000]; //considero solo l'elemento che mi interessa del metodo get
                    values.add(value);
                }
            }
            //caso in cui nomeCampo non sia un anno
            else {
                //serve per scorrere tutti gli oggetti ed estrarre i valori del campo nomeCampo
                for (ContributoNazione contr : contributi) {
                    Method getter = ContributoNazione.class.getMethod("get" + nomeCampo.substring(0, 1).toUpperCase() + nomeCampo.substring(1)); //costruisco il metodo get del modello di riferimento
                    Object value = getter.invoke(contr); //invoco il metodo get sull'oggetto della classe modellante
                    values.add(value); //aggiungo il valore alla lista
                }

            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Il campo '" + nomeCampo + "' non esiste!");
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return values; //ritorno la lista
    }

    /**
     * Restituisce una lista formata dagli oggetti che soddisfano il filtro
     *
     * @param nomeCampo campo da filtrare
     * @param oper  operatore di confronto
     * @param rif valore di riferimento
     * @return lista di oggetti che soddisfano il filtro
     */
    public List<ContributoNazione> getDatiFiltrati(String nomeCampo, String oper, Object rif) {
        List<Integer> filtrati = Filtri.filtra( getValoriCampo (nomeCampo), oper, rif);    //applico il filtro alla lista di valori rispetto al campo nomeCampo
        List<ContributoNazione> risultatoFiltro = new ArrayList<>(); //inizializzo lista che conterrà i dati filtrati
        //aggiungo alla lista solo gli oggetti che soddisfano le specifiche del filtro attraverso gli indici
        for (int i : filtrati) {
            risultatoFiltro.add(contributi.get(i));
        }
        return risultatoFiltro;
    }

    /**
     * Metodo che, applicando un filtro, restituisce le statistiche relative a tutti i campi
     *
     * @param campoFiltro campo sul quale si applica il filtro
     * @param oper operatore di confronto
     * @param rif valore di riferimento
     * @return lista delle mappe contenenti le statistiche di ogni campo
     */
    public List<Map> getStatisticheFiltrate(String campoFiltro, String oper, Object rif) {
        Field[] fields = ContributoNazione.class.getDeclaredFields ();// ottengo l'elenco di tutti gli attributi della classe modellante
        List<Map> lista = new ArrayList<> (); //inizializzo la lista delle mappe
        for (Field f : fields) {
            String nomeCampo = f.getName ();//f è l'oggetto di tipo field, con getName si estrae il nome del campo corrente
            if(nomeCampo.equals ("contributo")) //gestione campo contributo con anni
                for(int i=0; i<ContributoNazione.intervalloAnni; i++)
                    lista.add( getStatisticheFiltrate ( Integer.toString(i+2000), campoFiltro, oper, rif )); //calcolo statistiche per ogni anno e aggiungo la mappa alla lista
                else //gestione altri campi
                lista.add ( getStatisticheFiltrate ( nomeCampo, campoFiltro, oper, rif ) );//calcolo statistiche per altri campo e aggiungo la mappa alla lista
        }
        return lista; //ritorno la lista di mappe ottenuta
    }
        /**
     * Metodo che, applicando un filtro, restituisce le statistiche relative ad un campo
     *
     * @param campoStatistiche campo del quale si richiedono le statistiche
     * @param campoFiltro campo sul quale si applica il filtro
     * @param oper operatore di confronto
     * @param rif valore di riferimento
     * @return Mappa con le statistiche sul campo passato come primo parametro applicate solo agli elementi che soddisfano il filtro
     */
    public Map getStatisticheFiltrate(String campoStatistiche, String campoFiltro, String oper, Object rif) {
        List<Integer> indici = Filtri.filtra( getValoriCampo (campoFiltro), oper, rif); //indici degli elementi che soddisfano il filtro
        List valoriCampo = getValoriCampo (campoStatistiche); //lista dei valori del campo rispetto al quale calcolare le statistiche
        List<Object> filtrati = new ArrayList<>(); //inizializzo lista che conterrà gli elementi filtrati
        for (int i : indici) {
            filtrati.add(valoriCampo.get(i)); // aggiungo alla lista gli elementi che soddisfano il filtro
        }
        return Statistiche.getTutteStatistiche (campoStatistiche, filtrati); //calcolo e ritorno le statistiche
    }

}

