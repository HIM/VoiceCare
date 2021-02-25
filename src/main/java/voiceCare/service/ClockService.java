package voiceCare.service;

import voiceCare.model.entity.Clock;

public interface ClockService {

    int save(Clock clock);

    int update(Clock clock);

    int delete(Integer id, String createTime);
}
