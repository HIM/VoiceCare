package voiceCare.service;

import voiceCare.model.entity.Clock;
import voiceCare.model.entity.User;

import java.util.List;

public interface ClockService {

    int save(Clock clock);

    int update(Clock clock);

    int delete(Integer id, String createTime);

    List<Clock> showClocks(Integer id);

    void loginInState(int id);

    void quitState(int id);

    List<User> findLoginUser();

    List<Clock> findLoginUserClock(Integer id);
}
