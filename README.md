# Aplicación de Administración Financiera

Esta aplicación está diseñada para la gestión de clientes, productos financieros y transacciones en una entidad financiera. Permite crear, actualizar y eliminar clientes y productos financieros, realizar transacciones entre cuentas y consultar estados de cuentas.

## 1. Requerimientos Funcionales

### Clientes
- **Crear Cliente**: Los clientes pueden ser creados con los atributos: id, tipo de identificación, número de identificación, nombres, apellido, correo electrónico, fecha de nacimiento, fecha de creación, fecha de modificación.
- **Modificar Cliente**: Se actualiza la información del cliente y se calcula automáticamente la fecha de modificación.
- **Eliminar Cliente**: Los clientes pueden ser eliminados, pero no si tienen productos vinculados.
- **Validaciones**:
  - No se permite la creación o existencia de un cliente menor de edad.
  - El campo correo electrónico debe tener el formato `xxxx@xxxxx.xxx`.
  - El nombre y el apellido deben tener al menos 2 caracteres.

### Productos (Cuentas)
- **Tipos de Producto**: Cuentas Corrientes y Cuentas de Ahorro.
- **Atributos**: id, tipo de cuenta, número de cuenta, estado (activa, inactiva, cancelada), saldo, exenta GMF, fecha de creación, fecha modificación, usuario.
- **Validaciones**:
  - Cuentas de ahorro no pueden tener saldo menor a $0.
  - Los números de cuenta deben ser únicos y generarse automáticamente: "53" para cuentas de ahorro y "33" para cuentas corrientes.
  - Cuentas de ahorro se crean como activas por defecto.
  - Solo se pueden cancelar cuentas con saldo $0.
- **Actualización**: El saldo de la cuenta se actualiza con cada transacción exitosa.

### Transacciones (Movimientos Financieros)
- **Tipos de Transacción**: Consignación, Retiro, Transferencia entre cuentas.
- **Actualización de Saldos**: Se actualizan los saldos y saldos disponibles con cada transacción realizada.
- **Transferencias**: Solo se pueden realizar entre cuentas existentes y deben actualizar los saldos de crédito y débito correspondientes.

## 2. Requerimientos No Funcionales

- **Backend**: Desarrollado en Java con Spring Boot.
- **Arquitectura**: Utiliza una arquitectura hexagonal o MVC.
- **Base de Datos**: Se ha utilizado MySQL para la persistencia de datos.
- **Estructura de Proyecto**: El proyecto se organiza en capas: `entity`, `service`, `controller`, `repository`.
- **Pruebas Unitarias**: Implementadas utilizando JUnit 5 y Mockito con cobertura para las capas `Service` y `Controller`.

## 3. Test Unitarios

Los test unitarios están implementados para verificar el funcionamiento correcto de los servicios y controladores. Los tests cubren los casos de creación, modificación, eliminación y validación de clientes, productos y transacciones.

## 4. Control de Versiones

El proyecto utiliza Git para el control de versiones y se encuentra en un repositorio en GitHub. Los avances del proyecto están evidenciados mediante commits y push.

## 5. Instrucciones de Ejecución

### Prerequisitos

- Java 17 o superior.
- MySQL Database.
- Maven para la gestión de dependencias y ejecución del proyecto.
- -xammp


