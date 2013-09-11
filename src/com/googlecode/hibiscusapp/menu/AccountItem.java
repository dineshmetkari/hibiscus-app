package com.googlecode.hibiscusapp.menu;

import com.googlecode.hibiscusapp.model.Account;

/**
 * Package: com.googlecode.hibiscusapp.menu
 * Date: 11/09/13
 * Time: 00:11
 *
 * @author eike
 */
public class AccountItem
{

    private Account account;
    private int iconRes;

    public AccountItem(Account account, int iconRes)
    {
        this.account = account;
        this.iconRes = iconRes;
    }

    public Account getAccount()
    {
        return account;
    }

    public int getIconRes()
    {
        return iconRes;
    }
}
