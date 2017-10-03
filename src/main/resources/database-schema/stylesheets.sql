DROP TABLE IF EXISTS stylesheets;

CREATE TABLE stylesheets (
  id int(11) NOT NULL AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  xslt text NOT NULL,
  created DATETIME DEFAULT CURRENT_TIMESTAMP(),
  is_latest int(3) DEFAULT 1,
  PRIMARY KEY (id)
);

