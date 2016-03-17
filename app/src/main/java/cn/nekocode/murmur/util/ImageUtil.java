package cn.nekocode.murmur.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class ImageUtil {

	// 将Bitmap转换成InputStream
	public static InputStream bitmap2InputStream(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		InputStream is = new ByteArrayInputStream(baos.toByteArray());
		return is;
	}

	// 将Bitmap转换成InputStream
	public static InputStream bitmap2InputStream(Bitmap bm, int quality) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, quality, baos);
		InputStream is = new ByteArrayInputStream(baos.toByteArray());
		return is;
	}

	// 将InputStream转换成Bitmap
	public static Bitmap inputStream2Bitmap(InputStream is) {
		return BitmapFactory.decodeStream(is);
	}

	// Drawable转换成InputStream
	public static InputStream drawable2InputStream(Drawable d) {
		Bitmap bitmap = drawable2Bitmap(d);
		return bitmap2InputStream(bitmap);
	}

	// InputStream转换成Drawable
	public static Drawable inputStream2Drawable(InputStream is) {
		Bitmap bitmap = inputStream2Bitmap(is);
		return bitmap2Drawable(bitmap);
	}

	// Drawable转换成byte[]
	public static byte[] drawable2Bytes(Drawable d) {
		Bitmap bitmap = drawable2Bitmap(d);
		return bitmap2Bytes(bitmap);
	}

	// byte[]转换成Drawable
	public static Drawable bytes2Drawable(byte[] b) {
		Bitmap bitmap = bytes2Bitmap(b);
		return bitmap2Drawable(bitmap);
	}

	// Bitmap转换成byte[]
	public static byte[] bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

	// byte[]转换成Bitmap
	public static Bitmap bytes2Bitmap(byte[] b) {
		if (b.length != 0) {
			return BitmapFactory.decodeByteArray(b, 0, b.length);
		}
		return null;
	}

	public static Bitmap getBitmapFromUri(Context context, Uri uri) {
		try {
			// 读取uri所在的图片
			Bitmap bitmap = MediaStore.Images.Media.getBitmap(
					context.getContentResolver(), uri);
			return bitmap;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static int calculateInSampleSize(BitmapFactory.Options options,
            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }

        return inSampleSize;
    }
	
	public static Bitmap decodeSampledBitmapFromFile(String filename,
            int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filename, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filename, options);
    }

	// Drawable转换成Bitmap
	public static Bitmap drawable2Bitmap(Drawable drawable) {
		Bitmap bitmap = Bitmap
				.createBitmap(
						drawable.getIntrinsicWidth(),
						drawable.getIntrinsicHeight(),
						drawable.getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888
								: Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight());
		drawable.draw(canvas);
		return bitmap;
	}
	
	public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
		Bitmap newbmp = null;
		if (bitmap != null) {
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();
			Matrix matrix = new Matrix();
			float scaleWidht = ((float) w / width);
			float scaleHeight = ((float) h / height);
			matrix.postScale(scaleWidht, scaleHeight);
			newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix,
					true);
		}
		return newbmp;
	}

	// Bitmap转换成Drawable
	@SuppressWarnings("deprecation")
	public static Drawable bitmap2Drawable(Bitmap bitmap) {
		BitmapDrawable bd = new BitmapDrawable(bitmap);
		Drawable d = (Drawable) bd;
		return d;
	}
	
	public static Bitmap getCircleBitmap(Bitmap source) {
		int minSize = Math.min(source.getWidth(), source.getHeight());

		Bitmap output = Bitmap.createBitmap(minSize, minSize, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final Paint paint = new Paint();
		final Rect srcRect = new Rect(
				(source.getWidth()-minSize)/2,
				(source.getHeight()-minSize)/2,
				minSize, minSize);
		final Rect dstRect = new Rect(0, 0, minSize, minSize);

		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setDither(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(Color.parseColor("#BAB399"));
		canvas.drawCircle(minSize / 2.0f, minSize / 2.0f, minSize / 2.0f, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(source, srcRect, dstRect, paint);

		return output;
	}
}
