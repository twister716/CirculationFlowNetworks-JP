package com.circulation.circulation_networks.utils;

import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;

import java.util.UUID;

public final class NbtCompat {

    private NbtCompat() {
    }

    public static String getStringOr(CompoundTag tag, String key, String fallback) {
        return tag.getString(key).orElse(fallback);
    }

    public static boolean contains(CompoundTag tag, String key) {
        return tag.contains(key);
    }

    public static int getIntOr(CompoundTag tag, String key, int fallback) {
        return tag.getInt(key).orElse(fallback);
    }

    public static long getLongOr(CompoundTag tag, String key, long fallback) {
        return tag.getLong(key).orElse(fallback);
    }

    public static double getDoubleOr(CompoundTag tag, String key, double fallback) {
        return tag.getDouble(key).orElse(fallback);
    }

    public static byte getByteOr(CompoundTag tag, String key, byte fallback) {
        return tag.getByte(key).orElse(fallback);
    }

    public static void putString(CompoundTag tag, String key, String value) {
        tag.putString(key, value);
    }

    public static void putInt(CompoundTag tag, String key, int value) {
        tag.putInt(key, value);
    }

    public static void putLong(CompoundTag tag, String key, long value) {
        tag.putLong(key, value);
    }

    public static void putDouble(CompoundTag tag, String key, double value) {
        tag.putDouble(key, value);
    }

    public static void putByte(CompoundTag tag, String key, byte value) {
        tag.putByte(key, value);
    }

    public static ListTag getListOrEmpty(CompoundTag tag, String key) {
        return tag.getList(key).orElseGet(ListTag::new);
    }

    public static CompoundTag getCompoundOrEmpty(CompoundTag tag, String key) {
        return tag.getCompound(key).orElseGet(CompoundTag::new);
    }

    public static CompoundTag getCompoundOrEmpty(ListTag tag, int index) {
        return tag.getCompound(index).orElseGet(CompoundTag::new);
    }

    public static long getLongValue(Tag tag) {
        return tag instanceof LongTag longTag ? longTag.longValue() : 0L;
    }

    public static UUID getUuidOrNull(CompoundTag tag, String key) {
        return tag.getIntArray(key).map(UUIDUtil::uuidFromIntArray).orElse(null);
    }

    public static void putUuid(CompoundTag tag, String key, UUID value) {
        tag.putIntArray(key, UUIDUtil.uuidToIntArray(value));
    }
}
