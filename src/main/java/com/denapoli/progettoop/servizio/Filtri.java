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

    public static boolean check(Object val, String oper, Object rif) {
        if (operatori.contains(oper)) {
            if (val instanceof Number) {
                double valNum = ((Number) val).doubleValue();
                if (rif instanceof Number) {
                    double rifNum = ((Number) rif).doubleValue();
                    switch (oper) {
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
                            String erroreOper = "L'operatore: '" + oper + "' risulta inadatto per gli operandi: '" + val + "' , '" + rif + "'";
                            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, erroreOper);
                    }
                } else if (rif instanceof List) {
                    List lRif = ((List) rif);
                    if (!lRif.isEmpty() && lRif.get(0) instanceof Number) {

                        List<Double> lRifNum = new ArrayList<>();
                        for (Object elem : lRif) {
                            lRifNum.add(((Number) elem).doubleValue());
                        }
                        switch (oper) {
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
            } else if (val instanceof String) {//implementare filtri stringhe

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