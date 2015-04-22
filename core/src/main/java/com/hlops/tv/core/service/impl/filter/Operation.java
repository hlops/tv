package com.hlops.tv.core.service.impl.filter;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

/**
 * Created by tom on 4/21/15.
 */
enum Operation implements IOperation {
    eq {
        @Override
        public boolean success(@Nullable String s1, @Nullable String s2) {
            return StringUtils.equalsIgnoreCase(s1, s2);
        }
    },

    lt {
        @Override
        public boolean success(@Nullable String s1, @Nullable String s2) {
            return compare(s1, s2) < 0;
        }
    },

    le {
        @Override
        public boolean success(@Nullable String s1, @Nullable String s2) {
            return compare(s1, s2) <= 0;
        }
    },

    gt {
        @Override
        public boolean success(@Nullable String s1, @Nullable String s2) {
            return compare(s1, s2) > 0;
        }
    },

    ge {
        @Override
        public boolean success(@Nullable String s1, @Nullable String s2) {
            return compare(s1, s2) >= 0;
        }
    };

    private static int compare(@Nullable String s1, @Nullable String s2) throws NullPointerException {
        if (s1 == null && s2 == null) return 0;
        return s1.compareTo(s2);
    }

}
