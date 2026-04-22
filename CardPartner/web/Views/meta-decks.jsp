<%@page import="java.util.List"%>
<%@page import="modelo.Mazo"%>
<%@page import="modelo.Carta"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    // RECUPERAR LA LISTA ENVIADA POR EL SERVLET
    List<Mazo> listaMeta = (List<Mazo>) request.getAttribute("listaMazosComunidad");
    
    // ESCUDO: Redirección si se accede directamente
    if (listaMeta == null) {
        response.sendRedirect(request.getContextPath() + "/CatalogoMazosServlet?accion=listar&tipo_mazo=oficial");
        return; 
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="referrer" content="no-referrer">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;700&family=Poppins:wght@400;600&display=swap" rel="stylesheet">
    
    <title>Card Partner - META Decks</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/metaDecksStyle.css?v=8">
    
    <style>
        /* Forzamos que las imágenes del meta no se deformen */
        .mazo-imagen {
            width: 100%;
            aspect-ratio: 2 / 2.8; /* Proporción estándar de carta */
            overflow: hidden;
            background: rgba(0,0,0,0.1);
            border-radius: 8px;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        .mazo-imagen img {
            width: 100%;
            height: 100%;
            object-fit: contain; /* Evita estiramientos */
            display: block;
        }
    </style>
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
                            <form action="${pageContext.request.contextPath}/CatalogoMazosServlet" method="GET">
                                <input type="hidden" name="accion" value="listar">
                                <input type="hidden" name="tipo_mazo" value="oficial">
                                <input type="text" name="nombre" placeholder="Buscar líder o color...">
                                <button type="submit" class="btn-filtro" style="border:none; cursor:pointer;">Buscar</button>
                            </form>
                        </div>
                    </div>
                </div>

                <h3 class="subtitulo-mazos">Los mejores mazos del formato oficial</h3>
                
                <section class="galeria-mazos">
                    
                    <%
                        if (listaMeta != null && !listaMeta.isEmpty()) {
                            for(Mazo m : listaMeta) {
                                Carta lider = m.getCartaLider();
                                
                                // LÓGICA DE PROXY PARA IMÁGENES EXTERNAS
                                String imgRaw = (lider != null && lider.getImagen() != null) ? lider.getImagen().trim() : "";
                                String srcFinal = "";

                                if (!imgRaw.isEmpty() && imgRaw.toLowerCase().startsWith("http")) {
                                    srcFinal = "https://images.weserv.nl/?url=" + imgRaw;
                                } else {
                                    srcFinal = request.getContextPath() + (imgRaw.isEmpty() ? "/img/logo.png" : "/img/" + imgRaw);
                                }

                                String colorLider = (lider != null && lider.getColor() != null) ? lider.getColor() : "Desconocido";
                                String nombreLider = (lider != null) ? lider.getNombreCarta() : "Desconocido";
                    %>
                        <a href="${pageContext.request.contextPath}/CatalogoMazosServlet?accion=detalle&id=<%= m.getIdMazo() %>" class="mazo-item">
                            <div class="mazo-imagen">
                                <img src="<%= srcFinal %>" 
                                     alt="Líder Meta: <%= nombreLider %>"
                                     onerror="this.onerror=null; this.src='${pageContext.request.contextPath}/img/logo.png';">
                            </div>
                            <div class="mazo-info">
                                <h3><%= m.getNombreMazo() %></h3>
                                <p class="mazo-meta"><strong>Colores:</strong> <%= colorLider %></p>
                                <p class="mazo-meta"><strong>Líder:</strong> <%= nombreLider %></p>
                                <span class="mazo-tipo"><%= (m.getDescripcion() != null) ? m.getDescripcion() : "" %></span>
                            </div>
                        </a>
                    <%
                            }
                        } else {
                    %>
                        <p style="color: white; text-align: center; width: 100%; padding: 40px;">
                            Aún no hay mazos oficiales registrados en el meta actual.
                        </p>
                    <%
                        }
                    %>

                </section>

            </main>

        </div>
    </div>
</body>
</html>