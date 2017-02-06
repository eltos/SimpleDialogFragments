package eltos.simpledialogfragment.color;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
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
import android.view.ViewGroup;

/**
 *
 * Created by eltos on 03.02.2017.
 */
public class ColorWheelView extends View {

    public static int DEFAULT_COLOR = 0xFFCF4747;

    private OnColorChangeListener mListener;

    private TriangleWithSuggestions triangle;
    private Rainbow rainbow;
    private final RectF circleBox = new RectF();
    private final Paint colorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private C myColor = new C(DEFAULT_COLOR);


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
        triangle = new TriangleWithSuggestions(); // new Triangle();

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
            return other.hsv[0] == hsv[0] && other.hsv[1] == hsv[1] && other.hsv[2] == hsv[2];
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
        C hue(int h){
            hsv[0] = mod(h, 360);
            return this;
        }
        C sat(float s){
            hsv[1] = Math.min(1, Math.max(0, s));
            return this;
        }
        C val(float v){
            hsv[2] = Math.min(1, Math.max(0, v));
            return this;
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
        triangle.invalidate();
        rainbow.setColor(myColor);
//        rainbow.invalidate();
        colorPaint.setColor(myColor.rgba());

        if (hsvChanged){
            invalidate();
        }

        if (changed && mListener != null && callListener){
            mListener.onColorChange(getColor());
        }
    }

    enum Keep {WIDTH, HEIGHT}

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        Keep keep = Keep.WIDTH;

        int desiredHeight = (int) dp(50);
        if (getLayoutParams().height == ViewGroup.LayoutParams.MATCH_PARENT){
            desiredHeight = MeasureSpec.getSize(heightMeasureSpec); // parent height
            keep = Keep.HEIGHT;
        }

        int desiredWidth = (int) dp(50);
        if (getLayoutParams().width == ViewGroup.LayoutParams.MATCH_PARENT){
            desiredWidth = MeasureSpec.getSize(widthMeasureSpec); // parent width
            keep = Keep.WIDTH;
        }

        int size = keep == Keep.HEIGHT ? desiredHeight : desiredWidth;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY || widthMode == MeasureSpec.AT_MOST) {
            int widthLimited = MeasureSpec.getSize(widthMeasureSpec);
            size = Math.min(size, widthLimited);
        }

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode == MeasureSpec.EXACTLY || heightMode == MeasureSpec.AT_MOST) {
            int heightLimited = MeasureSpec.getSize(heightMeasureSpec);
            size = Math.min(size, heightLimited);
        }

        setMeasuredDimension(size, size);


    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        float size = Math.max(dp(5), dp(30)*Math.min(w, h)/900);

        float padding = Math.max(dp(2), dp(10)*Math.min(w, h)/900);

        PointF center = new PointF(w/2, h/2);
        float radius = (Math.min(w, h) - padding - size)/2;


        rainbow.setGeometry(center, radius, size);
