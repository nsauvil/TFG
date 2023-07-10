package nsauvil.tfg.ui.domain.model

data class Producto(val id:String, val nom_producto:String, val cord1:Float, val cord2:Float, val cord11:Float, val cord22:Float){
    constructor(): this("","",0.0f,0.0f,0.0f,0.0f)
}
//cord11 y cord22 son las coordenadas del mapa en horizontal