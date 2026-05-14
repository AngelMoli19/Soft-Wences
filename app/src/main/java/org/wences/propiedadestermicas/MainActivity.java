package org.wences.propiedadestermicas;

import android.app.Activity;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends Activity {
    private static final int COLOR_BG = 0xFF060C18;
    private static final int COLOR_SURFACE = 0xFF0D1628;
    private static final int COLOR_SURFACE_2 = 0xFF111E35;
    private static final int COLOR_BORDER = 0x554B8EF7;
    private static final int COLOR_TEXT = 0xFFEEF2FF;
    private static final int COLOR_MUTED = 0xAAB0C8E4;
    private static final int COLOR_PRIMARY = 0xFF4B8EF7;
    private static final int COLOR_SECONDARY = 0xFF06C8D8;
    private static final int COLOR_ACCENT = 0xFFE8A930;
    private static final int COLOR_DANGER = 0xFFEF4444;
    private static final int COLOR_VIOLET = 0xFFAB7DF8;
    private static final int SCREEN_MENU = 0;
    private static final int SCREEN_CALCULATOR = 1;
    private static final int SCREEN_HISTORY = 2;
    private static final int SCREEN_ABOUT = 3;
    private static final int SCREEN_RESULTS = 4;
    private static final int ICON_MENU = 0;
    private static final int ICON_HOME = 1;
    private static final int ICON_CALCULATE = 2;
    private static final int ICON_HISTORY = 3;
    private static final int ICON_INFO = 4;
    private static final int ICON_DATABASE = 5;
    private static final int ICON_READING = 6;
    private static final int ICON_REPORT = 7;
    private static final int ICON_CURVE = 8;
    private static final int ICON_DROPLET = 9;
    private static final int ICON_WIND = 10;
    private static final int ICON_CLOUD = 11;
    private static final int ICON_THERMOMETER = 12;
    private static final int ICON_TRASH = 13;
    private static final int ICON_DOTS = 14;
    private static final int ICON_EMPTY_HISTORY = 15;
    private static final int ICON_BOLT = 16;
    private static final int ICON_RULER = 17;
    private static final int ICON_LIST = 18;
    private static final int ICON_COPY = 19;
    private static final int ICON_CHECK = 20;
    private static final int ICON_CPU = 21;
    private static final int ICON_CHEVRON = 22;
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
    private FrameLayout rootLayout;
    private ScrollView mainScrollView;
    private LinearLayout pageLayout;
    private FrameLayout navBar;
    private TextView navTitleView;
    private boolean navHidden = false;
    private int lastScrollY = 0;
    private boolean enableNavScrollHide = false;
    private LinearLayout navDrawer;
    private View navScrim;
    private boolean navOpen = false;
    private FrameLayout bottomNavBar;
    private LinearLayout bottomNavRow;
    private final List<BottomNavItemView> bottomNavItems = new ArrayList<>();
    private int bottomNavInsetBottom = 0;
    private int systemStatusInsetTop = 0;
    private int currentScreen = SCREEN_MENU;
    private int selectedTableIndex = 0;
    private EditText temperatureInput;
    private FrameLayout segmentFrame;
    private LinearLayout segmentContainer;
    private View segmentIndicator;
    private final List<TextView> segmentButtons = new ArrayList<>();
    private LinearLayout fluidInfoCard;
    private TextView fluidTitleView;
    private TextView fluidDescriptionView;
    private LinearLayout fluidIconFrame;
    private LinearLayout rangeBadge;
    private TextView rangeBadgeText;
    private TextView rangeTooltip;
    private FrameLayout temperatureField;
    private TextView floatingLabel;
    private TextView temperatureSuffix;
    private TextView validationError;
    private TextView calculateCta;
    private ShimmerView calculateShimmer;
    private LinearLayout resultsLayout;
    private LinearLayout resultsSummaryHeader;
    private LinearLayout exportPdfButtonView;
    private TextView exportPdfText;
    private NavIconView exportPdfIcon;
    private SmallSpinnerView exportPdfSpinner;
    private LinearLayout historyLayout;
    private LinearLayout historyStickyHeader;
    private TextView historyStickyText;
    private final List<View> historyHeaderViews = new ArrayList<>();
    private PopupWindow historyMenuPopup;
    private FrameLayout bottomSheetOverlay;
    private final List<CalculationRecord> history = new ArrayList<>();
    private final List<View> pendingRevealViews = new ArrayList<>();
    private final List<String> lastPdfLines = new ArrayList<>();
    private final List<ResultEntry> lastPdfEntries = new ArrayList<>();
    private String lastPdfTitle = "";
    private String lastTableKey = "";
    private double lastTemperature = Double.NaN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= 30) {
            window.setDecorFitsSystemWindows(false);
        } else {
            window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            );
        }
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
        rootLayout = new FrameLayout(this);
        rootLayout.setBackgroundColor(COLOR_BG);
        rootLayout.setOnApplyWindowInsetsListener((view, insets) -> {
            int top = insets == null ? 0 : Math.max(0, insets.getSystemWindowInsetTop());
            applyStatusTopInset(top);
            return insets;
        });

        mainScrollView = new ScrollView(this);
        mainScrollView.setFillViewport(false);
        mainScrollView.setBackgroundColor(COLOR_BG);
        mainScrollView.setOnScrollChangeListener((view, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            checkScrollRevealViews();
            handleNavScroll(scrollY, oldScrollY);
            updateHistoryStickyHeader();
        });

        pageLayout = new LinearLayout(this);
        pageLayout.setOrientation(LinearLayout.VERTICAL);
        pageLayout.setPadding(dp(16), dp(16), dp(16), dp(104));
        mainScrollView.addView(pageLayout);

        rootLayout.addView(mainScrollView, new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        ));

        historyStickyHeader = stickyHistoryHeaderView();
        historyStickyHeader.setVisibility(View.GONE);
        FrameLayout.LayoutParams stickyParams = new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            dp(28)
        );
        stickyParams.gravity = Gravity.TOP;
        stickyParams.topMargin = systemStatusInsetTop + dp(12);
        stickyParams.leftMargin = dp(30);
        stickyParams.rightMargin = dp(30);
        rootLayout.addView(historyStickyHeader, stickyParams);

        bottomNavBar = buildBottomNavBar();
        FrameLayout.LayoutParams bottomParams = new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            dp(84)
        );
        bottomParams.gravity = Gravity.BOTTOM;
        rootLayout.addView(bottomNavBar, bottomParams);

        setContentView(rootLayout);
        if (Build.VERSION.SDK_INT >= 20) {
            rootLayout.requestApplyInsets();
        }
        showMainMenu();
    }

    private void showMainMenu() {
        currentScreen = SCREEN_MENU;
        configureTopNav("Inicio", false, true);
        updateBottomNav();
        setResultsChromeVisible(false);
        setHomePagePadding();
        pendingRevealViews.clear();
        pageLayout.removeAllViews();
        pageLayout.addView(homeHeader(), matchWrap(0, 0, 0, 0));
        pageLayout.addView(heroSection(), matchWrap(dp(16), dp(20), dp(16), 0));
        pageLayout.addView(menuSummary(), matchWrap(dp(16), dp(16), dp(16), 0));
        animateIntro();
    }

    private FrameLayout buildTopNavBar() {
        FrameLayout bar = new FrameLayout(this);
        bar.setPadding(dp(14), 0, dp(14), 0);
        bar.setBackground(rounded(0xFF080F20, 0));

        View menu = topNavActionButton(false);
        menu.setOnClickListener(view -> {
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
            toggleNavDrawer();
        });
        FrameLayout.LayoutParams menuParams = new FrameLayout.LayoutParams(dp(44), dp(44));
        menuParams.gravity = Gravity.START | Gravity.CENTER_VERTICAL;
        bar.addView(menu, menuParams);

        navTitleView = text("", 14, COLOR_TEXT, false);
        navTitleView.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        navTitleView.setGravity(Gravity.CENTER);
        FrameLayout.LayoutParams titleParams = new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        );
        titleParams.leftMargin = dp(72);
        titleParams.rightMargin = dp(72);
        bar.addView(navTitleView, titleParams);

        View logo = navLogoView();
        FrameLayout.LayoutParams logoParams = new FrameLayout.LayoutParams(dp(30), dp(30));
        logoParams.gravity = Gravity.END | Gravity.CENTER_VERTICAL;
        bar.addView(logo, logoParams);
        addNavBottomBorder(bar);
        return bar;
    }

    private FrameLayout buildBottomNavBar() {
        BottomNavShell shell = new BottomNavShell(this);
        shell.setPadding(0, dp(20), 0, 0);
        shell.setOnApplyWindowInsetsListener((view, insets) -> {
            bottomNavInsetBottom = insets == null ? 0 : Math.max(0, insets.getSystemWindowInsetBottom());
            view.setPadding(0, dp(20), 0, 0);
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
            if (params != null) {
                params.height = dp(84) + bottomNavInsetBottom;
                view.setLayoutParams(params);
            }
            updateBottomNavContentPadding();
            setPagePaddingForCurrentScreen();
            return insets;
        });

        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setGravity(Gravity.BOTTOM);
        container.setPadding(dp(8), 0, dp(8), bottomNavInsetBottom);
        container.setBackground(bottomNavBackground());

        bottomNavRow = new LinearLayout(this);
        bottomNavRow.setOrientation(LinearLayout.HORIZONTAL);
        bottomNavRow.setGravity(Gravity.CENTER);
        bottomNavRow.addView(bottomNavItem(ICON_HOME, "Inicio", SCREEN_MENU), new LinearLayout.LayoutParams(0, dp(64), 1));
        bottomNavRow.addView(bottomNavItem(ICON_CURVE, "Calcular", SCREEN_CALCULATOR), new LinearLayout.LayoutParams(0, dp(64), 1));
        bottomNavRow.addView(bottomNavItem(ICON_HISTORY, "Historial", SCREEN_HISTORY), new LinearLayout.LayoutParams(0, dp(64), 1));
        bottomNavRow.addView(bottomNavItem(ICON_INFO, "Acerca de", SCREEN_ABOUT), new LinearLayout.LayoutParams(0, dp(64), 1));
        container.addView(bottomNavRow, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(64)));

        FrameLayout.LayoutParams containerParams = new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            dp(64) + bottomNavInsetBottom
        );
        containerParams.gravity = Gravity.BOTTOM;
        shell.addView(container, containerParams);
        if (Build.VERSION.SDK_INT >= 20) {
            shell.post(shell::requestApplyInsets);
        }
        return shell;
    }

    private BottomNavItemView bottomNavItem(int iconType, String label, int targetScreen) {
        BottomNavItemView item = new BottomNavItemView(this, iconType, label, targetScreen);
        bottomNavItems.add(item);
        item.setOnClickListener(view -> {
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
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
        return item;
    }

    private void updateBottomNavContentPadding() {
        if (bottomNavBar == null || bottomNavBar.getChildCount() == 0) {
            return;
        }
        View container = bottomNavBar.getChildAt(0);
        container.setPadding(dp(8), 0, dp(8), bottomNavInsetBottom);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) container.getLayoutParams();
        if (params != null) {
            params.height = dp(64) + bottomNavInsetBottom;
            container.setLayoutParams(params);
        }
    }

    private void updateBottomNav() {
        int activeScreen = currentScreen == SCREEN_RESULTS ? SCREEN_CALCULATOR : currentScreen;
        for (BottomNavItemView item : bottomNavItems) {
            item.setSelectedState(item.targetScreen == activeScreen, history.size());
        }
    }

    private GradientDrawable bottomNavBackground() {
        GradientDrawable drawable = rounded(0xFF0A1E1A, 0);
        drawable.setCornerRadii(new float[] {
            dp(20), dp(20),
            dp(20), dp(20),
            0, 0,
            0, 0
        });
        drawable.setStroke(dp(1), 0xFF1D3530);
        return drawable;
    }

    private void configureTopNav(String title, boolean resultsMode, boolean scrollHide) {
        enableNavScrollHide = false;
        if (historyStickyHeader != null && currentScreen != SCREEN_HISTORY) {
            historyStickyHeader.setVisibility(View.GONE);
        }
        navHidden = false;
        if (mainScrollView != null) {
            mainScrollView.scrollTo(0, 0);
        }
        lastScrollY = 0;
        if (navBar != null) {
            navBar.setVisibility(View.GONE);
            navBar.animate().cancel();
            navBar.setTranslationY(0f);
            navBar.removeAllViews();
            navBar.setPadding(dp(14), 0, dp(14), 0);
            navBar.setBackground(rounded(0xFF0A1E1A, 0));
            navBar.setElevation(0);

            View left = resultsMode ? topBackButton() : topNavActionButton(false);
            if (!resultsMode) {
                left.setOnClickListener(view -> {
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
                    toggleNavDrawer();
                });
            }
            FrameLayout.LayoutParams leftParams = new FrameLayout.LayoutParams(resultsMode ? dp(92) : dp(44), dp(44));
            leftParams.gravity = Gravity.START | Gravity.CENTER_VERTICAL;
            navBar.addView(left, leftParams);

            navTitleView = text(title, 14, COLOR_TEXT, false);
            navTitleView.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
            navTitleView.setGravity(Gravity.CENTER);
            navTitleView.setAlpha(0f);
            FrameLayout.LayoutParams titleParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            );
            titleParams.leftMargin = dp(96);
            titleParams.rightMargin = dp(96);
            navBar.addView(navTitleView, titleParams);
            navTitleView.animate().alpha(1f).setDuration(150).start();

            View logo = navLogoView();
            FrameLayout.LayoutParams logoParams = new FrameLayout.LayoutParams(dp(30), dp(30));
            logoParams.gravity = Gravity.END | Gravity.CENTER_VERTICAL;
            navBar.addView(logo, logoParams);
            addNavBottomBorder(navBar);
        }
    }

    private void addNavBottomBorder(FrameLayout bar) {
        View border = new View(this);
        border.setBackgroundColor(0xFF1D3530);
        FrameLayout.LayoutParams borderParams = new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            dp(1)
        );
        borderParams.gravity = Gravity.BOTTOM;
        bar.addView(border, borderParams);
    }

    private View topNavActionButton(boolean selected) {
        NavIconView icon = new NavIconView(this, ICON_MENU, COLOR_TEXT);
        icon.setBackground(rippleBackground(0x00000000, withAlpha(COLOR_PRIMARY, 31), dp(40)));
        icon.setContentDescription("Abrir menú");
        return icon;
    }

    private View topBackButton() {
        LinearLayout back = new LinearLayout(this);
        back.setOrientation(LinearLayout.HORIZONTAL);
        back.setGravity(Gravity.CENTER_VERTICAL);
        back.setPadding(0, 0, dp(8), 0);
        back.setBackground(rippleBackground(0x00000000, withAlpha(COLOR_PRIMARY, 31), dp(40)));
        BackArrowView arrow = new BackArrowView(this, 0xFF7FA89C, false);
        back.addView(arrow, new LinearLayout.LayoutParams(dp(24), dp(44)));
        TextView label = text("Calcular", 12, 0xFF7FA89C, false);
        label.setGravity(Gravity.CENTER_VERTICAL);
        back.addView(label, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, dp(44)));
        back.setOnClickListener(view -> {
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
            showCalculatorScreen();
        });
        return back;
    }

    private View navLogoView() {
        ImageView logo = new ImageView(this);
        logo.setImageResource(R.drawable.logo_circular_dark);
        logo.setScaleType(ImageView.ScaleType.FIT_CENTER);
        logo.setAdjustViewBounds(true);
        logo.setContentDescription("Logo TermoWences");
        return logo;
    }

    private View topBar(String title) {
        LinearLayout bar = new LinearLayout(this);
        bar.setOrientation(LinearLayout.HORIZONTAL);
        bar.setGravity(Gravity.CENTER_VERTICAL);
        bar.setPadding(dp(12), dp(10), dp(12), dp(10));
        bar.setBackground(roundedStroke(0xD91A2724, 0x5531C7A0, dp(12)));
        bar.setElevation(dp(6));

        View menu = navIconButton(ICON_MENU, "Abrir navegación", false);
        menu.setOnClickListener(view -> {
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
            toggleNavDrawer();
        });
        bar.addView(menu, new LinearLayout.LayoutParams(dp(48), dp(48)));

        TextView titleView = text(title, 18, COLOR_TEXT, true);
        titleView.setGravity(Gravity.CENTER_VERTICAL);
        titleView.setPadding(dp(12), 0, 0, 0);
        bar.addView(titleView, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        ImageView logo = new ImageView(this);
        logo.setImageResource(R.drawable.logo_circular_dark);
        logo.setScaleType(ImageView.ScaleType.FIT_CENTER);
        bar.addView(logo, new LinearLayout.LayoutParams(dp(40), dp(40)));
        animateScaleIn(logo, 120);
        return bar;
    }

    private LinearLayout buildNavDrawer() {
        LinearLayout drawer = new LinearLayout(this);
        drawer.setOrientation(LinearLayout.VERTICAL);
        drawer.setPadding(0, dp(36), 0, dp(18));
        drawer.setBackground(drawerBackground());
        refreshNavDrawer(drawer);
        return drawer;
    }

    private void refreshNavDrawer(LinearLayout drawer) {
        drawer.removeAllViews();
        drawer.addView(drawerHeader(), matchWrap(0, 0, 0, 14));
        drawer.addView(thinDivider(), new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Math.max(1, (int) dp(0.5f))));
        drawer.addView(navItem(ICON_HOME, "Inicio", SCREEN_MENU), navItemWrap(14));
        drawer.addView(navItem(ICON_CURVE, "Calcular", SCREEN_CALCULATOR), navItemWrap(4));
        drawer.addView(navItem(ICON_HISTORY, "Historial", SCREEN_HISTORY), navItemWrap(4));
        drawer.addView(navItem(ICON_INFO, "Acerca de", SCREEN_ABOUT), navItemWrap(4));
    }

    private View navItem(int iconType, String label, int targetScreen) {
        boolean selected = currentScreen == targetScreen;
        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.HORIZONTAL);
        item.setGravity(Gravity.CENTER_VERTICAL);
        item.setPadding(0, 0, dp(14), 0);
        item.setBackground(rippleBackground(selected ? 0xFF1D4A3C : 0x00000000, withAlpha(COLOR_PRIMARY, 31), dp(40)));

        View activeBar = new View(this);
        activeBar.setBackgroundColor(selected ? COLOR_PRIMARY : 0x00000000);
        item.addView(activeBar, new LinearLayout.LayoutParams(dp(3), LinearLayout.LayoutParams.MATCH_PARENT));

        NavIconView icon = new NavIconView(this, iconType, selected ? COLOR_PRIMARY : 0xFF7FA89C);
        item.addView(icon, new LinearLayout.LayoutParams(dp(44), dp(48)));

        TextView labelView = text(label, 13, selected ? COLOR_PRIMARY : 0xFF7FA89C, false);
        labelView.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
        item.addView(labelView, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        if (targetScreen == SCREEN_HISTORY && !history.isEmpty()) {
            TextView badge = text(String.valueOf(history.size()), 9, 0xFF04342C, true);
            badge.setGravity(Gravity.CENTER);
            badge.setBackground(rounded(COLOR_PRIMARY, dp(8)));
            item.addView(badge, new LinearLayout.LayoutParams(dp(16), dp(16)));
        }

        item.setContentDescription(label);
        item.setOnClickListener(clicked -> {
            clicked.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
            closeNavDrawer();
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
        return item;
    }

    private View drawerHeader() {
        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(16), 0, dp(16), 0);

        View logo = navLogoView();
        header.addView(logo, new LinearLayout.LayoutParams(dp(38), dp(38)));

        LinearLayout textBlock = new LinearLayout(this);
        textBlock.setOrientation(LinearLayout.VERTICAL);
        textBlock.setPadding(dp(12), 0, 0, 0);
        TextView name = text("TermoWences", 15, COLOR_TEXT, false);
        name.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        textBlock.addView(name, compactWrap());
        textBlock.addView(text("v1.0", 11, 0xFF7FA89C, false), compactWrap());
        header.addView(textBlock, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        return header;
    }

    private View thinDivider() {
        View divider = new View(this);
        divider.setBackgroundColor(0x801D4A3C);
        return divider;
    }

    private View navIconButton(int iconType, String label, boolean selected) {
        int bg = selected ? COLOR_PRIMARY : COLOR_SURFACE_2;
        int fg = selected ? 0xFF07100F : COLOR_TEXT;
        NavIconView view = new NavIconView(this, iconType, fg);
        view.setBackground(rippleBackground(bg, withAlpha(COLOR_PRIMARY, 70), dp(8)));
        view.setContentDescription(label);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            view.setTooltipText(label);
        } else {
            view.setOnLongClickListener(target -> {
                showMessage(label);
                return true;
            });
        }
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
        if (navOpen) {
            refreshNavDrawer(navDrawer);
            navScrim.setVisibility(View.VISIBLE);
            navDrawer.setVisibility(View.VISIBLE);
            navDrawer.setTranslationX(-dp(260));
            navDrawer.animate()
                .translationX(0f)
                .setDuration(220)
                .setInterpolator(new DecelerateInterpolator())
                .start();
        } else {
            closeNavDrawer();
        }
    }

    private void closeNavDrawer() {
        navOpen = false;
        if (navScrim != null) {
            navScrim.setVisibility(View.GONE);
        }
        if (navDrawer != null) {
            if (navDrawer.getVisibility() == View.VISIBLE) {
                navDrawer.animate()
                    .translationX(-dp(260))
                    .setDuration(220)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .withEndAction(() -> navDrawer.setVisibility(View.GONE))
                    .start();
            } else {
                navDrawer.setTranslationX(-dp(260));
            }
        }
    }

    private void handleNavScroll(int scrollY, int oldScrollY) {
        if (!enableNavScrollHide || navBar == null || navOpen) {
            lastScrollY = scrollY;
            return;
        }
        int delta = scrollY - oldScrollY;
        if (delta > dp(12) && !navHidden) {
            navHidden = true;
            navBar.animate()
                .translationY(-dp(56))
                .setDuration(200)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
        } else if (delta < 0 && navHidden) {
            navHidden = false;
            navBar.animate()
                .translationY(0f)
                .setDuration(200)
                .setInterpolator(new DecelerateInterpolator())
                .start();
        }
        lastScrollY = scrollY;
    }

    private View menuSummary() {
        LinearLayout outer = new LinearLayout(this);
        outer.setOrientation(LinearLayout.VERTICAL);

        TextView sectionLabel = text("CARACTERÍSTICAS", 10, withAlpha(COLOR_PRIMARY, 200), false);
        sectionLabel.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        sectionLabel.setLetterSpacing(0.12f);
        outer.addView(sectionLabel, matchWrap(dp(2), 0, 0, dp(14)));

        View metricsView = metricsRow();
        outer.addView(metricsView, matchWrap(0, 0, 0, dp(16)));

        View chipOne = technicalChip(ICON_DATABASE, "Base de consulta", "Agua, aire seco y vapor saturado con interpolación cúbica.", COLOR_PRIMARY);
        View chipTwo = technicalChip(ICON_CURVE, "Lectura técnica", "Valor, unidad, fórmula, explicación y tendencia por propiedad.", COLOR_SECONDARY);
        View chipThree = technicalChip(ICON_REPORT, "Reporte PDF", "Exporta resultados con gráficos y fórmulas listos para revisión.", COLOR_VIOLET);

        outer.addView(chipOne);
        outer.addView(chipTwo, matchWrap(0, dp(10), 0, 0));
        outer.addView(chipThree, matchWrap(0, dp(10), 0, 0));

        outer.post(() -> {
            animateRevealUp(metricsView, 0);
            checkScrollRevealViews();
        });
        return outer;
    }

    private View metricsRow() {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);

        row.addView(metric("3", "fluidos", COLOR_PRIMARY), weightedButton(0, 0, dp(8), 0));
        row.addView(metric("24", "propiedades", COLOR_SECONDARY), weightedButton(0, 0, dp(8), 0));
        row.addView(metric("PDF", "reporte", COLOR_VIOLET), weightedButton(0, 0, 0, 0));
        return row;
    }

    private View metric(String value, String label, int color) {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setGravity(Gravity.CENTER);
        box.setPadding(dp(8), dp(14), dp(8), dp(14));
        GradientDrawable metricBg = new GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            new int[]{ withAlpha(color, 30), withAlpha(color, 12) }
        );
        metricBg.setCornerRadius(dp(14));
        metricBg.setStroke(dp(1), withAlpha(color, 55));
        box.setBackground(metricBg);
        box.setElevation(dp(4));
        box.setOnClickListener(view -> view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP));
        TextView valueView = text(value, 20, color, true);
        valueView.setGravity(Gravity.CENTER);
        box.addView(valueView, compactWrap());
        TextView labelView = text(label, 10, withAlpha(COLOR_TEXT, 160), false);
        labelView.setGravity(Gravity.CENTER);
        box.addView(labelView, matchWrap(0, dp(3), 0, 0));
        return box;
    }

    private View technicalChip(int iconType, String title, String detail, int color) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(0, 0, dp(14), 0);
        GradientDrawable chipBg = new GradientDrawable();
        chipBg.setColor(COLOR_SURFACE_2);
        chipBg.setCornerRadius(dp(14));
        chipBg.setStroke(dp(1), withAlpha(color, 45));
        card.setBackground(chipBg);
        card.setElevation(dp(5));

        View accent = new View(this);
        GradientDrawable accentBg = new GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            new int[]{ color, withAlpha(color, 140) }
        );
        accentBg.setCornerRadii(new float[]{ dp(14), dp(14), 0, 0, 0, 0, dp(14), dp(14) });
        accent.setBackground(accentBg);
        card.addView(accent, new LinearLayout.LayoutParams(dp(3), dp(72)));

        LinearLayout chip = new LinearLayout(this);
        chip.setOrientation(LinearLayout.HORIZONTAL);
        chip.setGravity(Gravity.CENTER_VERTICAL);
        chip.setPadding(dp(12), dp(12), 0, dp(12));

        NavIconView icon = new NavIconView(this, iconType, color);
        icon.setBackground(rounded(withAlpha(color, 22), dp(12)));
        chip.addView(icon, new LinearLayout.LayoutParams(dp(40), dp(40)));

        LinearLayout textBlock = new LinearLayout(this);
        textBlock.setOrientation(LinearLayout.VERTICAL);
        textBlock.setPadding(dp(12), 0, 0, 0);
        TextView titleView = text(title, 14, COLOR_TEXT, false);
        titleView.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        textBlock.addView(titleView, compactWrap());
        TextView detailView = text(detail, 12, COLOR_MUTED, false);
        detailView.setLineSpacing(0, 1.22f);
        textBlock.addView(detailView, matchWrap(0, dp(3), 0, 0));
        chip.addView(textBlock, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        card.addView(chip, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        registerScrollReveal(card);
        return card;
    }

    private View gradientDivider() {
        GradientDividerView divider = new GradientDividerView(this);
        divider.setAlpha(0.85f);
        return divider;
    }

    private void showCalculatorScreen() {
        currentScreen = SCREEN_CALCULATOR;
        configureTopNav("Calcular", false, false);
        updateBottomNav();
        setResultsChromeVisible(false);
        setDefaultPagePadding();
        pageLayout.removeAllViews();
        pageLayout.addView(calculatorSection(), matchWrap(0, 0, 0, 14));

        setSelectedTable(lastTableKey, false);
        updateSelectedTable(false);
        if (!Double.isNaN(lastTemperature)) {
            temperatureInput.setText(String.format(Locale.US, "%.2f", lastTemperature));
        }
        validateTemperatureInput(false);
        animateIntro();
    }

    private void showHistoryScreen() {
        currentScreen = SCREEN_HISTORY;
        configureTopNav("Historial", false, true);
        updateBottomNav();
        setResultsChromeVisible(false);
        setDefaultPagePadding();
        pageLayout.removeAllViews();
        historyLayout = new LinearLayout(this);
        historyLayout.setOrientation(LinearLayout.VERTICAL);
        pageLayout.addView(historyCard(historyLayout), matchWrap(0, 0, 0, 0));
        renderHistory();
        animateIntro();
    }

    private void showAboutScreen() {
        currentScreen = SCREEN_ABOUT;
        configureTopNav("Acerca de", false, false);
        updateBottomNav();
        setResultsChromeVisible(false);
        setDefaultPagePadding();
        pageLayout.removeAllViews();
        View hero = aboutHeroCard();
        pageLayout.addView(hero, matchWrap(0, 0, 0, 14));
        pageLayout.addView(purposeSection(), matchWrap(0, 0, 0, 14));
        pageLayout.addView(authorsSection(), matchWrap(0, 0, 0, 14));
        pageLayout.addView(academicFooter(), matchWrap(0, 0, 0, 0));
        animateScaleIn(hero, 0);
        animateIntro();
    }

    private void showResultsScreen() {
        currentScreen = SCREEN_RESULTS;
        configureTopNav("Resultados", true, false);
        updateBottomNav();
        setResultsPagePadding();
        pageLayout.removeAllViews();
        resultsLayout = new LinearLayout(this);
        resultsLayout.setOrientation(LinearLayout.VERTICAL);
        pageLayout.addView(resultsLayout, matchWrap(0, 0, 0, 0));
        renderCurrentResults();
        setResultsChromeVisible(true);
        animateIntro();
    }

    private void setDefaultPagePadding() {
        if (pageLayout != null) {
            pageLayout.setPadding(dp(16), systemStatusInsetTop + dp(24), dp(16), dp(104) + bottomNavInsetBottom);
        }
    }

    private void setHomePagePadding() {
        if (pageLayout != null) {
            pageLayout.setPadding(0, 0, 0, dp(104) + bottomNavInsetBottom);
        }
    }

    private void setResultsPagePadding() {
        if (pageLayout != null) {
            pageLayout.setPadding(dp(16), systemStatusInsetTop + dp(114), dp(16), dp(104) + bottomNavInsetBottom);
        }
    }

    private void setPagePaddingForCurrentScreen() {
        if (currentScreen == SCREEN_RESULTS) {
            setResultsPagePadding();
        } else if (currentScreen == SCREEN_MENU) {
            setHomePagePadding();
        } else {
            setDefaultPagePadding();
        }
    }

    private void applyStatusTopInset(int topInset) {
        int normalized = Math.max(0, topInset);
        if (normalized == systemStatusInsetTop) {
            return;
        }
        systemStatusInsetTop = normalized;
        setPagePaddingForCurrentScreen();
        updateTopOverlayMargins();
    }

    private void updateTopOverlayMargins() {
        if (historyStickyHeader != null) {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) historyStickyHeader.getLayoutParams();
            if (params != null) {
                params.topMargin = systemStatusInsetTop + dp(12);
                historyStickyHeader.setLayoutParams(params);
            }
        }
        if (resultsSummaryHeader != null) {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) resultsSummaryHeader.getLayoutParams();
            if (params != null) {
                params.topMargin = systemStatusInsetTop + dp(24);
                resultsSummaryHeader.setLayoutParams(params);
            }
        }
    }

    private void setResultsChromeVisible(boolean visible) {
        if (!visible) {
            if (resultsSummaryHeader != null) {
                rootLayout.removeView(resultsSummaryHeader);
                resultsSummaryHeader = null;
            }
            exportPdfButtonView = null;
            exportPdfText = null;
            exportPdfIcon = null;
            exportPdfSpinner = null;
            return;
        }
        showResultsSummaryHeader();
    }

    private void showResultsSummaryHeader() {
        if (resultsSummaryHeader != null) {
            rootLayout.removeView(resultsSummaryHeader);
        }
        resultsSummaryHeader = resultsSummaryHeaderView();
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            dp(74)
        );
        params.gravity = Gravity.TOP;
        params.topMargin = systemStatusInsetTop + dp(24);
        params.leftMargin = dp(16);
        params.rightMargin = dp(16);
        rootLayout.addView(resultsSummaryHeader, params);
        animateRevealUp(resultsSummaryHeader, 0);
    }

    private LinearLayout resultsSummaryHeaderView() {
        LinearLayout wrapper = new LinearLayout(this);
        wrapper.setOrientation(LinearLayout.HORIZONTAL);
        wrapper.setGravity(Gravity.CENTER_VERTICAL);
        wrapper.setPadding(dp(14), dp(10), dp(14), dp(10));
        wrapper.setBackground(roundedStroke(0xFF0D2B24, 0xFF1D4A3C, dp(12)));
        wrapper.setElevation(dp(3));

        LinearLayout left = new LinearLayout(this);
        left.setOrientation(LinearLayout.VERTICAL);
        TextView label = text("FLUIDO ANALIZADO", 9, 0xFF7FA89C, false);
        label.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        left.addView(label, compactWrap());
        TextView value = text(summaryFluidText(), 15, COLOR_PRIMARY, false);
        value.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        left.addView(value, matchWrap(0, dp(2), 0, 0));
        LinearLayout time = new LinearLayout(this);
        time.setOrientation(LinearLayout.HORIZONTAL);
        time.setGravity(Gravity.CENTER_VERTICAL);
        time.addView(new NavIconView(this, ICON_HISTORY, 0xFF3A5A52), new LinearLayout.LayoutParams(dp(13), dp(13)));
        TextView timeText = text(" " + resultTimestamp(), 10, 0xFF3A5A52, false);
        time.addView(timeText, compactWrap());
        left.addView(time, matchWrap(0, dp(2), 0, 0));
        wrapper.addView(left, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        LinearLayout count = new LinearLayout(this);
        count.setOrientation(LinearLayout.VERTICAL);
        count.setGravity(Gravity.CENTER);
        count.setPadding(dp(10), dp(6), dp(10), dp(6));
        count.setBackground(rounded(0xFF1D4A3C, dp(8)));
        count.addView(text("propiedades", 9, 0xFF7FA89C, false), compactWrap());
        TextView number = text(String.valueOf(lastPdfEntries.size()), 18, COLOR_PRIMARY, false);
        number.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        number.setGravity(Gravity.CENTER);
        count.addView(number, compactWrap());
        wrapper.addView(count, new LinearLayout.LayoutParams(dp(86), LinearLayout.LayoutParams.WRAP_CONTENT));
        return wrapper;
    }

    private String summaryFluidText() {
        TableSpec spec = specForKey(lastTableKey);
        String name = spec == null ? "Calculo" : spec.title;
        if (Double.isNaN(lastTemperature)) {
            return name;
        }
        return String.format(Locale.US, "%s - %.2f C", name, lastTemperature);
    }

    private String resultTimestamp() {
        return new SimpleDateFormat("HH:mm - d MMMM", new Locale("es", "ES")).format(new Date());
    }

    private void handleExportPdfPress() {
        if (lastPdfLines.isEmpty()) {
            showMessage("Primero realiza un calculo para exportar.");
            return;
        }
        setExportPdfState("loading");
        exportPdf();
    }

    private void setExportPdfState(String state) {
        if (exportPdfText == null || exportPdfIcon == null || exportPdfSpinner == null) {
            return;
        }
        if ("loading".equals(state)) {
            exportPdfText.setText("Generando reporte...");
            exportPdfText.setTextSize(13);
            exportPdfIcon.setVisibility(View.GONE);
            exportPdfSpinner.setVisibility(View.VISIBLE);
            exportPdfSpinner.start();
            return;
        }
        exportPdfSpinner.stop();
        exportPdfSpinner.setVisibility(View.GONE);
        exportPdfIcon.setVisibility(View.VISIBLE);
        exportPdfIcon.setIconType(ICON_REPORT);
        exportPdfIcon.setIconColor(0xFF412402);
        exportPdfIcon.invalidate();
        if (exportPdfButtonView != null) {
            exportPdfButtonView.setBackground(rippleBackground(COLOR_ACCENT, 0x33000000, dp(12)));
        }
        exportPdfText.setTextSize(14);
        exportPdfText.setTextColor(0xFF412402);
        exportPdfText.setText("Exportar PDF");
    }

    private void flashExportPdfSuccess() {
        if (exportPdfText == null || exportPdfIcon == null) {
            return;
        }
        setExportPdfState("normal");
        exportPdfText.setText("PDF listo");
        exportPdfText.setTextColor(0xFF04342C);
        exportPdfIcon.setIconType(ICON_CHECK);
        exportPdfIcon.setIconColor(0xFF04342C);
        if (exportPdfButtonView != null) {
            LinearLayout pdfButton = exportPdfButtonView;
            ValueAnimator color = ValueAnimator.ofObject(new ArgbEvaluator(), COLOR_ACCENT, COLOR_PRIMARY);
            color.setDuration(200);
            color.addUpdateListener(animation -> pdfButton.setBackground(rippleBackground((int) animation.getAnimatedValue(), 0x33000000, dp(12))));
            color.start();
        }
        exportPdfIcon.invalidate();
        exportPdfText.postDelayed(() -> {
            if (exportPdfText != null && exportPdfIcon != null) {
                exportPdfText.setTextColor(0xFF412402);
                exportPdfText.setText("Exportar PDF");
                exportPdfIcon.setIconType(ICON_REPORT);
                exportPdfIcon.setIconColor(0xFF412402);
                if (exportPdfButtonView != null) {
                    exportPdfButtonView.setBackground(rippleBackground(COLOR_ACCENT, 0x33000000, dp(12)));
                }
                exportPdfIcon.invalidate();
            }
        }, 2000);
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
        logo.setImageResource(R.drawable.logo_circular_dark);
        logo.setScaleType(ImageView.ScaleType.FIT_CENTER);
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

    private void setSelectedTable(String tableKey, boolean animate) {
        if (tableKey == null || tableKey.isEmpty()) {
            return;
        }
        for (int i = 0; i < TABLES.length; i++) {
            if (TABLES[i].key.equals(tableKey)) {
                selectedTableIndex = i;
                updateSegmentedControl(animate);
                return;
            }
        }
    }

    private View heroSection() {
        LinearLayout section = new LinearLayout(this);
        section.setOrientation(LinearLayout.VERTICAL);

        TextView sectionLabel = text("ACCESO RÁPIDO", 10, withAlpha(COLOR_PRIMARY, 200), false);
        sectionLabel.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        sectionLabel.setLetterSpacing(0.12f);
        section.addView(sectionLabel, matchWrap(dp(2), 0, 0, dp(12)));

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);

        View calcCard = quickActionCard(ICON_CURVE, "Calcular", "Agua · Aire · Vapor", COLOR_PRIMARY, SCREEN_CALCULATOR);
        View histCard = quickActionCard(ICON_HISTORY, "Historial", "Consultas previas", COLOR_SECONDARY, SCREEN_HISTORY);
        View infoCard = quickActionCard(ICON_INFO, "Acerca de", "Info del app", COLOR_VIOLET, SCREEN_ABOUT);

        LinearLayout.LayoutParams cardParams1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        LinearLayout.LayoutParams cardParams2 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        LinearLayout.LayoutParams cardParams3 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        cardParams1.setMargins(0, 0, dp(8), 0);
        cardParams2.setMargins(0, 0, dp(8), 0);
        row.addView(calcCard, cardParams1);
        row.addView(histCard, cardParams2);
        row.addView(infoCard, cardParams3);

        section.addView(row, compactWrap());
        return section;
    }

    private View quickActionCard(int iconType, String title, String subtitle, int color, int targetScreen) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(Gravity.CENTER_HORIZONTAL);
        card.setPadding(dp(10), dp(18), dp(10), dp(16));

        GradientDrawable cardBg = new GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            new int[]{ withAlpha(color, 28), 0xFF0D1628 }
        );
        cardBg.setCornerRadius(dp(16));
        cardBg.setStroke(dp(1), withAlpha(color, 65));
        card.setBackground(cardBg);
        card.setElevation(dp(5));

        FrameLayout iconCircle = new FrameLayout(this);
        View iconBg = new View(this);
        GradientDrawable circleBg = new GradientDrawable();
        circleBg.setShape(GradientDrawable.OVAL);
        circleBg.setColor(withAlpha(color, 30));
        circleBg.setStroke(dp(1), withAlpha(color, 55));
        iconBg.setBackground(circleBg);
        iconCircle.addView(iconBg, new FrameLayout.LayoutParams(dp(46), dp(46)));
        NavIconView icon = new NavIconView(this, iconType, color);
        FrameLayout.LayoutParams iconParams = new FrameLayout.LayoutParams(dp(24), dp(24));
        iconParams.gravity = Gravity.CENTER;
        iconCircle.addView(icon, iconParams);
        card.addView(iconCircle, new LinearLayout.LayoutParams(dp(46), dp(46)));

        TextView titleView = text(title, 13, COLOR_TEXT, false);
        titleView.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        titleView.setGravity(Gravity.CENTER);
        card.addView(titleView, matchWrap(0, dp(10), 0, 0));

        TextView subtitleView = text(subtitle, 10, withAlpha(COLOR_TEXT, 130), false);
        subtitleView.setGravity(Gravity.CENTER);
        subtitleView.setLineSpacing(0, 1.2f);
        card.addView(subtitleView, matchWrap(0, dp(3), 0, 0));

        card.setOnClickListener(view -> {
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
            view.animate().scaleX(0.95f).scaleY(0.95f).setDuration(80).withEndAction(() ->
                view.animate().scaleX(1f).scaleY(1f).setDuration(100).withEndAction(() -> {
                    if (targetScreen == SCREEN_CALCULATOR) showCalculatorScreen();
                    else if (targetScreen == SCREEN_HISTORY) showHistoryScreen();
                    else showAboutScreen();
                }).start()
            ).start();
        });
        animateRevealUp(card, 100);
        return card;
    }

    private View homeHeader() {
        FrameLayout outer = new FrameLayout(this);
        GradientDrawable headerBg = new GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            new int[]{ 0xFF0A1530, 0xFF060C18 }
        );
        outer.setBackground(headerBg);

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setGravity(Gravity.CENTER_HORIZONTAL);
        content.setPadding(dp(24), systemStatusInsetTop + dp(32), dp(24), dp(32));

        // Logo con anillo de brillo
        FrameLayout logoFrame = new FrameLayout(this);
        View outerGlow = new View(this);
        GradientDrawable outerGlowBg = new GradientDrawable();
        outerGlowBg.setShape(GradientDrawable.OVAL);
        outerGlowBg.setColor(withAlpha(COLOR_PRIMARY, 18));
        outerGlowBg.setStroke(dp(1), withAlpha(COLOR_PRIMARY, 50));
        outerGlow.setBackground(outerGlowBg);
        FrameLayout.LayoutParams outerGlowParams = new FrameLayout.LayoutParams(dp(76), dp(76));
        outerGlowParams.gravity = Gravity.CENTER;
        logoFrame.addView(outerGlow, outerGlowParams);

        View innerGlow = new View(this);
        GradientDrawable innerGlowBg = new GradientDrawable();
        innerGlowBg.setShape(GradientDrawable.OVAL);
        innerGlowBg.setColor(withAlpha(COLOR_SECONDARY, 14));
        innerGlow.setBackground(innerGlowBg);
        FrameLayout.LayoutParams innerGlowParams = new FrameLayout.LayoutParams(dp(62), dp(62));
        innerGlowParams.gravity = Gravity.CENTER;
        logoFrame.addView(innerGlow, innerGlowParams);

        ImageView logo = new ImageView(this);
        logo.setImageResource(R.drawable.logo_circular_dark);
        logo.setScaleType(ImageView.ScaleType.FIT_CENTER);
        logo.setContentDescription("Logo TermoWences");
        FrameLayout.LayoutParams logoParams = new FrameLayout.LayoutParams(dp(50), dp(50));
        logoParams.gravity = Gravity.CENTER;
        logoFrame.addView(logo, logoParams);
        content.addView(logoFrame, new LinearLayout.LayoutParams(dp(76), dp(76)));
        animateScaleIn(logoFrame, 40);

        // Nombre de la app
        TextView title = text("TermoWences", 28, COLOR_TEXT, false);
        title.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        title.setGravity(Gravity.CENTER);
        content.addView(title, matchWrap(0, dp(16), 0, 0));

        // Badge de versión
        TextView badge = text("Ingeniería Térmica  ·  v1.0", 11, COLOR_PRIMARY, false);
        badge.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        badge.setGravity(Gravity.CENTER);
        badge.setPadding(dp(16), dp(5), dp(16), dp(5));
        GradientDrawable badgeBg = new GradientDrawable();
        badgeBg.setColor(withAlpha(COLOR_PRIMARY, 18));
        badgeBg.setStroke(dp(1), withAlpha(COLOR_PRIMARY, 55));
        badgeBg.setCornerRadius(dp(20));
        badge.setBackground(badgeBg);
        LinearLayout.LayoutParams badgeParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        badgeParams.topMargin = dp(10);
        content.addView(badge, badgeParams);

        // Descripción
        TextView desc = text("Consulta propiedades térmicas con resultados\ntrazables y reportes para revisión técnica.", 13, COLOR_MUTED, false);
        desc.setGravity(Gravity.CENTER);
        desc.setLineSpacing(0, 1.45f);
        content.addView(desc, matchWrap(0, dp(18), 0, 0));

        // Línea gradiente decorativa
        GradientDrawable lineGrad = new GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            new int[]{ 0x00000000, COLOR_SECONDARY, COLOR_PRIMARY, 0x00000000 }
        );
        View line = new View(this);
        line.setBackground(lineGrad);
        LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, dp(1));
        lineParams.topMargin = dp(24);
        content.addView(line, lineParams);

        outer.addView(content, new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        return outer;
    }

    private View aboutHeroCard() {
        HexHeroCard hero = new HexHeroCard(this);
        hero.setPadding(dp(20), systemStatusInsetTop + dp(20), dp(20), dp(20));
        hero.setBackground(roundedStroke(0xFF0D2B24, 0xFF1D4A3C, dp(14)));
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setGravity(Gravity.CENTER);

        ImageView logo = new ImageView(this);
        logo.setImageResource(R.drawable.logo_circular_dark);
        logo.setScaleType(ImageView.ScaleType.FIT_CENTER);
        logo.setContentDescription("Logo TermoWences");
        content.addView(logo, new LinearLayout.LayoutParams(dp(44), dp(44)));

        TextView name = text("TermoWences", 18, COLOR_TEXT, false);
        name.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        name.setGravity(Gravity.CENTER);
        content.addView(name, matchWrap(0, dp(10), 0, 0));

        TextView version = text("v1.0 · Ingeniería Térmica", 9, COLOR_PRIMARY, false);
        version.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        version.setGravity(Gravity.CENTER);
        version.setPadding(dp(10), dp(2), dp(10), dp(2));
        version.setBackground(roundedStroke(0xFF1D4A3C, 0xFF1D9E75, dp(20)));
        content.addView(version, matchWrap(dp(72), dp(6), dp(72), 0));

        hero.addView(content, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        return hero;
    }

    private View purposeSection() {
        LinearLayout content = section("Para qué sirve");
        View first = aboutFeatureCard(ICON_BOLT, "Consulta rápida", "Termodinámica, fluidos y transferencia de calor");
        View second = aboutFeatureCard(ICON_RULER, "Entrada simple", "Solo el fluido y la temperatura");
        View third = aboutFeatureCard(ICON_LIST, "Salida ordenada", "Valor, unidad y explicación por propiedad");
        content.addView(first);
        content.addView(second);
        content.addView(third);
        content.post(() -> {
            animateRevealUp(first, 0);
            animateRevealUp(second, 80);
            animateRevealUp(third, 160);
        });
        return card(content);
    }

    private View aboutFeatureCard(int iconType, String title, String detail) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(12), dp(10), dp(12), dp(10));
        row.setBackground(roundedStroke(COLOR_SURFACE, 0xFF253D37, dp(10)));

        LinearLayout iconBox = new LinearLayout(this);
        iconBox.setGravity(Gravity.CENTER);
        iconBox.setBackground(rounded(0xFF1D4A3C, dp(8)));
        iconBox.addView(new NavIconView(this, iconType, COLOR_PRIMARY), new LinearLayout.LayoutParams(dp(24), dp(24)));
        row.addView(iconBox, new LinearLayout.LayoutParams(dp(34), dp(34)));

        LinearLayout copy = new LinearLayout(this);
        copy.setOrientation(LinearLayout.VERTICAL);
        copy.setPadding(dp(10), 0, 0, 0);
        TextView titleView = text(title, 12, COLOR_TEXT, false);
        titleView.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        copy.addView(titleView, compactWrap());
        TextView detailView = text(detail, 10, 0xFF7FA89C, false);
        detailView.setMaxLines(2);
        copy.addView(detailView, matchWrap(0, dp(2), 0, 0));
        row.addView(copy, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        return wrapWithMargin(row, 0, dp(8), 0, 0);
    }

    private View authorsSection() {
        LinearLayout content = section("Autores");
        content.addView(authorRow("WM", "Wenceslao T. Medina Espinoza", "Autor", "Coordinación del análisis térmico y validación de propiedades.", 0xFF1D4A3C, COLOR_PRIMARY));
        content.addView(authorRow("MM", "Miguel A. Molina Mansilla", "Autor", "Diseño de flujo de cálculo y revisión de entradas.", 0xFF0C1E3C, 0xFF85B7EB));
        content.addView(authorRow("ET", "Edward Torres Cruz", "Autor", "Organización de resultados, reportes y gráficos.", 0xFF2A1E06, COLOR_ACCENT));
        content.addView(authorRow("AL", "Alica Leon Tacca", "Autora", "Revisión académica, presentación y documentación.", 0xFF2A1510, 0xFFF0997B));
        return card(content);
    }

    private View authorRow(String initials, String name, String role, String contribution, int avatarBg, int avatarText) {
        LinearLayout wrapper = new LinearLayout(this);
        wrapper.setOrientation(LinearLayout.VERTICAL);
        wrapper.setPadding(dp(12), dp(8), dp(12), dp(8));
        wrapper.setBackground(roundedStroke(COLOR_SURFACE, 0xFF253D37, dp(10)));

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        TextView avatar = text(initials, 10, avatarText, false);
        avatar.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        avatar.setGravity(Gravity.CENTER);
        avatar.setBackground(rounded(avatarBg, dp(15)));
        row.addView(avatar, new LinearLayout.LayoutParams(dp(30), dp(30)));

        LinearLayout copy = new LinearLayout(this);
        copy.setOrientation(LinearLayout.VERTICAL);
        copy.setPadding(dp(10), 0, 0, 0);
        TextView nameView = text(name, 12, COLOR_TEXT, false);
        nameView.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        copy.addView(nameView, compactWrap());
        copy.addView(text(role, 10, 0xFF7FA89C, false), compactWrap());
        row.addView(copy, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        wrapper.addView(row, compactWrap());

        TextView detail = text(contribution, 10, 0xFF3A5A52, false);
        detail.setVisibility(View.GONE);
        detail.setAlpha(0f);
        wrapper.addView(detail, matchWrap(dp(40), dp(6), 0, 0));
        final boolean[] expanded = {false};
        wrapper.setOnClickListener(view -> {
            expanded[0] = !expanded[0];
            if (expanded[0]) {
                detail.setVisibility(View.VISIBLE);
                detail.setTranslationY(-dp(6));
                detail.animate().alpha(1f).translationY(0f).setDuration(200).setInterpolator(new DecelerateInterpolator()).start();
            } else {
                detail.animate().alpha(0f).translationY(-dp(6)).setDuration(160).withEndAction(() -> detail.setVisibility(View.GONE)).start();
            }
        });
        return wrapWithMargin(wrapper, 0, dp(8), 0, 0);
    }

    private View academicFooter() {
        LinearLayout footer = new LinearLayout(this);
        footer.setOrientation(LinearLayout.VERTICAL);
        footer.setPadding(0, dp(12), 0, dp(12));
        footer.addView(gradientDivider(), matchWrap(0, 0, 0, dp(12)));
        TextView lineOne = text("Desarrollado como proyecto académico", 10, 0xFF3A5A52, false);
        lineOne.setGravity(Gravity.CENTER);
        footer.addView(lineOne, compactWrap());
        TextView lineTwo = text("Universidad · 2025", 10, 0xFF3A5A52, false);
        lineTwo.setGravity(Gravity.CENTER);
        footer.addView(lineTwo, compactWrap());
        return footer;
    }

    private View calculatorSection() {
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);

        TextView title = text("Calcular", 22, COLOR_TEXT, false);
        title.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        content.addView(title, compactWrap());

        TextView subtitle = text("Consulta propiedades y revisa gráficos individuales por resultado.", 12, 0xFF7FA89C, false);
        subtitle.setLineSpacing(0, 1.16f);
        content.addView(subtitle, matchWrap(0, dp(4), 0, dp(12)));

        LinearLayout.LayoutParams segmentParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(44));
        segmentParams.setMargins(0, 0, 0, dp(10));
        content.addView(segmentedControl(), segmentParams);
        content.addView(fluidInfoCard(), matchWrap(0, 0, 0, 10));
        content.addView(rangeBadgeView(), matchWrap(0, 0, 0, 8));
        content.addView(rangeTooltipView(), matchWrap(0, 0, 0, 10));

        temperatureInput = new EditText(this);
        temperatureInput.setBackgroundColor(0x00000000);
        temperatureInput.setHint("");
        temperatureInput.setSingleLine(true);
        temperatureInput.setTextSize(17);
        temperatureInput.setTextColor(COLOR_TEXT);
        temperatureInput.setPadding(dp(12), dp(14), dp(42), 0);
        temperatureInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
        temperatureInput.setImeOptions(EditorInfo.IME_ACTION_DONE);
        temperatureInput.setOnEditorActionListener((view, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                handleCalculatePress();
                return true;
            }
            return false;
        });
        temperatureInput.setOnFocusChangeListener((view, hasFocus) -> updateFloatingLabel(true));
        temperatureInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence value, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence value, int start, int before, int count) {
                updateFloatingLabel(true);
                validateTemperatureInput(true);
            }

            @Override
            public void afterTextChanged(Editable value) {
            }
        });
        content.addView(temperatureFieldView(), new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(52)));
        content.addView(validationErrorView(), matchWrap(0, dp(4), 0, 10));

        content.addView(calculateButtonView(), new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(52)));
        content.addView(clearInputButton(), matchWrap(0, dp(8), 0, 0));
        return card(content);
    }

    private View segmentedControl() {
        segmentFrame = new FrameLayout(this);
        segmentFrame.setPadding(dp(3), dp(3), dp(3), dp(3));
        segmentFrame.setMinimumHeight(dp(44));
        segmentFrame.setBackground(roundedStroke(0xFF1E2F2B, 0xFF253D37, dp(24)));

        segmentIndicator = new View(this);
        segmentIndicator.setBackground(rounded(COLOR_PRIMARY, dp(20)));
        segmentFrame.addView(segmentIndicator, new FrameLayout.LayoutParams(0, dp(38)));

        segmentContainer = new LinearLayout(this);
        segmentContainer.setOrientation(LinearLayout.HORIZONTAL);
        segmentButtons.clear();

        String[] labels = {"Agua", "Aire seco", "Vapor"};
        for (int i = 0; i < labels.length; i++) {
            final int index = i;
            TextView option = text(labels[i], 11, 0xFF7FA89C, false);
            option.setGravity(Gravity.CENTER);
            option.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
            option.setBackground(rounded(0x00000000, dp(20)));
            option.setOnClickListener(view -> selectSegment(index));
            segmentButtons.add(option);
            segmentContainer.addView(option, new LinearLayout.LayoutParams(0, dp(38), 1));
        }
        segmentFrame.addView(segmentContainer, new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        ));
        segmentFrame.post(() -> updateSegmentedControl(false));
        updateSegmentedControl(false);
        return segmentFrame;
    }

    private void selectSegment(int index) {
        if (index == selectedTableIndex) {
            return;
        }
        selectedTableIndex = index;
        updateSegmentedControl(true);
        crossFadeSelectedTableChange();
    }

    private void updateSegmentedControl(boolean animate) {
        for (int i = 0; i < segmentButtons.size(); i++) {
            TextView option = segmentButtons.get(i);
            boolean selected = i == selectedTableIndex;
            option.setTextColor(selected ? 0xFF04342C : 0xFF7FA89C);
            option.setTypeface(Typeface.create("sans-serif", selected ? Typeface.BOLD : Typeface.NORMAL));
            option.setBackground(rounded(0x00000000, dp(20)));
        }
        if (segmentFrame != null && segmentIndicator != null && segmentFrame.getWidth() > 0) {
            int available = segmentFrame.getWidth() - dp(6);
            int itemWidth = available / TABLES.length;
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) segmentIndicator.getLayoutParams();
            params.width = itemWidth;
            params.height = dp(38);
            params.leftMargin = dp(3);
            params.topMargin = dp(3);
            segmentIndicator.setLayoutParams(params);
            float target = itemWidth * selectedTableIndex;
            if (animate) {
                segmentIndicator.animate()
                    .translationX(target)
                    .setDuration(320)
                    .setInterpolator(new DecelerateInterpolator(1.5f))
                    .start();
            } else {
                segmentIndicator.setTranslationX(target);
            }
        }
    }

    private View fluidInfoCard() {
        fluidInfoCard = new LinearLayout(this);
        fluidInfoCard.setOrientation(LinearLayout.HORIZONTAL);
        fluidInfoCard.setGravity(Gravity.CENTER_VERTICAL);
        fluidInfoCard.setPadding(dp(10), 0, dp(10), 0);
        fluidInfoCard.setBackground(roundedStroke(0xFF1E2F2B, 0xFF253D37, dp(10)));

        fluidIconFrame = new LinearLayout(this);
        fluidIconFrame.setGravity(Gravity.CENTER);
        fluidIconFrame.setBackground(rounded(0xFF1D4A3C, dp(16)));
        fluidInfoCard.addView(fluidIconFrame, new LinearLayout.LayoutParams(dp(32), dp(32)));

        LinearLayout copy = new LinearLayout(this);
        copy.setOrientation(LinearLayout.VERTICAL);
        copy.setPadding(dp(10), 0, 0, 0);
        fluidTitleView = text("", 12, COLOR_TEXT, false);
        fluidTitleView.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        fluidDescriptionView = text("", 10, 0xFF7FA89C, false);
        fluidDescriptionView.setMaxLines(2);
        copy.addView(fluidTitleView, compactWrap());
        copy.addView(fluidDescriptionView, matchWrap(0, dp(2), 0, 0));
        fluidInfoCard.addView(copy, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        updateFluidInfoContent();
        return fluidInfoCard;
    }

    private void crossFadeSelectedTableChange() {
        if (fluidInfoCard == null) {
            updateSelectedTable(true);
            return;
        }
        fluidInfoCard.animate()
            .alpha(0f)
            .setDuration(150)
            .withEndAction(() -> {
                updateSelectedTable(true);
                fluidInfoCard.animate()
                    .alpha(1f)
                    .setDuration(150)
                    .setInterpolator(new DecelerateInterpolator())
                    .start();
            })
            .start();
    }

    private void updateFluidInfoContent() {
        if (fluidTitleView == null || fluidDescriptionView == null || fluidIconFrame == null) {
            return;
        }
        TableSpec spec = selectedSpec();
        fluidTitleView.setText(spec.title);
        fluidDescriptionView.setText(descriptionFor(spec.key));
        fluidIconFrame.removeAllViews();
        int icon = ICON_DATABASE;
        int color = COLOR_PRIMARY;
        if ("water".equals(spec.key)) {
            icon = ICON_DROPLET;
            color = COLOR_PRIMARY;
        } else if ("air".equals(spec.key)) {
            icon = ICON_WIND;
            color = 0xFF85B7EB;
        } else if ("steam".equals(spec.key)) {
            icon = ICON_CLOUD;
            color = 0xFF9FE1CB;
        }
        fluidIconFrame.addView(new NavIconView(this, icon, color), new LinearLayout.LayoutParams(dp(32), dp(32)));
    }

    private View rangeBadgeView() {
        rangeBadge = new LinearLayout(this);
        rangeBadge.setOrientation(LinearLayout.HORIZONTAL);
        rangeBadge.setGravity(Gravity.CENTER);
        rangeBadge.setPadding(dp(10), dp(4), dp(10), dp(4));
        rangeBadge.setBackground(roundedStroke(0xFF2A1E06, COLOR_ACCENT, dp(20)));

        NavIconView icon = new NavIconView(this, ICON_THERMOMETER, COLOR_ACCENT);
        rangeBadge.addView(icon, new LinearLayout.LayoutParams(dp(18), dp(18)));
        rangeBadgeText = text("", 10, COLOR_ACCENT, true);
        rangeBadgeText.setGravity(Gravity.CENTER_VERTICAL);
        rangeBadge.addView(rangeBadgeText, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, dp(22)));

        rangeBadge.setOnLongClickListener(view -> {
            showRangeTooltip(true);
            return true;
        });
        rangeBadge.setOnTouchListener((view, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                showRangeTooltip(false);
            }
            return false;
        });
        rangeBadge.post(() -> {
            rangeBadge.setAlpha(0f);
            rangeBadge.animate().alpha(1f).setDuration(200).start();
        });
        return rangeBadge;
    }

    private View rangeTooltipView() {
        rangeTooltip = text("", 11, COLOR_MUTED, false);
        rangeTooltip.setPadding(dp(10), dp(8), dp(10), dp(8));
        rangeTooltip.setBackground(rounded(0xFF1A2724, dp(8)));
        rangeTooltip.setVisibility(View.GONE);
        return rangeTooltip;
    }

    private void showRangeTooltip(boolean visible) {
        if (rangeTooltip == null) {
            return;
        }
        if (visible) {
            rangeTooltip.setText("El rango indica los límites disponibles para interpolar con datos confiables del fluido seleccionado.");
            rangeTooltip.setAlpha(0f);
            rangeTooltip.setVisibility(View.VISIBLE);
            rangeTooltip.animate().alpha(1f).setDuration(150).start();
        } else {
            rangeTooltip.setVisibility(View.GONE);
        }
    }

    private View temperatureFieldView() {
        temperatureField = new FrameLayout(this);
        temperatureField.setBackground(roundedStroke(0xFF1E2F2B, withAlpha(COLOR_PRIMARY, 102), dp(10)));
        temperatureField.addView(temperatureInput, new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        ));

        floatingLabel = text("Temperatura del fluido", 13, 0xFF7FA89C, false);
        floatingLabel.setGravity(Gravity.CENTER_VERTICAL);
        floatingLabel.setPadding(dp(12), 0, 0, 0);
        floatingLabel.setOnClickListener(view -> temperatureInput.requestFocus());
        temperatureField.addView(floatingLabel, new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        ));

        temperatureSuffix = text("°C", 12, 0xFF7FA89C, false);
        temperatureSuffix.setGravity(Gravity.CENTER);
        temperatureSuffix.setOnClickListener(view -> temperatureInput.requestFocus());
        FrameLayout.LayoutParams suffixParams = new FrameLayout.LayoutParams(dp(38), FrameLayout.LayoutParams.MATCH_PARENT);
        suffixParams.gravity = Gravity.END;
        temperatureField.addView(temperatureSuffix, suffixParams);
        temperatureField.setOnClickListener(view -> temperatureInput.requestFocus());
        return temperatureField;
    }

    private View validationErrorView() {
        validationError = text("", 10, 0xFFD85A30, false);
        validationError.setVisibility(View.GONE);
        return validationError;
    }

    private View calculateButtonView() {
        FrameLayout button = new FrameLayout(this);
        button.setBackground(rippleBackground(COLOR_PRIMARY, 0x33000000, dp(12)));
        calculateCta = text("Calcular", 14, 0xFF04342C, false);
        calculateCta.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        calculateCta.setGravity(Gravity.CENTER);
        button.addView(calculateCta, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        calculateShimmer = new ShimmerView(this);
        calculateShimmer.setVisibility(View.GONE);
        button.addView(calculateShimmer, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        button.setOnClickListener(view -> handleCalculatePress());
        button.setOnTouchListener((view, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                view.animate().scaleX(0.97f).scaleY(0.97f).setDuration(80).start();
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                view.animate().scaleX(1f).scaleY(1f).setDuration(120).start();
            }
            return false;
        });
        return button;
    }

    private View clearInputButton() {
        LinearLayout clear = new LinearLayout(this);
        clear.setOrientation(LinearLayout.HORIZONTAL);
        clear.setGravity(Gravity.CENTER);
        clear.setPadding(0, 0, 0, 0);
        NavIconView icon = new NavIconView(this, ICON_TRASH, 0xFF7FA89C);
        clear.addView(icon, new LinearLayout.LayoutParams(dp(22), dp(44)));
        TextView label = text("Limpiar entrada", 11, 0xFF7FA89C, false);
        label.setGravity(Gravity.CENTER_VERTICAL);
        clear.addView(label, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, dp(44)));
        clear.setOnTouchListener((view, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                label.setTextColor(COLOR_TEXT);
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                label.setTextColor(0xFF7FA89C);
            }
            return false;
        });
        clear.setOnClickListener(view -> clearInputAnimated());
        return clear;
    }

    private LinearLayout section(String title) {
        return section(title, 20);
    }

    private LinearLayout section(String title, int titleSize) {
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        TextView titleView = text(title, titleSize, COLOR_TEXT, true);
        titleView.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        content.addView(titleView, compactWrap());
        return content;
    }

    private View feature(String title, String detail) {
        TextView view = body(title + ": " + detail);
        view.setTextSize(14);
        view.setPadding(dp(12), dp(10), dp(12), dp(10));
        view.setBackground(roundedStroke(COLOR_SURFACE_2, COLOR_BORDER, dp(8)));
        return wrapWithMargin(view, 0, dp(8), 0, 0);
    }

    private void updateSelectedTable() {
        updateSelectedTable(true);
    }

    private void updateSelectedTable(boolean resetResults) {
        TableSpec spec = selectedSpec();
        PropertyTable table = tables.get(spec.key);
        updateFluidInfoContent();
        if (table != null) {
            if (rangeBadgeText != null) {
                rangeBadgeText.setText(String.format(Locale.US, " %.2f °C — %.2f °C", table.minTemperature(), table.maxTemperature()));
            }
        }
        if (resetResults) {
            temperatureInput.setText("");
            clearResults();
        }
        validateTemperatureInput(false);
        updateFloatingLabel(true);
    }

    private boolean validateTemperatureInput(boolean animate) {
        if (temperatureInput == null || temperatureField == null) {
            return false;
        }
        String raw = temperatureInput.getText().toString().trim().replace(",", ".");
        boolean hasValue = !raw.isEmpty();
        boolean valid = false;
        String error = "";
        TableSpec spec = selectedSpec();
        PropertyTable table = tables.get(spec.key);
        if (hasValue && table != null) {
            try {
                double value = Double.parseDouble(raw);
                valid = value >= table.minTemperature() && value <= table.maxTemperature();
                if (!valid) {
                    error = String.format(Locale.US, "Fuera del rango permitido: %.2f–%.2f °C", table.minTemperature(), table.maxTemperature());
                }
            } catch (NumberFormatException exc) {
                error = "Ingrese una temperatura válida";
            }
        }

        int border = valid || !hasValue ? withAlpha(COLOR_PRIMARY, 102) : 0xFFD85A30;
        if (temperatureInput.hasFocus() && (valid || !hasValue)) {
            border = COLOR_PRIMARY;
        }
        temperatureField.setBackground(roundedStroke(0xFF1E2F2B, border, dp(10)));

        if (validationError != null) {
            if (!error.isEmpty()) {
                validationError.setText(error);
                if (validationError.getVisibility() != View.VISIBLE) {
                    validationError.setVisibility(View.VISIBLE);
                    if (animate) {
                        animateRevealUp(validationError, 0);
                    }
                }
            } else {
                validationError.setVisibility(View.GONE);
            }
        }
        updateCalculateState(valid);
        return valid;
    }

    private void updateFloatingLabel(boolean animate) {
        if (floatingLabel == null || temperatureInput == null) {
            return;
        }
        boolean floated = temperatureInput.hasFocus() || temperatureInput.getText().length() > 0;
        float targetY = floated ? -dp(14) : 0f;
        float targetScale = floated ? 0.85f : 1f;
        floatingLabel.setTextColor(floated ? COLOR_PRIMARY : 0xFF7FA89C);
        floatingLabel.setTextSize(floated ? 10 : 13);
        if (animate) {
            floatingLabel.animate()
                .translationY(targetY)
                .scaleX(targetScale)
                .scaleY(targetScale)
                .setDuration(150)
                .setInterpolator(new DecelerateInterpolator())
                .start();
        } else {
            floatingLabel.setTranslationY(targetY);
            floatingLabel.setScaleX(targetScale);
            floatingLabel.setScaleY(targetScale);
        }
    }

    private void updateCalculateState(boolean valid) {
        if (calculateCta == null) {
            return;
        }
        View parent = (View) calculateCta.getParent();
        parent.setBackground(rippleBackground(valid ? COLOR_PRIMARY : 0xFF1E2F2B, 0x33000000, dp(12)));
        calculateCta.setTextColor(valid ? 0xFF04342C : 0xFF4A6B62);
    }

    private void handleCalculatePress() {
        boolean valid = validateTemperatureInput(true);
        if (!valid) {
            shakeView((View) calculateCta.getParent());
            return;
        }
        startCalculateLoading();
        if (calculateCta != null) {
            calculateCta.postDelayed(this::calculate, 350);
        } else {
            calculate();
        }
    }

    private void startCalculateLoading() {
        if (calculateCta == null || calculateShimmer == null) {
            return;
        }
        calculateCta.animate().alpha(0f).setDuration(100).start();
        calculateShimmer.setVisibility(View.VISIBLE);
        calculateShimmer.start();
    }

    private void stopCalculateLoading() {
        if (calculateCta != null) {
            calculateCta.animate().alpha(1f).setDuration(100).start();
        }
        if (calculateShimmer != null) {
            calculateShimmer.stop();
            calculateShimmer.setVisibility(View.GONE);
        }
    }

    private void shakeView(View view) {
        if (view == null) {
            return;
        }
        view.animate().cancel();
        view.animate().translationX(dp(4)).setDuration(50).withEndAction(() ->
            view.animate().translationX(-dp(4)).setDuration(50).withEndAction(() ->
                view.animate().translationX(dp(4)).setDuration(50).withEndAction(() ->
                    view.animate().translationX(0f).setDuration(50).start()
                ).start()
            ).start()
        ).start();
    }

    private void clearInputAnimated() {
        if (temperatureInput == null) {
            return;
        }
        temperatureInput.animate()
            .alpha(0f)
            .setDuration(100)
            .withEndAction(() -> {
                temperatureInput.setText("");
                temperatureInput.setAlpha(1f);
                clearResults();
                updateFloatingLabel(true);
                validateTemperatureInput(false);
            })
            .start();
    }

    private void calculate() {
        TableSpec spec = selectedSpec();
        PropertyTable table = tables.get(spec.key);
        if (table == null) {
            stopCalculateLoading();
            showMessage("No se pudo cargar la tabla");
            return;
        }

        double temperature;
        try {
            temperature = Double.parseDouble(temperatureInput.getText().toString().trim().replace(",", "."));
        } catch (NumberFormatException exc) {
            stopCalculateLoading();
            showMessage("Ingrese una temperatura valida");
            return;
        }

        if (temperature < table.minTemperature() || temperature > table.maxTemperature()) {
            stopCalculateLoading();
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
        exportPdfButtonView = null;
        exportPdfText = null;
        exportPdfIcon = null;
        exportPdfSpinner = null;

        for (int i = 0; i < lastPdfEntries.size(); i++) {
            View row = resultPropertyCard(lastPdfEntries.get(i));
            int bottom = i == lastPdfEntries.size() - 1 ? 0 : dp(8);
            resultsLayout.addView(row, matchWrap(0, 0, 0, bottom));
            animateView(row, i * 45L);
        }

        if (!lastPdfEntries.isEmpty()) {
            View pdfButton = exportPdfButtonItem();
            resultsLayout.addView(pdfButton, matchWrap(0, 0, 0, 0));
            animateView(pdfButton, lastPdfEntries.size() * 45L);
        }
    }

    private View exportPdfButtonItem() {
        FrameLayout wrapper = new FrameLayout(this);
        wrapper.setPadding(0, dp(16), 0, dp(16));

        LinearLayout button = new LinearLayout(this);
        exportPdfButtonView = button;
        button.setOrientation(LinearLayout.HORIZONTAL);
        button.setGravity(Gravity.CENTER);
        button.setPadding(dp(16), 0, dp(16), 0);
        button.setBackground(rippleBackground(COLOR_ACCENT, 0x33000000, dp(12)));
        button.setOnClickListener(view -> handleExportPdfPress());
        button.setOnTouchListener((view, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                view.animate().scaleX(0.97f).scaleY(0.97f).setDuration(80).start();
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                view.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
            }
            return false;
        });

        exportPdfIcon = new NavIconView(this, ICON_REPORT, 0xFF412402);
        button.addView(exportPdfIcon, new LinearLayout.LayoutParams(dp(24), dp(24)));

        exportPdfSpinner = new SmallSpinnerView(this, 0xFF412402);
        exportPdfSpinner.setVisibility(View.GONE);
        button.addView(exportPdfSpinner, new LinearLayout.LayoutParams(dp(22), dp(22)));

        exportPdfText = text("Exportar PDF", 14, 0xFF412402, false);
        exportPdfText.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        textParams.leftMargin = dp(8);
        button.addView(exportPdfText, textParams);

        FrameLayout.LayoutParams buttonParams = new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            dp(52)
        );
        buttonParams.gravity = Gravity.TOP;
        wrapper.addView(button, buttonParams);
        return wrapper;
    }

    private View resultPropertyCard(ResultEntry entry) {
        LinearLayout card = new LinearLayout(this);
        String tableKey = tableKeyForEntry(entry);
        int trend = trendType(entry);
        int trendColor = trendColor(trend);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(0, 0, 0, 0);
        card.setBackground(roundedStroke(COLOR_SURFACE, 0xFF253D37, dp(12)));
        card.setElevation(dp(2));

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(10), dp(9), dp(10), dp(7));
        TextView name = text(entry.label.toUpperCase(Locale.US), 9, 0xFF7FA89C, false);
        name.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        header.addView(name, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        TextView trendBadge = text(trendLabel(trend), 9, trendColor, false);
        trendBadge.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        trendBadge.setPadding(dp(7), dp(2), dp(7), dp(2));
        trendBadge.setBackground(rounded(0xFF1D4A3C, dp(5)));
        header.addView(trendBadge, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        card.addView(header, compactWrap());
        card.addView(thinResultDivider(), new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Math.max(1, (int) dp(0.5f))));

        LinearLayout valueRow = new LinearLayout(this);
        valueRow.setOrientation(LinearLayout.HORIZONTAL);
        valueRow.setGravity(Gravity.CENTER_VERTICAL);
        valueRow.setPadding(dp(10), dp(8), dp(10), dp(8));
        valueRow.addView(symbolView(formulaMeta(entry).symbol, 20, COLOR_PRIMARY, true), new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        TextView value = text(" " + valueOnly(entry), 17, COLOR_PRIMARY, false);
        value.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.BOLD));
        valueRow.addView(value, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        String unitBadge = unitBadge(entry);
        if (!unitBadge.isEmpty()) {
            TextView unit = text(" " + unitBadge, 11, 0xFF7FA89C, false);
            valueRow.addView(unit, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        }
        card.addView(valueRow, compactWrap());
        card.addView(thinResultDivider(), new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Math.max(1, (int) dp(0.5f))));
        card.addView(formulaBlock(entry), compactWrap());
        card.addView(thinResultDivider(), new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Math.max(1, (int) dp(0.5f))));
        card.addView(descriptionBlock(entry.explanation), compactWrap());
        card.addView(thinResultDivider(), new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Math.max(1, (int) dp(0.5f))));

        PropertyTable table = tables.get(tableKey);
        PropertySpec prop = propertyForEntry(entry);
        if (table != null && prop != null && !Double.isNaN(entry.temperature)) {
            MiniTrendChartView miniChart = new MiniTrendChartView(this);
            miniChart.setData(table, prop, entry.temperature, trendColor);
            card.addView(miniChart, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(40)));
        }
        return card;
    }

    private View thinResultDivider() {
        View divider = new View(this);
        divider.setBackgroundColor(0xFF1D3530);
        return divider;
    }

    private String valueOnly(ResultEntry entry) {
        if (entry.unit == null || entry.unit.isEmpty()) {
            return entry.value;
        }
        String suffix = " " + entry.unit;
        if (entry.value.endsWith(suffix)) {
            return entry.value.substring(0, entry.value.length() - suffix.length()).trim();
        }
        return entry.value;
    }

    private View formulaBlock(ResultEntry entry) {
        return new FormulaBlockView(this, entry);
    }

    private GradientDrawable formulaBackground() {
        GradientDrawable drawable = rounded(0xFF1E2F2B, dp(6));
        drawable.setCornerRadii(new float[] {
            0, 0,
            dp(6), dp(6),
            dp(6), dp(6),
            0, 0
        });
        drawable.setStroke(dp(1), 0xFF1E2F2B);
        return drawable;
    }

    private View descriptionBlock(String value) {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(10), dp(7), dp(10), dp(7));
        TextView description = text(value, 10, 0xFF7FA89C, false);
        description.setLineSpacing(0, 1.18f);
        description.setMaxLines(2);
        description.setEllipsize(android.text.TextUtils.TruncateAt.END);
        box.addView(description, compactWrap());
        TextView toggle = text("Ver mas", 11, COLOR_PRIMARY, false);
        toggle.setVisibility(View.GONE);
        toggle.setPadding(0, dp(3), 0, 0);
        box.addView(toggle, compactWrap());
        description.post(() -> {
            if (description.getLineCount() > 2 || value.length() > 90) {
                toggle.setVisibility(View.VISIBLE);
            }
        });
        toggle.setOnClickListener(view -> {
            boolean expanded = description.getMaxLines() > 2;
            description.animate().alpha(0.55f).setDuration(80).withEndAction(() -> {
                description.setMaxLines(expanded ? 2 : Integer.MAX_VALUE);
                description.setEllipsize(expanded ? android.text.TextUtils.TruncateAt.END : null);
                toggle.setText(expanded ? "Ver mas" : "Ver menos");
                description.animate().alpha(1f).setDuration(120).start();
            }).start();
        });
        return box;
    }

    private FrameLayout.LayoutParams compactFrame() {
        return new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
    }

    private String mathSymbol(ResultEntry entry) {
        return formulaMeta(entry).symbol;
    }

    private View symbolView(String symbol, int sp, int color, boolean dependent) {
        if ("cp".equals(symbol) || "P_sat".equals(symbol)) {
            LinearLayout group = new LinearLayout(this);
            group.setOrientation(LinearLayout.HORIZONTAL);
            group.setGravity(Gravity.BOTTOM);
            String base = "cp".equals(symbol) ? "c" : "P";
            String sub = "cp".equals(symbol) ? "p" : "sat";
            TextView baseView = text(base, sp, color, false);
            baseView.setTypeface(Typeface.create("serif", Typeface.ITALIC));
            group.addView(baseView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            TextView subView = text(sub, Math.max(8, sp - 8), color, false);
            subView.setTypeface(Typeface.create("serif", Typeface.ITALIC));
            group.addView(subView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            return group;
        }
        TextView view = text(displaySymbol(symbol), dependent ? sp : Math.max(12, sp - 3), color, false);
        view.setTypeface(Typeface.create("serif", Typeface.ITALIC));
        return view;
    }

    private String displaySymbol(String symbol) {
        if ("beta".equals(symbol)) return "β";
        if ("rho".equals(symbol)) return "ρ";
        if ("alpha".equals(symbol)) return "α";
        if ("mu".equals(symbol)) return "μ";
        if ("nu".equals(symbol)) return "ν";
        if ("tau".equals(symbol)) return "τ";
        return symbol;
    }

    private String unitBadge(ResultEntry entry) {
        String label = entry.label == null ? "" : entry.label.toLowerCase(Locale.US);
        String unit = entry.unit == null ? "" : entry.unit;
        if (label.contains("expansi")) {
            return "×10⁻³ K⁻¹";
        }
        if (label.contains("densidad")) {
            return "kg/m³";
        }
        if (label.contains("calor")) {
            return "kJ/kg·K";
        }
        if (label.contains("conductividad")) {
            return "W/m·K";
        }
        if (label.contains("difusividad")) {
            return "×10⁻⁶ m²/s";
        }
        if (label.contains("viscosidad absoluta")) {
            return "×10⁻⁶ Pa·s";
        }
        if (label.contains("viscosidad cin")) {
            return "×10⁻⁶ m²/s";
        }
        if (label.contains("prandtl")) {
            return "adim.";
        }
        return normalizeUnit(unit);
    }

    private String normalizeUnit(String unit) {
        if (unit == null || unit.isEmpty()) {
            return "";
        }
        return unit
            .replace("x 10^-3 ", "×10⁻³ ")
            .replace("x 10-6 ", "×10⁻⁶ ")
            .replace("x 10^-6 ", "×10⁻⁶ ")
            .replace("x10^-6 ", "×10⁻⁶ ")
            .replace("x10^-3 ", "×10⁻³ ")
            .replace("K-1", "K⁻¹")
            .replace("K^-1", "K⁻¹")
            .replace("Pa s", "Pa·s")
            .replace("Pa*s", "Pa·s")
            .replace("m K", "m·K")
            .replace("m*K", "m·K")
            .replace("kg K", "kg·K")
            .replace("kg*K", "kg·K")
            .replace("m2/s", "m²/s")
            .replace("m3", "m³");
    }

    private String evaluationValue(ResultEntry entry) {
        return valueOnly(entry);
    }

    private String formulaText(ResultEntry entry) {
        FormulaMeta meta = formulaMeta(entry);
        String eval = String.format(Locale.US, "@ %.2f °C = %s %s", entry.temperature, evaluationValue(entry), unitBadge(entry));
        return meta.copyExpression + "\nMetodo: spline cubica interpolada\n" + eval.trim();
    }

    private String formulaExpression(String label) {
        String lower = label == null ? "" : label.toLowerCase(Locale.US);
        if (lower.contains("densidad")) {
            return "rho = m / V";
        }
        if (lower.contains("pres")) {
            return "P_sat = f(T)";
        }
        if (lower.contains("expansi")) {
            return "beta = (1 / V) (dV / dT)";
        }
        if (lower.contains("calor")) {
            return "c_p = dq / (m dT)";
        }
        if (lower.contains("conductividad")) {
            return "k = q L / (A dT)";
        }
        if (lower.contains("difusividad")) {
            return "alpha = k / (rho c_p)";
        }
        if (lower.contains("viscosidad cin")) {
            return "nu = mu / rho";
        }
        if (lower.contains("viscosidad")) {
            return "mu = tau / (du / dy)";
        }
        if (lower.contains("prandtl")) {
            return "Pr = nu / alpha";
        }
        if (lower.contains("entalp")) {
            return "h = u + p v";
        }
        if (lower.contains("entrop")) {
            return "ds = dq_rev / T";
        }
        return "propiedad = f(T)";
    }

    private void copyFormula(ResultEntry entry) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            clipboard.setPrimaryClip(ClipData.newPlainText("formula", formulaText(entry)));
        }
    }

    private void showVariableTooltip(View anchor, ResultEntry entry, String token) {
        LinearLayout popupContent = new LinearLayout(this);
        popupContent.setOrientation(LinearLayout.VERTICAL);
        popupContent.setPadding(dp(9), dp(6), dp(9), dp(6));
        popupContent.setBackground(roundedStroke(0xFF132320, 0xFF1D4A3C, dp(6)));

        String normalized = token.replace("d", "");
        TextView title = text(token + " — " + variableName(normalized), 11, COLOR_TEXT, false);
        popupContent.addView(title, compactWrap());
        TextView value = text(variableValue(entry, normalized), 11, COLOR_PRIMARY, false);
        value.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL));
        popupContent.addView(value, matchWrap(0, dp(2), 0, 0));
        popupContent.addView(text("unidad: " + variableUnit(entry, normalized), 10, 0xFF7FA89C, false), matchWrap(0, dp(2), 0, 0));

        PopupWindow popup = new PopupWindow(popupContent, Math.min(dp(200), getResources().getDisplayMetrics().widthPixels - dp(48)), LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popup.setOutsideTouchable(true);
        popup.setElevation(0f);
        popup.showAsDropDown(anchor, 0, dp(4));
        anchor.postDelayed(popup::dismiss, 3000);
    }

    private String variableName(String token) {
        if ("beta".equals(token)) return "coeficiente de expansión volumétrica";
        if ("rho".equals(token)) return "densidad";
        if ("cp".equals(token)) return "calor específico";
        if ("P_sat".equals(token)) return "presión de saturación";
        if ("h".equals(token)) return "entalpía";
        if ("s".equals(token)) return "entropía";
        if ("k".equals(token)) return "conductividad térmica";
        if ("alpha".equals(token)) return "difusividad térmica";
        if ("mu".equals(token)) return "viscosidad absoluta";
        if ("nu".equals(token)) return "viscosidad cinemática";
        if ("Pr".equals(token)) return "número de Prandtl";
        if ("m".equals(token)) return "masa";
        if ("V".equals(token)) return "volumen";
        if ("T".equals(token)) return "temperatura";
        if ("u".equals(token)) return "energía interna o velocidad";
        if ("q".equals(token)) return "calor transferido";
        if ("l".equals(token)) return "longitud";
        if ("A".equals(token)) return "área";
        if ("tau".equals(token)) return "esfuerzo cortante";
        if ("y".equals(token)) return "coordenada normal";
        if ("p".equals(token)) return "presión";
        if ("v".equals(token)) return "volumen específico";
        return "variable de ecuación";
    }

    private String variableValue(ResultEntry entry, String token) {
        if (mathSymbol(entry).equals(token)) {
            return "= " + evaluationValue(entry);
        }
        if ("T".equals(token)) {
            return String.format(Locale.US, "= %.2f", entry.temperature);
        }
        return "= variable del modelo";
    }

    private String variableUnit(ResultEntry entry, String token) {
        if (mathSymbol(entry).equals(token)) {
            return unitBadge(entry);
        }
        if ("T".equals(token)) {
            return "°C";
        }
        return "segun definicion";
    }

    private FormulaMeta formulaMeta(ResultEntry entry) {
        String label = entry == null || entry.label == null ? "" : entry.label.toLowerCase(Locale.US);
        if (label.contains("pres")) {
            return new FormulaMeta("P_sat", "P_sat = f(T)", "Propiedad de saturación obtenida como función de la temperatura.", "[P_sat] = kPa", "T");
        }
        if (label.contains("expansi")) {
            return new FormulaMeta("beta", "β = (1/V)·(dV/dT)", "Derivada de la definición termodinámica del cambio relativo de volumen con la temperatura.", "[β] = K⁻¹", "1", "V", "dV", "dT");
        }
        if (label.contains("densidad")) {
            return new FormulaMeta("rho", "ρ = m/V", "Relación directa entre masa y volumen ocupado por el fluido.", "[ρ] = kg·m⁻³", "m", "V");
        }
        if (label.contains("calor")) {
            return new FormulaMeta("cp", "cp = du/(dm·dT)", "Energía requerida para cambiar la temperatura de una masa diferencial.", "[cp] = kJ·kg⁻¹·K⁻¹", "du", "dm", "dT");
        }
        if (label.contains("conductividad")) {
            return new FormulaMeta("k", "k = (q·l)/(A·dT)", "Forma de Fourier para conducción térmica en una dirección.", "[k] = W·m⁻¹·K⁻¹", "q", "l", "A", "dT");
        }
        if (label.contains("difusividad")) {
            return new FormulaMeta("alpha", "α = k/(ρ·cp)", "Relación entre conducción térmica y capacidad de almacenamiento de energía.", "[α] = m²·s⁻¹", "k", "rho", "cp");
        }
        if (label.contains("viscosidad cin")) {
            return new FormulaMeta("nu", "ν = μ/ρ", "Viscosidad dinámica normalizada por la densidad del fluido.", "[ν] = m²·s⁻¹", "mu", "rho");
        }
        if (label.contains("viscosidad")) {
            return new FormulaMeta("mu", "μ = τ·(du/dy)", "Relación entre esfuerzo cortante y gradiente de velocidad.", "[μ] = Pa·s", "tau", "du", "dy");
        }
        if (label.contains("prandtl")) {
            return new FormulaMeta("Pr", "Pr = (μ·cp)/k", "Número adimensional que compara difusión de momento y difusión térmica.", "[Pr] = adim.", "mu", "cp", "k");
        }
        if (label.contains("entalp")) {
            return new FormulaMeta("h", "h = u + p·v", "Propiedad energética usada en balances de energía de vapor y líquido saturado.", "[h] = kJ·kg⁻¹", "u", "p", "v");
        }
        if (label.contains("entrop")) {
            return new FormulaMeta("s", "ds = dq_rev/T", "Propiedad de estado asociada a la irreversibilidad y transferencia reversible de calor.", "[s] = kJ·kg⁻¹·K⁻¹", "q", "T");
        }
        return new FormulaMeta("f", "f = f(T)", "Propiedad interpolada como función de la temperatura consultada.", "[f] = unidad de tabla", "T");
    }

    private int trendType(ResultEntry entry) {
        PropertyTable table = tables.get(tableKeyForEntry(entry));
        PropertySpec prop = propertyForEntry(entry);
        if (table == null || prop == null) {
            return 0;
        }
        double min = table.minTemperature();
        double max = table.maxTemperature();
        double previous = table.valueFor(prop.column, min);
        int direction = 0;
        for (int i = 1; i <= 20; i++) {
            double x = min + (max - min) * i / 20.0;
            double current = table.valueFor(prop.column, x);
            double delta = current - previous;
            int step = Math.abs(delta) < 1e-9 ? 0 : (delta > 0 ? 1 : -1);
            if (step != 0) {
                if (direction == 0) {
                    direction = step;
                } else if (direction != step) {
                    return 0;
                }
            }
            previous = current;
        }
        return direction == 0 ? 0 : direction;
    }

    private String trendLabel(int trend) {
        if (trend > 0) {
            return "creciente";
        }
        if (trend < 0) {
            return "decreciente";
        }
        return "no lineal";
    }

    private int trendColor(int trend) {
        if (trend > 0) {
            return COLOR_PRIMARY;
        }
        if (trend < 0) {
            return COLOR_ACCENT;
        }
        return 0xFFD85A30;
    }

    private TableSpec selectedSpec() {
        selectedTableIndex = Math.max(0, Math.min(TABLES.length - 1, selectedTableIndex));
        return TABLES[selectedTableIndex];
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

    private View historyCard(View child) {
        LinearLayout wrapper = new LinearLayout(this);
        wrapper.setOrientation(LinearLayout.VERTICAL);
        wrapper.setPadding(dp(16), dp(16), dp(16), dp(8));
        wrapper.setBackground(roundedStroke(COLOR_SURFACE, COLOR_BORDER, dp(12)));
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
        view.setTypeface(Typeface.create("sans-serif", bold ? Typeface.BOLD : Typeface.NORMAL));
        return view;
    }

    private TextView body(String value) {
        return text(value, 15, COLOR_MUTED, false);
    }

    private Button button(String value, int background, int textColor) {
        Button button = new Button(this);
        button.setText(value);
        button.setTextColor(textColor);
        button.setTextSize(14);
        button.setAllCaps(false);
        button.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        button.setBackground(rippleBackground(background, withAlpha(COLOR_PRIMARY, 70), dp(8)));
        button.setOnTouchListener((view, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                view.animate().scaleX(0.97f).scaleY(0.97f).setDuration(90).start();
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
                }
                view.animate().scaleX(1f).scaleY(1f).setDuration(120).start();
            }
            return false;
        });
        return button;
    }

    private void addHistory(String title, List<String> lines, List<ResultEntry> entries, String tableKey, double temperature) {
        String time = new SimpleDateFormat("HH:mm", Locale.US).format(new Date());
        String dateKey = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
        history.add(0, new CalculationRecord(dateKey, time, title, new ArrayList<>(lines), new ArrayList<>(entries), tableKey, temperature));
        while (history.size() > 8) {
            history.remove(history.size() - 1);
        }
        saveHistory();
        updateBottomNav();
        renderHistory();
    }

    private void renderHistory() {
        if (historyLayout == null) {
            return;
        }
        historyLayout.removeAllViews();
        updateBottomNav();
        historyHeaderViews.clear();
        historyLayout.addView(historyScreenHeader(), matchWrap(0, 0, 0, 14));
        if (history.isEmpty()) {
            if (historyStickyHeader != null) {
                historyStickyHeader.setVisibility(View.GONE);
            }
            historyLayout.addView(historyEmptyState(), new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(420)));
            return;
        }

        String currentSection = "";
        for (CalculationRecord record : history) {
            String section = historyDateHeader(record.dateKey);
            if (!section.equals(currentSection)) {
                View header = historySectionHeader(section);
                historyHeaderViews.add(header);
                historyLayout.addView(header, matchWrap(0, currentSection.isEmpty() ? 0 : dp(12), 0, dp(8)));
                currentSection = section;
            }
            historyLayout.addView(historyRecordCard(record), matchWrap(0, 0, 0, dp(8)));
        }
        historyLayout.post(this::updateHistoryStickyHeader);
    }

    private void updateHistoryStickyHeader() {
        if (historyStickyHeader == null || historyStickyText == null) {
            return;
        }
        if (currentScreen != SCREEN_HISTORY || history.isEmpty() || historyHeaderViews.isEmpty()) {
            historyStickyHeader.setVisibility(View.GONE);
            return;
        }
        int stickyTop = dp(88);
        int[] pos = new int[2];
        String active = "";
        for (View header : historyHeaderViews) {
            header.getLocationOnScreen(pos);
            if (pos[1] <= stickyTop + dp(6)) {
                Object tag = header.getTag();
                active = tag == null ? "" : tag.toString();
            }
        }
        if (active.isEmpty()) {
            historyStickyHeader.setVisibility(View.GONE);
            return;
        }
        historyStickyText.setText(active.toUpperCase(Locale.US));
        historyStickyHeader.setVisibility(View.VISIBLE);
    }

    private View historyScreenHeader() {
        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.VERTICAL);

        LinearLayout topRow = new LinearLayout(this);
        topRow.setOrientation(LinearLayout.HORIZONTAL);
        topRow.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout titleRow = new LinearLayout(this);
        titleRow.setOrientation(LinearLayout.HORIZONTAL);
        titleRow.setGravity(Gravity.CENTER_VERTICAL);

        TextView title = text("Historial", 18, COLOR_TEXT, false);
        title.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        titleRow.addView(title, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView badge = text(String.valueOf(history.size()), 9, COLOR_PRIMARY, true);
        badge.setGravity(Gravity.CENTER);
        badge.setBackground(rounded(0xFF1D4A3C, dp(9)));
        LinearLayout.LayoutParams badgeParams = new LinearLayout.LayoutParams(dp(18), dp(18));
        badgeParams.setMargins(dp(8), 0, 0, 0);
        titleRow.addView(badge, badgeParams);

        topRow.addView(titleRow, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        NavIconView dots = new NavIconView(this, ICON_DOTS, 0xFF7FA89C);
        dots.setBackground(rippleBackground(0x00000000, withAlpha(COLOR_PRIMARY, 31), dp(20)));
        dots.setOnClickListener(this::showHistoryOverflowMenu);
        topRow.addView(dots, new LinearLayout.LayoutParams(dp(44), dp(44)));
        header.addView(topRow, compactWrap());

        TextView subtitle = body("Reabre, exporta o elimina cálculos guardados.");
        subtitle.setTextSize(12);
        header.addView(subtitle, matchWrap(0, dp(4), 0, 0));
        return header;
    }

    private View historySectionHeader(String label) {
        LinearLayout wrapper = new LinearLayout(this);
        wrapper.setOrientation(LinearLayout.VERTICAL);
        wrapper.setTag(label);
        TextView header = text(label.toUpperCase(Locale.US), 9, 0xFF3A5A52, false);
        header.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        header.setLetterSpacing(0.06f);
        wrapper.addView(header, compactWrap());
        View line = new View(this);
        line.setBackgroundColor(0xFF1D3530);
        LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Math.max(1, (int) dp(0.5f)));
        lineParams.setMargins(0, dp(6), 0, 0);
        wrapper.addView(line, lineParams);
        return wrapper;
    }

    private LinearLayout stickyHistoryHeaderView() {
        LinearLayout wrapper = new LinearLayout(this);
        wrapper.setOrientation(LinearLayout.VERTICAL);
        historyStickyText = text("", 9, 0xFF3A5A52, false);
        historyStickyText.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        historyStickyText.setLetterSpacing(0.06f);
        wrapper.addView(historyStickyText, compactWrap());
        View line = new View(this);
        line.setBackgroundColor(0xFF1D3530);
        LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Math.max(1, (int) dp(0.5f)));
        lineParams.setMargins(0, dp(6), 0, 0);
        wrapper.addView(line, lineParams);
        return wrapper;
    }

    private View historyRecordCard(CalculationRecord record) {
        FrameLayout shell = new FrameLayout(this);
        LinearLayout deleteAction = new LinearLayout(this);
        deleteAction.setGravity(Gravity.CENTER);
        deleteAction.setBackgroundColor(0xFF2D1A14);
        FrameLayout.LayoutParams deleteParams = new FrameLayout.LayoutParams(dp(64), dp(58));
        deleteParams.gravity = Gravity.END | Gravity.CENTER_VERTICAL;
        deleteAction.addView(new NavIconView(this, ICON_TRASH, 0xFFD85A30), new LinearLayout.LayoutParams(dp(34), dp(34)));
        shell.addView(deleteAction, deleteParams);

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(dp(12), dp(10), dp(12), dp(10));
        card.setBackground(roundedStroke(COLOR_SURFACE, 0xFF253D37, dp(10)));

        LinearLayout avatar = new LinearLayout(this);
        avatar.setGravity(Gravity.CENTER);
        avatar.setBackground(rounded(0xFF1D4A3C, dp(17)));
        avatar.addView(new NavIconView(this, iconForTable(record.tableKey), colorForTable(record.tableKey)), new LinearLayout.LayoutParams(dp(24), dp(24)));
        card.addView(avatar, new LinearLayout.LayoutParams(dp(34), dp(34)));

        LinearLayout copy = new LinearLayout(this);
        copy.setOrientation(LinearLayout.VERTICAL);
        copy.setPadding(dp(10), 0, dp(8), 0);
        TextView title = text(historyTitle(record), 12, COLOR_TEXT, false);
        title.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        copy.addView(title, compactWrap());

        LinearLayout timeRow = new LinearLayout(this);
        timeRow.setOrientation(LinearLayout.HORIZONTAL);
        timeRow.setGravity(Gravity.CENTER_VERTICAL);
        timeRow.addView(new NavIconView(this, ICON_HISTORY, 0xFF7FA89C), new LinearLayout.LayoutParams(dp(14), dp(14)));
        TextView time = text(" " + historyRelativeTime(record), 10, 0xFF7FA89C, false);
        timeRow.addView(time, compactWrap());
        copy.addView(timeRow, matchWrap(0, dp(3), 0, 0));
        card.addView(copy, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        TextView viewChip = text("Ver", 10, COLOR_PRIMARY, false);
        viewChip.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        viewChip.setGravity(Gravity.CENTER);
        viewChip.setPadding(dp(8), dp(4), dp(8), dp(4));
        viewChip.setBackground(rippleBackground(0x00000000, withAlpha(COLOR_PRIMARY, 31), dp(6)));
        viewChip.setOnClickListener(view -> showHistoryRecord(record));
        card.addView(viewChip, new LinearLayout.LayoutParams(dp(48), dp(30)));

        shell.addView(card, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, dp(58)));
        attachSwipeToDelete(shell, card, record);
        return shell;
    }

    private void attachSwipeToDelete(View shell, View card, CalculationRecord record) {
        final float[] downX = {0f};
        final boolean[] dragging = {false};
        card.setOnTouchListener((view, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                downX[0] = event.getRawX();
                dragging[0] = false;
                return true;
            }
            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                float delta = event.getRawX() - downX[0];
                if (delta < -dp(4)) {
                    dragging[0] = true;
                    view.setTranslationX(Math.max(delta, -view.getWidth()));
                    return true;
                }
            }
            if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                float offset = view.getTranslationX();
                if (dragging[0]) {
                    if (Math.abs(offset) > view.getWidth() * 0.5f) {
                        animateHistoryDelete(shell, view, record);
                    } else {
                        view.animate()
                            .translationX(0f)
                            .setDuration(260)
                            .setInterpolator(new DecelerateInterpolator(1.6f))
                            .start();
                    }
                    return true;
                }
            }
            return false;
        });
    }

    private void animateHistoryDelete(View shell, View card, CalculationRecord record) {
        card.animate()
            .translationX(-Math.max(card.getWidth(), dp(320)))
            .setDuration(200)
            .setInterpolator(new AccelerateDecelerateInterpolator())
            .withEndAction(() -> {
                ValueAnimator collapse = ValueAnimator.ofInt(shell.getHeight(), 0);
                collapse.setDuration(250);
                collapse.addUpdateListener(animation -> {
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) shell.getLayoutParams();
                    params.height = (int) animation.getAnimatedValue();
                    shell.setLayoutParams(params);
                });
                collapse.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        history.remove(record);
                        saveHistory();
                        updateBottomNav();
                        renderHistory();
                    }
                });
                collapse.start();
            })
            .start();
    }

    private View historyEmptyState() {
        LinearLayout empty = new LinearLayout(this);
        empty.setOrientation(LinearLayout.VERTICAL);
        empty.setGravity(Gravity.CENTER);
        empty.addView(new NavIconView(this, ICON_EMPTY_HISTORY, 0xFF3A5A52), new LinearLayout.LayoutParams(dp(60), dp(80)));
        TextView title = text("Sin cálculos guardados", 15, 0xFF7FA89C, false);
        title.setGravity(Gravity.CENTER);
        empty.addView(title, matchWrap(0, dp(16), 0, 0));
        TextView subtitle = text("Realiza tu primer cálculo para verlo aquí", 12, 0xFF3A5A52, false);
        subtitle.setGravity(Gravity.CENTER);
        empty.addView(subtitle, matchWrap(0, dp(6), 0, 0));
        TextView cta = text("Ir a calcular", 13, COLOR_PRIMARY, false);
        cta.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        cta.setGravity(Gravity.CENTER);
        cta.setPadding(dp(24), dp(10), dp(24), dp(10));
        cta.setBackground(rippleBackground(0x00000000, withAlpha(COLOR_PRIMARY, 31), dp(24)));
        cta.setOnClickListener(view -> showCalculatorScreen());
        empty.addView(cta, matchWrap(0, dp(20), 0, 0));
        empty.setAlpha(0f);
        empty.post(() -> empty.animate().alpha(1f).setDuration(300).start());
        return empty;
    }

    private void showHistoryOverflowMenu(View anchor) {
        if (historyMenuPopup != null && historyMenuPopup.isShowing()) {
            historyMenuPopup.dismiss();
            return;
        }
        LinearLayout menu = new LinearLayout(this);
        menu.setOrientation(LinearLayout.HORIZONTAL);
        menu.setGravity(Gravity.CENTER_VERTICAL);
        menu.setPadding(dp(12), dp(8), dp(12), dp(8));
        menu.setBackground(roundedStroke(COLOR_SURFACE, 0xFF253D37, dp(10)));

        menu.addView(new NavIconView(this, ICON_TRASH, 0xFFD85A30), new LinearLayout.LayoutParams(dp(24), dp(24)));
        TextView label = text("Borrar todo", 13, 0xFFD85A30, false);
        label.setPadding(dp(8), 0, 0, 0);
        menu.addView(label, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, dp(36)));
        menu.setOnClickListener(view -> {
            if (historyMenuPopup != null) {
                historyMenuPopup.dismiss();
            }
            showClearHistoryBottomSheet();
        });

        historyMenuPopup = new PopupWindow(menu, dp(164), dp(52), true);
        historyMenuPopup.setBackgroundDrawable(rounded(0x00000000, 0));
        historyMenuPopup.setOutsideTouchable(true);
        historyMenuPopup.showAsDropDown(anchor, -dp(132), -dp(4));
    }

    private void showClearHistoryBottomSheet() {
        if (bottomSheetOverlay != null) {
            rootLayout.removeView(bottomSheetOverlay);
        }
        bottomSheetOverlay = new FrameLayout(this);
        bottomSheetOverlay.setBackgroundColor(0x80000000);
        bottomSheetOverlay.setOnClickListener(view -> dismissBottomSheet());

        LinearLayout sheet = new LinearLayout(this);
        sheet.setOrientation(LinearLayout.VERTICAL);
        sheet.setPadding(dp(18), dp(18), dp(18), dp(18));
        sheet.setBackground(roundedStroke(COLOR_SURFACE, 0xFF253D37, dp(16)));
        sheet.setOnClickListener(view -> { });

        TextView title = text(String.format(Locale.US, "¿Eliminar los %d cálculos?", history.size()), 18, COLOR_TEXT, true);
        sheet.addView(title, compactWrap());
        sheet.addView(text("Esta acción borra todo el historial guardado en el dispositivo.", 12, 0xFF7FA89C, false), matchWrap(0, dp(6), 0, dp(14)));

        TextView destroy = text("Eliminar historial", 13, 0xFFFFFFFF, true);
        destroy.setGravity(Gravity.CENTER);
        destroy.setPadding(0, dp(12), 0, dp(12));
        destroy.setBackground(rippleBackground(0xFFD85A30, 0x33FFFFFF, dp(12)));
        destroy.setOnClickListener(view -> {
            dismissBottomSheet();
            clearHistory();
        });
        sheet.addView(destroy, compactWrap());

        TextView cancel = text("Cancelar", 13, COLOR_MUTED, false);
        cancel.setGravity(Gravity.CENTER);
        cancel.setPadding(0, dp(14), 0, 0);
        cancel.setOnClickListener(view -> dismissBottomSheet());
        sheet.addView(cancel, compactWrap());

        FrameLayout.LayoutParams sheetParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        sheetParams.gravity = Gravity.BOTTOM;
        sheetParams.leftMargin = dp(12);
        sheetParams.rightMargin = dp(12);
        sheetParams.bottomMargin = dp(12);
        bottomSheetOverlay.addView(sheet, sheetParams);
        rootLayout.addView(bottomSheetOverlay, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        sheet.setTranslationY(dp(240));
        sheet.animate().translationY(0f).setDuration(220).setInterpolator(new DecelerateInterpolator()).start();
    }

    private void dismissBottomSheet() {
        if (bottomSheetOverlay != null) {
            rootLayout.removeView(bottomSheetOverlay);
            bottomSheetOverlay = null;
        }
    }

    private String historyDateHeader(String dateKey) {
        Calendar record = calendarFromKey(dateKey);
        Calendar today = Calendar.getInstance();
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_YEAR, -1);
        if (sameDay(record, today)) {
            return "Hoy · " + dayMonth(record);
        }
        if (sameDay(record, yesterday)) {
            return "Ayer";
        }
        return dayMonth(record);
    }

    private String historyRelativeTime(CalculationRecord record) {
        Calendar recordDate = calendarFromKey(record.dateKey);
        Calendar today = Calendar.getInstance();
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_YEAR, -1);
        if (sameDay(recordDate, today)) {
            return "Hoy, " + record.time;
        }
        if (sameDay(recordDate, yesterday)) {
            return "Ayer, " + record.time;
        }
        return dayMonthShort(recordDate) + ", " + record.time;
    }

    private String dayMonth(Calendar calendar) {
        return calendar.get(Calendar.DAY_OF_MONTH) + " " + spanishMonth(calendar.get(Calendar.MONTH));
    }

    private String dayMonthShort(Calendar calendar) {
        return calendar.get(Calendar.DAY_OF_MONTH) + " " + spanishMonth(calendar.get(Calendar.MONTH)).substring(0, 3);
    }

    private String spanishMonth(int month) {
        String[] months = {"enero", "febrero", "marzo", "abril", "mayo", "junio", "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre"};
        return months[Math.max(0, Math.min(months.length - 1, month))];
    }

    private Calendar calendarFromKey(String dateKey) {
        Calendar calendar = Calendar.getInstance();
        try {
            Date parsed = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(dateKey);
            if (parsed != null) {
                calendar.setTime(parsed);
            }
        } catch (Exception ignored) {
        }
        return calendar;
    }

    private boolean sameDay(Calendar first, Calendar second) {
        return first.get(Calendar.YEAR) == second.get(Calendar.YEAR)
            && first.get(Calendar.DAY_OF_YEAR) == second.get(Calendar.DAY_OF_YEAR);
    }

    private String historyTitle(CalculationRecord record) {
        String fluid = tableTitle(record.tableKey);
        double temp = Double.isNaN(record.temperature) ? extractTemperature(record.lines) : record.temperature;
        if (Double.isNaN(temp)) {
            return record.title;
        }
        return String.format(Locale.US, "%s · %.2f °C", fluid, temp);
    }

    private String tableTitle(String tableKey) {
        for (TableSpec spec : TABLES) {
            if (spec.key.equals(tableKey)) {
                return spec.title;
            }
        }
        return "Cálculo";
    }

    private int iconForTable(String tableKey) {
        if ("water".equals(tableKey)) {
            return ICON_DROPLET;
        }
        if ("air".equals(tableKey)) {
            return ICON_WIND;
        }
        if ("steam".equals(tableKey)) {
            return ICON_CLOUD;
        }
        return ICON_THERMOMETER;
    }

    private int colorForTable(String tableKey) {
        if ("air".equals(tableKey)) {
            return 0xFF85B7EB;
        }
        if ("steam".equals(tableKey)) {
            return 0xFF9FE1CB;
        }
        return COLOR_PRIMARY;
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
        updateBottomNav();
        renderHistory();
    }

    private void clearHistory() {
        history.clear();
        saveHistory();
        updateBottomNav();
        renderHistory();
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
            String dateKey = fields.length >= 7 ? unescape(fields[6]) : new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
            List<ResultEntry> entries = decodeEntries(fields[3], tableKey, temperature);
            history.add(new CalculationRecord(dateKey, unescape(fields[0]), title, lines, entries, tableKey, temperature));
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
                .append(escape(String.format(Locale.US, "%.6f", record.temperature))).append(FIELD_SEPARATOR)
                .append(escape(record.dateKey));
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
        } else if (requestCode == REQUEST_CREATE_PDF) {
            setExportPdfState("normal");
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
            flashExportPdfSuccess();
        } catch (Exception exc) {
            showMessage("No se pudo exportar el PDF.");
            setExportPdfState("normal");
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
        return 112 + textLines * 12 + 96;
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
        paint.setColor(COLOR_PRIMARY);
        canvas.drawText(trimForPdf(entry.value, 40), left + 12, startY + 40, paint);

        paint.setTypeface(Typeface.DEFAULT);
        paint.setTextSize(9);
        paint.setColor(0xFF9FE1CB);
        int formulaTop = startY + 52;
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xFF1E2F2B);
        canvas.drawRoundRect(left + 12, formulaTop, right - 12, formulaTop + 42, 5, 5, paint);
        paint.setColor(0xFF0F6E56);
        canvas.drawRect(left + 12, formulaTop, left + 16, formulaTop + 42, paint);
        paint.setColor(0xFF9FE1CB);
        paint.setTypeface(Typeface.MONOSPACE);
        int fy = formulaTop + 12;
        for (String line : formulaText(entry).split("\\n")) {
            canvas.drawText(trimForPdf(line, 72), left + 22, fy, paint);
            fy += 12;
        }

        paint.setTypeface(Typeface.DEFAULT);
        paint.setTextSize(10);
        paint.setColor(COLOR_MUTED);
        int textY = formulaTop + 58;
        for (String part : splitForPdf(entry.explanation, 70)) {
            canvas.drawText(part, left + 12, textY, paint);
            textY += 12;
        }

        PropertyTable table = tables.get(tableKey);
        PropertySpec prop = propertyForEntry(entry);
        if (table != null && prop != null && !Double.isNaN(entry.temperature)) {
            double[] range = selectedGraphRange(table);
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
                trendColor(trendType(entry)),
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
        Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.logo_circular_dark);
        if (logo != null) {
            canvas.drawBitmap(
                logo,
                new Rect(0, 0, logo.getWidth(), logo.getHeight()),
                new RectF(x, y, x + 34, y + 34),
                paint
            );
            return;
        }
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(COLOR_PRIMARY);
        canvas.drawCircle(x + 17, y + 17, 17, paint);
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

    private GradientDrawable drawerBackground() {
        GradientDrawable drawable = rounded(0xFF132320, 0);
        drawable.setCornerRadii(new float[] {
            0, 0,
            dp(16), dp(16),
            dp(16), dp(16),
            0, 0
        });
        drawable.setStroke(dp(1), 0xFF1D4A3C);
        return drawable;
    }

    private RippleDrawable rippleBackground(int color, int rippleColor, int radius) {
        return new RippleDrawable(
            ColorStateList.valueOf(rippleColor),
            rounded(color, radius),
            null
        );
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
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(48));
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

    private void animateRevealUp(View view, long offset) {
        AnimationSet set = new AnimationSet(true);
        set.setInterpolator(new DecelerateInterpolator());
        set.setDuration(300);
        set.setStartOffset(offset);
        AlphaAnimation fade = new AlphaAnimation(0f, 1f);
        TranslateAnimation slide = new TranslateAnimation(0, 0, dp(20), 0);
        set.addAnimation(fade);
        set.addAnimation(slide);
        view.startAnimation(set);
    }

    private void registerScrollReveal(View view) {
        view.setAlpha(0f);
        view.setTranslationY(dp(20));
        pendingRevealViews.add(view);
    }

    private void checkScrollRevealViews() {
        if (pendingRevealViews.isEmpty()) {
            return;
        }
        int[] screenPosition = new int[2];
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        int revealed = 0;
        for (int i = pendingRevealViews.size() - 1; i >= 0; i--) {
            View view = pendingRevealViews.get(i);
            if (view.getWindowToken() == null) {
                continue;
            }
            view.getLocationOnScreen(screenPosition);
            int top = screenPosition[1];
            int bottom = top + view.getHeight();
            if (top < screenHeight - dp(24) && bottom > dp(24)) {
                view.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setStartDelay(revealed * 80L)
                    .setDuration(300)
                    .setInterpolator(new DecelerateInterpolator())
                    .start();
                pendingRevealViews.remove(i);
                revealed++;
            }
        }
    }

    private void animateScaleIn(View view, long offset) {
        view.setScaleX(0.82f);
        view.setScaleY(0.82f);
        view.setAlpha(0f);
        view.animate()
            .scaleX(1f)
            .scaleY(1f)
            .alpha(1f)
            .setStartDelay(offset)
            .setDuration(200)
            .setInterpolator(new AccelerateDecelerateInterpolator())
            .start();
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private float dp(float value) {
        return value * getResources().getDisplayMetrics().density;
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
        private float wavePhase = 0f;
        private ValueAnimator waveAnimator;

        EngineeringHeroVisual(Context context) {
            super(context);
            setPadding(dp(12), dp(12), dp(12), dp(12));
            startWaveAnimation();
        }

        private void startWaveAnimation() {
            waveAnimator = ValueAnimator.ofFloat(0f, (float) (Math.PI * 2));
            waveAnimator.setDuration(4000);
            waveAnimator.setRepeatCount(ValueAnimator.INFINITE);
            waveAnimator.setInterpolator(new LinearInterpolator());
            waveAnimator.addUpdateListener(animation -> {
                wavePhase = (float) animation.getAnimatedValue();
                invalidate();
            });
            waveAnimator.start();
        }

        @Override
        protected void onDetachedFromWindow() {
            if (waveAnimator != null) {
                waveAnimator.cancel();
            }
            super.onDetachedFromWindow();
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
            paint.setColor(0xFF10211E);
            canvas.drawRoundRect(left, top, right, bottom, dp(8), dp(8), paint);

            paint.setColor(0xFF162925);
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

            drawThermalWave(canvas, left, right, bottom, 0f, COLOR_ACCENT, dp(2.4f), 1f);
            drawThermalWave(canvas, left, right, bottom, 1.25f, COLOR_PRIMARY, dp(1.8f), 0.58f);
            drawThermalWave(canvas, left, right, bottom, 2.45f, COLOR_SECONDARY, dp(1.5f), 0.42f);

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

        private void drawThermalWave(Canvas canvas, float left, float right, float bottom, float phaseOffset, int color, float width, float amplitudeScale) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(width);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setColor(withAlpha(color, (int) (255 * amplitudeScale)));
            path.reset();
            for (int i = 0; i <= 52; i++) {
                float x = left + dp(34) + (right - left - dp(68)) * i / 52f;
                float normal = i / 52f;
                float y = bottom - dp(38)
                    - (float) Math.sin(normal * Math.PI * 2.1f + wavePhase + phaseOffset) * dp(18) * amplitudeScale
                    - i * 0.42f;
                if (i == 0) {
                    path.moveTo(x, y);
                } else {
                    path.lineTo(x, y);
                }
            }
            canvas.drawPath(path, paint);
        }
    }

    private final class HexHeroCard extends FrameLayout {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Path hex = new Path();

        HexHeroCard(Context context) {
            super(context);
            setWillNotDraw(false);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dp(1));
            paint.setColor(withAlpha(COLOR_PRIMARY, 15));
            float cellW = dp(24);
            float cellH = dp(20);
            float radius = cellH * 0.45f;
            for (float y = -cellH; y < getHeight() + cellH; y += cellH * 0.75f) {
                int row = Math.round(y / (cellH * 0.75f));
                float offset = row % 2 == 0 ? 0 : cellW * 0.5f;
                for (float x = -cellW; x < getWidth() + cellW; x += cellW) {
                    drawHex(canvas, x + offset + cellW * 0.5f, y + cellH * 0.5f, radius);
                }
            }
        }

        private void drawHex(Canvas canvas, float cx, float cy, float r) {
            hex.reset();
            for (int i = 0; i < 6; i++) {
                double angle = Math.PI / 6 + i * Math.PI / 3;
                float x = cx + (float) Math.cos(angle) * r;
                float y = cy + (float) Math.sin(angle) * r;
                if (i == 0) {
                    hex.moveTo(x, y);
                } else {
                    hex.lineTo(x, y);
                }
            }
            hex.close();
            canvas.drawPath(hex, paint);
        }
    }

    private final class BackArrowView extends View {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Path arrow = new Path();
        private final int arrowColor;
        private final boolean drawRing;

        BackArrowView(Context context) {
            this(context, COLOR_TEXT, true);
        }

        BackArrowView(Context context, int arrowColor, boolean drawRing) {
            super(context);
            this.arrowColor = arrowColor;
            this.drawRing = drawRing;
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
            paint.setColor(arrowColor);

            arrow.reset();
            arrow.moveTo(cx + size * 0.55f, cy - size);
            arrow.lineTo(cx - size * 0.55f, cy);
            arrow.lineTo(cx + size * 0.55f, cy + size);
            canvas.drawPath(arrow, paint);
            canvas.drawLine(cx - size * 0.45f, cy, cx + size * 1.10f, cy, paint);

            if (drawRing) {
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(dp(1));
                paint.setColor(withAlpha(COLOR_PRIMARY, 90));
                canvas.drawCircle(cx, cy, size * 1.55f, paint);
            }
        }
    }

    private final class GradientDividerView extends View {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        GradientDividerView(Context context) {
            super(context);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            setMeasuredDimension(width, dp(1));
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            paint.setShader(new LinearGradient(
                0,
                0,
                getWidth(),
                0,
                COLOR_PRIMARY,
                0x002DC89A,
                Shader.TileMode.CLAMP
            ));
            canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
            paint.setShader(null);
        }
    }

    private final class BottomNavShell extends FrameLayout {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        BottomNavShell(Context context) {
            super(context);
            setWillNotDraw(false);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            paint.setShader(new LinearGradient(
                0,
                0,
                0,
                dp(20),
                0x000F1A18,
                COLOR_BG,
                Shader.TileMode.CLAMP
            ));
            canvas.drawRect(0, 0, getWidth(), dp(20), paint);
            paint.setShader(null);
            super.onDraw(canvas);
        }
    }

    private final class BottomNavItemView extends FrameLayout {
        private final int iconType;
        private final String label;
        private final int targetScreen;
        private final LinearLayout pill;
        private final NavIconView icon;
        private final TextView labelView;
        private final TextView badge;
        private boolean selectedState = false;

        BottomNavItemView(Context context, int iconType, String label, int targetScreen) {
            super(context);
            this.iconType = iconType;
            this.label = label;
            this.targetScreen = targetScreen;
            setPadding(0, 0, 0, 0);
            setBackground(rippleBackground(0x00000000, withAlpha(COLOR_PRIMARY, 26), dp(32)));
            setContentDescription(label);

            pill = new LinearLayout(context);
            pill.setOrientation(LinearLayout.VERTICAL);
            pill.setGravity(Gravity.CENTER);
            pill.setPadding(dp(18), dp(8), dp(18), dp(6));
            pill.setBackground(rounded(0xFF1D4A3C, dp(14)));
            pill.setScaleX(0f);
            pill.setScaleY(0f);

            FrameLayout iconWrap = new FrameLayout(context);
            icon = new NavIconView(context, iconType, 0xFF3A5A52);
            FrameLayout.LayoutParams iconParams = new FrameLayout.LayoutParams(dp(28), dp(28));
            iconParams.gravity = Gravity.CENTER;
            iconWrap.addView(icon, iconParams);

            badge = text("", 9, 0xFF04342C, true);
            badge.setGravity(Gravity.CENTER);
            badge.setBackground(rounded(COLOR_PRIMARY, dp(8)));
            badge.setVisibility(GONE);
            badge.setScaleX(0f);
            badge.setScaleY(0f);
            FrameLayout.LayoutParams badgeParams = new FrameLayout.LayoutParams(dp(16), dp(16));
            badgeParams.gravity = Gravity.TOP | Gravity.END;
            badgeParams.topMargin = -dp(4);
            badgeParams.rightMargin = -dp(4);
            iconWrap.addView(badge, badgeParams);

            pill.addView(iconWrap, new LinearLayout.LayoutParams(dp(32), dp(26)));
            labelView = text(label, 10, COLOR_PRIMARY, false);
            labelView.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
            labelView.setGravity(Gravity.CENTER);
            labelView.setVisibility(GONE);
            pill.addView(labelView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            FrameLayout.LayoutParams pillParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            );
            pillParams.gravity = Gravity.CENTER;
            addView(pill, pillParams);
        }

        void setSelectedState(boolean selected, int historyCount) {
            if (selectedState != selected) {
                selectedState = selected;
                icon.animate().alpha(0.25f).setDuration(75).withEndAction(() -> {
                    icon.setIconColor(selected ? COLOR_PRIMARY : 0xFF3A5A52);
                    icon.invalidate();
                    icon.animate().alpha(1f).setDuration(75).start();
                }).start();
                labelView.setVisibility(selected ? VISIBLE : GONE);
                pill.animate()
                    .scaleX(selected ? 1f : 0.68f)
                    .scaleY(selected ? 1f : 0.68f)
                    .alpha(selected ? 1f : 0f)
                    .setDuration(220)
                    .setInterpolator(new DecelerateInterpolator())
                    .withStartAction(() -> {
                        if (selected) {
                            pill.setVisibility(VISIBLE);
                        }
                    })
                    .withEndAction(() -> {
                        if (!selected) {
                            pill.setVisibility(VISIBLE);
                            pill.setScaleX(1f);
                            pill.setScaleY(1f);
                            pill.setAlpha(1f);
                            pill.setBackground(rounded(0x00000000, dp(14)));
                        } else {
                            pill.setBackground(rounded(0xFF1D4A3C, dp(14)));
                        }
                    })
                    .start();
                if (!selected) {
                    pill.setBackground(rounded(0x00000000, dp(14)));
                    labelView.setVisibility(GONE);
                }
            } else {
                icon.setIconColor(selected ? COLOR_PRIMARY : 0xFF3A5A52);
                icon.invalidate();
                labelView.setVisibility(selected ? VISIBLE : GONE);
                pill.setBackground(rounded(selected ? 0xFF1D4A3C : 0x00000000, dp(14)));
                pill.setScaleX(1f);
                pill.setScaleY(1f);
                pill.setAlpha(1f);
            }
            updateBadge(historyCount);
        }

        private void updateBadge(int historyCount) {
            if (targetScreen != SCREEN_HISTORY) {
                return;
            }
            if (historyCount <= 0) {
                if (badge.getVisibility() == VISIBLE) {
                    badge.animate()
                        .scaleX(0f)
                        .scaleY(0f)
                        .alpha(0f)
                        .setDuration(150)
                        .withEndAction(() -> badge.setVisibility(GONE))
                        .start();
                }
                return;
            }
            badge.setText(String.valueOf(Math.min(99, historyCount)));
            if (badge.getVisibility() != VISIBLE) {
                badge.setVisibility(VISIBLE);
                badge.setAlpha(0f);
                badge.setScaleX(0f);
                badge.setScaleY(0f);
                badge.animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(150).start();
            }
        }
    }

    private final class NavIconView extends View {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Path path = new Path();
        private int iconType;
        private int iconColor;

        NavIconView(Context context, int iconType, int iconColor) {
            super(context);
            this.iconType = iconType;
            this.iconColor = iconColor;
            setPadding(dp(10), dp(10), dp(10), dp(10));
        }

        void setIconType(int iconType) {
            this.iconType = iconType;
        }

        void setIconColor(int iconColor) {
            this.iconColor = iconColor;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float w = getWidth();
            float h = getHeight();
            float cx = w * 0.5f;
            float cy = h * 0.5f;
            float size = Math.min(w, h) * 0.26f;

            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(Math.max(2f, dp(3)));
            paint.setColor(iconColor);
            paint.setTypeface(Typeface.DEFAULT);

            if (iconType == ICON_MENU) {
                paint.setStrokeWidth(dp(1.5f));
                float left = cx - dp(8);
                float right = cx + dp(8);
                float gap = dp(4.5f);
                canvas.drawLine(left, cy - gap, right, cy - gap, paint);
                canvas.drawLine(left, cy, right, cy, paint);
                canvas.drawLine(left, cy + gap, right, cy + gap, paint);
                return;
            }

            if (iconType == ICON_HOME) {
                path.reset();
                path.moveTo(cx - size * 1.05f, cy - size * 0.05f);
                path.lineTo(cx, cy - size * 0.98f);
                path.lineTo(cx + size * 1.05f, cy - size * 0.05f);
                canvas.drawPath(path, paint);
                canvas.drawLine(cx - size * 0.78f, cy - size * 0.02f, cx - size * 0.78f, cy + size * 0.95f, paint);
                canvas.drawLine(cx + size * 0.78f, cy - size * 0.02f, cx + size * 0.78f, cy + size * 0.95f, paint);
                canvas.drawLine(cx - size * 0.78f, cy + size * 0.95f, cx + size * 0.78f, cy + size * 0.95f, paint);
                return;
            }

            if (iconType == ICON_CALCULATE) {
                float left = cx - size * 1.1f;
                float bottom = cy + size * 0.9f;
                canvas.drawLine(left, cy - size * 0.9f, left, bottom, paint);
                canvas.drawLine(left, bottom, cx + size * 1.15f, bottom, paint);
                path.reset();
                for (int i = 0; i <= 24; i++) {
                    float x = left + size * 0.22f + size * 1.85f * i / 24f;
                    float y = cy + (float) Math.sin(i / 24f * Math.PI * 1.35f + Math.PI) * size * 0.55f;
                    if (i == 0) {
                        path.moveTo(x, y);
                    } else {
                        path.lineTo(x, y);
                    }
                }
                canvas.drawPath(path, paint);
                paint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(cx + size * 0.48f, cy - size * 0.45f, size * 0.15f, paint);
                paint.setStyle(Paint.Style.STROKE);
                return;
            }

            if (iconType == ICON_HISTORY) {
                canvas.drawCircle(cx, cy, size * 1.05f, paint);
                canvas.drawLine(cx, cy, cx, cy - size * 0.62f, paint);
                canvas.drawLine(cx, cy, cx + size * 0.55f, cy + size * 0.28f, paint);
                paint.setStrokeWidth(Math.max(1.5f, dp(2)));
                canvas.drawArc(cx - size * 1.28f, cy - size * 1.28f, cx + size * 1.28f, cy + size * 1.28f, 205, 70, false, paint);
                return;
            }

            if (iconType == ICON_DATABASE) {
                float left = cx - size * 0.9f;
                float right = cx + size * 0.9f;
                float top = cy - size * 0.85f;
                float bottom = cy + size * 0.85f;
                canvas.drawOval(left, top - size * 0.25f, right, top + size * 0.25f, paint);
                canvas.drawLine(left, top, left, bottom, paint);
                canvas.drawLine(right, top, right, bottom, paint);
                canvas.drawOval(left, bottom - size * 0.25f, right, bottom + size * 0.25f, paint);
                canvas.drawArc(left, cy - size * 0.25f, right, cy + size * 0.25f, 0, 180, false, paint);
                float lensCx = cx + size * 0.52f;
                float lensCy = cy + size * 0.52f;
                canvas.drawCircle(lensCx, lensCy, size * 0.34f, paint);
                canvas.drawLine(lensCx + size * 0.24f, lensCy + size * 0.24f, lensCx + size * 0.55f, lensCy + size * 0.55f, paint);
                return;
            }

            if (iconType == ICON_READING) {
                float left = cx - size;
                float right = cx + size;
                float top = cy - size * 0.85f;
                float lineGap = size * 0.52f;
                canvas.drawRoundRect(left, top, right, cy + size * 0.85f, dp(5), dp(5), paint);
                canvas.drawLine(left + size * 0.35f, top + lineGap, right - size * 0.35f, top + lineGap, paint);
                canvas.drawLine(left + size * 0.35f, top + lineGap * 1.7f, right - size * 0.35f, top + lineGap * 1.7f, paint);
                canvas.drawLine(left + size * 0.35f, top + lineGap * 2.4f, right - size * 0.65f, top + lineGap * 2.4f, paint);
                return;
            }

            if (iconType == ICON_REPORT) {
                float left = cx - size * 0.8f;
                float top = cy - size;
                float right = cx + size * 0.8f;
                float bottom = cy + size;
                canvas.drawRoundRect(left, top, right, bottom, dp(5), dp(5), paint);
                canvas.drawLine(left + size * 0.28f, cy - size * 0.35f, right - size * 0.28f, cy - size * 0.35f, paint);
                canvas.drawLine(left + size * 0.28f, cy + size * 0.05f, right - size * 0.28f, cy + size * 0.05f, paint);
                paint.setStyle(Paint.Style.FILL);
                paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                paint.setTextSize(size * 0.44f);
                canvas.drawText("PDF", left + size * 0.25f, cy + size * 0.65f, paint);
                paint.setStyle(Paint.Style.STROKE);
                return;
            }

            if (iconType == ICON_CURVE) {
                float left = cx - size;
                float bottom = cy + size * 0.82f;
                canvas.drawLine(left, cy - size * 0.82f, left, bottom, paint);
                canvas.drawLine(left, bottom, cx + size, bottom, paint);
                path.reset();
                for (int i = 0; i <= 24; i++) {
                    float x = left + size * 0.18f + size * 1.62f * i / 24f;
                    float y = cy + (float) Math.sin(i / 24f * Math.PI * 1.6f + Math.PI) * size * 0.45f;
                    if (i == 0) {
                        path.moveTo(x, y);
                    } else {
                        path.lineTo(x, y);
                    }
                }
                canvas.drawPath(path, paint);
                return;
            }

            if (iconType == ICON_DROPLET) {
                path.reset();
                path.moveTo(cx, cy - size * 1.15f);
                path.cubicTo(cx + size * 0.95f, cy - size * 0.18f, cx + size * 0.78f, cy + size * 0.95f, cx, cy + size);
                path.cubicTo(cx - size * 0.78f, cy + size * 0.95f, cx - size * 0.95f, cy - size * 0.18f, cx, cy - size * 1.15f);
                canvas.drawPath(path, paint);
                return;
            }

            if (iconType == ICON_WIND) {
                canvas.drawLine(cx - size * 1.05f, cy - size * 0.55f, cx + size * 0.72f, cy - size * 0.55f, paint);
                canvas.drawArc(cx + size * 0.35f, cy - size * 0.95f, cx + size * 1.12f, cy - size * 0.18f, 260, 210, false, paint);
                canvas.drawLine(cx - size * 1.05f, cy, cx + size * 1.05f, cy, paint);
                canvas.drawLine(cx - size * 1.05f, cy + size * 0.55f, cx + size * 0.55f, cy + size * 0.55f, paint);
                canvas.drawArc(cx + size * 0.18f, cy + size * 0.18f, cx + size * 0.95f, cy + size * 0.95f, 270, 210, false, paint);
                return;
            }

            if (iconType == ICON_CLOUD) {
                canvas.drawArc(cx - size * 1.10f, cy - size * 0.12f, cx - size * 0.15f, cy + size * 0.82f, 180, 180, false, paint);
                canvas.drawArc(cx - size * 0.55f, cy - size * 0.82f, cx + size * 0.35f, cy + size * 0.18f, 200, 210, false, paint);
                canvas.drawArc(cx + size * 0.02f, cy - size * 0.55f, cx + size * 1.08f, cy + size * 0.72f, 225, 210, false, paint);
                canvas.drawLine(cx - size * 0.68f, cy + size * 0.82f, cx + size * 0.66f, cy + size * 0.82f, paint);
                return;
            }

            if (iconType == ICON_THERMOMETER) {
                canvas.drawLine(cx, cy - size * 1.05f, cx, cy + size * 0.25f, paint);
                canvas.drawCircle(cx, cy + size * 0.65f, size * 0.36f, paint);
                canvas.drawLine(cx + size * 0.28f, cy - size * 0.72f, cx + size * 0.65f, cy - size * 0.72f, paint);
                canvas.drawLine(cx + size * 0.28f, cy - size * 0.25f, cx + size * 0.55f, cy - size * 0.25f, paint);
                return;
            }

            if (iconType == ICON_TRASH) {
                canvas.drawLine(cx - size * 0.82f, cy - size * 0.65f, cx + size * 0.82f, cy - size * 0.65f, paint);
                canvas.drawLine(cx - size * 0.35f, cy - size, cx + size * 0.35f, cy - size, paint);
                canvas.drawLine(cx - size * 0.55f, cy - size * 0.38f, cx - size * 0.42f, cy + size * 0.9f, paint);
                canvas.drawLine(cx + size * 0.55f, cy - size * 0.38f, cx + size * 0.42f, cy + size * 0.9f, paint);
                canvas.drawLine(cx - size * 0.42f, cy + size * 0.9f, cx + size * 0.42f, cy + size * 0.9f, paint);
                canvas.drawLine(cx - size * 0.35f, cy - size * 0.05f, cx + size * 0.35f, cy + size * 0.65f, paint);
                canvas.drawLine(cx + size * 0.35f, cy - size * 0.05f, cx - size * 0.35f, cy + size * 0.65f, paint);
                return;
            }

            if (iconType == ICON_DOTS) {
                paint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(cx, cy - size * 0.55f, size * 0.14f, paint);
                canvas.drawCircle(cx, cy, size * 0.14f, paint);
                canvas.drawCircle(cx, cy + size * 0.55f, size * 0.14f, paint);
                paint.setStyle(Paint.Style.STROKE);
                return;
            }

            if (iconType == ICON_EMPTY_HISTORY) {
                canvas.drawLine(cx, cy - size * 1.1f, cx, cy + size * 0.2f, paint);
                canvas.drawCircle(cx, cy + size * 0.62f, size * 0.34f, paint);
                paint.setTextSize(size * 0.9f);
                paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                canvas.drawText("?", cx + size * 0.42f, cy - size * 0.1f, paint);
                paint.setTypeface(Typeface.DEFAULT);
                return;
            }

            if (iconType == ICON_BOLT) {
                path.reset();
                path.moveTo(cx + size * 0.12f, cy - size * 1.05f);
                path.lineTo(cx - size * 0.55f, cy + size * 0.05f);
                path.lineTo(cx + size * 0.08f, cy + size * 0.05f);
                path.lineTo(cx - size * 0.12f, cy + size * 1.05f);
                path.lineTo(cx + size * 0.62f, cy - size * 0.18f);
                path.lineTo(cx, cy - size * 0.18f);
                path.close();
                canvas.drawPath(path, paint);
                return;
            }

            if (iconType == ICON_RULER) {
                canvas.drawRoundRect(cx - size, cy - size * 0.45f, cx + size, cy + size * 0.45f, dp(4), dp(4), paint);
                for (int i = 0; i < 5; i++) {
                    float x = cx - size * 0.72f + i * size * 0.36f;
                    canvas.drawLine(x, cy - size * 0.45f, x, cy - size * (i % 2 == 0 ? 0.05f : 0.2f), paint);
                }
                canvas.drawLine(cx - size * 0.72f, cy + size * 0.7f, cx + size * 0.72f, cy - size * 0.7f, paint);
                return;
            }

            if (iconType == ICON_LIST) {
                for (int i = 0; i < 3; i++) {
                    float y = cy - size * 0.62f + i * size * 0.62f;
                    canvas.drawCircle(cx - size * 0.75f, y, size * 0.08f, paint);
                    canvas.drawLine(cx - size * 0.42f, y, cx + size * 0.9f, y, paint);
                }
                return;
            }

            if (iconType == ICON_CPU) {
                canvas.drawRoundRect(cx - size * 0.75f, cy - size * 0.75f, cx + size * 0.75f, cy + size * 0.75f, dp(3), dp(3), paint);
                for (int i = -1; i <= 1; i++) {
                    float offset = i * size * 0.45f;
                    canvas.drawLine(cx - size * 1.05f, cy + offset, cx - size * 0.75f, cy + offset, paint);
                    canvas.drawLine(cx + size * 0.75f, cy + offset, cx + size * 1.05f, cy + offset, paint);
                    canvas.drawLine(cx + offset, cy - size * 1.05f, cx + offset, cy - size * 0.75f, paint);
                    canvas.drawLine(cx + offset, cy + size * 0.75f, cx + offset, cy + size * 1.05f, paint);
                }
                return;
            }

            if (iconType == ICON_CHEVRON) {
                path.reset();
                path.moveTo(cx - size * 0.72f, cy - size * 0.24f);
                path.lineTo(cx, cy + size * 0.48f);
                path.lineTo(cx + size * 0.72f, cy - size * 0.24f);
                canvas.drawPath(path, paint);
                return;
            }

            if (iconType == ICON_COPY) {
                float left = cx - size * 0.55f;
                float top = cy - size * 0.82f;
                canvas.drawRoundRect(left, top, left + size * 1.05f, top + size * 1.2f, dp(3), dp(3), paint);
                canvas.drawRoundRect(cx - size * 0.18f, cy - size * 0.38f, cx + size * 0.88f, cy + size * 0.82f, dp(3), dp(3), paint);
                return;
            }

            if (iconType == ICON_CHECK) {
                path.reset();
                path.moveTo(cx - size * 0.9f, cy + size * 0.02f);
                path.lineTo(cx - size * 0.22f, cy + size * 0.68f);
                path.lineTo(cx + size * 0.92f, cy - size * 0.68f);
                canvas.drawPath(path, paint);
                return;
            }

            canvas.drawCircle(cx, cy, size * 1.05f, paint);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(cx, cy - size * 0.52f, size * 0.13f, paint);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawLine(cx, cy - size * 0.08f, cx, cy + size * 0.58f, paint);
        }
    }

    private final class FormulaBlockView extends LinearLayout {
        private final ResultEntry entry;
        private final View accent;
        private final LinearLayout expandedRows;
        private final NavIconView copyIcon;
        private final NavIconView chevronIcon;
        private boolean expanded = false;

        FormulaBlockView(Context context, ResultEntry entry) {
            super(context);
            this.entry = entry;
            setOrientation(HORIZONTAL);
            setBackground(rounded(0xFF1E2F2B, 0));

            accent = new View(context);
            accent.setBackgroundColor(0xFF0F6E56);
            addView(accent, new LinearLayout.LayoutParams(dp(3), LinearLayout.LayoutParams.MATCH_PARENT));

            FrameLayout frame = new FrameLayout(context);
            LinearLayout content = new LinearLayout(context);
            content.setOrientation(VERTICAL);
            content.setPadding(dp(10), dp(8), dp(44), dp(8));
            content.setOnClickListener(view -> toggleExpanded());

            MathEquationView equation = new MathEquationView(context, entry);
            HorizontalScrollView equationScroll = new HorizontalScrollView(context);
            equationScroll.setHorizontalScrollBarEnabled(false);
            equationScroll.addView(equation, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            content.addView(equationScroll, compactWrap());

            LinearLayout method = new LinearLayout(context);
            method.setGravity(Gravity.CENTER_VERTICAL);
            method.setOrientation(HORIZONTAL);
            method.addView(new NavIconView(context, ICON_CPU, 0xFF3A5A52), new LinearLayout.LayoutParams(dp(14), dp(14)));
            TextView methodText = text("método: spline cúbica interpolada", 9, 0xFF3A5A52, false);
            method.addView(methodText, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            content.addView(method, matchWrap(0, dp(4), 0, 0));

            LinearLayout eval = new LinearLayout(context);
            eval.setGravity(Gravity.CENTER_VERTICAL);
            eval.setOrientation(HORIZONTAL);
            TextView evalText = text(String.format(Locale.US, "@ %.2f °C → %s", entry.temperature, evaluationValue(entry)), 10, COLOR_PRIMARY, false);
            evalText.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
            eval.addView(evalText, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            TextView badge = text(unitBadge(entry), 9, 0xFF7FA89C, false);
            badge.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
            badge.setPadding(dp(4), dp(1), dp(4), dp(1));
            badge.setBackground(rounded(0xFF1D4A3C, dp(3)));
            LinearLayout.LayoutParams badgeParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            badgeParams.leftMargin = dp(3);
            eval.addView(badge, badgeParams);
            content.addView(eval, matchWrap(0, dp(4), 0, 0));

            expandedRows = new LinearLayout(context);
            expandedRows.setOrientation(VERTICAL);
            expandedRows.setVisibility(GONE);
            TextView deduction = text(formulaMeta(entry).deduction, 10, 0xFF7FA89C, false);
            deduction.setLineSpacing(0, 1.15f);
            expandedRows.addView(deduction, matchWrap(0, dp(6), 0, 0));
            TextView dimension = text(formulaMeta(entry).dimension, 11, 0xFF5DCAA5, false);
            dimension.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
            expandedRows.addView(dimension, matchWrap(0, dp(4), 0, 0));
            content.addView(expandedRows, compactWrap());

            frame.addView(content, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));

            copyIcon = new NavIconView(context, ICON_COPY, 0xFF3A5A52);
            copyIcon.setBackground(rippleBackground(0x00000000, withAlpha(COLOR_PRIMARY, 31), dp(18)));
            copyIcon.setOnClickListener(view -> copyFormulaWithFeedback());
            FrameLayout.LayoutParams copyParams = new FrameLayout.LayoutParams(dp(36), dp(36));
            copyParams.gravity = Gravity.TOP | Gravity.END;
            copyParams.topMargin = dp(2);
            copyParams.rightMargin = dp(33);
            frame.addView(copyIcon, copyParams);

            chevronIcon = new NavIconView(context, ICON_CHEVRON, 0xFF3A5A52);
            chevronIcon.setBackground(rippleBackground(0x00000000, withAlpha(COLOR_PRIMARY, 31), dp(18)));
            chevronIcon.setOnClickListener(view -> toggleExpanded());
            FrameLayout.LayoutParams chevronParams = new FrameLayout.LayoutParams(dp(36), dp(36));
            chevronParams.gravity = Gravity.TOP | Gravity.END;
            chevronParams.topMargin = dp(2);
            chevronParams.rightMargin = dp(2);
            frame.addView(chevronIcon, chevronParams);

            addView(frame, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        }

        private void copyFormulaWithFeedback() {
            performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK);
            copyFormula(entry);
            copyIcon.setAlpha(0f);
            copyIcon.setIconType(ICON_CHECK);
            copyIcon.setIconColor(COLOR_PRIMARY);
            copyIcon.invalidate();
            copyIcon.animate().alpha(1f).setDuration(100).start();
            copyIcon.postDelayed(() -> {
                copyIcon.animate().alpha(expanded ? 0f : 1f).setDuration(120).withEndAction(() -> {
                    copyIcon.setIconType(ICON_COPY);
                    copyIcon.setIconColor(0xFF3A5A52);
                    copyIcon.invalidate();
                }).start();
            }, 1500);
        }

        private void toggleExpanded() {
            expanded = !expanded;
            animateExpandedRows(expanded);
            chevronIcon.animate().rotation(expanded ? 180f : 0f).setDuration(200).setInterpolator(new DecelerateInterpolator()).start();
            copyIcon.animate().alpha(expanded ? 0f : 1f).setDuration(160).start();
            ValueAnimator color = ValueAnimator.ofObject(new ArgbEvaluator(), expanded ? 0xFF0F6E56 : COLOR_PRIMARY, expanded ? COLOR_PRIMARY : 0xFF0F6E56);
            color.setDuration(200);
            color.addUpdateListener(animation -> accent.setBackgroundColor((int) animation.getAnimatedValue()));
            color.start();
        }

        private void animateExpandedRows(boolean show) {
            if (show) {
                expandedRows.setVisibility(VISIBLE);
                expandedRows.measure(
                    MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.AT_MOST),
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
                );
                int target = expandedRows.getMeasuredHeight();
                expandedRows.getLayoutParams().height = 0;
                expandedRows.requestLayout();
                ValueAnimator animator = ValueAnimator.ofInt(0, target);
                animator.setDuration(200);
                animator.setInterpolator(new DecelerateInterpolator());
                animator.addUpdateListener(animation -> {
                    expandedRows.getLayoutParams().height = (int) animation.getAnimatedValue();
                    expandedRows.requestLayout();
                });
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        expandedRows.getLayoutParams().height = LinearLayout.LayoutParams.WRAP_CONTENT;
                        expandedRows.requestLayout();
                    }
                });
                animator.start();
                return;
            }
            int start = expandedRows.getHeight();
            ValueAnimator animator = ValueAnimator.ofInt(start, 0);
            animator.setDuration(200);
            animator.setInterpolator(new DecelerateInterpolator());
            animator.addUpdateListener(animation -> {
                expandedRows.getLayoutParams().height = (int) animation.getAnimatedValue();
                expandedRows.requestLayout();
            });
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    expandedRows.setVisibility(GONE);
                    expandedRows.getLayoutParams().height = LinearLayout.LayoutParams.WRAP_CONTENT;
                    expandedRows.requestLayout();
                }
            });
            animator.start();
        }
    }

    private final class MathEquationView extends LinearLayout {
        private final ResultEntry entry;

        MathEquationView(Context context, ResultEntry entry) {
            super(context);
            this.entry = entry;
            setOrientation(HORIZONTAL);
            setGravity(Gravity.CENTER_VERTICAL);
            setPadding(0, 0, 0, 0);
            buildEquation();
        }

        private void buildEquation() {
            FormulaMeta meta = formulaMeta(entry);
            String symbol = meta.symbol;
            addVariable(symbol, true);
            addOperator(" = ");
            if ("P_sat".equals(symbol)) {
                addVariable("f", false);
                addOperator("(");
                addVariable("T", false);
                addOperator(")");
            } else if ("beta".equals(symbol)) {
                addFraction(constant("1"), variable("V"));
                addOperator(" * ");
                addFraction(variable("dV"), variable("dT"));
            } else if ("rho".equals(symbol)) {
                addFraction(variable("m"), variable("V"));
            } else if ("cp".equals(symbol)) {
                addFraction(variable("du"), row(variable("dm"), operator("*"), variable("dT")));
            } else if ("k".equals(symbol)) {
                addFraction(row(variable("q"), operator("*"), variable("l")), row(variable("A"), operator("*"), variable("dT")));
            } else if ("alpha".equals(symbol)) {
                addFraction(variable("k"), row(variable("rho"), operator("*"), variable("cp")));
            } else if ("mu".equals(symbol)) {
                addVariable("tau", false);
                addOperator(" * ");
                addFraction(variable("du"), variable("dy"));
            } else if ("nu".equals(symbol)) {
                addFraction(variable("mu"), variable("rho"));
            } else if ("Pr".equals(symbol)) {
                addFraction(row(variable("mu"), operator("*"), variable("cp")), variable("k"));
            } else if ("h".equals(symbol)) {
                addVariable("u", false);
                addOperator(" + ");
                addVariable("p", false);
                addOperator("*");
                addVariable("v", false);
            } else if ("s".equals(symbol)) {
                addFraction(variable("dq_rev"), variable("T"));
            } else {
                addVariable("f", true);
                addOperator("(T)");
            }
        }

        private void addVariable(String value, boolean dependent) {
            addView(variable(value, dependent));
        }

        private void addOperator(String value) {
            addView(operator(value));
        }

        private void addFraction(View numerator, View denominator) {
            addView(new FractionView(getContext(), numerator, denominator), new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        }

        private View variable(String value) {
            return variable(value, false);
        }

        private View variable(String value, boolean dependent) {
            View view = symbolView(value, dependent ? 16 : 13, dependent ? COLOR_PRIMARY : 0xFF9FE1CB, dependent);
            view.setOnClickListener(anchor -> showVariableTooltip(anchor, entry, value));
            return view;
        }

        private TextView operator(String value) {
            TextView view = text(value, 14, 0xFF5DCAA5, false);
            view.setTypeface(Typeface.create("serif", Typeface.NORMAL));
            return view;
        }

        private TextView constant(String value) {
            TextView view = text(value, 13, 0xFF7FA89C, false);
            view.setTypeface(Typeface.create("serif", Typeface.NORMAL));
            return view;
        }

        private LinearLayout row(View... views) {
            LinearLayout row = new LinearLayout(getContext());
            row.setOrientation(HORIZONTAL);
            row.setGravity(Gravity.CENTER);
            for (View view : views) {
                row.addView(view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            }
            return row;
        }
    }

    private final class FractionView extends LinearLayout {
        FractionView(Context context, View numerator, View denominator) {
            super(context);
            setOrientation(VERTICAL);
            setGravity(Gravity.CENTER);
            setPadding(dp(2), 0, dp(2), 0);
            addView(centered(numerator), compactWrap());
            View line = new View(context);
            line.setBackgroundColor(0xFF5DCAA5);
            addView(line, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Math.max(1, (int) dp(1.5f))));
            addView(centered(denominator), compactWrap());
        }

        private LinearLayout centered(View child) {
            LinearLayout box = new LinearLayout(getContext());
            box.setGravity(Gravity.CENTER);
            box.addView(child, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            return box;
        }
    }

    private final class ShimmerView extends View {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private ValueAnimator animator;
        private float progress = -0.35f;

        ShimmerView(Context context) {
            super(context);
        }

        void start() {
            stop();
            progress = -0.35f;
            animator = ValueAnimator.ofFloat(-0.35f, 1.35f);
            animator.setDuration(1200);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setInterpolator(new LinearInterpolator());
            animator.addUpdateListener(animation -> {
                progress = (float) animation.getAnimatedValue();
                invalidate();
            });
            animator.start();
        }

        void stop() {
            if (animator != null) {
                animator.cancel();
                animator = null;
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float w = getWidth();
            float h = getHeight();
            float center = w * progress;
            paint.setShader(new LinearGradient(
                center - w * 0.18f,
                0,
                center + w * 0.18f,
                0,
                new int[] {0x00FFFFFF, 0x33FFFFFF, 0x00FFFFFF},
                new float[] {0f, 0.5f, 1f},
                Shader.TileMode.CLAMP
            ));
            canvas.drawRoundRect(0, 0, w, h, dp(12), dp(12), paint);
            paint.setShader(null);
        }
    }

    private final class SmallSpinnerView extends View {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final int color;
        private ValueAnimator animator;
        private float rotation = 0f;

        SmallSpinnerView(Context context, int color) {
            super(context);
            this.color = color;
        }

        void start() {
            stop();
            animator = ValueAnimator.ofFloat(0f, 360f);
            animator.setDuration(900);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setInterpolator(new LinearInterpolator());
            animator.addUpdateListener(animation -> {
                rotation = (float) animation.getAnimatedValue();
                invalidate();
            });
            animator.start();
        }

        void stop() {
            if (animator != null) {
                animator.cancel();
                animator = null;
            }
        }

        @Override
        protected void onDetachedFromWindow() {
            stop();
            super.onDetachedFromWindow();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float size = Math.min(getWidth(), getHeight()) - dp(3);
            float left = (getWidth() - size) / 2f;
            float top = (getHeight() - size) / 2f;
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dp(2));
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setColor(color);
            canvas.drawArc(left, top, left + size, top + size, rotation, 250, false, paint);
        }
    }

    private final class MiniTrendChartView extends View {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Path path = new Path();
        private final List<Double> xValues = new ArrayList<>();
        private final List<Double> yValues = new ArrayList<>();
        private PropertySpec prop;
        private PropertyTable table;
        private double temperature = Double.NaN;
        private int lineColor = COLOR_PRIMARY;

        MiniTrendChartView(Context context) {
            super(context);
            setPadding(dp(8), dp(6), dp(8), dp(6));
        }

        void setData(PropertyTable table, PropertySpec prop, double temperature, int lineColor) {
            this.table = table;
            this.prop = prop;
            this.temperature = temperature;
            this.lineColor = lineColor;
            fillChartValues(table, prop, table.minTemperature(), table.maxTemperature(), xValues, yValues, 52);
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (table == null || prop == null || xValues.isEmpty()) {
                return;
            }
            float left = getPaddingLeft();
            float right = getWidth() - getPaddingRight();
            float top = getPaddingTop();
            float bottom = getHeight() - getPaddingBottom() - dp(9);
            if (right <= left || bottom <= top) {
                return;
            }
            double minX = table.minTemperature();
            double maxX = table.maxTemperature();
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

            path.reset();
            for (int i = 0; i < xValues.size(); i++) {
                float px = (float) (left + (xValues.get(i) - minX) / (maxX - minX) * (right - left));
                float py = (float) (bottom - (yValues.get(i) - minY) / (maxY - minY) * (bottom - top));
                if (i == 0) {
                    path.moveTo(px, py);
                } else {
                    path.lineTo(px, py);
                }
            }

            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dp(1.5f));
            paint.setColor(lineColor);
            paint.setPathEffect(null);
            canvas.drawPath(path, paint);

            paint.setStyle(Paint.Style.FILL);
            paint.setTextSize(dp(9));
            paint.setTypeface(Typeface.MONOSPACE);
            paint.setColor(0xFF3A5A52);
            canvas.drawText(String.format(Locale.US, "%.0f °C", minX), left, getHeight() - dp(2), paint);
            canvas.drawText(String.format(Locale.US, "%.0f °C", maxX), right - dp(42), getHeight() - dp(2), paint);

            if (!Double.isNaN(temperature) && temperature >= minX && temperature <= maxX) {
                double current = table.valueFor(prop.column, temperature);
                float px = (float) (left + (temperature - minX) / (maxX - minX) * (right - left));
                float py = (float) (bottom - (current - minY) / (maxY - minY) * (bottom - top));
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(Math.max(1f, dp(0.5f)));
                paint.setColor(withAlpha(lineColor, 102));
                paint.setPathEffect(new DashPathEffect(new float[] {dp(2), dp(2)}, 0));
                canvas.drawLine(px, py, px, bottom, paint);
                paint.setPathEffect(null);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(dp(1));
                paint.setColor(withAlpha(lineColor, 77));
                canvas.drawCircle(px, py, dp(5.5f), paint);
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(lineColor);
                canvas.drawCircle(px, py, dp(3.5f), paint);
                paint.setTypeface(Typeface.MONOSPACE);
                paint.setTextSize(dp(8));
                String label = String.format(Locale.US, "%.3g", current);
                float labelX = Math.min(px + dp(4), right - dp(44));
                canvas.drawText(label, labelX, Math.max(top + dp(8), py - dp(4)), paint);
            }
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
        final String dateKey;
        final String time;
        final String title;
        final List<String> lines;
        final List<ResultEntry> entries;
        final String tableKey;
        final double temperature;

        CalculationRecord(String dateKey, String time, String title, List<String> lines, List<ResultEntry> entries, String tableKey, double temperature) {
            this.dateKey = dateKey == null || dateKey.isEmpty() ? "" : dateKey;
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

    private static final class FormulaMeta {
        final String symbol;
        final String copyExpression;
        final String deduction;
        final String dimension;
        final String[] variables;

        FormulaMeta(String symbol, String copyExpression, String deduction, String dimension, String... variables) {
            this.symbol = symbol;
            this.copyExpression = copyExpression;
            this.deduction = deduction;
            this.dimension = dimension;
            this.variables = variables;
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
