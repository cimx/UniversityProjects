/*global THREE, requestAnimationFrame, console*/
var camera, scene, renderer, clock;

var can_shoot = true;
var current_camera = "0";
var current_material = "lambert";
var old_material = "lambert";

var gameElements = [];
var gameMaterials = new MaterialManager();

var gameState = "paused"; // active, paused, finished
var gameScore = 10;
var shipLives = 3;

var spotlight;

function resetGameVariables() {
    // clean scene
    for(let element of gameElements){
        scene.remove(element);
    }
    for(let shipLive of shipLivesObjects){
        scene.remove(shipLive);
    }
    gameElements = [];
    shipLivesObjects = [];

    // generate game elements
    generateShipWithSpotlight();
    generateAliens();

    // setup game variable: state, score, lives
    gameScore = 10;
    shipLives = 3;
    generateShipLives(2000);
    playGame();
}
function generateShipWithSpotlight(){
    spotlight = addSpotLight();
    generateShip(spotlight);
    setInitialSpotlightPositions(spotlight);
}
function init() {
    'use strict';
    clock = new THREE.Clock(false);
    renderer = new THREE.WebGLRenderer({
        antialias: true
    });
    renderer.setSize(window.innerWidth, window.innerHeight);
	renderer.autoClear = false;
    document.body.appendChild(renderer.domElement);

    createScene();
    generateShipWithSpotlight()

    createBackground('img/background.jpg');
    createGameStateMessage('img/paused.png', 0, 0, 800);
    createGameStateMessage('img/gameover.png', 0, 0, 1000);
    createGameStateMessage('img/win.png', 0, 0, 1200);
    generateShipLives(2000);

    createOrthographicCamera(0, 400, 0);
    camera_2 = createPerspectiveCamera(0, 270, 440);
    camera_3 = createPerspectiveCamera(ship.position.x, 270, 440);
    createGameStateCamera(0,150,800);
    createShipLivesCamera(0,150,2000);

    addDirectionalLight();
    addPointLight();
    camera = camera_1;
    //playGame();
    pauseGame();
    render();

    window.addEventListener("keydown", onKeyDown);
    window.addEventListener("keyup", onKeyUp);
    window.addEventListener("resize", onResize);
}

function playGame() {
    clock.start();
    gameState = "active";
    showPausedView = false;
}

function pauseGame() {
    clock.stop();
    gameState = "paused";
    updateGameStateCamera(0,150,800);
    showPausedView = true;
}

function gameOver(){
    pauseGame();
    updateGameStateCamera(0,150,1000);
    gameState = "gameover";
}

function finishGame(){
    pauseGame();
    updateGameStateCamera(0,150,1200);
    gameState = "finished";
}

function createScene() {
    'use strict';
    scene = new THREE.Scene();
    generateAliens();
}

function onResize() {
    'use strict';
    renderer.setSize(window.innerWidth, window.innerHeight);
    if (window.innerHeight > 0 && window.innerWidth > 0) {
        if (camera instanceof THREE.OrthographicCamera) {
            resizeOrthographicCamera();
        } else {
            camera.aspect = window.innerWidth / window.innerHeight;
        }
        camera.updateProjectionMatrix();
    }
}

function onKeyUp(e) {
    'use strict';
    switch (e.keyCode) {
        case 37: // left arrow key
            ship.startBraking();
            break;
        case 39: // right arrow key
            ship.startBraking();
            break;
        case 83:
            break;
        case 66: //B
            can_shoot = true;
            break;
    }
}

function onKeyDown(e) {
    'use strict';

    switch (e.keyCode) {
        case 65: //A
            gameMaterials.changeMaterialsWireframe();
            break;
        case 66: //B
            if (can_shoot) {
                generateBullet(ship);
                can_shoot = false;
            }
            break;

        case 67: //C
            togglePointLight();
            break;
        case 72: //H
            toggleSpotLight();
            break;
        case 71: //G
            if (current_material == "lambert") {
                current_material = "phong";
            } else {
                current_material = "lambert";
            }
            gameElements.forEach(function(element) {
                element.changeMaterialsType(current_material);
            });
            break;
        case 76: //L
            // enable/disable lighting - calculo da iluminação
            if (current_material == "basic") {
                current_material = old_material;
            } else {
                old_material = current_material;
                current_material = "basic";
            }
            gameElements.forEach(function(element) {
                element.changeMaterialsType(current_material);
            });
            break;
        case 78: //N
            // modo de dia/noite - ligar/desligar luz direcional
            toggleDirectionalLight();
            break;
        case 49: //1
            camera = camera_1;
            current_camera = "1";
            camera.aspect = window.innerWidth / window.innerHeight;
            resizeOrthographicCamera();
            camera.updateProjectionMatrix();
            break;
        case 50: //2
            camera = camera_2;
            current_camera = "2";
            camera.aspect = window.innerWidth / window.innerHeight;
            camera.updateProjectionMatrix();
            break;
        case 51: //3
            camera = camera_3;
            current_camera = "3";
            camera.aspect = window.innerWidth / window.innerHeight;
            camera.updateProjectionMatrix();
            break;
        case 82: //R
            if (gameState == "finished" || gameState == "gameover") {
                resetGameVariables();
            }
            break;
        case 83: //S
            if (gameState == "active") {
                pauseGame();
            }
            else if (gameState == "paused") {
                playGame();
            }
            break;
        case 37: // left arrow key
            ship.accelerating = "yes";
            ship.acceleratingDirection = "left";
            break;
        case 39: // right arrow key
            ship.accelerating = "yes";
            ship.acceleratingDirection = "right";
            break;
    }
}

function animate() {
    'use strict';
    if (gameScore == 0 && gameState == "active") {
        finishGame();
    }
    var delta = clock.getDelta();
    // detect Colisions
    gameElements.forEach(function(element) {
        element.detectColisions(delta)
    });
    // move gameElements (ship, aliens, bullets)
    gameElements.forEach(function(element) {
        element.executeMovement(delta);
    });
    if (current_camera == "3") {
        cameraFollowShip();
    }

    render();
    requestAnimationFrame(animate);
}

function updateScoreOnAlienKill(){
    gameScore = gameScore - 1;
}

function loseShipLife(){
    // remove one generated ship
    scene.remove(shipLivesObjects.pop());
    // if ship lives counter is zero, show game over
    if (shipLivesObjects.length == 0) {
        gameOver();
    }
}
