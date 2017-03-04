class Bullet extends GameEntity {
    constructor(x) {
        super();
        this.collidedAlien = 0;
        this.buildBullet(x);
        this.setSpeed(0, -500);
        this.setCollisionRadius(10);
    }
    changeMaterialsType(type) {
        this.children[0].material = gameMaterials.getBulletMaterial(type, "red");
    }
    checkTopBottomLimitsColision(){
        if (this.position.z < -325) {
            this.removeElement();
        }
    }
    processColisions(element){
        if (element instanceof Alien) {
            this.removeElement();
            element.removeElement();
            updateScoreOnAlienKill();
        }
    }

    //------make bullet----------------
    buildBullet(x) {
        this.position.set(x, 40, 200);
        scene.add(this);
        this.addBullet();
    }
    addBullet() {
        var geometry = new THREE.CubeGeometry(5, 5, 15);
        var mesh = new THREE.Mesh(geometry, gameMaterials.getBulletMaterial("lambert", "red"));
        mesh.position.set(0, 0, 0);
        this.add(mesh);
    }
}

function generateBullet(ship) {
    //bullets.push(new Bullet(ship.position.x))
    gameElements.push(new Bullet(ship.position.x))
}
