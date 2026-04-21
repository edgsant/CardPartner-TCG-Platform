<%@page import="java.util.List"%>
<%@page import="modelo.Mazo"%>
<%@page import="modelo.Carta"%>
<%@page import="datos.MazoDAO"%>
<%@page import="datos.CartaDAO"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    // ==========================================
    // CONEXIÓN REAL A LA BD: MAZOS OFICIALES
    // ==========================================
    MazoDAO mazoDao = new MazoDAO();
    CartaDAO cartaDao = new CartaDAO();
    
    // Le pasamos "true" en el primer parámetro para que solo traiga los Oficiales
    List<Mazo> listaMeta = mazoDao.filtrarMazosPublicos(true, null, null, null, 20, 0);
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;700&family=Poppins:wght@400;600&display=swap" rel="stylesheet">
    
    <title>Card Partner - META Decks</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/metaDecksStyle.css?v=5">
</head>
<body>

    <div class="fondo-rosa">
        <div class="contenedor-principal">
            
            <jsp:include page="/components/cabecera.jsp" />

            <main class="contenido-mazos">
                
                <div class="barra-busqueda">
                    <h2 class="titulo-seccion">META DECKS</h2>
                    <div class="acciones-derecha">
                        <div class="filtros">
                            <input type="text" placeholder="Buscar líder o color...">
                            <a href="#modal-filtros" class="btn-filtro" style="text-decoration: none; display: inline-block;">Filtros ⚙️</a>
                        </div>
                    </div>
                </div>

                <h3 class="subtitulo-mazos">Los mejores mazos del formato oficial</h3>
                <section class="galeria-mazos">
                    
                    <%
                        if (listaMeta != null && !listaMeta.isEmpty()) {
                            for(Mazo m : listaMeta) {
                                Carta lider = cartaDao.obtenerCartaPorId(m.getIdCartaLider());
                                String imgLider = (lider != null && lider.getImagen() != null) ? lider.getImagen() : "/img/logo.png";
                                String colorLider = (lider != null && lider.getColor() != null) ? lider.getColor() : "Desconocido";
                    %>
                        <a href="${pageContext.request.contextPath}/Views/deck-view.jsp?id=<%= m.getIdMazo() %>" class="mazo-item">
                            <div class="mazo-imagen">
                                <img src="${pageContext.request.contextPath}<%= imgLider %>" alt="Líder Meta">
                            </div>
                            <div class="mazo-info">
                                <h3><%= m.getNombreMazo() %></h3>
                                <p class="mazo-meta">Colores: <%= colorLider %></p>
                                <span class="mazo-tipo"><%= m.getDescripcion() %></span>
                            </div>
                        </a>
                    <%
                            }
                        } else {
                    %>
                        <p style="color: white;">Aún no hay mazos oficiales registrados en el meta actual.</p>
                    <%
                        }
                    %>

                </section>

            </main>

        </div>
    </div>
</body>
</html>