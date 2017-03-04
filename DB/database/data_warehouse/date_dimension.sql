DROP TABLE IF EXISTS date_dimension;
CREATE TABLE date_dimension (
  date_id    int(11) NOT NULL,
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

DROP PROCEDURE IF EXISTS populate_date_dimension;
DELIMITER //
CREATE PROCEDURE populate_date_dimension()
  BEGIN
    SET @d0 = '2016-01-01';
    SET @d1 = '2017-12-31';
    SET @date = date_sub(@d0, INTERVAL 1 DAY);

    WHILE date_add(@date, INTERVAL 1 DAY) <= @d1 DO
      SET @date = date_add(@date, INTERVAL 1 DAY);
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
    END WHILE;
  END //

CALL populate_date_dimension();