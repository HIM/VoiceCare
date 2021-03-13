package voiceCare.mapper;

import org.apache.ibatis.annotations.Param;
import voiceCare.model.entity.News;

import java.util.List;

public interface NewsMapper {
    List<News> showNews();


    News getDetailNews(@Param("nid") int newsId);
}
