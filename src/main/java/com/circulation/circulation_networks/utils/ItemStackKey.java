package com.circulation.circulation_networks.utils;

//? if <1.20 {
import net.minecraft.item.ItemStack;
//?} else {
/*import net.minecraft.world.item.ItemStack;
*///?}

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class ItemStackKey {

    public static final ItemStackKey EMPTY = new ItemStackKey(ItemStack.EMPTY);
    private static final Queue<ItemStackKey> POOL = new ConcurrentLinkedQueue<>();
    private ItemStack key;
    private int hashCode;

    private ItemStackKey(ItemStack key) {
        set(key);

    }

    public static ItemStackKey get(ItemStack key) {
        if (POOL.isEmpty()) {
            return new ItemStackKey(key);
        }
        return POOL.poll().set(key);
    }

    public boolean isEmpty() {
        return key.isEmpty();
    }

    private ItemStackKey set(ItemStack key) {
        this.key = key;
        //? if <1.20 {
        this.hashCode = Objects.hash(key.getItem(), key.getTagCompound());
        //?} else {
        /*this.hashCode = Objects.hash(key.getItem(), key.getTag());
        *///?}
        return this;
    }

    public void recycle() {
        this.key = null;
        this.hashCode = 0;
        if (POOL.size() < 100) {
            POOL.add(this);
        }
    }

    public long getCount() {
        return key.getCount();
    }

    public ItemStack getItemStack() {
        if (key.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return key.copy();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != ItemStackKey.class) {
            return false;
        }
        ItemStackKey other = (ItemStackKey) obj;
        return this.equals(other.key);
    }

    public boolean equals(ItemStack key) {
        //? if <1.20 {
        return this.key.getItem() == key.getItem()
            && (this.key.getMetadata() == key.getMetadata() || this.key.getMetadata() == 32767 || key.getMetadata() == 32767)
            && Objects.equals(this.key.getTagCompound(), key.getTagCompound());
        //?} else {
        /*return this.key.getItem() == key.getItem()
            && Objects.equals(this.key.getTag(), key.getTag());
        *///?}
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public String toString() {
        return "ItemStackKey(key=" + key + ", hashCode=" + hashCode + ")";
    }
}
