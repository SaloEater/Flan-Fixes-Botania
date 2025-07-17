package com.saloeater.flan_fixes.botania;


public interface IStorage {
    void set(String key, Boolean value);
    Boolean get(String key);

    boolean has(String key);
}
