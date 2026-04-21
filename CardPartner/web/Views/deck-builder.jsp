<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;700&family=Poppins:wght@400;600&display=swap" rel="stylesheet">
    
    <title>Card Partner - Creador de Mazos</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/deckBuilderStyle.css?v=5">
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
                    </div>

                </div>

                <div class="builder-cartas-seccion">
                    
                    <div class="cabecera-cartas">
                        <h3>CARTAS AÑADIDAS (Visualización estática temporal)</h3>
                        <a href="#modalCartas" class="btn-buscar-cartas" style="text-decoration: none; text-align: center; display: inline-block;">+ Buscar Cartas</a>
                    </div>

                    <div class="deck-grid">
                        <div class="carta-wrapper">
                            <img src="${pageContext.request.contextPath}/img/op01-120.jpg" alt="Shanks" class="carta-pequena">
                            <span class="cantidad-carta">x2</span>
                        </div>
                        <div class="carta-wrapper">
                            <img src="${pageContext.request.contextPath}/img/op01-025.jpg" alt="Zoro" class="carta-pequena">
                            <span class="cantidad-carta">x4</span>
                        </div>

                        <div class="slot-vacio"></div>
                        <div class="slot-vacio"></div>
                    </div>
                </div>

           </form> </main>

        </div>
    </div>
</body>
</html>