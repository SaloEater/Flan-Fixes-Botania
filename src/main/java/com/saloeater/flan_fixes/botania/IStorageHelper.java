package com.saloeater.flan_fixes.botania;

import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.NotNull;

//After first install old burst doesn't have required methods so they throw AbstractMethodError
public class IStorageHelper {
    public static void set(IStorage storage, String key, Boolean value) {
        try {
            storage.set(key, value);
        } catch (AbstractMethodError ignored) {}
    }
    public static Boolean get(IStorage storage, String key){
        try {
            return storage.get(key);
        } catch (AbstractMethodError ignored) {
            return null;
        }
    }

    public static boolean has(IStorage storage, String key){
        try {
            return storage.has(key);
        } catch (AbstractMethodError ignored) {
            return false;
        }
    }

    public static String getKey(BlockPos pos) {
        String key = String.valueOf(pos.asLong());
        return key;
    }
}
