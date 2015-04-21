package com.hlops.tv.core.service.impl.filter;

import org.jetbrains.annotations.Nullable;

/**
 * Created by tom on 4/21/15.
 */
interface IOperation {
    boolean success(@Nullable String s1, @Nullable String s2);
}
