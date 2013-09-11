package com.googlecode.hibiscusapp.menu;

/**
 * Package: com.googlecode.hibiscusapp.menu
 * Date: 08/09/13
 * Time: 19:27
 *
 * @author eike
 */
public class DrawerItem
{
    final private int id;
    final private int iconRes;
    final private int titleRes;

    public DrawerItem(int id, int iconRes, int titleRes)
    {
        this.id = id;
        this.iconRes = iconRes;
        this.titleRes = titleRes;
    }

    public int getId()
    {
        return id;
    }

    public int getIconRes()
    {
        return iconRes;
    }

    public int getTitleRes()
    {
        return titleRes;
    }
}
