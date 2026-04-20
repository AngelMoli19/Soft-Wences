package org.wences.propiedadestermicas;

import android.app.Activity;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
    private static final int COLOR_BG = 0xFFF3F7F8;
    private static final int COLOR_TEXT = 0xFF132027;
    private static final int COLOR_MUTED = 0xFF53636B;
    private static final int COLOR_PRIMARY = 0xFF126782;
    private static final int COLOR_SECONDARY = 0xFF0A9396;
    private static final int COLOR_ACCENT = 0xFFE9C46A;

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
    private LinearLayout pageLayout;
    private Spinner tableSpinner;
    private TextView inputLabel;
    private EditText temperatureInput;
    private LinearLayout resultsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(COLOR_PRIMARY);
        loadTables();
        buildUi();
        updateSelectedTable();
        animateIntro();
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
        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(false);
        scrollView.setBackgroundColor(COLOR_BG);

        pageLayout = new LinearLayout(this);
        pageLayout.setOrientation(LinearLayout.VERTICAL);
        pageLayout.setPadding(dp(16), dp(16), dp(16), dp(20));
        scrollView.addView(pageLayout);

        pageLayout.addView(heroSection(), matchWrap(0, 0, 0, 14));
        pageLayout.addView(purposeSection(), matchWrap(0, 0, 0, 14));
        pageLayout.addView(authorsSection(), matchWrap(0, 0, 0, 14));
        pageLayout.addView(calculatorSection(), matchWrap(0, 0, 0, 14));

        resultsLayout = new LinearLayout(this);
        resultsLayout.setOrientation(LinearLayout.VERTICAL);
        pageLayout.addView(card(resultsLayout), matchWrap(0, 0, 0, 0));

        tableSpinner.setOnItemSelectedListener(new SimpleItemSelectedListener(this::updateSelectedTable));
        setContentView(scrollView);
    }

    private View heroSection() {
        LinearLayout hero = new LinearLayout(this);
        hero.setOrientation(LinearLayout.VERTICAL);
        hero.setPadding(dp(18), dp(18), dp(18), dp(16));
        hero.setBackground(gradient(0xFF0B4F6C, 0xFF0A9396, dp(8)));
        hero.setElevation(dp(4));

        LinearLayout brandRow = new LinearLayout(this);
        brandRow.setOrientation(LinearLayout.HORIZONTAL);
        brandRow.setGravity(Gravity.CENTER_VERTICAL);

        ImageView logo = new ImageView(this);
        logo.setImageResource(getResources().getIdentifier("ic_thermowences_logo", "drawable", getPackageName()));
        brandRow.addView(logo, new LinearLayout.LayoutParams(dp(64), dp(64)));

        LinearLayout brandText = new LinearLayout(this);
        brandText.setOrientation(LinearLayout.VERTICAL);
        brandText.setPadding(dp(12), 0, 0, 0);
        brandText.addView(text("TermoWences", 27, 0xFFFFFFFF, true), compactWrap());
        brandText.addView(text("Propiedades termicas para ingenieria", 14, 0xFFE8F7FA, false), compactWrap());
        brandRow.addView(brandText, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        hero.addView(brandRow, matchWrap(0, 0, 0, 14));

        ImageView illustration = new ImageView(this);
        illustration.setImageResource(getResources().getIdentifier("illustration_engineering", "drawable", getPackageName()));
        illustration.setAdjustViewBounds(true);
        illustration.setBackground(rounded(0x22FFFFFF, dp(8)));
        illustration.setPadding(dp(10), dp(10), dp(10), dp(10));
        hero.addView(illustration, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(150)));

        TextView intro = text(
            "Calcula propiedades de agua, aire seco y vapor saturado mediante interpolacion cubica, usando tablas CSV integradas en la app.",
            15,
            0xFFFFFFFF,
            false
        );
        intro.setLineSpacing(0, 1.08f);
        hero.addView(intro, matchWrap(0, dp(14), 0, 0));
        return hero;
    }

    private View purposeSection() {
        LinearLayout content = section("Para que sirve");
        content.addView(body(
            "TermoWences esta pensado como una herramienta academica y practica para consultar rapidamente propiedades termofisicas. " +
            "Permite ingresar una temperatura, elegir la tabla correspondiente y obtener valores interpolados con orden y precision."
        ), compactWrap());
        content.addView(feature("Agua", "Presion de saturacion, densidad, viscosidad, difusividad y numero de Prandtl."));
        content.addView(feature("Aire seco", "Propiedades para calculos de transferencia de calor y fluidos."));
        content.addView(feature("Vapor saturado", "Presion, densidades, entalpias y entropias de referencia."));
        return card(content);
    }

    private View authorsSection() {
        LinearLayout content = section("Autores");
        content.addView(body("Desarrollado para apoyar el analisis de propiedades termicas en ejercicios de ingenieria."), compactWrap());
        content.addView(author("Wenceslao T. Medina Espinoza"));
        content.addView(author("Miguel A. Molina Mansilla"));
        content.addView(author("Edward Torres Cruz"));
        content.addView(author("Alica Leon Tacca"));
        return card(content);
    }

    private View calculatorSection() {
        LinearLayout content = section("Calculadora");

        tableSpinner = new Spinner(this);
        String[] titles = new String[TABLES.length];
        for (int i = 0; i < TABLES.length; i++) {
            titles[i] = TABLES[i].title;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, titles);
        tableSpinner.setAdapter(adapter);
        tableSpinner.setBackground(roundedStroke(0xFFFFFFFF, 0xFFD5E5EA, dp(8)));
        content.addView(tableSpinner, matchWrap(0, dp(10), 0, 10));

        inputLabel = text("", 14, COLOR_MUTED, true);
        content.addView(inputLabel, compactWrap());

        temperatureInput = new EditText(this);
        temperatureInput.setHint("Ingrese temperatura");
        temperatureInput.setSingleLine(true);
        temperatureInput.setTextSize(18);
        temperatureInput.setTextColor(COLOR_TEXT);
        temperatureInput.setHintTextColor(0xFF7E8D94);
        temperatureInput.setPadding(dp(12), 0, dp(12), 0);
        temperatureInput.setBackground(roundedStroke(0xFFFFFFFF, 0xFFD5E5EA, dp(8)));
        temperatureInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
        temperatureInput.setImeOptions(EditorInfo.IME_ACTION_DONE);
        temperatureInput.setOnEditorActionListener((view, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                calculate();
                return true;
            }
            return false;
        });
        content.addView(temperatureInput, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(52)));

        LinearLayout buttons = new LinearLayout(this);
        buttons.setOrientation(LinearLayout.HORIZONTAL);
        buttons.setPadding(0, dp(14), 0, 0);

        Button calculate = button("Calcular", COLOR_PRIMARY, 0xFFFFFFFF);
        calculate.setOnClickListener(view -> calculate());
        buttons.addView(calculate, weightedButton(0, 0, 6, 0));

        Button clear = button("Limpiar", 0xFFEAF3F5, COLOR_PRIMARY);
        clear.setOnClickListener(view -> clearResults());
        buttons.addView(clear, weightedButton(6, 0, 0, 0));
        content.addView(buttons, matchWrap(0, 0, 0, 0));
        return card(content);
    }

    private LinearLayout section(String title) {
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.addView(text(title, 20, COLOR_TEXT, true), compactWrap());
        return content;
    }

    private View feature(String title, String detail) {
        TextView view = body(title + ": " + detail);
        view.setPadding(dp(10), dp(9), dp(10), dp(9));
        view.setBackground(rounded(0xFFF5FAFB, dp(8)));
        return wrapWithMargin(view, 0, dp(8), 0, 0);
    }

    private View author(String name) {
        TextView view = text(name, 15, COLOR_TEXT, true);
        view.setPadding(dp(12), dp(9), dp(12), dp(9));
        view.setBackground(roundedStroke(0xFFFFFFFF, 0xFFE1ECEF, dp(8)));
        return wrapWithMargin(view, 0, dp(8), 0, 0);
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
        resultsLayout.addView(text(String.format(Locale.US, "%s a %.2f C", spec.title, temperature), 20, COLOR_TEXT, true), matchWrap(0, 0, 0, 8));

        for (PropertySpec prop : spec.properties) {
            Double value = values.get(prop.column);
            if (value == null) {
                continue;
            }
            String unit = prop.unit.isEmpty() ? "" : " " + prop.unit;
            resultsLayout.addView(resultRow(prop.label, String.format(Locale.US, "%.4f%s", value, unit)), matchWrap(0, 0, 0, 8));
        }
        animateView(resultsLayout, 0);
    }

    private void clearResults() {
        resultsLayout.removeAllViews();
        resultsLayout.addView(text("Resultados", 20, COLOR_TEXT, true), matchWrap(0, 0, 0, 8));
        resultsLayout.addView(body("Seleccione una tabla, ingrese una temperatura y presione Calcular."), compactWrap());
    }

    private View resultRow(String name, String value) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.VERTICAL);
        row.setPadding(dp(12), dp(10), dp(12), dp(10));
        row.setBackground(roundedStroke(0xFFFFFFFF, 0xFFE0EBEE, dp(8)));
        row.addView(text(name, 14, COLOR_MUTED, false), compactWrap());
        row.addView(text(value, 19, COLOR_PRIMARY, true), compactWrap());
        return row;
    }

    private TableSpec selectedSpec() {
        int index = Math.max(0, tableSpinner.getSelectedItemPosition());
        return TABLES[index];
    }

    private View card(View child) {
        LinearLayout wrapper = new LinearLayout(this);
        wrapper.setOrientation(LinearLayout.VERTICAL);
        wrapper.setPadding(dp(16), dp(16), dp(16), dp(16));
        wrapper.setBackground(rounded(0xFFFFFFFF, dp(8)));
        wrapper.setElevation(dp(3));
        wrapper.addView(child, compactWrap());
        return wrapper;
    }

    private TextView text(String value, int sp, int color, boolean bold) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextColor(color);
        view.setTextSize(sp);
        view.setLineSpacing(0, 1.1f);
        if (bold) {
            view.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        }
        return view;
    }

    private TextView body(String value) {
        return text(value, 15, COLOR_MUTED, false);
    }

    private Button button(String value, int background, int textColor) {
        Button button = new Button(this);
        button.setText(value);
        button.setTextColor(textColor);
        button.setTextSize(15);
        button.setAllCaps(false);
        button.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        button.setBackground(rounded(background, dp(8)));
        return button;
    }

    private GradientDrawable rounded(int color, int radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(radius);
        return drawable;
    }

    private GradientDrawable roundedStroke(int color, int strokeColor, int radius) {
        GradientDrawable drawable = rounded(color, radius);
        drawable.setStroke(dp(1), strokeColor);
        return drawable;
    }

    private GradientDrawable gradient(int start, int end, int radius) {
        GradientDrawable drawable = new GradientDrawable(GradientDrawable.Orientation.TL_BR, new int[] {start, end});
        drawable.setCornerRadius(radius);
        return drawable;
    }

    private LinearLayout.LayoutParams compactWrap() {
        return new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    private LinearLayout.LayoutParams matchWrap(int left, int top, int right, int bottom) {
        LinearLayout.LayoutParams params = compactWrap();
        params.setMargins(left, top, right, bottom);
        return params;
    }

    private LinearLayout.LayoutParams weightedButton(int left, int top, int right, int bottom) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, dp(48), 1);
        params.setMargins(left, top, right, bottom);
        return params;
    }

    private View wrapWithMargin(View child, int left, int top, int right, int bottom) {
        LinearLayout wrapper = new LinearLayout(this);
        wrapper.setOrientation(LinearLayout.VERTICAL);
        wrapper.addView(child, compactWrap());
        wrapper.setLayoutParams(matchWrap(left, top, right, bottom));
        return wrapper;
    }

    private void animateIntro() {
        for (int i = 0; i < pageLayout.getChildCount(); i++) {
            animateView(pageLayout.getChildAt(i), i * 90L);
        }
    }

    private void animateView(View view, long offset) {
        AnimationSet set = new AnimationSet(true);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.setDuration(520);
        set.setStartOffset(offset);
        AlphaAnimation fade = new AlphaAnimation(0f, 1f);
        TranslateAnimation slide = new TranslateAnimation(0, 0, dp(18), 0);
        set.addAnimation(fade);
        set.addAnimation(slide);
        view.startAnimation(set);
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
