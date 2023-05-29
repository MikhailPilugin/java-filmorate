package ru.yandex.practicum.filmorate.storage;

public interface UserFeedStorage {
    void likeEvent(int userId, int entityId, String operation);

    void friendEvent(int userId, int entityId, String operation);

    void reviewEvent(int userId, int entityId, String operation);
}
