package eltos.simpledialogfragment.color;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import eltos.simpledialogfragment.R;

/**
 *
 * Created by eltos on 03.02.2017.
 */
public class ColorWheelView extends View {


    private OnColorChangeListener mListener;

    private Triangle triangle;
    private Rainbow rainbow;
    private final RectF circleBox = new RectF();
    private final Paint colorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);


    interface OnColorChangeListener {
        void onColorChange(int color);
    }

    public ColorWheelView(Context context) {
        this(context, null);
    }

    public ColorWheelView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorWheelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        triangle = new Triangle();
        rainbow = new Rainbow();
    }

    public void setColor(int color){
        setColor(color, true);
    }

    public void setColor(int color, boolean callListener){
        setColorInternal(new C(color), callListener);
    }

    public void updateAlpha(int alpha){
        updateAlpha(alpha, true);
    }

    public void updateAlpha(int alpha, boolean callListener){
        C newC = new C(myColor);
        newC.alpha(alpha);
        setColorInternal(newC, callListener);
    }

    public int getColor(){
        return myColor.rgba();
    }

    public void setOnColorChangeListener(OnColorChangeListener listener){
        mListener = listener;
    }



    private C myColor = new C(Color.RED);

    private class C {
        private int alpha = 0xFF;
        private float[] hsv = new float[3];

        C(int a, int h, float s, float v){
            alpha(a);
            hue(h);
            sat(s);
            val(v);
        }

        C(int rgba){
            float[] hsv = new float[3];
            Color.colorToHSV(rgba, hsv);
            int alpha = Color.alpha(rgba);

            alpha(alpha);
            hue((int) hsv[0]);
            sat(hsv[1]);
            val(hsv[2]);
        }

        C(C c){
            this(c.alpha(), c.hue(), c.sat(), c.val());
        }

        public boolean equalAlpha(C other){
            return other.alpha == alpha;
        }
        public boolean equalHSV(C other){
            return other.hsv[0] == hsv[0] && other.hsv[1] == hsv[1] && other.hsv[1] == hsv[1];
        }
        public boolean equalAlphaHSV(C other){
            return equalAlpha(other) && equalHSV(other);
        }

        @Deprecated
        @Override
        public boolean equals(Object o) {
            return super.equals(o) || o instanceof C && ((C) o).alpha == alpha
                    && ((C) o).hsv[0] == hsv[0] && ((C) o).hsv[1] == hsv[1] && ((C) o).hsv[2] == hsv[2];
        }

        int rgb(){ return Color.HSVToColor(hsv); }
        int rgba(){ return Color.HSVToColor(alpha, hsv); }
        int r(){ return (rgb() >> 16) & 0xFF; }
        int g(){ return (rgb() >> 8) & 0xFF; }
        int b(){ return rgb() & 0xFF; }
        int alpha(){ return alpha; }
        void alpha(int a){ alpha = a & 0xFF; }
        int hue(){
            return (int) hsv[0];
        }
        float sat(){
            return hsv[1];
        }
        float val(){
            return hsv[2];
        }
        void hue(int h){
            hsv[0] = h % 360;
        }
        void sat(float s){
            hsv[1] = Math.min(1, Math.max(0, s));
        }
        void val(float v){
            hsv[2] = Math.min(1, Math.max(0, v));
        }

        C inverted(){

            return new C(Color.argb(alpha, 255-r(), 255-g(), 255-b()));
        }

        C contrastColor(){
            if (r()*0.299+g()*0.587+b()*0.144 >= 128){
                return new C(Color.BLACK);
            } else {
                return new C(Color.WHITE);
            }
        }

        public C rotated(int degrees) {
            return new C(alpha(), hue() + degrees, sat(), val());
        }
    }


    private void setColorInternal(C color) {
        setColorInternal(color, true);
    }
    private void setColorInternal(C color, boolean callListener){
        boolean hsvChanged = !myColor.equalHSV(color);
        boolean changed = !myColor.equalAlphaHSV(color);
        myColor = color;

        triangle.setColor(myColor);
        triangle.setRotation(myColor.hue());
        rainbow.setColor(myColor);
        colorPaint.setColor(myColor.rgba());

        if (hsvChanged){
            invalidate();
        }

        if (changed && mListener != null && callListener){
            mListener.onColorChange(getColor());
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        float size = getResources().getDimension(R.dimen.defaultWheelCircleWith);

        float padding = dp(10);

        PointF center = new PointF(w/2, h/2);
        float radius = (Math.min(w, h) - padding - size)/2;


        rainbow.setGeometry(center, radius, size);

        triangle.setGeometry(center, radius - size/2, padding);


        circleBox.set(center.x - radius, center.y - radius, center.x + radius, center.y + radius);

        colorPaint.setStyle(Paint.Style.FILL);

    }


    private enum Touch {NONE, TRIANGLE, SUGGESTION, HUE}
    private Touch touch = Touch.NONE;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        boolean handled = false;

        PointF pointer = new PointF(event.getX(), event.getY());

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (rainbow.encloses(pointer)) {
                // touch on hue circle
                touch = Touch.HUE;
                handled = true;

            } else if (triangle.encloses(pointer)) {
                touch = Touch.TRIANGLE;
                handled = true;

            } else if (triangle.suggestionTouched(pointer) != null){
                touch = Touch.SUGGESTION;
                handled = true;
            }

        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (touch == Touch.HUE) {
                C newC = new C(myColor);
                newC.hue(rainbow.hueAt(pointer));
                setColorInternal(newC);
                handled = true;

            } else if (touch == Touch.TRIANGLE) {
                setColorInternal(triangle.colorAt(pointer));
                handled = true;

            } else if (touch == Touch.SUGGESTION){
                if (triangle.suggestionTouched(pointer) == null){
                    touch = Touch.NONE;
                }
                handled = true;
            }


        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (touch == Touch.SUGGESTION){
                C c = triangle.suggestionTouched(pointer);
                if (c != null) { setColorInternal(c); }
            }

            touch = Touch.NONE;
            handled = true;

        }

        if (handled) {
            if (getParent() != null && event.getAction() == MotionEvent.ACTION_DOWN) {
                // prevent parent (e.g. scroll view) from reaction on further events
                // during this touch
                getParent().requestDisallowInterceptTouchEvent(true);
            }
            return true; // event was handled
        }

        return super.dispatchTouchEvent(event);

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // Try for a width based on our minimum
        int minW = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();
        int w = resolveSizeAndState(minW, widthMeasureSpec, 1);

        setMeasuredDimension(w, w);
    }


    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);

        ss.saveColor = myColor.hsv;
        ss.saveAlpha = myColor.alpha;

        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if(!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState ss = (SavedState)state;
        super.onRestoreInstanceState(ss.getSuperState());

        myColor = new C(ss.saveAlpha, (int) ss.saveColor[0], ss.saveColor[1], ss.saveColor[2]);
    }

    static class SavedState extends BaseSavedState {
        float[] saveColor;
        int saveAlpha;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            in.readFloatArray(saveColor);
            saveAlpha = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeFloatArray(saveColor);
            out.writeInt(saveAlpha);
        }

        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }
                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }


    //
