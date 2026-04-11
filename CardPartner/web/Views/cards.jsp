<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;700&family=Poppins:wght@400;600&display=swap" rel="stylesheet">
    
    <title>Card Partner - Cartas</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/cardsStyle.css?v=3">
</head>
<body>

    <div class="fondo-rosa">
        <div class="contenedor-principal">
            
            <jsp:include page="/components/cabecera.jsp" />

            <main class="contenido-cartas">
                
                <div class="barra-busqueda">
                    <h2 class="titulo-seccion">CARDS</h2>
                    <div class="filtros">
                        <input type="text" placeholder="Buscar personaje, ID o color...">
                        <a href="#modal-filtros" class="btn-filtro" style="text-decoration: none; display: inline-block;">Filtros ⚙️</a>
                    </div>
                </div>

                <section class="galeria-cartas">
                    
                    <a href="card-view.jsp" class="carta-item">
                        <img src="${pageContext.request.contextPath}/img/op01-120.jpg" alt="Shanks OP01-120">
                        <div class="carta-info">
                            <h3>Shanks</h3>
                            <p>OP01-120 | SEC (Secret Rare)</p>
                            <span class="precio">45.50 €</span>
                        </div>
                    </a>

                    <a href="card-view.jsp" class="carta-item">
                        <img src="${pageContext.request.contextPath}/img/op01-025.jpg" alt="Roronoa Zoro OP01-025">
                        <div class="carta-info">
                            <h3>Roronoa Zoro</h3>
                            <p>OP01-025 | SR (Super Rare)</p>
                            <span class="precio">18.00 €</span>
                        </div>
                    </a>

                    <a href="card-view.jsp" class="carta-item">
                        <img src="${pageContext.request.contextPath}/img/op05-119.jpg" alt="Monkey.D.Luffy OP05-119">
                        <div class="carta-info">
                            <h3>Monkey.D.Luffy</h3>
                            <p>OP05-119 | SEC (Secret Rare)</p>
                            <span class="precio">32.25 €</span>
                        </div>
                    </a>

                    <a href="card-view.jsp" class="carta-item">
                        <img src="${pageContext.request.contextPath}/img/op01-047.jpg" alt="Trafalgar Law OP01-047">
                        <div class="carta-info">
                            <h3>Trafalgar Law</h3>
                            <p>OP01-047 | SR (Super Rare)</p>
                            <span class="precio">12.10 €</span>
                        </div>
                    </a>
                    
                    <a href="card-view.jsp" class="carta-item">
                        <img src="${pageContext.request.contextPath}/img/op01-016.jpg" alt="Nami OP01-016">
                        <div class="carta-info">
                            <h3>Nami</h3>
                            <p>OP01-016 | R (Rare)</p>
                            <span class="precio">5.50 €</span>
                        </div>
                    </a>

                </section>
            </main>

        </div>
    </div>

    <div id="modal-filtros" class="modal-overlay">
        <div class="modal-content">
            <a href="#" class="btn-cerrar">&times;</a>
            <h2 class="titulo-seccion" style="font-size: 24px; margin-bottom: 20px; text-align: left;">Filtros de Búsqueda</h2>

            <form action="#" style="text-align: left;">
                <div style="margin-bottom: 15px;">
                    <label style="font-weight: bold; display: block; margin-bottom: 5px;">Color de la carta:</label>
                    <select style="width: 100%; padding: 10px; border-radius: 8px; border: 1px solid #ccc; font-family: 'Poppins', sans-serif; font-size: 14px;">
                        <option>Todos los colores</option>
                        <option>Rojo</option>
                        <option>Verde</option>
                        <option>Azul</option>
                        <option>Morado</option>
                        <option>Negro</option>
                        <option>Amarillo</option>
                    </select>
                </div>

                <div style="margin-bottom: 15px;">
                    <label style="font-weight: bold; display: block; margin-bottom: 5px;">Rareza:</label>
                    <div style="display: flex; gap: 10px; flex-wrap: wrap; font-size: 14px;">
                        <label><input type="checkbox"> Common (C)</label>
                        <label><input type="checkbox"> Uncommon (UC)</label>
                        <label><input type="checkbox"> Rare (R)</label>
                        <label><input type="checkbox"> Super Rare (SR)</label>
                        <label><input type="checkbox"> Secret Rare (SEC)</label>
                    </div>
                </div>

                <div style="margin-bottom: 25px;">
                    <label style="font-weight: bold; display: block; margin-bottom: 5px;">Coste de Don!!:</label>
                    <input type="range" min="1" max="10" style="width: 100%;">
                    <div style="display: flex; justify-content: space-between; font-size: 12px; color: #666;">
                        <span>1</span><span>10</span>
                    </div>
                </div>

                <a href="#" class="btn-filtro" style="width: 100%; background: #333; color: white; text-align: center; display: block; text-decoration: none;">APLICAR FILTROS</a>
            </form>
        </div>
    </div>
</body>
</html>