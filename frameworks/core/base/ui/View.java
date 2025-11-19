package com.pearos.framework.core.base.ui;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pearos.framework.core.base.animation.Animation;
import com.pearos.framework.core.base.animation.Animator;
import com.pearos.framework.core.base.animation.LayoutTransition;

import com.pearos.framework.core.base.graphics.Canvas;
import com.pearos.framework.core.base.graphics.Color;
import com.pearos.framework.core.base.graphics.Paint;
import com.pearos.framework.core.base.graphics.Path;
import com.pearos.framework.core.base.graphics.Rect;
import com.pearos.framework.core.base.graphics.Region;
import com.pearos.framework.core.base.graphics.Bitmap;
import com.pearos.framework.core.base.graphics.Matrix;

import com.pearos.framework.core.base.hardware.Sensor;
import com.pearos.framework.core.base.hardware.SensorEvent;

import com.pearos.framework.core.base.input.KeyEvent;
import com.pearos.framework.core.base.input.MotionEvent;
import com.pearos.framework.core.base.input.InputDevice;

import com.pearos.framework.core.base.os.Handler;
import com.pearos.framework.core.base.os.Looper;
import com.pearos.framework.core.base.os.Vibrator;
import com.pearos.framework.core.base.os.SystemClock;

import com.pearos.framework.core.base.util.AttributeSet;
import com.pearos.framework.core.base.util.TypedArray;
import com.pearos.framework.core.base.util.Log;
import com.pearos.framework.core.base.util.DisplayMetrics;
import com.pearos.framework.core.base.util.PearContext;

import com.pearos.framework.core.base.view.animation.Interpolator;
import com.pearos.framework.core.base.view.animation.AccelerateDecelerateInterpolator;
import com.pearos.framework.core.base.view.animation.LinearInterpolator;
import com.pearos.framework.core.base.view.animation.AlphaAnimation;

// --- Static imports igual Android ---
import static com.pearos.framework.core.base.graphics.Color.BLACK;
import static com.pearos.framework.core.base.graphics.Color.WHITE;
import static com.pearos.framework.core.base.graphics.Color.TRANSPARENT;

import static com.pearos.framework.core.base.input.MotionEvent.ACTION_DOWN;
import static com.pearos.framework.core.base.input.MotionEvent.ACTION_UP;
import static com.pearos.framework.core.base.input.MotionEvent.ACTION_MOVE;

import static com.pearos.framework.core.base.view.ViewDebug.DEBUG_DRAW;
import static com.pearos.framework.core.base.view.ViewDebug.DEBUG_LAYOUT;

import static com.pearos.framework.core.base.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.pearos.framework.core.base.view.ViewGroup.LayoutParams.WRAP_CONTENT;
package com.pearos.framework.core.base.ui;

// === IMPORTS ===
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

// === CLASSE MASTER DO ARQUIVO ===
public class View {

    // -------------------------------
    // CAMPOS BÁSICOS DO VIEW
    // -------------------------------
    protected int x = 0;
    protected int y = 0;
    protected int width = 0;
    protected int height = 0;
    protected View parent = null;

    protected String tagName = "View";

    public View() {}

    public String getTagName() {
        return tagName;
    }

    public View getParent() {
        return parent;
    }

    // Método chamado pelo renderizador
    public void onDraw(Canvas canvas) {}

    // Método chamado quando toca
    public boolean onTouch(int action, int tx, int ty) { return false; }

    // ---------------------------------------------------------
    // CLASSE ViewGroup EMBUTIDA DENTRO DE View.java
    // ---------------------------------------------------------
    public static class ViewGroup extends View {

        private ArrayList<View> children = new ArrayList<>();

        public ViewGroup() {
            this.tagName = "Layout";
        }

        public void addView(View v) {
            v.parent = this;
            children.add(v);
        }

        public ArrayList<View> getChildren() {
            return children;
        }

        @Override
        public void onDraw(Canvas canvas) {
            for (View v : children) {
                v.onDraw(canvas);
            }
        }
    }

    // ---------------------------------------------------------
    // CLASSE TextView DENTRO DE View.java
    // ---------------------------------------------------------
    public static class TextView extends View {

        private String text = "";
        private int size = 16;
        private int color = Color.WHITE;

