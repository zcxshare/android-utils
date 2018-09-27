package com.fuyekeji.www.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.linkface.utils.Util;

/**
 * 相机上面绘制蒙层,中间留个矩形框
 */
public class OverlayView extends View {
    private static final String TAG = OverlayView.class.getSimpleName();

    /**
     * 竖向扫描
     */
    public static final int SCAN_ORIENTATION_VERTICAL = 1;

    /**
     * 横向扫描
     */
    public static final int SCAN_ORIENTATION_HORIZONTAL = 2;

    /**
     * 四个角的颜色
     */
    private int mBorderColor =0xffffffff;

    /**
     * 绘制边界的画笔
     */
    private Paint mBorderPaint;

    /**
     * 背景颜色
     */
    private int mBackgroundColor = 0xbb000000;

    /**
     * 背景区域
     */
    private Path mLockedBackgroundPath;

    /**
     * 背景画笔
     */
    private Paint mLockedBackgroundPaint;

    /**
     * 覆盖层字体颜色
     */
    private int mTextColor = Color.WHITE;

    /**
     * 覆盖层字体
     */
    private String mScanText;

    /**
     * 覆盖层字体的大小
     */
    private float mTextSize = 14;

    /**
     * 多行字体时覆盖层字体的上下间距
     */
    private float mTextLineSpace = 6;

    /**
     * 绘制字体的画笔
     */
    private Paint mTextPaint;

    /**
     * 采集图像区域的大小(扫描区域)
     */
    private Rect mScanRect;

    /**
     * 预览区域大小
     */
    private Rect mCameraPreviewRect;

    /**
     * 横向扫描线图片
     */
    private Bitmap mScanLineHorizontalBitmap;

    /**
     * 纵向扫描图片
     */
    private Bitmap mScanLineVerticalBitmap;

    /**
     * 扫描方向。默认横向,无法识别方向则不扫描
     */
    private int mScanOrientation;

    /**
     * 是否开启扫描
     */
    private boolean mIsStartScan;

    /**
     * 切割扫描区域
     */
    private Path mClipScanPath;

    /**
     * 扫描的Matrix
     */
    private Matrix mScanLineMatrix;

    /**
     * 绘制的扫描取景框
     */
    private RectF mDrawScanRectF;

    /**
     * 扫描框上部分文字
     */
    private String[] mSplitScanText;
    private int mViewheight;
    private int mViewWidth;

    public OverlayView(Context context) {
        this(context, null);
    }

    public OverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initBackGroundPaint();
        initCornerPaint();
        initTextPaint();

