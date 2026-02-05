package com.example.backend.Repository;

import com.example.backend.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepo extends JpaRepository<Message, UUID> {
    @Query("select m from Message m join m.chat c where c.id=:chatId")
    public List<Message> findByChatId(@Param("chatId") UUID chatId);
}
