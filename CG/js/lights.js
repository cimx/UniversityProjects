var directionalLight, spotlight;
var star1, star2, star3, star4, star5, star6;
var lightColor = 0xffffff;
var lightIntensity = 1;
var lightDistance = 400;
var sphere = new THREE.SphereGeometry(5, 16, 8);
var starMaterial = new THREE.MeshBasicMaterial({
    color: lightColor
});

function addDirectionalLight() {
    directionalLight = new THREE.DirectionalLight(lightColor, 1);
    directionalLight.position.set(1, 1, 1);
    scene.add(directionalLight);
}

function toggleDirectionalLight() {
    directionalLight.visible = !directionalLight.visible;
}

function addPointLight() {
    star1 = new THREE.PointLight(lightColor, lightIntensity, lightDistance);
    //star1.add(new THREE.Mesh(sphere, starMaterial));
    star1.position.set(-400, 50, 100);
    scene.add(star1);

    star2 = new THREE.PointLight(lightColor, lightIntensity, lightDistance);
    //star2.add(new THREE.Mesh(sphere, starMaterial));
    star2.position.set(0, 50, 100);
    scene.add(star2);

    star3 = new THREE.PointLight(lightColor, lightIntensity, lightDistance);
    //star3.add(new THREE.Mesh(sphere, starMaterial));
    star3.position.set(400, 50, 100);
    scene.add(star3);

    star4 = new THREE.PointLight(lightColor, lightIntensity, lightDistance);
    //star4.add(new THREE.Mesh(sphere, starMaterial));
    star4.position.set(-400, 50, -250);
    scene.add(star4);

    star5 = new THREE.PointLight(lightColor, lightIntensity, lightDistance);
    //star5.add(new THREE.Mesh(sphere, starMaterial));
    star5.position.set(0, 50, -250);
    scene.add(star5);

    star6 = new THREE.PointLight(lightColor, lightIntensity, lightDistance);
    //star6.add(new THREE.Mesh(sphere, starMaterial));
    star6.position.set(400, 50, -250);
    scene.add(star6);
}

function togglePointLight() {
    star1.visible = !star1.visible;
    star2.visible = !star2.visible;
    star3.visible = !star3.visible;
    star4.visible = !star4.visible;
    star5.visible = !star5.visible;
    star6.visible = !star6.visible;
}

function addSpotLight(){
    spotlight = new THREE.SpotLight( 0xffffff, 10);
    spotlight.angle = Math.PI / 4;
    spotlight.distance = 500;
    return spotlight;
}

function setInitialSpotlightPositions(spotlight){
    spotlight.position.set(0, 40, -30);
    //spotlight.position.set(0, 33, -20);
    spotlight.target.position.set(ship.position.x, 0, -500);
}

function toggleSpotLight() {
    spotlight.visible = !spotlight.visible;
}