        mScanText = "";
        mScanOrientation = SCAN_ORIENTATION_HORIZONTAL;
    }


    /**
     * 初始化背景画笔对象
     */
    private void initBackGroundPaint() {
        mLockedBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLockedBackgroundPaint.clearShadowLayer();
        mLockedBackgroundPaint.setStyle(Paint.Style.FILL);
        mLockedBackgroundPaint.setColor(mBackgroundColor); // 75% black
        mLockedBackgroundPaint.setAlpha(200);//set BackGround alpha, range of value 0~255
    }

    /**
     * 初始化绘制四个角的画笔
     */
    private void initCornerPaint() {
        mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorderPaint.clearShadowLayer();
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setStrokeWidth(dip2px(1));
    }

    /**
     * 绘制绘制字体的画笔
     */
    private void initTextPaint() {
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(sp2px(mTextSize));
        Util.setupTextPaintStyle(mTextPaint);
    }

    /**
     * 获取四个角的颜色
     *
     * @return
     */
    public int getBorderColor() {
        return mBorderColor;
    }

    /**
     * 设置四个角的颜色
     *
     * @param color
     */
    public void setBorderColor(@ColorInt int color) {
        mBorderColor = color;
        if (mBorderPaint != null) {
            mBorderPaint.setColor(mBorderColor);
        }
        invalidate();
    }

    public int getScanBackGroundColor() {
        return mBackgroundColor;
    }

    /**
     * 设置覆盖层背景颜色
     *
     * @param color
     */
    public void setScanBackGroundColor(int color) {
        mBackgroundColor = color;
        invalidate();
    }

    /**
     * 获取覆盖层字体
     *
     * @return
     */
    public String getScanText() {
        return mScanText;
    }

    /**
     * 设置覆盖层字体
     *
     * @param scanText
     */
    public void setScanText(String scanText) {
        this.mScanText = scanText;
        if (!TextUtils.isEmpty(scanText)) {
            mSplitScanText = mScanText.split("\n");
        } else {
            mSplitScanText = null;
        }
        invalidate();
    }

    /**
     * 设置横向扫描图片
     *
     * @param scanLineHorizontalBitmap
     */
    public void setScanLineHorizontalBitmap(Bitmap scanLineHorizontalBitmap) {
        this.mScanLineHorizontalBitmap = scanLineHorizontalBitmap;
    }

    /**
     * 设置纵向扫描图片
     *
     * @param scanLineVerticalBitmap
     */
    public void setScanLineVerticalBitmap(Bitmap scanLineVerticalBitmap) {
        this.mScanLineVerticalBitmap = scanLineVerticalBitmap;
    }


    /**
     * 设置当前的扫描方向
     *
     * @param scanOrientation
     */
    public void setScanOrientation(int scanOrientation) {
        this.mScanOrientation = scanOrientation;
    }

    /**
     * 是否开启扫描
     *
     * @param isStartScan
     */
    public void switchScan(boolean isStartScan) {
        mIsStartScan = isStartScan;
    }

    /**
     * 更新覆盖层字体和字体颜色
     *
     * @param scanText
     * @param textColor
     */
    public void updateTextAndColor(String scanText, int textColor) {
        this.mScanText = scanText;
        if (!TextUtils.isEmpty(scanText)) {
            mSplitScanText = mScanText.split("\n");
        } else {
            mSplitScanText = null;
        }
        this.mTextColor = textColor;
        if (mTextPaint != null) {
            mTextPaint.setColor(mTextColor);
        }
        invalidate();
    }

    /**
     * 设置预览区域大小
     *
     * @param rect
     */
    public void setCameraPreviewRect(Rect rect) {
        mCameraPreviewRect = rect;
    }

    /**
     * 设置预览区域和扫描区域的大小
     *
     * @param cameraPreviewRect
     * @param scanRect
     */
    public void setPreviewAndScanRect(Rect cameraPreviewRect, Rect scanRect) {
        mCameraPreviewRect = cameraPreviewRect;
        mScanRect = scanRect;
        if (mCameraPreviewRect != null) {
            mLockedBackgroundPath = new Path();
            mLockedBackgroundPath.addRect(new RectF(mCameraPreviewRect), Path.Direction.CCW);
            mLockedBackgroundPath.addRoundRect(new RectF(mScanRect), dip2px(14), dip2px(14), Path.Direction.CW);
        }
        invalidate();
        initClipScanPath();
    }

    private void initClipScanPath() {
        mClipScanPath = new Path();
        mClipScanPath.addRoundRect(new RectF(mScanRect), dip2px(14), dip2px(14), Path.Direction.CW);
    }

    @Override
    public void onDraw(Canvas canvas) {

        if (mScanRect == null || mCameraPreviewRect == null) {
            return;
        }
        drawBackGround(canvas);
        drawRoundRect(canvas);
        drawText(canvas);
        if (mIsStartScan) {
            drawScanLine(canvas);
        }
    }

    /**
     * 绘制背景
     *
     * @param canvas
     */
    private void drawBackGround(Canvas canvas) {
        mLockedBackgroundPaint.setColor(mBackgroundColor);
        // draw lock shadow.
        canvas.drawPath(mLockedBackgroundPath, mLockedBackgroundPaint);
    }

    private void drawRoundRect(Canvas canvas) {
        if (mDrawScanRectF == null) {
            mDrawScanRectF = new RectF(mScanRect);
        }
        canvas.drawRoundRect(mDrawScanRectF, dip2px(14), dip2px(14), mBorderPaint);
    }

    public int getScanLeft() {
        return mScanRect.left;
    }

    public int getScanTop() {
        return mScanRect.top;
    }

    public int getScanRight() {
        return mScanRect.right;
    }

    public int getScanBottom() {
        return mScanRect.bottom;
    }

    /**
     * 绘制覆盖层文字
     *
     * @param canvas
     */
    private void drawText(Canvas canvas) {
        float lineHeight = dip2px(mTextLineSpace + mTextSize);
        if (mSplitScanText != null) {
            float y = mScanRect.top - lineHeight;
            int width = getWidth();
            for (int i = 0; i < mSplitScanText.length; i++) {
                canvas.drawText(mSplitScanText[i], width / 2, y, mTextPaint);
                y += lineHeight;
            }
        }
    }

    private void drawScanLine(Canvas canvas) {
        switch (mScanOrientation) {
            case SCAN_ORIENTATION_HORIZONTAL:
                drawHorizontalScanLine(canvas);
                break;
            case SCAN_ORIENTATION_VERTICAL:
                drawVerticalScanLine(canvas);
                break;
            default:
                break;
        }
    }

    private int mScanType = -1;
    private int mCurrentLineOffset = 0;

    /**
     * 绘制扫描线
     *
     * @param canvas
     */
    private void drawHorizontalScanLine(Canvas canvas) {

        canvas.save();

        if (mScanLineHorizontalBitmap != null) {

            //切割扫描区域

            if (mClipScanPath == null) {
                initClipScanPath();
            }
            canvas.clipPath(mClipScanPath);

            if (mScanLineMatrix == null) {
                mScanLineMatrix = new Matrix();
            } else {
                mScanLineMatrix.reset();
            }

            //扫描线偏移距离，右边坐标变化形成扫描效果
            mCurrentLineOffset += mScanType * 8;

            float scale = (mScanRect.height() + 0.0f) / mScanLineHorizontalBitmap.getHeight() * 1.2f;
            mScanLineMatrix.postScale(scale, scale);

            int currentScanLineX = mScanRect.right + mCurrentLineOffset;
            mScanLineMatrix.postTranslate(currentScanLineX, mScanRect.top - 12 * scale);

            //扫描到顶部之后扫描线旋转180度，继续向下扫描
            if (mScanType > 0) {
                float centerX = currentScanLineX + mScanLineHorizontalBitmap.getWidth() / 2;
                float centerY = mScanRect.top + 6 + mScanRect.height() / 2;
                mScanLineMatrix.postRotate(180, centerX, centerY);
            }

            //绘制扫描线
            canvas.drawBitmap(mScanLineHorizontalBitmap, mScanLineMatrix, mBorderPaint);

            if ((currentScanLineX + mScanLineHorizontalBitmap.getWidth()) < mScanRect.left) {
                mScanType = 1;
            }
            if (currentScanLineX > mScanRect.right) {
                mScanType = -1;
            }
        }

        canvas.restore();

        invalidate();

    }

    private void drawVerticalScanLine(Canvas canvas) {
        canvas.save();

        if (mScanLineVerticalBitmap != null) {

            //切割扫描区域
            if (mClipScanPath == null) {
                initClipScanPath();
            }
            canvas.clipPath(mClipScanPath);

            if (mScanLineMatrix == null) {
                mScanLineMatrix = new Matrix();
            } else {
                mScanLineMatrix.reset();
            }

            //扫描线偏移距离，右边坐标变化形成扫描效果
            mCurrentLineOffset += mScanType * 8;

            float scale = (mScanRect.width() + 0.0f) / mScanLineVerticalBitmap.getWidth() * 1.2f;
            mScanLineMatrix.postScale(scale, scale);

            int currentScanLineY = mScanRect.bottom + mCurrentLineOffset;

            mScanLineMatrix.postTranslate(mScanRect.left - 12 * scale, currentScanLineY);

            //扫描到顶部之后扫描线旋转180度，继续向下扫描
            if (mScanType > 0) {
                float centerX = mScanRect.left + mScanRect.width() / 2;
                float centerY = currentScanLineY + mScanLineVerticalBitmap.getHeight() / 2;
                mScanLineMatrix.postRotate(180, centerX, centerY);
            }

            //绘制扫描线
            canvas.drawBitmap(mScanLineVerticalBitmap, mScanLineMatrix, mBorderPaint);

            if ((currentScanLineY + mScanLineVerticalBitmap.getHeight()) < mScanRect.top) {
                mScanType = 1;
            }
            if (currentScanLineY > mScanRect.bottom) {
                mScanType = -1;
            }
        }

        canvas.restore();

        invalidate();
    }

    public int dip2px(float dpValue) {
        int densityDpi = getResources().getDisplayMetrics().densityDpi;
        return (int) (dpValue * (densityDpi / 160));
    }

    private int sp2px(float spValue) {
        final float fontScale = getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

}