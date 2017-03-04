<html>
<meta charset="utf-8" />
    <body>
<?php
    function prepareAndExecuteStatement($connection, $statement, $arguments){
        $stmt = $connection->prepare($statement);
        $stmt->execute($arguments);
    }
    function requestNumeroReserva(){
        $numeroReserva = $_REQUEST['numero'];
        echo("<p>Número da reserva a pagar: {$numeroReserva}</p>");
        echo("
            <h3>Escolher método de pagamento</h3>
            <form action='updatePagarReserva.php' method='post'>
                <p><input type='hidden' name='numero' value='{$numeroReserva}'/></p>
                <p>Data de Pagamento: <input type='text' name='data' required/></p>
                <p>Método de Pagamento: <input type='text' name='metodo' required/></p>
                <p><input type='submit' name='submit' value='Concluir Pagamento'/></p>
            </form>
        ");
    }
    function executePagarReserva(){
        $numeroReserva = $_REQUEST['numero'];
        $dataPagamento = $_REQUEST['data'];
        $metodoPagamento = $_REQUEST['metodo'];
        try
        {
            $host = "db.ist.utl.pt";
            $user ="ist180967";
            $password = "hzan0912";
            $dbname = $user;
            $db = new PDO("mysql:host=$host;dbname=$dbname", $user, $password);
            $db->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

            $db->beginTransaction();
            $sql = "INSERT INTO paga VALUES (:numero, :data, :metodo);";
            $data = array(
                ':numero' => $numeroReserva,
                ':data' => $dataPagamento,
                ':metodo' => $metodoPagamento,
            );
            prepareAndExecuteStatement($db, $sql, $data);
            echo("<p>Executing PDO prepared statement for insert paga...</p>");

            $sql = "INSERT INTO estado VALUES (:numero, :time_stamp, :estado);";
            $data = array(
                ':numero' => $numeroReserva,
                ':time_stamp' => $dataPagamento,
                ':estado' => 'Paga',
            );
            prepareAndExecuteStatement($db, $sql, $data);
            echo("<p>Executing PDO prepared statement for insert estado...</p>");

            $db->commit();
            $db = null;
        }
        catch (PDOException $e)
        {
            $db->query("rollback;");
            echo("<p>ERROR: {$e->getMessage()}</p>");
        }
        header( "refresh:3;url=mainPagarReserva.php" );
        }
?>
<?php
    if(isset($_POST['submit']))
    {
        executePagarReserva();
    }
    else {
        requestNumeroReserva();
    }
?>
    </body>
</html>
