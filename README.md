# Progetto esame Object-Oriented Programming

Progetto d'esame per il corso di "Programmazione ad oggetti" a.a. 2018/2019 all'interno del corso di laurea di *Ingegneria Informatica e dell'Automazione* dell'*Università Politecnica delle Marche*.

## Introduzione
Questa repository contiene un'applicazione *Java* basata sul framework *Spring* che restituisce tramite API REST GET o POST dati e statistiche in formato JSON di un dataset assegnatoci. Il progetto può essere compilato attraverso il framework *Gradle* che gestisce l'importazione delle librerie *Spring*.


## Funzionamento all'avvio
L'applicazione, una volta lanciata, esegue il download di un dataset in formato CSV contenuto in un JSON fornito tramite un [URL](http://data.europa.eu/euodp/data/api/3/action/package_show?id=V7ZkhAQ536LhqVNfAeGA).
Il download del dataset avviene solo se non è stato precedentemente effettuato e verrà salvato nella cartella del progetto con il nome di dataset.csv. Successivamente viene effettuato il *parsing* del file CSV in modo da poter creare le istanze del modello che verranno inserite all'interno di una lista. 
Inoltre, il programma avvia un web-server in locale sulla porta 8080 che riceve richieste dall'utente. 

## Interpretazione modello e dati

I dati sono tratti dal sito dell'Unione Europea e in particolare essi riguardano il contributo di "aid instruments" in milioni di euro degli stati europei negli anni dal 2000 al 2017.

Il dataset CSV contiene:
1) La frequenza di rilevazione del dato (indicata con "freq", nel nostro caso sempre annuale);
2) La nazione che ha fornito il contributo (indicata con "geo", sigla della nazione in maiuscolo);
3) L'unità di misura (indicata con "unit", milioni di euro);
4) Categoria di aid instruments forniti (indicata con "aid_instr");
5) Contributi annui forniti anno per anno dal 2000 al 2017 (indicati con "contributo").

