/* user_dimension */
/* create user_dimension table */
DROP TABLE IF EXISTS user_dimension;
CREATE TABLE user_dimension (
  user_id varchar(13),
  user_nif varchar(9) NOT NULL,
  user_nome varchar(80) NOT NULL,
  user_telefone varchar(26) NOT NULL,
  PRIMARY KEY (user_id)
);

/* populate user_dimension */
INSERT INTO user_dimension
SELECT 
  concat('user',nif) as user_id,
  nif as user_nif,
  nome as user_nome,
  telefone as user_telefone
FROM user;

select * from user_dimension;

/* location_dimension */
/* create location_dimension table */
DROP TABLE IF EXISTS location_dimension;
CREATE TABLE location_dimension (
  location_id varchar(765),
  morada varchar(255) NOT NULL,
  codigo_espaco varchar(255) NOT NULL,
  codigo_posto varchar(255),
  PRIMARY KEY (location_id)
);

/* populate location_dimension */
INSERT INTO location_dimension
SELECT
	concat(morada,codigo) as location_id,
  	morada,
  	codigo,
  	' '
FROM espaco
UNION
SELECT
	concat(morada,codigo_espaco,codigo),
	morada,
	codigo_espaco,
	codigo
FROM posto;

select * from location_dimension;

/* date_dimension */
/* create date_dimension table */
DROP TABLE IF EXISTS date_dimension;
CREATE TABLE date_dimension (
  date_id    int(11),
  date_time date DEFAULT NULL,
  
  date_year    int(11) DEFAULT NULL,
  semester    int(11) DEFAULT NULL,
  
  month_number   int(11) DEFAULT NULL,
  month_name  char(10) DEFAULT NULL,

  week_number    int(11) DEFAULT NULL,
  week_day_number    int(11) DEFAULT NULL,
  week_day_name char(10) DEFAULT NULL,
  PRIMARY KEY (date_id)
);

/* procedure to populate date_dimension */
DROP PROCEDURE IF EXISTS populate_date_dimension;
DELIMITER //
CREATE PROCEDURE populate_date_dimension()
  BEGIN
    SET @d0 = '2016-01-01';
    SET @d1 = '2017-12-31';
    SET @date = @d0;

    WHILE @date <= @d1 DO
      IF quarter(@date) <= 2 THEN
        SET @semester = 1;
      ELSE
        SET @semester = 2;
      END IF;
      INSERT INTO date_dimension VALUES(
        date_format(@date, "%Y%m%d"),
        @date,
        year(@date),
        @semester,
        month(@date),
        monthname(@date),
        week(@date),
        day(@date),
        dayname(@date)
      );
      SET @date = date_add(@date, INTERVAL 1 DAY);
    END WHILE;
  END //

CALL populate_date_dimension();

select * from date_dimension;

/* time_dimension */
/* create time_dimension table */
DROP TABLE IF EXISTS time_dimension;
CREATE TABLE time_dimension (
  time_id int(4),
  time_of_day time NOT NULL,
  hour_of_day int(2) NOT NULL,

  minute_of_day int(4) NOT NULL,
  minute_of_hour int(2) NOT NULL,
  PRIMARY KEY (time_id)
);

/* procedure to populate time_dimension */
DROP PROCEDURE IF EXISTS populate_time_dimension;
DELIMITER //
CREATE PROCEDURE populate_time_dimension()
  BEGIN
    SET @t0 = '2016-11-11 00:00:00';
    SET @t1 = '2016-11-11 23:59:59';
    SET @time = @t0;
    WHILE @time <= @t1 DO
      SET @minuteofday = ( hour(@time) * 60 ) + minute(@time) + 1;
      INSERT INTO time_dimension VALUES(
      	date_format(@time, "%H%i"),
      	@time,
      	hour(@time),
      	@minuteofday,
      	minute(@time)
      );
      SET @time = date_add(@time, INTERVAL 1 MINUTE);
    END WHILE;
  END //

CALL populate_time_dimension();

select * from time_dimension;

/* reservas_info - facts table */
/* criar tabela reservas_info */
DROP TABLE IF EXISTS reservas_info;
CREATE TABLE reservas_info (
  user_id varchar(13),
  location_id varchar(510),
  time_id int(4),
  date_id int(11),
  montante_pago int NOT NULL,
  duracao int NOT NULL,
  PRIMARY KEY (time_id,date_id),
  FOREIGN KEY(time_id) REFERENCES time_dimension(time_id),
  FOREIGN KEY(date_id) REFERENCES date_dimension(date_id),
  FOREIGN KEY(location_id) REFERENCES location_dimension(location_id),
  FOREIGN KEY(user_id) REFERENCES user_dimension(user_id)
);

/* populate reservas_info */
INSERT INTO reservas_info
SELECT
  concat('user',nif) as user_id,
  concat(morada,codigo) as location_id,
  date_format(data, "%H%i") as time_id,
  date_format(data, "%Y%m%d") as date_id,
  tarifa * (data_fim - data_inicio) as montante_pago,
  data_fim - data_inicio as duracao
FROM aluga NATURAL JOIN oferta NATURAL JOIN espaco NATURAL JOIN paga
UNION
SELECT
  concat('user',nif) as user_id,
  concat(morada, codigo_espaco, codigo) as location_id,
  date_format(data, "%H%i") as time_id,
  date_format(data, "%Y%m%d") as date_id,
  tarifa * (data_fim - data_inicio) as montante_pago,
  data_fim - data_inicio as duracao
FROM aluga NATURAL JOIN oferta NATURAL JOIN posto NATURAL JOIN paga;

select * from reservas_info;
