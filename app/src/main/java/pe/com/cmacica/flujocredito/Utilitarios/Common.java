package pe.com.cmacica.flujocredito.Utilitarios;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class Common {

    public static void hideKeyboard(Context context, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
