package pe.com.cmacica.flujocredito.Utilitarios;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class AppUtil {

    public static String compressImage(Bitmap bitmap) {

        Bitmap bitmapTemporary;
        int newHeight = 800;
        int newWidth = 800;

        newHeight = bitmap.getHeight() > 800 ? 800 : bitmap.getHeight();
        newWidth = bitmap.getWidth() > 800 ? 800 : bitmap.getWidth();

        Matrix matrix = new Matrix();

        bitmapTemporary = Bitmap.createBitmap(bitmap, 0, 0, newWidth, newHeight, matrix, false);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmapTemporary.compress(Bitmap.CompressFormat.JPEG, 50, baos);

        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);

    }

}
