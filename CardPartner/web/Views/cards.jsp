<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="modelo.Carta"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    // ==========================================
    // SIMULACIÓN (MOCK) - A borrar cuando haya BD
    // En el futuro, el Servlet te pasará la lista así: 
    // List<Carta> listaCartas = dao.obtenerTodasLasCartas();
    // ==========================================
    
    List<Carta> listaCartas = new ArrayList<>();
    listaCartas.add(new Carta("OP01-120", "Shanks", "SEC (Secret Rare)", "Rojo", "Personaje", 9, 10000, "", "/img/op01-120.jpg"));
    listaCartas.add(new Carta("OP01-025", "Roronoa Zoro", "SR (Super Rare)", "Rojo", "Personaje", 3, 5000, "", "/img/op01-025.jpg"));
    listaCartas.add(new Carta("OP05-119", "Monkey.D.Luffy", "SEC (Secret Rare)", "Morado", "Personaje", 5, 6000, "", "/img/op05-119.jpg"));
    listaCartas.add(new Carta("OP01-047", "Trafalgar Law", "SR (Super Rare)", "Verde/Rojo", "Personaje", 5, 6000, "", "/img/op01-047.jpg"));
    listaCartas.add(new Carta("OP01-016", "Nami", "R (Rare)", "Rojo", "Personaje", 1, 1000, "", "/img/op01-016.jpg"));
%>
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
                    
                    <%
                        // BUCLE MÁGICO: Dibuja una caja por cada carta en la lista
                        for(Carta c : listaCartas) {
                            
                            // Generamos un precio aleatorio entre 5€ y 65€ para dar realismo al prototipo
                            double precioMercado = Math.round((Math.random() * 60 + 5) * 100.0) / 100.0;
                    %>
                        <a href="${pageContext.request.contextPath}/Views/card-view.jsp?id=<%= c.getIdCarta() %>" class="carta-item">
                            <img src="${pageContext.request.contextPath}<%= c.getImagenUrl() %>" alt="<%= c.getNombre() %>">
                            <div class="carta-info">
                                <h3><%= c.getNombre() %></h3>
                                <p><%= c.getIdCarta() %> | <%= c.getRareza() %></p>
                                <span class="precio"><%= String.format("%.2f", precioMercado) %> €</span>
                            </div>
                        </a>
                    <%
                        }
                    %>

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