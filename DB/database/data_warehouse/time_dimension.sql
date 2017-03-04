DROP TABLE IF EXISTS time_dimension;
CREATE TABLE time_dimension (
  time_id int(4) NOT NULL,
  time_of_day time NOT NULL,
  hour_of_day int(2) NOT NULL,

  minute_of_day int(4) NOT NULL,
  minute_of_hour int(2) NOT NULL,
  PRIMARY KEY (time_id)
);

DROP PROCEDURE IF EXISTS populate_time_dimension;
DELIMITER //
CREATE PROCEDURE populate_time_dimension()
  BEGIN
    SET @t0 = '2016-11-11 00:00:00';
    SET @t1 = '2016-11-11 23:59:59';
    SET @time = date_sub(@t0, INTERVAL 1 MINUTE);
    WHILE date_add(@time, INTERVAL 1 MINUTE) <= @t1 DO
      SET @time = date_add(@time, INTERVAL 1 MINUTE);
      SET @minuteofday = ( hour(@time) * 60 ) + minute(@time) + 1;
      INSERT INTO time_dimension VALUES(
      	date_format(@time, "%H%i"),
      	@time,
      	hour(@time),
      	@minuteofday,
      	minute(@time)
      );
    END WHILE;
  END //

CALL populate_time_dimension();