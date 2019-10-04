package com.denapoli.progettoop.controller;

import com.denapoli.progettoop.modello.ContributoNazione;
import com.denapoli.progettoop.service.ContrNazService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *  Controller Spring per gestire le richieste dell'utente
 */
@RestController
public class ContrNazController {
    private ContrNazService service;
    /**
     * L'annotazione @Autowired lancia automaticamente il costruttore all'avvio di Spring
     * @param service riferimento all'istanza del servizio
     */
    @Autowired //dependency injection
    public ContrNazController(ContrNazService service) {
        this.service = service;
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
        return service.getData();
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
        return service.getContrNaz(id);
    }

    /**
     * Metodo per gestire la richiesta GET alla rotta "/metadata", restituendo i metadati
     *
     * @return lista contenete tutti i metadati
     */
    @GetMapping("/metadata")
    public List getMetadata() {
        return service.metadata.getMetadata();
    }

    /**
     * Metodo per gestire la richiesta GET alla rotta "/anni", restituendo la lista di anni gestiti
     *
     * @return lista contenete gli anni gestiti
     */
    @GetMapping("/anni")
    public List getAnni() {
        return service.getAnni();
    }



    /**
     * Metodo per gestire la richiesta GET alla rotta "/statistiche", restituendo le statistiche
     *
     * @param nomeCampo nome del campo per statistiche o anno su cui calcolare statistiche numeriche, se non viene inserito vengono fornite le statistiche su ogni campo
     * @return lista contenente le statistiche richieste
     */
    @GetMapping("/statistiche") //da modificare? nomeCampo unico paramentro, prova
    public List getStats(@RequestParam(value = "campo", required = false, defaultValue = "") String nomeCampo) {
        if (nomeCampo.equals("")) {
            return service.getStatistiche();
        } else {
            List<Map> lista = new ArrayList<>();
                lista.add(service.getStatistiche(nomeCampo));
            return lista;
        }
    }




}




