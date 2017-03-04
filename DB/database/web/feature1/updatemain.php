<html>
<meta charset="utf-8" /> 
    <body>
<?php
    function prepareAndExecuteStatement($connection, $statement, $arguments){
        $stmt = $connection->prepare($statement);
        $stmt->execute($arguments);
    }
    $tipo = $_REQUEST['tipo'];
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
        if ($tipo == "edificio") {
            $moradaEdificio = $_REQUEST['morada'];
            if ($operacao == "inserir") {
                $sql = "INSERT INTO edificio VALUES (:morada);";
                $data = array(':morada' => $moradaEdificio);

                prepareAndExecuteStatement($db, $sql, $data);
                echo("<p>Executing PDO prepared statement for insert edificio...</p>");
            }
            elseif ($operacao == "remover"){
                $sql = "DELETE FROM edificio WHERE morada = :morada;";
                $data = array(':morada' => $moradaEdificio);
                prepareAndExecuteStatement($db, $sql, $data);

                echo("<p>Executing PDO prepared statement for delete edificio...</p>");
            }
        }
        elseif ($tipo == "espaco"){
            $moradaEspaco = $_REQUEST['morada'];
            $codigoEspaco = $_REQUEST['codigo'];
            if ($operacao == "inserir") {
                $fotografia = $_REQUEST['foto'];
                $sql = "INSERT INTO alugavel VALUES (:morada, :codigo, :fotografia);";
                $data = array(':morada' => $moradaEspaco, ':codigo' => $codigoEspaco, ':fotografia' => $fotografia);
                prepareAndExecuteStatement($db, $sql, $data);

                $sql = "INSERT INTO espaco VALUES (:morada, :codigo);";
                $data = array(':morada' => $moradaEspaco, ':codigo' => $codigoEspaco);
                prepareAndExecuteStatement($db, $sql, $data);

                echo("<p>Executing PDO prepared statement for insert espaco...</p>");
            }
            elseif ($operacao == "remover"){
                $sql = "DELETE FROM espaco WHERE morada = :morada AND codigo = :codigo";
                $data = array(':morada' => $moradaEspaco, ':codigo' => $codigoEspaco);
                prepareAndExecuteStatement($db, $sql, $data);

                $sql = "DELETE FROM alugavel WHERE morada = :morada AND codigo = :codigo";
                $data = array(':morada' => $moradaEspaco, ':codigo' => $codigoEspaco);
                prepareAndExecuteStatement($db, $sql, $data);

                echo("<p>Executing PDO prepared statement for delete espaco...</p>");
            }
        }
        elseif ($tipo == "posto"){
            $moradaPosto = $_REQUEST['morada'];
            $codigoPosto = $_REQUEST['codigoPosto'];
            $codigoEspaco = $_REQUEST['codigoEspaco'];
            if ($operacao == "inserir") {
                $fotografia = $_REQUEST['foto'];
                $sql = "INSERT INTO alugavel VALUES (:moradaPosto, :codigoPosto, :fotografia);";
                $data = array(':moradaPosto' => $moradaPosto, ':codigoPosto' => $codigoPosto, ':fotografia' => $fotografia);
                prepareAndExecuteStatement($db, $sql, $data);

                $sql = "INSERT INTO posto VALUES (:moradaPosto, :codigoPosto, :codigoEspaco);";
                $data = array(':moradaPosto' => $moradaPosto, ':codigoPosto' => $codigoPosto, ':codigoEspaco' => $codigoEspaco);
                prepareAndExecuteStatement($db, $sql, $data);

                echo("<p>Executing PDO prepared statement for insert posto...</p>");
            }
            elseif ($operacao == "remover") {
                $sql = "DELETE FROM posto WHERE morada = :moradaPosto AND codigo = :codigoPosto AND codigo_espaco = :codigoEspaco;";
                $data = array(':moradaPosto' => $moradaPosto, ':codigoPosto' => $codigoPosto, ':codigoEspaco' => $codigoEspaco);
                prepareAndExecuteStatement($db, $sql, $data);

                $sql = "DELETE FROM alugavel WHERE morada = :moradaPosto AND codigo = :codigoPosto";
                $data = array(':moradaPosto' => $moradaPosto, ':codigoPosto' => $codigoPosto);
                prepareAndExecuteStatement($db, $sql, $data);

                echo("<p>Executing PDO prepared statement for delete posto...</p>");
            }
        }
        $db->commit();
        $db = null;
    }
    catch (PDOException $e)
    {
        $db->query("rollback;");
        echo("<p>ERROR: {$e->getMessage()}</p>");
    }
    header( "refresh:5;url=main.php" );
?>
    </body>
</html>
