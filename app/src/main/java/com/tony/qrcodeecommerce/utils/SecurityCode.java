package com.tony.qrcodeecommerce.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Locale;
import java.util.Random;

public class SecurityCode {
    private static final char[] CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    private static SecurityCode bpUtil;

    public static SecurityCode getInstance() {
        if (bpUtil == null) bpUtil = new SecurityCode();
        return bpUtil;
    }

    // default settings
    private static final int DEFAULT_CODE_LENGTH = 4;// 驗證碼的長度 這裡是4位
    private static final int DEFAULT_FONT_SIZE = 60;// 字體大小

    private static final int DEFAULT_LINE_NUMBER = 3;// 多少條干擾線
    private static final int BASE_PADDING_LEFT = 20; // 左邊距
    private static final int RANGE_PADDING_LEFT = 35;// 左邊距範圍值
    private static final int BASE_PADDING_TOP = 42;// 上邊距
    private static final int RANGE_PADDING_TOP = 15;// 上邊距範圍值
    private static final int DEFAULT_WIDTH = 200;// 默認寬度.圖片的總寬
    private static final int DEFAULT_HEIGHT = 70;// 默認高度.圖片的總高
    private final int DEFAULT_COLOR = 0xdf;// 默認背景顏色值
    // settings decided by the layout xml
    // canvas width and height
    private int width = DEFAULT_WIDTH;
    private int height = DEFAULT_HEIGHT;
    // random word space and pading_top
    private int base_padding_left = BASE_PADDING_LEFT;
    private int range_padding_left = RANGE_PADDING_LEFT;
    private int base_padding_top = BASE_PADDING_TOP;
    private int range_padding_top = RANGE_PADDING_TOP;
    // number of chars, lines; font size
    private int codeLength = DEFAULT_CODE_LENGTH;
    private int line_number = DEFAULT_LINE_NUMBER;
    private int font_size = DEFAULT_FONT_SIZE;
    // variables
    private String code;// 保存生成的驗證碼
    private int padding_left, padding_top;
    private Random random = new Random();

    private Bitmap createBitmap() {
        padding_left = 0;
        Bitmap bp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bp);
        code = createCode();
        c.drawColor(Color.rgb(DEFAULT_COLOR, DEFAULT_COLOR, DEFAULT_COLOR));
        Paint paint = new Paint();
        paint.setTextSize(font_size);
        for (int i = 0; i < code.length(); i++) {
            randomTextStyle(paint);
            randomPadding();
            c.drawText(code.charAt(i) + "", padding_left, padding_top, paint);
        }
        for (int i = 0; i < line_number; i++) {
            drawLine(c, paint);
        }
        c.save(Canvas.ALL_SAVE_FLAG);// 保存
        c.restore();
        return bp;
    }

    /**
     * @Description:是否區分大小寫
     */

    public String getCode(boolean isMatchCase) {
        if (isMatchCase) {
            if (code != null) {
                code.toLowerCase(Locale.getDefault());
            }
        } else {
            return code;
        }
        return null;
    }

    public Bitmap getBitmap() {
        return createBitmap();
    }

    private String createCode() {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < codeLength; i++) {
            buffer.append(CHARS[random.nextInt(CHARS.length)]);
        }
        return buffer.toString();
    }

    private void drawLine(Canvas canvas, Paint paint) {
        int color = randomColor();
        int startX = random.nextInt(width);
        int startY = random.nextInt(height);
        int stopX = random.nextInt(width);
        int stopY = random.nextInt(height);
        paint.setStrokeWidth(1);
        paint.setColor(color);
        canvas.drawLine(startX, startY, stopX, stopY, paint);
    }

    private int randomColor() {
        return randomColor(1);
    }

    private int randomColor(int rate) {
        int red = random.nextInt(256) / rate;
        int green = random.nextInt(256) / rate;
        int blue = random.nextInt(256) / rate;
        return Color.rgb(red, green, blue);
    }

    private void randomTextStyle(Paint paint) {
        int color = randomColor();
        paint.setColor(color);
        paint.setFakeBoldText(random.nextBoolean()); // true為粗體，false為非粗體
        float skewX = random.nextInt(11) / 10;
        skewX = random.nextBoolean() ? skewX : -skewX;
        paint.setTextSkewX(skewX); // float類型參數，負數表示右斜，整數左斜
        paint.setUnderlineText(true); //true為底線，false為非底線
        paint.setStrikeThruText(true); //true為刪除線，false為非刪除線
    }

    private void randomPadding() {
        padding_left += base_padding_left + random.nextInt(range_padding_left);
        padding_top = base_padding_top + random.nextInt(range_padding_top);
    }
}

