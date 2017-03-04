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

        /* Form para inserir Edificio */
        echo("<h3>Inserir Edificio</h3>
            <form action='updatemain.php' method='post'>
                <p><input type='hidden' name='tipo' value='edificio'/></p>
                <p><input type='hidden' name='operacao' value='inserir'/></p>
                <p>Morada do Edificio: <input type='text' name='morada' required/></p>
                <p><input type='submit' value='Submit'/></p>
            </form>"
        );

        /* Tabela para remover Edificio */
        $sql = "SELECT * FROM edificio;";
        $result = $db->query($sql);
        echo("<h3>Remover Edificio</h3>");
        echo("<table border=\"1\" cellspacing=\"5\">");
        echo("<th>Morada</th>");
        foreach($result as $row)
        {
            echo("<tr>\n");
            echo("<td>{$row['morada']}</td>\n");
            echo("<td><a href=\"updatemain.php?tipo=edificio&morada={$row['morada']}&operacao=remover\">Remover</a></td>\n");
            echo("</tr>\n");
        }
        echo("</table>\n");

        /* Form para inserir Espaço */
        echo("<h3>Inserir Espaço</h3>
        <form action='updatemain.php' method='post'>
            <p><input type='hidden' name='tipo' value='espaco'/></p>
            <p><input type='hidden' name='operacao' value='inserir'/></p>
            <p>Morada do Espaço: <input type='text' name='morada' required/></p>
            <p>Código do Espaço: <input type='text' name='codigo' required/></p>
            <p>Fotografia: <input type='text' name='foto' required/></p>
            <p><input type='submit' value='Submit'/></p>
        </form>");

        /* Tabela para remover Espaço */
        $sql = "SELECT * FROM espaco;";
        $result = $db->query($sql);
        echo("<h3>Remover Espaço</h3>");
        echo("<table border=\"1\" cellspacing=\"5\">");
        echo("<th>Morada</th><th>Código</th>");
        foreach($result as $row)
        {
            echo("<tr>\n");
            echo("<td>{$row['morada']}</td>\n");
            echo("<td>{$row['codigo']}</td>\n");
            echo("<td><a href=\"updatemain.php?tipo=espaco&morada={$row['morada']}&codigo={$row['codigo']}&operacao=remover\">Remover</a></td>\n");
            echo("</tr>\n");
        }
        echo("</table>\n");

        /* Form para inserir Posto */
        echo("<h3>Inserir Posto</h3>
        <form action='updatemain.php' method='post'>
            <p><input type='hidden' name='tipo' value='posto'/></p>
            <p><input type='hidden' name='operacao' value='inserir'/></p>
            <p>Morada do Posto: <input type='text' name='morada' required/></p>
            <p>Código do Posto: <input type='text' name='codigoPosto' required/></p>
            <p>Código do Espaço: <input type='text' name='codigoEspaco' required/></p>
            <p>Fotografia: <input type='text' name='foto' required/></p>
            <p><input type='submit' value='Submit'/></p>
        </form>");

        /* Tabela para remover Posto */
        $sql = "SELECT * FROM posto;";
        $result = $db->query($sql);
        echo("<h3>Remover Posto</h3>");
        echo("<table border=\"1\" cellspacing=\"5\">");
        echo("<th>Morada</th><th>Código do Posto</th><th>Código do Espaço</th>");
        foreach($result as $row)
        {
            echo("<tr>\n");
            echo("<td>{$row['morada']}</td>\n");
            echo("<td>{$row['codigo']}</td>\n");
            echo("<td>{$row['codigo_espaco']}</td>\n");
            echo("<td><a href=\"updatemain.php?tipo=posto&morada={$row['morada']}&codigoPosto={$row['codigo']}&codigoEspaco={$row['codigo_espaco']}&operacao=remover\">Remover</a></td>\n");
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
