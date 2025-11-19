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
