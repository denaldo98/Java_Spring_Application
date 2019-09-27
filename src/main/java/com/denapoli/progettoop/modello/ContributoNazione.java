package com.denapoli.progettoop.modello;

import java.io.Serializable;

/**
 * Classe che modella i record del dataset csv
 */
public class ContributoNazione implements Serializable{
    private char freq;
    private String geo;
    private String unit;
    private String aid_instr;
    private double[] contributo;
    private static final int intervalloAnni=18;


    /**
     * Costruttore della classe
     *
     * @param freq   Primo campo del file csv
     * @param geo Secondo campo del file csv
     * @param unit Terzo campo del file csv
     * @param aid_instr Quarto campo del file csv
     * @param contributo Successivi 18 campi del file csv
     */
    public ContributoNazione(char freq, String geo, String unit, String aid_instr, double[] contributo) {
        this.freq = freq;
        this.geo = geo;
        this.unit= unit;
        this.aid_instr = aid_instr;
        this.contributo=contributo;
    }

    public char getFreq() {
        return freq;
    }

    public String getGeo() {
        return geo;
    }

    public String getUnit() { return unit;}

    public String getAid_instr() {
        return aid_instr;
    }

    public double[] getContributo() {
        return contributo;
    }

    /**
     * Metodo toString per stampare l'oggetto
     *
     * @return Restituisce una stringa contenente il valore dei vari campi
     */
    @Override
    public String toString() {
        StringBuilder s;
        s = new StringBuilder("ContributoNazione{" +
                "freq=" + freq +
                ", geo='" + geo + '\'' +
                ", unit='" + unit + '\'' +
                ", aid_instr='" + aid_instr + '\'');
        for(int i=0; i<intervalloAnni; i++ ) s.append(" anno=").append(2000 + i).append(" contributo=").append(contributo[i]).append(";");
        s.append('}');
        return s.toString();
    }
}