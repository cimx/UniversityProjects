/* Query a
-Quais os espacos com postos que nunca foram alugados
*/

SELECT DISTINCT morada, codigo_espaco 
FROM posto
WHERE (morada, codigo) NOT IN(
	SELECT DISTINCT A.morada, A.codigo FROM aluga A, estado B 
	WHERE A.numero=B.numero
	AND B.estado="Aceite");

/* Query b 
-Quais edificios com um numero de reservas superior a media?
*/

SELECT DISTINCT morada 
FROM aluga
GROUP BY morada 
HAVING count(numero) > (SELECT AVG(valor)
                        FROM(
                          SELECT count(numero) AS valor
                          FROM aluga
                          GROUP BY morada
                          ) AS S);
						  
/* Query c 
-Quais utilizadores cujos alugaveis foram fiscalizados sempre pelo mesmo fiscal 
*/

SELECT S.nif
FROM (
	SELECT DISTINCT user.nif, fiscaliza.id
	FROM user, arrenda, fiscaliza
	WHERE user.nif=arrenda.nif AND fiscaliza.morada=arrenda.morada AND fiscaliza.codigo=arrenda.codigo
  ) AS S
GROUP BY S.nif 
HAVING count(S.id)=1;
------------
SELECT nif, nome
FROM user NATURAL JOIN arrenda NATURAL JOIN fiscaliza
GROUP BY nif
HAVING count(DISTINCT id)=1;


/* Query d
-Qual o montante total realizado (pago) por cada espaço durante o ano de 2016?
	(Assuma que a tarifa indicada na oferta é diária. Deve considerar os casos em que o
	espaço foi alugado totalmente ou por postos.)
*/
	
SELECT Res.morada, Res.codigo, SUM(Res.total) AS totalRealizado
FROM( ( SELECT S.morada, S.codigo, SUM(S.total) AS total
        FROM(( SELECT morada, codigo, SUM(tarifa * (data_fim - data_inicio)) AS total
               FROM espaco NATURAL JOIN aluga NATURAL JOIN oferta NATURAL JOIN paga
               WHERE year(paga.data) = '2016'
               GROUP BY morada, codigo)
             UNION
             ( SELECT morada, codigo_espaco, SUM(tarifa * (data_fim - data_inicio)) AS total
               FROM posto NATURAL JOIN aluga NATURAL JOIN oferta NATURAL JOIN paga
               WHERE year(paga.data) = '2016'
               GROUP BY morada, codigo)
        ) AS S
        GROUP BY morada, codigo)
        UNION
      ( SELECT morada, codigo, 0 AS total FROM espaco )
) AS Res
GROUP BY morada, codigo;

/*
Query e
- Quais os espaços de trabalho cujos postos nele contidos foram todos alugados?
	(Por alugado entende-se um posto de trabalho que tenha pelo menos uma oferta aceite,
	independentemente das suas datas.)
*/

SELECT DISTINCT morada, codigo_espaco
	FROM posto
	WHERE(morada, codigo_espaco) NOT IN (
		SELECT DISTINCT morada, codigo_espaco
			FROM posto
			WHERE (morada, codigo) NOT IN(
				SELECT DISTINCT A.morada, A.codigo
				FROM aluga A, estado B
				WHERE A.numero=B.numero AND B.estado= 'Aceite' ));