Ulteriori informazioni sul dataset sono presenti a questo [link](https://webgate.ec.europa.eu/comp/redisstat/databrowser/view/COMP_AI_SA_X$COMP_AI_SA_01/default/table)


## Packages e classi

Il progetto presenta un package principale  `com.denapoli.progettoop`che contiene tutti i sorgenti delle classi Java e in particolare la classe main `ProgettoopApplication` che avvia il server Spring. Le altre classi sono divise in tre package:

-   `modello`: contiene la classe  `ContributoNazione`che modella il singolo record del dataset;
-   `service`: contiene la classe  `ContrNazService`  che gestisce il download, il parsing e l'accesso al dataset, la classe  `Statistiche`per il calcolo delle statistiche numeriche e non,  la classe`Filtri`per la gestione del filtraggio dei dati, la classe `Metadata`per generare e restituire i metadati e la classe `Utilities` che contiene eventuali variabili e metodi utili ;
-   `controller`: contiene la classe  `ContrNazController`che gestisce richieste da parte dell'utente (risposte sottoforma di stringhe in formato JSON;

Visionare la *JavaDoc* per informazioni più specifiche su classi e relativi metodi.

## Richieste GET e POST gestite
E' possibile effettuare le richieste ***GET*** e ***POST*** , dopo aver avviato il progetto, all'indirizzo : `http://localhost:8080`

### Richieste GET
Le richieste GET di questa applicazione sono:

 - **/data** - Restituisce l'intero dataset in formato JSON;
 - **/metadata** - Restituisce il JSON contenente tutti i metadati;
 - **/data/{i}** - Restituisce il JSON contenente il record all'indice desiderato i;
 - **/anni** - Restituisce la lista di anni considerati dal dataset;
 - **/operatori** - Restituisce la lista di operatori gestiti;
 - **/statistiche?campo="nomeCampo"** - Restituisce le statistiche del campo desiderato: se il campo è numerico restituisce la media, l'elemento minimo e massimo, la deviazione standard, la somma e il conteggio, altrimenti restituisce gli elementi unici e il conteggio. 
 Se non viene inserito alcun campo vengono restituite le statistiche su tutti i campi.
 
### Richieste POST
Le richieste POST di questa applicazione sono:

 - **/data** - Restituisce il dataset filtrato sulla base dei parametri inseriti nel body della richiesta(per la sintassi del filtro leggere più avanti);
 - **/statistiche?campo="nomeCampo"** - Restituisce le statistiche considerando solo i record che soddisfano il filtro specificato nel body della richiesta(per la sintassi del filtro leggere più avanti). 
 Anche in questo caso si può specificare un campo oppure omettere il parametro.


A differenza dalle richieste GET precedenti dunque,  queste aggiungono il filtraggio.
Un filtro, in questo caso, è utile a farci restituire i dati (o le statistiche) filtrati a seconda delle nostre esigenze.

#### Sintassi filtro
Il filtro va inserito nel body della richiesta POST con il seguente formato: 
 
 `{"campo" : {"operatore" : riferimento}}`
 - In "campo" inserire il campo sul quale deve essere applicato il filtro; 
 - In "operatore" inserire uno tra i filtri: "\$eq", "\$not", "\$in", "\$nin", "\$gt", "\$gte", "\$lt", "\$lte", "$bt";
 - In riferimento il valore di riferimento da confrontare nel campo (il tipo di dato da usare come riferimento deve essere lo stesso del campo);
 
 Se dobbiamo inserire più valori bisogna inserirli tra parentesi quadre, esempio: `{"2000" : {"$bt":[2000,3000]}}`
 Non è possibile applicare più filtri contemporaneamente.
 Se si vuole utilizzare l'operatore  `$eq`si può usare un formato più compatto per il filtro, ovvero:
{"campo":riferimento} = {"campo":{"$eq":riferimento}}

### Statistiche implementate
Le statistiche si dividono in statistiche numeriche e statistiche non numeriche.

#### Statistiche numeriche:
 - MEDIA (avg);
 - MINIMO (min);
 - MASSIMO (max);
 - DEVIAZIONE STANDARD (devStd):
 - SOMMA (sum);
 - CONTEGGIO (count).
 
#### Statistiche non numeriche:
 - CONTA ELEMENTI UNINCI (contaElementiUnici);
 - CONTEGGIO (count).

### Operatori di filtraggio implementati:
 - $not : operatore di disuguaglianza;
 - $in : operatore che verifica l'appartenenza;
 - $nin : operatore di non appartenenza;
 - $eq : operatore di uguaglianza;
 - $gt : operatore maggiore (>);
 - $gte : operatore maggiore o uguale (>=);
 - $lt : operatore minore (<);
 - $lte : operatore minore o uguale (<=);
 - $bt : operatore compreso tra ( < . >);

### Esempi di richieste GET/POST
Risultati ottenuti mediante Postman.

 - GET anni 
![anni](https://github.com/denaldo98/Progetto-OOP/blob/master/GET_POST_screens/Anni.PNG)

 - GET campo errato
![esempio campo errato](https://github.com/denaldo98/Progetto-OOP/blob/master/GET_POST_screens/Campo%20errato.PNG)

 - POST contributo>20000
![enter image description here](https://github.com/denaldo98/Progetto-OOP/blob/master/GET_POST_screens/Contributo_2000$gt_20000.PNG)

 - GET data 1
![enter image description here](https://github.com/denaldo98/Progetto-OOP/blob/master/GET_POST_screens/Dati%201.PNG)

 - GET data
![enter image description here](https://github.com/denaldo98/Progetto-OOP/blob/master/GET_POST_screens/Dati.PNG)

 - GET metadata
![enter image description here](https://github.com/denaldo98/Progetto-OOP/blob/master/GET_POST_screens/Metadati.PNG)

  GET statistiche (freq)
![enter image description here](https://github.com/denaldo98/Progetto-OOP/blob/master/GET_POST_screens/Statistiche%20freq.PNG)

 - GET statistiche
![enter image description here](https://github.com/denaldo98/Progetto-OOP/blob/master/GET_POST_screens/Statistiche.PNG)

 - GET statistiche (2003)
![enter image description here](https://github.com/denaldo98/Progetto-OOP/blob/master/GET_POST_screens/Stiatistiche%202003.PNG)

 - GET operatori
![enter link description here](https://github.com/denaldo98/Progetto-OOP/blob/master/GET_POST_screens/operatori.PNG)


## Diagrammi UML
 - Casi d'uso
 
![Casi d'uso](https://github.com/denaldo98/Progetto-OOP/blob/master/Diagrammi%20UML/Diagramma%20casi%20d%27uso.PNG)
 
 - Classi
 
![Classi](https://github.com/denaldo98/Progetto-OOP/blob/master/Diagrammi%20UML/Diagramma%20delle%20classi.PNG)

 - GET anni 
 
![GET Anni](https://github.com/denaldo98/Progetto-OOP/blob/master/Diagrammi%20UML/GET%20Anni.PNG)

 - GET data
 
![GET Data](https://github.com/denaldo98/Progetto-OOP/blob/master/Diagrammi%20UML/GET%20Data.PNG)

 - GET data(i)
 
![GET Data(i)](https://github.com/denaldo98/Progetto-OOP/blob/master/Diagrammi%20UML/GET%20Data-i.PNG)

 - GET metadata

![GET Metadata](https://github.com/denaldo98/Progetto-OOP/blob/master/Diagrammi%20UML/GET%20Metadata.PNG)

 - GET operatori
 
![GET Operatori](https://github.com/denaldo98/Progetto-OOP/blob/master/Diagrammi%20UML/GET%20Operatori.PNG)

 - GET statistiche
 
![GET Statistiche](https://github.com/denaldo98/Progetto-OOP/blob/master/Diagrammi%20UML/GET%20Statistiche.PNG)

 - GET statistiche dettagliato
 
![GET Statistiche dettagliato](https://github.com/denaldo98/Progetto-OOP/blob/master/Diagrammi%20UML/GET%20Statistiche%20dettagliato.PNG)

 - POST data
 
![POST Data](https://github.com/denaldo98/Progetto-OOP/blob/master/Diagrammi%20UML/POST%20Data.PNG)

 - POST statistiche dettagliato
 
![POST Statistiche dettagliato](https://github.com/denaldo98/Progetto-OOP/blob/master/Diagrammi%20UML/POST%20Statistiche%20dettagliato.PNG)

### Autori

* **Denaldo Lapi** - [denaldo98](https://github.com/denaldo98)
* **Antonio Politano** - [S1082351](https://github.com/S1082351)
