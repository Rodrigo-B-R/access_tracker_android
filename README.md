# Access Tracker — Sonrisas de Cristal

App Android (Kotlin + Jetpack Compose) para el control de asistencia del personal de las estancias. Este repositorio contiene el **prototipo de interfaz**: todas las pantallas y su navegación, sin backend.

> ⚠️ **El login y todos los datos son simulados.** No hay servidor, base de datos, autenticación real, GPS ni notificaciones reales. Los nombres, horarios, asistencias, avisos y estadísticas que se ven son datos de ejemplo fijos en el código.

## Requisitos

- Android Studio (versión reciente con soporte para AGP/Kotlin del proyecto)
- JDK 11 o superior (el Android Studio incluye uno)
- Dispositivo o emulador con **Android 11 (API 30) o superior**

## Cómo correr el proyecto

### Desde Android Studio
1. Clona el repositorio y ábrelo con **File > Open** seleccionando la carpeta `access_tracker`.
2. Espera a que termine la sincronización de Gradle.
3. Elige un emulador o conecta un dispositivo con depuración USB.
4. Presiona **Run ▶** (módulo `app`).

### Desde la terminal
```bash
# Windows
gradlew.bat installDebug

# macOS / Linux
./gradlew installDebug
```
Requiere un emulador iniciado o dispositivo conectado (`adb devices`).

## Login y usuarios de prueba

El login **no valida la contraseña** ni consulta ningún servicio: cualquier contraseña es aceptada. El **perfil** se decide únicamente por el texto de la **Clave**:

| Perfil | La clave debe contener | Ejemplo de clave | Contraseña |
|---|---|---|---|
| Responsable | `sofia`, `asist` o `resp` | `sofia.martinez` | cualquiera |
| Dirección | `dir` | `direccion` | cualquiera |
| Empleada/o | cualquier otra | `ana.torres` (valor por defecto) | cualquiera (por defecto `1234567890`) |

La pantalla de login ya viene prellenada con `ana.torres` / `1234567890` (perfil Empleada/o).

Flujo tras iniciar sesión: **Cambio de contraseña inicial → Permisos (ubicación y notificaciones) → pantalla principal del perfil**. Ambos pasos son solo visuales.

## Menú de pantallas (índice demo)

Para revisar **todas las pantallas** sin recorrer cada flujo, en la pantalla de inicio de sesión toca el enlace subrayado al final:

**"Ver índice de pantallas (demo)"**

Abre un índice agrupado por sección que lleva directo a cada pantalla:

- **Comunes:** inicio de sesión, cambio de contraseña, permisos, sesión expirada, aviso sin conexión, exportar.
- **Empleada/o:** checar (fuera / dentro / sin internet), mis asistencias, mi horario, avisos (bandeja y detalle).
- **Responsable:** inicio, asistencia del día, horarios y turnos, personal, registro por turno, ausencias/incidencias, alta y baja de personal, avisos, menú, salas y mínimos, bitácora.
- **Dirección:** resumen, estadísticas, bitácora por estancia.

El índice se define en `app/src/main/java/com/example/access_tracker/navigation/IndicePantallas.kt` y las rutas en `navigation/ScreenNames.kt` y `navigation/AppNavHost.kt`.

## Estructura

```
app/src/main/java/com/example/access_tracker/
├── components/    Componentes de UI reutilizables
├── data/          API, sesión (DataStore), cola offline y sincronización
│   └── local/         Room: tabla, DAO y base de datos
├── model/         Modelos de datos (de ejemplo)
├── navigation/    NavHost, rutas e índice de pantallas
├── screens/
│   ├── comunes/       Login, contraseña, permisos, errores
│   ├── empleada/      Checar, asistencias, horario, avisos
│   ├── responsable/   Gestión de personal, horarios, avisos, bitácora
│   └── direccion/     Resumen, estadísticas, bitácora
└── ui/theme/      Colores, tipografía y tema
```

## Almacenamiento local

La app guarda datos en el teléfono con dos librerías de Jetpack:

| Qué | Dónde | Archivo |
|---|---|---|
| Sesión (token JWT, rol, usuario, expiración) | **DataStore** | `data/SessionStore.kt` |
| Última copia del estado del turno (para verlo sin internet) | **DataStore** | `data/CacheEstado.kt` |
| Checadas hechas sin internet, pendientes de enviar | **Room** (`access_tracker.db`, tabla `cola_checadas`) | `data/ColaChecadas.kt`, `data/local/` |

### Checadas sin conexión (Room)

- Si una entrada o salida **no se puede enviar** (sin internet), se guarda en Room con estado `PENDIENTE` y la hora real del toque. Si se envía bien a la primera, no se guarda nada.
- Cuando vuelve la conexión, `Sincronizador` las envía **en orden**, aunque la app esté cerrada (WorkManager), y cambia su estado:
  - `ENVIADA`: llegó al servidor; no se vuelve a mandar.
  - `RECHAZADA`: la API la rechazó (por ejemplo, sin turno); se avisa a la persona una sola vez.
- Las `ENVIADA` (y las `RECHAZADA` ya avisadas) se **borran solas a los 7 días**. Las `PENDIENTE` nunca se borran.
- Cada checada lleva el `usuarioId`: nunca se envía con la sesión de otra persona.

### Migración desde SharedPreferences

Las versiones anteriores usaban SharedPreferences. Al abrir la app por primera vez con esta versión, los datos se pasan solos (sesión y copia del turno a DataStore, cola a Room) y se borran los archivos viejos. Nadie pierde su sesión ni sus checadas pendientes.

### Ver los datos guardados

- **Room:** *View > Tool Windows > App Inspection > Database Inspector* → `access_tracker.db` → `cola_checadas`.
- **DataStore:** *Device Explorer* → `/data/data/com.example.access_tracker/files/datastore/`.

Para probar el modo sin conexión en el emulador, activa el **modo avión dentro del emulador** 

Estos archivos están excluidos de los respaldos de Android (`res/xml/backup_rules.xml` y `data_extraction_rules.xml`), porque contienen el token y ubicaciones.
