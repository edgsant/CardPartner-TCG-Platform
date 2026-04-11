<%@page import="java.util.ArrayList"%>
<%@page import="modelo.Mazo"%>
<%@page import="modelo.Carta"%>
<%@page import="modelo.Usuario"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    // ==========================================
    // SIMULACIÓN (MOCK) - A borrar cuando haya BD
    // En el futuro, el Servlet te pasará el mazo así: 
    // Mazo mazoActual = (Mazo) request.getAttribute("mazoMostrado");
    // ==========================================
    
    Mazo mazoActual = new Mazo(1, "Sakazuki Control (Dinámico)", "Mazo de control simulado en Java sin usar Base de Datos.", "Público", null, null);
    
    // Le añadimos unas cuantas cartas de prueba a nuestro mazo falso
    mazoActual.añadirCarta(new Carta("OP01-120", "Shanks", "SEC", "Rojo", "Personaje", 9, 10000, "", "/img/op01-120.jpg"));
    mazoActual.añadirCarta(new Carta("OP01-025", "Zoro", "SR", "Rojo", "Personaje", 3, 5000, "", "/img/op01-025.jpg"));
    mazoActual.añadirCarta(new Carta("OP05-119", "Luffy", "SEC", "Morado", "Personaje", 5, 6000, "", "/img/op05-119.jpg"));
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;700&family=Poppins:wght@400;600&display=swap" rel="stylesheet">
    <title>Card Partner - Detalle Mazo</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/deckViewStyle.css?v=3">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>
    <div class="fondo-rosa">
        <div class="contenedor-principal">
            
            <jsp:include page="/components/cabecera.jsp" />

            <main class="contenido-detalle">
                <h2 class="titulo-mazo-grande"><%= mazoActual.getNombre() %></h2>
                
                <div class="deck-header">
                    <div class="lider-seccion">
                        <img src="${pageContext.request.contextPath}/img/lider-sakazuki.jpg" alt="Líder Sakazuki">
                        <p><strong>Líder:</strong> Sakazuki (OP05-041)</p>
                    </div>

                    <div class="descripcion-seccion">
                        <h3>DESCRIPCIÓN DEL MAZO</h3>
                        <p><%= mazoActual.getDescripcion() %></p>
                        <p><strong>Privacidad:</strong> <%= mazoActual.getVisibilidad() %> | <strong>Dificultad:</strong> Alta</p>
                        
                        <div class="estadisticas">
                            <span class="badge badge-azul">Azul</span>
                            <span class="badge badge-negro">Negro</span>
                            <span class="stat-normal">Cartas: <%= mazoActual.getListaCartas().size() %> / 50</span>
                        </div>

                        <div class="acciones-mazo">
                            <button class="btn-accion-mazo"><i class="fas fa-heart"></i> Guardar Mazo</button>
                            <a href="${pageContext.request.contextPath}/Views/deck-builder.jsp" class="btn-accion-mazo" style="text-decoration: none;"><i class="fas fa-edit"></i> Copiar / Editar</a>
                            <button class="btn-comprar-mazo"><i class="fas fa-shopping-cart"></i> Comprar Cartas</button>
                        </div>
                    </div>
                </div>

                <h3 class="titulo-cartas">LISTA DE CARTAS</h3>
                <div class="deck-grid">
                    <%
                        // BUCLE MÁGICO: Recorremos las cartas del mazo y dibujamos una caja por cada una
                        for(Carta c : mazoActual.getListaCartas()) {
                    %>
                        <div class="carta-wrapper">
                            <a href="${pageContext.request.contextPath}/Views/card-view.jsp?id=<%= c.getIdCarta() %>">
                                <img src="${pageContext.request.contextPath}<%= c.getImagenUrl() %>" alt="<%= c.getNombre() %>" class="carta-pequena" title="<%= c.getNombre() %>">
                            </a>
                            <span class="cantidad-carta">x1</span>
                        </div>
                    <%
                        }
                    %>
                </div>
            </main>
        </div>
    </div>
</body>
</html>