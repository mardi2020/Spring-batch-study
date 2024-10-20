package com.mardi2020.exbatch.chunk.data;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ItemInfo {

    private String info;

    public ItemInfo(Item item) {
        this.info = item.getName() + " " + item.getAge();
    }
}
