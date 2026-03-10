package SegundUM.Productos.dominio;

public enum EstadoProducto {
    NUEVO(5),
    COMO_NUEVO(4),
    BUEN_ESTADO(3),
    ACEPTABLE(2),
    PARA_PIEZAS_O_REPARAR(1);
    
    private final int nivel;
    
    EstadoProducto(int nivel) {
        this.nivel = nivel;
    }
    
    public int getNivel() {
        return nivel;
    }
    
    // Método para comparar estados (mejor >= peor)
    public boolean esMejorOIgualQue(EstadoProducto otro) {
        return this.nivel >= otro.nivel;
    }
    
    @Override
    public String toString() {
    			return this.name();
		}
    
}