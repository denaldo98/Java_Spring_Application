package com.denapoli.progettoop.servizio;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Classe con metodi per filtraggio dati e statistiche
 */
public class Filtri {
    //operatori di confronto considerati
    private static final List<String> operatori = Arrays.asList("$not", "$in", "$nin", "$eq", "$gt", "$gte", "$lt", "$lte", "$bt");

    /**
     * Metodo per confrontare in base all'operatore inserito il valore val e il riferimento rif
     *
     * @param val  valore sul quale applicare l'operatore
     * @param oper operatore da applicare
     * @param rif  valore di riferimento
     * @return boolean
     */
    //Da aggiungere filtro Char? chiamata su vettore?
    public static boolean check(Object val, String oper, Object rif) {
        if (operatori.contains(oper)) {             //controllo che l'operatore sia uno di quelli gestiti
            if (val instanceof Number) {            //caso in cui il valore da controllare sia un numero
                double valNum = ((Number) val).doubleValue();  //cast in double
                if (rif instanceof Number) {         //caso in cui il riferimento sia un numero
                    double rifNum = ((Number) rif).doubleValue();  //cast in double
                    switch (oper) {                     //selezione operatore corrispondente, non uso break poichè ho il return
                        case "$not":
                            return valNum != rifNum;
                        case "$eq":
                            return valNum == rifNum;
                        case "$gt":
                            return valNum > rifNum;
                        case "$gte":
                            return valNum >= rifNum;
                        case "$lt":
                            return valNum < rifNum;
                        case "$lte":
                            return valNum <= rifNum;
                        default:
                            String erroreOper = "L'operatore: '" + oper + "' risulta inadatto per gli operandi: '" + val + "' , '" + rif + "'"; //stampa di errore nel caso in cui l'operatore non sia appropriato per variabili numeriche
                            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, erroreOper); //restituisce il messaggio di errore in formato JSON
                    }
                } else if (rif instanceof List) {                              //caso in cui il riferimento sia una lista
                    List rifL = ((List) rif);
                    if (!rifL.isEmpty() && rifL.get(0) instanceof Number) {             //lista non vuota e contenente numeri
                        //conversione lista generica in lista di double
                        List<Double> lRifNum = new ArrayList<>();
                        for (Object elem : rifL) {
                            lRifNum.add(((Number) elem).doubleValue()); //converto singolo elemento
                        }
                        switch (oper) {                             //selezione operatore
                            case "$in":
                                return lRifNum.contains(valNum);
                            case "$nin":
                                return !lRifNum.contains(valNum);
                            case "$bt":
                                double primo = lRifNum.get(0);
                                double secondo = lRifNum.get(1);
                                return valNum >= primo && valNum <= secondo;
                            default:
                                String erroreOper = "L'operatore: '" + oper + "' risulta inadatto per gli operandi: '" + val + "' , '" + rif + "'";
                                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, erroreOper);
                        }
                    } else
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lista vuota o non numerica");
                } else
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Il riferimento: '" + rif + "' non è compatibile con il valore: '" + val + "'");
            } else if (val instanceof String) {     // caso in cui il valore da controllare sia una stringa
                String valStr = ((String) val); // conversione
                if (rif instanceof String) {        // caso in cui il riferimento sia una stringa
                    String rifStr = ((String) rif); // conversione
                    switch (oper) {
                        case "$eq":
                            return valStr.equals(rifStr);
                        case "$not":
                            return !valStr.equals(rifStr);
                        default:
                            String erroreOper = "L'operatore:'" + oper + "' risulta inadatto per gli operandi: '" + val + "' , '" + rif + "'";
                            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, erroreOper);
                    }
                } else if (rif instanceof List) {  //caso in cui il riferimento sia una lista
                    List rifL = ((List) rif);
                    if (!rifL.isEmpty() && rifL.get(0) instanceof String) {   // se la lista non è vuota e contiene stringhe
                        // effettuo la conversione da lista generica a lista di stringhe
                        List<String> rifLStr = new ArrayList<>();
                        for (Object elem : rifL) {
                            rifLStr.add((String) elem);
                        }
                        switch (oper) {
                            case "$in":
                                return rifLStr.contains(valStr);
                            case "$nin":
                                return !rifLStr.contains(valStr);
                            default:
                                String message = "L'operatore: '" + oper + "' risulta inadatto per gli operandi: '" + val + "' , '" + rif + "'";
                                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
                        }
                    } else
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La lista è vuota o non contiene stringhe"); //caso in cui la lista sia vuota o non contenente stringhe
                } else
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Riferimento:'" + rif + "' non compativile con il valore'" + val + "'"); //caso in cui valore e riferimento non siano compatibili
            } else
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Valore da controllare non valido: '" + val + "'");  //caso in cui il valore non sia valido (no stringa o numerico)
        } else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Operatore non valido: " + oper); //operatore non gestito
    }

    /**
     * Metodo per restituire la lista di operatori gestiti
     *
     * @return lista degli operatori validi per i filtri
     */
    public static List<String> getOperatori() {
        return operatori;
    }

    /**
     * Metodo per applicare i filtri ad una lista
     *
     * @param val   lista dei valori su cui applicare i filtri
     * @param oper operatore da applicare
     * @param rif      valore di riferimento
     * @return lista con gli indici dei valori che soddisfano il filtro
     */
    public static List<Integer> filtra(List val, String oper, Object rif) {
        List<Integer> filtrati = new ArrayList<>();
        for (int i = 0; i < val.size(); i++) {
            if (check(val.get(i), oper, rif))        // eseguiamo il controllo per ogni elemento della lista: se soddisfatto aggiungo l'indice alla lista
                filtrati.add(i);
        }
        return filtrati;         //restituisco la lista con gli indici
    }
}