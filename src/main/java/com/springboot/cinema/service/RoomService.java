package com.springboot.cinema.service;

import com.springboot.cinema.entity.Room;
import java.util.List;

public interface RoomService {
    List<Room> getAllRooms();
    Room getRoomById(int roomId);
    Room saveRoom(Room room);
    void deleteRoom(int roomId);
}
