package com.denapoli.progettoop.controllore;

import com.denapoli.progettoop.modello.ContributoNazione;
import com.denapoli.progettoop.servizio.ContrNazServizio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *  Controllore Spring che gestisce le richieste dell'utente
 */
@RestController
public class ContrNazControllore {
    private ContrNazServizio servizio;

    /**
     * L'annotazione @Autowired lancia automaticamente il costruttore all'avvio di Spring
     * @param servizio riferimento all'istanza del servizio
     */
    @Autowired //dependency injection
    public ContrNazControllore(ContrNazServizio servizio) {
        this.servizio = servizio;
    }

    //Metodi che attraverso l'utilizzo di una richiesta GET all'url indicato restituiranno differenti oggetti

    /**
     * Metodo per gestire la richiesta GET alla rotta "/data", restituendo l'intero dataset
     *
     * @return lista di tutti gli oggetti del dataset
     */
    //la rotta è la parte dell'url dopo dominio:porta es.: localhost:8080/data
    @GetMapping("/data")
    public List getData() {
        return servizio.getData();
    }

    /**
     * Metodo per gestire la richiesta GET alla rotta "/data/{id}", restituendo il record del dataset corrispondente a {id}
     * {id} è pertanto da sosttuire con l'id del record desiderato
     *
     * @param id id del record desiderato
     * @return oggetto corrispondente all'id richiesto
     */
    @GetMapping("/data/{id}")
    public ContributoNazione getContrNazId(@PathVariable int id) {
        return servizio.getContrNaz(id);
    }

    /**
     * Metodo per gestire la richiesta GET alla rotta "/metadata", restituendo i metadati
     *
     * @return lista contenete tutti i metadati
     */
    @GetMapping("/metadata")
    public List getMetadata() {
        return servizio.metadata.getMetadata();
    }

    /**
     * Metodo per gestire la richiesta GET alla rotta "/stats", restituendo le statistiche
     *
     * @param nomeCampo parametro opzionale per richiedere le statistiche di un solo campo
     * @return lista contenente le statistiche richieste
     */
    @GetMapping("/stats") //da modificare
    public List getStats(@RequestParam(value = "field", required = false, defaultValue = "") String nomeCampo) {
        if (nomeCampo.equals("")) {
            return servizio.getStatistiche();
        } else {
            List<Map> list = new ArrayList<>();
            list.add(servizio.getStatistiche(nomeCampo));
            return list;
        }
    }




}




