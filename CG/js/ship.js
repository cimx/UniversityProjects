var ship;
var shipLivesObjects = [];
class Ship extends GameEntity {
    // Class constructor
    constructor(x, y, z) {
        super(x, y, z);
        this.setSpeed(0, 0);
        this.acceleration = 250;
        this.brakes = 200;
        this.maxspeed = 400;
        this.minspeed = -this.maxspeed;
        this.accelerating = "no";
        this.acceleratingDirection = "none";
        this.buildShip(x, y, z);
		this.setCollisionRadius(80);
    }
    changeMaterialsType(type) {
        this.children[0].material = gameMaterials.getShipMaterial(type, "blue");
        this.children[1].material = gameMaterials.getShipMaterial(type, "green");
        this.children[2].material = gameMaterials.getShipMaterial(type, "red");
        this.children[3].material = gameMaterials.getShipMaterial(type, "green");
        this.children[4].material = gameMaterials.getShipMaterial(type, "green");
        this.children[5].material = gameMaterials.getShipMaterial(type, "blue");
        this.children[6].material = gameMaterials.getShipMaterial(type, "blue");
        this.children[7].material = gameMaterials.getShipMaterial(type, "red");
        this.children[8].material = gameMaterials.getShipMaterial(type, "red");
    }

    // Movement methods
    executeMovement(delta) {
        if (this.accelerating == "yes") {
            this.accelerate(delta);
        } else if (this.accelerating == "brake") {
            this.brake(delta);
        }
        super.executeMovement(delta);
    }
    checkLeftRightLimitsColision() {
        if (this.position.x > 508) {
            // limit max position
            this.position.x = 508;
            this.setSpeed_x(0);
        }
        if (this.position.x < -508) {
            // limit min position
            this.position.x = -508;
            this.setSpeed_x(0);
        }
    }
    accelerate(delta) {
        if (this.acceleratingDirection == "left") {
            this.setSpeed_x(this.getSpeed_x() - (delta * this.acceleration));
        } else if (this.acceleratingDirection == "right") {
            this.setSpeed_x(this.getSpeed_x() + (delta * this.acceleration));
        }
        if (this.getSpeed_x() > this.maxspeed) {
            // limit max speed
            this.setSpeed_x(this.maxspeed);
        } else if (this.getSpeed_x() < this.minspeed) {
            // limit min speed
            this.setSpeed_x(this.minspeed);
        }
    }
    brake(delta) {
        if (this.getSpeed_x() > 0) {
            this.setSpeed_x(this.getSpeed_x() - (delta * this.brakes));
            if (this.getSpeed_x() < 0) {
                this.setSpeed_x(0);
                this.accelerating = "no";
            }
        } else {
            this.setSpeed_x(this.getSpeed_x() + (delta * this.brakes));
            if (this.getSpeed_x() > 0) {
                this.setSpeed_x(0);
                this.accelerating = "no";
            }
        }
    }
    startBraking() {
        this.accelerating = "brake";
    }