        public TextView() {
            this.tagName = "Text";
        }

        public void setText(String t) { text = t; }
        public void setSize(int s) { size = s; }
        public void setColor(int c) { color = c; }

        @Override
        public void onDraw(Canvas canvas) {
            canvas.drawText(text, x, y, size, color);
        }
    }

    // ---------------------------------------------------------
    // CLASSE Button DENTRO DE View.java
    // ---------------------------------------------------------
    public static class Button extends View {

        private String text = "";
        private int bg = Color.GRAY;

        public Button() {
            this.tagName = "Button";
        }

        public void setText(String t) { text = t; }
        public void setBackground(int c) { bg = c; }

        @Override
        public void onDraw(Canvas canvas) {
            canvas.drawRect(x, y, width, height, bg);
            canvas.drawText(text, x + 10, y + 10, 16, Color.WHITE);
        }
    }

    // ---------------------------------------------------------
    // CLASSE Color EMBUTIDA
    // ---------------------------------------------------------
    public static class Color {
        public static final int WHITE = 0xFFFFFFFF;
        public static final int BLACK = 0xFF000000;
        public static final int GRAY  = 0xFF444444;

        public static int parse(String s) {
            try {
                return (int)Long.parseLong(s.replace("#", ""), 16);
            } catch (Exception e) {
                return WHITE;
            }
        }
    }

    // ---------------------------------------------------------
    // CLASSE Canvas EMBUTIDA
    // ---------------------------------------------------------
    public static class Canvas {
        public void drawText(String txt, int x, int y, int size, int color) {}
        public void drawRect(int x, int y, int w, int h, int color) {}
    }

    // ---------------------------------------------------------
    // CLASSE AttributeSet EMBUTIDA
    // ---------------------------------------------------------
    public static class AttributeSet {
        private Map<String,String> map;

        public AttributeSet(Map<String,String> m) {
            this.map = m;
        }

        public String get(String k) {
            return map.get(k);
        }
    }

    // ---------------------------------------------------------
    // PARTE DO XML — TUDO DENTRO DESSE ARQUIVO
    // ---------------------------------------------------------
    public static class XmlLayoutParser {

        public View inflate(InputStream xmlStream) {

            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);

                XmlPullParser parser = factory.newPullParser();
                parser.setInput(xmlStream, "UTF-8");

                View root = null;
                ViewGroup currentGroup = null;

                int event = parser.getEventType();

                while (event != XmlPullParser.END_DOCUMENT) {

                    if (event == XmlPullParser.START_TAG) {

                        String tag = parser.getName();

                        Map<String,String> attrs = new HashMap<>();
                        for (int i = 0; i < parser.getAttributeCount(); i++) {
                            attrs.put(parser.getAttributeName(i), parser.getAttributeValue(i));
                        }

                        AttributeSet set = new AttributeSet(attrs);

                        View v = create(tag, set);

                        if (root == null) {
                            root = v;
                        }

                        if (currentGroup != null) {
                            currentGroup.addView(v);
                        }

                        if (v instanceof ViewGroup) {
                            currentGroup = (ViewGroup) v;
                        }
                    }

                    else if (event == XmlPullParser.END_TAG) {
                        if (currentGroup != null && currentGroup.getTagName().equals(parser.getName())) {
                            currentGroup = (ViewGroup) currentGroup.getParent();
                        }
                    }

                    event = parser.next();
                }

                return root;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        private View create(String tag, AttributeSet attrs) {

            switch (tag) {

                case "Text":
                    TextView tv = new TextView();
                    tv.setText(attrs.get("text"));
                    tv.setColor(Color.parse(attrs.get("color")));
                    tv.setSize(parseSize(attrs.get("size")));
                    return tv;

                case "Button":
                    Button bt = new Button();
                    bt.setText(attrs.get("text"));
                    bt.setBackground(Color.parse(attrs.get("background")));
                    return bt;

                case "Layout":
                    return new ViewGroup();

                default:
                    return new View();
            }
        }

        private int parseSize(String s) {
            try {
                return Integer.parseInt(s.replace("dp", ""));
            } catch (Exception e) {
                return 16;
            }
        }
    }
}
package frameworks.core.base.ui;

import java.util.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

