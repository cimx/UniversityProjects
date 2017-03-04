/* RI-1 - Não podem existir ofertas com datas sobrepostas */


		DROP TRIGGER IF EXISTS ofertasDatasSobrepostas;
		DELIMITER //
		CREATE TRIGGER ofertasDatasSobrepostas BEFORE INSERT ON oferta
		FOR EACH ROW
		BEGIN
		IF EXISTS(
			SELECT morada, codigo
			FROM (
				SELECT morada, codigo, data_inicio, data_fim
				FROM oferta
				WHERE morada = new.morada AND codigo = new.codigo
			) AS O
			WHERE new.data_inicio <= O.data_fim AND new.data_fim >= O.data_inicio
			) THEN
		CALL ERROR;
		END IF;
		END//
		DELIMITER ;

		
/* 	Testes do trigger 1
	1. data_inicio dentro do intervalo e data_fim fora do intervalo 
	2. data_inicio fora do intervalo e data_fim dentro do intervalo
	3. data_inicio e data_fim dentro do intervalo
	4. data_inicio e data_fim iguais ao intervalo
	5. data_inicio e data_fim fora invalidos
	6. data_inicio e data_fim fora validos
	*/

/* 	insert para testes do trigger 1
	INSERT IGNORE INTO oferta VALUES ('IST','DEI','2017-03-01','2017-03-31','1111');
	*/
/*
	INSERT INTO oferta VALUES ('IST','DEI','2017-03-03','2017-03-01','1111');
	INSERT INTO oferta VALUES ('IST','DEI','2017-02-01','2017-03-07','1111');
	INSERT INTO oferta VALUES ('IST','DEI','2017-03-04','2017-03-24','1111');
	INSERT INTO oferta VALUES ('IST','DEI','2017-03-01','2017-03-31','1111');
	INSERT INTO oferta VALUES ('IST','DEI','2017-02-01','2017-04-13','1111');
	INSERT INTO oferta VALUES ('IST','DEI','2017-04-01','2017-04-30','1111');
	*/


/* RI-2: "A data de pagamento de uma reserva paga tem de 
ser superior ao timestamp do último estado dessa reserva */


		DROP TRIGGER IF EXISTS dataPagamentoSuperiorUltimoEstado;
		DELIMITER //
		CREATE TRIGGER dataPagamentoSuperiorUltimoEstado BEFORE INSERT ON paga
		FOR EACH ROW
		BEGIN
		IF EXISTS(
			SELECT *
			FROM estado
			WHERE numero = new.numero
			AND
			new.data <= (
				SELECT MAX(time_stamp)
				FROM estado
				WHERE numero = new.numero
				)
			) THEN
		CALL ERROR;
		END IF;
		END//
		DELIMITER ;

/* Testes do trigger 2

  Reserva de Teste (existente no populate do fenix)
  numero de reserva: 2016-12
  ultimo estado: 2016-12	2016-01-01 01:33:19	Aceite

  Casos de Teste:
  1. pagamento com data valida
  2. pagamento com data inferior/invalida
  3. pagamento com data sobreposta/invalida

  INSERT INTO paga VALUES ('2016-12',	'2016-01-01 01:33:29', 'Cartão Crédito');
  INSERT INTO paga VALUES ('2016-12', 	'2016-01-01 01:33:10', 'Cartão Crédito');
  INSERT INTO paga VALUES ('2016-12',	'2016-01-01 01:33:19', 'Cartão Crédito');

*/