    processColisions(element){
        if (element instanceof Alien) {
            element.removeElement();
            updateScoreOnAlienKill();
            loseShipLife();
        }
    }
    // Ship 3D modelling
    buildShip(x, y, z) {
        scene.add(this);
        this.addShipBody();
        this.addShipWindow();
        this.addShipCannon();
        this.addShipWingConnectorLeft();
        this.addShipWingConnectorRight();
        this.addShipWingLeft();
        this.addShipWingRight();
        this.addShipThrusterLeft();
        this.addShipThrusterRight();
    }
    addShipBody() {
        'use strict';
        var vertices = [
            new THREE.Vector3(-17.5, 20, -50), //vertice B 0
            new THREE.Vector3(17.5, 20, -50), //vertice C 1
            new THREE.Vector3(-17.5, 20, 50), //vertice H 2
            new THREE.Vector3(17.5, 20, 50), //vertice I 3
            new THREE.Vector3(-17.5, -20, -50), //vertice B' 4
            new THREE.Vector3(17.5, -20, -50), //vertice C' 5
            new THREE.Vector3(-17.5, -20, 50), //vertice H' 6
            new THREE.Vector3(17.5, -20, 50), //vertice I' 7
        ];

        var faces = [
            new THREE.Face3(1, 0, 2), // face C,B,H
            new THREE.Face3(2, 3, 1), // face H,I,C

            new THREE.Face3(0, 4, 6), // face B,B',H'
            new THREE.Face3(6, 2, 0), // face H',H,B

            new THREE.Face3(5, 1, 3), // face C',C,I
            new THREE.Face3(3, 7, 5), // face I,I',C'

            new THREE.Face3(1, 0, 4), // face C,B,B'
            new THREE.Face3(4, 5, 1), // face B',C',C

            new THREE.Face3(3, 2, 6), // face I,H,H'
            new THREE.Face3(6, 7, 3), // face H',I',I

            new THREE.Face3(5, 4, 6), // face C',B',H'
            new THREE.Face3(6, 7, 5), // face H',I',C'

        ];
        var geom = new THREE.Geometry();
        geom.vertices = vertices;
        geom.faces = faces;
        geom.computeFaceNormals();

        var mesh = new THREE.Mesh(geom, gameMaterials.getShipMaterial("lambert", "blue"));
        mesh.position.set(0, 0, 0);
        this.add(mesh);
    }
    addShipWindow() {
        'use strict';
        var vertices = [
            new THREE.Vector3(-7.5, 25, -32.5), //vertice D 0
            new THREE.Vector3(7.5, 25, -32.5), //vertice E 1
            new THREE.Vector3(-7.5, 25, -17.5), //vertice F 2
            new THREE.Vector3(7.5, 25, -17.5), //vertice G 3
            new THREE.Vector3(-7.5, 5, -32.5), //vertice D' 4
            new THREE.Vector3(7.5, 5, -32.5), //vertice E' 5
            new THREE.Vector3(-7.5, 5, -17.5), //vertice F' 6
            new THREE.Vector3(7.5, 5, -17.5), //vertice G' 7
        ];

        var faces = [
            new THREE.Face3(1, 0, 2), // face E,D,F
            new THREE.Face3(2, 3, 1), // face F,G,E

            new THREE.Face3(0, 4, 6), // face D,D',F'
            new THREE.Face3(6, 2, 0), // face F',F,D

            new THREE.Face3(5, 1, 3), // face E',E,G
            new THREE.Face3(3, 7, 5), // face G,G',E'

            new THREE.Face3(0, 1, 5), // face K,J,J'
            new THREE.Face3(5, 4, 0), // face J',K',K

            new THREE.Face3(3, 2, 6), // face G,F,F'
            new THREE.Face3(6, 7, 3), // face F',G',G

            new THREE.Face3(5, 4, 6), // face E',D',F'
            new THREE.Face3(6, 7, 5), // face F',G',E'
        ];

        var geom = new THREE.Geometry();
        geom.vertices = vertices;
        geom.faces = faces;
        geom.computeFaceNormals();

        var mesh = new THREE.Mesh(geom, gameMaterials.getShipMaterial("lambert", "green"));
        mesh.position.set(0, 0, 0);
        this.add(mesh);
    }
    addShipCannon() {
        'use strict';
        var vertices = [
            new THREE.Vector3(-17.5, 20, -50), //vertice B 0
            new THREE.Vector3(17.5, 20, -50), //vertice C 1
            new THREE.Vector3(-17.5, -20, -50), //vertice B'2
            new THREE.Vector3(17.5, -20, -50), //vertice C' 3
            new THREE.Vector3(0, 0, -77.5), //vertice A 4
        ];

        var faces = [
            new THREE.Face3(0, 1, 3), // face C,B,B'
            new THREE.Face3(3, 2, 0), // face B',C',C

            new THREE.Face3(0, 1, 4), // face B,C,A
            new THREE.Face3(1, 3, 4), // face B,B',A
            new THREE.Face3(3, 2, 4), // face C',B',A
            new THREE.Face3(2, 0, 4), // face C,C',A
        ];

        var geom = new THREE.Geometry();
        geom.vertices = vertices;
        geom.faces = faces;
        geom.computeFaceNormals();

        var mesh = new THREE.Mesh(geom, gameMaterials.getShipMaterial("lambert", "red"));
        mesh.position.set(0, 0, 0);
        this.add(mesh);
    }
    addShipThrusterLeft() {
        'use strict';
        var vertices = [
            new THREE.Vector3(-42.5, 15, 50.25), //vertice L 0
            new THREE.Vector3(-27.5, 15, 50.25), //vertice M 1
            new THREE.Vector3(-42.5, -15, 50.25), //vertice L'2
            new THREE.Vector3(-27.5, -15, 50.25), //vertice M' 3
            new THREE.Vector3(-35, 0, 60), //vertice R 4

        ];

        var faces = [
            new THREE.Face3(1, 0, 2), // face M,L,L'
            new THREE.Face3(2, 3, 1), // face L',M',M

            new THREE.Face3(0, 2, 4), // face B,C,A
            new THREE.Face3(1, 0, 4), // face C,C',A
            new THREE.Face3(3, 1, 4), // face C',B',A
            new THREE.Face3(2, 3, 4), // face B,B',A
        ];

        var geom = new THREE.Geometry();
        geom.vertices = vertices;
        geom.faces = faces;
        geom.computeFaceNormals();

        var mesh = new THREE.Mesh(geom, gameMaterials.getShipMaterial("lambert", "red"));
        mesh.position.set(0, 0, 0);
        this.add(mesh);
    }
    addShipThrusterRight() {
        'use strict';
        var vertices = [
            new THREE.Vector3(27.5, 15, 50.25), //vertice P 0
            new THREE.Vector3(42.5, 15, 50.25), //vertice Q 1
            new THREE.Vector3(27.5, -15, 50.25), //vertice P'2
            new THREE.Vector3(42.5, -15, 50.25), //vertice Q' 3
            new THREE.Vector3(35, 0, 60), //vertice S 4
        ];

        var faces = [
            new THREE.Face3(1, 0, 2), // face M,L,L'
            new THREE.Face3(2, 3, 1), // face L',M',M

            new THREE.Face3(0, 2, 4), // face B,C,A
            new THREE.Face3(1, 0, 4), // face C,C',A
            new THREE.Face3(3, 1, 4), // face C',B',A
            new THREE.Face3(2, 3, 4), // face B,B',A
        ];

        var geom = new THREE.Geometry();
        geom.vertices = vertices;
        geom.faces = faces;
        geom.computeFaceNormals();

        var mesh = new THREE.Mesh(geom, gameMaterials.getShipMaterial("lambert", "red"));
        mesh.position.set(0, 0, 0);
        this.add(mesh);
    }

