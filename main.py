from __future__ import annotations

import os
from pathlib import Path

BASE_DIR = Path(__file__).resolve().parent
if os.name == "nt":
    os.environ.setdefault("KIVY_HOME", str(BASE_DIR / ".kivy"))

from kivy.app import App
from kivy.core.window import Window
from kivy.metrics import dp
from kivy.uix.boxlayout import BoxLayout
from kivy.uix.button import Button
from kivy.uix.gridlayout import GridLayout
from kivy.uix.label import Label
from kivy.uix.scrollview import ScrollView
from kivy.uix.spinner import Spinner
from kivy.uix.textinput import TextInput

from thermo_core import available_tables, format_value, load_all_tables


class ThermoApp(App):
    title = "Propiedades termicas"

    def build(self):
        Window.clearcolor = (0.96, 0.97, 0.98, 1)
        self.tables = load_all_tables(BASE_DIR)
        self.specs = {spec.title: spec for spec in available_tables()}
        self.current_spec = available_tables()[0]

        root = BoxLayout(
            orientation="vertical",
            padding=dp(14),
            spacing=dp(10),
        )

        title = Label(
            text="Propiedades termicas",
            color=(0.08, 0.1, 0.12, 1),
            font_size="22sp",
            bold=True,
            size_hint_y=None,
            height=dp(42),
        )
        root.add_widget(title)

        self.spinner = Spinner(
            text=self.current_spec.title,
            values=[spec.title for spec in available_tables()],
            size_hint_y=None,
            height=dp(46),
            background_color=(0.13, 0.34, 0.43, 1),
        )
        self.spinner.bind(text=self.on_table_change)
        root.add_widget(self.spinner)

        self.input_label = Label(
            text=self.current_spec.input_label,
            color=(0.08, 0.1, 0.12, 1),
            halign="left",
            valign="middle",
            size_hint_y=None,
            height=dp(28),
        )
        self.input_label.bind(size=self._sync_text_size)
        root.add_widget(self.input_label)

        self.temperature_input = TextInput(
            hint_text="Ingrese temperatura",
            multiline=False,
            size_hint_y=None,
            height=dp(48),
            font_size="18sp",
            padding=(dp(10), dp(12)),
            write_tab=False,
        )
        self.temperature_input.bind(on_text_validate=lambda *_: self.calculate())
        root.add_widget(self.temperature_input)

        button_row = BoxLayout(
            orientation="horizontal",
            spacing=dp(10),
            size_hint_y=None,
            height=dp(46),
        )
        button_row.add_widget(
            Button(
                text="Interpolar",
                background_color=(0.07, 0.48, 0.34, 1),
                on_release=lambda *_: self.calculate(),
            )
        )
        button_row.add_widget(
            Button(
                text="Limpiar",
                background_color=(0.52, 0.21, 0.18, 1),
                on_release=lambda *_: self.clear_results(),
            )
        )
        root.add_widget(button_row)

        self.message_label = Label(
            text="",
            color=(0.52, 0.1, 0.08, 1),
            halign="left",
            valign="middle",
            size_hint_y=None,
            height=dp(42),
        )
        self.message_label.bind(size=self._sync_text_size)
        root.add_widget(self.message_label)

        scroll = ScrollView()
        self.results_grid = GridLayout(
            cols=1,
            spacing=dp(8),
            size_hint_y=None,
        )
        self.results_grid.bind(minimum_height=self.results_grid.setter("height"))
        scroll.add_widget(self.results_grid)
        root.add_widget(scroll)

        self.result_labels = {}
        self.rebuild_result_rows()
        self.update_range_message()
        return root

    def on_table_change(self, _spinner, title):
        self.current_spec = self.specs[title]
        self.input_label.text = self.current_spec.input_label
        self.temperature_input.text = ""
        self.rebuild_result_rows()
        self.update_range_message()

    def rebuild_result_rows(self):
        self.results_grid.clear_widgets()
        self.result_labels = {}

        for prop in self.current_spec.properties:
            row = BoxLayout(
                orientation="horizontal",
                spacing=dp(8),
                padding=(dp(10), dp(8)),
                size_hint_y=None,
                height=dp(68),
            )

            name_label = Label(
                text=self._property_text(prop),
                color=(0.08, 0.1, 0.12, 1),
                halign="left",
                valign="middle",
                size_hint_x=0.68,
                font_size="14sp",
            )
            name_label.bind(size=self._sync_text_size)

            value_label = Label(
                text="-",
                color=(0.02, 0.23, 0.2, 1),
                halign="right",
                valign="middle",
                size_hint_x=0.32,
                font_size="16sp",
                bold=True,
            )
            value_label.bind(size=self._sync_text_size)

            row.add_widget(name_label)
            row.add_widget(value_label)
            self.results_grid.add_widget(row)
            self.result_labels[prop.column] = value_label

    def calculate(self):
        raw_temperature = self.temperature_input.text.strip().replace(",", ".")
        if not raw_temperature:
            self.message_label.text = "Ingrese una temperatura."
            return

        try:
            temperature = float(raw_temperature)
        except ValueError:
            self.message_label.text = "Ingrese un numero valido."
            return

        table = self.tables[self.current_spec.key]
        try:
            results = table.calculate(temperature)
        except ValueError as exc:
            self.message_label.text = str(exc)
            return

        self.message_label.text = (
            f"Resultados para {temperature:g} C "
            f"({table.min_temperature:g} a {table.max_temperature:g} C)."
        )
        for prop in self.current_spec.properties:
            self.result_labels[prop.column].text = format_value(results[prop.column])

    def clear_results(self):
        self.temperature_input.text = ""
        self.update_range_message()
        for label in self.result_labels.values():
            label.text = "-"

    def update_range_message(self):
        table = self.tables[self.current_spec.key]
        self.message_label.text = (
            f"Rango disponible: {table.min_temperature:g} a {table.max_temperature:g} C."
        )

    @staticmethod
    def _property_text(prop):
        if prop.unit:
            return f"{prop.label}\n{prop.unit}"
        return prop.label

    @staticmethod
    def _sync_text_size(label, size):
        label.text_size = size


if __name__ == "__main__":
    ThermoApp().run()
