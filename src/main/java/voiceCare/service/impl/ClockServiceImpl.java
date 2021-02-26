package voiceCare.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import voiceCare.mapper.ClockMapper;
import voiceCare.model.entity.Clock;
import voiceCare.service.ClockService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class ClockServiceImpl implements ClockService {
    @Autowired
    private ClockMapper clockMapper;

    @Override
    public int save(Clock clock) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        clock.setCreateTime(df.format(new Date()));
        return clockMapper.save(clock);
    }

    @Override
    public int update(Clock clock) {
        return clockMapper.update(clock);
    }

    @Override
    public int delete(Integer id, String createTime) {
        return clockMapper.delete(id, createTime);
    }

    @Override
    public List<Clock> showClocks(Integer id) {
        return clockMapper.showClocks(id);
    }
}
