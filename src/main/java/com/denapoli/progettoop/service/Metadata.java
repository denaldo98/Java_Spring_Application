package com.denapoli.progettoop.service;

import com.denapoli.progettoop.modello.ContributoNazione;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Metadata {
    private List<Map> metadata = new ArrayList<>();         //lista per i metadati

    public Metadata() {
        //Il costruttore genera la lista dei metadati relativi al dataset considerato
        Field[] fields = ContributoNazione.class.getDeclaredFields();//estrae gli attributi della classe modellante

        for (Field f : fields) { //scorro tutti i campi
            Map<String, String> map = new HashMap<>(); //inizializzo la mappa che conterrà i metadati
            //inseririamo le coppie nome valore
            map.put("alias", f.getName());
            map.put("sourceField", f.getName());//nome del campo in csv
            map.put("type", f.getType().getSimpleName());
            metadata.add(map); //aggiungo la mappa ai metadati
        }
    }

    public List getMetadata (){return metadata;} //ritorno i metadati
}
