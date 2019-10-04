package com.denapoli.progettoop.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Statistiche {

    /**
     * Metodo per calcolare la media degli elementi di una lista
     *
     * @param lista lista di numeri di cui vogliamo calcolare la media
     * @return media degli elementi
     */
    public static double avg(List<Double> lista) {
        return sum(lista) / count(lista);
    }

    /**
     * Metodo per trovare il valore minimo tra gli elementi di una lista
     *
     * @param lista lista di numeri dai quali trovare il minimo
     * @return valore minimo della lista
     */
    public static double min(List<Double> lista) {
        double min = lista.get(0);
        for (Double num : lista) {
            if (num < min)     min = num;
        }
        return min;
    }

    /**
     * Metodo pre trvare il valore massimo tra gli elementi di una lista
     *
     * @param lista lista di numeri del quale cerchiamo il massimo
     * @return valore massimo della lista
     */
    public static double max(List<Double> lista) {
        double max = lista.get(0);
        for (Double num : lista) {
            if (num > max)     max = num;
        }
        return max;
    }

    /**
     * Metodo per calcolare la deviazione standard degli elementi di una lista
     *
     * @param lista lista di numeri dei quali calcolare la dev. std.
     * @return deviazione standard dei valori della lista
     */
    public static double devStd(List<Double> lista) {
        double avg = avg(lista);
        double var = 0;
        for (Double num : lista) {
            var += Math.pow(num - avg, 2);
        }
        return Math.sqrt(var);
    }


    /**
     * Metodo per sommare tutti gli elementi di una lista
     *
     * @param lista lista di numeri da sommare
     * @return somma degli elementi
     */
    public static double sum(List<Double> lista) {
        double s = 0;
        for (Double n : lista) {
            s += n;       //con il for-each scorro tutta la lista sommando ogni membro
        }
        return s;
    }


    /**
     * Metodo per contare gli elementi di una lista
     *
     * @param lista lista di valori da contare
     * @return dimensione della lista
     */
    public static int count(List lista) {
        return lista.size();
    }


    /**
     * Metodo per contare quante volte compare ogni elemento in una lista
     *
     * @param lista lista di valori
     * @return Map che ha come chiavi gli elementi della lista e come valori il numero di occorrenze
     */
    public static Map<Object, Integer> contaElementiUnici(List lista) {
        Map<Object, Integer> m = new HashMap<>();
        for (Object o : lista) {
            Integer num=m.get(o);
            m.put(o, (num == null ? 1 : num+1));       //se la chiave si trova già nella mappa aumento di 1 il valore
        }                                               // altrimenti aggiungo l'oggetto appena trovato alla mappa con valore 1
        return m;
    }


    /**
     * Metodo che restituisce tutti valori statistici di un certo campo del dataset
     *
     * @param nomeCampo nome del campo dal quale si è estratta la lista di valori
     * @param lista lista dei valori del campo
     * @return Map che ha come chiavi i nomi delle statistiche calcolabili sul campo e associati i rispettivi valori
     */
    public static Map<String, Object> getTutteStatistiche(String nomeCampo, List lista) {
        Map<String, Object> m = new HashMap<>();
        m.put("campo", nomeCampo);
        if (!lista.isEmpty()) { //calcolo le statistiche solo se la lista non è vuota
            if (ContrNazService.anni.contains(nomeCampo) ) {        // calcola le statistiche numeriche rispetto all'anno scelto
                //converto la lista generica in lista di double
                List<Double> listNum = new ArrayList<>();
                for (Object o : lista){
                    listNum.add(((Double) o));
                }
                //riempio la mappa con le statistiche numeriche relative all'anno richiesto
                m.put("avg", avg(listNum));
                m.put("min", min(listNum));
                m.put("max", max(listNum));
                m.put("dev std", devStd(listNum));
                m.put("sum", sum(listNum));
                m.put("count", count(listNum));
                return m;
            } else {        // calcola le statistiche non numeriche se nomeCampo non è uno degli anni gestiti
                m.put("elementiUnici", contaElementiUnici(lista));
                m.put("count", count(lista));
            }
        }
        return m;
    }
}
