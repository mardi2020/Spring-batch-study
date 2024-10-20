package com.mardi2020.exbatch.chunk;

import com.mardi2020.exbatch.chunk.data.Item;
import com.mardi2020.exbatch.chunk.data.ItemInfo;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

/**
 * ItemProcessor를 구현하여 Spring batch로 전달
 * Reader로 읽어온 chunk data를 처리하고 데이터를 변환하거나 외부 인터페이스 호출 등 수행
 * chunk model에서 없어도 되는 옵션
 */
@Component
public class CustomItemProcessor implements ItemProcessor<Item, ItemInfo> {

    /**
     *
     * @param item to be processed, never {@code null}.
     * @return input item을 변환한 객체
     * @throws Exception 예외
     */
    @Override
    public ItemInfo process(@NonNull Item item) throws Exception {
        return new ItemInfo(item);
    }
}
