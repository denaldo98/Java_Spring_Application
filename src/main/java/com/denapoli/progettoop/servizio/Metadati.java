package com.denapoli.progettoop.servizio;

import com.denapoli.progettoop.modello.ContributoNazione;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Metadati {
    private List<Map> metadati = new ArrayList<>();//lista per i metadati

    public Metadati() {
        /*Il costruttore genera la lista dei metadati relativi al dataset considerato
         *Con metadati si intendono le informazioni riguardanti il nome dei campi del dataset, il nome e il tipo della variabile con cui essi vengono gestiti nell'applicazione */
        Field[] fields = ContributoNazione.class.getDeclaredFields();//estrae gli attributi della classe modellante

        for (Field f : fields) {
            Map<String, String> map = new HashMap<>();
            //inseririamo le coppie nome valore
            map.put("alias", f.getName());
            map.put("sourceField", f.getName());//nome del campo in csv
            map.put("type", f.getType().getSimpleName());
            metadati.add(map);
        }
    }

    public List getMetadati (){return metadati;}
}