//    class Tri {
//        private Bitmap bitmap;
//        private Paint paint;
//        private Matrix matrix;
//
//        Tri(int size){
//            bitmap = createColorTriangleBitmap(Color.RED, size);
//            paint = new Paint();
//            matrix = new Matrix();
//            setRotation(0);
//        }
//
//        public void setHue(float hue) {
//            paint.setColorFilter(createHueFilter(getHue()));
//        }
//
//        public void setRotation(float degrees){
//            matrix.setRotate(degrees - 120, bitmap.getWidth()/2, bitmap.getHeight()/2);
//        }
//
//        public void draw(Canvas canvas){
//            canvas.drawBitmap(bitmap, matrix, paint);
//        }
//
//        public ColorFilter createHueFilter(float degrees) {
//            ColorMatrix cm = new ColorMatrix();
//
//            degrees %= 360;
//            if (degrees != 0) {
//                float cosVal = (float) Math.cos(Math.toRadians(degrees));
//                float sinVal = (float) Math.sin(Math.toRadians(degrees));
//                float lumR = 0.213f;
//                float lumG = 0.715f;
//                float lumB = 0.072f;
//                float[] mat = new float[]
//                        {
//                                lumR + cosVal * (1 - lumR) + sinVal * (-lumR), lumG + cosVal * (-lumG) + sinVal * (-lumG), lumB + cosVal * (-lumB) + sinVal * (1 - lumB), 0, 0,
//                                lumR + cosVal * (-lumR) + sinVal * (0.143f), lumG + cosVal * (1 - lumG) + sinVal * (0.140f), lumB + cosVal * (-lumB) + sinVal * (-0.283f), 0, 0,
//                                lumR + cosVal * (-lumR) + sinVal * (-(1 - lumR)), lumG + cosVal * (-lumG) + sinVal * (lumG), lumB + cosVal * (1 - lumB) + sinVal * (lumB), 0, 0,
//                                0f, 0f, 0f, 1f, 0f,
//                                0f, 0f, 0f, 0f, 1f};
//                cm.postConcat(new ColorMatrix(mat));
//            }
//
//            return new ColorMatrixColorFilter(cm);
//        }
//
//        private Bitmap createColorTriangleBitmap(int color, int size) {
//
//            Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
//
//            float[] hsv = new float[3];
//            Color.colorToHSV(color, hsv);
//
//            for (int x = 0; x < size; x++) {
//                for (int y = 0; y < size; y++) {
//                    if (y <= 0.75*size) {
//                        float v = (float) y / (0.75f * size);
//                        float dx = 0.433f * v;
//                        float x0 = (0.5f - dx) * size;
//                        float x1 = (0.5f + dx) * size;
//                        if (x0 <= x && x <= x1) {
//                            float s = ((float) x - x0) / (x1 - x0);
//                            hsv[1] = s; // saturation 0 .. 1
//                            hsv[2] = v; // value 0 .. 1
//
//                            int c = Color.HSVToColor(hsv);
//
//                            bitmap.setPixel(x, y, c);
//
//                        }
//                    }
//                }
//            }
//
//            return bitmap;
//
//        }
//
//
//    }


    private float dp(int value){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                value /*dp*/, getResources().getDisplayMetrics());
    }








    @Override
    protected void onDraw(Canvas canvas) {

        //canvas.drawOval(circleBox, colorPaint);

        triangle.draw(canvas);
        rainbow.draw(canvas);


    }








    private class Triangle {
        private PointF mCenter = new PointF();
        private float mRadius;
        private float mPadding;
        private int mRotation;
        private C mColor = new C(Color.BLACK);

        private PointF[] points;
        private Path path;
        private final Paint paint;
        private final Paint dotPaint;
        class Sug{Paint paint; PointF pos; C color;}
        private final Sug[] suggestions = new Sug[3];
        private float suggestionSize;
        private float dotSize;
        private PointF dot = new PointF();

        Triangle(){
            this(new PointF(), 0);
        }
        Triangle(PointF center, float radius){
            this(center, radius, 0, new C(Color.BLACK));
        }
        Triangle(PointF center, float radius, int rotation, C color){
            mRotation = rotation;
            mColor = color;

            dotSize = dp(4);
            float marker = dp(1);

            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setStyle(Paint.Style.FILL);

            dotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            dotPaint.setStyle(Paint.Style.STROKE);
            dotPaint.setStrokeWidth(marker);


            for (int i = 0; i < suggestions.length; i++) {
                suggestions[i] = new Sug();
                suggestions[i].paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                suggestions[i].paint.setStyle(Paint.Style.FILL);
                suggestions[i].pos = new PointF();
            }

            setGeometry(center, radius, 0);

            // fix for bug in hardware accelerated rendering where ComposeShader is unsupported
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        public void setGeometry(PointF center, float radius, float padding){
            mCenter = center;
            mRadius = radius;
            mPadding = padding;

            updateGeometry();
            updateShader();
            updateDot();
        }


        public int getRotation() {
            return mRotation;
        }

        void setRotation(int rotation){
            if (mRotation != rotation) {
                mRotation = rotation;
                updateGeometry();
                updateShader();
                updateDot();
            }
        }

        public void setColor(C color) {
            if (mColor.hue() != color.hue()) {
                mColor = color;
                updateShader();
            }

            mColor = color;
            updateDot();

        }

        private void updateGeometry(){
            points = new PointF[]{
                    new PointF(mCenter.x + (mRadius-mPadding) * (float) Math.cos(Math.toRadians(mRotation - 90)),
                            mCenter.y + (mRadius-mPadding) * (float) Math.sin(Math.toRadians(mRotation - 90))),
                    new PointF(mCenter.x + (mRadius-mPadding) * (float) Math.cos(Math.toRadians(mRotation + 30)),
                            mCenter.y + (mRadius-mPadding) * (float) Math.sin(Math.toRadians(mRotation + 30))),
                    new PointF(mCenter.x + (mRadius-mPadding) * (float) Math.cos(Math.toRadians(mRotation + 150)),
                            mCenter.y + (mRadius-mPadding) * (float) Math.sin(Math.toRadians(mRotation + 150)))
            };

            path = new Path();
            path.moveTo(points[0].x, points[0].y);
            path.lineTo(points[1].x, points[1].y);
            path.lineTo(points[2].x, points[2].y);
            path.close();

            for (int i = 0; i < suggestions.length; i++) {
                suggestions[i].pos.set(
                        mCenter.x + (0.75f*mRadius-mPadding/4)*(float)Math.cos(Math.toRadians(mRotation - 30 + i*120)),
                        mCenter.y + (0.75f*mRadius-mPadding/4)*(float)Math.sin(Math.toRadians(mRotation - 30 + i*120)));
            }
            suggestionSize = mRadius/4 - 0.75f*mPadding;
        }

        private void updateShader(){
            PointF A = points[0];
            PointF B = points[1];
            PointF C = points[2];

            Shader base = new LinearGradient(A.x, A.y, (B.x + C.x)/2, (B.y + C.y)/2,
                    Color.HSVToColor(new float[]{mColor.hue(), 1, 1}), Color.BLACK, Shader.TileMode.CLAMP);
            Shader light = new LinearGradient((A.x + C.x)/2, (A.y + C.y)/2, B.x, B.y,
                    Color.BLACK, Color.WHITE, Shader.TileMode.CLAMP);
            Shader both = new ComposeShader(base, light, PorterDuff.Mode.ADD);

            paint.setShader(both);

        }

        private void updateDot(){
            PointF A = points[0];
            PointF B = points[1];
            PointF C = points[2];

            dotPaint.setColor(mColor.inverted().rgb());
            dot = new PointF(C.x + (B.x - C.x + (A.x - B.x)*mColor.sat())*mColor.val(),
                    C.y + (B.y - C.y + (A.y - B.y)*mColor.sat())*mColor.val());

            for (int i = 0; i < suggestions.length; i++) {
                C c = mColor.rotated((i+1)*90);
                suggestions[i].color = c;
                suggestions[i].paint.setColor(c.rgb());
            }

        }


        C colorAt(PointF point){
            PointF A = points[0];
            PointF B = points[1];
            PointF C = points[2];

            float s = ((point.y-C.y)*(B.x-C.x) - (point.x - C.x)*(B.y-C.y)) /
                    ((point.x-C.x)*(A.y-B.y) - (point.y-C.y)*(A.x-B.x));
            float v = (point.x-C.x) / ((A.x-B.x)*s+B.x-C.x);

            C newColor = new C(mColor);
            newColor.sat(s);
            newColor.val(v);

            return newColor;
        }



        boolean encloses(PointF point){
            boolean b1 = sign(point, points[0], points[1]) < 0.0f,
                    b2 = sign(point, points[1], points[2]) < 0.0f,
                    b3 = sign(point, points[2], points[0]) < 0.0f;

            return ((b1 == b2) && (b2 == b3));
        }
        private float sign(PointF p1, PointF p2, PointF p3) {
            return (p1.x - p3.x) * (p2.y - p3.y) - (p2.x - p3.x) * (p1.y - p3.y);
        }

        C suggestionTouched(PointF point){
            for (Sug sug : suggestions) {
                double d = Math.sqrt(Math.pow(sug.pos.x - point.x, 2)
                        + Math.pow(sug.pos.y - point.y, 2));
                if (d <= suggestionSize) {
                    return sug.color;
                }
            }
            return null;
        }


        public void draw(Canvas canvas) {
            canvas.drawPath(path, paint);
            canvas.drawCircle(dot.x, dot.y, dotSize, dotPaint);
            for (Sug sug : suggestions) {
                canvas.drawCircle(sug.pos.x, sug.pos.y, suggestionSize, sug.paint);
            }
        }
    }

    private class Rainbow {

        private RectF boundingBox;
        private PointF center;
        private float radius;
        private float with;
        private C mColor = new C(Color.BLACK);
        private final Paint paint;
        private final Paint markerPaint;
        private float[] marker;

        Rainbow(){
            this(new PointF(), 0, 0);
        }
        Rainbow(PointF center, float radius, float with){
            this(center, radius, with, new C(Color.BLACK));
        }
        Rainbow(PointF center, float radius, float with, C color){
            mColor = color;

            float marker = dp(1);

            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setStyle(Paint.Style.STROKE);

            markerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            markerPaint.setStyle(Paint.Style.STROKE);
            markerPaint.setStrokeWidth(marker);

            setGeometry(center, radius, with);
        }

        public void setGeometry(PointF center, float radius, float with){
            this.center = center;
            this.radius = radius;
            this.with = with;

            paint.setStrokeWidth(with);

            boundingBox = new RectF(center.x - radius, center.y - radius,
                    center.x + radius, center.y + radius);

            updateShader();
            updateMarker();
        }


        void setColor(C color){
            if (mColor.hue() != color.hue()){
                mColor = color;
                updateMarker();
            }
            mColor = color;
        }

        private void updateShader(){
            int[] gradient = new int[]{Color.RED, Color.YELLOW, Color.GREEN,
                    Color.CYAN, Color.BLUE, Color.MAGENTA, Color.RED};
            SweepGradient rainbow = new SweepGradient(center.x, center.y, gradient, null);

            paint.setShader(rainbow);
        }

        private void updateMarker(){
            markerPaint.setColor(new C(0xFF, mColor.hue(), 1.0f, 1.0f).inverted().rgb());
            float sx = (float) Math.cos(Math.toRadians(mColor.hue())),
                    sy = (float) Math.sin(Math.toRadians(mColor.hue()));
            marker = new float[]{
                    center.x + (radius -with/3)*sx, center.y + (radius -with/3)*sy,
                    center.x + (radius +with/3)*sx, center.y + (radius +with/3)*sy};
        }


        public void draw(Canvas canvas) {
            canvas.save();
            canvas.rotate(-90, canvas.getWidth() / 2, canvas.getHeight() / 2);
            canvas.drawArc(boundingBox, 0, 360, false, paint);
            canvas.drawLines(marker, markerPaint);
            canvas.restore();
        }

        public boolean encloses(PointF pointer) {
            double r = Math.sqrt(Math.pow(pointer.x-center.x, 2) + Math.pow(pointer.y-center.y, 2));
            return radius - with <= r && r <= radius + with;
        }

        public int hueAt(PointF pointer) {
            return (int) Math.toDegrees(Math.atan2(pointer.y-center.y, pointer.x-center.x)) + 450;
        }
    }

}
