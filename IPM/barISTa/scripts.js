var active_menu_id = "inicial";
var previous_menu_ids = [];
var active_tab_id = "";
var flag_saida=false;
var chamou_taxi=false;
var saiu=false;
var totalSeconds;
var count;
var bebado = 0; //>2 esta bebado!
var flag_taxi = false;
var flag_canc_taxi = false;

var n_bebida = 0;
var n_comida = 0;

var pedidos_comida_existe = false;
var pedidos_bebida_existe = false;

var perder_progresso_bebida = true;
var perder_progresso_comida = true;
var perder_progresso_nextmenu_id = "";

var personalizar_bebida_atual = "";
var personalizar_comida_atual = "";

var total_mais_tarde = 0;

var descricao_backup = "";

var id_like_dislike = 10;

/* Mapa de correspondencias bebida-preco */
var mapa_bebidas = {
    "Cerveja" : 1,
    "Vodka" : 3,
    "Absinto": 4,
    "Gim" : 5,
    "Ginja" : 6,
    "Whiskey" : 9,
    "Vinho" : 15,
    "Somersby" : 2,
    
    "Água" : 1,
    "Coca-Cola" : 2,
    "Sprite" : 2,
    "Chá" : 5,
    "Café" : 4,
    "Ice-tea" : 3,
};
/* Mapa de correspondencias comida-preco */
var mapa_comidas = {
    "Tremoços" : 1,
    "Pipocas" : 3,
    "Batata Frita" : 2,
    "Amendoins" : 4,
    "Amêndoas" : 2,
    "Salame" : 3,
    
    "Hamburguer" : 1,
    "Cachorro" : 3,
    "Pizza" : 2,
    "Baguete" : 5,
    "Bitoque": 9,
    "Lasanha" : 8,
    "Feijoada" : 14,
    
};

var bebidas_personalizadas = new Array();
var comidas_personalizadas = new Array();

/* Funcao para comecar o barISTa */
$("#ecra_inicial").click(function() {
   $( "#ecra_inicial" ).toggle("puff");
    //document.getElementById("ecra_inicial").style.display = "none";
    document.getElementById("sidebar").style.display = "block";
    document.getElementById("inicial").style.display = "block";
    active_menu_id = "inicial";
});

/* Funcao para mudar o menu */
function menuChange(new_id) {
    if(active_menu_id == "menu_bebidas" && perder_progresso_nextmenu_id == ""){
        checkPedidosBebida();
        if(pedidos_bebida_existe == true && new_id != active_menu_id){
            perder_progresso_bebida = false;
            perder_progresso_nextmenu_id = new_id;
            popupProgressoBebidas("abrir");
        }
        if(pedidos_bebida_existe == true && new_id == "menu_bebidas"){
            return;
        }
    }
    if(active_menu_id == "menu_comida" && perder_progresso_nextmenu_id == ""){
        checkPedidosComida();
        if(pedidos_comida_existe == true && new_id != active_menu_id){
            perder_progresso_comida = false;
            perder_progresso_nextmenu_id = new_id;
            popupProgressoComidas("abrir");
        }
        if(pedidos_comida_existe == true && new_id == "menu_comida"){
            return;
        }
    }
    if (new_id != active_menu_id) {
        if(active_menu_id == "menu_bebidas" && perder_progresso_bebida == false){
            return;
        }
        if(active_menu_id == "menu_comida" && perder_progresso_comida == false){
            return;
        }
        else{
            document.getElementById(active_menu_id).style.display = "none";
            document.getElementById(new_id).style.display = "block";
            if (new_id != "inicial") {
                previous_menu_ids.push(active_menu_id);
            }
            active_menu_id = new_id;            
        }

    }
    if (active_menu_id=="menu_comboio"){
        mapaComboio('lisboa');
    }
    if(new_id == "menu_bebidas" && perder_progresso_comida == true){
        clearPedidosComida();
        document.getElementById("cancelar_pedido_id").style.backgroundColor = "rgba(128,128,128,0.3)";
        active_tab_id = "tabela_alcoolicas";
        document.getElementById("tabela_alcoolicas").style.display = "inline-block";
        document.getElementById("tabela_naoalcoolicas").style.display = "none";
        tabChangeColor(new_id, 'alcoolicas', 'nao-alcoolicas');
        $('.searchbebida').val('');
        document.getElementById('reset_bebida').click();
    }
    if(new_id == "menu_comida" && perder_progresso_bebida == true){
        clearPedidosBebida();
        document.getElementById("cancelar_pedido_id_c").style.backgroundColor = "rgba(128,128,128,0.3)";
        active_tab_id = "tabela_aperitivos";
        document.getElementById("tabela_aperitivos").style.display = "inline-block";
        document.getElementById("tabela_refeicoes").style.display = "none";
        tabChangeColor(new_id, 'aperitivos', 'refeicoes');
        $('.searchcomida').val('');
        document.getElementById('reset_comida').click();
    }
    if(new_id == "menu_top_musica"){
        active_tab_id = "tabela_top_bar";
        document.getElementById("tabela_top_bar").style.display = "inline-block";
        document.getElementById("tabela_top_semanal").style.display = "none";
        document.getElementById("tabela_top_mundial").style.display = "none";        
        tabChangeColor("tabela_top_bar", 'top_bar', '');        
    }
    if(new_id == "menu_transportes" && chamou_taxi == true){
        document.getElementById("cancel_taxi").style.bottom = "15px";
        document.getElementById("timer_taxi").style.bottom = "25px";
    }
    if(new_id == "menu_taxi1"){
        popupTaxi("nao_chamar");
    }
    if(new_id == "menu_taxi2"){
        popupTaxi("fechar");
    }   

    
    if(new_id == "menu_sair_1"){
        if(chamou_taxi == true){
            document.getElementById("cancel_taxi").style.bottom = "150px";
            document.getElementById("timer_taxi").style.bottom = "160px";
        }
        if (bebado <3){
            document.getElementById('menu_sair_1').style.display = "none";
            document.getElementById('menu_sair_1b').style.display = "block";
            active_menu_id = "menu_sair_1b";
        }
        clear_previous(1);
        flag_saida = true;
        document.getElementById("disable_sidebar").style.display = "block";
    }
    if(new_id == "menu_sair_3"){
        saiu=true;
        count=0;
        reiniciar();
        var image = document.getElementById('barra_alcool');
        image.src = "icons/barra0.png";
        bebado=0;
        document.getElementById("cancel_taxi").style.display = "none";
        document.getElementById("timer_taxi").style.display = "none";
    }
    if(new_id =="menu_sair_2" && total_mais_tarde==0){
        menuChange("menu_sair_3");
    }
    if(new_id == "inicial" && flag_saida == true){
        menuChange('menu_sair_1');
    }
    if(new_id == "menu_taxi1" && chamou_taxi==true && flag_taxi== false){
        menuChange("menu_taxi1b");
        previous_menu_ids.pop();
    }
}


function setFlagTaxi(){
    if(flag_taxi==false){
        flag_taxi=true; /*esta a carregar no botao chamar_taxi_sim*/
    }
    else{
        flag_taxi=false;
    }
}
function setFlagCancTaxi(){
    flag_canc_taxi = true;
}
/* Funcao para mudar para menu anterior */
function menuChangePrevious() {
    if (previous_menu_ids.length != 0) {
        var previous_id = previous_menu_ids.pop();
        if(active_menu_id == "menu_sair_1" || active_menu_id == "menu_sair_1b"){
            flag_saida = false;
            document.getElementById("disable_sidebar").style.display = "none";
            if(chamou_taxi == true){
                document.getElementById("cancel_taxi").style.bottom = "15px";
                document.getElementById("timer_taxi").style.bottom = "25px";                
            }
        }
        document.getElementById(active_menu_id).style.display = "none";
        document.getElementById(previous_id).style.display = "block";
        active_menu_id = previous_id;
        if(previous_id == "menu_sair_1" || previous_id == "menu_sair_1b"){
            if(chamou_taxi == true){
                document.getElementById("cancel_taxi").style.bottom = "150px";
                document.getElementById("timer_taxi").style.bottom = "160px";
            }            
        }
    }
}

/* Funcao para mudar o tab do menu de bebidas/comidas/musicas */
function tabChange(new_tab_id, tab, other_tab){
    if(new_tab_id != active_tab_id){
        document.getElementById(new_tab_id).style.display = "inline-block";
        document.getElementById(active_tab_id).style.display = "none";
        tabChangeColor(new_tab_id, tab, other_tab);
        active_tab_id = new_tab_id;
    }
}

