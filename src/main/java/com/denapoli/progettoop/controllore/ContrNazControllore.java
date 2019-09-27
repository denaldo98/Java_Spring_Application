package com.denapoli.progettoop.controllore;

import com.denapoli.progettoop.servizio.ContrNazServizio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ContrNazControllore {
    private ContrNazServizio servizio;

    @Autowired
    public ContrNazControllore(ContrNazServizio servizio) {
        this.servizio = servizio;
    }

    @GetMapping("/data")
    public List getDati() {
        return servizio.getData();
    }

}




