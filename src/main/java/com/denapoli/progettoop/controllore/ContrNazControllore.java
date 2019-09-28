package com.denapoli.progettoop.controllore;

import com.denapoli.progettoop.servizio.ContrNazServizio;
import com.denapoli.progettoop.servizio.Metadati;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *  Controllore Spring che gestisce le richieste dell'utente
 */
@RestController
public class ContrNazControllore {
    private ContrNazServizio servizio;

    /**
     * L'annotazione @Autowired lancia automaticamente il costruttore all'avvio di Spring
     * @param servizio riferimento all'istanza del servizio
     */
    @Autowired //dipendenza
    public ContrNazControllore(ContrNazServizio servizio) {
        this.servizio = servizio;
    }
}




