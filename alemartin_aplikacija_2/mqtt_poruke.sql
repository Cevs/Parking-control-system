CREATE TABLE mqtt_poruke (
  id integer NOT NULL 
                PRIMARY KEY GENERATED ALWAYS AS IDENTITY 
                (START WITH 1, INCREMENT BY 1),
  korisnik varchar(255) NOT NULL DEFAULT '', 
  akcija varchar(25) NOT NULL DEFAULT '',   
  registracija varchar(25) NOT NULL DEFAULT '',
  vrijeme timestamp,
  idParkiraliste int NOT NULL
);