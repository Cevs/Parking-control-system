CREATE TABLE dnevnik (
  id integer NOT NULL 
                PRIMARY KEY GENERATED ALWAYS AS IDENTITY 
                (START WITH 1, INCREMENT BY 1),
  korisnik varchar(25) NOT NULL DEFAULT '',
  url varchar(255) NOT NULL DEFAULT '',
  ipadresa varchar(25) NOT NULL DEFAULT '',
  vrsta_zahtjeva varchar(50) NOT NULL DEFAULT '',
  sadrzaj_zahtjeva varchar(255) NOT NULL DEFAULT '',
  vrijeme timestamp,
  trajanje int NOT NULL DEFAULT 0,
  status int NOT NULL DEFAULT 0
);