function tabChangeColor(new_tab_id, tab, other_tab){
    if(active_menu_id == "menu_bebidas"){
        document.getElementById(tab).style.backgroundColor = "rgba(36,139,136 ,1)";
        document.getElementById(other_tab).style.backgroundColor = "rgba(36,139,136 ,0.5)";
    }
    if(active_menu_id == "menu_comida"){
        document.getElementById(tab).style.backgroundColor = "rgba(173,0,76 ,1)";
        document.getElementById(other_tab).style.backgroundColor = "rgba(173,0,76 ,0.5)";
    }
    if(active_menu_id == "menu_top_musica"){
        if(tab == "top_bar"){
            document.getElementById(tab).style.backgroundColor = "rgba(202, 47, 24, 1)";
            document.getElementById("top_semanal").style.backgroundColor = "rgba(202, 47, 24, 0.5)";        
            document.getElementById("top_mundial").style.backgroundColor = "rgba(202, 47, 24, 0.5)";        
        }
        if(tab == "top_semanal"){
            document.getElementById(tab).style.backgroundColor = "rgba(202, 47, 24, 1)";
            document.getElementById("top_bar").style.backgroundColor = "rgba(202, 47, 24, 0.5)";        
            document.getElementById("top_mundial").style.backgroundColor = "rgba(202, 47, 24, 0.5)";        
        }
        if(tab == "top_mundial"){
            document.getElementById(tab).style.backgroundColor = "rgba(202, 47, 24, 1)";
            document.getElementById("top_semanal").style.backgroundColor = "rgba(202, 47, 24, 0.5)";        
            document.getElementById("top_bar").style.backgroundColor = "rgba(202, 47, 24, 0.5)";        
        }        
    }
}



/* Funcao para ativar as tabelas de bebidas/comidas */
function ativarTablesorter(){
    /* Activar o tablesorter das bebidas */
    $(".lista_bebidas").tablesorter({
        widgets: ["zebra","filter"],
        widgetOptions : {
            filter_external: ".searchbebida",
            filter_reset: "#reset_bebida",
            filter_columnFilters: false
        },
        sortList: [[2,1]] // ordem inicial - mais popular
    });
    /* Activar o tablesorter das comidas */
    $(".lista_comidas").tablesorter({
        widgets: ["zebra","filter"],
        widgetOptions : {
            filter_external: ".searchcomida",
            filter_reset: "#reset_comida",
            filter_columnFilters: false
        },
        sortList: [[2,1]] // ordem inicial - mais popular 
    });    
}
/* Funcao para ativar o movimento dos mapas */
function ativarKineticMapas(){
    $("#mapa_comboio_grande").kinetic();
    $("#mapa_autocarro_grande").kinetic();
    $("#mapa_metro_grande").kinetic();
}

/**
 * Funções para o menu de Bebidas
 *  editar, alterar total, adicionar, remover e apagar tudo
 * 
 */
 function editarBebida(bebida){
    var nome_bebida = bebida.data.nome;
    document.getElementById("personalizar_bebida_p").innerHTML = "Personalizar " + nome_bebida;
    personalizar_bebida_atual = bebida.data.id;
    popupPersonalizarBebida("abrir");
}

function pagar(caso){
    if (caso == "descricao"){
        document.getElementById("descricao_sidebar").style.display = "block";
        document.getElementById("disable_buttons_pagar").style.display = "block";
     }
    if(caso=="abrir"){
        if(total_mais_tarde!=0){
            document.getElementById("descricao_sidebar").style.display = "none";
            document.getElementById('disable_buttons_pagar').style.display = "block";
            document.getElementById('pagar_sidebar').style.display = "block";
        }
    }
    if(caso=="sim"){
        document.getElementById('ecra_pagar').style.display = "block";
        document.getElementById('pagar_sidebar').style.display = "none";
    }
    if(caso=="nao"){
        document.getElementById('pagar_sidebar').style.display = "none";
        document.getElementById('disable_buttons_pagar').style.display = "none";
        document.getElementById('ecra_pagar').style.display = "none";
    }
    if(caso=="pagou"){
        document.getElementById('ok_pagar_tudo').style.display = "block";
        document.getElementById('ecra_pagar').style.display = "none";
    }
    if(caso=="ok"){
        total = "Total: " + 0 + " €";
        total_mais_tarde = 0;
        document.getElementById("pagar_total").style.color = "rgba(255,255,255,0.3)";
        document.getElementById("pagar_total").style.border = "4px solid rgba(255,255,255,0.3)";
        document.getElementById("pagar_total").style.backgroundColor = "transparent";
        document.getElementById("a_pagar").innerHTML = total;
        document.getElementById('disable_buttons_pagar').style.display = "none";
        document.getElementById('ok_pagar_tudo').style.display = "none";
        clearPedidosSidebar();
    }
}

function clearPedidosSidebar(){
    document.getElementById("pedidos_sidebar").innerHTML = "";
}
function popupPersonalizarBebida(caso){
    if(caso == "abrir"){
        getBebidaPersonalizada(personalizar_bebida_atual);
        document.getElementById("disable_buttons_customBebida").style.display = "block";
        document.getElementById("personalizar_bebida").style.display = "block";
    }
    if(caso == "cancelar"){
        personalizar_bebida_atual = "";
        document.getElementById("disable_buttons_customBebida").style.display = "none";
        document.getElementById("personalizar_bebida").style.display = "none";
    }
    if(caso == "confirmar"){
        guardarBebidaPersonalizada(personalizar_bebida_atual);
        personalizar_bebida_atual = "";
        document.getElementById("disable_buttons_customBebida").style.display = "none";
        document.getElementById("personalizar_bebida").style.display = "none";        
    }
}
function getBebidaPersonalizada(bebida_id){
    var index_b = findIndexBebida(bebida_id);
    var bebida = bebidas_personalizadas[index_b];
    $('#personalizar_bebida input[type=checkbox]').each(function () {
        var tipo = this.value;
        if(bebida[tipo] == "Sim"){
            this.checked = true;
        }
        else{
            this.checked = false;
        }
    });   
}
function guardarBebidaPersonalizada(bebida_id){
    var index_b = findIndexBebida(bebida_id);
    var bebida = bebidas_personalizadas[index_b];
    $('#personalizar_bebida input[type=checkbox]').each(function () {
        var tipo = this.value;
        if(this.checked){
            bebida[tipo] = "Sim";
        }
        else{
            bebida[tipo] = "Não";
        }
    });
}

/* Funcao para atualizar o total a pagar no menu de bebidas */
function atualizarTotalBebidas(bebida){
    var total = parseInt(document.getElementById("total_bebidas").innerHTML);
    total = total + mapa_bebidas[bebida];
    document.getElementById("total_bebidas").innerHTML = total;
    if(total!=0){
        document.getElementById("concluir_pedido_id").style.backgroundColor = "rgba(0,128,0,0.7)";
        document.getElementById("cancelar_pedido_id").style.backgroundColor = "rgba(258,0,0,0.8)";
    }
}

function reduzirTotalBebidas(bebida){
    var total = parseInt(document.getElementById("total_bebidas").innerHTML);
    total = total - mapa_bebidas[bebida];
    document.getElementById("total_bebidas").innerHTML = total;    
    if (total==0){
        document.getElementById("concluir_pedido_id").style.backgroundColor = "rgba(128,128,128,0.3)";
        document.getElementById("cancelar_pedido_id").style.backgroundColor = "rgba(128,128,128,0.3)";
    }
}

function adicionarBebida(bebida){
    pedidos_bebida_existe = true;
    var bebida_existe = false;
    $("#pedidos_bebidas tr").each(function(){
        var td_bebida = $('td:nth-child(3)', $(this)).text();
        if(td_bebida == bebida){
            var td_quantidade = $('td:nth-child(2)',$(this)).text();
            bebida_existe = true;
            $('td:nth-child(2)',$(this)).html(parseInt(td_quantidade)+1);
            var bebida_index = findIndexBebida($(this).attr("id"));
            var nova_quant = bebidas_personalizadas[bebida_index].quant + 1;
            atualizarBebidaPersonalizada($(this).attr("id"),"quant",nova_quant);
            /* Atualizar total a pagar */
            atualizarTotalBebidas(bebida);
        }
    });
    if(bebida_existe  == false){
        var bebida_id = "bebida" + n_bebida;
        var editar_id = "editar_" + bebida_id;
        var adicionar_id = "adicionar_" + bebida_id;
        var remover_id = "remover_" + bebida_id;
        var remover_tudo_id = "remover_tudo_" + bebida_id;
        $("#pedidos_bebidas tbody").append(  
            "<tr id = '" + bebida_id + "' >"+
            "<td><img src='icons/no.png' style='margin-right:0px' id='" + remover_tudo_id +"'></td>"+
            "<td>" + 1 + "</td>"+
            "<td>" + bebida + "</td>"+
            "<td><img src='icons/plus-icon.png' id='" + adicionar_id +"'></td>"+
            "<td><img src='icons/minus-icon.png' id='" + remover_id +"'></td>"+
            "<td><img src='icons/gear.png' id='" + editar_id +"'></td>"+
            "</tr>"
            );
        var bind_editar_id = "#" + editar_id;
        var bind_adicionar_id = "#" + adicionar_id;
        var bind_remover_id = "#" + remover_id;
        var bind_remover_tudo_id = "#" + remover_tudo_id;
        $(bind_editar_id).bind("click", {id: bebida_id, nome: bebida}, editarBebida);
        $(bind_adicionar_id).bind("click", {id: bebida_id, nome: bebida}, incrementarBebida);
        $(bind_remover_tudo_id).bind("click", {id: bebida_id}, removerBebidaToda);
        $(bind_remover_id).bind("click", {id: bebida_id}, removerBebida);
        
        /* Atualizar total a pagar */
        atualizarTotalBebidas(bebida);
        n_bebida++;
        var preco = mapa_bebidas[bebida];
        var bebida_objeto = {
            id : bebida_id,
            nome : bebida,
            preco: preco,
            quant: 1,
            gelo : "Não",
            sal : "Não",
            limao : "Não",
            palhinha : "Não",
            tipo: "nao-alcoolica"
        };
        if(active_tab_id == "tabela_alcoolicas"){
            bebida_objeto["tipo"] = "alcoolica";
        }
        bebidas_personalizadas.push(bebida_objeto);
    } 
}
function findIndexBebida(bebida_id){
    return bebidas_personalizadas.findIndex(function(obj){
        return obj.id == bebida_id;
    });
}
function incrementarBebida(bebida){
    var nome_bebida = bebida.data.nome;
    adicionarBebida(nome_bebida);
}

