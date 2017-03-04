DROP TABLE IF EXISTS reservas_info;
CREATE TABLE reservas_info (
  reserva_id varchar(255) NOT NULL,
  user_id varchar(13) NOT NULL,
  location_id varchar(510) NOT NULL,
  time_id int(4) NOT NULL,
  date_id int(11) NOT NULL,
  montante_pago int NOT NULL,
  duracao int NOT NULL,
  PRIMARY KEY (reserva_id,time_id,date_id),
  FOREIGN KEY(time_id) REFERENCES time_dimension(time_id),
  FOREIGN KEY(date_id) REFERENCES date_dimension(date_id),
  FOREIGN KEY(location_id) REFERENCES location_dimension(location_id),
  FOREIGN KEY(user_id) REFERENCES user_dimension(user_id)
);

INSERT INTO reservas_info
SELECT
  numero as reserva_id,
  concat('user',nif) as user_id,
  concat(morada,codigo) as location_id,
  date_format(data, "%H%i") as time_id,
  date_format(data, "%Y%m%d") as date_id,
  tarifa * (data_fim - data_inicio) as montante_pago,
  data_fim - data_inicio as duracao
FROM aluga NATURAL JOIN oferta NATURAL JOIN espaco NATURAL JOIN paga
UNION
SELECT
  numero as reserva_id,
  concat('user',nif) as user_id,
  concat(morada, codigo_espaco, codigo) as location_id,
  date_format(data, "%H%i") as time_id,
  date_format(data, "%Y%m%d") as date_id,
  tarifa * (data_fim - data_inicio) as montante_pago,
  data_fim - data_inicio as duracao
FROM aluga NATURAL JOIN oferta NATURAL JOIN posto NATURAL JOIN paga;
