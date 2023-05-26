package ru.yandex.practicum.filmorate.storage;

public interface UserFeedStorage {
    void likeEvent(int user_id, int entity_id, String operation);

    void friendEvent(int user_id, int entity_id, String operation);

    void reviewEvent(int user_id, int entity_id, String operation);
}
