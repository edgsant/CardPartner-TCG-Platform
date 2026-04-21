<%@page import="java.util.List"%>
<%@page import="modelo.Mazo"%>
<%@page import="modelo.Carta"%>
<%@page import="datos.MazoDAO"%>
<%@page import="datos.CartaDAO"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    // ==========================================
    // CONEXIÓN REAL A LA BASE DE DATOS
    // ==========================================
    MazoDAO mazoDao = new MazoDAO();
    CartaDAO cartaDao = new CartaDAO();
    
    // Obtenemos los mazos públicos (limite de 20 para no saturar)
    List<Mazo> listaMazos = mazoDao.filtrarMazosPublicos(null, null, null, null, 20, 0);
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;700&family=Poppins:wght@400;600&display=swap" rel="stylesheet">
    
    <title>Card Partner - Community Decks</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/communityDeckStyle.css?v=5">
</head>
<body>

    <div class="fondo-rosa">
        <div class="contenedor-principal">
            
            <jsp:include page="/components/cabecera.jsp" />

            <main class="contenido-mazos">
                
                <div class="barra-busqueda">
                    <h2 class="titulo-seccion">COMMUNITY DECKS</h2>
                    <div class="acciones-derecha">
                        <div class="filtros">
                            <input type="text" placeholder="Buscar líder, color o creador...">
                            <a href="#modal-filtros" class="btn-filtro" style="text-decoration: none; display: inline-block;">Filtros ⚙️</a>
                        </div>
                        <a href="${pageContext.request.contextPath}/Views/deck-builder.jsp" class="btn-crear">+ Crear Mazo</a>
                    </div>
                </div>

                <h3 class="subtitulo-mazos">Mazos creados por los jugadores</h3>
                <section class="galeria-mazos">
                    
                    <%
                        if (listaMazos != null && !listaMazos.isEmpty()) {
                            for(Mazo m : listaMazos) {
                                // Buscamos al líder para sacar su foto y color
                                Carta lider = cartaDao.obtenerCartaPorId(m.getIdCartaLider());
                                String imgLider = (lider != null && lider.getImagen() != null) ? lider.getImagen() : "/img/logo.png";
                                String colorLider = (lider != null && lider.getColor() != null) ? lider.getColor() : "Desconocido";
                    %>
                        <a href="${pageContext.request.contextPath}/Views/deck-view.jsp?id=<%= m.getIdMazo() %>" class="mazo-item">
                            <div class="mazo-imagen">
                                <img src="${pageContext.request.contextPath}<%= imgLider %>" alt="Líder del mazo">
                            </div>
                            <div class="mazo-info">
                                <h3><%= m.getNombreMazo() %></h3>
                                <p class="mazo-meta">Colores: <%= colorLider %></p>
                                <p class="mazo-autor">ID Creador: <%= m.getIdUsuarioCreador() %></p>
                                <span class="mazo-tipo"><%= m.getDescripcion() %></span>
                            </div>
                        </a>
                    <%
                            }
                        } else {
                    %>
                        <p style="color: white;">Aún no hay mazos en la comunidad.</p>
                    <%
                        }
                    %>

                </section>

            </main>

        </div>
    </div>
</body>
</html>