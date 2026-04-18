@echo off
set DB_NAME=cardpartner_db
set DB_USER=admin_cardpartner
set DB_PASS=123456

echo --- Configurando Base de Datos para CardPartner ---

:: Intentamos ejecutar con el usuario root de MySQL
:: Si no tiene contraseña (XAMPP), funcionará directo.
mysql -u root -e "CREATE DATABASE IF NOT EXISTS %DB_NAME%; CREATE USER IF NOT EXISTS '%DB_USER%'@'localhost' IDENTIFIED BY '%DB_PASS%'; GRANT ALL PRIVILEGES ON %DB_NAME%.* TO '%DB_USER%'@'localhost'; FLUSH PRIVILEGES;"

if %errorlevel% equ 0 (
    echo  Usuario y Base de Datos creados con exito.
) else (
    echo  Error: Asegurate de que MySQL/XAMPP este encendido y 'mysql' este en tu PATH.
)
pause
