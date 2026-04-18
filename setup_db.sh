#!/bin/bash

# Configuracion
DB_NAME="cardpartner_db"
DB_USER="admin_cardpartner"
DB_PASS="123456"

echo "--- Configurando Base de Datos para CardPartner ---"

# Comando SQL para crear todo
SQL_COMMANDS="CREATE DATABASE IF NOT EXISTS $DB_NAME;
CREATE USER IF NOT EXISTS '$DB_USER'@'localhost' IDENTIFIED BY '$DB_PASS';
GRANT ALL PRIVILEGES ON $DB_NAME.* TO '$DB_USER'@'localhost';
FLUSH PRIVILEGES;"

# Ejecutar como root (pedirá sudo para el socket)
echo "Introduce la contraseña de tu sistema (sudo) para acceder a MariaDB:"
sudo mysql -u root -e "$SQL_COMMANDS"

if [ $? -eq 0 ]; then
  echo "Usuario y Base de Datos creados con éxito."
else
  echo "Hubo un error al configurar la base de datos."
fi
