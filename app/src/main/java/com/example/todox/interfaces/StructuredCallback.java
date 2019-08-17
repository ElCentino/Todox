package com.example.todox.interfaces;

import java.util.ArrayList;

public interface StructuredCallback<T> {
    void OnStructuredCallbackResolveItems(ArrayList<T> items);
}
