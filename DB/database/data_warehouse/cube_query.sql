/* 	como nao existe a funcao cube em mysql
	fazemos uma query com group by's diferentes para
	gerar as combinacoes todas possiveis (como seria num cube)
*/
SELECT codigo_espaco, codigo_posto, month_number, week_day_number, avg(montante_pago)
FROM reservas_info
  NATURAL JOIN location_dimension
  NATURAL JOIN date_dimension
GROUP BY codigo_espaco, codigo_posto, month_number, week_day_number WITH ROLLUP

UNION

SELECT codigo_espaco, codigo_posto, month_number, week_day_number, avg(montante_pago)
FROM reservas_info
  NATURAL JOIN location_dimension
  NATURAL JOIN date_dimension
GROUP BY codigo_posto, month_number, week_day_number, codigo_espaco  WITH ROLLUP

UNION

SELECT codigo_espaco, codigo_posto, month_number, week_day_number, avg(montante_pago)
FROM reservas_info
  NATURAL JOIN location_dimension
  NATURAL JOIN date_dimension
GROUP BY month_number, week_day_number, codigo_espaco, codigo_posto  WITH ROLLUP

UNION

SELECT codigo_espaco, codigo_posto, month_number, week_day_number, avg(montante_pago)
FROM reservas_info
  NATURAL JOIN location_dimension
  NATURAL JOIN date_dimension
GROUP BY week_day_number, codigo_espaco, codigo_posto, month_number WITH ROLLUP

UNION

SELECT codigo_espaco, codigo_posto, month_number, week_day_number, avg(montante_pago)
FROM reservas_info
  NATURAL JOIN location_dimension
  NATURAL JOIN date_dimension
GROUP BY codigo_espaco, month_number, codigo_posto, week_day_number WITH ROLLUP

UNION

SELECT codigo_espaco, codigo_posto, month_number, week_day_number, avg(montante_pago)
FROM reservas_info
  NATURAL JOIN location_dimension
  NATURAL JOIN date_dimension
GROUP BY codigo_posto, week_day_number, codigo_espaco, month_number WITH ROLLUP;