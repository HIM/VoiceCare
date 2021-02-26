package voiceCare.mapper;

import org.apache.ibatis.annotations.Param;
import voiceCare.model.entity.Clock;

import java.util.List;

public interface ClockMapper {
    int save(Clock clock);

    int update(Clock clock);

    int delete(@Param("id") Integer id, @Param("createTime") String createTime);

    List<Clock> showClocks(@Param("userId") Integer id);
}
