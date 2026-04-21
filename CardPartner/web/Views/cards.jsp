<%@page import="java.util.List"%>
<%@page import="modelo.Carta"%>
<%@page import="datos.CartaDAO"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    // ==========================================
    // CONEXIÓN REAL A LA BASE DE DATOS
    // ==========================================
    CartaDAO cartaDao = new CartaDAO();
    
    // Traemos las primeras 20 cartas de la base de datos.
    // Los 4 primeros 'null' son los filtros (color, tipo, etc) que de momento dejamos vacíos.
    List<Carta> listaCartas = cartaDao.filtrarCartas(null, null, null, null, 20, 0);
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;700&family=Poppins:wght@400;600&display=swap" rel="stylesheet">
    
    <title>Card Partner - Cartas</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/cardsStyle.css?v=4">
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
                        // Comprobamos si hay cartas y las dibujamos
                        if (listaCartas != null && !listaCartas.isEmpty()) {
                            for(Carta c : listaCartas) {
                                // Como el script de la BD aún no tiene imágenes, ponemos el logo por defecto si viene null
                                String imgCarta = (c.getImagen() != null) ? c.getImagen() : "/img/logo.png";
                                
                                // Mantenemos el truco del precio aleatorio para que se vea bien visualmente
                                double precioMercado = Math.round((Math.random() * 60 + 5) * 100.0) / 100.0;
                    %>
                        <a href="${pageContext.request.contextPath}/Views/card-view.jsp?id=<%= c.getIdCarta() %>" class="carta-item">
                            <img src="${pageContext.request.contextPath}<%= imgCarta %>" alt="<%= c.getNombreCarta() %>">
                            <div class="carta-info">
                                <h3><%= c.getNombreCarta() %></h3>
                                <p><%= c.getIdCarta() %> | <%= c.getTipo() %></p>
                                <span class="precio"><%= String.format("%.2f", precioMercado) %> €</span>
                            </div>
                        </a>
                    <%
                            }
                        } else {
                    %>
                        <p style="color: white;">No hay cartas en la base de datos.</p>
                    <%
                        }
                    %>

                </section>
            </main>

        </div>
    </div>
    
    <!-- (El HTML del modal-filtros lo he omitido para no hacer el bloque gigante, puedes pegar aquí el <div id="modal-filtros">...</div> que tenías original) -->
    
</body>
</html>