function removerBebida(bebida){
    var tr_bebida_id = document.getElementById(bebida.data.id);
    /* Decrementar a quantidade */
    var quantidade = $('td:nth-child(2)',$(tr_bebida_id)).text();
    $('td:nth-child(2)',$(tr_bebida_id)).html(parseInt(quantidade)-1);
    var bebida_nome = $('td:nth-child(3)',$(tr_bebida_id)).text();
    reduzirTotalBebidas(bebida_nome);
    var bebida_index = findIndexBebida(bebida.data.id);
    var nova_quant = bebidas_personalizadas[bebida_index].quant - 1;
    atualizarBebidaPersonalizada(bebida.data.id,"quant",nova_quant);
    if(quantidade == 1){
        removerBebidaPersonalizada(bebida.data.id);
        tr_bebida_id.remove();
    }
}
function removerBebidaToda(bebida){
    var tr_bebida_id = document.getElementById(bebida.data.id);
    var quantidade = $('td:nth-child(2)',$(tr_bebida_id)).text();
    for(var i=quantidade; i>0; i--){
        removerBebida(bebida);
    }
}

function clearPedidosBebida(){
    $("#pedidos_bebidas tbody tr").each(function(){
        $(this).remove();
    });    
    document.getElementById("total_bebidas").innerHTML = 0;
    bebidas_personalizadas = [];
    pedidos_bebida_existe = false;
}

function cancelarPedidoBebidas(){
    clearPedidosBebida();
    document.getElementById("concluir_pedido_id").style.backgroundColor = "rgba(128,128,128,0.3)";
    document.getElementById("cancelar_pedido_id").style.backgroundColor = "rgba(128,128,128,0.3)";
}

function checkPedidosBebida(){
    if($("#pedidos_bebidas tbody tr").length == 0){
        pedidos_bebida_existe = false;
    }
    else{
        pedidos_bebida_existe = true;
    }
}

function popupProgressoBebidas(caso){
    if(caso == "abrir"){
        // abrir popup
        document.getElementById("disable_buttons_progressoBebida").style.display = "block";
        document.getElementById("progresso_bebida").style.display = "block";
    }
    if(caso == "sim"){
        document.getElementById("disable_buttons_progressoBebida").style.display = "none";
        document.getElementById("progresso_bebida").style.display = "none";
        document.getElementById("concluir_pedido_id").style.backgroundColor = "rgba(128,128,128,0.5)";
        clearPedidosBebida();
        perder_progresso_bebida = true;
        menuChange(perder_progresso_nextmenu_id);
        perder_progresso_nextmenu_id = "";
        // fechar popup
    }
    if(caso == "nao"){
        document.getElementById("disable_buttons_progressoBebida").style.display = "none";
        document.getElementById("progresso_bebida").style.display = "none";
        perder_progresso_bebida = false;
        perder_progresso_nextmenu_id = "";        
        // fechar popup
    }    
}

function removerBebidaPersonalizada(bebida_id){
    var novo_array = bebidas_personalizadas.filter(function(obj){
        return obj.id != bebida_id;
    });
    bebidas_personalizadas = novo_array;
}
function atualizarBebidaPersonalizada(bebida_id,tipo,novo_valor){
    var bebida_index = findIndexBebida(bebida_id);
    bebidas_personalizadas[bebida_index][tipo] = novo_valor;
}

function printDescricaoBebidas(){
    for (var j = 0; j < bebidas_personalizadas.length; j++){
        var bebida = bebidas_personalizadas[j];
        var quantidade = bebida.quant;
        var nome = bebida.nome;
        var produto = quantidade + " " + nome;
        var preco_total = quantidade * bebida.preco;
        for (var i = 0; i < 70; i++) {
            produto += ".";
        }

        /* alinhar precos */
        var preco_len = preco_total.toString().length;
        if(preco_len == 1){
            produto = "<span style='display:inline-block;white-space:nowrap;overflow:hidden;width:335px'>" +
                    produto + "</span>";
        }
        if(preco_len == 2){
            produto = "<span style='display:inline-block;white-space:nowrap;overflow:hidden;width:325px'>" +
                    produto + "</span>";
        }
        if(preco_len == 3){
            produto = "<span style='display:inline-block;white-space:nowrap;overflow:hidden;width:315px'>" +
                    produto + "</span>";
        }
        preco_total += "€";
        preco_total = "<span style='display:inline-block;vertical-align:top'>" + preco_total + "</span>";
        var extras = "Com  ";
        for (var property in bebida) {
            if (bebida.hasOwnProperty(property)) {
                if(bebida[property] == "Sim"){
                    extras += property + ", ";
                }
            }
        }
        extras = extras.slice(0,-2);
        if(extras == "Com"){ extras = ""};
        var extras_span = "<span style='display:block;margin-left:50px;font-size:16px'>" + extras + "</span>";
        
        $("#pedido_descricao").append(  
            "<p>" + produto + preco_total + extras_span + "</p>"   
            );
    }
}

function printDescricaoComidas(){
    for (var j = 0; j < comidas_personalizadas.length; j++){
        var comida = comidas_personalizadas[j];
        var quantidade = comida.quant;
        var nome = comida.nome;
        var produto = quantidade + " " + nome;
        var preco_total = quantidade * comida.preco;
        for (var i = 0; i < 70; i++) {
            produto += ".";
        }

        /* alinhar precos */
        var preco_len = preco_total.toString().length;
        if(preco_len == 1){
            produto = "<span style='display:inline-block;white-space:nowrap;overflow:hidden;width:335px'>" +
                    produto + "</span>";
        }
        if(preco_len == 2){
            produto = "<span style='display:inline-block;white-space:nowrap;overflow:hidden;width:325px'>" +
                    produto + "</span>";
        }
        if(preco_len == 3){
            produto = "<span style='display:inline-block;white-space:nowrap;overflow:hidden;width:315px'>" +
                    produto + "</span>";
        }
        preco_total += "€";
        preco_total = "<span style='display:inline-block;vertical-align:top'>" + preco_total + "</span>";
        var extras = "Com  ";
        for (var property in comida) {
            if (comida.hasOwnProperty(property)) {
                if(comida[property] == "Sim"){
                    extras += property + ", ";
                }
            }
        }
        extras = extras.slice(0,-2);
        if(extras == "Com"){ extras = ""};
        var extras_span = "<span style='display:block;margin-left:50px;font-size:16px'>" + extras + "</span>";
        
        $("#pedido_descricao").append(  
            "<p>" + produto + preco_total + extras_span + "</p>"   
            );
    }
}

function clearDescricaoPedido(){
    descricao_backup = document.getElementById("pedido_descricao").innerHTML;
    document.getElementById("pedido_descricao").innerHTML = "";
}

function addDescricaoSidebar(){
    $("#pedidos_sidebar").append(descricao_backup);
    descricao_backup = "";

}