// Imports estáticos (igual Android usa muito)
import static java.lang.Math.*;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * View.java
 * Sistema de Interface Único (sem arquivos externos)
 * Tudo fica aqui:
 *  - Parser XML modificado
 *  - Armazenamento de atributos
 *  - Renderização
 *  - Widgets básicos
 */
public class View {

    // ------------------------------
    // 1. Estrutura interna da View
    // ------------------------------

    public String type;
    public Map<String, String> attrs = new HashMap<>();
    public List<View> children = new ArrayList<>();


    // ------------------------------
    // 2. Construtor genérico
    // ------------------------------
    public View(String type) {
        this.type = type;
    }

    public void addChild(View v) {
        children.add(v);
    }


    // ------------------------------
    // 3. XML MODIFICADO DO TEU SISTEMA
    //
    // Exemplo:
    // <UI>
    //   <Label text="Olá mundo" size="22" color="#ffffff" />
    //   <Button text="OK" width="100" />
    // </UI>
    //
    // ------------------------------

    public static View loadFromModifiedXML(String xmlContent) {
        XMLParser parser = new XMLParser(xmlContent);
        return parser.parse();
    }


    // ------------------------------
    // 4. RENDERIZAÇÃO DA INTERFACE
    // (pra máquina entender)
    // ------------------------------

    public void render() {
        renderView(this, 0);
    }

    private void renderView(View v, int depth) {
        String pad = " ".repeat(depth * 2);
        System.out.println(pad + "[" + v.type + "] " + v.attrs);

        for (View c : v.children) {
            renderView(c, depth + 1);
        }
    }


    // --------------------------------
    // 5. PARSER XML MODIFICADO
    // --------------------------------
    static class XMLParser {

        private final String src;
        private int pos = 0;

        XMLParser(String s) {
            src = s.replace("\n", "").replace("\r", "").trim();
        }

        View parse() {
            skipSpaces();
            return readTag();
        }

        private View readTag() {
            expect('<');
            String tag = readName();

            View root = new View(tag);

            skipSpaces();

            // Lê atributos
            while (peek() != '>' && peek() != '/') {
                String attrName = readName();
                skipSpaces();
                expect('=');
                skipSpaces();
                String attrValue = readQuoted();
                root.attrs.put(attrName, attrValue);
                skipSpaces();
            }

            // Tag tipo <Button />
            if (peek() == '/') {
                expect('/');
                expect('>');
                return root;
            }

            // Fecha cabeçalho >
            expect('>');

            // Lê filhos
            while (!look("</" + tag)) {
                skipSpaces();
                if (peek() == '<') {
                    View child = readTag();
                    root.addChild(child);
                } else {
                    break;
                }
            }

            // Lê fechamento </tag>
            expect('<');
            expect('/');
            readName();
            expect('>');

            return root;
        }


        // -----------------------------
        // Funções auxiliares do parser
        // -----------------------------

        private char peek() {
            return src.charAt(pos);
        }

        private void expect(char c) {
            if (src.charAt(pos) != c)
                throw new RuntimeException("Erro de parser: esperado '" + c + "' em pos " + pos);
            pos++;
        }

        private boolean look(String s) {
            return src.startsWith(s, pos);
        }

        private String readName() {
            int start = pos;
            while (Character.isLetterOrDigit(peek()) || peek() == '_' || peek() == '-') {
                pos++;
            }
            return src.substring(start, pos);
        }

        private String readQuoted() {
            expect('"');
            int start = pos;
            while (peek() != '"') pos++;
            String v = src.substring(start, pos);
            expect('"');
            return v;
        }

