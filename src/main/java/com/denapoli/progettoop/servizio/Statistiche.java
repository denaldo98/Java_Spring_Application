package com.denapoli.progettoop.servizio;

import java.util.List;

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
}
