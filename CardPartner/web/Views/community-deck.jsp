<%@page import="java.util.List"%>
<%@page import="modelo.Mazo"%>
<%@page import="modelo.Carta"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    // ==========================================
    // PATRÓN MVC: RECIBIR DATOS DEL SERVLET
    // ==========================================
    List<Mazo> listaMazos = (List<Mazo>) request.getAttribute("listaMazosComunidad");
    
    // ESCUDO: Si entramos directo al JSP, redirigimos al Servlet indicando que queremos los de la comunidad
    if (listaMazos == null) {
        response.sendRedirect(request.getContextPath() + "/CatalogoMazosServlet?accion=listar&tipo_mazo=comunidad");
        return; 
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;700&family=Poppins:wght@400;600&display=swap" rel="stylesheet">
    
    <title>Card Partner - Community Decks</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/communityDeckStyle.css?v=6">
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
                        if (!listaMazos.isEmpty()) {
                            for(Mazo m : listaMazos) {
                                // El Servlet ya nos incluye el objeto Carta líder dentro del Mazo
                                Carta lider = m.getCartaLider();
                                String imgLider = (lider != null && lider.getImagen() != null) ? lider.getImagen() : "/img/logo.png";
                                String colorLider = (lider != null && lider.getColor() != null) ? lider.getColor() : "Desconocido";
                    %>
                        <!-- MUY IMPORTANTE: Ahora el enlace viaja al Servlet pidiendo el "detalle" -->
                        <a href="${pageContext.request.contextPath}/CatalogoMazosServlet?accion=detalle&id=<%= m.getIdMazo() %>" class="mazo-item">
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
                        <p style="color: white; text-align: center; width: 100%;">Aún no hay mazos en la comunidad.</p>
                    <%
                        }
                    %>

                </section>

            </main>

        </div>
    </div>
</body>
</html>