package com.springboot.cinema.service.impl;

import com.springboot.cinema.entity.Room;
import com.springboot.cinema.repository.RoomRepository;
import com.springboot.cinema.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoomServiceImpl implements RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<>();
        roomRepository.findAll().forEach(rooms::add);
        return rooms;
    }

    @Override
    @Transactional(readOnly = true)
    public Room getRoomById(int roomId) {
        return roomRepository.findById(roomId).orElse(null);
    }

    @Override
    @Transactional
    public Room saveRoom(Room room) {
        return roomRepository.save(room);
    }

    @Override
    @Transactional
    public void deleteRoom(int roomId) {
        Room room = roomRepository.findById(roomId).orElse(null);
        if (room != null) {
            if ((room.getSeatList() != null && !room.getSeatList().isEmpty()) ||
                (room.getShowtimeList() != null && !room.getShowtimeList().isEmpty())) {
                throw new IllegalStateException("Không thể xóa phòng chiếu này vì đang có ghế hoặc lịch chiếu hoạt động.");
            }
            roomRepository.deleteById(roomId);
        }
    }
}
