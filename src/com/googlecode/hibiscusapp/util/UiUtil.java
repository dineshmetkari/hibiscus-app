package com.googlecode.hibiscusapp.util;

import android.content.Context;
import android.widget.TextView;
import com.googlecode.hibiscusapp.R;

import java.text.NumberFormat;

/**
 * Package: com.googlecode.hibiscusapp.util
 * Date: 25/11/13
 * Time: 22:44
 *
 * @author eike
 */
public class UiUtil
{
    /**
     * This method sets the text color of a TextView instance.
     * If the value is zero, the text color will be set to the default color,
     * If the value is negative, the text color will be red.
     * If the value if positive, the text color will be green
     *
     * @param context the context instance
     * @param view the text view instance
     * @param value the value
     */
    public static void setTextColor(Context context, TextView view, double value)
    {
        int color = 0;
        if (value == 0.0) {
            // set the default TextView color
            color = new TextView(context).getCurrentTextColor();
        } else if (value < 0) {
            color = context.getResources().getColor(R.color.balance_negative);
        } else if (value > 0) {
            color = context.getResources().getColor(R.color.balance_positive);
        }

        view.setTextColor(color);
    }

    /**
     * This method sets the value and text color of a currency TextView.
     *
     * @param view the text view instance
     * @param value the value
     */
    public static void setCurrencyValueAndTextColor(Context context, TextView view, double value)
    {
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance();
        view.setText(numberFormat.format(value));

        setTextColor(context, view, value);
    }
}
