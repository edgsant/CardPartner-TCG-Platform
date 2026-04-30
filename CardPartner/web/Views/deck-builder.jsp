<%@page import="java.util.List"%>
<%@page import="modelo.Carta"%>
<%@page import="modelo.Mazo"%>
<%@page import="modelo.ContieneCarta"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    List<Carta> listaLideres = (List<Carta>) request.getAttribute("listaLideres");
    if (listaLideres == null) {
        response.sendRedirect(request.getContextPath() + "/DeckBuilderServlet");
        return; 
    }

    // ==========================================
    // LÓGICA PARA PRE-RELLENAR SI ESTAMOS EDITANDO
    // ==========================================
    Mazo mazoAEditar = (Mazo) request.getAttribute("mazoAEditar");
    List<ContieneCarta> cartasAEditar = (List<ContieneCarta>) request.getAttribute("cartasAEditar");
    
    String formNombre = "";
    String formDesc = "";
    String formVisibilidad = "Público";
    String formLiderId = "";
    String formLiderImg = request.getContextPath() + "/img/logo.png";
    String formLiderColor = "";
    String formIdMazo = ""; // Vacío significa "Mazo Nuevo"
    String opacidadLogo = "0.3";

    if(mazoAEditar != null) {
        formNombre = mazoAEditar.getNombreMazo();
        formDesc = mazoAEditar.getDescripcion() != null ? mazoAEditar.getDescripcion() : "";
        formVisibilidad = mazoAEditar.isEsPublico() ? "Público" : "Privado";
        formIdMazo = String.valueOf(mazoAEditar.getIdMazo());
        formLiderId = mazoAEditar.getIdCartaLider();
        
        if(mazoAEditar.getCartaLider() != null) {
            formLiderColor = mazoAEditar.getCartaLider().getColor();
            String raw = mazoAEditar.getCartaLider().getImagen();
            if(raw != null && raw.toLowerCase().startsWith("http")) {
                formLiderImg = "https://images.weserv.nl/?url=" + raw;
            } else {
                formLiderImg = request.getContextPath() + raw;
            }
            opacidadLogo = "1"; // Como ya hay líder, la foto se ve bien
        }
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;700&family=Poppins:wght@400;600&display=swap" rel="stylesheet">
    <title>Card Partner - Creador de Mazos</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/deckBuilderStyle.css?v=10">
    <style>
        .grid-lideres { display: grid; grid-template-columns: repeat(auto-fill, minmax(120px, 1fr)); gap: 15px; margin-top: 15px; max-height: 50vh; overflow-y: auto; padding-right: 10px; }
        .lider-opcion { cursor: pointer; transition: transform 0.2s; text-align: center; }
        .lider-opcion:hover { transform: scale(1.05); }
        .lider-opcion img { width: 100%; border-radius: 8px; background-color: #222; }
        .lider-opcion p { font-size: 12px; color: #333; margin-top: 5px; font-weight: bold; }
        
        .carta-wrapper { transition: transform 0.2s; position: relative; cursor: pointer; }
        .carta-wrapper:hover { transform: scale(1.05); opacity: 0.8; }
        .carta-wrapper::after { content: "➖ Quitar"; position: absolute; bottom: 5px; left: 50%; transform: translateX(-50%); background: rgba(200, 0, 0, 0.9); color: white; font-size: 10px; padding: 2px 5px; border-radius: 4px; opacity: 0; pointer-events: none; transition: opacity 0.2s; }
        .carta-wrapper:hover::after { opacity: 1; }
    </style>
</head>
<body>

    <div class="fondo-rosa">
        <div class="contenedor-principal">
            
            <jsp:include page="/components/cabecera.jsp" />

            <main class="contenido-builder">
                <form action="${pageContext.request.contextPath}/GuardarMazoServlet" method="POST" id="form-guardar-mazo">
                    
                    <input type="hidden" name="id_mazo" value="${mazoAEditar != null ? mazoAEditar.idMazo : '0'}">
                
                    
                    <input type="hidden" name="lider_mazo" id="input_lider_mazo" value="<%= formLiderId %>"> 
                    <input type="hidden" name="lista_cartas" id="input_lista_cartas" value=""> 

                    <div class="barra-superior-builder">
                        <h2 class="titulo-seccion"><%= (mazoAEditar != null) ? "EDITANDO MAZO" : "DECK BUILDER" %></h2>
                        <button type="submit" class="btn-guardar" style="border:none; cursor:pointer;">💾 <%= (mazoAEditar != null) ? "Actualizar Mazo" : "Guardar Mazo" %></button>
                    </div>

                    <div class="builder-header">
                        <div class="lider-slot">
                            <h3>LÍDER SELECCIONADO</h3>
                            <div class="imagen-contenedor" style="display: flex; align-items: center; justify-content: center; background-color: #2a2a2a; border-radius: 8px; min-height: 250px;">
                                <img src="<%= formLiderImg %>" alt="Líder" id="img_lider_preview" style="max-height: <%= (mazoAEditar != null) ? "100%" : "150px" %>; opacity: <%= opacidadLogo %>; object-fit: contain;">
                            </div>
                            <a href="#modalLideres" class="btn-cambiar" style="text-decoration: none; text-align: center; display: inline-block;">Cambiar Líder</a>
                        </div>

                        <div class="deck-detalles">
                                <h3>INFORMACIÓN DEL MAZO</h3>
                                <div class="campo-form">
                                    <label>Nombre del Mazo:</label>
                                    <input type="text" name="nombre_mazo" id="input_nombre_mazo" value="<%= formNombre %>" placeholder="Ej: Mi Mazo Invencible" required>
                                </div>
                                <div class="campo-form">
                                    <label>Descripción / Estrategia:</label>
                                    <textarea name="descripcion_mazo" rows="3" placeholder="Explica aquí la estrategia de tu mazo..."><%= formDesc %></textarea>
                                </div>
                                <div class="campo-form">
                                    <label>Visibilidad:</label>
                                    <select name="visibilidad_mazo" style="font-family: 'Poppins', sans-serif; padding: 8px; border-radius: 8px; border: 1px solid #ccc;">
                                        <option value="Público" <%= formVisibilidad.equals("Público") ? "selected" : "" %>>Público (Visible en Community Decks)</option>
                                        <option value="Privado" <%= formVisibilidad.equals("Privado") ? "selected" : "" %>>Privado (Solo visible en mi Perfil)</option>
                                    </select>
                                </div>
                        </div>
                    </div>

                    <div class="builder-cartas-seccion">
                        <div class="cabecera-cartas">
                            <h3>CARTAS AÑADIDAS <span id="contador_cartas">(0/50)</span></h3>
                            <a href="#modalCartas" onclick="verificarLider(event)" class="btn-buscar-cartas" style="text-decoration: none; text-align: center; display: inline-block;">+ Añadir Cartas</a>
                        </div>

                        <div class="deck-grid" id="contenedor_cartas_mazo" style="min-height: 200px; display: flex; align-items: center; justify-content: center; border: 2px dashed #ccc; border-radius: 8px; margin-top: 15px;">
                            <p style="color: #888; font-style: italic;">Aún no has añadido ninguna carta a tu mazo.</p>
                        </div>
                    </div>
                </form> 
            </main>
        </div>
    </div>

    <!-- Modales (Líderes y Cartas) idénticos al código anterior -->
    <div id="modalLideres" class="modal-overlay">
        <div class="modal-content" style="max-width: 800px;">
            <a href="#" class="btn-cerrar">&times;</a>
            <h2 class="titulo-seccion" style="font-size: 24px; margin-bottom: 10px; text-align: left;">Catálogo de Líderes</h2>
            <div class="grid-lideres">
                <%
                    for(Carta lider : listaLideres) {
                        String raw = (lider.getImagen() != null) ? lider.getImagen().trim() : "";
                        String imgFinal = (!raw.isEmpty() && raw.toLowerCase().startsWith("http")) ? "https://images.weserv.nl/?url=" + raw : request.getContextPath() + (raw.isEmpty() ? "/img/logo.png" : raw);
                        String nombreSafe = lider.getNombreCarta().replace("'", "\\'");
                %>
                <div class="lider-opcion" onclick="aplicarLider('<%= lider.getIdCarta() %>', '<%= nombreSafe %>', '<%= imgFinal %>', '<%= lider.getColor() %>')">
                    <img src="<%= imgFinal %>" alt="<%= lider.getNombreCarta() %>" loading="lazy">
                    <p><%= lider.getNombreCarta() %></p>
                </div>
                <% } %>
            </div>
        </div>
    </div>

    <div id="modalCartas" class="modal-overlay">
        <div class="modal-content" style="max-width: 900px;">
            <a href="#" class="btn-cerrar">&times;</a>
            <h2 class="titulo-seccion" style="font-size: 24px; margin-bottom: 10px; text-align: left;">Catálogo de Cartas</h2>
            <p style="color: #666; margin-bottom: 20px;">Filtro activo para tu líder: <span id="span_colores_activos" style="font-weight:bold; color:#e83e8c;">Ninguno</span></p>
            <div style="display: flex; gap: 10px; margin-bottom: 20px;">
                <input type="text" id="input_buscar_nombre" placeholder="Buscar por nombre..." style="flex: 1; padding: 10px; border-radius: 8px; border: 1px solid #ccc; font-family: 'Poppins', sans-serif;">
                <button type="button" onclick="cargarCartas()" class="btn-filtro" style="cursor:pointer; border:none; padding: 10px 20px;">Buscar</button>
            </div>
            <div class="grid-lideres" id="grid_resultados_cartas" style="min-height: 200px;">
                <p style="grid-column: 1 / -1; text-align: center; color: #888;">Buscando cartas en la base de datos...</p>
            </div>
        </div>
    </div>

    <!-- ========================================== -->
    <!-- JAVASCRIPT DEL BUILDER                     -->
    <!-- ========================================== -->
    <script>
        let coloresPermitidosMazo = "<%= formLiderColor %>";
        let mazoActual = {}; 
        let totalCartas = 0;

        // Si estamos editando, inyectamos las cartas que traemos de la BD
        <% if (mazoAEditar != null && cartasAEditar != null) { %>
            <% for (ContieneCarta cc : cartasAEditar) { 
                String rawImg = cc.getCarta().getImagen();
                String jsImg = (rawImg != null && rawImg.toLowerCase().startsWith("http")) ? "https://images.weserv.nl/?url=" + rawImg : request.getContextPath() + rawImg;
            %>
                mazoActual["<%= cc.getIdCarta() %>"] = { img: "<%= jsImg %>", cantidad: <%= cc.getCantidad() %> };
                totalCartas += <%= cc.getCantidad() %>;
            <% } %>
            
            // Dibujamos el mazo al cargar la página
            window.onload = function() {
                actualizarVistaMazo();
            };
        <% } %>

        // --- VALIDACIÓN AL GUARDAR ---
        document.getElementById("form-guardar-mazo").addEventListener("submit", function(event) {
            const liderSeleccionado = document.getElementById("input_lider_mazo").value;
            if (!liderSeleccionado) {
                event.preventDefault();
                alert("Debes seleccionar un Líder antes de guardar el mazo.");
                return;
            }
            if (totalCartas !== 50) {
                event.preventDefault(); 
                alert("⚠️ ¡Cuidado! Las reglas oficiales exigen que tu mazo principal tenga exactamente 50 cartas. Actualmente tienes " + totalCartas + ".");
            }
        });

        // --- LÓGICAS (Idénticas al anterior) ---
        function verificarLider(event) {
            const inputLider = document.getElementById("input_lider_mazo").value;
            if (!inputLider) {
                event.preventDefault(); 
                alert("⚠️ ¡Alto ahí! Primero debes seleccionar un Líder.");
            } else {
                document.getElementById("span_colores_activos").innerText = coloresPermitidosMazo;
                cargarCartas();
            }
        }

        function aplicarLider(idCarta, nombreCarta, urlImagen, coloresCarta) {
            if (totalCartas > 0 && idCarta !== document.getElementById("input_lider_mazo").value) {
                if(!confirm("Si cambias de líder ahora, se vaciarán todas las cartas que ya habías añadido. ¿Estás seguro?")) {
                    return; 
                }
                mazoActual = {};
                totalCartas = 0;
                actualizarVistaMazo();
            }
            document.getElementById("input_lider_mazo").value = idCarta;
            const preview = document.getElementById("img_lider_preview");
            preview.src = urlImagen;
            preview.style.opacity = "1";
            preview.style.maxHeight = "100%";
            const inputNombre = document.getElementById("input_nombre_mazo");
            if(inputNombre.value === "") {
                inputNombre.value = "Mazo de " + nombreCarta;
            }
            coloresPermitidosMazo = coloresCarta;
            window.location.hash = "#";
        }

        function cargarCartas() {
            const contenedor = document.getElementById("grid_resultados_cartas");
            const textoBusqueda = document.getElementById("input_buscar_nombre").value;
            contenedor.innerHTML = '<p style="grid-column: 1 / -1; text-align: center; color: #888;">Cargando cartas...</p>';
            const url = "${pageContext.request.contextPath}/CartasApiServlet?colores=" + encodeURIComponent(coloresPermitidosMazo) + "&nombre=" + encodeURIComponent(textoBusqueda);
            fetch(url).then(response => response.json()).then(cartas => {
                contenedor.innerHTML = ""; 
                if(cartas.length === 0) {
                    contenedor.innerHTML = '<p style="grid-column: 1 / -1; text-align: center; color: #888;">No se encontraron cartas.</p>';
                    return;
                }
                cartas.forEach(carta => {
                    let imgRaw = carta.imagen ? carta.imagen.trim() : "";
                    let imgFinal = (imgRaw !== "" && imgRaw.toLowerCase().startsWith("http")) ? "https://images.weserv.nl/?url=" + imgRaw : "${pageContext.request.contextPath}" + (imgRaw === "" ? "/img/logo.png" : imgRaw);
                    contenedor.innerHTML += `<div class="lider-opcion" onclick="añadirCartaAlMazo('\${carta.idCarta}', '\${imgFinal}')"><img src="\${imgFinal}" alt="\${carta.nombreCarta}" loading="lazy"><p>\${carta.nombreCarta}</p></div>`;
                });
            }).catch(error => {
                contenedor.innerHTML = '<p style="grid-column: 1 / -1; text-align: center; color: #cc0000;">Error de conexión.</p>';
            });
        }

        function añadirCartaAlMazo(idCarta, imgUrl) {
            if (totalCartas >= 50) { alert("Límite de 50 cartas alcanzado."); return; }
            if (mazoActual[idCarta]) {
                if (mazoActual[idCarta].cantidad >= 4) { alert("Máximo 4 copias de la misma carta."); return; }
                mazoActual[idCarta].cantidad++;
            } else { mazoActual[idCarta] = { img: imgUrl, cantidad: 1 }; }
            totalCartas++;
            actualizarVistaMazo();
        }

        function quitarCartaDelMazo(idCarta) {
            if (mazoActual[idCarta]) {
                mazoActual[idCarta].cantidad--;
                totalCartas--;
                if (mazoActual[idCarta].cantidad === 0) { delete mazoActual[idCarta]; }
                actualizarVistaMazo();
            }
        }

        function actualizarVistaMazo() {
            const contenedor = document.getElementById("contenedor_cartas_mazo");
            const contadorTxt = document.getElementById("contador_cartas");
            const inputListaOculta = document.getElementById("input_lista_cartas");
            contadorTxt.innerText = `(` + totalCartas + `/50)`;

            if (totalCartas === 0) {
                contenedor.innerHTML = '<p style="color: #888; font-style: italic;">Aún no has añadido ninguna carta a tu mazo.</p>';
                contenedor.style.display = "flex";
                inputListaOculta.value = "";
                return;
            }
            contenedor.style.display = "grid"; 
            contenedor.innerHTML = "";
            let arrayFormatoBackend = [];
            for (const [id, datos] of Object.entries(mazoActual)) {
                contenedor.innerHTML += `<div class="carta-wrapper" onclick="quitarCartaDelMazo('\${id}')" title="Clic para quitar"><img src="\${datos.img}" alt="\${id}" class="carta-pequena"><span class="cantidad-carta">x\${datos.cantidad}</span></div>`;
                arrayFormatoBackend.push(`\${id}:\${datos.cantidad}`);
            }
            inputListaOculta.value = arrayFormatoBackend.join(",");
        }
    </script>
</body>
</html>