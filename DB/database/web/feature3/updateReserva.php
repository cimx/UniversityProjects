<html>
<meta charset="utf-8" />
    <body>
<?php
    function prepareAndExecuteStatement($connection, $statement, $arguments){
        $stmt = $connection->prepare($statement);
        $stmt->execute($arguments);
    }
    function requestNumeroAndNif(){
        $moradaOferta = $_REQUEST['morada'];
        $codigoOferta = $_REQUEST['codigo'];
        $data_inicio = $_REQUEST['data_inicio'];
        echo("<h3>Oferta escolhida</h3>");
        echo("<p>Morada da oferta: {$moradaOferta}</p>");
        echo("<p>Código da oferta: {$codigoOferta}</p>");
        echo("<p>Data de inicio: {$data_inicio}</p>");
        /* Form para definir Numero da Reserva e NIF do User */
        echo("
            <h3>Escolher número da Reserva e NIF do User </h3>
            <form action='updateReserva.php' method='post'>
                <p><input type='hidden' name='morada' value='{$moradaOferta}'/></p>
                <p><input type='hidden' name='codigo' value='{$codigoOferta}'/></p>
                <p><input type='hidden' name='data_inicio' value='{$data_inicio}'/></p>
                <p>Número da reserva: <input type='text' name='numero' required/></p>
                <p>NIF do user: <input type='text' name='nif' required/></p>
                <p><input type='submit' name='submit' value='Concluir reserva'/></p>
            </form>"
        );
    }
    function executeCriarReserva(){
        try
        {
            $host = "db.ist.utl.pt";
            $user ="ist180967";
            $password = "hzan0912";
            $dbname = $user;
            $db = new PDO("mysql:host=$host;dbname=$dbname", $user, $password);
            $db->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
            $db->beginTransaction();

            $moradaOferta = $_REQUEST['morada'];
            $codigoOferta = $_REQUEST['codigo'];
            $data_inicio = $_REQUEST['data_inicio'];
            $numeroReserva = $_REQUEST['numero'];
            $nifUser = $_REQUEST['nif'];

            $sql = "INSERT INTO reserva VALUES (:numero);";
            $data = array(
                ':numero' => $numeroReserva,
            );
            prepareAndExecuteStatement($db, $sql, $data);
            echo("<p>Executing PDO prepared statement for insert new reserva...</p>");


            $sql = "INSERT INTO estado VALUES (:numero, :time_stamp, :estado);";
            $data = array(
                ':numero' => $numeroReserva,
                ':time_stamp' => $data_inicio,
                ':estado' => 'Aceite',
            );
            prepareAndExecuteStatement($db, $sql, $data);
            echo("<p>Executing PDO prepared statement for insert new estado...</p>");            

            $sql = "INSERT INTO aluga VALUES (:morada, :codigo, :data_inicio, :nif, :numero);";
            $data = array(
                ':morada' => $moradaOferta,
                ':codigo' => $codigoOferta,
                ':data_inicio' => $data_inicio,
                ':nif' => $nifUser,
                'numero' => $numeroReserva,
            );
            prepareAndExecuteStatement($db, $sql, $data);
            echo("<p>Executing PDO prepared statement for insert new aluga...</p>");

            $db->commit();
            $db = null;
        }
        catch (PDOException $e)
        {
            $db->query("rollback;");
            echo("<p>ERROR: {$e->getMessage()}</p>");
        }
        header( "refresh:3;url=mainReserva.php" );
    }
?>
<?php
    if(isset($_POST['submit']))
    {
        executeCriarReserva();
    }
    else {
        requestNumeroAndNif();
    }
?>
    </body>
</html>
