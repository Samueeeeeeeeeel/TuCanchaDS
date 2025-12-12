# 📱 Guía de Integración Android - TuCancha Microservicios

## 📋 Índice
1. [Requisitos Previos](#requisitos-previos)
2. [Paso 1: Configurar Dependencias](#paso-1-configurar-dependencias)
3. [Paso 2: Configurar AndroidManifest](#paso-2-configurar-androidmanifest)
4. [Paso 3: Copiar Archivos al Proyecto](#paso-3-copiar-archivos-al-proyecto)
5. [Paso 4: Verificar Microservicios](#paso-4-verificar-microservicios)
6. [Paso 5: Ejemplos de Uso](#paso-5-ejemplos-de-uso)
7. [Solución de Problemas](#solución-de-problemas)

---

## Requisitos Previos

### En tu computadora:
- ✅ MySQL instalado y corriendo
- ✅ Las 4 bases de datos creadas (db_canchas, db_disponibilidad, db_login, db_reservas)
- ✅ Los 4 microservicios corriendo en sus puertos (8081, 8082, 8083, 8084)

### En Android Studio:
- ✅ Proyecto Android con Kotlin
- ✅ minSdk 26 o superior (para java.time)
- ✅ compileSdk 34 o superior

---

## Paso 1: Configurar Dependencias

Abre `app/build.gradle.kts` y agrega en el bloque `dependencies`:

```kotlin
dependencies {
    // ... tus otras dependencias ...
    
    // Retrofit - Cliente HTTP
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    
    // OkHttp con logging
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    
    // Gson
    implementation("com.google.code.gson:gson:2.10.1")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    
    // ViewModel y LiveData
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    
    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0")
}
```

Sincroniza el proyecto (Sync Now).

---

## Paso 2: Configurar AndroidManifest

Abre `app/src/main/AndroidManifest.xml` y agrega:

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <!-- PERMISOS DE INTERNET -->
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

    <application
        android:usesCleartextTraffic="true"
        ... resto de tu configuración ...
    >
        <!-- Tus activities -->
    </application>

</manifest>
```

⚠️ **IMPORTANTE**: `android:usesCleartextTraffic="true"` permite HTTP sin SSL (necesario para desarrollo local).

---

## Paso 3: Copiar Archivos al Proyecto

Copia las carpetas del proyecto `ANDROID_INTEGRATION` a tu proyecto Android:

```
tu_proyecto_android/
└── app/
    └── src/
        └── main/
            └── java/
                └── com/
                    └── tucancha/    <-- Ajusta al package de tu app
                        ├── data/
                        │   ├── models/
                        │   │   ├── Usuario.kt
                        │   │   ├── Cancha.kt
                        │   │   ├── Reserva.kt
                        │   │   └── Disponibilidad.kt
                        │   ├── network/
                        │   │   ├── ApiConfig.kt
                        │   │   ├── RetrofitClient.kt
                        │   │   ├── UsuariosApi.kt
                        │   │   ├── CanchasApi.kt
                        │   │   ├── ReservasApi.kt
                        │   │   └── DisponibilidadApi.kt
                        │   ├── repository/
                        │   │   ├── UsuarioRepository.kt
                        │   │   ├── CanchaRepository.kt
                        │   │   └── ReservaRepository.kt
                        │   └── session/
                        │       └── UserSessionManager.kt
                        └── ui/
                            └── viewmodel/
                                ├── LoginViewModel.kt
                                ├── CanchasViewModel.kt
                                └── ReservasViewModel.kt
```

**Después de copiar**, actualiza los `package` en cada archivo para que coincida con tu estructura.

---

## Paso 4: Verificar Microservicios

### 4.1 Iniciar los microservicios

En 4 terminales diferentes, navega a cada microservicio y ejecuta:

```bash
# Terminal 1 - Canchas
cd Canchas
./mvnw spring-boot:run

# Terminal 2 - Disponibilidad  
cd Disponibilidad
./mvnw spring-boot:run

# Terminal 3 - Login
cd Login
./mvnw spring-boot:run

# Terminal 4 - Reservas
cd Reservas
./mvnw spring-boot:run
```

### 4.2 Verificar que están corriendo

Abre en tu navegador:
- http://localhost:8081/swagger-ui.html (Canchas)
- http://localhost:8082/swagger-ui.html (Disponibilidad)
- http://localhost:8083/swagger-ui.html (Login)
- http://localhost:8084/swagger-ui.html (Reservas)

### 4.3 Configurar IP para dispositivo físico

Si usas un **dispositivo físico** en lugar del emulador:

1. Obtén tu IP local:
   - Windows: `ipconfig` → busca IPv4
   - Mac/Linux: `ifconfig` o `ip addr`

2. Edita `ApiConfig.kt`:
   ```kotlin
   private const val BASE_HOST_DEVICE = "192.168.1.XXX"  // Tu IP
   private const val CURRENT_HOST = BASE_HOST_DEVICE     // Cambia a esto
   ```

3. Asegúrate que tu celular y computadora estén en la misma red WiFi.

---

## Paso 5: Ejemplos de Uso

### 5.1 Login en una Activity

```kotlin
class LoginActivity : AppCompatActivity() {
    
    private lateinit var viewModel: LoginViewModel
    private lateinit var sessionManager: UserSessionManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        
        // Inicializar
        sessionManager = UserSessionManager(this)
        viewModel = LoginViewModel(UsuarioRepository(), sessionManager)
        
        // Observar estados
        viewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            btnLogin.isEnabled = !isLoading
        }
        
        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }
        
        viewModel.loginSuccess.observe(this) { success ->
            if (success) {
                // Ir a pantalla principal
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
        
        // Botón login
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            viewModel.login(email, password)
        }
    }
}
```

### 5.2 Registrar Usuario

```kotlin
btnRegistrar.setOnClickListener {
    viewModel.registrar(
        email = etEmail.text.toString(),
        password = etPassword.text.toString(),
        nombre = etNombre.text.toString(),
        apellido = etApellido.text.toString(),
        telefono = etTelefono.text.toString()
    )
}

viewModel.registroSuccess.observe(this) { success ->
    if (success) {
        Toast.makeText(this, "Registro exitoso! Ahora puedes iniciar sesión", Toast.LENGTH_LONG).show()
        // Ir a login
    }
}
```

### 5.3 Mostrar Lista de Canchas

```kotlin
class CanchasFragment : Fragment() {
    
    private lateinit var viewModel: CanchasViewModel
    private lateinit var adapter: CanchasAdapter
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel = CanchasViewModel(CanchaRepository())
        
        // Configurar RecyclerView
        adapter = CanchasAdapter { cancha ->
            // Click en una cancha
            viewModel.seleccionarCancha(cancha)
            // Navegar a detalle
        }
        recyclerView.adapter = adapter
        
        // Observar canchas
        viewModel.canchas.observe(viewLifecycleOwner) { canchas ->
            adapter.submitList(canchas)
        }
        
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        // Filtrar por tipo
        chipFutbol.setOnClickListener {
            viewModel.buscarPorTipo("Fútbol")
        }
        
        chipTenis.setOnClickListener {
            viewModel.buscarPorTipo("Tenis")
        }
    }
}
```

### 5.4 Crear una Reserva

```kotlin
class CrearReservaActivity : AppCompatActivity() {
    
    private lateinit var viewModel: ReservasViewModel
    private var canchaSeleccionada: Cancha? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val sessionManager = UserSessionManager(this)
        viewModel = ReservasViewModel(ReservaRepository(), sessionManager)
        
        // Obtener cancha del intent
        canchaSeleccionada = intent.getParcelableExtra("cancha")
        
        btnVerificar.setOnClickListener {
            val fechaInicio = obtenerFechaInicio() // Tu lógica para obtener fecha
            val fechaFin = obtenerFechaFin()
            
            viewModel.verificarDisponibilidad(
                canchaId = canchaSeleccionada!!.id!!,
                fechaInicio = fechaInicio,
                fechaFin = fechaFin
            )
        }
        
        viewModel.disponible.observe(this) { disponible ->
            disponible?.let {
                if (it) {
                    btnReservar.isEnabled = true
                    tvEstado.text = "✅ Disponible"
                } else {
                    btnReservar.isEnabled = false
                    tvEstado.text = "❌ No disponible"
                }
            }
        }
        
        btnReservar.setOnClickListener {
            val fechaInicio = obtenerFechaInicio()
            val fechaFin = obtenerFechaFin()
            val horas = calcularHoras(fechaInicio, fechaFin)
            val precioTotal = canchaSeleccionada!!.precioPorHora * horas
            
            viewModel.crearReserva(
                canchaId = canchaSeleccionada!!.id!!,
                fechaInicio = fechaInicio,
                fechaFin = fechaFin,
                precioTotal = precioTotal,
                observaciones = etObservaciones.text.toString()
            )
        }
        
        viewModel.successMessage.observe(this) { mensaje ->
            mensaje?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                viewModel.clearSuccessMessage()
                finish() // Volver atrás
            }
        }
    }
}
```

### 5.5 Ver Mis Reservas

```kotlin
class MisReservasFragment : Fragment() {
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val sessionManager = UserSessionManager(requireContext())
        val viewModel = ReservasViewModel(ReservaRepository(), sessionManager)
        
        // Cargar reservas al iniciar
        viewModel.cargarMisReservas()
        
        viewModel.misReservas.observe(viewLifecycleOwner) { reservas ->
            adapter.submitList(reservas)
            
            // Mostrar mensaje si no hay reservas
            tvEmpty.visibility = if (reservas.isEmpty()) View.VISIBLE else View.GONE
        }
        
        // Cancelar reserva con swipe o botón
        adapter.onCancelarClick = { reserva ->
            AlertDialog.Builder(requireContext())
                .setTitle("Cancelar Reserva")
                .setMessage("¿Estás seguro de cancelar esta reserva?")
                .setPositiveButton("Sí") { _, _ ->
                    viewModel.cancelarReserva(reserva.id!!, "Cancelado por el usuario")
                }
                .setNegativeButton("No", null)
                .show()
        }
    }
}
```

---

## Solución de Problemas

### ❌ Error: "Unable to resolve host"

**Causa**: No puede conectar con el servidor.

**Solución**:
1. Verifica que los microservicios estén corriendo
2. Si usas emulador: usa `10.0.2.2` como host
3. Si usas dispositivo físico: usa tu IP local y verifica que estén en la misma red
4. Verifica el firewall de Windows no bloquee los puertos

### ❌ Error: "Cleartext HTTP traffic not permitted"

**Solución**: Agrega `android:usesCleartextTraffic="true"` en AndroidManifest.xml

### ❌ Error: "NetworkOnMainThreadException"

**Causa**: Estás haciendo llamadas de red en el hilo principal.

**Solución**: Usa las funciones `suspend` dentro de `viewModelScope.launch { }` o coroutines.

### ❌ Error: "Credenciales inválidas"

**Solución**:
1. Verifica que el usuario existe en la base de datos
2. Verifica email y password correctos
3. Prueba primero en Swagger UI: http://localhost:8083/swagger-ui.html

### ❌ Las reservas se crean pero no aparecen

**Causa**: El usuarioId no se está enviando correctamente.

**Solución**: Verifica que el usuario esté logueado y que `UserSessionManager` tenga el ID guardado.

### ❌ Error de timeout

**Solución**: 
1. Aumenta los timeouts en `ApiConfig.kt`
2. Verifica la conexión de red
3. Verifica que MySQL no esté lento

---

## 🔥 Tips Importantes

1. **Siempre verifica disponibilidad** antes de crear una reserva
2. **Guarda la sesión** del usuario para no pedir login cada vez
3. **Maneja los errores** mostrando mensajes claros al usuario
4. **Usa el Logcat** con tag "OkHttp" para ver las llamadas HTTP
5. **Prueba primero en Swagger** antes de implementar en Android

---

## 📞 Puertos Rápidos

| Servicio | Puerto | URL Base (Emulador) |
|----------|--------|---------------------|
| Canchas | 8081 | http://10.0.2.2:8081/ |
| Disponibilidad | 8082 | http://10.0.2.2:8082/ |
| Login | 8083 | http://10.0.2.2:8083/ |
| Reservas | 8084 | http://10.0.2.2:8084/ |

---

¡Listo! Con esto tienes todo para conectar tu app Android con los microservicios. Los datos se guardarán directamente en MySQL a través de los microservicios Spring Boot. 🚀

