from __future__ import annotations

import csv
from bisect import bisect_right
from dataclasses import dataclass
from pathlib import Path
from typing import Dict, Iterable, List, Sequence


@dataclass(frozen=True)
class PropertySpec:
    column: str
    label: str
    unit: str = ""


@dataclass(frozen=True)
class TableSpec:
    key: str
    title: str
    input_label: str
    csv_file: str
    properties: Sequence[PropertySpec]


TABLES: Sequence[TableSpec] = (
    TableSpec(
        key="water",
        title="Agua",
        input_label="Temperatura del agua (C)",
        csv_file="water.csv",
        properties=(
            PropertySpec("y1", "Presion de saturacion", "kPa"),
            PropertySpec("y2", "Coeficiente de expansion volumetrica", "K-1"),
            PropertySpec("y3", "Densidad", "kg/m3"),
            PropertySpec("y4", "Calor especifico", "kJ/kg K"),
            PropertySpec("y5", "Conductividad termica", "W/m K"),
            PropertySpec("y6", "Difusividad termica", "x 10-6 m2/s"),
            PropertySpec("y7", "Viscosidad absoluta", "x 10-6 Pa s"),
            PropertySpec("y8", "Viscosidad cinematica", "x 10-6 m2/s"),
            PropertySpec("y9", "Numero de Prandtl"),
        ),
    ),
    TableSpec(
        key="air",
        title="Aire seco",
        input_label="Temperatura del aire (C)",
        csv_file="Air.csv",
        properties=(
            PropertySpec("y1", "Coeficiente de expansion volumetrica", "K-1"),
            PropertySpec("y2", "Densidad", "kg/m3"),
            PropertySpec("y3", "Calor especifico", "kJ/kg K"),
            PropertySpec("y4", "Conductividad termica", "W/m K"),
            PropertySpec("y5", "Difusividad termica", "x 10-6 m2/s"),
            PropertySpec("y6", "Viscosidad absoluta", "x 10-6 Pa s"),
            PropertySpec("y7", "Viscosidad cinematica", "x 10-6 m2/s"),
            PropertySpec("y8", "Numero de Prandtl"),
        ),
    ),
    TableSpec(
        key="steam",
        title="Vapor saturado",
        input_label="Temperatura del vapor (C)",
        csv_file="Sat-steam.csv",
        properties=(
            PropertySpec("y1", "Presion de saturacion del vapor", "kPa"),
            PropertySpec("y2", "Densidad liquido", "kg/m3"),
            PropertySpec("y3", "Densidad vapor", "kg/m3"),
            PropertySpec("y4", "Entalpia liquido", "kJ/kg"),
            PropertySpec("y5", "Entalpia vapor", "kJ/kg"),
            PropertySpec("y6", "Entropia liquido", "kJ/kg K"),
            PropertySpec("y7", "Entropia vapor", "kJ/kg K"),
        ),
    ),
)


def available_tables() -> Sequence[TableSpec]:
    return TABLES


def find_table(key: str) -> TableSpec:
    for table in TABLES:
        if table.key == key:
            return table
    raise KeyError(f"No existe la tabla {key!r}")


class CubicSpline:
    """Cubic spline with not-a-knot boundaries, matching SciPy's default style."""

    def __init__(self, x_values: Sequence[float], y_values: Sequence[float]):
        if len(x_values) != len(y_values):
            raise ValueError("x e y deben tener la misma cantidad de datos")
        if len(x_values) < 4:
            raise ValueError("La interpolacion cubica necesita al menos 4 puntos")

        self.x = [float(value) for value in x_values]
        self.y = [float(value) for value in y_values]

        if any(self.x[index] >= self.x[index + 1] for index in range(len(self.x) - 1)):
            raise ValueError("La columna x debe estar ordenada de menor a mayor y sin repetidos")

        self._second_derivatives = self._build_second_derivatives()

    @property
    def min_x(self) -> float:
        return self.x[0]

    @property
    def max_x(self) -> float:
        return self.x[-1]

    def __call__(self, value: float) -> float:
        x_new = float(value)
        if x_new < self.min_x or x_new > self.max_x:
            raise ValueError(
                f"La temperatura debe estar entre {self.min_x:g} y {self.max_x:g} C"
            )

        if x_new == self.max_x:
            interval = len(self.x) - 2
        else:
            interval = max(0, bisect_right(self.x, x_new) - 1)

        x0 = self.x[interval]
        x1 = self.x[interval + 1]
        y0 = self.y[interval]
        y1 = self.y[interval + 1]
        m0 = self._second_derivatives[interval]
        m1 = self._second_derivatives[interval + 1]
        h = x1 - x0

        left = x1 - x_new
        right = x_new - x0

        return (
            m0 * left**3 / (6.0 * h)
            + m1 * right**3 / (6.0 * h)
            + (y0 - m0 * h**2 / 6.0) * left / h
            + (y1 - m1 * h**2 / 6.0) * right / h
        )

    def _build_second_derivatives(self) -> List[float]:
        n = len(self.x)
        h = [self.x[i + 1] - self.x[i] for i in range(n - 1)]
        matrix = [[0.0 for _ in range(n)] for _ in range(n)]
        rhs = [0.0 for _ in range(n)]

        matrix[0][0] = -h[1]
        matrix[0][1] = h[0] + h[1]
        matrix[0][2] = -h[0]

        for i in range(1, n - 1):
            matrix[i][i - 1] = h[i - 1]
            matrix[i][i] = 2.0 * (h[i - 1] + h[i])
            matrix[i][i + 1] = h[i]
            slope_right = (self.y[i + 1] - self.y[i]) / h[i]
            slope_left = (self.y[i] - self.y[i - 1]) / h[i - 1]
            rhs[i] = 6.0 * (slope_right - slope_left)

        matrix[n - 1][n - 3] = -h[n - 2]
        matrix[n - 1][n - 2] = h[n - 3] + h[n - 2]
        matrix[n - 1][n - 1] = -h[n - 3]

        return _solve_linear_system(matrix, rhs)