        private void skipSpaces() {
            while (pos < src.length() && Character.isWhitespace(src.charAt(pos)))
                pos++;
        }
    }

}
    // ============================================================
    // 6. SISTEMA DE COORDENADAS MULTIDIMENSIONAIS (2D → 11D)
    // ============================================================

    public static class NDPoint {
        public final double[] coords;

        public NDPoint(int dimensions) {
            this.coords = new double[dimensions];
        }

        public void set(int dimensionIndex, double value) {
            if (dimensionIndex < 0 || dimensionIndex >= coords.length)
                throw new RuntimeException("Dimensão inválida: " + dimensionIndex);
            coords[dimensionIndex] = value;
        }

        public double get(int dimensionIndex) {
            return coords[dimensionIndex];
        }

        @Override
        public String toString() {
            return Arrays.toString(coords);
        }
    }


    // ============================================================
    // 7. VIEW base com suporte a posição ND (até 11D)
    // ============================================================

    public NDPoint positionND = new NDPoint(11);  // 2D padrão + 9D adicionais

    public void setPositionND(int dimension, double value) {
        positionND.set(dimension, value);
    }


    // ============================================================
    // 8. View especializada: TextPixels (texto puro em pixels)
    // ============================================================

    public static class TextPixelsView extends View {
        public String text;
        public int pixelSize;

        public TextPixelsView() {
            super("TextPixels");
        }

        @Override
        public void render() {
            System.out.println("[TextPixels] \"" + text + "\" size=" + pixelSize +
                " pos=" + positionND);
        }
    }


    // ============================================================
    // 9. Views 3D, 4D, …, 11D (matemática completa)
    // ============================================================

    public static class ObjectNDView extends View {
        public int dims;

        public ObjectNDView(int dims) {
            super("Object" + dims + "D");
            this.dims = dims;
        }

        @Override
        public void render() {
            System.out.println("[Object" + dims + "D] pos=" + positionND);
        }
    }


    // ============================================================
    // 10. Suporte no XML parser para TextPixels e ObjectND
    // ============================================================

    @Override
    public void addChild(View v) {
        children.add(v);
    }

    public static View createSpecialViewFromTag(String tag, Map<String,String> attrs) {
        switch (tag) {

            case "TextPixels": {
                TextPixelsView t = new TextPixelsView();
                t.text = attrs.getOrDefault("text", "");
                t.pixelSize = Integer.parseInt(attrs.getOrDefault("size", "10"));
                applyNDPosition(attrs, t);
                return t;
            }

            case "Object11D": {
                ObjectNDView o = new ObjectNDView(11);
                applyNDPosition(attrs, o);
                return o;
            }

            case "Object3D": {
                ObjectNDView o = new ObjectNDView(3);
                applyNDPosition(attrs, o);
                return o;
            }
        }

        return new View(tag);
    }

    private static void applyNDPosition(Map<String,String> attrs, View view) {
        for (int i = 0; i < 11; i++) {
            String key = "x" + (i + 1);
            if (attrs.containsKey(key)) {
                double v = Double.parseDouble(attrs.get(key));
                view.setPositionND(i, v);
            }
        }
    }
