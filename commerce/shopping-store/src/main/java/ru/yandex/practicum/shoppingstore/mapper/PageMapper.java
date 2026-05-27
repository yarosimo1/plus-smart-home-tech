package ru.yandex.practicum.shoppingstore.mapper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.interactionapi.dto.store.PageProductDto;
import ru.yandex.practicum.interactionapi.dto.store.PageableObject;
import ru.yandex.practicum.interactionapi.dto.store.ProductDto;
import ru.yandex.practicum.interactionapi.dto.store.SortObject;

import java.util.List;

@Component
public class PageMapper {

    public PageProductDto toPageProductDto(Page<ProductDto> page) {
        List<SortObject> sort = toSortObjects(page.getSort());
        PageableObject pageableObject = new PageableObject(
                page.getPageable().getOffset(),
                toSortObjects(page.getPageable().getSort()),
                page.getPageable().isUnpaged(),
                page.getPageable().isPaged(),
                page.getPageable().getPageNumber(),
                page.getPageable().getPageSize()
        );

        return new PageProductDto(
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast(),
                page.getSize(),
                page.getContent(),
                page.getNumber(),
                sort,
                page.getNumberOfElements(),
                pageableObject,
                page.isEmpty()
        );
    }

    private List<SortObject> toSortObjects(Sort sort) {
        return sort.stream()
                .map(order -> new SortObject(
                        order.getDirection().name(),
                        order.getNullHandling().name(),
                        order.isAscending(),
                        order.getProperty(),
                        order.isIgnoreCase()))
                .toList();
    }
}
