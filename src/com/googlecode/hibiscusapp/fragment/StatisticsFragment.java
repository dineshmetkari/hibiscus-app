package com.googlecode.hibiscusapp.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.googlecode.hibiscusapp.R;

/**
 * Package: com.googlecode.hibiscusapp.fragment
 * Date: 09/09/13
 * Time: 22:10
 *
 * @author eike
 */
public class StatisticsFragment extends Fragment
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.statistics, container, false);
    }
}