//        rainbow.invalidate();
        triangle.setGeometry(center, radius - size/2, padding);
        triangle.invalidate();


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
                setColorInternal(new C(myColor).hue(rainbow.hueAt(pointer)));
                handled = true;

            } else if (triangle.encloses(pointer)) {
                touch = Touch.TRIANGLE;
                setColorInternal(triangle.colorAt(pointer));
                handled = true;

            } else if (triangle.suggestionTouched(pointer) != null){
                touch = Touch.SUGGESTION;
                handled = true;
            }

        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (touch == Touch.HUE) {
                setColorInternal(new C(myColor).hue(rainbow.hueAt(pointer)));
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


    private float dp(int value){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                value /*dp*/, getResources().getDisplayMetrics());
    }

    private float mod(float i, float m){
        return (((i % m) + m) % m);
    }






    @Override
    protected void onDraw(Canvas canvas) {

        //canvas.drawOval(circleBox, colorPaint);

        triangle.draw(canvas);
        rainbow.draw(canvas);


    }




    private class TriangleWithSuggestions extends Triangle {
        private Field[] mFields = new Field[9];
        private Paint strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        private class Field{
            private Path rawPath = new Path();
            private Path cachedPath = new Path();
            private C color = new C(Color.BLACK);
            private Paint paint;
            public float startAngle = 0;
            public float endAngle = 0;

            Field(){
                paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                paint.setStyle(Paint.Style.FILL);
            }
        }

        TriangleWithSuggestions(){
            super();

            strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            strokePaint.setStyle(Paint.Style.STROKE);
            strokePaint.setColor(Color.YELLOW);

            for (int i = 0; i < mFields.length; i++) {
                mFields[i] = new Field();
            }

        }



        /**
         *
         * @param R radius
         * @param p padding
         * @param alpha angle in deg (0..120°)
         * @param pos true for pos rotation, false otherwise
         */
        private PointF calcInnerPoint(float R, float p, float alpha, boolean pos){
            float a = (float) Math.toRadians(alpha);
            float tan30 = (float) Math.tan(Math.toRadians(30));

            float x = alpha == 90 ? 0 : (float) (R / (1 + Math.tan(a) / tan30));
            float y = alpha == 90 ? R*tan30 : (float) (x * Math.tan(a));
            float xy = (float) Math.sqrt(x*x+y*y);

            float b = (float) (pos ? Math.toRadians(30) + a : Math.toRadians(150) - a);

            float d = (float) (Math.abs((0.5*Math.cos(b)-1)/Math.sin(b))*p);
            float r = (float) Math.sqrt(Math.pow(xy + d, 2) + Math.pow(p, 2)/4);

            float aId = (float) Math.asin(p/2/r);
            float aI = pos ? a+aId : a-aId;

            return new PointF(r*(float)Math.cos(aI), r*(float)Math.sin(aI));
        }



        private void createFieldGeometry(Field f, float startAngle, float sweepAngle) {
            startAngle = mod(startAngle, 360);
            int rot = startAngle < 120 ? 0 : startAngle < 240 ? 1 : 2;
            startAngle = mod(startAngle, 120);

            float R = mRadius - mPadding;
            float p = mPadding;

            PointF p_i1 = calcInnerPoint(R, p, startAngle, true);
            PointF p_i2 = calcInnerPoint(R, p, startAngle + sweepAngle, false);

            float aAd = (float) Math.toDegrees(Math.asin(p/2/R));
            float alpha_1 = startAngle + aAd;
            float alpha_2 = startAngle + sweepAngle - aAd;
//            PointF p_a1 = new PointF(R*(float)Math.cos(Math.toRadians(alpha_1)), R*(float)Math.sin(Math.toRadians(alpha_1)));
//            PointF p_a2 = new PointF(R*(float)Math.cos(Math.toRadians(alpha_2)), R*(float)Math.sin(Math.toRadians(alpha_2)));

            p_i1.offset(mCenter.x, mCenter.y);
            p_i2.offset(mCenter.x, mCenter.y);
//            p_a1.offset(mCenter.x, mCenter.y);
//            p_a2.offset(mCenter.x, mCenter.y);

            RectF oval = new RectF(mCenter.x - R, mCenter.y - R, mCenter.x + R, mCenter.y + R);


            //f.points = new PointF[]{p_i1, p_a1, p_a2, p_i2};
            f.rawPath = new Path();
            f.rawPath.moveTo(p_i1.x, p_i1.y);
//            f.path.lineTo(p_a1.x, p_a1.y);
//            f.path.lineTo(p_a2.x, p_a2.y);
            f.rawPath.arcTo(oval, alpha_1, alpha_2-alpha_1);
            f.rawPath.lineTo(p_i2.x, p_i2.y);

            f.rawPath.close();


            Matrix mMatrix = new Matrix();
            mMatrix.postRotate( -90 + rot*120, mCenter.x, mCenter.y);
            f.rawPath.transform(mMatrix);
            f.startAngle = mod(alpha_1 -90 + rot*120, 360);
            f.endAngle = mod(alpha_2 -90 + rot*120, 360);

        }




        @Override
        protected void updateGeometryDependant() {
            super.updateGeometryDependant();

//            for (int i = 0; i < mFields.length; i++) {
//                int alpha = 30 + 30*i + 30*(i/3);
//                createFieldGeometry(mFields[i], alpha - 15, 30);
//            }

            for (int i = 0; i < mFields.length; i++) {
                float alpha = 7.5f + 35*i + 15*(i/3);
                createFieldGeometry(mFields[i], alpha, 35);
            }

//            createFieldGeometry(mFields[0],   7.5f     , 35);
//            createFieldGeometry(mFields[1],   7.5f + 35, 35);
//            createFieldGeometry(mFields[2],   7.5f + 70, 35);
//            createFieldGeometry(mFields[3], 127.5f     , 35);
//            createFieldGeometry(mFields[4], 127.5f + 35, 35);
//            createFieldGeometry(mFields[5], 127.5f + 70, 35);
//            createFieldGeometry(mFields[6], 247.5f     , 35);
//            createFieldGeometry(mFields[7], 247.5f + 35, 35);
//            createFieldGeometry(mFields[8], 247.5f + 70, 35);

        }

        @Override
        protected void updateRotationDependant() {
            super.updateRotationDependant();

            Matrix rotationMatrix = new Matrix();
            rotationMatrix.postRotate(mRotation, mCenter.x, mCenter.y);

            for (Field field : mFields) {
                field.rawPath.transform(rotationMatrix, /* to */ field.cachedPath);
            }
        }

        @Override
        protected void updateColorDependant(boolean hsvChanged, boolean hueChanged) {
            super.updateColorDependant(hsvChanged, hueChanged);

            mFields[0].color = new C(mColor).sat(0.75f);        // light saturation shades
            mFields[1].color = new C(mColor).sat(0.50f);
            mFields[2].color = new C(mColor).sat(0.25f);
            mFields[3].color = new C(mColor).rotated(120);      // complementary colors
            mFields[4].color = new C(mColor).rotated(180);
            mFields[5].color = new C(mColor).rotated(240);
            mFields[6].color = new C(mColor).val(0.25f);        // dark value shades
            mFields[7].color = new C(mColor).val(0.50f);
            mFields[8].color = new C(mColor).val(0.75f);

            if (hsvChanged) {
                for (Field field : mFields) {
                    field.paint.setColor(field.color.rgb());
                }
            }

//            int[] rot = new int[]{30, 60, 90, 150, 180, 210, 270, 300, 330};
//
//            for (int i = 0; i < mFields.length; i++) {
//                mFields[i].color = mColor.rotated(rot[i]);
//                mFields[i].paint.setColor(mFields[i].color.rgb());
//            }
        }

        @Override
        public void draw(Canvas canvas) {
            super.draw(canvas);

            for (Field field : mFields) {
                canvas.drawPath(field.cachedPath, field.paint);
            }
        }


        public C suggestionTouched(PointF pointer){

            double r = Math.sqrt(Math.pow(pointer.x - mCenter.x, 2)+Math.pow(pointer.y - mCenter.y, 2));
            if (r > mRadius - mPadding){
                return null; // outside
            }

            if (encloses(pointer)){
                return null; // inside
            }

            float phi = mod((float) (Math.toDegrees(
                    Math.atan2(pointer.y-mCenter.y, pointer.x-mCenter.x)) - mRotation), 360);

            for (Field field : mFields) {
                if (between(field.startAngle, phi, field.endAngle)){
                    return field.color;
                }
            }

            return null;

        }
    }

    private boolean between(float lowAngle, float angle, float highAngle){
        lowAngle = mod(lowAngle, 360);
        angle = mod(angle, 360);
        highAngle = mod(highAngle, 360);
        if (lowAngle < highAngle){
            return lowAngle <= angle && angle <= highAngle;
        } else {
            return lowAngle <= angle || angle <= highAngle;
        }
    }


    private class Triangle {
        protected PointF mCenter = new PointF();
        protected float mRadius = 0;
        protected float mPadding = 0;
        protected int mRotation = 0;
        protected C mColor = new C(Color.BLACK);

        protected boolean geometryNeedsUpdate = true;
        protected boolean rotationNeedsUpdate = true;
        protected boolean colorHueChanged = true;
        protected boolean colorHsvChanged = true;
        protected boolean colorNeedsUpdate = true;

        private PointF A = new PointF();
        private PointF B = new PointF();
        private PointF C = new PointF();
        protected Path path = new Path();
        private final Paint paint;
        private final Paint dotPaint;
        class Sug{Paint paint; PointF pos; C color;}
        private final Sug[] suggestions = new Sug[3];
        private float suggestionSize = 0;
        private float dotSize = 0;
        private PointF dot = new PointF();

        Triangle(){
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

            // fix for bug in hardware accelerated rendering where ComposeShader is unsupported
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        public void setGeometry(PointF center, float radius, float padding){
            if (!mCenter.equals(center) || radius != mRadius || padding != mPadding){
                geometryNeedsUpdate = true;
            }
            mCenter = center;
            mRadius = radius;
            mPadding = padding;
        }


        public int getRotation() {
            return mRotation;
        }

        void setRotation(int rotation){
            if (mRotation != rotation) {
                rotationNeedsUpdate = true;
            }
            mRotation = rotation;
        }

        public void setColor(C color) {
            if (!mColor.equalAlphaHSV(color)) {
                colorNeedsUpdate = true;
            }
            colorHsvChanged |= !mColor.equalHSV(color);
            colorHueChanged |= mColor.hue() != color.hue();
            mColor = color;
        }

        public void forceInvalidate(){
            geometryNeedsUpdate = true;
            rotationNeedsUpdate = true;
            colorHueChanged = true;
            colorHsvChanged = true;
            colorNeedsUpdate = true;
            invalidate();
        }

        public void invalidate(){
            if (geometryNeedsUpdate){
                updateGeometryDependant();
            }
            if (geometryNeedsUpdate || rotationNeedsUpdate){
                updateRotationDependant();
            }
            if (geometryNeedsUpdate || rotationNeedsUpdate || colorNeedsUpdate){
                updateColorDependant(
                        geometryNeedsUpdate || rotationNeedsUpdate || colorHsvChanged,
                        geometryNeedsUpdate || rotationNeedsUpdate || colorHueChanged);
            }
            geometryNeedsUpdate = false;
            rotationNeedsUpdate = false;
            colorHueChanged = false;
            colorHsvChanged = false;
            colorNeedsUpdate = false;
        }

        protected void updateGeometryDependant(){

//            for (int i = 0; i < suggestions.length; i++) {
//                suggestions[i].pos.set(
//                        mCenter.x + (0.75f*mRadius-mPadding/4)*(float)Math.cos(Math.toRadians(mRotation - 30 + i*120)),
//                        mCenter.y + (0.75f*mRadius-mPadding/4)*(float)Math.sin(Math.toRadians(mRotation - 30 + i*120)));
//            }
//            suggestionSize = mRadius/4 - 0.75f*mPadding;
        }

        protected void updateRotationDependant(){
            A.set(mCenter.x + (mRadius-mPadding) * (float) Math.cos(Math.toRadians(mRotation - 90)),
                    mCenter.y + (mRadius-mPadding) * (float) Math.sin(Math.toRadians(mRotation - 90)));
            B.set(mCenter.x + (mRadius-mPadding) * (float) Math.cos(Math.toRadians(mRotation + 30)),
                    mCenter.y + (mRadius-mPadding) * (float) Math.sin(Math.toRadians(mRotation + 30)));
            C.set(mCenter.x + (mRadius-mPadding) * (float) Math.cos(Math.toRadians(mRotation + 150)),
                    mCenter.y + (mRadius-mPadding) * (float) Math.sin(Math.toRadians(mRotation + 150)));

            path = new Path();
            path.moveTo(A.x, A.y);
            path.lineTo(B.x, B.y);
            path.lineTo(C.x, C.y);
            path.close();

        }

        protected void updateColorDependant(boolean hsvChanged, boolean hueChanged){
            if (hueChanged) {
                Shader base = new LinearGradient(A.x, A.y, (B.x + C.x) / 2, (B.y + C.y) / 2,
                        Color.HSVToColor(new float[]{mColor.hue(), 1, 1}), Color.BLACK, Shader.TileMode.CLAMP);
                Shader light = new LinearGradient((A.x + C.x) / 2, (A.y + C.y) / 2, B.x, B.y,
                        Color.BLACK, Color.WHITE, Shader.TileMode.CLAMP);
                Shader both = new ComposeShader(base, light, PorterDuff.Mode.ADD);
                paint.setShader(both);
            }
            if (hsvChanged) {
                dotPaint.setColor(mColor.inverted().rgb());
                dot = new PointF(C.x + (B.x - C.x + (A.x - B.x) * mColor.sat()) * mColor.val(),
                        C.y + (B.y - C.y + (A.y - B.y) * mColor.sat()) * mColor.val());
            }
//            for (int i = 0; i < suggestions.length; i++) {
//                C c = mColor.rotated((i+1)*90);
//                suggestions[i].color = c;
//                suggestions[i].paint.setColor(c.rgb());
//            }
        }


        C colorAt(PointF point){
            PointF p = new PointF(point.x, point.y);

            float s = ((p.y-C.y)*(B.x-C.x) - (p.x-C.x)*(B.y-C.y)) /
                    ((p.x-C.x)*(A.y-B.y) - (p.y-C.y)*(A.x-B.x));
            float v = (p.x-C.x) / ((A.x-B.x)*s+B.x-C.x);

            if (v < 0){ s *= -1; }  // correct s for better user experience

            C newColor = new C(mColor);
            newColor.sat(s);
            newColor.val(v);

            return newColor;
        }



        boolean encloses(PointF point){
            boolean b1 = sign(point, A, B) < 0.0f,
                    b2 = sign(point, B, C) < 0.0f,
                    b3 = sign(point, C, A) < 0.0f;
            return ((b1 == b2) && (b2 == b3));
        }
        private float sign(PointF p1, PointF p2, PointF p3) {
            return (p1.x - p3.x) * (p2.y - p3.y) - (p2.x - p3.x) * (p1.y - p3.y);
        }

//        C suggestionTouched(PointF point){
//            for (Sug sug : suggestions) {
//                double d = Math.sqrt(Math.pow(sug.pos.x - point.x, 2)
//                        + Math.pow(sug.pos.y - point.y, 2));
//                if (d <= suggestionSize) {
//                    return sug.color;
//                }
//            }
//            return null;
//        }


        public void draw(Canvas canvas) {
            canvas.drawPath(path, paint);
            canvas.drawCircle(dot.x, dot.y, dotSize, dotPaint);
//            for (Sug sug : suggestions) {
//                canvas.drawCircle(sug.pos.x, sug.pos.y, suggestionSize, sug.paint);
//            }
        }
    }

    private class Rainbow {

        private RectF boundingBox = new RectF();
        private PointF center = new PointF();
        private float radius = 0;
        private float with = 0;
        private C mColor = new C(Color.BLACK);
        private final Paint paint;
        private final Paint markerPaint;
        private float[] marker = new float[]{0, 0, 0, 0};

        Rainbow(){
            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setStyle(Paint.Style.STROKE);

            markerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            markerPaint.setStyle(Paint.Style.STROKE);
            markerPaint.setStrokeWidth(dp(1));

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
            return (int) mod((float) (Math.toDegrees(
                    Math.atan2(pointer.y-center.y, pointer.x-center.x)) + 90), 360);
        }
    }

}