function checkHasBebidaAlcool(){
    for (var j = 0; j < bebidas_personalizadas.length; j++){
        var bebida = bebidas_personalizadas[j];
        if(bebida.tipo == "alcoolica"){
            return true;
        }
    }
    return false;
}
/*TAMBEM PARA COMIDA*/
function concluirPedidoBebidas(caso){
    if(caso == "abrir"){
        if(active_menu_id=="menu_bebidas"){
            var total = document.getElementById("total_bebidas").innerHTML;
        }
        else{
            var total = document.getElementById("total_comidas").innerHTML;
        }
        if(total!=0){
            document.getElementById("disable_buttons_concluirPedidoB").style.display = "block";
            document.getElementById("concluir_pedido_descricao").style.display = "block";
            if(active_menu_id == "menu_bebidas"){ printDescricaoBebidas();}
            if(active_menu_id == "menu_comida") { printDescricaoComidas();}
        }
    }
    if(caso == "p_mais_tarde"){
        // alterar total a pagar na sidebar e ir para o menu inicial
        if(active_menu_id=="menu_bebidas"){
            if(checkHasBebidaAlcool()){
                bebado +=1;               
            }
            var image = document.getElementById('barra_alcool');
            if (bebado==1){ image.src = "icons/barra1.png"; }
            if (bebado==2) { image.src = "icons/barra2.png"; } 
            if (bebado==3) { image.src = "icons/barra3.png"; } 
            if (bebado==4) {  image.src = "icons/barra4.png"; }
            if (bebado>=5) { image.src = "icons/barra5.png"; } 
            document.getElementById("concluir_pedido_bb").style.display = "none";
            document.getElementById("disable_buttons_concluirPedidoB").style.display = "none";
            var total = document.getElementById("total_bebidas").innerHTML;
            total_mais_tarde += parseInt(total);
            total = "Total: " + total_mais_tarde + " €";
            document.getElementById("a_pagar").innerHTML = total;
            clearPedidosBebida();
            document.getElementById("concluir_pedido_id").style.backgroundColor = "rgba(128,128,128,0.3)";

        }
        else{
            var total = document.getElementById("total_comidas").innerHTML;
            total_mais_tarde += parseInt(total);
            total = "Total: " + total_mais_tarde + " €";
            document.getElementById("a_pagar").innerHTML = total;
            clearPedidosComida();
            document.getElementById("concluir_pedido_bb").style.display = "none";
            document.getElementById("disable_buttons_concluirPedidoB").style.display = "none";
            document.getElementById("concluir_pedido_id_c").style.backgroundColor = "rgba(128,128,128,0.3)";
        }
        document.getElementById("pagar_total").style.color = "black";
        document.getElementById("pagar_total").style.border = "4px solid black";
        document.getElementById("pagar_total").style.backgroundColor = "rgba(255,255,255,0.7)";
        menuChange("inicial");
        addDescricaoSidebar();
    }
    if(caso == "p_imediato"){
        document.getElementById("pagamento_imediato_b").style.display = "block";
        document.getElementById("concluir_pedido_bb").style.display = "none";
    }
    if(caso == "p_imediato_conf"){
        // abrir popup pagamento concluido
	if(active_menu_id=="menu_bebidas"){
            if(checkHasBebidaAlcool()){
                bebado +=1;               
            }
            var image = document.getElementById('barra_alcool');
            if (bebado==1){ image.src = "icons/barra1.png"; }
            if (bebado==2) { image.src = "icons/barra2.png"; } 
            if (bebado==3) { image.src = "icons/barra3.png"; } 
            if (bebado==4) {  image.src = "icons/barra4.png"; }
            if (bebado>=5) { image.src = "icons/barra5.png"; }     
        }
        clearPedidosBebida();
        clearPedidosComida();
        document.getElementById("concluir_pedido_id").style.backgroundColor = "rgba(128,128,128,0.3)";
        document.getElementById("concluir_pedido_id_c").style.backgroundColor = "rgba(128,128,128,0.3)";
        document.getElementById("ok_pagar_bebidas").style.display = "block";
        document.getElementById("pagamento_imediato_b").style.display = "none";
    }

    if(caso == "fechar_conf"){
        // fechar pagamento concluido e ir para o menu inicial
        descricao_backup = "";
        menuChange("inicial");
        document.getElementById("disable_buttons_concluirPedidoB").style.display = "none";
        document.getElementById("ok_pagar_bebidas").style.display = "none";
    }
    if(caso == "continuar"){
        clearDescricaoPedido();
        document.getElementById("concluir_pedido_descricao").style.display = "none";
        document.getElementById("concluir_pedido_bb").style.display = "block";
    }
    if(caso == "cancelar"){
        clearDescricaoPedido();
        document.getElementById("disable_buttons_concluirPedidoB").style.display = "none";
        document.getElementById("concluir_pedido_descricao").style.display = "none";
        document.getElementById("concluir_pedido_bb").style.display = "none";
        document.getElementById("pagamento_imediato_b").style.display = "none";

    }
}

/**
 * Funções para o menu de Comida
 *
 */
 function findIndexComida(comida_id){
    return comidas_personalizadas.findIndex(function(obj){
        return obj.id == comida_id;
    });
}
function removerComidaPersonalizada(comida_id){
    var novo_array = comidas_personalizadas.filter(function(obj){
        return obj.id != comida_id;
    });
    comidas_personalizadas = novo_array;
}
function atualizarComidaPersonalizada(comida_id,tipo,novo_valor){
    var comida_index = findIndexComida(comida_id);
    comidas_personalizadas[comida_index][tipo] = novo_valor;
}

/* Funcao para atualizar o total a pagar no menu de comidas */
function atualizarTotalComidas(comida){
    var total = parseInt(document.getElementById("total_comidas").innerHTML);
    total = total + mapa_comidas[comida];
    document.getElementById("total_comidas").innerHTML = total;
    if(total!=0){
        document.getElementById("concluir_pedido_id_c").style.backgroundColor = "rgba(0,128,0,0.7)";
        document.getElementById("cancelar_pedido_id_c").style.backgroundColor = "rgba(258,0,0,0.8)";
    }

}
/* Funcao para reduzir o total a pagar no menu de comidas */
function reduzirTotalComidas(comida){
    var total = parseInt(document.getElementById("total_comidas").innerHTML);
    total = total - mapa_comidas[comida];
    document.getElementById("total_comidas").innerHTML = total;    
    if (total==0){
        document.getElementById("concluir_pedido_id_c").style.backgroundColor = "rgba(128,128,128,0.3)";
        document.getElementById("cancelar_pedido_id_c").style.backgroundColor = "rgba(128,128,128,0.3)";
    }
}
/* Funcao para adicionar uma comida a lista de pedidos*/
function adicionarComida(comida){
    pedidos_comida_existe = true;
    var comida_existe = false;
    $("#pedidos_comidas tr").each(function(){
        var td_bebida = $('td:nth-child(3)', $(this)).text();
        if(td_bebida == comida){
            var td_quantidade = $('td:nth-child(2)',$(this)).text();
            comida_existe = true;
            $('td:nth-child(2)',$(this)).html(parseInt(td_quantidade)+1);
            var comida_index = findIndexComida($(this).attr("id"));
            var nova_quant = comidas_personalizadas[comida_index].quant + 1;
            atualizarComidaPersonalizada($(this).attr("id"),"quant",nova_quant);
            /* Atualizar total a pagar */
            atualizarTotalComidas(comida);
        }
    });
    if(comida_existe  == false){
        var comida_id = "comida" + n_comida;
        var editar_id = "editar_" + comida_id;
        var adicionar_id = "adicionar_" + comida_id;
        var remover_id = "remover_" + comida_id;
        var remover_tudo_id = "remover_tudo_" + comida_id;
        $("#pedidos_comidas tbody").append(  
            "<tr id = '" + comida_id + "' >"+
            "<td><img src='icons/no.png' style='margin-right:0px' id='" + remover_tudo_id +"'></td>"+
            "<td>" + 1 + "</td>"+
            "<td>" + comida + "</td>"+
            "<td><img src='icons/plus-icon.png' id='" + adicionar_id +"'></td>"+
            "<td><img src='icons/minus-icon.png' id='" + remover_id +"'></td>"+
            "<td><img src='icons/gear.png' id='" + editar_id +"'></td>"+
            "</tr>"
            );
        var bind_editar_id = "#" + editar_id;
        var bind_adicionar_id = "#" + adicionar_id;
        var bind_remover_id = "#" + remover_id;
        var bind_remover_tudo_id = "#" + remover_tudo_id;
        $(bind_editar_id).bind("click", {id: comida_id, nome: comida}, editarComida);
        $(bind_adicionar_id).bind("click", {id: comida_id, nome: comida},incrementarComida);
        $(bind_remover_tudo_id).bind("click", {id: comida_id}, removerComidaToda);
        $(bind_remover_id).bind("click", {id: comida_id}, removerComida);
        /* Atualizar total a pagar */
        atualizarTotalComidas(comida);
        n_comida++;
        var comida_objeto = {
            id : comida_id,
            nome : comida,
            quant: 1,
            preco: mapa_comidas[comida],
            sal : "Não",
            ketchup : "Não",
            maionese : "Não",
            mostarda : "Não"
        }
        comidas_personalizadas.push(comida_objeto);
    } 
};
/* Funcao para incrementar a quantidade de uma comida da lista de pedidos */
function incrementarComida(comida){
    var nome_comida = comida.data.nome;
    adicionarComida(nome_comida);
}
/* Funcao para decrementar ou remover uma comida da lista de pedidos */
function removerComida(comida){
    var tr_comida_id = document.getElementById(comida.data.id);
    /* Decrementar a quantidade */
    var quantidade = $('td:nth-child(2)',$(tr_comida_id)).text();
    $('td:nth-child(2)',$(tr_comida_id)).html(parseInt(quantidade)-1);
    var comida_nome = $('td:nth-child(3)',$(tr_comida_id)).text();
    reduzirTotalComidas(comida_nome);
    var comida_index = findIndexComida(comida.data.id);
    var nova_quant = comidas_personalizadas[comida_index].quant - 1;
    atualizarComidaPersonalizada(comida.data.id,"quant",nova_quant);
    if(quantidade == 1){
        removerComidaPersonalizada(comida.data.id);
        tr_comida_id.remove();
    }
}
function removerComidaToda(comida){
    var tr_comida_id = document.getElementById(comida.data.id);
    var quantidade = $('td:nth-child(2)',$(tr_comida_id)).text();  
    for (var i=quantidade; i>0; i--){
        removerComida(comida);
    }
}
/* Funcao para limpar a lista de pedidos de comida e o total a pagar */
function clearPedidosComida(){
    $("#pedidos_comidas tbody tr").each(function() {
        $(this).remove(); 
    });
    document.getElementById("total_comidas").innerHTML = 0;
    comidas_personalizadas = [];
    pedidos_comida_existe = false;
}

