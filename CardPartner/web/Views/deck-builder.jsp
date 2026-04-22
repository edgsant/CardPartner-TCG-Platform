<%@page import="java.util.List"%>
<%@page import="modelo.Carta"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    // ==========================================
    // PATRÓN MVC: ESCUDO DE PROTECCIÓN
    // ==========================================
    List<Carta> listaLideres = (List<Carta>) request.getAttribute("listaLideres");
    
    if (listaLideres == null) {
        response.sendRedirect(request.getContextPath() + "/DeckBuilderServlet");
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
    
    <title>Card Partner - Creador de Mazos</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/deckBuilderStyle.css?v=9">
    <style>
        .grid-lideres {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
            gap: 15px;
            margin-top: 15px;
            max-height: 50vh;
            overflow-y: auto;
            padding-right: 10px;
        }
        .lider-opcion {
            cursor: pointer;
            transition: transform 0.2s;
            text-align: center;
        }
        .lider-opcion:hover {
            transform: scale(1.05);
        }
        .lider-opcion img {
            width: 100%;
            border-radius: 8px;
            background-color: #222;
        }
        .lider-opcion p {
            font-size: 12px;
            color: #333;
            margin-top: 5px;
            font-weight: bold;
        }
        
        /* Efecto hover para las cartas añadidas al mazo (para que el usuario sepa que puede borrarlas) */
        .carta-wrapper {
            transition: transform 0.2s;
            position: relative;
        }
        .carta-wrapper:hover {
            transform: scale(1.05);
            opacity: 0.8;
        }
        .carta-wrapper::after {
            content: "➖ Quitar";
            position: absolute;
            bottom: 5px;
            left: 50%;
            transform: translateX(-50%);
            background: rgba(200, 0, 0, 0.9);
            color: white;
            font-size: 10px;
            padding: 2px 5px;
            border-radius: 4px;
            opacity: 0;
            pointer-events: none;
            transition: opacity 0.2s;
        }
        .carta-wrapper:hover::after {
            opacity: 1;
        }
    </style>
</head>
<body>

    <div class="fondo-rosa">
        <div class="contenedor-principal">
            
            <jsp:include page="/components/cabecera.jsp" />

            <main class="contenido-builder">
                <!-- AÑADIDO: id "form-guardar-mazo" para interceptarlo antes de enviar -->
                <form action="${pageContext.request.contextPath}/GuardarMazoServlet" method="POST" id="form-guardar-mazo">
                
                    <input type="hidden" name="lider_mazo" id="input_lider_mazo" value=""> 
                    <input type="hidden" name="lista_cartas" id="input_lista_cartas" value=""> 

                    <div class="barra-superior-builder">
                        <h2 class="titulo-seccion">DECK BUILDER</h2>
                        <button type="submit" class="btn-guardar" style="border:none; cursor:pointer;">💾 Guardar Mazo</button>
                    </div>

                    <div class="builder-header">
                        
                        <div class="lider-slot">
                            <h3>LÍDER SELECCIONADO</h3>
                            <div class="imagen-contenedor" style="display: flex; align-items: center; justify-content: center; background-color: #2a2a2a; border-radius: 8px; min-height: 250px;">
                                <img src="${pageContext.request.contextPath}/img/logo.png" alt="Sin líder" id="img_lider_preview" style="max-height: 150px; opacity: 0.3; object-fit: contain;">
                            </div>
                            <a href="#modalLideres" class="btn-cambiar" style="text-decoration: none; text-align: center; display: inline-block;">Seleccionar Líder</a>
                        </div>

                        <div class="deck-detalles">
                                <h3>INFORMACIÓN DEL MAZO</h3>
                                <div class="campo-form">
                                    <label>Nombre del Mazo:</label>
                                    <input type="text" name="nombre_mazo" id="input_nombre_mazo" placeholder="Ej: Mi Mazo Invencible" required>
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
                            <h3>CARTAS AÑADIDAS <span id="contador_cartas">(0/50)</span></h3>
                            <a href="#modalCartas" onclick="verificarLider(event)" class="btn-buscar-cartas" style="text-decoration: none; text-align: center; display: inline-block;">+ Añadir Cartas</a>
                        </div>

                        <!-- El contenedor ahora tiene ID para que JS inyecte las cartas -->
                        <div class="deck-grid" id="contenedor_cartas_mazo" style="min-height: 200px; display: flex; align-items: center; justify-content: center; border: 2px dashed #ccc; border-radius: 8px; margin-top: 15px;">
                            <p style="color: #888; font-style: italic;">Aún no has añadido ninguna carta a tu mazo.</p>
                        </div>
                    </div>

                </form> 
            </main>

        </div>
    </div>

    <!-- ========================================== -->
    <!-- MODAL PARA ELEGIR LÍDER                    -->
    <!-- ========================================== -->
    <div id="modalLideres" class="modal-overlay">
        <div class="modal-content" style="max-width: 800px;">
            <a href="#" class="btn-cerrar">&times;</a>
            <h2 class="titulo-seccion" style="font-size: 24px; margin-bottom: 10px; text-align: left;">Catálogo de Líderes</h2>
            <p style="color: #666; margin-bottom: 20px;">Selecciona el líder para tu mazo. Esto definirá los colores de cartas que puedes utilizar.</p>
            
            <div class="grid-lideres">
                <%
                    for(Carta lider : listaLideres) {
                        String imgRaw = (lider.getImagen() != null) ? lider.getImagen().trim() : "";
                        String imgFinal = (!imgRaw.isEmpty() && imgRaw.toLowerCase().startsWith("http")) 
                                          ? "https://images.weserv.nl/?url=" + imgRaw 
                                          : request.getContextPath() + (imgRaw.isEmpty() ? "/img/logo.png" : imgRaw);
                        
                        String nombreSafe = lider.getNombreCarta().replace("'", "\\'");
                %>
                <div class="lider-opcion" onclick="aplicarLider('<%= lider.getIdCarta() %>', '<%= nombreSafe %>', '<%= imgFinal %>', '<%= lider.getColor() %>')">
                    <img src="<%= imgFinal %>" alt="<%= lider.getNombreCarta() %>" loading="lazy">
                    <p><%= lider.getNombreCarta() %></p>
                </div>
                <%
                    }
                %>
            </div>
        </div>
    </div>

    <!-- ========================================== -->
    <!-- MODAL PARA BUSCAR Y AÑADIR CARTAS          -->
    <!-- ========================================== -->
    <div id="modalCartas" class="modal-overlay">
        <div class="modal-content" style="max-width: 900px;">
            <a href="#" class="btn-cerrar">&times;</a>
            <h2 class="titulo-seccion" style="font-size: 24px; margin-bottom: 10px; text-align: left;">Catálogo de Cartas</h2>
            <p style="color: #666; margin-bottom: 20px;">
                Filtro activo para tu líder: <span id="span_colores_activos" style="font-weight:bold; color:#e83e8c;">Ninguno</span>
            </p>
            
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
    <!-- LÓGICA JAVASCRIPT DEL BUILDER              -->
    <!-- ========================================== -->
    <script>
        let coloresPermitidosMazo = "";
        
        // El estado interno del mazo. Estructura: { "OP01-001": { img: "url...", cantidad: 2 } }
        let mazoActual = {}; 
        let totalCartas = 0;

        // --- VALIDACIÓN AL GUARDAR ---
        document.getElementById("form-guardar-mazo").addEventListener("submit", function(event) {
            const liderSeleccionado = document.getElementById("input_lider_mazo").value;
            
            if (!liderSeleccionado) {
                event.preventDefault();
                alert("Debes seleccionar un Líder antes de guardar el mazo.");
                return;
            }
            
            if (totalCartas !== 50) {
                event.preventDefault(); // Detenemos el envío al Servlet
                alert("⚠️ ¡Cuidado! Las reglas oficiales exigen que tu mazo principal tenga exactamente 50 cartas. Actualmente tienes " + totalCartas + ".");
            }
        });

        // --- LÓGICA DEL LÍDER ---
        function verificarLider(event) {
            const inputLider = document.getElementById("input_lider_mazo").value;
            if (!inputLider) {
                event.preventDefault(); 
                alert("⚠️ ¡Alto ahí! Primero debes seleccionar un Líder. El líder definirá qué colores de cartas puedes añadir a tu mazo.");
            } else {
                document.getElementById("span_colores_activos").innerText = coloresPermitidosMazo;
                cargarCartas();
            }
        }

        function aplicarLider(idCarta, nombreCarta, urlImagen, coloresCarta) {
            
            // Si cambias de líder en mitad de la construcción, avisamos de que se borrará el mazo actual
            if (totalCartas > 0 && idCarta !== document.getElementById("input_lider_mazo").value) {
                if(!confirm("Si cambias de líder ahora, se vaciarán todas las cartas que ya habías añadido. ¿Estás seguro?")) {
                    return; // Si dice Cancelar, abortamos
                }
                // Si acepta, reiniciamos el mazo
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

        // --- LÓGICA DE BÚSQUEDA DE CARTAS ---
        function cargarCartas() {
            const contenedor = document.getElementById("grid_resultados_cartas");
            const textoBusqueda = document.getElementById("input_buscar_nombre").value;
            
            contenedor.innerHTML = '<p style="grid-column: 1 / -1; text-align: center; color: #888;">Cargando cartas...</p>';

            const url = "${pageContext.request.contextPath}/CartasApiServlet?colores=" + encodeURIComponent(coloresPermitidosMazo) + "&nombre=" + encodeURIComponent(textoBusqueda);

            fetch(url)
                .then(response => response.json())
                .then(cartas => {
                    contenedor.innerHTML = ""; 
                    
                    if(cartas.length === 0) {
                        contenedor.innerHTML = '<p style="grid-column: 1 / -1; text-align: center; color: #888;">No se encontraron cartas con esos filtros.</p>';
                        return;
                    }

                    cartas.forEach(carta => {
                        let imgRaw = carta.imagen ? carta.imagen.trim() : "";
                        let imgFinal = (imgRaw !== "" && imgRaw.toLowerCase().startsWith("http")) 
                                        ? "https://images.weserv.nl/?url=" + imgRaw 
                                        : "${pageContext.request.contextPath}" + (imgRaw === "" ? "/img/logo.png" : imgRaw);

                        contenedor.innerHTML += `
                            <div class="lider-opcion" onclick="añadirCartaAlMazo('\${carta.idCarta}', '\${imgFinal}')">
                                <img src="\${imgFinal}" alt="\${carta.nombreCarta}" loading="lazy">
                                <p>\${carta.nombreCarta}</p>
                            </div>
                        `;
                    });
                })
                .catch(error => {
                    console.error("Error al cargar cartas:", error);
                    contenedor.innerHTML = '<p style="grid-column: 1 / -1; text-align: center; color: #cc0000;">Error de conexión con la base de datos.</p>';
                });
        }

        // --- LÓGICA MATEMÁTICA DEL MAZO ---
        function añadirCartaAlMazo(idCarta, imgUrl) {
            // Regla: Máximo 50 cartas
            if (totalCartas >= 50) {
                alert("Has alcanzado el límite máximo de 50 cartas.");
                return;
            }

            // Si la carta ya existe, comprobamos el límite de 4 copias
            if (mazoActual[idCarta]) {
                if (mazoActual[idCarta].cantidad >= 4) {
                    alert("Solo puedes tener un máximo de 4 copias de la misma carta en tu mazo.");
                    return;
                }
                mazoActual[idCarta].cantidad++;
            } else {
                // Si es nueva, la registramos
                mazoActual[idCarta] = { img: imgUrl, cantidad: 1 };
            }

            totalCartas++;
            actualizarVistaMazo();
        }

        function quitarCartaDelMazo(idCarta) {
            if (mazoActual[idCarta]) {
                mazoActual[idCarta].cantidad--;
                totalCartas--;
                
                // Si nos quedamos a 0 copias, eliminamos el rastro de la carta
                if (mazoActual[idCarta].cantidad === 0) {
                    delete mazoActual[idCarta];
                }
                actualizarVistaMazo();
            }
        }

        // --- LÓGICA DE DIBUJO Y ENVÍO AL BACKEND ---
        function actualizarVistaMazo() {
            const contenedor = document.getElementById("contenedor_cartas_mazo");
            const contadorTxt = document.getElementById("contador_cartas");
            const inputListaOculta = document.getElementById("input_lista_cartas");

            // 1. Actualizamos el texto de contador visual
            contadorTxt.innerText = `(` + totalCartas + `/50)`;

            // 2. Si vaciamos el mazo entero
            if (totalCartas === 0) {
                contenedor.innerHTML = '<p style="color: #888; font-style: italic;">Aún no has añadido ninguna carta a tu mazo.</p>';
                contenedor.style.display = "flex";
                inputListaOculta.value = "";
                return;
            }

            // 3. Dibujamos las cartas usando el grid de CSS
            contenedor.style.display = "grid"; 
            contenedor.innerHTML = "";
            
            let arrayFormatoBackend = [];

            for (const [id, datos] of Object.entries(mazoActual)) {
                // Inyectamos la carta visual en el HTML (Con evento onclick para borrarla)
                contenedor.innerHTML += `
                    <div class="carta-wrapper" onclick="quitarCartaDelMazo('\${id}')" style="cursor:pointer;" title="Clic para quitar una copia de \${id}">
                        <img src="\${datos.img}" alt="\${id}" class="carta-pequena">
                        <span class="cantidad-carta">x\${datos.cantidad}</span>
                    </div>
                `;
                // Metemos el dato matemático en el array
                arrayFormatoBackend.push(`\${id}:\${datos.cantidad}`);
            }

            // 4. Transformamos el array en el string exacto que pidió tu compañero del Backend [OP01-001:4,ST01-002:2]
            inputListaOculta.value = arrayFormatoBackend.join(",");
        }
    </script>
</body>
</html>