    addShipWingLeft() {
        'use strict';
        var vertices = [
            new THREE.Vector3(-42.5, 15, 0), //vertice J 0
            new THREE.Vector3(-27.5, 15, 0), //vertice K 1
            new THREE.Vector3(-42.5, 15, 50), //vertice L 2
            new THREE.Vector3(-27.5, 15, 50), //vertice M 3
            new THREE.Vector3(-42.5, -15, 0), //vertice J' 4
            new THREE.Vector3(-27.5, -15, 0), //vertice K' 5
            new THREE.Vector3(-42.5, -15, 50), //vertice L' 6
            new THREE.Vector3(-27.5, -15, 50), //vertice M' 7
        ];

        var faces = [
            new THREE.Face3(1, 0, 2), // face K,J,L
            new THREE.Face3(2, 3, 1), // face L,M,K

            new THREE.Face3(0, 4, 6), // face J,J',L'
            new THREE.Face3(6, 2, 0), // face L',L,J

            new THREE.Face3(5, 1, 3), // face K',K,M
            new THREE.Face3(3, 7, 5), // face M,M',K'

            new THREE.Face3(0, 1, 5), // face K,J,J'
            new THREE.Face3(5, 4, 0), // face J',K',K

            new THREE.Face3(3, 2, 6), // face M,L,L'
            new THREE.Face3(6, 7, 3), // face L',M',M

            new THREE.Face3(5, 4, 6), // face K',J',L'
            new THREE.Face3(6, 7, 5), // face L',M',K'
        ];

        var geom = new THREE.Geometry();
        geom.vertices = vertices;
        geom.faces = faces;
        geom.computeFaceNormals();

        var mesh = new THREE.Mesh(geom, gameMaterials.getShipMaterial("lambert", "blue"));
        mesh.position.set(0, 0, 0);
        this.add(mesh);
    }
    addShipWingRight() {
        'use strict';
        var vertices = [
            new THREE.Vector3(27.5, 15, 0), //vertice N 0
            new THREE.Vector3(42.5, 15, 0), //vertice O 1
            new THREE.Vector3(27.5, 15, 50), //vertice P 2
            new THREE.Vector3(42.5, 15, 50), //vertice Q 3
            new THREE.Vector3(27.5, -15, 0), //vertice N' 4
            new THREE.Vector3(42.5, -15, 0), //vertice P' 5
            new THREE.Vector3(27.5, -15, 50), //vertice Q' 6
            new THREE.Vector3(42.5, -15, 50), //vertice N' 7
        ];

        var faces = [
            new THREE.Face3(1, 0, 2), // face O,N,P
            new THREE.Face3(2, 3, 1), // face P,Q,O

            new THREE.Face3(0, 4, 6), // face N,N',Q'
            new THREE.Face3(6, 2, 0), // face Q',P,N

            new THREE.Face3(5, 1, 3), // face P',O,Q
            new THREE.Face3(3, 7, 5), // face Q,N',P'

            new THREE.Face3(0, 1, 5), // face K,J,J'
            new THREE.Face3(5, 4, 0), // face J',K',K

            new THREE.Face3(3, 2, 6), // face Q,P,Q'
            new THREE.Face3(6, 7, 3), // face Q',N',Q

            new THREE.Face3(5, 4, 6), // face P',N',Q'
            new THREE.Face3(6, 7, 5), // face Q',N',P'
        ];

        var geom = new THREE.Geometry();
        geom.vertices = vertices;
        geom.faces = faces;
        geom.computeFaceNormals();

        var mesh = new THREE.Mesh(geom, gameMaterials.getShipMaterial("lambert", "blue"));
        mesh.position.set(0, 0, 0);
        this.add(mesh);
    }

