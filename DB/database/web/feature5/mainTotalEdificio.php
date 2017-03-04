<html>
<meta charset="utf-8" />
    <body>
    <h2>Para um dado edificio, mostrar o total realizado por cada espaço</h2>
<?php
    function prepareAndExecuteStatement($connection, $statement, $arguments){
        $stmt = $connection->prepare($statement);
        $stmt->execute($arguments);
        return $stmt->fetchAll();
    }
    function requestMoradaEdificio(){
        echo("
            <h3>Escolher morada do edificio</h3>
            <form action='mainTotalEdificio.php' method='post'>
                <p>Morada do Edificio: <input type='text' name='morada' required/></p>
                <p><input type='submit' name='submit' value='Submit'/></p>
            </form>
        ");
    }
    function executeTotalEdificio(){
        try
        {
            $host = "db.ist.utl.pt";
            $user ="ist180967";
            $password = "hzan0912";
            $dbname = $user;
            $db = new PDO("mysql:host=$host;dbname=$dbname", $user, $password);
            $db->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

			
            $sql = "SELECT Res.morada, Res.codigo, SUM(Res.total) as totalRealizado
                    FROM( ( SELECT S.morada, S.codigo, SUM(S.total) AS total
                            FROM(( SELECT morada, codigo, SUM(tarifa * (data_fim - data_inicio)) AS total
                                   FROM espaco NATURAL JOIN aluga NATURAL JOIN oferta NATURAL JOIN paga
                                   GROUP BY morada, codigo
                                   HAVING morada = :morada)
                                 UNION
                                 ( SELECT morada, codigo_espaco, SUM(tarifa * (data_fim - data_inicio)) AS total
                                   FROM posto NATURAL JOIN aluga NATURAL JOIN oferta NATURAL JOIN paga
                                   GROUP BY morada, codigo
                                   HAVING morada = :morada)
                            ) AS S
                            GROUP BY morada, codigo)
                            UNION
                          ( SELECT morada, codigo, 0 AS total FROM espaco WHERE morada = :morada)
                    ) AS Res
                    GROUP BY morada, codigo;";
            $moradaEdificio = $_REQUEST['morada'];
            $data = array(':morada' => $moradaEdificio);
            $stmt = $db->prepare($sql);
            $stmt->execute($data);
            $result = $stmt->fetchAll();

			
			
            echo("<h3>Total realizado no edificio com morada '{$data[':morada']}'</h3>");
            echo("<table border=\"1\" cellspacing=\"5\">");
            echo("<th>Código do Espaço</th>");
            echo("<th>Total Realizado</th>");
            foreach($result as $row)
            {
                echo("<tr>\n");
                echo("<td>{$row['codigo']}</td>\n");
                echo("<td>{$row['totalRealizado']}</td>\n");
                echo("</tr>\n");
            }
            echo("</table>\n");
            $db = null;
        }
        catch (PDOException $e)
        {
            $db->query("rollback;");
            echo("<p>ERROR: {$e->getMessage()}</p>");
        }
        header( "refresh:10;url=mainTotalEdificio.php" );
    }
?>
<?php
    if(isset($_POST['submit']))
    {
        executeTotalEdificio();
    }
    else {
        requestMoradaEdificio();
    }
?>
    </body>
</html>
