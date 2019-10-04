package com.denapoli.progettoop.controller;

import com.denapoli.progettoop.modello.ContributoNazione;
import com.denapoli.progettoop.service.ContrNazService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.BasicJsonParser;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  Controller Spring per gestire le richieste dell'utente
 */
@RestController
public class   ContrNazController {
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



    /**
     * Metodo per eseguire il parsing del filtro passato tramite body di una POST
     *
     * @param body body della richiesta POST contenente un filtro
     * @return mappa contenente i parametri del filtro: campo, operatore, valore di riferimento
     */
    private static Map<String, Object> parseFiltro(String body) {
        Map<String, Object> bodyParsato = new BasicJsonParser().parseMap(body);
        String nomeCampo = bodyParsato.keySet().toArray(new String[0])[0];
        Object valore = bodyParsato.get(nomeCampo);
        Object rif;
        String oper;
        if (valore instanceof Map) {
            Map filtro = (Map) valore;
            oper = ((String) filtro.keySet().toArray()[0]).toLowerCase();
            rif = filtro.get(oper);
        } else {
            oper = "$eq";
            rif = valore;
        }
        Map<String, Object> filtro = new HashMap<>();
        filtro.put("oper", oper);
        filtro.put("campo", nomeCampo);
        filtro.put("rif", rif);
        return filtro;
    }

    /**
     * Metodo che gestisce una richiesta POST alla rotta "/data", resituendo la lista dei record che soddisfano il filtro
     *
     * @param body body della richiesta POST contenente il filtro
     * @return lista di oggetti che soddisfano il filtro
     */
    @PostMapping("/data")
    public List getDatiFiltrati(@RequestBody String body) {
        Map<String, Object> filtro = parseFiltro(body);
        String nomeCampo = (String) filtro.get("campo");
        String oper = (String) filtro.get("oper");
        Object rif = filtro.get("rif");
        return service.getDatiFiltrati(nomeCampo, oper, rif);
    }

    /**
     * Metodo che gestisce la richiesta POST alla rotta "/statistiche", restiuisce le statistiche sul campo richiesto (opzionale) o su tutti i campi, considerando soltanto i record che soddisfano il filtro
     *
     * @param fieldName campo di cui si richiedono le statistiche (opzionale)
     * @param body      body della richiesta POST che contiene il filtro
     * @return lista contenente le statistiche richieste
     */
    @PostMapping("/statistiche")
    public List<Map> getStatisticheFiltrate(@RequestParam(value = "campo", required = false, defaultValue = "") String fieldName, @RequestBody String body) {
        Map<String, Object> filtro = parseFiltro(body);
        String campoFiltro = (String) filtro.get("field");
        String oper = (String) filtro.get("oper");
        Object rif = filtro.get("rif");
        if (fieldName.equals("")) {
            return service.getStatisticheFiltrate(campoFiltro, oper, rif);
        } else {
            List<Map> lista = new ArrayList<>();
            lista.add(service.getStatisticheFiltrate(fieldName, campoFiltro, oper, rif));
            return lista;
        }
    }





}




