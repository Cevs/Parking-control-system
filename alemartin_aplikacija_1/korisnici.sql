CREATE TABLE korisnici (
  id int UNSIGNED AUTO_INCREMENT PRIMARY KEY, 
  korisnicko_ime varchar(255) NOT NULL,
  lozinka varchar(255) NOT NULL,
  prezime varchar(255) NOT NULL,
  ime varchar(255) NOT NULL
);