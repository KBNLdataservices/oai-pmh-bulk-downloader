CREATE TABLE repositories (
  id int(11) NOT NULL AUTO_INCREMENT,
  name VARCHAR(255) DEFAULT NULL,
  url varchar(255) DEFAULT NULL,
  metadataPrefix varchar(255) DEFAULT NULL,
  oai_set varchar(50) DEFAULT NULL,
  datestamp varchar(50) DEFAULT NULL,
  schedule int(11) NOT NULL,
  enabled int(3) DEFAULT 0 NOT NULL,
  lastHarvest TIMESTAMP(6) DEFAULT NULL,
  PRIMARY KEY (id)
);


