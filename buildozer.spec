[app]

title = Propiedades Termicas
package.name = propiedadestermicas
package.domain = org.wences

source.dir = .
source.include_exts = py,csv,kv,png,jpg,atlas
source.exclude_dirs = __pycache__,bin,build,.git
source.exclude_patterns = .kivy/*,C*UsersUSUARIO/*,.github/*

version = 0.1.0

requirements = python3,kivy

orientation = portrait
fullscreen = 0

android.permissions =
android.minapi = 23
android.api = 35
android.archs = arm64-v8a
android.accept_sdk_license = True

[buildozer]

log_level = 2
warn_on_root = 1
