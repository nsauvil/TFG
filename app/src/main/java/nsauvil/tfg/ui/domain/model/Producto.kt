package nsauvil.tfg.ui.domain.model

data class Producto(val id:String, val nom_producto:String, val cord1:Float, val cord2:Float, val cord11:Float, val cord22:Float){
    constructor(): this("","",0.0f,0.0f,0.0f,0.0f)
}
//id es el nombre del producto, sin mayúsculas ni tildes, para facilitar su búsqueda posterior
//nom_producto es el nombre del producto tal y como debe aparecer en la interfaz
//cord1 es la coordenada horizontal del producto en la orientación vertical
//cord2 es la coordenada vertical del producto en la orientación vertical
//cord11 es la coordenada horizontal del producto en la orientación apaisada
//cord22 es la coordenada vertical del producto en la orientación apaisada