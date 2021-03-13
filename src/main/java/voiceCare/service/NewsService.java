package voiceCare.service;

import voiceCare.model.entity.News;

import java.util.List;

public interface NewsService {
    List<News> showNews();

    News getDetailNews(int newsId);
}
