package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithAnswers;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Transactional
    @Override
    public ItemRequestDto addItemRequest(long userId, ItemRequestDto itemRequestDto) {
        User user = validateUserById(userId);
        itemRequestDto.setCreated(LocalDateTime.now());
        ItemRequest itemRequest = itemRequestRepository.save(ItemRequestMapper.toItemRequest(itemRequestDto, user));
        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestDtoWithAnswers> getOwnersItemRequests(long userId) {
        validateUserById(userId);
        List<ItemRequest> itemRequests = itemRequestRepository.findByRequestorIdOrderByCreatedDesc(userId);
        return itemRequests.stream()
                .map(this::addAnswerItems)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestDtoWithAnswers> getAllItemRequests(long userId, int from, int size) {
        validateUserById(userId);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("created").descending());
        return itemRequestRepository.findByRequestorIdNot(pageable, userId).stream()
                .map(this::addAnswerItems)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public ItemRequestDtoWithAnswers getItemRequest(long userId, long itemId) {
        validateUserById(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(itemId).orElseThrow(() -> new EntityNotFoundException(
                "Запрос с id = %s не найден!",
                ItemRequest.class.getName()));
        return addAnswerItems(itemRequest);
    }

    private ItemRequestDtoWithAnswers addAnswerItems(ItemRequest itemRequest) {
        List<ItemDto> answerItems = ItemMapper.toItemDtoList(itemRepository.findByRequestId(itemRequest.getId()));
        return ItemRequestMapper.toItemRequestDtoWithAnswers(itemRequest, answerItems);
    }

    private User validateUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(
                String.format("Пользователь с id = %s не найден!", userId),
                User.class.getName()));
    }
}
