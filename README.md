# Farmacia Desktop App (Java Swing)

Aplicación de prueba de escritorio para gestión de compras desarrollada con Java Swing.

## Descripción
Proyecto de práctica para explorar el desarrollo de aplicaciones desktop con Java Swing. 
Experimente con las diferentes conexiones a bases de datos.
Probé un poco containerización básica con Docker. 

## Características Principales
- Interfaz gráfica con componentes Swing mejorados (SwingX)
- Conexión a bases de datos MySQL/H2
- Pool de conexiones con HikariCP

## Tecnologías y Herramientas
- **Java 17**
- **Swing Framework**: Con windows builder en eclipse
- **SwingX 1.6.4**: Componentes UI avanzados
- **HikariCP 5.0.1**: Pool de conexiones 
- **MySQL Connector/J 9.1.0**: Driver para conexión a MySQL
- **H2 Database 2.3.232**: Base de datos embebida alternativa
- **IDE**: Eclipse IDE

## Estructura del Proyecto (con estilo MVC)
src/

├──main/java/nicodim/pharmacy/

│ ├── connections # Gestión de conexiones DB

│ ├── controllers # Lógica de controladores

│ ├── dao # Data Access Objects

│ ├── exceptions # Manejo personalizado de errores

│ ├── models # Entidades de datos

│ ├── services # Lógica de negocio

│ ├── utils # Utilidades comunes

│ ├── views # Componentes de interfaz gráfica

│ └── Main.java # Punto de entrada

└── resources/ # Configuraciones y assets


