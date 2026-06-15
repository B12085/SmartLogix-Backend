# Ejecución de Pruebas con JaCoCo

## Inicio Rápido

### Windows PowerShell:

```powershell
# 1. Navegar a cada microservicio y ejecutar pruebas
cd "c:\Users\basti\Desktop\Smartlogix\ms-transportistas"
.\mvnw.cmd clean test

cd "c:\Users\basti\Desktop\Smartlogix\ms-envios"
.\mvnw.cmd clean test

cd "c:\Users\basti\Desktop\Smartlogix\ms-pedidos"
.\mvnw.cmd clean test

cd "c:\Users\basti\Desktop\Smartlogix\ms-logistics-base"
.\mvnw.cmd clean test
```

### ejecutar el script automático:

```batch
cd c:\Users\basti\Desktop\Smartlogix
.\compile_all_with_jacoco.bat
```

---

## Reportes de Cobertura

Después de ejecutar las pruebas, abrir en navegador:

### Windows:
```powershell
# ms-transportistas
start "c:\Users\basti\Desktop\Smartlogix\ms-transportistas\target\site\jacoco\index.html"

# ms-envios
start "c:\Users\basti\Desktop\Smartlogix\ms-envios\target\site\jacoco\index.html"

# ms-pedidos
start "c:\Users\basti\Desktop\Smartlogix\ms-pedidos\target\site\jacoco\index.html"

# ms-logistics-base
start "c:\Users\basti\Desktop\Smartlogix\ms-logistics-base\target\site\jacoco\index.html"
```
