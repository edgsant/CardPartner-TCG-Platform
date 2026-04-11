<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;700&family=Poppins:wght@400;600&display=swap" rel="stylesheet">
    
    <title>Card Partner - TCGs</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/tcgStyle.css?v=5">
</head>
<body>

    <div class="fondo-rosa">
        <div class="contenedor-principal">
            
            <jsp:include page="/components/cabecera.jsp" />

            <main class="contenido-mazos">
                
                <div class="barra-busqueda">
                    <h2 class="titulo-seccion">SELECCIONA TU JUEGO (TCG)</h2>
                </div>

                <section class="galeria-mazos">
                    <a href="${pageContext.request.contextPath}/index.jsp" class="mazo-item">
                        <div class="mazo-imagen">
                            <img src="${pageContext.request.contextPath}/img/one-Piece-Logo.jpg" alt="Logo One Piece">
                        </div>
                        <div class="mazo-info">
                            <h3 style="text-align: center;">ONE PIECE TCG</h3>
                        </div>
                    </a>
                    
                    <a href="#" onclick="alert('El portal de Magic The Gathering estará disponible en futuras versiones.'); return false;" class="mazo-item">
                        <div class="mazo-imagen">
                            <img src="${pageContext.request.contextPath}/img/magic-The-Gathering-logo.jpg" alt="Logo Magic">
                        </div>
                        <div class="mazo-info">
                            <h3 style="text-align: center;">MAGIC THE GATHERING</h3>
                        </div>
                    </a>
                    
                    <a href="#" onclick="alert('El portal de Yu-Gi-Oh! estará disponible en futuras versiones.'); return false;" class="mazo-item">
                        <div class="mazo-imagen">
                            <img src="${pageContext.request.contextPath}/img/yu-Gi-Oh-logo.jpg" alt="Logo Yu-Gi-Oh">
                        </div>
                        <div class="mazo-info">
                            <h3 style="text-align: center;">YU-GI-OH!</h3>
                        </div>
                    </a>
                    
                </section>

            </main>

        </div>
    </div>

</body>
</html>