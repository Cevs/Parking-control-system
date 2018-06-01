CREATE TABLE korisnici (
  id int UNSIGNED AUTO_INCREMENT PRIMARY KEY, 
  korisnicko_ime varchar(255) NOT NULL DEFAULT "",
  lozinka varchar(255) NOT NULL DEFAULT "",
  prezime varchar(255) NOT NULL DEFAULT "",
  ime varchar(255) NOT NULL DEFAULT ""
);