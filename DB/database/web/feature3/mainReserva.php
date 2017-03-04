<html>
<meta charset="utf-8" />
    <body>
<?php
    try
    {
        $host = "db.ist.utl.pt";
        $user ="ist180967";
        $password = "hzan0912";
        $dbname = $user;

        $db = new PDO("mysql:host=$host;dbname=$dbname", $user, $password);
        $db->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

        // Escolher oferta a reservar
        $sql = "SELECT * FROM oferta;";
        $result = $db->query($sql);
        echo("<h3>Reservar Oferta</h3>");
        echo("<table border=\"1\" cellspacing=\"5\">");
        echo("<th>Morada</th>");
        echo("<th>Código</th>");
        echo("<th>Data de Inicio</th>");
        echo("<th>Data de Fim</th>");
        echo("<th>Tarifa</th>");
        foreach($result as $row)
        {
            echo("<tr>\n");
            echo("<td>{$row['morada']}</td>\n");
            echo("<td>{$row['codigo']}</td>\n");
            echo("<td>{$row['data_inicio']}</td>\n");
            echo("<td>{$row['data_fim']}</td>\n");
            echo("<td>{$row['tarifa']}</td>\n");
            echo("<td><a href=\"updateReserva.php?morada={$row['morada']}&codigo={$row['codigo']}&data_inicio={$row['data_inicio']}\">Reservar</a></td>\n");
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