class PropertyTable:
    def __init__(self, spec: TableSpec, base_path: Path | str = "."):
        self.spec = spec
        self.path = Path(base_path) / spec.csv_file
        rows = _read_numeric_csv(self.path)
        self.x_values = rows["x"]
        self.splines = {
            prop.column: CubicSpline(self.x_values, rows[prop.column])
            for prop in spec.properties
        }

    @property
    def min_temperature(self) -> float:
        return self.x_values[0]

    @property
    def max_temperature(self) -> float:
        return self.x_values[-1]

    def calculate(self, temperature: float) -> Dict[str, float]:
        return {
            prop.column: self.splines[prop.column](temperature)
            for prop in self.spec.properties
        }


def load_all_tables(base_path: Path | str = ".") -> Dict[str, PropertyTable]:
    return {spec.key: PropertyTable(spec, base_path) for spec in TABLES}


def format_value(value: float) -> str:
    return f"{value:.4f}"


def _read_numeric_csv(path: Path) -> Dict[str, List[float]]:
    if not path.exists():
        raise FileNotFoundError(f"No se encontro el archivo {path}")

    with path.open(newline="", encoding="utf-8-sig") as csv_file:
        reader = csv.DictReader(csv_file)
        if not reader.fieldnames:
            raise ValueError(f"El archivo {path.name} no tiene encabezados")

        columns: Dict[str, List[float]] = {name: [] for name in reader.fieldnames}
        for line_number, row in enumerate(reader, start=2):
            for name in reader.fieldnames:
                raw_value = (row.get(name) or "").strip()
                if raw_value == "":
                    continue
                try:
                    columns[name].append(float(raw_value))
                except ValueError as exc:
                    raise ValueError(
                        f"Dato no numerico en {path.name}, linea {line_number}, columna {name}"
                    ) from exc

    required_columns = ["x"]
    for name in required_columns:
        if name not in columns or not columns[name]:
            raise ValueError(f"Falta la columna requerida {name!r} en {path.name}")

    row_count = len(columns["x"])
    return {
        name: values
        for name, values in columns.items()
        if len(values) == row_count
    }


def _solve_linear_system(matrix: List[List[float]], rhs: List[float]) -> List[float]:
    n = len(rhs)
    augmented = [row[:] + [rhs_value] for row, rhs_value in zip(matrix, rhs)]

    for pivot_index in range(n):
        pivot_row = max(
            range(pivot_index, n),
            key=lambda row_index: abs(augmented[row_index][pivot_index]),
        )
        if abs(augmented[pivot_row][pivot_index]) < 1e-14:
            raise ValueError("No se pudo resolver la interpolacion cubica")

        if pivot_row != pivot_index:
            augmented[pivot_index], augmented[pivot_row] = (
                augmented[pivot_row],
                augmented[pivot_index],
            )

        pivot = augmented[pivot_index][pivot_index]
        for column in range(pivot_index, n + 1):
            augmented[pivot_index][column] /= pivot

        for row_index in range(n):
            if row_index == pivot_index:
                continue
            factor = augmented[row_index][pivot_index]
            if factor == 0.0:
                continue
            for column in range(pivot_index, n + 1):
                augmented[row_index][column] -= factor * augmented[pivot_index][column]

    return [augmented[row_index][n] for row_index in range(n)]
