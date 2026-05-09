<%@page contentType="text/html" pageEncoding="UTF-8"%>

<header class="cabecera">
    <a href="${pageContext.request.contextPath}/index.jsp" class="logo">
        <img src="${pageContext.request.contextPath}/img/logo.png" alt="Logo de la Empresa">
    </a>

    <div class="fila-de-botones">
        <div class="mi-desplegable">
            <a href="${pageContext.request.contextPath}/Views/tcg.jsp" style="text-decoration: none;">
                <button class="mi-boton">TCGS</button>
            </a>
        </div>

        <div class="mi-desplegable">
            <button class="mi-boton">SOCIAL ▾</button>
            <div class="mi-submenu">
                <a href="${pageContext.request.contextPath}/Views/news.jsp">NEWS</a>
                <a href="${pageContext.request.contextPath}/Views/tournaments.jsp">TOURNAMENTS</a>
                <a href="${pageContext.request.contextPath}/Views/guides.jsp">GUIDES</a>
            </div>
        </div>

        <div class="mi-desplegable">
            <a href="#" style="text-decoration: none;">
                <button class="mi-boton">DECKS ▾</button>
            </a>
            <div class="mi-submenu">
                <a href="${pageContext.request.contextPath}/Views/community-deck.jsp">Community decks</a>
                <a href="${pageContext.request.contextPath}/Views/meta-decks.jsp">META Decks</a>
                <a href="${pageContext.request.contextPath}/Views/deck-builder.jsp">Creador de mazos</a>
            </div>
        </div>

        <a href="${pageContext.request.contextPath}/Views/cards.jsp" class="mi-boton">CARDS</a>
    </div>

    <div class="login">
        <% 
            // Preguntamos a la sesión si hay un usuario conectado
            modelo.Usuario usuario = (modelo.Usuario) session.getAttribute("usuarioActivo");
            
            if (usuario != null) { 
                // --- SI EL USUARIO ESTÁ LOGUEADO ---
        %>
            <div style="display: flex; gap: 15px; align-items: center;">
                
                <a href="${pageContext.request.contextPath}/Views/perfil.jsp" title="Ir al perfil de <%= usuario.getApodoUsuario() %>">
                    <img src="${pageContext.request.contextPath}/AvatarServlet?id=<%= usuario.getIdUsuario() %>" 
                         alt="Mi Avatar" 
                         style="width: 50px; height: 50px; border-radius: 50%; object-fit: cover; border: 2px solid #d32f2f; cursor: pointer; background-color: #fff; transition: transform 0.2s ease;"
                         onmouseover="this.style.transform='scale(1.1)'"
                         onmouseout="this.style.transform='scale(1)'">
                </a>

                <a href="${pageContext.request.contextPath}/LogoutServlet" style="text-decoration: none;">
                    <button style="background-color: #333; color: white;">CERRAR<br>SESIÓN</button>
                </a>
            </div>
        <% 
            } else { 
                // --- SI NO HAY NADIE LOGUEADO ---
        %>
            <a href="${pageContext.request.contextPath}/Views/login.jsp" style="text-decoration: none;">
                <button>SIGN IN<br>LOGIN</button>
            </a>
        <% 
            } 
        %>
    </div>
</header>