    addShipWingConnectorLeft() {
        'use strict';
        var vertices = [
            new THREE.Vector3(-32.5, 10, 24), //vertice T 0
            new THREE.Vector3(-17.5, 10, 24), //vertice U 1
            new THREE.Vector3(-32.5, 10, 36), //vertice V 2
            new THREE.Vector3(-17.5, 10, 36), //vertice X 3
            new THREE.Vector3(-32.5, -10, 24), //vertice T' 4
            new THREE.Vector3(-17.5, -10, 24), //vertice U' 5
            new THREE.Vector3(-32.5, -10, 36), //vertice V' 6
            new THREE.Vector3(-17.5, -10, 36), //vertice X' 7
        ];

        var faces = [
            new THREE.Face3(1, 0, 2), // face U,T,V
            new THREE.Face3(2, 3, 1), // face V,X,U

            new THREE.Face3(0, 4, 6), // face T,T',V'
            new THREE.Face3(6, 2, 0), // face V',V,T

            new THREE.Face3(5, 1, 3), // face U',U,X
            new THREE.Face3(3, 7, 5), // face X,X',U'

            new THREE.Face3(0, 1, 5), // face K,J,J'
            new THREE.Face3(5, 4, 0), // face J',K',K

            new THREE.Face3(3, 2, 6), // face X,V,V'
            new THREE.Face3(6, 7, 3), // face V',X',X

            new THREE.Face3(5, 4, 6), // face U',T',V'
            new THREE.Face3(6, 7, 5), // face V',X',U'
        ];

        var geom = new THREE.Geometry();
        geom.vertices = vertices;
        geom.faces = faces;
        geom.computeFaceNormals();

        var mesh = new THREE.Mesh(geom, gameMaterials.getShipMaterial("lambert", "green"));
        mesh.position.set(0, 0, 0);
        this.add(mesh);
    }
    addShipWingConnectorRight() {
        'use strict';
        var vertices = [
            new THREE.Vector3(17.5, 10, 24), //vertice Z 0
            new THREE.Vector3(32.5, 10, 24), //vertice A' 1
            new THREE.Vector3(17.5, 10, 36), //vertice B' 2
            new THREE.Vector3(32.5, 10, 36), //vertice C' 3
            new THREE.Vector3(17.5, -10, 24), //vertice Z' 4
            new THREE.Vector3(32.5, -10, 24), //vertice A'' 5
            new THREE.Vector3(17.5, -10, 36), //vertice B'' 6
            new THREE.Vector3(32.5, -10, 36), //vertice C'' 7
        ];

        var faces = [
            new THREE.Face3(1, 0, 2), // face A',Z,B'
            new THREE.Face3(2, 3, 1), // face B',C',A'

            new THREE.Face3(0, 4, 6), // face Z,Z',B''
            new THREE.Face3(6, 2, 0), // face B'',B',Z

            new THREE.Face3(5, 1, 3), // face A'',A',C'
            new THREE.Face3(3, 7, 5), // face C', C'',A''

            new THREE.Face3(0, 1, 5), // face K,J,J'
            new THREE.Face3(5, 4, 0), // face J',K',K

            new THREE.Face3(3, 2, 6), // face C',B',B''
            new THREE.Face3(6, 7, 3), // face B'',C'',C'

            new THREE.Face3(5, 4, 6), // face A'',Z',B''
            new THREE.Face3(6, 7, 5), // face B'',C'',A''
        ];

        var geom = new THREE.Geometry();
        geom.vertices = vertices;
        geom.faces = faces;
        geom.computeFaceNormals();

        var mesh = new THREE.Mesh(geom, gameMaterials.getShipMaterial("lambert", "green"));
        mesh.position.set(0, 0, 0);
        this.add(mesh);
    }

}

function generateShip(spotlight) {
    ship = new Ship(0, 40, 250);
    ship.add(spotlight);
    ship.add(spotlight.target);
    gameElements.push(ship);
}

function generateShipLives(position_z) {
  shipLivesObjects.push(new Ship(-200, 0, position_z));
  shipLivesObjects.push(new Ship(0, 0, position_z));
  shipLivesObjects.push(new Ship(200, 0, position_z));
  shipLivesObjects.forEach(function(element) {
    element.changeMaterialsType("basic");
  });
}
