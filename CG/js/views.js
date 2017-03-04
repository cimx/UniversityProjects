var showPausedView = true;

var views = {
    gameView: {
        left: 0,
        bottom: 0,
        width: 1,
        height: 1.0,
    },
    shipLives: {
        // posicionar a view
        left: 0.8,
        bottom: 0.85,
        // tamanho da view
        width: 0.2,
        height: 0.15,
    },
	pauseView: {
		left: 0.2,
		bottom: 0.2,
		width: 0.6,
		height: 0.6,
	}
};

function render() {
    'use strict';
    renderer.clear();
    // render main game viewport
    renderView(views.gameView);

    // render ship lives viewport
    views.shipLives.camera = shipLivesCamera;
    renderView(views.shipLives);

    if (showPausedView) {
        // render pause message viewport
        views.pauseView.camera = pauseCamera;
        renderView(views.pauseView);
    }
}

function renderView(view) {
    // define position and size of viewport
    var left = Math.floor(window.innerWidth * view.left);
    var bottom = Math.floor(window.innerHeight * view.bottom);
    var width = Math.floor(window.innerWidth * view.width);
    var height = Math.floor(window.innerHeight * view.height);
    renderer.setViewport(left, bottom, width, height);

    if (view.camera != undefined) {
        // viewport has specific camera - pause or shipLives views
        view.camera.aspect = width / height;
        view.camera.updateProjectionMatrix();
        renderer.render(scene, view.camera);
    }
    else {
        // default main game viewport
        camera.aspect = width / height;
        camera.updateProjectionMatrix();
        renderer.render(scene, camera);
    }
}

function createGameStateMessage(imagePath,x,y,z) {
    var loader = new THREE.TextureLoader();
    loader.load(
    	// resource URL
    	imagePath,
    	// Function when resource is loaded
    	function ( texture ) {
    		// do something with the texture
    		var material = new THREE.MeshBasicMaterial( {
    			map: texture
    		 } );
            var plane = new THREE.Mesh(new THREE.PlaneGeometry(200, 25), material);
            plane.position.set(x,y,z);
            plane.rotation.x = -Math.PI / 2;
            scene.add(plane);
    	},
    	// Function called when download progresses
    	function ( xhr ) {
    		console.log( (xhr.loaded / xhr.total * 100) + '% loaded' );
    	},
    	// Function called when download errors
    	function ( xhr ) {
    		console.log( 'An error happened' );
    	}
    )

}

function createBackground(imagePath){
    var loader = new THREE.TextureLoader();
    loader.load(
        // resource URL
        imagePath,
        // Function when resource is loaded
        function ( texture ) {
            // do something with the texture
            var material = new THREE.MeshBasicMaterial( {
                map: texture
             } );
            var plane = new THREE.Mesh(new THREE.PlaneGeometry(3500, 2100), material);
            plane.position.set(0,-250,0);
            plane.rotation.x = -Math.PI / 3;
            scene.add(plane);
        },
        // Function called when download progresses
        function ( xhr ) {
            console.log( (xhr.loaded / xhr.total * 100) + '% loaded' );
        },
        // Function called when download errors
        function ( xhr ) {
            console.log( 'An error happened' );
        }
    )
};
