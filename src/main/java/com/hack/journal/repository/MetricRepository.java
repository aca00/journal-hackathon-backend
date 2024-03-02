package com.hack.journal.repository;

import com.hack.journal.dto.AutoEmojiCountMetric;
import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@SqlResultSetMapping(name = "autoEmojiCountMetricMapper",
        classes = @ConstructorResult(
                targetClass = AutoEmojiCountMetric.class,
                columns = {
                        @ColumnResult(name = "emoji_auto", type = String.class),
                        @ColumnResult(name = "emoji_count", type = Integer.class)
                }
        ))

@Repository
@Embeddable
public class MetricRepository {
    @Autowired
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    public List<AutoEmojiCountMetric> getListOfAutoEmojiCountMetric(long userId, Timestamp start, Timestamp end) {
        String sql = "select emoji_auto, count(*) as emoji_count from diary_page where user_id=:userId and created_date>=:start and created_date<=:end group by emoji_auto";
        Query q = entityManager.createNativeQuery(sql, "autoEmojiCountMetricMapper");
        q.setParameter("end", end);
        q.setParameter("start", start);
        q.setParameter("userId", userId);
        return q.getResultList();

    }
}