function cancelarPedidoComidas(){
    clearPedidosComida();
    document.getElementById("concluir_pedido_id_c").style.backgroundColor = "rgba(128,128,128,0.3)";
    document.getElementById("cancelar_pedido_id_c").style.backgroundColor = "rgba(128,128,128,0.3)";
}
/* Funcao para verificar se existem produtos pendentes na lista de pedidos */
function checkPedidosComida(){
    if($("#pedidos_comidas tbody tr").length == 0){
        pedidos_comida_existe = false;
    }
    else{
        pedidos_comida_existe = true;
    }
}

function popupProgressoComidas(caso){
    if(caso == "abrir"){
        document.getElementById("disable_buttons_progressoComida").style.display = "block";
        document.getElementById("progresso_comida").style.display = "block";
        // abrir popup
    }
    if(caso == "sim"){
        document.getElementById("disable_buttons_progressoComida").style.display = "none";
        document.getElementById("progresso_comida").style.display = "none";
        clearPedidosComida();
        perder_progresso_comida = true;
        menuChange(perder_progresso_nextmenu_id);
        perder_progresso_nextmenu_id = "";
    }
    if(caso == "nao"){
        document.getElementById("disable_buttons_progressoComida").style.display = "none";
        document.getElementById("progresso_comida").style.display = "none";        perder_progresso_comida = false;
        perder_progresso_nextmenu_id = "";
      // fechar popup
  }      
}

function editarComida(comida){
    var nome_comida = comida.data.nome;
    personalizar_comida_atual = comida.data.id;
    popupPersonalizarComida("abrir");      
}
function popupPersonalizarComida(caso){
    if(caso == "abrir"){
        getComidaPersonalizada(personalizar_comida_atual);
        document.getElementById("disable_buttons_customComida").style.display = "block";
        document.getElementById("personalizar_comida").style.display = "block";
    }
    if(caso == "cancelar"){
        personalizar_comida_atual = "";
        document.getElementById("disable_buttons_customComida").style.display = "none";
        document.getElementById("personalizar_comida").style.display = "none";
    }
    if(caso == "confirmar"){
        guardarComidaPersonalizada(personalizar_comida_atual);
        personalizar_comida_atual = "";
        document.getElementById("disable_buttons_customComida").style.display = "none";
        document.getElementById("personalizar_comida").style.display = "none";        
    }
}

function getComidaPersonalizada(comida_id){
    var index_c = findIndexComida(comida_id);
    var comida = comidas_personalizadas[index_c];
    $('#personalizar_comida input[type=checkbox]').each(function () {
        var tipo = this.value;
        if(comida[tipo] == "Sim"){
            this.checked = true;
        }
        else{
            this.checked = false;
        }
    });   
}
function guardarComidaPersonalizada(comida_id){
    var index_c = findIndexComida(comida_id);
    var comida = comidas_personalizadas[index_c];
    $('#personalizar_comida input[type=checkbox]').each(function () {
        var tipo = this.value;
        if(this.checked){
            comida[tipo] = "Sim";
        }
        else{
            comida[tipo] = "Não";
        }
    });
}

function startTime(){
    var today = new Date();
    var horas = today.getHours();
    var minutos= today.getMinutes();
    if (minutos < 10) {minutos = "0" + minutos};
    document.getElementById('time_real').innerHTML = horas + ":" + minutos;
    var t = setTimeout(startTime, 1000);
}
$(document).ready(function(){
    /* Activar o tablesorter das bebidas e comidas */
    ativarTablesorter();
    ativarKineticMapas();
    startTime();
    convertToptoStars();
    /* Escolher uma bebida da lista e adicionar aos pedidos */
    $('.lista_bebidas tbody tr').click(function(){
        var produto = $('td:nth-child(1)', $(this)).text();
        adicionarBebida(produto);
    });
    
    /* Escolher uma comida da lista e adicionar aos pedidos */
    $('.lista_comidas tbody tr').click(function(){
        var produto = $('td:nth-child(1)', $(this)).text();
        adicionarComida(produto);
    });
    
});

/* Ordenar bebidas */
$("#order_bebidas").change(function() {
    var option = $('#order_bebidas option:selected').val();
    if (option == "barato") {
        // [1,0] -> coluna 1 (preço), ordem crescente 
        $(".lista_bebidas").trigger("sorton", [[[1, 0]]]);
    }
    if (option == "caro") {
        // [1,1] -> coluna 1 (preço), ordem decrescente 
        $(".lista_bebidas").trigger("sorton", [[[1, 1]]]);
    }
    if (option == "a-z") {
        // [0,0] -> coluna 1 (nome), ordem alfabetica 
        $(".lista_bebidas").trigger("sorton", [[[0, 0]]]);
    }
    if (option == "z-a") {
        // [0,1] -> coluna 1 (nome), ordem alfabetica inversa 
        $(".lista_bebidas").trigger("sorton", [[[0, 1]]]);
    }
    if (option == "+popular") {
        // [2,1] -> coluna 2 (popularidade), ordem decrescente 
        $(".lista_bebidas").trigger("sorton", [[[2, 1]]]);
    }
    if (option == "-popular") {
        // [2,0] -> coluna 2 (popularidade), ordem crescente 
        $(".lista_bebidas").trigger("sorton", [[[2, 0]]]);
    }
});

/* Ordenar comidas */
$("#order_comidas").change(function() {
    var option = $('#order_comidas option:selected').val();
    if (option == "barato") {
        // [1,0] -> coluna 1 (preço), ordem crescente 
        $(".lista_comidas").trigger("sorton", [[[1, 0]]]);
    }
    if (option == "caro") {
        // [1,1] -> coluna 1 (preço), ordem decrescente 
        $(".lista_comidas").trigger("sorton", [[[1, 1]]]);
    }
    if (option == "a-z") {
        // [0,0] -> coluna 1 (nome), ordem alfabetica 
        $(".lista_comidas").trigger("sorton", [[[0, 0]]]);
    }
    if (option == "z-a") {
        // [0,1] -> coluna 1 (nome), ordem alfabetica inversa 
        $(".lista_comidas").trigger("sorton", [[[0, 1]]]);
    }
    if (option == "+popular") {
        // [2,1] -> coluna 2 (popularidade), ordem decrescente 
        $(".lista_comidas").trigger("sorton", [[[2, 1]]]);
    }
    if (option == "-popular") {
        // [2,0] -> coluna 2 (popularidade), ordem crescente 
        $(".lista_comidas").trigger("sorton", [[[2, 0]]]);
    }
});



// Change the selector if needed
var $table = $('table.scroll'),
$bodyCells = $table.find('tbody tr:first').children(),
colWidth;

// Adjust the width of thead cells when window resizes
$(window).resize(function() {
    // Get the tbody columns width array
    colWidth = $bodyCells.map(function() {
        return $(this).width();
    }).get();
    
    // Set the width of thead columns
    $table.find('thead tr').children().each(function(i, v) {
        $(v).width(colWidth[i]);
    });    
}).resize(); // Trigger resize handler


/* Mensagem de confirmar taxi */
function popupTaxi(caso){
    if(caso == "abrir"){
        document.getElementById("chamar_taxi").style.display = "none";
        document.getElementById("confirm_taxi").style.display = "block";
    }
    if(caso == "chamar"){
        totalSeconds=1800;
        countTimer();
        chamou_taxi=true;
        chamou = true;
        flag_taxi=false;
        document.getElementById("confirm_taxi").style.display = "none";
        document.getElementById("ok_taxi").style.display = "block";
        document.getElementById("timer_taxi").style.display = "block";
        document.getElementById("cancel_taxi").style.display = "block";

    }
    if(caso == "nao_chamar"){
        document.getElementById("chamar_taxi").style.display = "block";
        document.getElementById("confirm_taxi").style.display = "none";
        document.getElementById("ok_taxi").style.display = "none";
        if(chamou_taxi == false){
            document.getElementById("timer_taxi").style.display = "none";
            document.getElementById("cancel_taxi").style.display = "none";  
        }
        clear_previous(2);
    }
    if(caso == "fechar"){
        document.getElementById("chamar_taxi").style.display = "block";
        document.getElementById("confirm_taxi").style.display = "none";
        document.getElementById("ok_taxi").style.display = "none";
    }
}
/* Mensagem de cancelar taxi */

