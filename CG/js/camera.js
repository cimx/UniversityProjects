var camera_1, camera_2, camera_3;

var shipLivesCamera;
var pauseCamera;

function createShipLivesCamera(x, y, z) {
    'use strict';
    var aspect = window.innerWidth / window.innerHeight;
    shipLivesCamera = new THREE.OrthographicCamera(150 * (-aspect), 150 * aspect, 100, -100, 1, 500);
    shipLivesCamera.position.set(x, y, z);
    shipLivesCamera.lookAt(new THREE.Vector3(x, 0, z));
}

function createGameStateCamera(x, y, z) {
    'use strict';
    var aspect = window.innerWidth / window.innerHeight;
    pauseCamera = new THREE.OrthographicCamera(150 * (-aspect), 150 * aspect, 100, -100, 1, 500);
    pauseCamera.position.set(x, y, z);
    pauseCamera.lookAt(new THREE.Vector3(x, 0, z));
}

function updateGameStateCamera(x,y,z){
	pauseCamera.position.set(x, y, z);
    pauseCamera.lookAt(new THREE.Vector3(x, 0, z));
}

function createOrthographicCamera(x,y,z) {
    'use strict';
    var world_aspect = 1200 / 800;
    var aspect = window.innerWidth / window.innerHeight;
    if (aspect > world_aspect) {
        //diminui altura (aunmentar largura)
        var viewSize = 400;
        camera_1 = new THREE.OrthographicCamera(viewSize * (-aspect), viewSize * aspect,
            viewSize, -viewSize, 1, 1000);
    } else {
        //diminui largura (aumentar altura)
        var viewSize = 600;
        camera_1 = new THREE.OrthographicCamera(-viewSize, viewSize,
            viewSize / aspect, -viewSize / aspect, 1, 1000);
    }
    camera_1.position.set(x, y, z);
    camera_1.lookAt(new THREE.Vector3(x, 0, z));
}

function createPerspectiveCamera(x, y ,z) {
    'use strict'
    var perspectiveCamera = new THREE.PerspectiveCamera(65,
            window.innerWidth / window.innerHeight,
            1,
            1500);
    perspectiveCamera.position.set(x, y , z);
    perspectiveCamera.lookAt(scene.position);
    return perspectiveCamera;
}

function resizeOrthographicCamera() {
    var world_aspect = 1200 / 800;
    var aspect = window.innerWidth / window.innerHeight;
    if (aspect > world_aspect) {
        //diminui altura (aunmentar largura)
        var viewSize = 400;
        camera_1.left = viewSize * (-aspect);
        camera_1.right = viewSize * aspect;
        camera_1.top = viewSize;
        camera_1.bottom = -viewSize;
    } else {
        //diminui largura (aumentar altura)
        var viewSize = 600;
        camera_1.left = viewSize;
        camera_1.right = -viewSize;
        camera_1.top = viewSize / aspect;
        camera_1.bottom = viewSize / (-aspect);
    }
}

function cameraFollowShip() {
    camera_3.position.set(ship.position.x, 270, 440);
    camera_3.lookAt(new THREE.Vector3(ship.position.x, 0, 0));
}
