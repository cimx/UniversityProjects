<html>
<meta charset="utf-8" />
    <body>
<?php
    function prepareAndExecuteStatement($connection, $statement, $arguments){
        $stmt = $connection->prepare($statement);
        $stmt->execute($arguments);
    }
    $moradaOferta = $_REQUEST['morada'];
    $codigoOferta = $_REQUEST['codigo'];
    $data_inicio = $_REQUEST['data_inicio'];
    $operacao = $_REQUEST['operacao'];
    try
    {
        $host = "db.ist.utl.pt";
        $user ="ist180967";
        $password = "hzan0912";
        $dbname = $user;
        $db = new PDO("mysql:host=$host;dbname=$dbname", $user, $password);
        $db->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

        $db->beginTransaction();
        if ($operacao == "inserir") {
            $data_fim = $_REQUEST['data_fim'];
            $tarifa = $_REQUEST['tarifa'];
            $sql = "INSERT INTO oferta VALUES (:morada, :codigo, :data_inicio, :data_fim, :tarifa);";
            $data = array(
                ':morada' => $moradaOferta,
                ':codigo' => $codigoOferta,
                ':data_inicio' => $data_inicio,
                ':data_fim' => $data_fim,
                ':tarifa' => $tarifa,
            );
            prepareAndExecuteStatement($db, $sql, $data);
            echo("<p>Executing PDO prepared statement for insert oferta...</p>");
        }
        elseif ($operacao == "remover"){
            $sql = "DELETE FROM oferta WHERE morada = :morada AND codigo = :codigo AND data_inicio = :data_inicio;";
            $data = array(
                ':morada' => $moradaOferta,
                ':codigo' => $codigoOferta,
                ':data_inicio' => $data_inicio,
            );
            prepareAndExecuteStatement($db, $sql, $data);
            echo("<p>Executing PDO prepared statement for delete oferta...</p>");
        }
        $db->commit();
        $db = null;
    }
    catch (PDOException $e)
    {
        $db->query("rollback;");
        echo("<p>ERROR: {$e->getMessage()}</p>");
    }
    header( "refresh:5;url=mainOfertas.php" );
?>
    </body>
</html>
