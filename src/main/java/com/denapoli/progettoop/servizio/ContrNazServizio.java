package com.denapoli.progettoop.servizio;

import com.denapoli.progettoop.modello.ContributoNazione;

import org.springframework.boot.json.BasicJsonParser;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static com.denapoli.progettoop.modello.ContributoNazione.intervalloAnni;


/**
 * Classe che carica il dataset gestendone l'accesso
 */
@Service
public class ContrNazServizio {
    private final static String COMMA_DELIMITER = ";";
    private List<ContributoNazione> contributi = new ArrayList<>();
    public Metadati metadati;
    /**
     * Costruttore per caricare il dataset facendo il parsing del csv
     */
    public ContrNazServizio() {
        String fileSeriale = "dataset.ser";
        if (Files.exists(Paths.get(fileSeriale))) {
            caricaSeriale(fileSeriale);
            System.out.println("Dataset caricato da file seriale");
        } else {
            String url = "http://data.europa.eu/euodp/data/api/3/action/package_show?id=V7ZkhAQ536LhqVNfAeGA"; // url sulla mail
            try {
                parsing(url);
                salvaSeriale(fileSeriale);
                System.out.println("Dataset parsato da remoto e salvato in locale");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
      metadati = new Metadati();
    }

    private void parsing(String colleg) throws IOException {
        // Inizializzazione buffer per il parsing
        BufferedReader bffr = null;
        try {
            URLConnection connessione = new URL(colleg).openConnection();   // avvia la connessione all'url preso come parametro
            connessione.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.90 Safari/537.36"); // aggiungo user-agent
            bffr = new BufferedReader(new InputStreamReader(connessione.getInputStream())); //nuovo buffer per leggere il json ottenuto dell'url
            String json = bffr.readLine();    // leggo dal buffer il json che so trova su una riga e lo salvo su una stringa
            bffr.close();     // chiusura buffer

            Map mappa = new BasicJsonParser().parseMap(json); // passo la stringa del json al parser di Spring che mi restituisce la mappa chiave-valore associata
            // scorro la mappa fino all'URL del file csv
            Map result = (Map) mappa.get("result");   // faccio il casting poiché get restituisce un generico object
            List resources = (List) result.get("resources");
            String csvlink = "";
            // Scorro le risorse fino a trovare quella col formato adatto riportato di seguito, quindi estraggo l'url
            for (Object r : resources) {
                Map mr = (Map) r;
                if (mr.get("format").equals("http://publications.europa.eu/resource/authority/file-type/CSV")) {
                    csvlink = (String) mr.get("url");
                    break;
                }
            }

            URL urlcsv = new URL(csvlink);  // apro connessione all'url
            bffr = new BufferedReader(new InputStreamReader(urlcsv.openStream()));    // apro il buffer di lettura
            bffr.readLine();  // salto la prima riga
            String riga;
            while ((riga = bffr.readLine()) != null) {    // leggo ogni riga del file
                //sostituisco le virgole con ; che utilizzerò come separatore
                riga = riga.replace(",", COMMA_DELIMITER);
                //uso split per dividere la riga in corrispondenza dei separatori, con trim elimino i caratteri non visibili
                String[] rigaSeparata = riga.trim().split(COMMA_DELIMITER);
                // prendiamo i valori dei singoli campi dalla riga
                char freq = rigaSeparata[0].trim().charAt(0);//freq è di tipo char
                String geo = rigaSeparata[1].trim();
                String unit = rigaSeparata[2].trim();
                String aid_instr = rigaSeparata[3].trim();
                double[] contributo = new double[intervalloAnni];
                for (int i = 0; i < intervalloAnni; i++) {
                    contributo[i] = Double.parseDouble(rigaSeparata[4 + i].trim());
                }
                // prendendo i valori ottenuti dal parsing, creo un nuovo oggetto e lo inserisco nella lista
                ContributoNazione nuova = new ContributoNazione(freq, geo, unit, aid_instr, contributo);
                contributi.add(nuova);
            }
        } catch (MalformedURLException e) {
            System.err.println("url non corretto!");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // chiudo buffer rimasti aperti nel finally
            if (bffr != null) bffr.close();
        }
    }


    /**
     * Metodo che esegue il salvataggio in locale tramite seriale java
     *
     * @param nomeFile file cache da creare
     */
    private void salvaSeriale(String nomeFile) {
        // buffer di output per salvare tramite seriale la lista creata
        try (ObjectOutputStream outStream = new ObjectOutputStream(new FileOutputStream(nomeFile))) {
            //salvo la lista come array per evitare problemi di casting, verrà dunque riconvertita in lista
            outStream.writeObject(contributi.toArray(new ContributoNazione[0]));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metodo che carica la lista di oggetti (già parsati) tramite seriale
     *
     * @param nomeFile nome del file cache da leggere
     */
    private void caricaSeriale(String nomeFile) {
        // buffer di  input da file seriale
        try (ObjectInputStream inStream = new ObjectInputStream(new FileInputStream(nomeFile))) {
            // salvo la lista come array per evitare problemi di casting
            contributi = Arrays.asList((ContributoNazione[]) inStream.readObject());   //readObject legge in ordine e non ha parametri
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.err.println("Impossibile trovare la classe!");
            e.printStackTrace();
        }
    }

    /**
     * Restituisce il dataset completo
     *
     * @return tutta la lista di oggetti
     */
    public List getData() {
        return contributi;
    }

    /**
     * Restituisce l'oggetto che corrisponde all'indice passato
     *
     * @param n n-indice dell'oggetto richiesto
     * @return l'oggetto corrispondente al valore di indice n
     */
    public ContributoNazione getContrNaz(int n) {//restituisce il contributo n-esimo
        if (n < contributi.size()) return contributi.get(n);
        else throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Oggetto di indice " + n + " non esiste!");
    }
}
