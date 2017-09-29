DROP TABLE IF EXISTS stylesheets;

CREATE TABLE stylesheets (
  id int(11) NOT NULL AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  xslt text NOT NULL,
  created DATETIME DEFAULT CURRENT_TIMESTAMP(),
  version_of int(11) DEFAULT NULL,
  active int(3) DEFAULT 1,
  PRIMARY KEY (id)
);


