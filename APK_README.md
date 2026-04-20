# App Android de propiedades termicas

Esta version movil usa Kivy y conserva las tres tablas del programa original:

- Agua: `water.csv`
- Aire seco: `Air.csv`
- Vapor saturado: `Sat-steam.csv`

La app no depende de `pandas`, `numpy` ni `scipy` para Android. El archivo
`thermo_core.py` carga los CSV con la libreria estandar de Python y calcula las
propiedades con interpolacion cubica.

## Probar en PC

Instalar Kivy:

```powershell
python -m pip install -r requirements-mobile.txt
```

Ejecutar:

```powershell
python main.py
```

## Generar APK

Buildozer trabaja mejor en Linux o WSL. En Windows, usar Ubuntu en WSL.

Instalar dependencias base en Ubuntu:

```bash
sudo apt update
sudo apt install -y python3 python3-pip python3-venv git zip unzip openjdk-17-jdk \
  build-essential ccache autoconf automake libtool pkg-config zlib1g-dev \
  libncurses5-dev libncursesw5-dev libffi-dev libssl-dev cmake libarchive-tools

python3 -m venv ~/.venvs/buildozer
source ~/.venvs/buildozer/bin/activate
python -m pip install --upgrade pip setuptools wheel
python -m pip install buildozer "Cython<3"
```

Entrar a la carpeta del proyecto y compilar:

```bash
source ~/.venvs/buildozer/bin/activate
buildozer -v android debug
```

El APK generado queda en:

```text
bin/
```

## Generar APK con GitHub Actions

Esta es la ruta recomendada si WSL falla o si se quiere generar el APK en un
Linux limpio.

1. Subir el proyecto a un repositorio de GitHub.
2. Entrar al repositorio en GitHub.
3. Abrir la pestana `Actions`.
4. Seleccionar `Build Android APK`.
5. Presionar `Run workflow`.
6. Cuando termine, abrir la ejecucion y descargar el artefacto
   `propiedades-termicas-debug-apk`.

El workflow esta en:

```text
.github/workflows/build-apk.yml
```

Si la compilacion local falla con `pyjnius` y un error de `long`, significa que
se instalo Cython 3. La solucion es usar:

```bash
python -m pip install "Cython<3"
```

## Notas de uso

Cada tabla acepta solo temperaturas dentro del rango disponible en su CSV:

- Agua: 0.01 a 250 C
- Aire seco: 0 a 250 C
- Vapor saturado: 0.01 a 250 C

Si se cambian valores en los CSV, los resultados de la app cambian tambien.
