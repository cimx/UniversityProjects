class MaterialManager {
    constructor() {
            this.alienMaterials = {
                basic: {
                    red: new THREE.MeshBasicMaterial({
                        color: 0xff0000
                    }),
                    green: new THREE.MeshBasicMaterial({
                        color: 0x00ff00
                    }),
                    blue: new THREE.MeshBasicMaterial({
                        color: 0x0000ff
                    }),
                    white: new THREE.MeshBasicMaterial({
                        color: 0xffffff
                    })
                },
                phong: {
                    red: new THREE.MeshPhongMaterial({
                        color: 0xff0000,
                        specular: 0xcf2828,
                        shininess: 100
                    }),
                    green: new THREE.MeshPhongMaterial({
                        color: 0x00ff00,
                        specular: 0x894040,
                        shininess: 100
                    }),
                    blue: new THREE.MeshPhongMaterial({
                        color: 0x0000ff,
                        specular: 0x292391,
                        shininess: 100
                    }),
                    white: new THREE.MeshPhongMaterial({
                        color: 0xffffff,
                        specular: 0x392d2d,
                        shininess: 100
                    }),
                },
                lambert: {
                    red: new THREE.MeshLambertMaterial({
                        color: 0xff0000
                    }),
                    green: new THREE.MeshLambertMaterial({
                        color: 0x00ff00
                    }),
                    blue: new THREE.MeshLambertMaterial({
                        color: 0x0000ff
                    }),
                    white: new THREE.MeshLambertMaterial({
                        color: 0xffffff
                    }),
                },
            }
            this.shipMaterials = {
                basic: {
                    red: new THREE.MeshBasicMaterial({
                        color: 0xff0000
                    }),
                    green: new THREE.MeshBasicMaterial({
                        color: 0x00ff00
                    }),
                    blue: new THREE.MeshBasicMaterial({
                        color: 0x0000ff
                    }),
                },
                phong: {
                    red: new THREE.MeshPhongMaterial({
                        color: 0xff0000,
                        specular: 0xcf2828,
                        shininess: 100
                    }),
                    green: new THREE.MeshPhongMaterial({
                        color: 0x00ff00,
                        specular: 0x894040,
                        shininess: 100
                    }),
                    blue: new THREE.MeshPhongMaterial({
                        color: 0x0000ff,
                        specular: 0x292391,
                        shininess: 100
                    }),
                },
                lambert: {
                    red: new THREE.MeshLambertMaterial({
                        color: 0xff0000
                    }),
                    green: new THREE.MeshLambertMaterial({
                        color: 0x00ff00
                    }),
                    blue: new THREE.MeshLambertMaterial({
                        color: 0x0000ff
                    }),
                },
            }
            this.bulletMaterials = {
                basic: {
                    red: new THREE.MeshBasicMaterial({
                        color: 0xff0000
                    }),
                },
                phong: {
                    red: new THREE.MeshPhongMaterial({
                        color: 0xff0000,
                        specular: 0xcf2828,
                        shininess: 100
                    }),
                },
                lambert: {
                    red: new THREE.MeshLambertMaterial({
                        color: 0xff0000
                    }),
                },
            }
        }
        // Getters
    getAlienMaterial(type, color) {
        return this.alienMaterials[type][color];
    }
    getShipMaterial(type, color) {
        return this.shipMaterials[type][color];
    }
    getBulletMaterial(type, color) {
            return this.bulletMaterials[type][color];
        }
        // Change wireframe
    changeMaterialsWireframe() {
        for (let material in this) {
            for (let type in this[material]) {
                for (let color in this[material][type]) {
                    this[material][type][color].wireframe = !this[material][type][color].wireframe;
                }
            }
        }
    }
}