function popupCancTaxi(caso){
    if(caso == "abrir"){
        document.getElementById("disable_buttons_cancTaxi").style.display = "block";
        document.getElementById("confirm_canc_taxi").style.display = "block";
    }
    if(caso == "cancelar"){
        flag_taxi = false;
        cancelar_taxi();
        document.getElementById("confirm_canc_taxi").style.display = "none";
        document.getElementById("ok_canc_taxi").style.display = "block";
    }
    if(caso == "fechar"){
        document.getElementById("confirm_canc_taxi").style.display = "none";
        document.getElementById("disable_buttons_cancTaxi").style.display = "none";
        document.getElementById("cancel_taxi").style.display = "block";
        document.getElementById("timer_taxi").style.display = "block";
    }
    
    if(caso == "ok"){
        document.getElementById("disable_buttons_cancTaxi").style.display = "none";
        document.getElementById("ok_canc_taxi").style.display = "none";
        if (flag_canc_taxi==true){
            menuChangePrevious();
            flag_taxi=true;
            flag_canc_taxi=false;
        }
    }
}
/*TAXI*/
var timerVar = setInterval(countTimer, 1000);
function countTimer() {
    --totalSeconds; 
    var hour = Math.floor(totalSeconds /3600);
    var minute = Math.floor((totalSeconds - hour*3600)/60);
    var seconds = totalSeconds - (hour*3600 + minute*60);
    document.getElementById("timer_taxi").innerHTML = "Taxi chega em: " + minute + "m" + seconds + "s";
    if (totalSeconds<=0 && chamou_taxi==true){
        document.getElementById("timer_taxi").innerHTML = "O teu taxi chegou!";
    }
}
var chamar_timer = setInterval(reiniciar, 1000);
function reiniciar(){
    ++count;
    if(count>=5 && saiu==true){
        flag_saida = false;             // flag de saida
        previous_menu_ids = [];         // menus anteriores
        n_bebida = 0;                   // numero de bebidas
        n_comida = 0;                   // numero de comidas
        pedidos_comida_existe = false;  // existe pedido bebida
        pedidos_bebida_existe = false;  // existe pedido comida
        perder_progresso_bebida = true;// progresso bebida
        perder_progresso_comida = true;// progresso comida
        total_mais_tarde = 0;           // total mais tarde
        document.getElementById("a_pagar").innerHTML = "Total: 0 €";
        perder_progresso_nextmenu_id = ""; // menu progresso
        count = 0;    
        saiu=false;   
        random = Math.floor((Math.random() * 10) + 1);
        cancelar_taxi();
        document.getElementById("ecra_inicial").style.display = "block";
        document.getElementById("sidebar").style.display = "none";
        document.getElementById("menu_sair_3").style.display = "none";
        document.getElementById(active_menu_id).style.display = "none";  //apagar o que aparecia quando se carregava num menu da sidebar
        document.getElementById("disable_sidebar").style.display = "none";
    }

}

function cancelar_taxi(){
   document.getElementById("timer_taxi").style.display = "none";
   document.getElementById("cancel_taxi").style.display = "none";
   totalSeconds = 1800; /*30min?*/

}

function popupEmpregado(caso){
    if (caso=="abrir") {
        document.getElementById("disable_buttons_empregado").style.display = "block";
        document.getElementById("janela_empregado").style.display = "block";
    }
    if (caso=="sim"){
        document.getElementById("empregado_ok").style.display = "block";
        document.getElementById("janela_empregado").style.display = "none";
    }
    if (caso=="nao"){
        document.getElementById("janela_empregado").style.display = "none";
        document.getElementById("disable_buttons_empregado").style.display = "none";

    }
    if (caso=="ok"){
        document.getElementById("empregado_ok").style.display = "none";
        document.getElementById("disable_buttons_empregado").style.display = "none";

    }
}

function popupPagar(caso){
    if (caso=="pago"){
        document.getElementById("pagamento_concluido").style.display = "block";
    }
    if(caso=="ok"){
        document.getElementById("pagamento_concluido").style.display = "none";
        total = "Total: " + 0 + " €";
        document.getElementById("a_pagar").innerHTML = total;
        menuChange('menu_sair_3');
    }
    document.getElementById("pagar_total").style.color = "rgba(255,255,255,0.3)";
    document.getElementById("pagar_total").style.border = "4px solid rgba(255,255,255,0.3)";
    document.getElementById("pagar_total").style.backgroundColor = "transparent";
}

function check_morada(){
    var morada = document.getElementById("input_morada").value;
    var botao_morada = document.getElementById("submit_morada");
    if (morada == "" ) {
        document.getElementById("submit_morada").disabled = true;
    }else {
        document.getElementById("submit_morada").disabled = false;
    }
}

function clear_morada(){
    document.getElementById("input_morada").value = "";
    document.getElementById("submit_morada").disabled = true;
}

function clear_previous(new_length){
    if (flag_saida == true) {
        previous_menu_ids = previous_menu_ids.slice(0,2);
    } 
    else {
        previous_menu_ids = previous_menu_ids.slice(0,new_length);
    }
}

function show_teclado(){
    $("#teclado").fadeIn("slow");
}

function hide_teclado(){
    $("#teclado").fadeOut("slow");
}

function mapaComboio(caso){
    if(caso=="cascais"){
        var image = document.getElementById('comboio_mapa');
        var image1 = document.getElementById('comboio_mapa1');
        image1.src = "icons/cascais.gif";
        image.src = "icons/cascais.gif";
        document.getElementById("comboio_proximo").innerHTML = "Próximo comboio em 15 minutos";
        document.getElementById('comboio_4').style.backgroundColor = "#555";
        document.getElementById('comboio_4').style.color = "white";
        document.getElementById('comboio_2').style.backgroundColor = "#f1f1f1";
        document.getElementById('comboio_2').style.color = "black";
        document.getElementById('comboio_3').style.backgroundColor = "#f1f1f1";
        document.getElementById('comboio_3').style.color = "black";
        document.getElementById('comboio_1').style.backgroundColor = "#f1f1f1";
        document.getElementById('comboio_1').style.color = "black";    
        document.getElementById('comboio_5').style.backgroundColor = "#f1f1f1";
        document.getElementById('comboio_5').style.color = "black";
    }
    if(caso=="sintra"){
        var image = document.getElementById('comboio_mapa');
        var image1 = document.getElementById('comboio_mapa1');
        image1.src = "icons/sintra.gif";
        image.src = "icons/sintra.gif";
        document.getElementById("comboio_proximo").innerHTML = "Próximo comboio em 25 minutos";
        document.getElementById('comboio_2').style.backgroundColor = "#555";
        document.getElementById('comboio_2').style.color = "white";
        document.getElementById('comboio_1').style.backgroundColor = "#f1f1f1";
        document.getElementById('comboio_1').style.color = "black";
        document.getElementById('comboio_3').style.backgroundColor = "#f1f1f1";
        document.getElementById('comboio_3').style.color = "black";
        document.getElementById('comboio_4').style.backgroundColor = "#f1f1f1";
        document.getElementById('comboio_4').style.color = "black";    
        document.getElementById('comboio_5').style.backgroundColor = "#f1f1f1";
        document.getElementById('comboio_5').style.color = "black"; 
    }
    if(caso=="lisboa"){
        var image = document.getElementById('comboio_mapa');
        var image1 = document.getElementById('comboio_mapa1');
        image1.src = "icons/comboio_lisboa.png";
        image.src = "icons/comboio_lisboa.png";
        document.getElementById("comboio_proximo").innerHTML = "Tempo maximo de espera: 30 minutos";
        document.getElementById('comboio_1').style.backgroundColor = "#555";
        document.getElementById('comboio_1').style.color = "white";
        document.getElementById('comboio_2').style.backgroundColor = "#f1f1f1";
        document.getElementById('comboio_2').style.color = "black";
        document.getElementById('comboio_3').style.backgroundColor = "#f1f1f1";
        document.getElementById('comboio_3').style.color = "black";
        document.getElementById('comboio_4').style.backgroundColor = "#f1f1f1";
        document.getElementById('comboio_4').style.color = "black";    
        document.getElementById('comboio_5').style.backgroundColor = "#f1f1f1";
        document.getElementById('comboio_5').style.color = "black";
    }
    if(caso=="azambuja"){
        var image = document.getElementById('comboio_mapa');
        var image1 = document.getElementById('comboio_mapa1');
        image1.src = "icons/azambuja.gif";
        image.src = "icons/azambuja.gif";
        document.getElementById("comboio_proximo").innerHTML = "Próximo comboio em 5 minutos";
        document.getElementById('comboio_3').style.backgroundColor = "#555";
        document.getElementById('comboio_3').style.color = "white";
        document.getElementById('comboio_2').style.backgroundColor = "#f1f1f1";
        document.getElementById('comboio_2').style.color = "black";
        document.getElementById('comboio_4').style.backgroundColor = "#f1f1f1";
        document.getElementById('comboio_4').style.color = "black";
        document.getElementById('comboio_1').style.backgroundColor = "#f1f1f1";
        document.getElementById('comboio_1').style.color = "black";    
        document.getElementById('comboio_5').style.backgroundColor = "#f1f1f1";
        document.getElementById('comboio_5').style.color = "black";
    }
    if(caso=="sado"){
        var image = document.getElementById('comboio_mapa');
        var image1 = document.getElementById('comboio_mapa1');
        image1.src = "icons/sado.gif";
        image.src = "icons/sado.gif";
        document.getElementById("comboio_proximo").innerHTML = "Próximo comboio em 10 minutos";
        document.getElementById('comboio_5').style.backgroundColor = "#555";
        document.getElementById('comboio_5').style.color = "white";
        document.getElementById('comboio_2').style.backgroundColor = "#f1f1f1";
        document.getElementById('comboio_2').style.color = "black";
        document.getElementById('comboio_3').style.backgroundColor = "#f1f1f1";
        document.getElementById('comboio_3').style.color = "black";
        document.getElementById('comboio_1').style.backgroundColor = "#f1f1f1";
        document.getElementById('comboio_1').style.color = "black";    
        document.getElementById('comboio_4').style.backgroundColor = "#f1f1f1";
        document.getElementById('comboio_4').style.color = "black";
    }
}

