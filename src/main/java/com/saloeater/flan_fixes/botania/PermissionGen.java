package com.saloeater.flan_fixes.botania;

import io.github.flemmli97.flan.api.permission.BuiltinPermission;
import io.github.flemmli97.flan.api.permission.ClaimPermission;
import io.github.flemmli97.flan.api.permission.ClaimPermissionProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class PermissionGen extends ClaimPermissionProvider {

    public PermissionGen(PackOutput output) {
        super(output);
    }

    @Override
    protected void add() {
        BuiltinPermission.DATAGEN_DATA.forEach(this::addPermission);
        this.addPermission(BotaniaCompat.BOTANIA, new ClaimPermission.Builder(
                new ClaimPermission.Builder.ItemStackHolder(ResourceLocation.parse("botania:lens_normal")),
                false, false, BuiltinPermission.order++, "botania",
                List.of("Gives permission to botania items to interact with your claim")));
        this.addPermission(BotaniaCompat.CORPOREA_INDEX, new ClaimPermission.Builder(
                new ClaimPermission.Builder.ItemStackHolder(ResourceLocation.parse("botania:corporea_index")),
                false, false, BuiltinPermission.order++, "botania",
                List.of("Gives permission to other players to use your Corporea Index")));
    }
}
