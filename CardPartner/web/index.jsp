<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="modelo.Mazo"%>
<%@page import="modelo.Usuario"%>
<%@page import="modelo.Carta"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    // ==========================================
    // SIMULACIÓN (MOCK) - Mazos Recomendados
    // En el futuro: List<Mazo> recomendados = daoMazo.obtenerRecomendados();
    // ==========================================
    List<Mazo> mazosRecomendados = new ArrayList<>();
    
    // Creamos un usuario "Sistema" o "Admin"
    Usuario admin = new Usuario(0, "CardPartner Admin", "admin@cardpartner.com", "", null);
    
    // Creamos las cartas líderes de los Starter Decks (aprovechando tus imágenes estáticas)
    Carta liderTeach = new Carta("ST20-001", "Marshall D. Teach", "L", "Negro", "Líder", 5, 5000, "", "/img/st20.jpg");
    Carta liderUta = new Carta("ST11-001", "Uta", "L", "Verde", "Líder", 5, 5000, "", "/img/st11.jpg");
    Carta liderLuffy = new Carta("ST08-001", "Monkey D. Luffy", "L", "Negro", "Líder", 5, 5000, "", "/img/st08.jpg");

    // Construimos los mazos recomendados
    mazosRecomendados.add(new Mazo(201, "ST-20 Marshall D. Teach", "Ideal para aprender control.", "Público", admin, liderTeach));
    mazosRecomendados.add(new Mazo(202, "ST-11 UTA", "Mazo basado en la película Film Red.", "Público", admin, liderUta));
    mazosRecomendados.add(new Mazo(203, "ST-08 Luffy", "Mazo clásico para entender lo básico.", "Público", admin, liderLuffy));
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>CardPartner - Inicio</title>
    
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/indexStyle.css?v=11">
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@700&family=Poppins:wght@400;600&display=swap" rel="stylesheet">
</head>
<body>

    <div class="fondo-rosa">
        <div class="contenedor-principal">

            <jsp:include page="/components/cabecera.jsp" />

            <div class="contenido">
                
                <div class="noticias">
                    <button class="flecha">←</button>
                    <a href="https://optcgsim.com/" class="promo-link" target="_blank">
                        <img src="${pageContext.request.contextPath}/img/Simulador.jpg" alt="Banner Principal">
                    </a>
                    <button class="flecha">→</button>
                </div>

                <div class="columnas-inferiores">
                    
                    <div class="guias">
                        <a href="https://www.youtube.com/watch?v=TeEwfKKWmOY" class="enlace-guia" target="_blank">
                            <h2>GUÍAS</h2>
                            <div class="contenedor-carrusel-guia">
                                <span class="flecha-guia">←</span>
                                <img src="${pageContext.request.contextPath}/img/tutorial.jpg" alt="Imagen Guía">
                                <span class="flecha-guia">→</span>
                            </div>
                        </a>
                    </div>

                    <div class="mazos">
                        <h2 class="titulo-mazos">Recomendados: Principiantes</h2>
                        <div class="contenido-mazos">
                            <%
                                // BUCLE MÁGICO: Dibuja los mazos recomendados desde la lista Java
                                for(Mazo m : mazosRecomendados) {
                            %>
                                <a href="${pageContext.request.contextPath}/Views/deck-view.jsp?id=<%= m.getId() %>" class="item-mazo">
                                    <img src="${pageContext.request.contextPath}<%= m.getLider().getImagenUrl() %>" alt="<%= m.getNombre() %>">
                                    <span><%= m.getNombre() %></span>
                                </a>
                            <%
                                }
                            %>
                        </div>
                        <div class="footer-mazos">
                            <a href="${pageContext.request.contextPath}/Views/community-deck.jsp" class="btn-ver-mas">Ver todos los recomendados</a>
                        </div>
                    </div>

                </div>
            </div>
        </div>
    </div>

</body>
</html>