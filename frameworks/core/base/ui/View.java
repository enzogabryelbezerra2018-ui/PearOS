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
