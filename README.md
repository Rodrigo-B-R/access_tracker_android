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
├── model/         Modelos de datos (de ejemplo)
├── navigation/    NavHost, rutas e índice de pantallas
├── screens/
│   ├── comunes/       Login, contraseña, permisos, errores
│   ├── empleada/      Checar, asistencias, horario, avisos
│   ├── responsable/   Gestión de personal, horarios, avisos, bitácora
│   └── direccion/     Resumen, estadísticas, bitácora
└── ui/theme/      Colores, tipografía y tema
```
