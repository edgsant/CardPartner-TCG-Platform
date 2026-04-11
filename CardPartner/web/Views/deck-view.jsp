<%@page contentType="text/html" pageEncoding="UTF-8"%>
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
                <h2 class="titulo-mazo-grande">Sakazuki Control (OP-05 META)</h2>
                
                <div class="deck-header">
                    <div class="lider-seccion">
                        <img src="${pageContext.request.contextPath}/img/lider-sakazuki.jpg" alt="Líder Sakazuki">
                        <p><strong>Líder:</strong> Sakazuki (OP05-041)</p>
                    </div>

                    <div class="descripcion-seccion">
                        <h3>DESCRIPCIÓN DEL MAZO</h3>
                        <p>Mazo de control absoluto basado en la mecánica de reducir el coste de los personajes rivales para luego eliminarlos fácilmente con efectos de K.O. o enviándolos al fondo del mazo.</p>
                        <p><strong>Estilo:</strong> Control | <strong>Dificultad:</strong> Alta</p>
                        
                        <div class="estadisticas">
                            <span class="badge badge-azul">Azul</span>
                            <span class="badge badge-negro">Negro</span>
                            <span class="stat-normal">Winrate: 58%</span>
                            <span class="stat-normal">Precio Aprox: 210 €</span>
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
                    <div class="carta-wrapper">
                        <a href="${pageContext.request.contextPath}/Views/card-view.jsp"><img src="${pageContext.request.contextPath}/img/op01-120.jpg" alt="Shanks" class="carta-pequena"></a>
                        <span class="cantidad-carta">x2</span>
                    </div>
                    <div class="carta-wrapper">
                        <a href="${pageContext.request.contextPath}/Views/card-view.jsp"><img src="${pageContext.request.contextPath}/img/op01-025.jpg" alt="Zoro" class="carta-pequena"></a>
                        <span class="cantidad-carta">x4</span>
                    </div>
                    <div class="carta-wrapper">
                        <a href="${pageContext.request.contextPath}/Views/card-view.jsp"><img src="${pageContext.request.contextPath}/img/op05-119.jpg" alt="Luffy" class="carta-pequena"></a>
                        <span class="cantidad-carta">x4</span>
                    </div>
                    <div class="carta-wrapper">
                        <a href="${pageContext.request.contextPath}/Views/card-view.jsp"><img src="${pageContext.request.contextPath}/img/op01-047.jpg" alt="Law" class="carta-pequena"></a>
                        <span class="cantidad-carta">x4</span>
                    </div>
                    <div class="carta-wrapper">
                        <a href="${pageContext.request.contextPath}/Views/card-view.jsp"><img src="${pageContext.request.contextPath}/img/op01-016.jpg" alt="Nami" class="carta-pequena"></a>
                        <span class="cantidad-carta">x4</span>
                    </div>
                </div>
            </main>
        </div>
    </div>
</body>
</html>