/**
para fazer um objeto 11D faz
    <Object11D
    x1="0"
    x2="2"
    x3="4"
    x4="8"
    x5="16"
    x6="32"
    x7="64"
    x8="128"
    x9="256"
    x10="512"
    x11="1024"
/>
    no XML */
    // ============================================================
    // 11. SISTEMA DE ANIMAÇÕES ND (2D → 11D) + propriedades comuns
    // ============================================================

    public static class Animation {
        public String property;     // exemplo: x1, x2, width, height, alpha...
        public double from;
        public double to;
        public long duration;
        public long startTime;
        public String easing;

        public boolean finished = false;

        public Animation(String property, double from, double to, long duration, String easing) {
            this.property = property;
            this.from = from;
            this.to = to;
            this.duration = duration;
            this.startTime = System.currentTimeMillis();
            this.easing = easing;
        }

        public double get(double progress) {
            switch (easing) {
                case "easeIn":
                    return from + (to - from) * (progress * progress);

                case "easeOut":
                    return from + (to - from) * (1 - Math.pow(1 - progress, 2));

                case "easeInOut":
                    return from + (to - from) *
                        (progress < 0.5 ? 2 * progress * progress : 1 - Math.pow(-2 * progress + 2, 2) / 2);

                default: // linear
                    return from + (to - from) * progress;
            }
        }
    }


    // Lista de animações ativas por View
    public List<Animation> animations = new ArrayList<>();


    // ============================================================
    // 12. Função pública pra animar qualquer coisa
    // ============================================================

    public void animate(String property, double from, double to, long duration, String easing) {
        Animation a = new Animation(property, from, to, duration, easing);
        animations.add(a);
    }

    public void animate(String property, double from, double to, long duration) {
        animate(property, from, to, duration, "linear");
    }


    // ============================================================
    // 13. Atualizador de animações (roda automaticamente ao renderizar)
    // ============================================================

    public void updateAnimations() {
        long now = System.currentTimeMillis();

        Iterator<Animation> it = animations.iterator();
        while (it.hasNext()) {
            Animation anim = it.next();

            long elapsed = now - anim.startTime;
            if (elapsed > anim.duration) {
                setAnimatedProperty(anim.property, anim.to);
                anim.finished = true;
                it.remove();
                continue;
            }

            double progress = (double) elapsed / anim.duration;
            double value = anim.get(progress);

            setAnimatedProperty(anim.property, value);
        }
    }


    // ============================================================
    // 14. Aplicar propriedades animadas (inclui ND)
    // ============================================================

    private void setAnimatedProperty(String prop, double value) {
        if (prop.startsWith("x")) {
            int dim = Integer.parseInt(prop.substring(1)) - 1;
            setPositionND(dim, value);
            return;
        }

        switch (prop) {
            case "width":
                this.width = (int) value;
                break;

            case "height":
                this.height = (int) value;
                break;

            case "alpha":
                this.alpha = value;
                break;

            case "rotation":
                this.rotation = value;
                break;

            case "scaleX":
                this.scaleX = value;
                break;

            case "scaleY":
                this.scaleY = value;
                break;
        }
    }


    // ============================================================
    // 15. Hook no render() base para rodar animações sempre
    // ============================================================

    @Override
    public void render() {
        updateAnimations(); // <-- animações rodando ao vivo!
        System.out.println("[View] render base posND=" + positionND);
    }
    // ============================================================
    // 16. SISTEMA DE SENSORES, AUDIO, CAMERA, NOTIFICAÇÕES E TEMPO
    // ============================================================

    // Gerente global (único) para acessar hardware
    public static class SystemManager {

        // ---------- SENSORES GENÉRICOS ----------
        public static class SensorValue {
            public double x, y, z;
            public long timestamp;

            @Override
            public String toString() {
                return "x=" + x + ", y=" + y + ", z=" + z + " @ " + timestamp;
            }
        }

        public Map<String, SensorValue> sensors = new HashMap<>();

        public SystemManager() {
            // todos os sensores possíveis
            sensors.put("accelerometer", new SensorValue());
            sensors.put("gyroscope", new SensorValue());
            sensors.put("magnetometer", new SensorValue());
            sensors.put("gravity", new SensorValue());
            sensors.put("rotation", new SensorValue());
            sensors.put("orientation", new SensorValue());
            sensors.put("light", new SensorValue());
            sensors.put("proximity", new SensorValue());
            sensors.put("temperature", new SensorValue());
            sensors.put("humidity", new SensorValue());
            sensors.put("pressure", new SensorValue());
            sensors.put("linear_acceleration", new SensorValue());
            sensors.put("step_counter", new SensorValue());
            sensors.put("heart_rate", new SensorValue());
        }

        // simula mudança de valor
        public void updateSensor(String type, double x, double y, double z) {
            if (sensors.containsKey(type)) {
                SensorValue v = sensors.get(type);
                v.x = x; v.y = y; v.z = z;
                v.timestamp = System.currentTimeMillis();
            }
        }

        public SensorValue getSensor(String type) {
            return sensors.get(type);
        }


        // ---------- MICROFONE ----------
        private boolean micEnabled = false;

        public void enableMicrophone() {
            micEnabled = true;
            System.out.println("[System] Microfone ligado");
        }

        public void disableMicrophone() {
            micEnabled = false;
            System.out.println("[System] Microfone desligado");
        }

        public boolean isMicrophoneOn() {
            return micEnabled;
        }


        // ---------- CÂMERAS ----------
        private boolean cameraFront = false;
        private boolean cameraBack = false;

        public void enableFrontCamera() {
            cameraFront = true;
            System.out.println("[System] Câmera frontal ON");
        }

        public void enableBackCamera() {
            cameraBack = true;
            System.out.println("[System] Câmera traseira ON");
        }

        public void disableCameras() {
            cameraFront = false;
            cameraBack = false;
            System.out.println("[System] Câmeras desligadas");
        }

        public boolean isFrontCameraOn() { return cameraFront; }
        public boolean isBackCameraOn()  { return cameraBack;  }


        // ---------- ÁUDIO / SPEAKER ----------
        private int volume = 50; // 0 a 100

        public void setVolume(int v) {
            volume = Math.max(0, Math.min(100, v));
            System.out.println("[System] Volume = " + volume);
        }

        public int getVolume() { return volume; }


        // ---------- ALERTAS ----------
        public void alert(String type, String msg) {
            System.out.println("[ALERTA - " + type + "] " + msg);
        }


        // ---------- NOTIFICAÇÕES ----------
        public void notify(String app, String msg) {
            System.out.println("[NOTIFICAÇÃO][" + app + "] " + msg);
        }


        // ---------- TEMPO / DATA ----------
        public String getDate() {
            Calendar c = Calendar.getInstance();
            int d = c.get(Calendar.DAY_OF_MONTH);
            int m = c.get(Calendar.MONTH) + 1;
            int y = c.get(Calendar.YEAR);
            return d + "/" + m + "/" + y;
        }

        public String getHour() {
            Calendar c = Calendar.getInstance();
            int h = c.get(Calendar.HOUR_OF_DAY);
            int min = c.get(Calendar.MINUTE);
            return String.format("%02d:%02d", h, min);
        }

        public String getTimeZone() {
            return TimeZone.getDefault().getID();
        }
    }


    // Único SystemManager do sistema
    public static final SystemManager system = new SystemManager();



    // ============================================================
    // 17. INTEGRAÇÃO COM VIEW — acessar sensores pelo XML
    // ============================================================

    // Exemplo:
    // <SensorRead type="accelerometer" />
    public static class SensorReadView extends View {
        public String type;

        public SensorReadView() {
            super("SensorRead");
        }

        @Override
        public void render() {
            SensorValue v = system.getSensor(type);
            System.out.println("[Sensor " + type + "] " + v);
        }
    }


    // XML parser adaptado pra sensores
    public static View createHardwareView(String tag, Map<String,String> attrs) {

        switch (tag) {
            case "SensorRead":
                SensorReadView s = new SensorReadView();
                s.type = attrs.getOrDefault("type", "accelerometer");
                return s;

            case "Alert":
                system.alert(attrs.getOrDefault("level","Normal"),
                             attrs.getOrDefault("text",""));
                return null;

            case "Notify":
                system.notify(attrs.getOrDefault("app","App"),
                              attrs.getOrDefault("text",""));
                return null;
        }

        return null;
    }
    // ============================================================
    // 18. DOCK DO SISTEMA + SCROLL HORIZONTAL
    // ============================================================

    public static class DockView extends View {

        public List<View> dockItems = new ArrayList<>();
        public double scrollOffset = 0;         // posição atual do scroll
        public double scrollVelocity = 0;       // velocidade (para inércia)
        public boolean scrolling = false;       // se está tocando
        public double lastTouchX = 0;

        public int dockHeight = 120;
        public int itemSpacing = 20;

        public DockView() {
            super("Dock");
        }

        public void addDockItem(View v) {
            dockItems.add(v);
        }

        @Override
        public void onTouch(double x, double y, String action) {

            if (action.equals("down")) {
                scrolling = true;
                lastTouchX = x;
                scrollVelocity = 0;
            }

            if (action.equals("move") && scrolling) {
                double dx = x - lastTouchX;
                scrollOffset += dx;
                lastTouchX = x;
            }

            if (action.equals("up")) {
                scrolling = false;
            }
        }

        @Override
        public void update() {
            super.update();

            // Inércia do scroll
            if (!scrolling) {
                scrollOffset += scrollVelocity;
                scrollVelocity *= 0.9; // atrito leve

                if (Math.abs(scrollVelocity) < 0.01)
                    scrollVelocity = 0;
            }
        }

        @Override
        public void render() {
            updateAnimations();
            update();

            System.out.println("=== [Dock] === scroll=" + scrollOffset);

            int index = 0;
            for (View item : dockItems) {
                double posX = width / 2 + (index * (item.width + itemSpacing)) + scrollOffset;
                double posY = height - dockHeight;

                item.setPositionND(0, posX);
                item.setPositionND(1, posY);

                item.render();
                index++;
            }
        }
    }


    // ============================================================
    // 19. DOCK ITEM (ícone simples)
    // ============================================================

    public static class DockItemView extends View {
        public String label = "";
        public String icon = "";

        public DockItemView() {
            super("DockItem");
        }

        @Override
        public void render() {
            System.out.println("[DockItem] '" + label +
                "' posND=" + Arrays.toString(positionND.coords));
        }
    }


    // ============================================================
    // 20. XML PARSER para dock
    // ============================================================

    public static View createDockViewFromXML(String tag, Map<String,String> attrs) {

        switch (tag) {
            case "Dock":
                DockView d = new DockView();
                return d;

            case "DockItem":
                DockItemView i = new DockItemView();
                i.label = attrs.getOrDefault("label", "App");
                i.icon = attrs.getOrDefault("icon", "");
                return i;
        }

        return null;
    }
