<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;700&family=Poppins:wght@400;600&display=swap" rel="stylesheet">
    
    <title>Card Partner - Creador de Mazos</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/deckBuilderStyle.css?v=4">
</head>
<body>

    <div class="fondo-rosa">
        <div class="contenedor-principal">
            
            <jsp:include page="/components/cabecera.jsp" />

            <main class="contenido-builder">
                <form action="${pageContext.request.contextPath}/GuardarMazoServlet" method="POST">
                
                    <div class="barra-superior-builder">
                        <h2 class="titulo-seccion">DECK BUILDER</h2>
                        <button type="submit" class="btn-guardar" style="border:none; cursor:pointer;">💾 Guardar Mazo</button>
                    </div>

                <div class="builder-header">
                    
                    <div class="lider-slot">
                        <h3>LÍDER SELECCIONADO</h3>
                        <div class="imagen-contenedor">
                            <img src="${pageContext.request.contextPath}/img/lider-sakazuki.jpg" alt="Líder Sakazuki">
                        </div>
                        <a href="#modalLideres" class="btn-cambiar" style="text-decoration: none; text-align: center; display: inline-block;">Cambiar Líder</a>
                    </div>

                    <div class="deck-detalles">
                            <h3>INFORMACIÓN DEL MAZO</h3>
                            <div class="campo-form">
                                <label>Nombre del Mazo:</label>
                                <input type="text" name="nombre_mazo" value="Control Sakazuki V2" required>
                            </div>
                            <div class="campo-form">
                                <label>Descripción / Estrategia:</label>
                                <textarea name="descripcion_mazo" rows="3">Mazo centrado en control de mesa...</textarea>
                            </div>
                            <div class="campo-form">
                                <label>Visibilidad:</label>
                                <select name="visibilidad_mazo" style="font-family: 'Poppins', sans-serif; padding: 8px; border-radius: 8px; border: 1px solid #ccc;">
                                    <option value="Público">Público (Visible en Community Decks)</option>
                                    <option value="Privado">Privado (Solo visible en mi Perfil)</option>
                                </select>
                            </div>
                        <div class="estadisticas-builder" style="margin-top: 15px;">
                            <span class="stat-badge">Cartas: 8 / 50</span>
                            <span class="stat-badge color-azul">Color: Azul</span>
                            <span class="stat-badge color-negro">Color: Negro</span>
                        </div>
                    </div>

                </div>

                <div class="builder-cartas-seccion">
                    
                    <div class="cabecera-cartas">
                        <h3>CARTAS AÑADIDAS</h3>
                        <a href="#modalCartas" class="btn-buscar-cartas" style="text-decoration: none; text-align: center; display: inline-block;">+ Buscar Cartas</a>
                    </div>

                    <div class="deck-grid">
                        <div class="carta-wrapper">
                            <img src="${pageContext.request.contextPath}/img/op01-120.jpg" alt="Shanks" class="carta-pequena">
                            <span class="cantidad-carta">x2</span>
                            <a href="#" class="btn-quitar" style="text-decoration: none;">x</a>
                        </div>
                        <div class="carta-wrapper">
                            <img src="${pageContext.request.contextPath}/img/op01-025.jpg" alt="Zoro" class="carta-pequena">
                            <span class="cantidad-carta">x4</span>
                            <a href="#" class="btn-quitar" style="text-decoration: none;">x</a>
                        </div>
                        <div class="carta-wrapper">
                            <img src="${pageContext.request.contextPath}/img/op05-119.jpg" alt="Luffy" class="carta-pequena">
                            <span class="cantidad-carta">x4</span>
                            <a href="#" class="btn-quitar" style="text-decoration: none;">x</a>
                        </div>
                        <div class="carta-wrapper">
                            <img src="${pageContext.request.contextPath}/img/op01-047.jpg" alt="Law" class="carta-pequena">
                            <span class="cantidad-carta">x4</span>
                            <a href="#" class="btn-quitar" style="text-decoration: none;">x</a>
                        </div>

                        <div class="slot-vacio"></div>
                        <div class="slot-vacio"></div>
                        <div class="slot-vacio"></div>
                        <div class="slot-vacio"></div>
                        <div class="slot-vacio"></div>
                        <div class="slot-vacio"></div>
                        <div class="slot-vacio"></div>
                        <div class="slot-vacio"></div>
                        <div class="slot-vacio"></div>
                    </div>
                </div>

           </form> </main>

        </div>
    </div>

    <div id="modalLideres" class="modal-overlay">
        <div class="modal-content">
            <a href="#" class="btn-cerrar">&times;</a>
            <h2 class="titulo-seccion" style="margin-bottom: 20px; font-size: 24px; text-align: left;">SELECCIONA TU LÍDER</h2>
            <div class="grid-seleccion">
                <a href="#"><img src="${pageContext.request.contextPath}/img/lider-sakazuki.jpg" alt="Sakazuki" class="img-lider-opcion"></a>
                <a href="#"><img src="${pageContext.request.contextPath}/img/lider-zoro.jpg" alt="Zoro" class="img-lider-opcion"></a>
                <a href="#"><img src="${pageContext.request.contextPath}/img/lider-enel.jpg" alt="Enel" class="img-lider-opcion"></a>
                <a href="#"><img src="${pageContext.request.contextPath}/img/lider-katakuri.jpg" alt="Katakuri" class="img-lider-opcion"></a>
            </div>
        </div>
    </div>

    <div id="modalCartas" class="modal-overlay">
        <div class="modal-content">
            <a href="#" class="btn-cerrar">&times;</a>
            <h2 class="titulo-seccion" style="margin-bottom: 10px; font-size: 24px; text-align: left;">AÑADIR CARTA</h2>
            
            <div style="display: flex; gap: 10px; margin-bottom: 20px;">
                <input type="text" placeholder="Buscar por nombre, ID, efecto..." style="flex: 1; padding: 10px; border-radius: 8px; border: 1px solid #ccc; font-family: 'Poppins', sans-serif;">
                <select style="padding: 10px; border-radius: 8px; border: 1px solid #ccc; font-family: 'Poppins', sans-serif;">
                    <option>Filtrar por Coste</option>
                    <option>Coste 1-3</option>
                    <option>Coste 4-6</option>
                    <option>Coste 7+</option>
                </select>
            </div>

            <div class="grid-seleccion">
                <a href="#"><img src="${pageContext.request.contextPath}/img/op01-120.jpg" alt="Shanks" class="img-carta-opcion"></a>
                <a href="#"><img src="${pageContext.request.contextPath}/img/op01-025.jpg" alt="Zoro" class="img-carta-opcion"></a>
                <a href="#"><img src="${pageContext.request.contextPath}/img/op05-119.jpg" alt="Luffy" class="img-carta-opcion"></a>
                <a href="#"><img src="${pageContext.request.contextPath}/img/op01-047.jpg" alt="Law" class="img-carta-opcion"></a>
                <a href="#"><img src="${pageContext.request.contextPath}/img/op01-016.jpg" alt="Nami" class="img-carta-opcion"></a>
            </div>
        </div>
    </div>

</body>
</html>