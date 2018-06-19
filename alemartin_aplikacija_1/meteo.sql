CREATE TABLE meteo (
  idMeteo int UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  id int UNSIGNED  NOT NULL,				
  adresaStanice varchar(255) NOT NULL DEFAULT '',
  latitude float(6) NOT NULL DEFAULT 0.0,
  longitude float(6) NOT NULL DEFAULT 0.0,
  vrijeme varchar(25) NOT NULL DEFAULT '',
  vrijemeOpis varchar(25) NOT NULL DEFAULT '',
  temp float NOT NULL DEFAULT -999,
  tempMin float NOT NULL DEFAULT -999,
  tempMax float NOT NULL DEFAULT -999,
  vlaga float NOT NULL DEFAULT -999,
  tlak float NOT NULL DEFAULT -999,
  vjetar float NOT NULL DEFAULT -999,
  vjetarSmjer float NOT NULL DEFAULT -999,
  preuzeto timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT meteo_FK2 FOREIGN KEY (id) REFERENCES parkiralista(id) 
);