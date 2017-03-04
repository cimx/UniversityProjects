<html>
<meta charset="utf-8" /> 
    <body>
    <h3>Criar Oferta</h3>
        <form action="updateOfertas.php" method="post">
            <p><input type="hidden" name="operacao" value="inserir"/></p>
            <p>Morada: <input type="text" name="morada" required/></p>
            <p>Código: <input type="text" name="codigo" required/></p>
            <p>Data de inicio: <input type="date" name="data_inicio" required/></p>
            <p>Data de fim: <input type="date" name="data_fim" required/></p>
            <p>Tarifa: <input type="text" name="tarifa" required/></p>
            <p><input type="submit" value="Submit"/></p>
        </form>
<?php
    try
    {
        $host = "db.ist.utl.pt";
        $user ="ist180967";
        $password = "hzan0912";
        $dbname = $user;

        $db = new PDO("mysql:host=$host;dbname=$dbname", $user, $password);
        $db->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

        $sql = "SELECT * FROM oferta;";
        $result = $db->query($sql);
        echo("<h3>Remover Oferta</h3>");
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
            echo("<td><a href=\"updateOfertas.php?morada={$row['morada']}&codigo={$row['codigo']}&data_inicio={$row['data_inicio']}&&operacao=remover\">Remover</a></td>\n");
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
