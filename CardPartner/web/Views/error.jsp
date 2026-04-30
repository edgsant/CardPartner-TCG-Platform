<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head><title>Error</title></head>
<body>
    <h2>¡Ups! Algo ha salido mal.</h2>
    <p>${mensajeError}</p>
    <a href="${pageContext.request.contextPath}/CatalogoMazosServlet?accion=listar">Volver al inicio</a>
</body>
</html>