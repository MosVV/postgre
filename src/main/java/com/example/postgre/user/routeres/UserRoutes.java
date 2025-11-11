package com.example.postgre.user.routeres;

import com.example.postgre.base.routes.BaseRoutes;

public class UserRoutes {

    private final static String ROOT = BaseRoutes.API + "/user";

    public final static String CREATE = ROOT;

    public final static String BY_ID = ROOT + "/{id}";

    public final static String SEARCH = ROOT;

    public final static String INIT = BaseRoutes.NOT_SECURED + "/init";
}
