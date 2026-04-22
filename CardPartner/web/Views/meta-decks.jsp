<%@page import="java.util.List"%>
<%@page import="modelo.Mazo"%>
<%@page import="modelo.Carta"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    // ==========================================
    // PATRÓN MVC: RECIBIR DATOS DEL SERVLET
    // ==========================================
    // El Servlet usa la misma variable ("listaMazosComunidad") para enviar la lista
    List<Mazo> listaMeta = (List<Mazo>) request.getAttribute("listaMazosComunidad");
    
    // ESCUDO: Si entramos directo al JSP, redirigimos al Servlet indicando que queremos los oficiales
    if (listaMeta == null) {
        response.sendRedirect(request.getContextPath() + "/CatalogoMazosServlet?accion=listar&tipo_mazo=oficial");
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
    
    <title>Card Partner - META Decks</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/metaDecksStyle.css?v=6">
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
                        if (!listaMeta.isEmpty()) {
                            for(Mazo m : listaMeta) {
                                Carta lider = m.getCartaLider();
                                String imgLider = (lider != null && lider.getImagen() != null) ? lider.getImagen() : "/img/logo.png";
                                String colorLider = (lider != null && lider.getColor() != null) ? lider.getColor() : "Desconocido";
                    %>
                        <!-- MUY IMPORTANTE: Ahora el enlace viaja al Servlet pidiendo el "detalle" -->
                        <a href="${pageContext.request.contextPath}/CatalogoMazosServlet?accion=detalle&id=<%= m.getIdMazo() %>" class="mazo-item">
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
                        <p style="color: white; text-align: center; width: 100%;">Aún no hay mazos oficiales registrados en el meta actual.</p>
                    <%
                        }
                    %>

                </section>

            </main>

        </div>
    </div>
</body>
</html>