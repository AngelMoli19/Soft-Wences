package org.wences.propiedadestermicas;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends Activity {
    private static final int COLOR_BG = 0xFF101416;
    private static final int COLOR_SURFACE = 0xFF171D21;
    private static final int COLOR_SURFACE_2 = 0xFF20282D;
    private static final int COLOR_BORDER = 0xFF2C383E;
    private static final int COLOR_TEXT = 0xFFF3F6F7;
    private static final int COLOR_MUTED = 0xFFA7B2B7;
    private static final int COLOR_PRIMARY = 0xFF5DA399;
    private static final int COLOR_SECONDARY = 0xFF5B8FA3;
    private static final int COLOR_ACCENT = 0xFFC6A15B;
    private static final int COLOR_DANGER = 0xFFB85C5C;
    private static final int SCREEN_MENU = 0;
    private static final int SCREEN_CALCULATOR = 1;
    private static final int SCREEN_HISTORY = 2;
    private static final int SCREEN_ABOUT = 3;
    private static final int REQUEST_CREATE_PDF = 701;
    private static final String HISTORY_PREFS = "thermowences_history";
    private static final String HISTORY_KEY = "records";
    private static final String RECORD_SEPARATOR = "\u001E";
    private static final String FIELD_SEPARATOR = "\u001F";
    private static final String LINE_SEPARATOR = "\u001D";

    private static final TableSpec[] TABLES = new TableSpec[] {
        new TableSpec(
            "water",
            "Agua",
            "Temperatura del agua (C)",
            "water.csv",
            new PropertySpec[] {
                new PropertySpec("y1", "Presión de saturación", "kPa"),
                new PropertySpec("y2", "Coeficiente de expansión volumétrica", "x 10^-3 K^-1"),
                new PropertySpec("y3", "Densidad", "kg/m3"),
                new PropertySpec("y4", "Calor específico", "kJ/kg K"),
                new PropertySpec("y5", "Conductividad térmica", "W/m K"),
                new PropertySpec("y6", "Difusividad térmica", "x 10-6 m2/s"),
                new PropertySpec("y7", "Viscosidad absoluta", "x 10-6 Pa s"),
                new PropertySpec("y8", "Viscosidad cinemática", "x 10-6 m2/s"),
                new PropertySpec("y9", "Número de Prandtl", "")
            }
        ),
        new TableSpec(
            "air",
            "Aire seco",
            "Temperatura del aire (C)",
            "Air.csv",
            new PropertySpec[] {
                new PropertySpec("y1", "Coeficiente de expansión volumétrica", "x 10^-3 K^-1"),
                new PropertySpec("y2", "Densidad", "kg/m3"),
                new PropertySpec("y3", "Calor específico", "kJ/kg K"),
                new PropertySpec("y4", "Conductividad térmica", "W/m K"),
                new PropertySpec("y5", "Difusividad térmica", "x 10-6 m2/s"),
                new PropertySpec("y6", "Viscosidad absoluta", "x 10-6 Pa s"),
                new PropertySpec("y7", "Viscosidad cinemática", "x 10-6 m2/s"),
                new PropertySpec("y8", "Número de Prandtl", "")
            }
        ),
        new TableSpec(
            "steam",
            "Vapor saturado",
            "Temperatura del vapor (C)",
            "Sat-steam.csv",
            new PropertySpec[] {
                new PropertySpec("y1", "Presión de saturación del vapor", "kPa"),
                new PropertySpec("y2", "Densidad líquido", "kg/m3"),
                new PropertySpec("y3", "Densidad vapor", "kg/m3"),
                new PropertySpec("y4", "Entalpía líquido", "kJ/kg"),
                new PropertySpec("y5", "Entalpía vapor", "kJ/kg"),
                new PropertySpec("y6", "Entropía líquido", "kJ/kg K"),
                new PropertySpec("y7", "Entropía vapor", "kJ/kg K")
            }
        )
    };

    private final Map<String, PropertyTable> tables = new HashMap<>();
    private LinearLayout pageLayout;
    private LinearLayout navDrawer;
    private boolean navOpen = false;
    private int currentScreen = SCREEN_MENU;
    private Spinner tableSpinner;
    private TextView inputLabel;
    private TextView tableDescription;
    private TextView tableRange;
    private EditText temperatureInput;
    private LinearLayout resultsLayout;
    private LinearLayout historyLayout;
    private Button exportPdfButton;
    private final List<CalculationRecord> history = new ArrayList<>();
    private final List<String> lastPdfLines = new ArrayList<>();
    private final List<ResultEntry> lastPdfEntries = new ArrayList<>();
    private String lastPdfTitle = "";
    private String lastTableKey = "";
    private double lastTemperature = Double.NaN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setStatusBarColor(COLOR_BG);
        window.setNavigationBarColor(0xFF0B0E10);
        loadTables();
        loadHistory();
        buildUi();
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
        pageLayout.setPadding(dp(16), dp(32), dp(16), dp(86));
        scrollView.addView(pageLayout);
        setContentView(scrollView);
        showMainMenu();
    }

    private void showMainMenu() {
        currentScreen = SCREEN_MENU;
        navOpen = false;
        pageLayout.removeAllViews();
        pageLayout.addView(topBar("TermoWences"), matchWrap(0, 0, 0, 12));
        pageLayout.addView(heroSection(), matchWrap(0, 0, 0, 14));
        pageLayout.addView(menuSummary(), matchWrap(0, 0, 0, 0));
        animateIntro();
    }

    private View topBar(String title) {
        FrameLayout frame = new FrameLayout(this);

        LinearLayout bar = new LinearLayout(this);
        bar.setOrientation(LinearLayout.HORIZONTAL);
        bar.setGravity(Gravity.CENTER_VERTICAL);
        bar.setPadding(dp(12), dp(10), dp(12), dp(10));
        bar.setBackground(roundedStroke(COLOR_SURFACE, COLOR_BORDER, dp(8)));

        TextView menu = iconButton("☰", COLOR_SURFACE_2, COLOR_TEXT);
        menu.setOnClickListener(view -> toggleNavDrawer());
        bar.addView(menu, new LinearLayout.LayoutParams(dp(48), dp(48)));

        TextView titleView = text(title, 18, COLOR_TEXT, true);
        titleView.setGravity(Gravity.CENTER_VERTICAL);
        titleView.setPadding(dp(12), 0, 0, 0);
        bar.addView(titleView, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        ImageView logo = new ImageView(this);
        logo.setImageResource(getResources().getIdentifier("ic_thermowences_logo", "drawable", getPackageName()));
        bar.addView(logo, new LinearLayout.LayoutParams(dp(40), dp(40)));
        frame.addView(bar, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));

        navDrawer = buildNavDrawer();
        navDrawer.setVisibility(View.GONE);
        FrameLayout.LayoutParams navParams = new FrameLayout.LayoutParams(dp(184), FrameLayout.LayoutParams.WRAP_CONTENT);
        navParams.gravity = Gravity.START | Gravity.TOP;
        navParams.topMargin = dp(62);
        frame.addView(navDrawer, navParams);
        return frame;
    }

    private LinearLayout buildNavDrawer() {
        LinearLayout drawer = new LinearLayout(this);
        drawer.setOrientation(LinearLayout.VERTICAL);
        drawer.setGravity(Gravity.CENTER_VERTICAL);
        drawer.setPadding(dp(10), dp(10), dp(10), dp(10));
        drawer.setBackground(roundedStroke(0xF01A2024, COLOR_BORDER, dp(8)));
        drawer.setElevation(dp(8));
        drawer.addView(navItem("⌂", "Inicio", SCREEN_MENU), navItemWrap(0));
        drawer.addView(navItem("∑", "Calcular", SCREEN_CALCULATOR), navItemWrap(8));
        drawer.addView(navItem("◷", "Historial", SCREEN_HISTORY), navItemWrap(8));
        drawer.addView(navItem("ⓘ", "Info", SCREEN_ABOUT), navItemWrap(8));
        return drawer;
    }

    private View navItem(String icon, String label, int targetScreen) {
        int bg = currentScreen == targetScreen ? COLOR_PRIMARY : COLOR_SURFACE_2;
        int fg = currentScreen == targetScreen ? 0xFF07100F : COLOR_TEXT;
        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.HORIZONTAL);
        item.setGravity(Gravity.CENTER_VERTICAL);
        item.setPadding(dp(8), 0, dp(10), 0);
        item.setBackground(rounded(bg, dp(8)));

        TextView iconView = navGlyph(icon, fg);
        item.addView(iconView, new LinearLayout.LayoutParams(dp(42), dp(42)));

        TextView labelView = text(label, 13, fg, true);
        labelView.setGravity(Gravity.CENTER_VERTICAL);
        labelView.setPadding(dp(8), 0, 0, 0);
        item.addView(labelView, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        item.setOnClickListener(clicked -> {
            navOpen = false;
            if (targetScreen == SCREEN_MENU) {
                showMainMenu();
            } else if (targetScreen == SCREEN_CALCULATOR) {
                showCalculatorScreen();
            } else if (targetScreen == SCREEN_HISTORY) {
                showHistoryScreen();
            } else {
                showAboutScreen();
            }
        });
        item.setOnTouchListener((target, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                target.animate().scaleX(0.97f).scaleY(0.97f).setDuration(90).start();
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                target.animate().scaleX(1f).scaleY(1f).setDuration(120).start();
            }
            return false;
        });
        return item;
    }

    private TextView navGlyph(String icon, int textColor) {
        TextView view = text(icon, 22, textColor, true);
        view.setGravity(Gravity.CENTER);
        view.setIncludeFontPadding(false);
        return view;
    }

    private TextView iconButton(String icon, int background, int textColor) {
        TextView view = text(icon, 24, textColor, true);
        view.setGravity(Gravity.CENTER);
        view.setBackground(rounded(background, dp(8)));
        view.setOnTouchListener((target, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                target.animate().scaleX(0.94f).scaleY(0.94f).setDuration(90).start();
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                target.animate().scaleX(1f).scaleY(1f).setDuration(120).start();
            }
            return false;
        });
        return view;
    }

    private void toggleNavDrawer() {
        if (navDrawer == null) {
            return;
        }
        navOpen = !navOpen;
        navDrawer.setVisibility(navOpen ? View.VISIBLE : View.GONE);
        if (navOpen) {
            animateView(navDrawer, 0);
        }
    }

    private View menuSummary() {
        LinearLayout content = section("Panel técnico");
        content.addView(body("Abre la navegación con el icono superior y trabaja por módulos. La consulta está pensada para cálculos de termodinámica, fluidos y transferencia de calor."), matchWrap(0, dp(4), 0, 12));
        content.addView(metricsRow(), matchWrap(0, 0, 0, 12));
        content.addView(feature("Base de consulta", "agua, aire seco y vapor saturado con interpolación cúbica."));
        content.addView(feature("Lectura técnica", "cada propiedad incluye valor, unidad, explicación y tendencia gráfica."));
        content.addView(feature("Entrega", "el reporte PDF conserva el orden de resultados y gráficos por propiedad."));
        return card(content);
    }

    private View metricsRow() {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.addView(metric("3", "fluidos"), weightedButton(0, 0, 5, 0));
        row.addView(metric("PDF", "reporte"), weightedButton(5, 0, 5, 0));
        row.addView(metric("ƒ", "curvas"), weightedButton(5, 0, 0, 0));
        return row;
    }

    private View metric(String value, String label) {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setGravity(Gravity.CENTER);
        box.setPadding(dp(8), dp(10), dp(8), dp(10));
        box.setBackground(roundedStroke(COLOR_SURFACE_2, COLOR_BORDER, dp(8)));
        TextView valueView = text(value, 18, COLOR_PRIMARY, true);
        valueView.setGravity(Gravity.CENTER);
        box.addView(valueView, compactWrap());
        TextView labelView = text(label, 11, COLOR_MUTED, false);
        labelView.setGravity(Gravity.CENTER);
        box.addView(labelView, compactWrap());
        return box;
    }

    private void showCalculatorScreen() {
        currentScreen = SCREEN_CALCULATOR;
        navOpen = false;
        pageLayout.removeAllViews();
        pageLayout.addView(topBar("Calcular"), matchWrap(0, 0, 0, 12));
        pageLayout.addView(screenHeader("Calcular", "Consulta propiedades y revisa gráficos individuales por resultado."), matchWrap(0, 0, 0, 14));
        pageLayout.addView(calculatorSection(), matchWrap(0, 0, 0, 14));

        tableSpinner.setOnItemSelectedListener(null);
        setSpinnerToTable(lastTableKey);
        updateSelectedTable(false);
        if (!Double.isNaN(lastTemperature)) {
            temperatureInput.setText(String.format(Locale.US, "%.2f", lastTemperature));
        }
        final boolean[] skipInitialSelection = {true};
        tableSpinner.post(() -> tableSpinner.setOnItemSelectedListener(new SimpleItemSelectedListener(() -> {
            if (skipInitialSelection[0]) {
                skipInitialSelection[0] = false;
                return;
            }
            updateSelectedTable(true);
        })));
        animateIntro();
    }

    private void showHistoryScreen() {
        currentScreen = SCREEN_HISTORY;
        navOpen = false;
        pageLayout.removeAllViews();
        pageLayout.addView(topBar("Historial"), matchWrap(0, 0, 0, 12));
        pageLayout.addView(screenHeader("Historial", "Reabre, exporta o elimina cálculos guardados."), matchWrap(0, 0, 0, 14));
        historyLayout = new LinearLayout(this);
        historyLayout.setOrientation(LinearLayout.VERTICAL);
        pageLayout.addView(card(historyLayout), matchWrap(0, 0, 0, 0));
        renderHistory();
        animateIntro();
    }

    private void showAboutScreen() {
        currentScreen = SCREEN_ABOUT;
        navOpen = false;
        pageLayout.removeAllViews();
        pageLayout.addView(topBar("Acerca de"), matchWrap(0, 0, 0, 12));
        pageLayout.addView(screenHeader("Acerca de", "Información académica y alcance del software."), matchWrap(0, 0, 0, 14));
        pageLayout.addView(purposeSection(), matchWrap(0, 0, 0, 14));
        pageLayout.addView(authorsSection(), matchWrap(0, 0, 0, 0));
        animateIntro();
    }

    private void showResultsScreen() {
        navOpen = false;
        pageLayout.removeAllViews();
        pageLayout.addView(resultsTopBar(), matchWrap(0, 0, 0, 12));
        pageLayout.addView(screenHeader("Resultados", "Valores interpolados y gráficos por propiedad."), matchWrap(0, 0, 0, 14));

        resultsLayout = new LinearLayout(this);
        resultsLayout.setOrientation(LinearLayout.VERTICAL);
        pageLayout.addView(card(resultsLayout), matchWrap(0, 0, 0, 0));
        renderCurrentResults();
        animateIntro();
    }

    private View resultsTopBar() {
        LinearLayout bar = new LinearLayout(this);
        bar.setOrientation(LinearLayout.HORIZONTAL);
        bar.setGravity(Gravity.CENTER_VERTICAL);
        bar.setPadding(dp(12), dp(10), dp(12), dp(10));
        bar.setBackground(roundedStroke(COLOR_SURFACE, COLOR_BORDER, dp(8)));

        BackArrowView back = new BackArrowView(this);
        back.setBackground(rounded(COLOR_SURFACE_2, dp(8)));
        back.setOnClickListener(view -> showCalculatorScreen());
        bar.addView(back, new LinearLayout.LayoutParams(dp(48), dp(48)));

        TextView titleView = text("Resultados", 18, COLOR_TEXT, true);
        titleView.setGravity(Gravity.CENTER_VERTICAL);
        titleView.setPadding(dp(12), 0, 0, 0);
        bar.addView(titleView, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        ImageView logo = new ImageView(this);
        logo.setImageResource(getResources().getIdentifier("ic_thermowences_logo", "drawable", getPackageName()));
        bar.addView(logo, new LinearLayout.LayoutParams(dp(40), dp(40)));
        return bar;
    }

    private View screenHeader(String title, String subtitle) {
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);

        TextView titleView = text(title, 26, COLOR_TEXT, true);
        content.addView(titleView, compactWrap());
        content.addView(body(subtitle), matchWrap(0, dp(4), 0, 0));
        return card(content);
    }

    private void setSpinnerToTable(String tableKey) {
        if (tableSpinner == null || tableKey == null || tableKey.isEmpty()) {
            return;
        }
        for (int i = 0; i < TABLES.length; i++) {
            if (TABLES[i].key.equals(tableKey)) {
                tableSpinner.setSelection(i);
                return;
            }
        }
    }

    private View heroSection() {
        LinearLayout hero = new LinearLayout(this);
        hero.setOrientation(LinearLayout.VERTICAL);
        hero.setPadding(dp(18), dp(18), dp(18), dp(16));
        hero.setBackground(gradient(0xFF12191D, 0xFF224943, dp(8)));
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
        brandText.addView(text("Propiedades térmicas para ingeniería", 14, COLOR_MUTED, false), compactWrap());
        brandRow.addView(brandText, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        hero.addView(brandRow, matchWrap(0, 0, 0, 14));

        EngineeringHeroVisual visual = new EngineeringHeroVisual(this);
        visual.setBackground(roundedStroke(0x2210181B, 0x334A7771, dp(8)));
        hero.addView(visual, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(174)));

        TextView intro = text(
            "Consulta propiedades térmicas con resultados trazables, gráficos por propiedad y reportes listos para revisión técnica.",
            15,
            COLOR_TEXT,
            false
        );
        intro.setLineSpacing(0, 1.08f);
        hero.addView(intro, matchWrap(0, dp(14), 0, 0));
        return hero;
    }

    private View purposeSection() {
        LinearLayout content = section("Para qué sirve");
        content.addView(body(
            "TermoWences ayuda a obtener propiedades térmicas a partir de una temperatura. " +
            "Elige el fluido, ingresa el valor y revisa los resultados listos para usar en tus cálculos."
        ), compactWrap());
        content.addView(feature("Uso principal", "Consulta rápida para ejercicios de termodinámica, fluidos y transferencia de calor."));
        content.addView(feature("Entrada simple", "Solo necesitas seleccionar el material e ingresar la temperatura."));
        content.addView(feature("Salida ordenada", "Los resultados se muestran con nombre, valor y unidad."));
        return card(content);
    }

    private View authorsSection() {
        LinearLayout content = section("Autores");
        content.addView(body("Desarrollado para apoyar el análisis de propiedades térmicas en ejercicios de ingeniería."), compactWrap());
        content.addView(author("Wenceslao T. Medina Espinoza"));
        content.addView(author("Miguel A. Molina Mansilla"));
        content.addView(author("Edward Torres Cruz"));
        content.addView(author("Alica Leon Tacca"));
        return card(content);
    }

    private View calculatorSection() {
        LinearLayout content = section("Calcular");
        content.addView(body("Selecciona el fluido, confirma el rango permitido e ingresa la temperatura."), matchWrap(0, dp(4), 0, 12));

        tableSpinner = new Spinner(this);
        String[] titles = new String[TABLES.length];
        for (int i = 0; i < TABLES.length; i++) {
            titles[i] = TABLES[i].title;
        }
        ArrayAdapter<String> adapter = spinnerAdapter(titles);
        tableSpinner.setAdapter(adapter);
        tableSpinner.setBackground(roundedStroke(COLOR_SURFACE_2, COLOR_BORDER, dp(8)));
        content.addView(tableSpinner, matchWrap(0, dp(10), 0, 8));

        tableDescription = body("");
        tableDescription.setPadding(dp(12), dp(10), dp(12), dp(10));
        tableDescription.setBackground(roundedStroke(0xFF1D262A, COLOR_BORDER, dp(8)));
        content.addView(tableDescription, matchWrap(0, 0, 0, 8));

        tableRange = text("", 14, COLOR_PRIMARY, true);
        tableRange.setGravity(Gravity.CENTER);
        tableRange.setPadding(dp(10), dp(8), dp(10), dp(8));
        tableRange.setBackground(roundedStroke(0xFF242416, COLOR_ACCENT, dp(8)));
        content.addView(tableRange, matchWrap(0, 0, 0, 12));

        inputLabel = text("", 14, COLOR_MUTED, true);
        content.addView(inputLabel, compactWrap());

        temperatureInput = new EditText(this);
        temperatureInput.setHint("Ingrese temperatura");
        temperatureInput.setSingleLine(true);
        temperatureInput.setTextSize(18);
        temperatureInput.setTextColor(COLOR_TEXT);
        temperatureInput.setHintTextColor(COLOR_MUTED);
        temperatureInput.setPadding(dp(12), 0, dp(12), 0);
        temperatureInput.setBackground(roundedStroke(COLOR_SURFACE_2, COLOR_BORDER, dp(8)));
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

        Button calculate = button("Calcular", COLOR_PRIMARY, 0xFF07100F);
        calculate.setTextSize(16);
        calculate.setOnClickListener(view -> calculate());
        content.addView(calculate, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(54)));

        Button clear = button("Limpiar entrada", COLOR_SURFACE_2, COLOR_TEXT);
        clear.setOnClickListener(view -> clearResults());
        content.addView(clear, matchWrap(0, dp(10), 0, 0));
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
        view.setTextSize(14);
        view.setPadding(dp(12), dp(10), dp(12), dp(10));
        view.setBackground(roundedStroke(COLOR_SURFACE_2, COLOR_BORDER, dp(8)));
        return wrapWithMargin(view, 0, dp(8), 0, 0);
    }

    private View author(String name) {
        TextView view = text(name, 15, COLOR_TEXT, true);
        view.setPadding(dp(12), dp(9), dp(12), dp(9));
        view.setBackground(roundedStroke(COLOR_SURFACE_2, COLOR_BORDER, dp(8)));
        return wrapWithMargin(view, 0, dp(8), 0, 0);
    }

    private void updateSelectedTable() {
        updateSelectedTable(true);
    }

    private void updateSelectedTable(boolean resetResults) {
        TableSpec spec = selectedSpec();
        PropertyTable table = tables.get(spec.key);
        inputLabel.setText(spec.inputLabel);
        tableDescription.setText(descriptionFor(spec.key));
        if (table != null) {
            tableRange.setText(String.format(Locale.US, "Rango disponible: %.2f C a %.2f C", table.minTemperature(), table.maxTemperature()));
        }
        if (resetResults) {
            temperatureInput.setText("");
            clearResults();
        }
        animateView(tableDescription, 0);
        animateView(tableRange, 80);
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
        String title = String.format(Locale.US, "%s a %.2f C", spec.title, temperature);

        List<String> lines = new ArrayList<>();
        lines.add("Temperatura: " + String.format(Locale.US, "%.2f C", temperature));
        List<ResultEntry> entries = new ArrayList<>();

        for (PropertySpec prop : spec.properties) {
            Double value = values.get(prop.column);
            if (value == null) {
                continue;
            }
            String unit = prop.unit.isEmpty() ? "" : " " + prop.unit;
            String formatted = String.format(Locale.US, "%.4f%s", value, unit);
            String explanation = explanationForProperty(prop);
            ResultEntry entry = new ResultEntry(spec.key, prop.column, prop.label, formatted, explanation, prop.unit, temperature);
            lines.add(prop.label + ": " + formatted);
            entries.add(entry);
        }
        lastPdfTitle = title;
        lastTableKey = spec.key;
        lastPdfLines.clear();
        lastPdfLines.addAll(lines);
        lastPdfEntries.clear();
        lastPdfEntries.addAll(entries);
        lastTemperature = temperature;
        addHistory(title, lines, entries, spec.key, temperature);
        showResultsScreen();
    }

    private void clearResults() {
        if (temperatureInput != null) {
            temperatureInput.setText("");
        }
        if (resultsLayout != null) {
            resultsLayout.removeAllViews();
        }
        lastPdfTitle = "";
        lastTableKey = "";
        lastPdfLines.clear();
        lastPdfEntries.clear();
        lastTemperature = Double.NaN;
    }

    private void renderCurrentResults() {
        if (resultsLayout == null) {
            return;
        }
        resultsLayout.removeAllViews();

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.VERTICAL);
        header.addView(text(lastPdfTitle, 15, COLOR_PRIMARY, true), matchWrap(0, dp(4), 0, 0));
        resultsLayout.addView(header, matchWrap(0, 0, 0, 10));

        for (int i = 0; i < lastPdfEntries.size(); i++) {
            View row = resultRow(lastPdfEntries.get(i));
            resultsLayout.addView(row, matchWrap(0, 0, 0, 10));
            animateView(row, i * 45L);
        }

        exportPdfButton = button("Exportar PDF", COLOR_ACCENT, 0xFF171208);
        exportPdfButton.setOnClickListener(view -> exportPdf());
        resultsLayout.addView(exportPdfButton, matchWrap(0, dp(2), 0, 0));
    }

    private View resultRow(ResultEntry entry) {
        LinearLayout row = new LinearLayout(this);
        String tableKey = tableKeyForEntry(entry);
        row.setOrientation(LinearLayout.VERTICAL);
        row.setPadding(dp(12), dp(12), dp(12), dp(12));
        row.setBackground(gradient(0xFF1B2327, 0xFF222B30, dp(8)));
        row.setElevation(dp(1));
        row.addView(text(entry.label, 14, COLOR_MUTED, false), compactWrap());
        row.addView(text(entry.value, 18, chartColorFor(tableKey), true), compactWrap());
        TextView help = text(entry.explanation, 13, COLOR_MUTED, false);
        help.setPadding(0, dp(6), 0, 0);
        row.addView(help, compactWrap());
        PropertyTable table = tables.get(tableKey);
        PropertySpec prop = propertyForEntry(entry);
        if (table != null && prop != null && !Double.isNaN(entry.temperature)) {
            ThermoChartView miniChart = new ThermoChartView(this);
            miniChart.setCompact(true);
            miniChart.setBackground(roundedStroke(0xFF141A1D, COLOR_BORDER, dp(8)));
            double[] range = focusedRange(table, entry.temperature);
            miniChart.setData(table, prop, range[0], range[1], entry.temperature, chartColorFor(tableKey));
            LinearLayout.LayoutParams chartParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(132));
            chartParams.setMargins(0, dp(10), 0, 0);
            row.addView(miniChart, chartParams);
        }
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
        wrapper.setBackground(roundedStroke(COLOR_SURFACE, COLOR_BORDER, dp(8)));
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

    private ArrayAdapter<String> spinnerAdapter(String[] values) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, values) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getView(position, convertView, parent);
                view.setTextColor(COLOR_TEXT);
                view.setTextSize(15);
                view.setPadding(dp(12), 0, dp(12), 0);
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getDropDownView(position, convertView, parent);
                view.setTextColor(COLOR_TEXT);
                view.setTextSize(15);
                view.setBackgroundColor(COLOR_SURFACE_2);
                view.setPadding(dp(14), dp(12), dp(14), dp(12));
                return view;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

    private Button button(String value, int background, int textColor) {
        Button button = new Button(this);
        button.setText(value);
        button.setTextColor(textColor);
        button.setTextSize(14);
        button.setAllCaps(false);
        button.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        button.setBackground(rounded(background, dp(8)));
        button.setOnTouchListener((view, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                view.animate().scaleX(0.97f).scaleY(0.97f).setDuration(90).start();
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                view.animate().scaleX(1f).scaleY(1f).setDuration(120).start();
            }
            return false;
        });
        return button;
    }

    private void addHistory(String title, List<String> lines, List<ResultEntry> entries, String tableKey, double temperature) {
        String time = new SimpleDateFormat("HH:mm", Locale.US).format(new Date());
        history.add(0, new CalculationRecord(time, title, new ArrayList<>(lines), new ArrayList<>(entries), tableKey, temperature));
        while (history.size() > 8) {
            history.remove(history.size() - 1);
        }
        saveHistory();
        renderHistory();
    }

    private void renderHistory() {
        if (historyLayout == null) {
            return;
        }
        historyLayout.removeAllViews();
        historyLayout.addView(text("Historial de cálculos", 20, COLOR_TEXT, true), matchWrap(0, 0, 0, 8));
        if (history.isEmpty()) {
            historyLayout.addView(body("Aún no hay cálculos guardados."), compactWrap());
            return;
        }
        Button clearAll = button("Borrar todo", COLOR_SURFACE_2, COLOR_DANGER);
        clearAll.setOnClickListener(view -> clearHistory());
        historyLayout.addView(clearAll, matchWrap(0, 0, 0, 10));
        for (CalculationRecord record : history) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setPadding(0, dp(8), 0, 0);

            TextView item = text(record.time + "  " + record.title, 14, COLOR_TEXT, true);
            item.setPadding(dp(12), dp(10), dp(12), dp(10));
            item.setBackground(roundedStroke(COLOR_SURFACE_2, COLOR_BORDER, dp(8)));
            row.addView(item, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

            Button viewButton = button("Ver", COLOR_PRIMARY, 0xFF07100F);
            viewButton.setOnClickListener(view -> showHistoryRecord(record));
            LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(dp(58), dp(44));
            viewParams.setMargins(dp(8), 0, 0, 0);
            row.addView(viewButton, viewParams);

            Button delete = button("Borrar", 0xFF2A1B1D, COLOR_DANGER);
            delete.setOnClickListener(view -> deleteHistoryRecord(record));
            LinearLayout.LayoutParams deleteParams = new LinearLayout.LayoutParams(dp(78), dp(44));
            deleteParams.setMargins(dp(6), 0, 0, 0);
            row.addView(delete, deleteParams);
            historyLayout.addView(row, compactWrap());
        }
    }

    private void showHistoryRecord(CalculationRecord record) {
        lastTableKey = record.tableKey;
        lastTemperature = Double.isNaN(record.temperature) ? extractTemperature(record.lines) : record.temperature;
        lastPdfTitle = record.title;
        lastPdfLines.clear();
        lastPdfLines.addAll(record.lines);
        lastPdfEntries.clear();
        lastPdfEntries.addAll(record.entries);
        showResultsScreen();
    }

    private void deleteHistoryRecord(CalculationRecord record) {
        history.remove(record);
        saveHistory();
        renderHistory();
        showMessage("Elemento eliminado del historial.");
    }

    private void clearHistory() {
        history.clear();
        saveHistory();
        renderHistory();
        showMessage("Historial borrado.");
    }

    private void loadHistory() {
        history.clear();
        SharedPreferences prefs = getSharedPreferences(HISTORY_PREFS, MODE_PRIVATE);
        String raw = prefs.getString(HISTORY_KEY, "");
        if (raw == null || raw.trim().isEmpty()) {
            return;
        }
        String[] records = raw.split(RECORD_SEPARATOR, -1);
        for (String record : records) {
            if (record.trim().isEmpty()) {
                continue;
            }
            String[] fields = record.split(FIELD_SEPARATOR, -1);
            if (fields.length < 4) {
                continue;
            }
            String title = unescape(fields[1]);
            List<String> lines = splitStoredList(fields[2]);
            String tableKey = fields.length >= 5 ? unescape(fields[4]) : inferTableKey(title);
            double temperature = fields.length >= 6 ? parseStoredDouble(unescape(fields[5])) : extractTemperature(lines);
            List<ResultEntry> entries = decodeEntries(fields[3], tableKey, temperature);
            history.add(new CalculationRecord(unescape(fields[0]), title, lines, entries, tableKey, temperature));
        }
    }

    private void saveHistory() {
        StringBuilder builder = new StringBuilder();
        for (CalculationRecord record : history) {
            if (builder.length() > 0) {
                builder.append(RECORD_SEPARATOR);
            }
            builder.append(escape(record.time)).append(FIELD_SEPARATOR)
                .append(escape(record.title)).append(FIELD_SEPARATOR)
                .append(joinStoredList(record.lines)).append(FIELD_SEPARATOR)
                .append(encodeEntries(record.entries)).append(FIELD_SEPARATOR)
                .append(escape(record.tableKey)).append(FIELD_SEPARATOR)
                .append(escape(String.format(Locale.US, "%.6f", record.temperature)));
        }
        getSharedPreferences(HISTORY_PREFS, MODE_PRIVATE)
            .edit()
            .putString(HISTORY_KEY, builder.toString())
            .apply();
    }

    private String joinStoredList(List<String> values) {
        StringBuilder builder = new StringBuilder();
        for (String value : values) {
            if (builder.length() > 0) {
                builder.append(LINE_SEPARATOR);
            }
            builder.append(escape(value));
        }
        return builder.toString();
    }

    private List<String> splitStoredList(String raw) {
        List<String> values = new ArrayList<>();
        if (raw == null || raw.isEmpty()) {
            return values;
        }
        for (String value : raw.split(LINE_SEPARATOR, -1)) {
            values.add(unescape(value));
        }
        return values;
    }

    private String encodeEntries(List<ResultEntry> entries) {
        List<String> rows = new ArrayList<>();
        for (ResultEntry entry : entries) {
            rows.add(escape(entry.tableKey) + "|" +
                escape(entry.column) + "|" +
                escape(entry.label) + "|" +
                escape(entry.value) + "|" +
                escape(entry.explanation) + "|" +
                escape(entry.unit) + "|" +
                escape(String.format(Locale.US, "%.6f", entry.temperature)));
        }
        return joinStoredList(rows);
    }

    private List<ResultEntry> decodeEntries(String raw, String defaultTableKey, double defaultTemperature) {
        List<ResultEntry> entries = new ArrayList<>();
        for (String row : splitStoredList(raw)) {
            String[] fields = row.split("\\|", -1);
            if (fields.length >= 7) {
                entries.add(new ResultEntry(
                    unescape(fields[0]),
                    unescape(fields[1]),
                    unescape(fields[2]),
                    unescape(fields[3]),
                    unescape(fields[4]),
                    unescape(fields[5]),
                    parseStoredDouble(unescape(fields[6]))
                ));
            } else if (fields.length >= 3) {
                String label = unescape(fields[0]);
                PropertySpec prop = propertyForLabel(defaultTableKey, label);
                entries.add(new ResultEntry(
                    defaultTableKey,
                    prop == null ? "" : prop.column,
                    label,
                    unescape(fields[1]),
                    unescape(fields[2]),
                    prop == null ? "" : prop.unit,
                    defaultTemperature
                ));
            }
        }
        return entries;
    }

    private String escape(String value) {
        return value
            .replace("\\", "\\\\")
            .replace(RECORD_SEPARATOR, "\\r")
            .replace(FIELD_SEPARATOR, "\\f")
            .replace(LINE_SEPARATOR, "\\l")
            .replace("|", "\\p");
    }

    private String unescape(String value) {
        StringBuilder builder = new StringBuilder();
        boolean escaped = false;
        for (int i = 0; i < value.length(); i++) {
            char current = value.charAt(i);
            if (escaped) {
                if (current == 'r') {
                    builder.append(RECORD_SEPARATOR);
                } else if (current == 'f') {
                    builder.append(FIELD_SEPARATOR);
                } else if (current == 'l') {
                    builder.append(LINE_SEPARATOR);
                } else if (current == 'p') {
                    builder.append('|');
                } else {
                    builder.append(current);
                }
                escaped = false;
            } else if (current == '\\') {
                escaped = true;
            } else {
                builder.append(current);
            }
        }
        if (escaped) {
            builder.append('\\');
        }
        return builder.toString();
    }

    private void exportPdf() {
        if (lastPdfLines.isEmpty()) {
            showMessage("Primero realiza un cálculo para exportar.");
            return;
        }
        String fileName = "TermoWences_" + new SimpleDateFormat("yyyyMMdd_HHmm", Locale.US).format(new Date()) + ".pdf";
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_TITLE, fileName);
        startActivityForResult(intent, REQUEST_CREATE_PDF);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CREATE_PDF && resultCode == RESULT_OK && data != null && data.getData() != null) {
            writePdf(data.getData());
        }
    }

    private void writePdf(Uri uri) {
        PdfDocument document = new PdfDocument();
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        PdfPageState state = startPdfPage(document, paint, 1);
        state.y = drawPdfSummary(state.canvas, paint, state.y);

        for (ResultEntry entry : lastPdfEntries) {
            int blockHeight = pdfResultBlockHeight(entry);
            if (state.y + blockHeight > 804) {
                document.finishPage(state.page);
                state = startPdfPage(document, paint, state.pageNumber + 1);
            }
            state.y = drawPdfResultBlock(state.canvas, paint, entry, state.y) + 12;
        }
        document.finishPage(state.page);

        try (OutputStream output = getContentResolver().openOutputStream(uri)) {
            document.writeTo(output);
            showMessage("PDF exportado correctamente.");
        } catch (Exception exc) {
            showMessage("No se pudo exportar el PDF.");
        } finally {
            document.close();
        }
    }

    private PdfPageState startPdfPage(PdfDocument document, Paint paint, int pageNumber) {
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, pageNumber).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(COLOR_BG);
        canvas.drawRect(0, 0, 595, 842, paint);
        int y = 46;
        drawPdfLogo(canvas, paint, 42, 24);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(COLOR_PRIMARY);
        paint.setTextSize(24);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("TermoWences", 92, y, paint);

        y += 26;
        paint.setTypeface(Typeface.DEFAULT);
        paint.setColor(COLOR_MUTED);
        paint.setTextSize(12);
        canvas.drawText("Reporte técnico de propiedades térmicas", 92, y, paint);

        paint.setColor(COLOR_BORDER);
        paint.setStrokeWidth(1);
        canvas.drawLine(42, 90, 552, 90, paint);
        return new PdfPageState(page, canvas, pageNumber, 118);
    }

    private int drawPdfSummary(Canvas canvas, Paint paint, int startY) {
        int y = startY;
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(COLOR_TEXT);
        paint.setTextSize(18);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText(lastPdfTitle, 42, y, paint);

        y += 22;
        paint.setTypeface(Typeface.DEFAULT);
        paint.setTextSize(12);
        paint.setColor(COLOR_MUTED);
        canvas.drawText("Fecha: " + new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US).format(new Date()), 42, y, paint);
        if (!Double.isNaN(lastTemperature)) {
            y += 16;
            canvas.drawText(String.format(Locale.US, "Temperatura calculada: %.2f C", lastTemperature), 42, y, paint);
        }
        return y + 22;
    }

    private int pdfResultBlockHeight(ResultEntry entry) {
        int textLines = splitForPdf(entry.explanation, 70).size();
        return 68 + textLines * 12 + 124;
    }

    private int drawPdfResultBlock(Canvas canvas, Paint paint, ResultEntry entry, int startY) {
        int left = 42;
        int right = 552;
        int blockHeight = pdfResultBlockHeight(entry);
        int bottom = startY + blockHeight;

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(COLOR_SURFACE);
        canvas.drawRoundRect(left, startY, right, bottom, 8, 8, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1);
        paint.setColor(COLOR_BORDER);
        canvas.drawRoundRect(left, startY, right, bottom, 8, 8, paint);
        paint.setStyle(Paint.Style.FILL);

        String tableKey = tableKeyForEntry(entry);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paint.setTextSize(13);
        paint.setColor(COLOR_TEXT);
        canvas.drawText(trimForPdf(entry.label, 48), left + 12, startY + 20, paint);

        paint.setTextSize(16);
        paint.setColor(chartColorFor(tableKey));
        canvas.drawText(trimForPdf(entry.value, 40), left + 12, startY + 40, paint);

        paint.setTypeface(Typeface.DEFAULT);
        paint.setTextSize(10);
        paint.setColor(COLOR_MUTED);
        int textY = startY + 58;
        for (String part : splitForPdf(entry.explanation, 70)) {
            canvas.drawText(part, left + 12, textY, paint);
            textY += 12;
        }

        PropertyTable table = tables.get(tableKey);
        PropertySpec prop = propertyForEntry(entry);
        if (table != null && prop != null && !Double.isNaN(entry.temperature)) {
            double[] range = focusedRange(table, entry.temperature);
            List<Double> xValues = new ArrayList<>();
            List<Double> yValues = new ArrayList<>();
            fillChartValues(table, prop, range[0], range[1], xValues, yValues, 44);
            drawChartCanvas(
                canvas,
                paint,
                "",
                prop.unit,
                xValues,
                yValues,
                range[0],
                range[1],
                entry.temperature,
                table.valueFor(prop.column, entry.temperature),
                chartColorFor(tableKey),
                left + 10,
                textY + 6,
                right - 10,
                bottom - 10,
                true,
                1f
            );
        }
        return bottom;
    }

    private List<String> splitForPdf(String value, int maxLength) {
        List<String> parts = new ArrayList<>();
        String remaining = value;
        while (remaining.length() > maxLength) {
            int split = remaining.lastIndexOf(' ', maxLength);
            if (split <= 0) {
                split = maxLength;
            }
            parts.add(remaining.substring(0, split));
            remaining = remaining.substring(split).trim();
        }
        parts.add(remaining);
        return parts;
    }

    private void drawPdfLogo(Canvas canvas, Paint paint, int x, int y) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(COLOR_PRIMARY);
        canvas.drawRoundRect(x, y, x + 34, y + 34, 7, 7, paint);
        paint.setColor(COLOR_ACCENT);
        canvas.drawCircle(x + 11, y + 12, 6, paint);
        paint.setColor(0xFFFFFFFF);
        paint.setStrokeWidth(3);
        canvas.drawLine(x + 8, y + 25, x + 27, y + 25, paint);
        canvas.drawLine(x + 23, y + 8, x + 23, y + 23, paint);
        paint.setStyle(Paint.Style.FILL);
    }

    private String trimForPdf(String value, int max) {
        if (value.length() <= max) {
            return value;
        }
        return value.substring(0, Math.max(0, max - 3)) + "...";
    }

    private void drawPdfChart(Canvas canvas, Paint paint, int left, int top, int width, int height) {
        TableSpec spec = selectedSpec();
        PropertyTable table = tables.get(spec.key);
        if (table == null) {
            return;
        }
        PropertySpec prop = selectedGraphProperty();
        double[] range = selectedGraphRange(table);
        double minX = range[0];
        double maxX = range[1];
        List<Double> yValues = new ArrayList<>();
        int points = 42;
        double minY = Double.POSITIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < points; i++) {
            double x = minX + (maxX - minX) * i / (points - 1);
            double y = table.valueFor(prop.column, x);
            yValues.add(y);
            minY = Math.min(minY, y);
            maxY = Math.max(maxY, y);
        }
        if (Math.abs(maxY - minY) < 1e-12) {
            maxY += 1.0;
            minY -= 1.0;
        }

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(COLOR_TEXT);
        paint.setTextSize(13);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("Gráfico: " + prop.label, left, top, paint);

        int plotLeft = left + 38;
        int plotTop = top + 26;
        int plotRight = left + width;
        int plotBottom = top + height;
        paint.setTypeface(Typeface.DEFAULT);
        paint.setColor(COLOR_BORDER);
        paint.setStrokeWidth(1);
        for (int i = 0; i <= 4; i++) {
            float y = plotBottom - (plotBottom - plotTop) * i / 4f;
            canvas.drawLine(plotLeft, y, plotRight, y, paint);
        }

        Path chartPath = new Path();
        for (int i = 0; i < points; i++) {
            double x = minX + (maxX - minX) * i / (points - 1);
            float px = (float) (plotLeft + (x - minX) / (maxX - minX) * (plotRight - plotLeft));
            float py = (float) (plotBottom - (yValues.get(i) - minY) / (maxY - minY) * (plotBottom - plotTop));
            if (i == 0) {
                chartPath.moveTo(px, py);
            } else {
                chartPath.lineTo(px, py);
            }
        }
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        paint.setColor(chartColorFor(spec.key));
        canvas.drawPath(chartPath, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(9);
        paint.setColor(COLOR_MUTED);
        canvas.drawText(String.format(Locale.US, "%.0f C", minX), plotLeft, plotBottom + 13, paint);
        canvas.drawText(String.format(Locale.US, "%.0f C", maxX), plotRight - 32, plotBottom + 13, paint);
        canvas.drawText(String.format(Locale.US, "%.2g", maxY), left, plotTop + 4, paint);
        canvas.drawText(String.format(Locale.US, "%.2g", minY), left, plotBottom, paint);

        if (!Double.isNaN(lastTemperature) && lastTemperature >= minX && lastTemperature <= maxX) {
            double currentValue = table.valueFor(prop.column, lastTemperature);
            float px = (float) (plotLeft + (lastTemperature - minX) / (maxX - minX) * (plotRight - plotLeft));
            float py = (float) (plotBottom - (currentValue - minY) / (maxY - minY) * (plotBottom - plotTop));
            paint.setColor(COLOR_ACCENT);
            canvas.drawCircle(px, py, 5, paint);
            paint.setColor(COLOR_TEXT);
            canvas.drawText(String.format(Locale.US, "%.4f", currentValue), Math.min(px + 8, plotRight - 70), Math.max(plotTop + 10, py - 8), paint);
        }
    }

    private String descriptionFor(String key) {
        if ("water".equals(key)) {
            return "Agua: ideal para revisar propiedades como presión de saturación, densidad, viscosidad y Prandtl.";
        }
        if ("air".equals(key)) {
            return "Aire seco: útil para estimaciones de convección, difusividad, viscosidad y transferencia de calor.";
        }
        return "Vapor saturado: consulta propiedades de referencia como presión, densidades, entalpías y entropías.";
    }

    private double[] selectedGraphRange(PropertyTable table) {
        return new double[] {table.minTemperature(), table.maxTemperature()};
    }

    private int chartColorFor(String key) {
        if ("water".equals(key)) {
            return COLOR_SECONDARY;
        }
        if ("air".equals(key)) {
            return COLOR_PRIMARY;
        }
        return COLOR_ACCENT;
    }

    private PropertySpec selectedGraphProperty() {
        TableSpec spec = selectedSpec();
        return spec.properties[0];
    }

    private double[] focusedRange(PropertyTable table, double temperature) {
        double min = Math.max(table.minTemperature(), temperature - 35.0);
        double max = Math.min(table.maxTemperature(), temperature + 35.0);
        if (max - min < 20.0) {
            min = table.minTemperature();
            max = table.maxTemperature();
        }
        return new double[] {min, max};
    }

    private void fillChartValues(PropertyTable table, PropertySpec prop, double minTemperature, double maxTemperature, List<Double> xValues, List<Double> yValues, int points) {
        xValues.clear();
        yValues.clear();
        for (int i = 0; i < points; i++) {
            double x = minTemperature + (maxTemperature - minTemperature) * i / (points - 1);
            xValues.add(x);
            yValues.add(table.valueFor(prop.column, x));
        }
    }

    private PropertySpec propertyForEntry(ResultEntry entry) {
        if (entry == null) {
            return null;
        }
        String tableKey = tableKeyForEntry(entry);
        TableSpec spec = specForKey(tableKey);
        if (spec == null) {
            return null;
        }
        for (PropertySpec prop : spec.properties) {
            if (prop.column.equals(entry.column)) {
                return prop;
            }
        }
        return propertyForLabel(tableKey, entry.label);
    }

    private String tableKeyForEntry(ResultEntry entry) {
        if (entry != null && entry.tableKey != null && !entry.tableKey.isEmpty()) {
            return entry.tableKey;
        }
        return lastTableKey == null ? "" : lastTableKey;
    }

    private PropertySpec propertyForLabel(String tableKey, String label) {
        TableSpec spec = specForKey(tableKey);
        if (spec == null || label == null) {
            return null;
        }
        for (PropertySpec prop : spec.properties) {
            if (prop.label.equals(label)) {
                return prop;
            }
        }
        return null;
    }

    private TableSpec specForKey(String tableKey) {
        for (TableSpec spec : TABLES) {
            if (spec.key.equals(tableKey)) {
                return spec;
            }
        }
        return null;
    }

    private String inferTableKey(String title) {
        if (title == null) {
            return "";
        }
        for (TableSpec spec : TABLES) {
            if (title.startsWith(spec.title)) {
                return spec.key;
            }
        }
        return "";
    }

    private double parseStoredDouble(String value) {
        if (value == null || value.trim().isEmpty()) {
            return Double.NaN;
        }
        try {
            return Double.parseDouble(value.trim().replace(",", "."));
        } catch (NumberFormatException exc) {
            return Double.NaN;
        }
    }

    private double extractTemperature(List<String> lines) {
        if (lines.isEmpty()) {
            return Double.NaN;
        }
        String first = lines.get(0).replace("Temperatura:", "").replace("C", "").trim();
        try {
            return Double.parseDouble(first);
        } catch (NumberFormatException exc) {
            return Double.NaN;
        }
    }

    private String explanationForProperty(PropertySpec prop) {
        String label = prop.label.toLowerCase(Locale.US);
        if (label.contains("presión")) {
            return "Presión correspondiente al estado de saturación para esa temperatura.";
        }
        if (label.contains("expansión")) {
            return "Indica cuánto cambia el volumen del fluido al variar la temperatura.";
        }
        if (label.contains("densidad")) {
            return "Indica la masa contenida por unidad de volumen.";
        }
        if (label.contains("calor específico")) {
            return "Energía necesaria para elevar la temperatura de una unidad de masa.";
        }
        if (label.contains("conductividad")) {
            return "Mide la facilidad del material para conducir calor.";
        }
        if (label.contains("difusividad")) {
            return "Relaciona la rapidez con la que el calor se difunde en el material.";
        }
        if (label.contains("viscosidad absoluta")) {
            return "Describe la resistencia interna del fluido al movimiento.";
        }
        if (label.contains("viscosidad cinemática")) {
            return "Relaciona viscosidad y densidad para análisis de flujo.";
        }
        if (label.contains("entalpía")) {
            return "Representa energía térmica útil por unidad de masa.";
        }
        if (label.contains("entropía")) {
            return "Ayuda a evaluar cambios energéticos y dirección de procesos térmicos.";
        }
        return "Número adimensional usado en transferencia de calor por convección.";
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

    private LinearLayout.LayoutParams navItemWrap(int top) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(52));
        params.setMargins(0, top, 0, 0);
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

    private void drawChartCanvas(
        Canvas canvas,
        Paint paint,
        String title,
        String unit,
        List<Double> xValues,
        List<Double> yValues,
        double minX,
        double maxX,
        double markerX,
        double markerY,
        int lineColor,
        int leftBound,
        int topBound,
        int rightBound,
        int bottomBound,
        boolean compact,
        float scale
    ) {
        if (xValues.isEmpty() || yValues.isEmpty() || maxX <= minX) {
            return;
        }

        int titleSpace = title == null || title.isEmpty() ? unitSize(compact ? 8 : 12, scale) : unitSize(compact ? 24 : 34, scale);
        int left = leftBound + unitSize(compact ? 38 : 48, scale);
        int right = rightBound - unitSize(14, scale);
        int top = topBound + titleSpace;
        int bottom = bottomBound - unitSize(compact ? 26 : 34, scale);
        if (right <= left || bottom <= top) {
            return;
        }

        double minY = yValues.get(0);
        double maxY = yValues.get(0);
        for (double value : yValues) {
            minY = Math.min(minY, value);
            maxY = Math.max(maxY, value);
        }
        if (Math.abs(maxY - minY) < 1e-12) {
            maxY += 1.0;
            minY -= 1.0;
        }

        paint.setStyle(Paint.Style.FILL);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paint.setTextSize(unitSize(compact ? 10 : 13, scale));
        paint.setColor(COLOR_TEXT);
        if (title != null && !title.isEmpty()) {
            canvas.drawText(title, leftBound + unitSize(14, scale), topBound + unitSize(23, scale), paint);
        }

        paint.setTypeface(Typeface.DEFAULT);
        paint.setStrokeWidth(Math.max(1f, unitSize(1, scale)));
        paint.setColor(0xFFD8E8EC);
        int gridLines = compact ? 3 : 5;
        for (int i = 0; i <= gridLines; i++) {
            float y = bottom - (bottom - top) * i / (float) gridLines;
            canvas.drawLine(left, y, right, y, paint);
        }

        paint.setColor(0xFF4A5A60);
        paint.setStrokeWidth(Math.max(1f, unitSize(1.5f, scale)));
        canvas.drawLine(left, top, left, bottom, paint);
        canvas.drawLine(left, bottom, right, bottom, paint);

        Path linePath = new Path();
        Path fillPath = new Path();
        for (int i = 0; i < xValues.size(); i++) {
            float px = (float) (left + (xValues.get(i) - minX) / (maxX - minX) * (right - left));
            float py = (float) (bottom - (yValues.get(i) - minY) / (maxY - minY) * (bottom - top));
            if (i == 0) {
                linePath.moveTo(px, py);
                fillPath.moveTo(px, bottom);
                fillPath.lineTo(px, py);
            } else {
                linePath.lineTo(px, py);
                fillPath.lineTo(px, py);
            }
        }
        fillPath.lineTo(right, bottom);
        fillPath.close();

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(withAlpha(lineColor, compact ? 28 : 34));
        canvas.drawPath(fillPath, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(unitSize(compact ? 2.2f : 3f, scale));
        paint.setColor(lineColor);
        canvas.drawPath(linePath, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setTypeface(Typeface.DEFAULT);
        paint.setTextSize(unitSize(compact ? 8.5f : 10f, scale));
        paint.setColor(COLOR_MUTED);
        int xLabels = compact ? 2 : 4;
        for (int i = 0; i <= xLabels; i++) {
            float x = left + (right - left) * i / (float) xLabels;
            double labelValue = minX + (maxX - minX) * i / (double) xLabels;
            canvas.drawText(String.format(Locale.US, "%.0f", labelValue), x - unitSize(8, scale), bottomBound - unitSize(9, scale), paint);
        }
        canvas.drawText("C", right + unitSize(2, scale), bottomBound - unitSize(9, scale), paint);
        canvas.drawText(formatAxis(maxY, unit, compact), leftBound + unitSize(8, scale), top + unitSize(4, scale), paint);
        canvas.drawText(formatAxis(minY, unit, compact), leftBound + unitSize(8, scale), bottom, paint);

        if (!Double.isNaN(markerX) && !Double.isNaN(markerY) && markerX >= minX && markerX <= maxX) {
            float px = (float) (left + (markerX - minX) / (maxX - minX) * (right - left));
            float py = (float) (bottom - (markerY - minY) / (maxY - minY) * (bottom - top));
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(Math.max(1f, unitSize(1, scale)));
            paint.setColor(withAlpha(COLOR_TEXT, 70));
            canvas.drawLine(px, top, px, bottom, paint);

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(COLOR_ACCENT);
            canvas.drawCircle(px, py, unitSize(compact ? 4.5f : 6f, scale), paint);
            paint.setColor(COLOR_TEXT);
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            paint.setTextSize(unitSize(compact ? 8.5f : 10f, scale));
            String markerLabel = String.format(Locale.US, "%.4f", markerY);
            canvas.drawText(markerLabel, Math.min(px + unitSize(7, scale), right - unitSize(68, scale)), Math.max(top + unitSize(10, scale), py - unitSize(7, scale)), paint);
        }
        paint.setStyle(Paint.Style.FILL);
    }

    private String formatAxis(double value, String unit, boolean compact) {
        String suffix = unit.isEmpty() || compact ? "" : " " + unit;
        return String.format(Locale.US, "%.2g%s", value, suffix);
    }

    private int withAlpha(int color, int alpha) {
        return (color & 0x00FFFFFF) | (Math.max(0, Math.min(255, alpha)) << 24);
    }

    private int unitSize(float value, float scale) {
        return (int) (value * scale + 0.5f);
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

    private final class EngineeringHeroVisual extends View {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Path path = new Path();

        EngineeringHeroVisual(Context context) {
            super(context);
            setPadding(dp(12), dp(12), dp(12), dp(12));
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            int w = getWidth();
            int h = getHeight();
            float pad = dp(16);
            float left = pad;
            float top = pad;
            float right = w - pad;
            float bottom = h - pad;

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(0xFF12191D);
            canvas.drawRoundRect(left, top, right, bottom, dp(8), dp(8), paint);

            paint.setColor(0xFF172327);
            canvas.drawRoundRect(left + dp(12), top + dp(12), right - dp(12), bottom - dp(12), dp(8), dp(8), paint);

            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dp(3));
            paint.setColor(COLOR_PRIMARY);
            float pipeY = top + h * 0.58f;
            canvas.drawLine(left + dp(28), pipeY, right - dp(28), pipeY, paint);
            canvas.drawLine(left + dp(62), pipeY, left + dp(62), top + dp(48), paint);
            canvas.drawLine(right - dp(74), pipeY, right - dp(74), top + dp(52), paint);

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(0xFF2A353A);
            canvas.drawRoundRect(left + dp(44), top + dp(40), left + dp(112), pipeY + dp(12), dp(6), dp(6), paint);
            canvas.drawRoundRect(right - dp(134), top + dp(44), right - dp(50), pipeY + dp(12), dp(6), dp(6), paint);

            paint.setColor(COLOR_SECONDARY);
            for (int i = 0; i < 5; i++) {
                float x = left + dp(56) + i * dp(10);
                canvas.drawLine(x, top + dp(52), x, pipeY + dp(2), paint);
            }
            paint.setColor(COLOR_ACCENT);
            for (int i = 0; i < 6; i++) {
                float x = right - dp(122) + i * dp(11);
                canvas.drawCircle(x, top + dp(64) + (i % 2) * dp(10), dp(3), paint);
            }

            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dp(2));
            paint.setColor(COLOR_ACCENT);
            path.reset();
            for (int i = 0; i <= 40; i++) {
                float x = left + dp(34) + (right - left - dp(68)) * i / 40f;
                float y = bottom - dp(38) - (float) Math.sin(i / 40f * Math.PI * 2.1f) * dp(18) - i * 0.45f;
                if (i == 0) {
                    path.moveTo(x, y);
                } else {
                    path.lineTo(x, y);
                }
            }
            canvas.drawPath(path, paint);

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(0xFF0F1416);
            canvas.drawCircle(right - dp(52), top + dp(38), dp(24), paint);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dp(3));
            paint.setColor(COLOR_PRIMARY);
            canvas.drawCircle(right - dp(52), top + dp(38), dp(20), paint);
            canvas.drawLine(right - dp(52), top + dp(38), right - dp(38), top + dp(28), paint);

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(COLOR_MUTED);
            paint.setTextSize(dp(10));
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            canvas.drawText("THERMAL DATA", left + dp(26), bottom - dp(18), paint);
            paint.setColor(COLOR_PRIMARY);
            canvas.drawCircle(left + dp(24), top + dp(28), dp(5), paint);
            paint.setColor(COLOR_ACCENT);
            canvas.drawCircle(left + dp(42), top + dp(28), dp(5), paint);
        }
    }

    private final class BackArrowView extends View {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Path arrow = new Path();

        BackArrowView(Context context) {
            super(context);
            setPadding(dp(8), dp(8), dp(8), dp(8));
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float w = getWidth();
            float h = getHeight();
            float cx = w * 0.50f;
            float cy = h * 0.50f;
            float size = Math.min(w, h) * 0.30f;

            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(dp(3));
            paint.setColor(COLOR_TEXT);

            arrow.reset();
            arrow.moveTo(cx + size * 0.55f, cy - size);
            arrow.lineTo(cx - size * 0.55f, cy);
            arrow.lineTo(cx + size * 0.55f, cy + size);
            canvas.drawPath(arrow, paint);
            canvas.drawLine(cx - size * 0.45f, cy, cx + size * 1.10f, cy, paint);

            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dp(1));
            paint.setColor(withAlpha(COLOR_PRIMARY, 90));
            canvas.drawCircle(cx, cy, size * 1.55f, paint);
        }
    }

    private final class ThermoChartView extends View {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final List<Double> xValues = new ArrayList<>();
        private final List<Double> yValues = new ArrayList<>();
        private String title = "Seleccione una propiedad";
        private String unit = "";
        private double rangeMin = 0.0;
        private double rangeMax = 1.0;
        private double markerX = Double.NaN;
        private double markerY = Double.NaN;
        private int lineColor = COLOR_PRIMARY;
        private boolean compact = false;

        ThermoChartView(Context context) {
            super(context);
            setPadding(dp(12), dp(12), dp(12), dp(12));
        }

        void setCompact(boolean compact) {
            this.compact = compact;
        }

        void setData(PropertyTable table, PropertySpec prop, double minTemperature, double maxTemperature, double selectedTemperature, int color) {
            xValues.clear();
            yValues.clear();
            title = compact ? "" : prop.label;
            unit = prop.unit;
            rangeMin = minTemperature;
            rangeMax = maxTemperature;
            lineColor = color;
            markerX = Double.NaN;
            markerY = Double.NaN;

            fillChartValues(table, prop, minTemperature, maxTemperature, xValues, yValues, compact ? 42 : 52);
            if (!Double.isNaN(selectedTemperature) && selectedTemperature >= minTemperature && selectedTemperature <= maxTemperature) {
                markerX = selectedTemperature;
                markerY = table.valueFor(prop.column, selectedTemperature);
            }
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            int width = getWidth();
            int height = getHeight();

            if (xValues.isEmpty()) {
                paint.setStyle(Paint.Style.FILL);
                paint.setTypeface(Typeface.DEFAULT);
                paint.setColor(COLOR_MUTED);
                paint.setTextSize(dp(12));
                canvas.drawText("Sin datos para graficar", dp(14), height / 2f, paint);
                return;
            }
            drawChartCanvas(
                canvas,
                paint,
                title,
                unit,
                xValues,
                yValues,
                rangeMin,
                rangeMax,
                markerX,
                markerY,
                lineColor,
                0,
                0,
                width,
                height,
                compact,
                getResources().getDisplayMetrics().density
            );
        }
    }

    private static final class CalculationRecord {
        final String time;
        final String title;
        final List<String> lines;
        final List<ResultEntry> entries;
        final String tableKey;
        final double temperature;

        CalculationRecord(String time, String title, List<String> lines, List<ResultEntry> entries, String tableKey, double temperature) {
            this.time = time;
            this.title = title;
            this.lines = lines;
            this.entries = entries;
            this.tableKey = tableKey == null ? "" : tableKey;
            this.temperature = temperature;
        }
    }

    private static final class PdfPageState {
        final PdfDocument.Page page;
        final Canvas canvas;
        final int pageNumber;
        int y;

        PdfPageState(PdfDocument.Page page, Canvas canvas, int pageNumber, int y) {
            this.page = page;
            this.canvas = canvas;
            this.pageNumber = pageNumber;
            this.y = y;
        }
    }

    private static final class ResultEntry {
        final String tableKey;
        final String column;
        final String label;
        final String value;
        final String explanation;
        final String unit;
        final double temperature;

        ResultEntry(String tableKey, String column, String label, String value, String explanation, String unit, double temperature) {
            this.tableKey = tableKey == null ? "" : tableKey;
            this.column = column == null ? "" : column;
            this.label = label;
            this.value = value;
            this.explanation = explanation;
            this.unit = unit == null ? "" : unit;
            this.temperature = temperature;
        }
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

        double valueFor(String column, double temperature) {
            return splines.get(column).valueAt(temperature);
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
