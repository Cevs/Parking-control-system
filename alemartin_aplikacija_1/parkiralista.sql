CREATE TABLE parkiralista (
  id int UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  naziv varchar(99) NOT NULL DEFAULT '',
  adresa varchar(255) NOT NULL DEFAULT '',
  latitude float(6) NOT NULL DEFAULT 0.0,
  longitude float(6) NOT NULL DEFAULT 0.0
);