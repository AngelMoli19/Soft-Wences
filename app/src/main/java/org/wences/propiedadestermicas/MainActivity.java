package org.wences.propiedadestermicas;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends Activity {
    private static final TableSpec[] TABLES = new TableSpec[] {
        new TableSpec(
            "water",
            "Agua",
            "Temperatura del agua (C)",
            "water.csv",
            new PropertySpec[] {
                new PropertySpec("y1", "Presion de saturacion", "kPa"),
                new PropertySpec("y2", "Coeficiente de expansion volumetrica", "K-1"),
                new PropertySpec("y3", "Densidad", "kg/m3"),
                new PropertySpec("y4", "Calor especifico", "kJ/kg K"),
                new PropertySpec("y5", "Conductividad termica", "W/m K"),
                new PropertySpec("y6", "Difusividad termica", "x 10-6 m2/s"),
                new PropertySpec("y7", "Viscosidad absoluta", "x 10-6 Pa s"),
                new PropertySpec("y8", "Viscosidad cinematica", "x 10-6 m2/s"),
                new PropertySpec("y9", "Numero de Prandtl", "")
            }
        ),
        new TableSpec(
            "air",
            "Aire seco",
            "Temperatura del aire (C)",
            "Air.csv",
            new PropertySpec[] {
                new PropertySpec("y1", "Coeficiente de expansion volumetrica", "K-1"),
                new PropertySpec("y2", "Densidad", "kg/m3"),
                new PropertySpec("y3", "Calor especifico", "kJ/kg K"),
                new PropertySpec("y4", "Conductividad termica", "W/m K"),
                new PropertySpec("y5", "Difusividad termica", "x 10-6 m2/s"),
                new PropertySpec("y6", "Viscosidad absoluta", "x 10-6 Pa s"),
                new PropertySpec("y7", "Viscosidad cinematica", "x 10-6 m2/s"),
                new PropertySpec("y8", "Numero de Prandtl", "")
            }
        ),
        new TableSpec(
            "steam",
            "Vapor saturado",
            "Temperatura del vapor (C)",
            "Sat-steam.csv",
            new PropertySpec[] {
                new PropertySpec("y1", "Presion de saturacion del vapor", "kPa"),
                new PropertySpec("y2", "Densidad liquido", "kg/m3"),
                new PropertySpec("y3", "Densidad vapor", "kg/m3"),
                new PropertySpec("y4", "Entalpia liquido", "kJ/kg"),
                new PropertySpec("y5", "Entalpia vapor", "kJ/kg"),
                new PropertySpec("y6", "Entropia liquido", "kJ/kg K"),
                new PropertySpec("y7", "Entropia vapor", "kJ/kg K")
            }
        )
    };

    private final Map<String, PropertyTable> tables = new HashMap<>();
    private Spinner tableSpinner;
    private TextView inputLabel;
    private EditText temperatureInput;
    private LinearLayout resultsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadTables();
        buildUi();
        updateSelectedTable();
    }

    private void loadTables() {
        try {
            for (TableSpec spec : TABLES) {
                tables.put(spec.key, new PropertyTable(spec, readCsv(spec.csvFile)));
            }
        } catch (Exception exc) {
            Toast.makeText(this, exc.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void buildUi() {
        int margin = dp(14);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(margin, margin, margin, margin);
        root.setBackgroundColor(0xFFF5F7FA);

        TextView title = new TextView(this);
        title.setText("Propiedades termicas");
        title.setTextSize(22);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        title.setTextColor(0xFF111820);
        title.setGravity(Gravity.CENTER);
        root.addView(title, matchWrap());

        tableSpinner = new Spinner(this);
        String[] titles = new String[TABLES.length];
        for (int i = 0; i < TABLES.length; i++) {
            titles[i] = TABLES[i].title;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, titles);
        tableSpinner.setAdapter(adapter);
        root.addView(tableSpinner, matchWrap());

        inputLabel = label("");
        root.addView(inputLabel, matchWrap());

        temperatureInput = new EditText(this);
        temperatureInput.setHint("Ingrese temperatura");
        temperatureInput.setSingleLine(true);
        temperatureInput.setTextSize(18);
        temperatureInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
        temperatureInput.setImeOptions(EditorInfo.IME_ACTION_DONE);
        temperatureInput.setOnEditorActionListener((view, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                calculate();
                return true;
            }
            return false;
        });
        root.addView(temperatureInput, matchWrap());

        LinearLayout buttons = new LinearLayout(this);
        buttons.setOrientation(LinearLayout.HORIZONTAL);

        Button calculate = new Button(this);
        calculate.setText("Calcular");
        calculate.setOnClickListener(view -> calculate());
        buttons.addView(calculate, new LinearLayout.LayoutParams(0, dp(48), 1));

        Button clear = new Button(this);
        clear.setText("Limpiar");
        clear.setOnClickListener(view -> clearResults());
        buttons.addView(clear, new LinearLayout.LayoutParams(0, dp(48), 1));
        root.addView(buttons, matchWrap());

        ScrollView scrollView = new ScrollView(this);
        resultsLayout = new LinearLayout(this);
        resultsLayout.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(resultsLayout);
        root.addView(scrollView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1));

        tableSpinner.setOnItemSelectedListener(new SimpleItemSelectedListener(this::updateSelectedTable));
        setContentView(root);
    }

    private void updateSelectedTable() {
        TableSpec spec = selectedSpec();
        inputLabel.setText(spec.inputLabel);
        temperatureInput.setText("");
        clearResults();
    }

    private void calculate() {
        TableSpec spec = selectedSpec();
        PropertyTable table = tables.get(spec.key);
        if (table == null) {
            showMessage("No se pudo cargar la tabla");
            return;
        }

        double temperature;
        try {
            temperature = Double.parseDouble(temperatureInput.getText().toString().trim().replace(",", "."));
        } catch (NumberFormatException exc) {
            showMessage("Ingrese una temperatura valida");
            return;
        }

        if (temperature < table.minTemperature() || temperature > table.maxTemperature()) {
            showMessage(String.format(Locale.US, "La temperatura debe estar entre %.2f y %.2f C", table.minTemperature(), table.maxTemperature()));
            return;
        }

        Map<String, Double> values = table.calculate(temperature);
        resultsLayout.removeAllViews();

        TextView heading = label(String.format(Locale.US, "%s a %.2f C", spec.title, temperature));
        heading.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        heading.setTextSize(18);
        resultsLayout.addView(heading, matchWrap());

        for (PropertySpec prop : spec.properties) {
            Double value = values.get(prop.column);
            if (value == null) {
                continue;
            }
            String unit = prop.unit.isEmpty() ? "" : " " + prop.unit;
            resultsLayout.addView(resultRow(prop.label, String.format(Locale.US, "%.4f%s", value, unit)), matchWrap());
        }
    }

    private void clearResults() {
        resultsLayout.removeAllViews();
        resultsLayout.addView(label("Seleccione una tabla e ingrese temperatura."), matchWrap());
    }

    private TableSpec selectedSpec() {
        int index = Math.max(0, tableSpinner.getSelectedItemPosition());
        return TABLES[index];
    }

    private TextView label(String text) {
        TextView view = new TextView(this);
        view.setText(text);
        view.setTextColor(0xFF111820);
        view.setTextSize(16);
        view.setPadding(0, dp(8), 0, dp(8));
        return view;
    }

    private TextView resultRow(String name, String value) {
        TextView view = label(name + "\n" + value);
        view.setBackgroundColor(0xFFFFFFFF);
        view.setPadding(dp(10), dp(10), dp(10), dp(10));
        return view;
    }

    private LinearLayout.LayoutParams matchWrap() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, dp(10));
        return params;
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private Map<String, List<Double>> readCsv(String assetName) throws Exception {
        Map<String, List<Double>> columns = new HashMap<>();
        try (InputStream stream = getAssets().open(assetName);
             BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                throw new IllegalArgumentException("El archivo " + assetName + " esta vacio");
            }
            headerLine = headerLine.replace("\uFEFF", "");
            String[] headers = headerLine.split(",");
            for (String header : headers) {
                columns.put(header.trim(), new ArrayList<>());
            }

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] cells = line.split(",", -1);
                for (int i = 0; i < headers.length && i < cells.length; i++) {
                    String raw = cells[i].trim();
                    if (!raw.isEmpty()) {
                        columns.get(headers[i].trim()).add(Double.parseDouble(raw));
                    }
                }
            }
        }
        return columns;
    }

    private static final class PropertySpec {
        final String column;
        final String label;
        final String unit;

        PropertySpec(String column, String label, String unit) {
            this.column = column;
            this.label = label;
            this.unit = unit;
        }
    }

    private static final class TableSpec {
        final String key;
        final String title;
        final String inputLabel;
        final String csvFile;
        final PropertySpec[] properties;

        TableSpec(String key, String title, String inputLabel, String csvFile, PropertySpec[] properties) {
            this.key = key;
            this.title = title;
            this.inputLabel = inputLabel;
            this.csvFile = csvFile;
            this.properties = properties;
        }
    }

    private static final class PropertyTable {
        private final TableSpec spec;
        private final List<Double> xValues;
        private final Map<String, CubicSpline> splines = new HashMap<>();

        PropertyTable(TableSpec spec, Map<String, List<Double>> columns) {
            this.spec = spec;
            this.xValues = columns.get("x");
            if (xValues == null || xValues.size() < 4) {
                throw new IllegalArgumentException("La tabla " + spec.title + " no tiene datos suficientes");
            }
            for (PropertySpec prop : spec.properties) {
                List<Double> values = columns.get(prop.column);
                if (values == null) {
                    throw new IllegalArgumentException("Falta la columna " + prop.column + " en " + spec.csvFile);
                }
                splines.put(prop.column, new CubicSpline(xValues, values));
            }
        }

        double minTemperature() {
            return xValues.get(0);
        }

        double maxTemperature() {
            return xValues.get(xValues.size() - 1);
        }

        Map<String, Double> calculate(double temperature) {
            Map<String, Double> values = new HashMap<>();
            for (PropertySpec prop : spec.properties) {
                values.put(prop.column, splines.get(prop.column).valueAt(temperature));
            }
            return values;
        }
    }

    private static final class CubicSpline {
        private final List<Double> x;
        private final List<Double> y;
        private final double[] secondDerivatives;

        CubicSpline(List<Double> x, List<Double> y) {
            if (x.size() != y.size()) {
                throw new IllegalArgumentException("x e y deben tener la misma cantidad de datos");
            }
            this.x = x;
            this.y = y;
            this.secondDerivatives = buildSecondDerivatives();
        }

        double valueAt(double value) {
            int interval = x.size() - 2;
            if (value < x.get(x.size() - 1)) {
                interval = 0;
                while (interval < x.size() - 1 && x.get(interval + 1) <= value) {
                    interval++;
                }
                interval = Math.max(0, interval);
            }

            double x0 = x.get(interval);
            double x1 = x.get(interval + 1);
            double y0 = y.get(interval);
            double y1 = y.get(interval + 1);
            double m0 = secondDerivatives[interval];
            double m1 = secondDerivatives[interval + 1];
            double h = x1 - x0;
            double left = x1 - value;
            double right = value - x0;

            return m0 * Math.pow(left, 3) / (6.0 * h)
                + m1 * Math.pow(right, 3) / (6.0 * h)
                + (y0 - m0 * h * h / 6.0) * left / h
                + (y1 - m1 * h * h / 6.0) * right / h;
        }

        private double[] buildSecondDerivatives() {
            int n = x.size();
            double[] h = new double[n - 1];
            for (int i = 0; i < n - 1; i++) {
                h[i] = x.get(i + 1) - x.get(i);
            }

            double[][] matrix = new double[n][n];
            double[] rhs = new double[n];
            matrix[0][0] = -h[1];
            matrix[0][1] = h[0] + h[1];
            matrix[0][2] = -h[0];

            for (int i = 1; i < n - 1; i++) {
                matrix[i][i - 1] = h[i - 1];
                matrix[i][i] = 2.0 * (h[i - 1] + h[i]);
                matrix[i][i + 1] = h[i];
                double slopeRight = (y.get(i + 1) - y.get(i)) / h[i];
                double slopeLeft = (y.get(i) - y.get(i - 1)) / h[i - 1];
                rhs[i] = 6.0 * (slopeRight - slopeLeft);
            }

            matrix[n - 1][n - 3] = -h[n - 2];
            matrix[n - 1][n - 2] = h[n - 3] + h[n - 2];
            matrix[n - 1][n - 1] = -h[n - 3];
            return solve(matrix, rhs);
        }

        private double[] solve(double[][] matrix, double[] rhs) {
            int n = rhs.length;
            for (int column = 0; column < n; column++) {
                int pivot = column;
                for (int row = column + 1; row < n; row++) {
                    if (Math.abs(matrix[row][column]) > Math.abs(matrix[pivot][column])) {
                        pivot = row;
                    }
                }
                double[] tempRow = matrix[column];
                matrix[column] = matrix[pivot];
                matrix[pivot] = tempRow;
                double temp = rhs[column];
                rhs[column] = rhs[pivot];
                rhs[pivot] = temp;

                double pivotValue = matrix[column][column];
                if (Math.abs(pivotValue) < 1e-12) {
                    throw new IllegalArgumentException("No se pudo resolver la interpolacion");
                }

                for (int j = column; j < n; j++) {
                    matrix[column][j] /= pivotValue;
                }
                rhs[column] /= pivotValue;

                for (int row = 0; row < n; row++) {
                    if (row == column) {
                        continue;
                    }
                    double factor = matrix[row][column];
                    for (int j = column; j < n; j++) {
                        matrix[row][j] -= factor * matrix[column][j];
                    }
                    rhs[row] -= factor * rhs[column];
                }
            }
            return rhs;
        }
    }
}
