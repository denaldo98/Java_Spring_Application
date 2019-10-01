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
    private static final List<String> operatori = Arrays.asList("$not", "$in", "$nin","$eq", "$gt", "$gte", "$lt", "$lte", "$bt");

    /**
     * Metodo per confrontare in base all'operatore inserito il valore val e il riferimento rif
     *
     * @param val valore sul quale applicare l'operatore
     * @param oper operatore da applicare
     * @param rif valore di riferimento
     * @return boolean
     */
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
                    List lRif = ((List) rif);
                    if (!lRif.isEmpty() && lRif.get(0) instanceof Number) {             //lista non vuota e contenente numeri
                        //conversione lista generica in lista di double
                        List<Double> lRifNum = new ArrayList<>();
                        for (Object elem : lRif) {
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
            } else if (val instanceof String) {                                         //implementare filtri stringhe

            } else
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Valore da controllare non valido: '" + val + "'");
        } else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Operatore non valido: " + oper);
    return false;
    }

    public static List<String> getOperatori() {
        return operatori;
    }
}