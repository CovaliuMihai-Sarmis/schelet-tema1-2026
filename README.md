Aplicatia simuleaza platforma Energex-Lite pentru centralizarea, validarea si interogarea a 6 fluxuri de date din piata de energie (DAM_PRICE, DAM_PRICE_PREDICTION, GRID_LOAD, GRID_LOAD_PREDICTION, IMBALANCE_PRICE si IMBALANCE_VOLUME), organizate pe zile si rezolutie de 15 minute (96 de intervale zilnice).

ARHITECTURA SI JUSTIFICAREA DECIZIILOR OOP

Arhitectura este impartita pe componente cu responsabilitati clare (structuredfiles, repository, startsimulation, commands si main), astfel incat datele de domeniu, stocarea, starea simularii si executia comenzilor sa fie decuplate.

Ierarhia de domeniu (Interval si DailyDocument)
Am ales o ierarhie pe doua niveluri: clasa abstracta Interval (extinsa de cate o clasa concreta pentru fiecare dintre cele 6 fluxuri) si clasa generica DailyDocument (specializata in pachetul specific_documents).
Justificare: toate cele 6 fluxuri au structura comuna la nivel de zi si la nivel de slot de 15 minute, diferind doar prin masuratorile specifice. Polimorfismul pe Interval permite repository-ului si comenzilor sa manipuleze toate tipurile de date uniform, fara a duplica logica de stocare. Genericitatea pe DailyDocument elimina codul repetitiv pentru cele 6 documente zilnice si pastreaza siguranta tipurilor la deserializarea template-urilor JSON. Fiecare subclasa de Interval incapsuleaza propriile campuri si decide singura daca slotul contine date valide.

Command Pattern si Factory Method (pachetul commands)
Am ales sa modelez fiecare comanda din input (startImport, stopImport, import, patch, get) printr-o clasa proprie care extinde clasa abstracta ExecuteCommand, instantele fiind create centralizat prin factory-ul ReturnObjectFromCommand.
Justificare: in locul unei metode monolitice in Main cu ramificatii lungi de tip if/else, fiecare comanda are o singura responsabilitate si aceeasi interfata de executie. Main ramane simplu si independent de detaliile comenzilor, iar adaugarea unei comenzi noi necesita doar o subclasa noua si inregistrarea ei in factory.

Repository Pattern (MarketDataRepository)
Am separat complet stocarea si interogarea datelor intr-o clasa dedicata de tip Repository, folosind o structura de tip Map<String, TreeMap<String, DailyDocument<?>>> cu initializare lenesa (lazy initialization) din fisierele template.
Justificare: comenzile nu trebuie sa cunoasca modul in care sunt pastrate documentele in memorie sau cum sunt incarcate template-urile. Initializarea lenesa evita crearea de documente goale pentru zile care nu primesc niciodata date. Alegerea unui TreeMap pentru cheile de tip data garanteaza ordonarea cronologica automata, necesara pentru cautarile istorice inapoi in timp.

Incapsularea starii globale (Simulation)
Am ales sa grupez starea unei rulari (repository-ul, ceasul intern si starea ingestiei) intr-o singura clasa Simulation, instantiata per test si transmisa comenzilor la executie.
Justificare: evitam variabilele statice globale (care ar pastra stare reziduala intre teste) si oferim tuturor comenzilor acces controlat la acelasi context al simularii.

Centralizarea regulilor de business (IntervalValidator)
Am extras toate regulile de validare ale celor 6 fluxuri si construirea avertismentelor WARNING intr-o clasa dedicata.
Justificare: atat comanda import, cat si comanda patch aplica exact aceleasi reguli de domeniu si aceleasi formate de mesaje. Separarea validarii elimina duplicarea codului intre cele doua comenzi si permite modificarea unei reguli intr-un singur loc.

EVOLUTIA ARHITECTURII SI PROBLEME INTAMPINATE

Pe parcursul dezvoltarii si testarii, mai multe probleme concrete au dus la rafinarea designului initial:

Trecerea de la HashMap la TreeMap in MarketDataRepository
Problema: in prima abordare, documentele zilnice puteau fi mapate intr-un HashMap simplu dupa data. Cand am implementat verificarea variatiei de 10000 MW pentru GRID_LOAD si interogarile get cu target LATEST, a aparut necesitatea de a cauta cel mai recent interval valid anterior, inclusiv peste granita dintre doua zile.
Schimbarea: am inlocuit HashMap cu TreeMap pe nivelul datelor calendaristice, ceea ce a permis parcurgerea ordonata descrescator a zilelor anterioare fara sortari suplimentare la fiecare interogare.

Separarea comportamentului la date invalide intre import si patch
Problema: initial, atat import, cat si patch ignorau pur si simplu un punct invalid, lasand intacta valoarea anterioara din slot. Testele avansate au evidentiat ca un import invalid venit peste un slot deja populat trebuie sa invalideze acel slot, in timp ce un patch invalid trebuie sa pastreze vechea valoare.
Schimbarea: am pastrat validarea comuna in IntervalValidator, dar am diferentiat reactia la esec in clasele de comanda, adaugand in repository o operatie explicita de resetare a slotului apelata doar la import.

Regandirea logicii de cautare pentru target LATEST
Problema: prima varianta calcula strict ultimul sfert de ora incheiat inainte de timestamp-ul comenzii get. Daca acel interval exact nu avea date pentru fluxul cerut, sistemul intorcea WARNING chiar daca existau date valide in intervalele sau zilele anterioare.
Schimbarea: am delegat cautarea ultimului interval valid catre repository (profitand de ordonarea TreeMap), iar comanda get foloseste intervalul implicit doar ca fallback cand nu exista nicio data pentru fluxul respectiv.

Eliminarea filtrarii rigide dupa timestamp la import si normalizarea intervalelor
Problema: initial, ImportCommand accepta doar intervalele egale cu timestamp-ul comenzii minus 15 minute si parsa string-ul de interval fara eliminarea spatiilor. In testele cu batch-uri de mai multe intervale sau cu formate mixte ("HH:mm-HH:mm" si "HH:mm - HH:mm"), o parte din puncte erau sarite silentios.
Schimbarea: am eliminat filtrarea restrictiva pe un singur slot din ImportCommand si am normalizat parsarea si formatarea intervalelor atat in comenzile de ingestie, cat si in validator.

Corectarea incapsularii starii de validitate in GridLoadInterval
Problema: verificarea prezentei datelor din GridLoadInterval tinea cont initial si de campul reservesRequiredMW, desi pentru fluxul GRID_LOAD se persista doar quantity, ceea ce facea ca interogarile get sa nu vada datele salvate.
Schimbarea: am aliniat conditia din GridLoadInterval strict la campurile persistate pentru acest tip de flux.

UTILIZAREA INSTRUMENTELOR AI

Am folosit un asistent AI exclusiv pentru structurarea si redactarea acestui fisier README. Continutul a fost revizuit si adaptat manual astfel incat sa reflecte exact implementarea si deciziile de design din proiect.