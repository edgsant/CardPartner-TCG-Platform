<%@page import="java.util.List"%>
<%@page import="modelo.Mazo"%>
<%@page import="modelo.Carta"%>
<%@page import="datos.MazoDAO"%>
<%@page import="datos.CartaDAO"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    String idParam = request.getParameter("id");
    Mazo mazoActual = null;
    Carta liderActual = null;
    
    // Como MazoDAO aún no tiene un "obtenerMazoPorId", usamos un pequeño truco: 
    // filtramos los públicos y buscamos el que coincida con el ID de la URL.
    if(idParam != null && !idParam.isEmpty()) {
        try {
            int idBusqueda = Integer.parseInt(idParam);
            MazoDAO mazoDao = new MazoDAO();
            List<Mazo> publicos = mazoDao.filtrarMazosPublicos(null, null, null, null, 100, 0);
            for(Mazo m : publicos) {
                if(m.getIdMazo() == idBusqueda) {
                    mazoActual = m;
                    break;
                }
            }
        } catch(Exception e) {}
    }

    // Control de errores si el mazo no existe
    if (mazoActual == null) {
        mazoActual = new Mazo();
        mazoActual.setIdMazo(0);
        mazoActual.setNombreMazo("Mazo no encontrado");
        mazoActual.setDescripcion("No se ha podido cargar este mazo de la base de datos.");
        mazoActual.setIdCartaLider("OP01-001");
    }
    
    CartaDAO cartaDao = new CartaDAO();
    liderActual = cartaDao.obtenerCartaPorId(mazoActual.getIdCartaLider());
    String imgLider = (liderActual != null && liderActual.getImagen() != null) ? liderActual.getImagen() : "/img/logo.png";
    String nombreLider = (liderActual != null) ? liderActual.getNombreCarta() : "Desconocido";
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;700&family=Poppins:wght@400;600&display=swap" rel="stylesheet">
    <title>Card Partner - Detalle Mazo</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/deckViewStyle.css?v=4">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>
    <div class="fondo-rosa">
        <div class="contenedor-principal">
            
            <jsp:include page="/components/cabecera.jsp" />

            <main class="contenido-detalle">
                <h2 class="titulo-mazo-grande"><%= mazoActual.getNombreMazo() %></h2>
                
                <div class="deck-header">
                    <div class="lider-seccion">
                        <img src="${pageContext.request.contextPath}<%= imgLider %>" alt="Líder">
                        <p><strong>Líder:</strong> <%= nombreLider %> (<%= mazoActual.getIdCartaLider() %>)</p>
                    </div>

                    <div class="descripcion-seccion">
                        <h3>DESCRIPCIÓN DEL MAZO</h3>
                        <p><%= mazoActual.getDescripcion() %></p>
                        <p><strong>Privacidad:</strong> <%= (mazoActual.isEsPublico()) ? "Público" : "Privado" %></p>
                        
                        <div class="acciones-mazo">
                            <button class="btn-accion-mazo"><i class="fas fa-heart"></i> Guardar Mazo</button>
                            <a href="${pageContext.request.contextPath}/Views/deck-builder.jsp" class="btn-accion-mazo" style="text-decoration: none;"><i class="fas fa-edit"></i> Copiar / Editar</a>
                        </div>
                    </div>
                </div>

                <h3 class="titulo-cartas">CARTAS (Pendiente de conexión BD)</h3>
                <div class="deck-grid">
                    <p style="color: white;">Tus compañeros del backend están preparando el DAO de "ContieneCarta". ¡Pronto podrás ver las cartas aquí!</p>
                </div>
            </main>
        </div>
    </div>
</body>
</html>