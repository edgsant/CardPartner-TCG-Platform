<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;700&family=Poppins:wght@400;600&display=swap" rel="stylesheet">
    
    <title>Card Partner - Creador de Mazos</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/deckBuilderStyle.css?v=7">
</head>
<body>

    <div class="fondo-rosa">
        <div class="contenedor-principal">
            
            <jsp:include page="/components/cabecera.jsp" />

            <main class="contenido-builder">
                <!-- El formulario envuelve toda la página y apunta al Servlet correcto -->
                <form action="${pageContext.request.contextPath}/GuardarMazoServlet" method="POST" id="form-guardar-mazo">
                
                    <!-- ========================================== -->
                    <!-- INPUTS OCULTOS PARA EL SERVLET             -->
                    <!-- ========================================== -->
                    <!-- Inician vacíos hasta que el usuario elija algo -->
                    <input type="hidden" name="lider_mazo" id="input_lider_mazo" value=""> 
                    <input type="hidden" name="lista_cartas" id="input_lista_cartas" value=""> 

                    <div class="barra-superior-builder">
                        <h2 class="titulo-seccion">DECK BUILDER</h2>
                        <button type="submit" class="btn-guardar" style="border:none; cursor:pointer;">💾 Guardar Mazo</button>
                    </div>

                    <div class="builder-header">
                        
                        <div class="lider-slot">
                            <h3>LÍDER SELECCIONADO</h3>
                            <!-- Contenedor con el logo como placeholder -->
                            <div class="imagen-contenedor" style="display: flex; align-items: center; justify-content: center; background-color: #2a2a2a; border-radius: 8px; min-height: 250px;">
                                <img src="${pageContext.request.contextPath}/img/logo.png" alt="Sin líder" id="img_lider_preview" style="max-height: 150px; opacity: 0.3; object-fit: contain;">
                            </div>
                            <a href="#modalLideres" class="btn-cambiar" style="text-decoration: none; text-align: center; display: inline-block;">Seleccionar Líder</a>
                        </div>

                        <div class="deck-detalles">
                                <h3>INFORMACIÓN DEL MAZO</h3>
                                <div class="campo-form">
                                    <label>Nombre del Mazo:</label>
                                    <input type="text" name="nombre_mazo" placeholder="Ej: Mi Mazo Invencible" required>
                                </div>
                                <div class="campo-form">
                                    <label>Descripción / Estrategia:</label>
                                    <textarea name="descripcion_mazo" rows="3" placeholder="Explica aquí la estrategia de tu mazo..."></textarea>
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
                            <h3>CARTAS AÑADIDAS</h3>
                            <a href="#modalCartas" class="btn-buscar-cartas" style="text-decoration: none; text-align: center; display: inline-block;">+ Añadir Cartas</a>
                        </div>

                        <!-- Cuadrícula vacía inicial -->
                        <div class="deck-grid" id="contenedor_cartas_mazo" style="min-height: 200px; display: flex; align-items: center; justify-content: center; border: 2px dashed #ccc; border-radius: 8px; margin-top: 15px;">
                            <p style="color: #888; font-style: italic;">Aún no has añadido ninguna carta a tu mazo.</p>
                        </div>
                    </div>

                </form> 
            </main>

        </div>
    </div>
</body>
</html>