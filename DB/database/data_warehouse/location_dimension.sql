DROP TABLE IF EXISTS location_dimension;
CREATE TABLE location_dimension (
  location_id varchar(765) NOT NULL,
  morada varchar(255) NOT NULL,
  codigo_espaco varchar(255) NOT NULL,
  codigo_posto varchar(255),
  location_foto varchar(255),
  PRIMARY KEY (location_id)
);

INSERT INTO location_dimension
SELECT
	concat(morada,codigo) as location_id,
  	morada,
  	codigo,
  	NULL,
  	foto
FROM espaco natural join alugavel;

INSERT INTO location_dimension
SELECT
	concat(morada,codigo_espaco,codigo),
	morada,
	codigo_espaco,
	codigo,
	foto
FROM posto natural join alugavel;
