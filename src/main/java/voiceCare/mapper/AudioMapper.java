package voiceCare.mapper;

import org.apache.ibatis.annotations.Param;
import voiceCare.model.entity.Audio;

public interface AudioMapper {

    Audio getAudioById(@Param("id") Integer id);

}
