package com.denapoli.progettoop.service;

import com.denapoli.progettoop.modello.ContributoNazione;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Classe per i metadati
 */
public class Metadata {
    private List<Map> metadata = new ArrayList<>(); //lista per i metadati

    /**
     * Il costruttore genera la lista dei metadati relativi al dataset considerato
     */
     Metadata() {
        Field[] fields = ContributoNazione.class.getDeclaredFields(); //estrae gli attributi della classe modellante
        for (Field f : fields) { //scorro tutti i campi
            Map<String, String> map = new HashMap<>(); //inizializzo la mappa che conterrà i metadati
            //inseriamo le coppie nome valore
            map.put("alias", f.getName());
            if(!f.getName().equals( "contributo" ) && !f.getName().equals( "intervalloAnni" ))
            map.put("sourceField", f.getName().toUpperCase()); //nome del campo nel csv
            else if(f.getName().equals( "contributo" )) map.put("sourceField","TIME_PERIOD"); //gestiamo il caso contributo (vettore)
            map.put("type", f.getType().getSimpleName());
            metadata.add(map); //aggiungo la mappa ai metadati
        }
    }

    /**
     * Metodo per restituire i metadati
     *
     * @return lista con i metadati
     */
    public List getMetadata (){return metadata;} //ritorno i metadati
}