/*-------------------MUSICA-------------------------*/
function partilharMusica(caso,tipo){
    if (caso=="abrir"){
        document.getElementById("disable_buttons_partilhar_musica").style.display = "block";
        document.getElementById("partilhar").style.display = "block";
    }
    if (caso=="sair"){
        document.getElementById("disable_buttons_partilhar_musica").style.display = "none";
        document.getElementById("partilhar").style.display = "none";
        document.getElementById("facebook").style.display = "none";
        document.getElementById("twitter").style.display = "none";
        document.getElementById("google").style.display = "none";
        document.getElementById("tumblr").style.display = "none";
        document.getElementById("partilhado").style.display = "none";
	    document.getElementById("iniciar_sessao").style.display = "none";
    }
    if (caso=="facebook"){
        document.getElementById("facebook").style.display = "block";
        document.getElementById("partilhar").style.display = "none";
    }
    if (caso=="twitter"){
        document.getElementById("twitter").style.display = "block";
        document.getElementById("partilhar").style.display = "none";
    }
    if (caso=="google"){
        document.getElementById("google").style.display = "block";
        document.getElementById("partilhar").style.display = "none";
    }
    if (caso=="tumblr"){
        document.getElementById("tumblr").style.display = "block";
        document.getElementById("partilhar").style.display = "none";
    }
    if (caso=="partilhar"){
        document.getElementById("iniciar_sessao").style.display = "block";
        document.getElementById("facebook").style.display = "none";
        document.getElementById("twitter").style.display = "none";
        document.getElementById("google").style.display = "none";
        document.getElementById("tumblr").style.display = "none";
        if(tipo=="facebook"){
            document.getElementById("logo_login").src = "icons/facebook1.png";
        }
        if(tipo=="twitter"){
            document.getElementById("logo_login").src = "icons/twitter.png";
        }
        if(tipo=="google"){
            document.getElementById("logo_login").src = "icons/google+.png";
        }
        if(tipo=="tumblr"){
            document.getElementById("logo_login").src = "icons/tumblr.png";
        }

    }
      if (caso=="partilhou"){
        document.getElementById("partilhado").style.display = "block";
        document.getElementById("iniciar_sessao").style.display = "none";
    }
}
function check_login(){
    var user = document.getElementById("input_user").value;
    var pass = document.getElementById("input_pass").value;
    if (user == "" || pass=="" ) {
        document.getElementById("submit_login").disabled = true;
    }else {
        document.getElementById("submit_login").disabled = false;
    }
}
function clear_login(){
    document.getElementById("input_user").value = "";
    document.getElementById("input_pass").value = "";
    document.getElementById("submit_login").disabled = true;
}
function convertToptoStars(){
    $("#tabela_top_bar tbody tr td:nth-child(5)").each(function() {
        var musica = $(this).text();
        var classificacao = parseInt(musica);
        var rate = "";
        if (classificacao == 0){
            rate = "<img src='icons/star1.png' class='star_music'><img src='icons/star1.png' class='star_music'><img src='icons/star1.png' class='star_music'><img src='icons/star1.png' class='star_music'><img src='icons/star1.png' class='star_music'>";
        }
        if (classificacao  <= 20 && classificacao  > 0){
            rate = "<img src='icons/star.png' class='star_music'><img src='icons/star1.png' class='star_music'><img src='icons/star1.png' class='star_music'><img src='icons/star1.png' class='star_music'><img src='icons/star1.png' class='star_music'>"
        }
        if (classificacao  <= 40 && classificacao  >20){
            rate = "<img src='icons/star.png' class='star_music'><img src='icons/star.png' class='star_music'><img src='icons/star1.png' class='star_music'><img src='icons/star1.png' class='star_music'><img src='icons/star1.png' class='star_music'>"
        }
        if ( classificacao  <= 60 && classificacao  >40){
            rate = "<img src='icons/star.png' class='star_music'><img src='icons/star.png' class='star_music'><img src='icons/star.png' class='star_music'><img src='icons/star1.png' class='star_music'><img src='icons/star1.png' class='star_music'>"
        }
        if ( classificacao  <= 80 && classificacao  >60){
            rate = "<img src='icons/star.png' class='star_music'><img src='icons/star.png' class='star_music'><img src='icons/star.png' class='star_music'><img src='icons/star.png' class='star_music'><img src='icons/star1.png' class='star_music'>"
        } 
        if ( classificacao  <= 100 && classificacao  >80){
            rate = "<img src='icons/star.png' class='star_music'><img src='icons/star.png' class='star_music'><img src='icons/star.png' class='star_music'><img src='icons/star.png' class='star_music'><img src='icons/star.png' class='star_music'>"
        }
        this.innerHTML = rate;
    });
    $("#tabela_top_semanal tbody tr td:nth-child(5)").each(function() {
        var musica = $(this).text();
        var classificacao = parseInt(musica);
        var rate = "";
        if (classificacao == 0){
            rate = "<img src='icons/star1.png' class='star_music'><img src='icons/star1.png' class='star_music'><img src='icons/star1.png' class='star_music'><img src='icons/star1.png' class='star_music'><img src='icons/star1.png' class='star_music'>";
        }
        if (classificacao  <= 20 && classificacao  > 0){
            rate = "<img src='icons/star.png' class='star_music'><img src='icons/star1.png' class='star_music'><img src='icons/star1.png' class='star_music'><img src='icons/star1.png' class='star_music'><img src='icons/star1.png' class='star_music'>"
        }
        if (classificacao  <= 40 && classificacao  >20){
            rate = "<img src='icons/star.png' class='star_music'><img src='icons/star.png' class='star_music'><img src='icons/star1.png' class='star_music'><img src='icons/star1.png' class='star_music'><img src='icons/star1.png' class='star_music'>"
        }
        if ( classificacao  <= 60 && classificacao  >40){
            rate = "<img src='icons/star.png' class='star_music'><img src='icons/star.png' class='star_music'><img src='icons/star.png' class='star_music'><img src='icons/star1.png' class='star_music'><img src='icons/star1.png' class='star_music'>"
        }
        if ( classificacao  <= 80 && classificacao  >60){
            rate = "<img src='icons/star.png' class='star_music'><img src='icons/star.png' class='star_music'><img src='icons/star.png' class='star_music'><img src='icons/star.png' class='star_music'><img src='icons/star1.png' class='star_music'>"
        } 
        if ( classificacao  <= 100 && classificacao  >80){
            rate = "<img src='icons/star.png' class='star_music'><img src='icons/star.png' class='star_music'><img src='icons/star.png' class='star_music'><img src='icons/star.png' class='star_music'><img src='icons/star.png' class='star_music'>"
        }
        this.innerHTML = rate;
    });
    $("#tabela_top_mundial tbody tr td:nth-child(5)").each(function() {
        var musica = $(this).text();
        var classificacao = parseInt(musica);
        var rate = "";
        if (classificacao == 0){
            rate = "<img src='icons/star1.png' class='star_music'><img src='icons/star1.png' class='star_music'><img src='icons/star1.png' class='star_music'><img src='icons/star1.png' class='star_music'><img src='icons/star1.png' class='star_music'>";
        }
        if (classificacao  <= 20 && classificacao  > 0){
            rate = "<img src='icons/star.png' class='star_music'><img src='icons/star1.png' class='star_music'><img src='icons/star1.png' class='star_music'><img src='icons/star1.png' class='star_music'><img src='icons/star1.png' class='star_music'>"
        }
        if (classificacao  <= 40 && classificacao  >20){
            rate = "<img src='icons/star.png' class='star_music'><img src='icons/star.png' class='star_music'><img src='icons/star1.png' class='star_music'><img src='icons/star1.png' class='star_music'><img src='icons/star1.png' class='star_music'>"
        }
        if ( classificacao  <= 60 && classificacao  >40){
            rate = "<img src='icons/star.png' class='star_music'><img src='icons/star.png' class='star_music'><img src='icons/star.png' class='star_music'><img src='icons/star1.png' class='star_music'><img src='icons/star1.png' class='star_music'>"
        }
        if ( classificacao  <= 80 && classificacao  >60){
            rate = "<img src='icons/star.png' class='star_music'><img src='icons/star.png' class='star_music'><img src='icons/star.png' class='star_music'><img src='icons/star.png' class='star_music'><img src='icons/star1.png' class='star_music'>"
        } 
        if ( classificacao  <= 100 && classificacao  >80){
            rate = "<img src='icons/star.png' class='star_music'><img src='icons/star.png' class='star_music'><img src='icons/star.png' class='star_music'><img src='icons/star.png' class='star_music'><img src='icons/star.png' class='star_music'>"
        }
        this.innerHTML = rate;
    });
}
function adicionarMusica(caso){
    if(caso == "abrir"){
        menuChange("menu_top_musica");
    }
}

