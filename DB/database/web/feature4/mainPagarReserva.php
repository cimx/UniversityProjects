<html>
<meta charset="utf-8" /> 
    <body>
<?php
    try
    {
        echo("<h3>Pagar Reserva</h3>");
        $host = "db.ist.utl.pt";
        $user ="ist180967";
        $password = "hzan0912";
        $dbname = $user;

        $db = new PDO("mysql:host=$host;dbname=$dbname", $user, $password);
        $db->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

        // 3. Escolher oferta a reservar
        $sql = "SELECT * FROM reserva natural join aluga where reserva.numero not in (select numero from paga);";
        $result = $db->query($sql);
        echo("<table border=\"1\" cellspacing=\"5\">");
        echo("<th>Reserva</th>");
        echo("<th>Morada</th>");
        echo("<th>Código</th>");
        echo("<th>Data de inicio</th>");
        echo("<th>NIF</th>");
        foreach($result as $row)
        {
            echo("<tr>\n");
            echo("<td>{$row['numero']}</td>\n");
            echo("<td>{$row['morada']}</td>\n");
            echo("<td>{$row['codigo']}</td>\n");
            echo("<td>{$row['data_inicio']}</td>\n");
            echo("<td>{$row['nif']}</td>\n");
            echo("<td><a href=\"updatePagarReserva.php?numero={$row['numero']}\">Pagar</a></td>\n");
            echo("</tr>\n");
        }
        echo("</table>\n");

        $db = null;
    }
    catch (PDOException $e)
    {
        echo("<p>ERROR: {$e->getMessage()}</p>");
    }
?>
    </body>
</html>
