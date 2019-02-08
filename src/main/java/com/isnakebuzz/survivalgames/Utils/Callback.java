package com.isnakebuzz.survivalgames.Utils;

import java.io.Serializable;

public interface Callback<T> extends Serializable {
    void done(T value);
}
