package voiceCare.service;

import voiceCare.model.entity.Clock;

import java.util.List;

public interface ClockService {

    int save(Clock clock);

    int update(Clock clock);

    int delete(Integer id, String createTime);

    List<Clock> showClocks(Integer id);
}
