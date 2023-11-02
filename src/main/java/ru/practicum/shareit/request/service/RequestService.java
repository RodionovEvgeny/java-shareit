package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithAnswers;

import java.util.List;

public interface RequestService {


    ItemRequestDto addItemRequest(long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDtoWithAnswers> getOwnersItemRequests(long userId);

    List<ItemRequestDto> getAllItemRequests(long userId, int from, int size);

    ItemRequestDtoWithAnswers getItemRequest(long itemId);
}

