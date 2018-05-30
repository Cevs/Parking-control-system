CREATE TABLE dnevnik (
  id int UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  korisnik varchar(25) NOT NULL DEFAULT '',
  url varchar(255) NOT NULL DEFAULT '',
  ipadresa varchar(25) NOT NULL DEFAULT '',
  vrsta_zahtjeva varchar(50) NOT NULL DEFAULT '',
  sadrzaj_zahtjeva varchar(255) NOT NULL DEFAULT '',
  vrijeme timestamp,
  trajanje int NOT NULL DEFAULT 0,
  status int NOT NULL DEFAULT 0
);