var buffer_musica_top = new Array();

function clickTopMusica(img, nome, artista, tempo, rating){
    if(checkTopMusica(nome)== false){
        img.src = "icons/yes1.png";
        var musica_objeto = {
            nome : nome,
            artista : artista,
            tempo : tempo,
            classificacao : rating,
        }
        buffer_musica_top.push(musica_objeto);
        adicionarPlaylist(musica_objeto);
    }
    else{
        var novo_array = buffer_musica_top.filter(function(obj){
            return obj.nome != nome;
        });
        buffer_musica_top = novo_array;
        img.src = "icons/plus_musica.png";
        removerPlaylist(nome);
    }

}
function adicionarPlaylist(musica){
    if ( musica.classificacao==0){
         var rate = "<img src='icons/star1.png' class='star_music'><img src='icons/star1.png' class='star_music'><img src='icons/star1.png' class='star_music'><img src='icons/star1.png' class='star_music'><img src='icons/star1.png' class='star_music'>"
    }
    if ( musica.classificacao <= 20 && musica.classificacao >0){
        var rate = "<img src='icons/star.png' class='star_music'><img src='icons/star1.png' class='star_music'><img src='icons/star1.png' class='star_music'><img src='icons/star1.png' class='star_music'><img src='icons/star1.png' class='star_music'>"
    }
    if ( musica.classificacao <= 40 && musica.classificacao >20){
        var rate = "<img src='icons/star.png' class='star_music'><img src='icons/star.png' class='star_music'><img src='icons/star1.png' class='star_music'><img src='icons/star1.png' class='star_music'><img src='icons/star1.png' class='star_music'>"
    }
    if ( musica.classificacao <= 60 && musica.classificacao >40){
       var rate = "<img src='icons/star.png' class='star_music'><img src='icons/star.png' class='star_music'><img src='icons/star.png' class='star_music'><img src='icons/star1.png' class='star_music'><img src='icons/star1.png' class='star_music'>"
    }
    if ( musica.classificacao <= 80 && musica.classificacao >60){
        var rate = "<img src='icons/star.png' class='star_music'><img src='icons/star.png' class='star_music'><img src='icons/star.png' class='star_music'><img src='icons/star.png' class='star_music'><img src='icons/star1.png' class='star_music'>"
    } 
    if ( musica.classificacao <= 100 && musica.classificacao >80){
        var rate = "<img src='icons/star.png' class='star_music'><img src='icons/star.png' class='star_music'><img src='icons/star.png' class='star_music'><img src='icons/star.png' class='star_music'><img src='icons/star.png' class='star_music'>"
    }
    id_like_dislike += 1;
    var id_like ="like" + id_like_dislike;
    var id_dislike = "dislike" + id_like_dislike;
    $("#playlist tbody").append(
        "<tr>" +
        "<td>" + "<img src='icons/no1.png' onclick='removerPlaylist2(this)' style='height: 30px; width: 30px'>"  + "</td>" +
        "<td>" + musica.nome + "</td>" +
        "<td>" + musica.artista + "</td>" +
        "<td>" + musica.tempo + "</td>" +
        "<td>" + rate + "</td>" +
        "<td>" + "<img id='like" + id_like_dislike + "' src='icons/like.png' onclick='likeMusic(this)'>" + "</td>" +
    	"<td>" + "<img id='dislike"+ id_like_dislike + "' src='icons/like1.png' onclick='dislikeMusic(this)'>" + "</td>" +
        "</tr>"
        );
}
function removerPlaylist(nome){
    $("#playlist tbody tr").each(function(){
        var musica_nome = $('td:nth-child(2)', $(this)).text();
        if(musica_nome == nome){
            $(this).remove();
        }
    }); 
}
function removerPlaylist2(param){
    var row = param.parentElement.parentElement;
    var nome = row.children[1].innerHTML;
    $("#playlist tbody tr").each(function(){
        var musica_nome = $('td:nth-child(2)', $(this)).text();
        if(musica_nome == nome){
            $(this).remove();
        }
    }); 
    $("#tabela_top_bar tbody tr").each(function(){
        var musica_nome = $('td:nth-child(2)', $(this)).text();
        if(musica_nome == nome){
            var img = $('td:nth-child(1) img', $(this));
            img.attr("src","icons/plus_musica.png");
        }
    });
    $("#tabela_top_semanal tbody tr").each(function(){
        var musica_nome = $('td:nth-child(2)', $(this)).text();
        if(musica_nome == nome){
            var img = $('td:nth-child(1) img', $(this));
            img.attr("src","icons/plus_musica.png");
        }
    });
    $("#tabela_top_mundial tbody tr").each(function(){
        var musica_nome = $('td:nth-child(2)', $(this)).text();
        if(musica_nome == nome){
            var img = $('td:nth-child(1) img', $(this));
            img.attr("src","icons/plus_musica.png");
        }
    });
    var novo_array = buffer_musica_top.filter(function(obj){
            return obj.nome != nome;
        });
    buffer_musica_top = novo_array;
}

function checkTopMusica(nome){
    var index =  buffer_musica_top.findIndex(function(obj){
        return obj.nome == nome;
    });
    if(index == -1){
        return false;
    }
    else
        return true;
}


function likeMusic(id){
    var image1 = id;
    // ir buscar o dislike desta musica
    var image2 = id.parentElement.parentElement.children[6].children[0];
    var like = image1.getAttribute("src");
    var dislike = image2.getAttribute("src");
    // neutro -> like
    if (like == "icons/like.png" && dislike == "icons/like1.png"){
        image1.src = "icons/like2.png";
    }
    // dislike -> neutro
    if(dislike == "icons/like3.png"){
        image2.src = "icons/like1.png";
    }
}

function dislikeMusic(id){
    var image1 = id;
    // ir buscar o like desta musica
    var image2 = id.parentElement.parentElement.children[5].children[0];
    var dislike = image1.getAttribute("src");
    var like = image2.getAttribute("src");
    // neutro -> dislike
    if(dislike == "icons/like1.png" && like == "icons/like.png"){
        image1.src = "icons/like3.png";
    }
    // like -> neutro
    if(like == "icons/like2.png"){
        image2.src = "icons/like.png";
    }
}

function ajuda(caso){
    if(caso == "abrir"){
        document.getElementById("disable_buttons_ajuda").style.display = "block";
        if(active_menu_id == "inicial"){
            document.getElementById("ajuda_inicial").style.display = "block";
        }
        if(active_menu_id == "menu_bebidas"){
            document.getElementById("ajuda_bebidas").style.display = "block";
        }
        if(active_menu_id == "menu_comida"){
            document.getElementById("ajuda_comida").style.display = "block";            
        }
        if(active_menu_id == "menu_top_musica" || active_menu_id == "menu_musica"){
            document.getElementById("ajuda_musica").style.display = "block";     
        }
        if(active_menu_id == "menu_transportes"){
            document.getElementById("ajuda_transportes").style.display = "block";
        }
        if(active_menu_id == "menu_metro" || active_menu_id == "menu_metro_grande"){
            document.getElementById("ajuda_metro").style.display = "block";
        }
        if(active_menu_id == "menu_autocarro" || active_menu_id == "menu_autocarro_grande"){
            document.getElementById("ajuda_autocarros").style.display = "block";
        }
        if(active_menu_id == "menu_comboio" || active_menu_id == "menu_comboio_grande"){
            document.getElementById("ajuda_comboios").style.display = "block";
        }
        if(active_menu_id == "menu_taxi1" || active_menu_id == "menu_taxi2"){
            document.getElementById("ajuda_taxi").style.display = "block";
        }
    }
    if(caso == "fechar"){
        document.getElementById("disable_buttons_ajuda").style.display = "none";
        if(active_menu_id == "inicial"){
            document.getElementById("ajuda_inicial").style.display = "none";
        }
        if(active_menu_id == "menu_bebidas"){
            document.getElementById("ajuda_bebidas").style.display = "none";
        }
        if(active_menu_id == "menu_comida"){
            document.getElementById("ajuda_comida").style.display = "none";            
        }
        if(active_menu_id == "menu_top_musica" || active_menu_id == "menu_musica"){
            document.getElementById("ajuda_musica").style.display = "none";     
        }
        if(active_menu_id == "menu_transportes"){
            document.getElementById("ajuda_transportes").style.display = "none";
        }
        if(active_menu_id == "menu_metro" || active_menu_id == "menu_metro_grande"){
            document.getElementById("ajuda_metro").style.display = "none";
        }
        if(active_menu_id == "menu_autocarro" || active_menu_id == "menu_autocarro_grande"){
            document.getElementById("ajuda_autocarros").style.display = "none";
        }
        if(active_menu_id == "menu_comboio" || active_menu_id == "menu_comboio_grande"){
            document.getElementById("ajuda_comboios").style.display = "none";
        }
        if(active_menu_id == "menu_taxi1" || active_menu_id == "menu_taxi2"){
            document.getElementById("ajuda_taxi").style.display = "none";
        }
    }
}