/* ============================================================
 * 7. App Icon & App Name Renderer
 * ============================================================
*/

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class AppIconRenderer {

    // Cache global de ícones
    private static final Map<String, BufferedImage> ICON_CACHE = new HashMap<>();

    // Configurações visuais gerais
    private static final int DEFAULT_ICON_SIZE_DP = 48;
    private static final int DEFAULT_TEXT_SIZE_SP = 14;

    // Carrega o ícone do app a partir do manifesto
    public static BufferedImage loadAppIcon(String packageName) {
        try {
            // Se já está no cache, devolve
            if (ICON_CACHE.containsKey(packageName)) {
                return ICON_CACHE.get(packageName);
            }

            // Arquivo do ícone
            String path = "/system/apps/" + packageName + "/icon.png";

            BufferedImage img = ImageIO.read(new File(path));
            ICON_CACHE.put(packageName, img);
            return img;

        } catch (Exception e) {
            System.err.println("[UI][ICON] Erro ao carregar ícone de " + packageName);
            return null;
        }
    }

    // Escala pra densidade da tela (dp → px)
    public static BufferedImage scaleIcon(BufferedImage img, float density) {
        if (img == null) return null;

        int sizePx = (int) (DEFAULT_ICON_SIZE_DP * density);

        BufferedImage scaled = new BufferedImage(sizePx, sizePx, BufferedImage.TYPE_INT_ARGB);

        scaled.getGraphics().drawImage(img, 0, 0, sizePx, sizePx, null);
        return scaled;
    }

    // Renderiza ícone + nome do app
    public static void renderAppEntry(
            Graphics2D g,
            String packageName,
            String appName,
            float density,
            int x,
            int y
    ) {
        BufferedImage icon = loadAppIcon(packageName);

        // Se não carregou o ícone, desenha um quadrado cinza
        if (icon == null) {
            g.setColor(new Color(150, 150, 150));
            g.fillRect(x, y, 100, 100);
        } else {
            BufferedImage scaled = scaleIcon(icon, density);
            g.drawImage(scaled, x, y, null);
        }

        // Ajusta tipografia
        int textSizePx = (int) (DEFAULT_TEXT_SIZE_SP * density);
        g.setFont(new Font("Roboto", Font.PLAIN, textSizePx));
        g.setColor(Color.WHITE);

        // Desenha o nome abaixo do ícone
        g.drawString(appName, x, y + (int)(DEFAULT_ICON_SIZE_DP * density) + textSizePx);
    }

    // Renderiza só o ícone (para dock, atalhos, etc)
    public static void renderOnlyIcon(
            Graphics2D g,
            String packageName,
            float density,
            int x,
            int y
    ) {
        BufferedImage icon = loadAppIcon(packageName);
        if (icon == null) return;

        BufferedImage scaled = scaleIcon(icon, density);
        g.drawImage(scaled, x, y, null);
    }

    // Renderiza só o texto (caso a UI queira)
    public static void renderOnlyText(
            Graphics2D g,
            String appName,
            float density,
            int x,
            int y
    ) {
        int textSizePx = (int) (DEFAULT_TEXT_SIZE_SP * density);
        g.setFont(new Font("Roboto", Font.PLAIN, textSizePx));
        g.setColor(Color.WHITE);
        g.drawString(appName, x, y);
    }

}
