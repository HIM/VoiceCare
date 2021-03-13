package voiceCare.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import voiceCare.mapper.NewsMapper;
import voiceCare.model.entity.News;
import voiceCare.service.NewsService;

import java.util.List;
@Service
public class NewsServiceImpl implements NewsService {
    @Autowired NewsMapper newsMapper;

    @Override
    public List<News> showNews() {
        return newsMapper.showNews();
    }

    @Override
    public News getDetailNews(int newsId) {
        return newsMapper.getDetailNews(newsId);
    }
}
